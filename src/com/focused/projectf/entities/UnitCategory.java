package com.focused.projectf.entities;

import com.focused.projectf.interfaces.IDamageable.DamageType;

public enum UnitCategory {
	Infantry		(DamageType.Sword),
	RangedInfantry	(DamageType.Arrow),
	GunnerInfantry	(DamageType.Gun),
	
	Cavelry			(DamageType.Sword),
	RangedCavelry	(DamageType.Arrow),
	GunnerCavelry	(DamageType.Gun),
	
	Siege			(DamageType.Siege),
	Ship			(DamageType.Arrow),
	Utility			(DamageType.Knife), 
	Other			(DamageType.Fist), 
	;
	public DamageType AttackDamageType;
	UnitCategory(DamageType defaultDamageType) {
		AttackDamageType = defaultDamageType;
	}
}
