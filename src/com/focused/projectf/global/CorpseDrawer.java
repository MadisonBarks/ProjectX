package com.focused.projectf.global;


import java.util.Vector;

import com.focused.projectf.Point;
import com.focused.projectf.entities.Unit;
import com.focused.projectf.entities.UnitType;
import com.focused.projectf.graphics.Canvas;
import com.focused.projectf.graphics.Color;
import com.focused.projectf.resources.images.FlareUnitAnimation;
import com.focused.projectf.utilities.FMath;
import com.focused.projectf.utilities.TimeKeeper;

public class CorpseDrawer {

	public static final int MAX_CORPSES = 200;

	private static Vector<Corpse> Corpses = new Vector<Corpse>();

	public static void addCorpse(Unit unit) {
		Corpses.add(new Corpse(unit));
	}

	private static final int DEFAULT_DEATH_ANIM_INDEX = 4;

	public static void RenderCorpses() {
		for(int i = 0; i < Corpses.size(); i++) {
			Corpse c = Corpses.get(i);
			FlareUnitAnimation anim = Unit.Animations.get(c.Type);
			c.timeDead += TimeKeeper.getElapsed();

			anim.setStateTime(DEFAULT_DEATH_ANIM_INDEX, c.Direction, 
					FMath.min(c.timeDead + TimeKeeper.getElapsed(), 
							anim.Actions[DEFAULT_DEATH_ANIM_INDEX].PlayTime * 0.999f));
			if(c.timeDead > 5)	{
				if(c.timeDead < 6) {
					Canvas.drawImage(anim, c.position, 
							Color.blend(Color.WHITE, Color.BLUE, 1 - (c.timeDead - 6)),
							Canvas.calcDepth(c.position.Y));
				} else if (c.timeDead < 7) {
					Canvas.drawImage(anim, c.position, Color.BLUE.withAlpha((7 - c.timeDead)), Canvas.calcDepth(c.position.Y));
				} else {
					Corpses.remove(i);
					i--;
				}

			} else
				Canvas.drawImage(anim, c.position);
		}		
	}
	
	public static int getCount() {
		return Corpses.size();
	}
	
	
	public static class Corpse {

		public final int Direction;
		public final UnitType Type;
		public Point position;
		public float timeDead = 0;
		public Corpse(Unit unit) {
			Direction = unit.Direction;
			Type = unit.getType();
			FlareUnitAnimation anim = Unit.Animations.get(unit.getType());
			position = unit.getPosition().minus(anim.getWidth() / 2, anim.getHeight() / 5 * 4);
		}
	}

}
