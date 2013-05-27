package com.focused.projectf.global.actionButtons;


public class ActionSetActionButton extends ActionButton {

	public final ActionButtonSet SubSet;
	
	public ActionSetActionButton(String imageSrc, String title, String desc, ActionButtonSet set) {
		super(imageSrc);
		SubSet = set;
		Title = title;
		Desc = desc;
	}

	@Override
	public void click() {
		ActionButtonManager.setUsing(SubSet);
	}
}
