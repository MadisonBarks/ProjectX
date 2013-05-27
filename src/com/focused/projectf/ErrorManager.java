package com.focused.projectf;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map.Entry;

import org.lwjgl.openal.AL10;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

public class ErrorManager {

	private static File logFile = new File("general_log.log");
	private static boolean initialized = false;
	private static PrintWriter writer;

	public static void logInfo(String message) {
		System.out.println("[Info]" + message);
		if(initialized) {
			writer.println("[Info]" + message);
			writer.flush();
		}
	}
	/**
	 * Checks if an error has occured in OpenGL.
	 * If one has, it will be logged and if Debugging, an error will be thrown for finding the problem quickly
	 */
	public static void GLErrorCheck() {
		
		String error = GLU.gluErrorString(GL11.glGetError());
		
		if(!error.equals("No error")) {
			Throwable out = new Exception();
			logWarning("GL Error: " + error, out);
			if(MainLoop.DEBUGGING)
				out.printStackTrace();
			else 
				throw new Error(error);
		}
	}
	
	public static void ALErrorCheck() {
		String error = AL10.alGetString(AL10.alGetError());
		if(!error.equals("No Error")) {
			Throwable out = new Exception();
			logWarning("AL Error: " + error, out);
			if(MainLoop.DEBUGGING)
				out.printStackTrace();
			else 
				throw new Error(error);
		}
	}

	
	/*
	 * --------------------------------------------------------------------------------
	 * -------------------------I SWEAR------------------------------------------------
	 * -------------------IF YOU TOUCH ANYTHING----------------------------------------
	 * ----------------------BELOW THIS LINE-------------------------------------------
	 * ----------------------I WILL KILL YOU-------------------------------------------
	 * --------------------------------------------------------------------------------
	 */
	public static void logDebug(String message) {
		System.out.println("[Debug]" + message);
		if(initialized) {
			writer.println("[Debug]" + message);
			writer.flush();
		}
		else {
			System.out.println("No log file");
		}
	}
	public static void logWarning(String message, Throwable out) {
		System.err.println("[Warning]" + message);
		if(initialized) {
			writer.println("[Warning]" + message);
			if(out != null) {
				writer.println("Stack Trace: ");
				out.printStackTrace(writer);
			}
			writer.flush();
		}
	}
	public static void logFatal(String message, Exception e) {
		System.err.println("[Fatal]" + message);
		if(initialized) {
			writer.println("[Fatal]" + message);
			writer.println("Stack Trace: ");
			e.printStackTrace(writer);
			writer.flush();
		}
		logWarning("Now shutting down the thread/program", null);
		System.exit(1);
	}
	public static void init(String logFileName) {
		try {
			logFile.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			writer = new PrintWriter(logFileName);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		initialized = true;
	}
	
	/**
	 * Prints out some highly useful information about the current computer to the log file. 
	 */
	public static void printPropertiesToFile() {
		String pathSeparator = System.getProperty("path.separator");
		for (Entry<Object, Object> e : System.getProperties().entrySet()) {
			String out = e.toString().replaceAll(pathSeparator, "\n\t" + pathSeparator);
			System.out.println(out);
			writer.println(out);
		}
	}
}
