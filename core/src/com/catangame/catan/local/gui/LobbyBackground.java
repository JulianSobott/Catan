package com.catangame.catan.local.gui;

import com.badlogic.gdx.Gdx;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GL30;
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
			+ "  vec2 displacement = texture2D(u_texture2, v_texCoords/3.5).xy;\n" //
			+ "  float t=v_texCoords.y +displacement.y*0.1-0.15+  (sin(v_texCoords.x * 60.0+timedelta) * 0.008); \n" //
			+ "  gl_FragColor = v_color * texture2D(u_texture, vec2(v_texCoords.x,t));\n"
			+ "}";
	String fragmentShaderBackground = "#ifdef GL_ES\n"
			+ "precision mediump float;\n"
			+ "#endif\n"
			+ "varying vec2 v_texCoords ;  \n"
			+ "varying vec4 v_color;\n"
			+ "uniform sampler2D u_textureSun; \n "
			+ "uniform vec2 sunPosition; \n"
			+ "void main()                                  \n"
			+ "{ "
			+ "  float dist = distance(gl_FragCoord.xy, sunPosition); \n"
			+ "  float val = 0; "
			+ "  if(dist < 50){"
			+ "   val = 1.0;"
			+ "    gl_FragColor = vec4(0.88f, 0.88f, 0.58f, 1.f);}"
			+ "  else{"
			+ "  val = 500/dist; \n"
			+ "  val = min(val, 0.7); \n"
			+ "  val = max(val, 0.0);"
			+ "  gl_FragColor = vec4(0.3f, 0.3f, 0.6f, val) * sunPosition.y/500 + vec4(0.0f, 0.0f, 0.0f, val) * (1 - sunPosition.y/500);}"
			+ "				//vec4(1f, 0.1f, 0.01f, val) * (1 - sunPosition.y/900);} \n"			
			+ "	 //gl_FragColor = vec4(226f/255f, 109f/255f, 13f/255f, val) ;//texture2D(u_textureSun, v_texCoords).rgb                                           \n"
			+ "}";
	
	String fragmentShaderWater = "#ifdef GL_ES\n" 
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
	ShaderProgram backgroundShader;
	
	Texture txtrWater;
	Texture txtrWaterReplace;
	Texture txtrBackground;
	Texture txtrSun;
	Texture txtrStartlogo;
	Sprite sprStartLogo;
	Matrix4 matrix;
	Mesh waterMesh;
	Mesh backgroundMesh;
	
	private long tick = 0;
	private int sunX = 0; 
	private int sunY = 0;
	private short sunSpeed = 1;
	

	float time; 

	private Sprite backgroundSprite;
	SpriteBatch batch;
	
	public LobbyBackground(Rectangle bounds) {
		super(bounds);
		

		txtrBackground = TextureMgr.getTexture("background");
		txtrBackground.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		txtrWater = TextureMgr.getTexture("water");
		txtrWater.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		txtrSun = TextureMgr.getTexture("sun");
		txtrSun.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		txtrStartlogo = TextureMgr.getTexture("startlogo");
		txtrStartlogo.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		sprStartLogo = new Sprite(txtrStartlogo);
		sprStartLogo.flip(false, true);
		
		txtrWaterReplace = TextureMgr.getTexture("waterdisplacement");
		txtrWaterReplace.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		matrix = new Matrix4();
		TextureRegion region = new TextureRegion(txtrBackground, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		ShaderProgram.pedantic = false;
		
		backgroundShader = new ShaderProgram(vertexShader, fragmentShaderBackground);
		backgroundShader.setUniformMatrix("u_projTrans", matrix);
		shader = new ShaderProgram(vertexShader, fragmentShader);
		waterShader = new ShaderProgram(vertexShader, fragmentShaderWater);
		waterShader.setUniformMatrix("u_projTrans", matrix);
		waterMesh = createQuad(-1, -1, 1, -1, 1, -0.3f, -1, -0.3f);
		backgroundMesh = createQuad(-1, -1f, 1, -1f, 1, 1, -1, 1);
		
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
		Gdx.gl20.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		Gdx.gl20.glEnable(GL20.GL_BLEND);
		float dt = Gdx.graphics.getDeltaTime();
		time += dt;
		float angle = time * (2* MathUtils.PI);
		if(angle  > (2 * MathUtils.PI))
			angle -= (2*MathUtils.PI);
		
		Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);
		if(sunSpeed > 0)
			sunY = calcHeightSun(sunX);
		else
			sunY = -100;
		Vector2 sunPosition = new Vector2(sunX, sunY);
		
		sb.begin();	
		sb.draw(backgroundSprite, 0, 0, Framework.windowSize.x, (Framework.windowSize.y/3)*3);
		sb.end();
		
		Gdx.gl20.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		Gdx.gl20.glEnable(GL20.GL_BLEND);
		txtrSun.bind(1);
		backgroundShader.begin();
		if(!backgroundShader.isCompiled())
			System.out.println(backgroundShader.getLog());
		backgroundShader.setUniformMatrix("u_worldView",  matrix);
		backgroundShader.setUniformf("sunPosition", sunPosition);
		backgroundShader.setUniformi("u_textureSun", 1);
		backgroundMesh.render(backgroundShader, GL20.GL_TRIANGLE_FAN);
		backgroundShader.end();
		
		
		
		
		
		txtrWater.bind(2);
		txtrWaterReplace.bind(3);
		shader.begin();
		shader.setUniformMatrix("u_worldView",  matrix);
		shader.setUniformi("u_texture", 2);
		shader.setUniformi("u_texture2", 3);
		shader.setUniformf("sunPosition", sunPosition);
		shader.setUniformf("timedelta", -angle);
		waterMesh.render(shader, GL20.GL_TRIANGLE_FAN);
		shader.end();
		Gdx.gl20.glActiveTexture(GL20.GL_TEXTURE0);
		
		Gdx.gl20.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		Gdx.gl20.glEnable(GL20.GL_BLEND);
		sb.begin();
		//sb.draw(sprStartLogo, Gdx.graphics.getWidth()/2 - 300, 10, 600, 208);
		sb.end();
		
		tick++;
		sunX += sunSpeed;
		if(sunX >= Gdx.graphics.getWidth() || sunX < 0)
			sunSpeed *= -1;

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
	
	float sin(float x) {
		return (float) (-300 * Math.sin((Math.PI*2)/Framework.windowSize.y/2*(x - Framework.windowSize.y/4)) + 300);
	}
	
	int calcHeightSun(int posSunX) {
		return (int)(Gdx.graphics.getHeight() * Math.sin(((2*Math.PI)/(Gdx.graphics.getWidth()*2)) *  posSunX));
	}
}
