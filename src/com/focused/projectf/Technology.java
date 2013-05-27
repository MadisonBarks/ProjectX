package com.focused.projectf;

public enum Technology {
	
	Age0				("Starting Age. ", "This should never show up", "", 0, 0, 0, 0, 0, 0),
	Age1				("Evolve 1", "Description  2rqewdqfbqre", "bg.jpg", 50, 100, 100, 100, 100, 0, Age0),
	Age2				("Evolve 2", "Description  2rqewdqfbqre", "bg.jpg", 50, 100, 100, 100, 100, 0, Age1),
	Age3				("Evolve 3", "Description  2rqewdqfbqre", "bg.jpg", 50, 100, 100, 100, 100, 0, Age2),
	Age4				("Evolve 4", "Description  2rqewdqfbqre", "bg.jpg", 50, 100, 100, 100, 100, 0, Age3),
	Organized_Watch		("Organised Watch", "", "bg.jpg", 20, 0, 200, 0, 0, 0, Age0),
	Binoculars			("Binoculars", "", "bg.jpg", 30, 100, 500, 0, 0, 0, Organized_Watch, Age1),
	
	;
	public final String Name, Description, IconURL;
	public float TimeToResearch;
	public int WoodCost, FoodCost, GoldCost, StoneCost, RadiumCost;
	public Technology[] Requirements;
	
	Technology() {
		this(null, null, null, 0, 0, 0, 0, 0, 0);
	}
	
	Technology(String name, String description, String iconUrl, float timeToResearch, int woodCost, int foodCost, int goldCost, int stoneCost, int radiumCost, Technology... requirements) {
		Name = name;
		Description = description;
		IconURL = iconUrl;
		TimeToResearch = timeToResearch;
		
		Requirements = requirements;
	}

	public boolean ComesBeforeAge(Technology age) {
		for(int i = 0; i < Requirements.length; i++) {
			if(Requirements[i].equals(age))
				return true;
			else if(ComesBeforeAge(Requirements[i]))
				return true;
		}
		return false;
	}
}
