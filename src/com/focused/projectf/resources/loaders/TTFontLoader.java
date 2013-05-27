package com.focused.projectf.resources.loaders;

import com.focused.projectf.ErrorManager;
import com.focused.projectf.resources.Content;
import com.focused.projectf.resources.Resource;
import com.focused.projectf.resources.TTFont;

/**
 * TODO: extract to individual elements
 */
public class TTFontLoader extends ResourceLoader<TTFont> {

	@Override
	public Object loadFromFile(String resource) {
		return resource;
	}

	@Override
	public void pushToOpenGL(String resource, Object fileData, Resource placeHolder) {
		TTFont font = new TTFont((String)fileData);
		TTFont out = (TTFont)placeHolder;
		out.font = font.font;
		font.requiresScaling = false;
		ErrorManager.logInfo("Resource Loaded:\t\tFont: " + resource);
	}

	@Override
	public boolean canLoadFile(String resource) {
		boolean res = resource.contains(".font");
		return res;
	}

	@Override
	public TTFont instancePlaceHolder(String file) {
		TTFont font = new TTFont();
		
		String[] nameParts = file.split("\\.")[0].split("\\-");
		String family = nameParts[0];
		int size = Integer.parseInt(nameParts[1]);
		boolean bold = file.contains("-b");
		boolean italic = file.contains("-i");
		
		for(TTFont ttfont : Content.Fonts.values()) {
			if(family.equalsIgnoreCase(ttfont.FontFamily) &&
					ttfont.Size >= size &&
					ttfont.Bold == bold &&
					ttfont.Italic == italic) {
				font.font = ttfont.font;
				font.requiresScaling = true;
			}
		}
		
		return font;
	}
}
