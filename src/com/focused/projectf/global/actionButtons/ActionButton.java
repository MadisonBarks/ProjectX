package com.focused.projectf.global.actionButtons;

import com.focused.projectf.graphics.Image;
import com.focused.projectf.resources.Content;
import com.focused.projectf.utilities.random.Chance;

public abstract class ActionButton {
	protected final String ImageSrc;
	protected Image Image;
	protected String Desc, Title;
	public ActionButton(String imageSrc) {
		ImageSrc = imageSrc;
		Image = Content.getImage(imageSrc);
		Desc = "Description and such" + Chance.randomInRange(0, 99);
		Title = "Title " + Chance.randomInRange(0, 99);
	}
	
	public abstract void click();

	public Image getImage() { return Image; }
	public String getImageSrc() { return ImageSrc; }
	public String getDesc() { return Desc; }
	public String getTitle() { return Title; }
	
	public boolean shouldBeVissible() {
		return true;
	}
}
