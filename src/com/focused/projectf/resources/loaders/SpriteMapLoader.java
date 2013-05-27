package com.focused.projectf.resources.loaders;

import java.io.BufferedReader;
import java.net.URL;

import com.focused.projectf.ErrorManager;
import com.focused.projectf.graphics.Image;
import com.focused.projectf.resources.Content;
import com.focused.projectf.resources.Resource;
import com.focused.projectf.resources.Texture;
import com.focused.projectf.resources.images.SpriteMapImage;
import com.focused.projectf.utilities.FMath;
import com.focused.projectf.utilities.IOUtils;

public class SpriteMapLoader extends ResourceLoader<SpriteMapImage> {

	@Override
	public Object loadFromFile(String resource) {
		FileData dat = new FileData();
		
		try {
			/*
			URL url = Content.getUrlForResource(resource);//new URL(Content.ResourceDirectory + resource);
			String[] pathParts = url.getFile().split("\\.")[0].split("/");
			StringBuilder sb = new StringBuilder();
			
			for(int i = 0; i < pathParts.length - 1; i++) {
				sb.append(pathParts[i]);
				sb.append('/');
			}
			sb.append(pathParts[pathParts.length - 1]);
			sb.append('.');
			sb.insert(0, "file://");
			sb.append(url.getFile().split("\\.")[1].split("/")[0]);
			URL mapFile = new URL(sb.toString());
			BufferedReader br = IOUtils.openBufferedReader(mapFile);
			*/
			URL url = Content.getUrlForResource(resource);
			BufferedReader br = IOUtils.openBufferedReader(url);

			if(resource.contains("tree"))
				br.read();
			
			String line = null;
			while((line = br.readLine()) != null) {
				if(line.startsWith("#"))
					continue;
				String[] parts = line.replaceAll("([\t ]|  |\t\t)", " ").split(" ");
				dat.BaseImage = Content.getImage(parts[0].trim());
				int width = Integer.parseInt(parts[1]);
				dat.BaseImage.getTexture().setWidth(width, FMath.nextPowerOf2(width));
				int height = Integer.parseInt(parts[2]);
				dat.BaseImage.getTexture().setHeight(height, FMath.nextPowerOf2(height));
				break;
			}			
			Texture texture = dat.BaseImage.getTexture();
			while((line = br.readLine()) != null) {
				if(line.startsWith("#"))
					continue;
				line = line.replaceAll("([\t ]|  |\t\t)", " ");
				String[] parts = line.split(" ");
				SpriteMapImage smp = null;
				if(parts.length >= 5) {
					smp = new SpriteMapImage(texture, 
							Integer.parseInt(parts[0]),
							Integer.parseInt(parts[1]), 
							Integer.parseInt(parts[2]),
							Integer.parseInt(parts[3]));
					if(parts.length >= 6) 
						smp.isSolid = Boolean.parseBoolean(parts[4]);
					if(parts.length >= 7)
						smp.DefaultLeftOffset = Integer.parseInt(parts[5]);
					if(parts.length >= 8)
						smp.DefaultBottomOffset = Integer.parseInt(parts[6]);

					Content.Images.put(resource + "/" + parts[parts.length - 1], smp);
					ErrorManager.logInfo("\t\t\tSubImage: " + parts[parts.length - 1]);
				}
			}
		} catch(Exception ex) {
			ex.printStackTrace(); //... Your ex has a stack trace? Well then... 
		}

		return dat; //ass to the bar
	}

	@Override
	public void pushToOpenGL(String resource, Object fileData, Resource placeHolder) {
		ErrorManager.logInfo("Resource Loaded:\t\t" + resource);
	}

	@Override
	public boolean canLoadFile(String resource) {
		return resource.contains(".smap");
	}

	@Override
	public SpriteMapImage instancePlaceHolder(String file) {
		return new SpriteMapImage();
	}


	protected static class FileData {
		public Image BaseImage;

	}
}
