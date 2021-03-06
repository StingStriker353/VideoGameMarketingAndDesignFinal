package com.riphtix.vgmad.entity.exp;

import com.riphtix.vgmad.Game;
import com.riphtix.vgmad.entity.items.armor.Armor;
import com.riphtix.vgmad.entity.mob.Mob;
import com.riphtix.vgmad.level.Level;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class Experience extends Game {

	private static int currentRank;
	private static double currentArmor;
	private static double xpToNextRank;
	private static Level currentLevel;

	public static List<Integer> xpGivenByMobs = new ArrayList<Integer>();
	public static List<Long> armorAtLevel = new ArrayList<>();

	public Experience() {

	}

	public static void init(Level level) {
		currentLevel = level;
		currentRank = level.getClientPlayer().rank;
		currentArmor = level.getClientPlayer().armor;

		if (currentRank <= 10) {
			xpToNextRank = (30 * (currentRank * currentRank)) + (120 * currentRank);
		}

		if (currentRank > 10 && currentRank <= 30) {
			xpToNextRank = (-.3 * (currentRank * currentRank * currentRank) + (30.3 * (currentRank * currentRank) + (132 * currentRank)));
		}

		if (currentRank > 30 && currentRank < 60) {
			xpToNextRank = ((21 * (currentRank * currentRank)) - (165 * currentRank) - 2250) * .82;
		}
	}

	public static double getXPToNextLevel() {
		return xpToNextRank;
	}

	public static double calculateXPFromMob(Mob mob) {
		double xpFromMob = 0;
		if(mob.classification == Mob.Classification.BASIC){
			xpFromMob = (23 * currentRank + (currentRank * (.05 * mob.rank)));
		} else if(mob.classification == Mob.Classification.CHAMPION){
			xpFromMob = (46 * currentRank + (currentRank * (.05 * mob.rank)));
		}else if(mob.classification == Mob.Classification.LORD){
			xpFromMob = (92 * currentRank + (currentRank * (.05 * mob.rank)));
		}else if(mob.classification == Mob.Classification.BOSS){
			xpFromMob = (184 * currentRank + (currentRank * (.05 * mob.rank)));
		}
		return xpFromMob;
	}

	public static double calculateArmor() {
		double armor = currentArmor + (1.25 * currentRank);

		for (int i = 0; i < currentLevel.getClientPlayer().inventory.size(); i++) {
			if (currentLevel.getClientPlayer().inventory.get(i).get(0) instanceof Armor) {
				armor += currentLevel.getClientPlayer().inventory.get(i).get(0).getArmor();
			}
		}
		return armor;
	}

	public static double calculateHealth() {
		return currentLevel.getClientPlayer().maxHealth + (ThreadLocalRandom.current().nextInt(5, 10) * currentRank);
	}

	public static double calculateMana() {
		return currentLevel.getClientPlayer().maxMana + (ThreadLocalRandom.current().nextInt(5, 15) * currentRank);
	}

	public static double calculateMobArmor(Mob mob, double startingStat) {
		double armor = startingStat + (1.05 * mob.rank);

		if(mob.inventory != null) {
			for (int i = 0; i < mob.inventory.size(); i++) {
				if (mob.inventory.get(i).get(0) instanceof Armor) {
					armor += mob.inventory.get(i).get(0).getArmor();
				}
			}
		}
		return armor;
	}

	public static double calculateMobHealth(Mob mob, double startingStat) {
		return startingStat + (ThreadLocalRandom.current().nextInt(5, 15) * mob.rank);
	}

	public static double calculateMobMana(Mob mob, double startingStat) {
		return startingStat + (ThreadLocalRandom.current().nextInt(5, 20) * mob.rank);
	}
}
