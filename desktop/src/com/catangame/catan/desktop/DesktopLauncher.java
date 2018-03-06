package com.catangame.catan.desktop;

import java.awt.*;
import java.awt.event.*;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.math.Vector2;
import com.catangame.catan.local.Framework;
import com.catangame.catan.utils.Color;

public class DesktopLauncher {
	private Frame mainFrame;
	private Label headerLabel;
	private Label statusLabel;
	private Panel controlPanel;

	public DesktopLauncher(){
      prepareGUI();
   }

	private void prepareGUI() {
		mainFrame = new Frame("Catan");
		mainFrame.setSize((int) Framework.initialScreenSize.x, (int) Framework.initialScreenSize.y);
		mainFrame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent windowEvent) {
				System.exit(0);
			}
		});
		mainFrame.setVisible(true);
	}
	void run() {
		ApplicationCanvas canvas = new ApplicationCanvas();
		mainFrame.add(canvas);

		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = (int) Framework.initialScreenSize.x;
		config.height = (int) Framework.initialScreenSize.y;
		config.addIcon("assets/res/icon.png", FileType.Internal);
		config.samples = 8;
		config.allowSoftwareMode = true;
		config.initialBackgroundColor = Framework.clearColor.gdx();
		mainFrame.setVisible(true);

		new LwjglApplication(new Framework(new Vector2(config.width, config.height), Framework.DeviceMode.DESKTOP),
				config, canvas);
	}

	class ApplicationCanvas extends Canvas {
		public ApplicationCanvas() {
			setBackground(Framework.clearColor.awt());
			setSize((int) Framework.initialScreenSize.x, (int) Framework.initialScreenSize.y);
		}

		public void paint(Graphics g) {
			Graphics2D g2;
			g2 = (Graphics2D) g;
			g2.drawString("Loading the game...", 70, 70);
		}
	}

	public static void main (String[] arg) {
		DesktopLauncher desktopApplication = new DesktopLauncher();
		desktopApplication.run();
	}
}
