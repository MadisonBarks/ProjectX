package com.focused.projectf.global.actionButtons;

import com.focused.projectf.Technology;
import com.focused.projectf.ai.buildings.ResearchTechAction;
import com.focused.projectf.entities.ActionQueue;
import com.focused.projectf.entities.Building;
import com.focused.projectf.global.AlertsSystem;
import com.focused.projectf.global.AlertsSystem.AlertType;
import com.focused.projectf.global.ResearchManager;
import com.focused.projectf.graphics.Image;
import com.focused.projectf.players.Player;
import com.focused.projectf.players.Selection;

public class ResearchTechButton extends ActionButton {

	public Technology Tech;
	public ActionButton AfterResearchComplete;

	public ResearchTechButton(Technology tech) {
		super(tech.IconURL);
		Tech = tech;
		super.Title = tech.Name.replace("_", "");
		super.Desc = tech.Description;
	}

	public ResearchTechButton(Technology tech, ActionButton afterResearching) {
		this(tech);
		AfterResearchComplete = afterResearching;
	}

	@Override
	public void click() {
		if(ResearchManager.hasBeenResearched(Player.getThisPlayer(), Tech)) {
			if(AfterResearchComplete != null)
				AfterResearchComplete.click();
		} else {
			Building Bld = Selection.getBuilding();
			ActionQueue queue = Bld.getActionQueue();

			if(queue.size() > 0) {
				for(int i = 0; i < queue.size(); i++) {
					if(queue.get(i) instanceof ResearchTechAction) {
						AlertsSystem.alert("Only one technology may be queried in a building at a time", AlertType.Major, 4);
						return;
					} 
				}

				queue.insert(new ResearchTechAction(Tech));
			} else 
				queue.addAction(new ResearchTechAction(Tech));
		}
	}

	public boolean shouldBeVissible() {
		if(ResearchManager.hasBeenResearched(Player.getThisPlayer(), Tech)){
			if(AfterResearchComplete != null)
				return AfterResearchComplete.shouldBeVissible();

			return false;
		}
		return !ResearchManager.isBeingResearched(Tech);
	}


	public Image getImage() { 
		if(!ResearchManager.hasBeenResearched(Player.getThisPlayer(), Tech))
			return Image;		
		if(AfterResearchComplete != null)
			return AfterResearchComplete.getImage();
		return null;
	}
	public String getImageSrc() { 
		if(!ResearchManager.hasBeenResearched(Player.getThisPlayer(), Tech))
			return ImageSrc;		
		if(AfterResearchComplete != null)
			return AfterResearchComplete.getImageSrc();
		return null;
	}
	public String getDesc() { 
		if(!ResearchManager.hasBeenResearched(Player.getThisPlayer(), Tech))
			return Desc;		
		if(AfterResearchComplete != null)
			return AfterResearchComplete.getDesc()	;
		return null;
	}
	public String getTitle() { 
		if(!ResearchManager.hasBeenResearched(Player.getThisPlayer(), Tech))
			return Title;		
		if(AfterResearchComplete != null)
			return AfterResearchComplete.getTitle();
		return null;
	}
}