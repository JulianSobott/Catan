package com.catangame.catan.local;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.files.FileHandle;
import com.catangame.catan.utils.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.input.GestureDetector.GestureListener;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.catangame.catan.core.LocalCore;
import com.catangame.catan.core.Map;
import com.catangame.catan.local.LocalGameLogic;
import com.catangame.catan.local.LocalUI;
import com.catangame.catan.network.Client;
import com.catangame.catan.network.Networkmanager;
import com.catangame.catan.network.RemoteCore;
import com.catangame.catan.network.Server;
import com.catangame.catan.superClasses.Core;
import com.catangame.catan.utils.Clock;
import com.catangame.catan.utils.FontMgr;

public class Framework extends ApplicationAdapter {
	public enum DeviceMode {
		DESKTOP, MOBILE
	}

	DeviceMode deviceMode;
	public static Color clearColor = new Color(0.04f, 0.57f, 1, 1);

	// view & camera
	private Vector2 windowSize = new Vector2();
	OrthographicCamera camera;
	OrthographicCamera guiCamera;
	float lastZoom;
	float mouse_value = 3.f;
	Vector2 mouse_start;
	ArrayList<Vector2> mouse_moved;
	boolean mouse_was_moved = false;

	// BitmapFont
	BitmapFont std_font;

	// timer
	Clock std_timer = new Clock();
	Clock frame_timer = new Clock();
	float target_fps = 60.f;

	// rendering
	private SpriteBatch sb;
	private ShapeRenderer sr;

	// local
	Networkmanager data_connection;
	LocalGameLogic gameLogic = new LocalGameLogic();
	LocalUI ui = new LocalUI((LocalGameLogic) gameLogic, this);

	// server
	Core core;

	public Framework(Vector2 windowSize, DeviceMode deviceMode) {
		this.windowSize = windowSize;
		this.deviceMode = deviceMode;
	}

	@Override
	public void create() {
		Map.update_constants();

		// camera
		camera = new OrthographicCamera();
		camera.setToOrtho(true, windowSize.x, windowSize.y);
		camera.zoom = 0.5f;
		lastZoom = camera.zoom;
		guiCamera = new OrthographicCamera();
		guiCamera.setToOrtho(true, windowSize.x, windowSize.y);
		update_view(true);

		// BitmapFont
		FontMgr.init();
		std_font = FontMgr.getFont(30); // font size 12 pixels

		// rendering
		sb = new SpriteBatch();
		sr = new ShapeRenderer();

		// event handling
		InputMultiplexer multiplexer = new InputMultiplexer();
		multiplexer.addProcessor(ui);
		if (deviceMode == DeviceMode.DESKTOP)
			multiplexer.addProcessor(new InputAdapter() {

				@Override
				public boolean touchUp(int screenX, int screenY, int pointer, int button) {
					if (button == Input.Buttons.RIGHT) {
						// disable screen moving
						mouse_was_moved = false;
						return true;
					} else
						return false;
				}

				@Override
				public boolean touchDragged(int screenX, int screenY, int pointer) {
					if (mouse_was_moved) {
						float x = (float) screenX, y = (float) screenY;
						camera.translate((mouse_start.x - x) * camera.zoom, (mouse_start.y - y) * camera.zoom);
						mouse_start = new Vector2(x, y);
						update_view(false);
						return true;
					} else
						return false;
				}

				@Override
				public boolean touchDown(int screenX, int screenY, int pointer, int button) {
					if (button == Input.Buttons.LEFT) {
						gameLogic.mouse_click_input(reverse_transform_position(screenX, screenY, camera));
						return true;
					} else if (button == Input.Buttons.RIGHT) {
						// reset mouse position
						mouse_start = new Vector2((float) screenX, (float) screenY);
						mouse_was_moved = true;
						return true;
					} else
						return false;
				}

				@Override
				public boolean scrolled(int amount) {
					camera.zoom *= Math.pow(0.9f, (float) -amount);
					update_view(false);
					return true;
				}
			});
		else if (deviceMode == DeviceMode.MOBILE)
			multiplexer.addProcessor(new GestureDetector(new GestureDetector.GestureAdapter() {

				@Override
				public boolean zoom(float initialDistance, float distance) {
					camera.zoom = lastZoom * initialDistance / distance;
					update_view(false);
					return true;
				}

				@Override
				public boolean panStop(float x, float y, int pointer, int button) {
					lastZoom = camera.zoom;
					return true;
				}

				@Override
				public boolean pan(float x, float y, float deltaX, float deltaY) {
					camera.translate(-deltaX * camera.zoom, -deltaY * camera.zoom);
					mouse_start = new Vector2(x, y);
					update_view(false);
					return true;
				}
			}));
		Gdx.input.setInputProcessor(multiplexer);

		gameLogic.init();
		ui.init(std_font);

		std_timer.restart();
	}

	@Override
	public void render() { // equals update
		float whole_time = std_timer.getElapsedTime().asSeconds();

		// actual rendering
		Gdx.gl.glClearColor(clearColor.r, clearColor.g, clearColor.b, clearColor.a);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT
				| (Gdx.graphics.getBufferFormat().coverageSampling ? GL20.GL_COVERAGE_BUFFER_BIT_NV : 0));

		sr.setProjectionMatrix(camera.combined);
		sb.setProjectionMatrix(camera.combined);
		gameLogic.render_map(sr, sb);
		sr.setProjectionMatrix(guiCamera.combined);
		sb.setProjectionMatrix(guiCamera.combined);
		ui.render(sr, sb);

		// pause
		long time = Math.max(0,
				(long) (((1 / target_fps) - (std_timer.getElapsedTime().asSeconds() - whole_time)) * 1000.f));
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void dispose() {
		sb.dispose();
		FontMgr.dispose();
	}

	@Override
	public void resize(int width, int height) {
		windowSize.x = width;
		windowSize.y = height;
		update_view(true);
	}

	void update_view(boolean updateWindowSize) {
		camera.position.x = Math.max(0.f,
				Math.min((Map.field_size + Map.field_distance) * Map.map_size_x, camera.position.x));
		camera.position.y = Math.max(0.f, Math
				.min((Map.field_size + Map.field_distance) * Map.map_size_y * Map.MAGIC_HEX_NUMBER, camera.position.y));// constraint
		camera.zoom = Math.max(0.2f, Math.min(Map.map_size_x * 0.15f, camera.zoom));// constraint
		camera.viewportWidth = windowSize.x;
		camera.viewportHeight = windowSize.y;
		guiCamera.translate(-(guiCamera.viewportWidth - windowSize.x) * 0.5f,
				-(guiCamera.viewportHeight - windowSize.y) * 0.5f);
		guiCamera.viewportWidth = windowSize.x;
		guiCamera.viewportHeight = windowSize.y;

		camera.update();
		guiCamera.update();
		if (updateWindowSize)
			ui.update_window_size(new Vector2(windowSize.x, windowSize.y), guiCamera);
	}

	Vector2 reverse_transform_position(int x, int y, OrthographicCamera view) {
		Vector3 vec3 = view.unproject(new Vector3(x, y, 0));
		return new Vector2(vec3.x, vec3.y);
	}

	// creates a new game with this machine as host
	void init_host_game() {
		core = new LocalCore();
		ui.setCore(core);
		((LocalCore) core).addUI(ui);
		((LocalCore) core).addLogic(gameLogic);
		data_connection = new Server((LocalCore) core);
		((LocalCore) core).setServer((Server) data_connection);
		gameLogic.setCore(core);
		gameLogic.setUI(ui);
	}

	// creates a new game with this machine as client
	public boolean init_guest_game(String ip, String name) {
		String serverIp = ip;
		try {
			data_connection = new Client(ui, gameLogic, serverIp);
		} catch (IOException e) {
			System.err.println("Wrong IP or server is not online");
			return false;
		}
		core = new RemoteCore();
		ui.setCore(core);
		((RemoteCore) core).setClientConnection((Client) data_connection);
		gameLogic.setCore(core);
		gameLogic.setUI(ui);

		Color user_color = new Color(((float) Math.random() * 170.f + 50) / 255.f,
				((float) Math.random() * 170.f + 50.f) / 255.f, ((float) Math.random() * 170.f + 50.f / 255.f), 1.f); // TODO implement color picker
		core.register_new_user(name, user_color);

		return true;
	}
	
	public void reset_game() {
		data_connection.closeAllResources();
		data_connection = null;
	}
}
