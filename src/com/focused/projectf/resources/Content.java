package com.focused.projectf.resources;

import java.net.URL;
import java.util.HashMap;
import java.util.Vector;

import org.lwjgl.opengl.ContextCapabilities;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;

import com.focused.projectf.ErrorManager;
import com.focused.projectf.global.Threading;
import com.focused.projectf.graphics.Image;
import com.focused.projectf.resources.loaders.AnimatedImageLoader;
import com.focused.projectf.resources.loaders.FlareUnitAnimationLoader;
import com.focused.projectf.resources.loaders.ResourceLoader;
import com.focused.projectf.resources.loaders.ShaderLoader;
import com.focused.projectf.resources.loaders.SpriteMapLoader;
import com.focused.projectf.resources.loaders.StaticImageLoader;
import com.focused.projectf.resources.loaders.TTFontLoader;
import com.focused.projectf.resources.shaders.ShaderProgram;
import com.focused.projectf.utilities.IOUtils;

public final class Content implements Runnable {

	public static ContextCapabilities Capabilities			= null;
	public static boolean ShadersSupported 					= false;
	public static boolean NonPowersOf2TextureSizesSupported = false;
	
	public static String WorkingDirectory;
	public static String ResourceDirectory = "res/";
	protected static boolean bgLoaderRunning;

	private static Vector<Integer> TextureIds = new Vector<Integer>();

	public static HashMap<String, Image> Images = new HashMap<String, Image>();
	public static HashMap<String, TTFont> Fonts = new HashMap<String, TTFont>();
	public static HashMap<String, SoundClip> Sounds = new HashMap<String, SoundClip>();

	public static HashMap<String, Texture> Textures = new HashMap<String, Texture>();
	public static Vector<ResourceLoader<?>> Loaders = new Vector<ResourceLoader<?>>();
	public static Vector<ResourceQueue<?>> LoadingQueue = new Vector<ResourceQueue<?>>();
	public static Vector<ResourceQueue<?>> LoadedQueue = new Vector<ResourceQueue<?>>();

	protected static Content runner = new Content();

	public static StaticImageLoader StaticImageLoader;

	public static boolean LoadContent;

	public static void initialize(boolean loadContent) {
		LoadContent = loadContent;
		Loaders.add(StaticImageLoader = new StaticImageLoader());
		Loaders.add(new ShaderLoader());
		Loaders.add(new SpriteMapLoader());
		Loaders.add(new AnimatedImageLoader());
		Loaders.add(new TTFontLoader());
		Loaders.add(new FlareUnitAnimationLoader());

		if(LoadContent) {
			while (TextureIds.size() < 10)
				TextureIds.add(new Integer(GL11.glGenTextures()));
			
			Capabilities						= GLContext.getCapabilities();
			ShadersSupported 					= Capabilities.OpenGL20;
			NonPowersOf2TextureSizesSupported	= Capabilities.GL_ARB_texture_non_power_of_two;
			
		} else {
			while (TextureIds.size() < 10)
				TextureIds.add(0);
		}
	}

	public static boolean isImageLoaded(String file) {
		Image img = Images.get(file);
		if(img == null || img.getTexture() == null || img.getTexture().getId() == 0)
			return false;
		for(int i = 0; i < LoadingQueue.size(); i++)
			synchronized(LoadingQueue) {
				if(LoadingQueue.get(i).file.equals(file))
					return false;
			}
		for(int i = 0; i < LoadedQueue.size(); i++) 
			synchronized(LoadingQueue) {
				if(LoadedQueue.get(i).file.equals(file))
					return false;
			}
		return true;
	}

	public static TTFont getFont(String fontFamily, int size, boolean bold,
			boolean italic) {
		String fullName = fontFamily + "-" + size;
		if (bold)
			fullName += "-b";
		if (italic)
			fullName += "-i";
		fullName += ".font";

		TTFont font = Fonts.get(fullName);
		if (font == null)
			font = loadResource(fullName);

		return font;
	}

	public static ShaderProgram getShader(String file) {
		return (ShaderProgram) loadResource(file);
	}

	public static Image getImage(String file) {
		Image img = Images.get(file);
		if (img == null) img = loadResource(file);
		if (img == null) throw new Error("Something broked");
		return img;
	}

	public static SoundClip getSoundClip(String file) {
		SoundClip sound = Sounds.get(file);
		if (sound == null) sound = loadResource(file);
		if (sound == null) throw new Error("Something broked");
		return sound;
	}

	public static void loadResources(String... files) {
		if (files == null)
			return;
		for (String file : files) {
			if (Images.containsKey(file)) continue;
			if (Fonts.containsKey(file)) continue;
			loadResource(file);
		}
	}

	@SuppressWarnings("unchecked")
	protected static synchronized <T extends Resource> T loadResource(final String file) {
		if(file == null)
			throw new Error("File can't be null");

		for (ResourceLoader<?> loader : Loaders) {
			if (loader.canLoadFile(file)) {
				try {
					Resource Placeholder = loader.instancePlaceHolder(file);

					T res = null;
					if (Placeholder instanceof Resource)
						res = (T) Placeholder;
					else
						throw new Error("");
					if(LoadContent) {

						ResourceQueue<T> q = new ResourceQueue<T>();
						q.file = file;
						q.backReference = res;
						if (loader instanceof ResourceLoader<?>)
							q.loader = (ResourceLoader<T>) loader;

						if(res instanceof TTFont)
							LoadingQueue.add(0, q);
						else
							LoadingQueue.add(q);
						Thread.yield();

						if (res instanceof Image)
							Images.put(file, (Image) res);
						else if (res instanceof TTFont)
							Fonts.put(file, (TTFont) res);
						else if (res instanceof ShaderProgram) {
							// XXX: don't store it. Otherwise you have to restart the game to see
							// what effect changes had, rather than waiting one frame
						} else
							throw new Error("Don't know where to store this type of resource");

						if (!bgLoaderRunning) {
							bgLoaderRunning = true;
							Threading.pushOperation(runner);
							Threading.pushOperation(runner);
						}
					}
					return res;
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}

		ErrorManager.logWarning("Unable to load file: \'" + file
				+ "\' : no ResourceLoader that accepts it is avalible",
				new Exception());

		Thread.yield();

		return null;
	}

	public static void bgLoaderTick() {
		ErrorManager.GLErrorCheck();

		while (TextureIds.size() < 50)
			TextureIds.add(new Integer(GL11.glGenTextures()));

		if (LoadedQueue.size() > 0) {
			ResourceQueue<?> queue;
			synchronized(LoadedQueue) {
				queue = LoadedQueue.remove(0);
			}
			queue.loader.pushToOpenGL(queue.file, queue.fileDat, queue.backReference);
		}
		ErrorManager.GLErrorCheck();
	}

	public void run() {
		bgLoaderRunning = true;

		do {
			ResourceQueue<?> res = null;
			synchronized (LoadingQueue) {
				if(LoadingQueue.size() > 0)
					res = LoadingQueue.remove(0);
			}
			if (res == null)
				continue;
			res.fileDat = res.loader.loadFromFile(res.file);
			synchronized (LoadedQueue) {
				LoadedQueue.add(res);
			}
		} while (LoadingQueue.size() > 0);
		bgLoaderRunning = false;
	}

	/** returns true if there are still resources being loaded */
	public static boolean isLoading() {
		return LoadingQueue.size() > 0 || LoadedQueue.size() > 0;
	}

	public static int popTextureId() {
		if(!LoadContent)
			return 0;
		return TextureIds.remove(0);
	}

	public static URL getUrlForResource(String str) {
		URL url = IOUtils.WorkingDirectory.getResource(Content.ResourceDirectory + str);
		if (url == null)
			url = IOUtils.WorkingDirectory.getResource(str);
		if (url == null)
			throw new Error("File can't be found: " + str);
		return url;
	}

	public static boolean isUsingResources() {
		return LoadContent;
	}

	public static boolean areLoaded(String[] required) {
		if(required == null)
			return true;

		for(int i = 0; i < required.length; i++) {
			String file = required[i];
			if(file.endsWith(".smap"))
				file = file.replace("smap", "png");
			Resource r = Fonts.get(file);
			r = (r == null) ? Images.get(file) : r;
				
			if(r == null || !r.isLoaded())
				return false;
		}

		return true;
	}
}