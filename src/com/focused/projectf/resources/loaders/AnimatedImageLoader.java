package com.focused.projectf.resources.loaders;

import java.io.BufferedReader;
import java.net.URL;
import java.util.Vector;

import com.focused.projectf.ErrorManager;
import com.focused.projectf.graphics.Image;
import com.focused.projectf.resources.Content;
import com.focused.projectf.resources.Resource;
import com.focused.projectf.resources.images.AnimatedImage;
import com.focused.projectf.resources.loaders.AnimatedImageLoader.fileData.oneImg;
import com.focused.projectf.utilities.IOUtils;

public class AnimatedImageLoader extends ResourceLoader<AnimatedImage> {

	@Override
	public Object loadFromFile(String resource) {
		String fullName = resource, textureGet = null;
		if(!resource.endsWith(".anim"))
			resource = resource.substring(0, resource.lastIndexOf("/"));

		fileData ret = new fileData();

		try {
			URL url = Content.getUrlForResource(resource);
			BufferedReader br = IOUtils.openBufferedReader(url);
			String line = null;

			ErrorManager.logInfo("Resource Loaded:\t\t" + resource);
			while((line = br.readLine()) != null) {
				if(line.startsWith("#"))
					continue;
				line = line.replace("\t", " ");
				line = line.replace("  ", " ");
				line = line.replace("\t", " ");
				line = line.replace("  ", " ");
				String[] parts = line.split(" ");

				Image img = Content.getImage(parts[0]);

				AnimatedImage anim = null;

				switch(parts.length) {
					case 4:
						anim = new AnimatedImage(img.getTexture(),
								Integer.parseInt(parts[2]),
								Integer.parseInt(parts[3]), 
								Float.parseFloat(parts[1]));
						break;
					case 5:
						anim = new AnimatedImage(img.getTexture(),
								Integer.parseInt(parts[2]),
								Integer.parseInt(parts[3]), 
								Integer.parseInt(parts[4]), 
								Float.parseFloat(parts[1]));
						break;
					default: break;
				}

				ret.Images.add(new oneImg(anim));

				String[] nameParts = parts[0].split("/");
				String outputName = resource + "/" + nameParts[nameParts.length - 1];

				Content.Images.put(fullName, anim);
				ErrorManager.logInfo("\t\t> " + outputName);

				if(!fullName.endsWith(".anim") && outputName.contains(fullName)) {
					ret.Instance = anim;
					textureGet = parts[0];
				}
			}
		} catch(Exception ex) {
			ErrorManager.logWarning("", ex);
		}
		if(textureGet != null) {
			ret.InstanceTextureData = Content.StaticImageLoader.loadFromFile(textureGet);
		}
		return ret;
	}

	@Override
	public void pushToOpenGL(String resource, Object fileData, Resource placeHolder) {
		fileData dat = (fileData)fileData;
		
		if(dat.Instance != null) {
			AnimatedImage anim = (AnimatedImage)placeHolder;
			anim.rebuild(dat.Instance.getFrameTime(), dat.Instance.getFramesWide(), dat.Instance.getFramesTall(), dat.Instance.getFrameCount());
			Content.StaticImageLoader.pushToOpenGL(resource, dat.InstanceTextureData, anim);
		}
	}

	@Override
	public boolean canLoadFile(String resource) {
		return resource.contains(".anim");
	}

	@Override
	public AnimatedImage instancePlaceHolder(String file) {
		return new AnimatedImage();
	}

	protected static class fileData {
		public AnimatedImage Instance;
		public Object InstanceTextureData;
		public Vector<oneImg> Images;

		public fileData() {
			Images = new Vector<oneImg>();
		}

		public static class oneImg {
			public AnimatedImage animation;
			public int framesWide;
			public int framesTall;
			public int frameCount;
			public float frameTime;

			public oneImg(AnimatedImage anim) {
				animation = anim;
				framesWide = anim.getFramesWide();
				framesTall = anim.getFramesTall();
				frameCount = anim.getFrameCount();
				frameTime = anim.getFrameTime();
			}
		}
	}
}
