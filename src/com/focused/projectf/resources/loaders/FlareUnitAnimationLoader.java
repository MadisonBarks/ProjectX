package com.focused.projectf.resources.loaders;

import java.io.BufferedReader;
import java.util.ArrayList;

import com.focused.projectf.resources.Content;
import com.focused.projectf.resources.Resource;
import com.focused.projectf.resources.images.FlareUnitAnimation;
import com.focused.projectf.utilities.IOUtils;

public class FlareUnitAnimationLoader extends ResourceLoader<FlareUnitAnimation> {

	@Override
	public Object loadFromFile(String resource) {
		Pass pass = new Pass();
		pass.ImgDat = Content.StaticImageLoader.loadFromFile(resource.replace(".flare", ".png"));

		BufferedReader reader = IOUtils.openBufferedReader(Content.ResourceDirectory + resource);
		try {
			String line = null;
			while((line = reader.readLine()) != null) {
				if(line.startsWith("#")) continue;
				String[] parts = line.trim().replace("\t", " ").replace("  ", " ").split(" ");
				pass.framesWide = Integer.parseInt(parts[0]);
				pass.framesTall = Integer.parseInt(parts[1]);
				if(pass.framesWide < 1 || pass.framesTall < 1)
					throw new Error("FramesWide and FramesTall must be greater than 0");
				break;
			}
			while((line = reader.readLine()) != null) {
				if(line.startsWith("#"))
					continue;
				pass.Actions.add(new FlareUnitAnimation.FlareAction(line));
			}
		} catch(Exception ex) {
			ex.printStackTrace();
		}

		return pass;
	}

	@Override
	public void pushToOpenGL(String resource, Object fileData, Resource placeHolder) {
		Pass pass = (Pass)fileData;
		Content.StaticImageLoader.pushToOpenGL(resource.replace(".flare", ".png"), pass.ImgDat, placeHolder);
		
		FlareUnitAnimation.FlareAction[] acts = new FlareUnitAnimation.FlareAction[pass.Actions.size()];
		for(int i = 0; i < acts.length; i++)
			acts[i] = pass.Actions.get(i);
		((FlareUnitAnimation)placeHolder).params(pass.framesWide, pass.framesTall, acts);
	}

	@Override
	public boolean canLoadFile(String resource) {
		return resource.endsWith(".flare");
	}

	@Override
	public FlareUnitAnimation instancePlaceHolder(String file) {
		return new FlareUnitAnimation();
	}

	public static class Pass {
		public int framesWide;
		public int framesTall;
		public Object ImgDat;
		public ArrayList<FlareUnitAnimation.FlareAction> Actions = new ArrayList<FlareUnitAnimation.FlareAction>();
	}
}
