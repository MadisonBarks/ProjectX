package com.focused.projectf;


import java.io.File;
import java.lang.management.ManagementFactory;

import org.lwjgl.LWJGLException;
import org.lwjgl.LWJGLUtil;
import org.lwjgl.Sys;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.PixelFormat;

import com.focused.projectf.audio.SoundManager;
import com.focused.projectf.entities.BuildingType;
import com.focused.projectf.entities.UnitType;
import com.focused.projectf.global.Threading;
import com.focused.projectf.global.actionButtons.ActionButtonSet;
import com.focused.projectf.graphics.Canvas;
import com.focused.projectf.input.Input;
import com.focused.projectf.multiplayer.NetworkingManager;
import com.focused.projectf.resources.Content;
import com.focused.projectf.screens.ScreenManager;
import com.focused.projectf.screens.screens.MainMenuScreen;
import com.focused.projectf.screens.screens.SlidingBackgroundScreen;
import com.focused.projectf.utilities.IOUtils;
import com.focused.projectf.utilities.TimeKeeper;

public class MainLoop {

	public static boolean DEBUGGING = ManagementFactory.getRuntimeMXBean().getInputArguments().toString().indexOf("-agentlib:jdwp") > 0;

	private NetworkingManager netManager = new NetworkingManager("127.0.0.1");

	int w, h;

	/**
	 * Note: you should probably use LWJGLUtil.getPlatform() instead. It is more throughly tested.
	 */
	public static String getOs() {
		String var0 = System.getProperty("os.name").toLowerCase();

		return var0.contains("win") ? "Windows" : (var0.contains("mac") ? "Macosx" : (var0.contains("solaris") ? "Solaris" : (var0.contains("sunos") ? "Solaris" : (var0.contains("linux") ? "Linux" : (var0.contains("unix") ? "Linux" : "Unknown")))));
	}

	public void start() {
		try {

			Display.setDisplayMode(new DisplayMode(900, 700));
			Display.setInitialBackground(0, 0, 0);
			Display.setVSyncEnabled(false);
			Display.setResizable(true);
			PixelFormat format = new PixelFormat();
			//format = format.withSamples(4); -- enables antialiasing
			format = format.withDepthBits(16);
			Display.create(format);
		} catch (LWJGLException e) {
			ErrorManager.logFatal("Error with LWJGL", e);
		}

		initGL();
		TimeKeeper.tick(); 
		IOUtils.initialize();
		Content.initialize(true);
		Canvas.initialize();
		Input.initialize();

		SoundManager.initialize();
		SoundManager.loadClipSet(Content.getUrlForResource("audio/gui/guiCues.csc"));
		ScreenManager.initialize();

		Input.registerInputReciever(ScreenManager.getInputReciever());
		ScreenManager.pushScreen(new SlidingBackgroundScreen(null, "bg.jpg"));
		ScreenManager.pushScreen(new MainMenuScreen(null, netManager));
	}

	public void run() {

		// XXX: keep this here so that unit types and building types are instanced in the correct order. 
		// XXX: If they aren't, stuff breaks.
		@SuppressWarnings("unused")
		BuildingType bType = BuildingType.Town_Square;
		@SuppressWarnings("unused")
		UnitType uType = UnitType.Villager;
		ActionButtonSet.populateUnitTypes();

		TimeKeeper.tick();

		int frame = 0;
		
		while(!Display.isCloseRequested()) {			
			GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);	

			if(Display.wasResized()) {
				resetGLSettings();
				ScreenManager.LayoutGUI();
			}

			float elapsed = TimeKeeper.tick();
			if(elapsed > 0.25f) elapsed = 0.25f;
			SoundManager.update();
			Input.Update(elapsed);
			ScreenManager.update(elapsed);

			Thread.yield();

			ScreenManager.draw(elapsed);	

			GL11.glFlush();
			Display.update();

			// preforms the OpenGL part of content loading for one item ever 3rd frame or if the frame rate is high.
			if((1.0f / 32) > TimeKeeper.silentTick() || frame++ % 3 == 0)	
				Content.bgLoaderTick();

			Display.sync(32);
		}
		exit();
	}

	public static void resetGLSettings() {
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();

		GL11.glOrtho(0, Display.getWidth(), Display.getHeight(), 0, 0, 100);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);

		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDepthFunc(GL11.GL_LEQUAL);
		GL11.glShadeModel(GL11.GL_SMOOTH);
	}

	public void initGL() {
		resetGLSettings();
	}

	public static void main(String[] args) {

		ErrorManager.init("general_log.log");
		ErrorManager.printPropertiesToFile();
		System.out.println(System.getProperty("org.lwjgl.librarypath"));

		File libDir = new File("libs/native/" + getOs() + "/");
		if(libDir.equals(null)) {
			System.err.println("GAHH!");
		}
		String[] lists = libDir.list();
		int inc = 1;
		String cpu = System.getProperty("sun.cpu.isalist");
		boolean bit64 = false;
		if(cpu.equals("amd64")) {
			bit64 = true;
		}
		for(String lib : lists) {
			if(bit64) {
				if(inc%2 == 0) {
					System.load(System.getProperty("user.dir") + "/libs/native/" + getOs() + "/" + lib);
				}
			}
			else {
				if(inc%2 == 1) {
					System.load(System.getProperty("user.dir") + "/libs/native/" + getOs() + "/" + lib);
				}
			}
			inc++;
		}

		MainLoop loop = new MainLoop();
		if(DEBUGGING) {
			loop.start();
			loop.run();
		} else {
			try {
				loop.start();
				loop.run();
			} catch(Throwable ex) {
				ex.printStackTrace();
				StringBuilder sb = new StringBuilder();
				for(int i = 0; i < ex.getStackTrace().length; i++) {
					sb.append(ex.getStackTrace()[i]);
					sb.append('\n');
				}
				Sys.alert("Error!", ex.getMessage() + "\n\n" + sb.toString());
			}
		}
		exit();
	}

	public static void exit() {
		SoundManager.shutDown();
		//TODO: save anything that needs saving
		Threading.shutdown();
		System.exit(0);
	}

	/**
	 * Extracts system specific native libraries to a folder in the machine's
	 *  temp directory, and adds them to the class path for loading. 
	 */
	public void extractNatives() {
		@SuppressWarnings("unused")
		String nativesPath;
		switch(LWJGLUtil.getPlatform()) {
			case LWJGLUtil.PLATFORM_LINUX: nativesPath = "native/linux"; break;
			case LWJGLUtil.PLATFORM_WINDOWS: nativesPath = "native/windows"; break;
			case LWJGLUtil.PLATFORM_MACOSX:	nativesPath = "native/macosx"; break;
			default: nativesPath = "native/solaris"; break;
		}
		
	}
}
