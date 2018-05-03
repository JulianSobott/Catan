package com.catangame.catan.local.gui;

import com.badlogic.gdx.Gdx;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.catangame.catan.local.Framework;
import com.catangame.catan.utils.Color;
import com.catangame.catan.utils.TextureMgr;

public class LobbyBackground extends Background {
	
	private OrthographicCamera camera;
	
	String vertexShader = "attribute vec4 a_position;    \n"
			+ "attribute vec2 a_texCoord0;\n"
			+ "uniform mat4 u_worldView;\n"
			+ "varying vec4 v_color;" 
			+ "varying vec2 v_texCoords;"
			+ "void main()                  \n"
			+ "{                            \n"
			+ "   v_color = vec4(1, 1, 1, 1); \n"
			+ "   v_texCoords = a_texCoord0; \n"
			+ "   gl_Position =  u_worldView * a_position;  \n"
			+ "}                            \n";
	
	String fragmentShader = "#ifdef GL_ES\n"
			+ "precision mediump float;\n"
			+ "#endif\n"
			+ "varying vec4 v_color;\n"
			+ "varying vec2 v_texCoords;\n"
			+ "uniform sampler2D u_texture;\n"
			+ "uniform sampler2D u_texture2;\n"
			+ "uniform float timedelta;\n"
			+ "void main()                                  \n"
			+ "{                                            \n"
			+ "  vec2 displacement = texture2D(u_texture2, v_texCoords/6.0).xy;\n" //
			+ "  float t=v_texCoords.y +displacement.y*0.1-0.15+  (sin(v_texCoords.x * 60.0+timedelta) * 0.005); \n" //
			+ "  gl_FragColor = v_color * texture2D(u_texture, vec2(v_texCoords.x,t));\n"
			+ "}";
	
	String fragmentShader2 = "#ifdef GL_ES\n" 
		    + "precision mediump float;\n"
			+ "#endif\n" 
			+ "varying vec4 v_color;\n"
			+ "varying vec2 v_texCoords;\n" 
			+ "uniform sampler2D u_texture;\n"
			+ "void main()                                  \n"
			+ "{                                            \n"
			+ "  gl_FragColor = v_color * texture2D(u_texture, v_texCoords);\n"
			+ "}";
	
	ShaderProgram shader;
	ShaderProgram waterShader;
	
	Texture txtrWater;
	Texture txtrWaterReplace;
	Texture txtrBackground;
	
	Matrix4 matrix;
	Mesh waterMesh;
	

	float time; 

	private Sprite backgroundSprite;
	SpriteBatch batch;
	
	public LobbyBackground(Rectangle bounds) {
		super(bounds);
		
		float w = Gdx.graphics.getWidth();
		float h = Gdx.graphics.getHeight();

		camera = new OrthographicCamera(1, h / w);
		batch = new SpriteBatch();
		
		txtrBackground = TextureMgr.getTexture("horizonTop");
		txtrBackground.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		txtrWater = TextureMgr.getTexture("water");
		txtrWater.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		
		txtrWaterReplace = TextureMgr.getTexture("waterdisplacement");
		txtrWaterReplace.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		txtrWaterReplace.bind();
		matrix = new Matrix4();
		
		TextureRegion region = new TextureRegion(txtrBackground, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		ShaderProgram.pedantic = false;
		
		shader = new ShaderProgram(vertexShader, fragmentShader);
		waterShader = new ShaderProgram(vertexShader, fragmentShader2);
		waterShader.setUniformMatrix("u_projTrans", matrix);
		waterMesh = createQuad(-1, -1, 1, -1, 1, -0.3f, -1, -0.3f);
		
		backgroundSprite = new Sprite(TextureMgr.getTexture("background"));
		backgroundSprite.flip(true, true);
		//backgroundSprite.flip(false, true);
		//backgroundSprite.setSize(1f, 1.3f * backgroundSprite.getHeight() / backgroundSprite.getWidth());
		//backgroundSprite.setOrigin(backgroundSprite.getWidth() / 2, backgroundSprite.getHeight() / 2);
		//backgroundSprite.setPosition(-backgroundSprite.getWidth()/2, -backgroundSprite.getHeight()/2);
		
		time = 1f;
	}
	
	
	
	@Override
	public void render(ShapeRenderer sr, SpriteBatch sb) {
		super.render(sr, sb);		
		float dt = Gdx.graphics.getDeltaTime();
		time += dt;
		float angle = time * (2* MathUtils.PI);
		if(angle  > (2 * MathUtils.PI))
			angle -= (2*MathUtils.PI);
		
		Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);


		sb.begin();	
		sb.draw(backgroundSprite, 0, 0, Framework.windowSize.x, (Framework.windowSize.y/3)*3);
		sb.end();
		
		Gdx.gl20.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		Gdx.gl20.glEnable(GL20.GL_BLEND);
		txtrWater.bind(1);
		txtrWaterReplace.bind(2);

		shader.begin();
		shader.setUniformMatrix("u_worldView",  matrix);
		shader.setUniformi("u_texture", 1);
		shader.setUniformi("u_texture2", 2);
		shader.setUniformf("timedelta", -angle);
		waterMesh.render(shader, GL20.GL_TRIANGLE_FAN);
		shader.end();
		Gdx.gl20.glDisable(GL20.GL_TEXTURE_2D);
	}

	
	
	Mesh createQuad(float x1, float y1, float x2, float y2, float x3,
			float y3, float x4, float y4) {
		float[] verts = new float[20];
		int i = 0;

		verts[i++] = x1; // x1
		verts[i++] = y1; // y1
		verts[i++] = 0;
		verts[i++] = 1f; // u1
		verts[i++] = 1f; // v1

		verts[i++] = x2; // x2
		verts[i++] = y2; // y2
		verts[i++] = 0;
		verts[i++] = 0f; // u2
		verts[i++] = 1f; // v2

		verts[i++] = x3; // x3
		verts[i++] = y3; // y2
		verts[i++] = 0;
		verts[i++] = 0f; // u3
		verts[i++] = 0f; // v3

		verts[i++] = x4; // x4
		verts[i++] = y4; // y4
		verts[i++] = 0;
		verts[i++] = 1f; // u4
		verts[i++] = 0f; // v4

		Mesh mesh = new Mesh(true, 4, 0, new VertexAttributes(new VertexAttribute(Usage.Position, 3, ShaderProgram.POSITION_ATTRIBUTE), new VertexAttribute(Usage.TextureCoordinates, 2,ShaderProgram.TEXCOORD_ATTRIBUTE + "0")));

		mesh.setVertices(verts);
		return mesh;

	}
}
