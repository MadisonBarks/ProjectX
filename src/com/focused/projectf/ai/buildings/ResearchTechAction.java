package com.focused.projectf.ai.buildings;

import com.focused.projectf.Technology;
import com.focused.projectf.global.ResearchManager;
import com.focused.projectf.graphics.Image;
import com.focused.projectf.players.Player;
import com.focused.projectf.resources.Content;
import com.focused.projectf.utilities.TimeKeeper;

public class ResearchTechAction extends BuildingAction {

	protected float Progress;
	public final Technology Tech;
	public Image icon;
	public ResearchTechAction(Technology tech) {
		Tech = tech;
		
		Progress = 0;
		icon = Content.getImage(Tech.IconURL);
	}
	
	@Override
	public void begin() { 
		ResearchManager.setBeingResearched(Tech, true);
	}

	@Override
	public void cancel() {
		ResearchManager.setBeingResearched(Tech, false);
		// TODO: return used up resources;
	}

	@Override
	public boolean update() {
		Progress += TimeKeeper.getElapsed();
		boolean completion = Progress > Tech.TimeToResearch;
		if(completion) {
			ResearchManager.onResearchComplete(Player.getThisPlayer(), Tech);
		}
		return completion;
	}

	@Override
	public float getProgress() { return Progress / Tech.TimeToResearch; }

	@Override
	public Image getIconImage() {
		return icon;
	}
}
