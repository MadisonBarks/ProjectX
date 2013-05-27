package com.focused.projectf.utilities;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

import com.focused.projectf.ErrorManager;
import com.focused.projectf.MainLoop;

public class IOUtils {

	public static ClassLoader WorkingDirectory;
	public static String ResourceDirectory;

	public static void initialize() {
		WorkingDirectory = MainLoop.class.getClassLoader();
		ResourceDirectory = "res/";
	}

	public static String readFileToString(String file, boolean includeLineReturns) {
		StringBuilder sb = new StringBuilder();
		BufferedReader reader = openBufferedReader(file);
		String line;
		try {
			if(includeLineReturns) {
				while((line = reader.readLine()) != null) {
					sb.append(line);
					sb.append(System.lineSeparator());
				}
			} else
				while((line = reader.readLine()) != null) 
					sb.append(line);

		} catch(Exception ex) {
			ErrorManager.logWarning("Unable to read line of file", ex);
		}
		return sb.toString();
	}
	
	public static String readFileToString(URL file, boolean includeLineReturns) {
		StringBuilder sb = new StringBuilder();
		BufferedReader reader = openBufferedReader(file);
		String line;
		try {
			if(includeLineReturns) {
				while((line = reader.readLine()) != null) {
					sb.append(line);
					sb.append(System.lineSeparator());
				}
			} else
				while((line = reader.readLine()) != null) 
					sb.append(line);

		} catch(Exception ex) {
			ErrorManager.logWarning("Unable to read line of file", ex);
		}
		return sb.toString();
	}
	
	public static BufferedReader openBufferedReader(URL url) {
		try {
			return new BufferedReader(new InputStreamReader(url.openStream()));
		} catch (Exception e) {
			ErrorManager.logWarning("Unable to open BufferedReader to file: \"" + url.toExternalForm() + "\"", e);
			e.printStackTrace();
			return null;
		}
	}
	
	public static BufferedReader openBufferedReader(String file) {
		try {
			return new BufferedReader(new InputStreamReader(WorkingDirectory.getResource(file).openStream()));
		} catch (Exception e) {
			ErrorManager.logWarning("Unable to open BufferedReader to file: \"" + file + "\"", e);
			e.printStackTrace();
			return null;
		} 
	}
}
