package com.riphtix.vgmad.entity.mob;

import com.riphtix.vgmad.entity.Entity;
import com.riphtix.vgmad.entity.exp.Experience;
import com.riphtix.vgmad.entity.items.Inventory;
import com.riphtix.vgmad.entity.items.Item;
import com.riphtix.vgmad.entity.particle.Particle;
import com.riphtix.vgmad.entity.projectile.SorceressProjectile;
import com.riphtix.vgmad.entity.spawner.ParticleSpawner;
import com.riphtix.vgmad.gfx.AnimatedSprite;
import com.riphtix.vgmad.gfx.Screen;
import com.riphtix.vgmad.gfx.Sprite;
import com.riphtix.vgmad.gfx.SpriteSheet;
import com.riphtix.vgmad.handler.Sound;
import com.riphtix.vgmad.level.tile.hitbox.MobHitbox;
import com.riphtix.vgmad.level.tile.hpBar.MobHealthBar;
import com.riphtix.vgmad.util.Vector2i;

import java.util.List;

public class ChampionShooter extends Mob {

	private AnimatedSprite down = new AnimatedSprite(SpriteSheet.genie_down, 32, 32, 3);
	private AnimatedSprite up = new AnimatedSprite(SpriteSheet.genie_up, 32, 32, 3);
	private AnimatedSprite left = new AnimatedSprite(SpriteSheet.genie_left, 32, 32, 3);
	private AnimatedSprite right = new AnimatedSprite(SpriteSheet.genie_right, 32, 32, 3);

	private AnimatedSprite animSprite = this.down;

	private int time = 0;
	private int xa = 0;
	private int ya = 0;

	private Entity rand = null;

	private int firerate = 0;

	public MobHitbox hitbox;
	public MobHealthBar healthBar0;
	public MobHealthBar healthBar25;
	public MobHealthBar healthBar50;
	public MobHealthBar healthBar75;
	boolean health75 = false;
	boolean health50 = false;
	boolean health25 = false;
	boolean health0 = false;

	public Inventory inventory;
	
	public ChampionShooter(int x, int y, int rank, Classification classification) {
		this.x = x << 4;
		this.y = y << 4;
		this.rank = rank;
		this.classification = classification;
		sprite = animSprite.getSprite();
		firerate = SorceressProjectile.FIRE_RATE;
		hitbox = new MobHitbox(Sprite.hitbox21x32);
		inventory = new Inventory();
		healthBar0 = new MobHealthBar((int) this.x - 10, (int) this.y - 20, Sprite.healthBar0);
		healthBar25 = new MobHealthBar((int) this.x - 5, (int) this.y - 20, Sprite.healthBar25);
		healthBar50 = new MobHealthBar((int) this.x, (int) this.y - 20, Sprite.healthBar50);
		healthBar75 = new MobHealthBar((int) this.x + 5, (int) this.y - 20, Sprite.healthBar75);
		range = 100;
		weapon = starterFireStaff;
		weapon.setRange(range);
		inventory.add(weapon);

		//Shooter default attributes
		maxHealth = Experience.calculateMobHealth(this, 350);
		health = maxHealth;
		maxMana = Experience.calculateMobMana(this, 100);
		mana = maxMana;
		armor = Experience.calculateMobArmor(this, 0);
		protectSpell = 0.0;
	}

	public void tick() {
		time++;
		animSprite.tick();
		if (firerate > 0) firerate--;
		if (ya < 0) {
			animSprite = up;
			dir = Direction.UP;
		} else if (ya > 0) {
			animSprite = down;
			dir = Direction.DOWN;
		}
		if (xa < 0) {
			animSprite = left;
			dir = Direction.LEFT;
		} else if (xa > 0) {
			animSprite = right;
			dir = Direction.RIGHT;
		}
		if (xa != 0 || ya != 0) {
			move(xa, 8, -10, ya, 0, 14);
			walking = true;
		} else {
			walking = false;
		}

		if (Vector2i.getDistance(new Vector2i((int) this.getX(), (int) this.getY()), new Vector2i((int) level.getClientPlayer().getX(), (int) level.getClientPlayer().getY())) <= range) {
			shootClosest();
			//shootRandom();
		}

		//getStatusEffects();

		healthBar0.setXY(this.x - 10, this.y - 20);
		healthBar25.setXY(this.x - 5, this.y - 20);
		healthBar50.setXY(this.x, this.y - 20);
		healthBar75.setXY(this.x + 5, this.y - 20);
		if (health / maxHealth > 0 && !health0) {
			level.add(healthBar0);
			if (health / maxHealth > .25 && !health25) {
				level.add(healthBar25);
				if (health / maxHealth > .5 && !health50) {
					level.add(healthBar50);
					if (health / maxHealth > .75 && !health75) {
						level.add(healthBar75);
						health75 = true;
					}
					health50 = true;
				}
				health25 = true;
			}
			health0 = true;
		}

		if (health / maxHealth <= .75) {
			if (health75) {
				healthBar75.remove();
				health75 = false;
			}
			if (health / maxHealth <= .5) {
				if (health50) {
					healthBar50.remove();
					health50 = false;
				}
				if (health / maxHealth <= .25) {
					if (health25) {
						healthBar25.remove();
						health25 = false;
					}
				}
			}
		}
	}

	public void championShooterDamaged(double damage) {
		// can have a multiplier here to reduce health damage due to spells or armor
		if(armor > 0 & protectSpell > 0) {
			health -= (damage - (damage / armor) - (damage / protectSpell));
		} else health -= damage;

		if (isDead()) {
			Sound.SoundEffect.FEMALE_DEAD.play();
			level.getClientPlayer().xp += Experience.calculateXPFromMob(this);
			level.getClientPlayer().totalXP += Experience.calculateXPFromMob(this);
			if (inventory != null && inventory.size() > 0) {
				for (int i = 0; i < inventory.size(); i++) {
						level.addItem(inventory.get(i).get(0), (int) x >> 4, (int) y >> 4);

				}
			}
			level.add(new ParticleSpawner((int) x, (int) y, 44, 50, level, 0xffc40000));
			remove();
			healthBar0.remove();
			healthBar25.remove();
			healthBar50.remove();
			healthBar75.remove();
		}
	}

	public boolean isDead() {
		return (health <= 0);
	}

	protected void shootRandom() {
		if (time % 60 == 0) {
			List<Entity> entities = level.getEntities(this, range);
			entities.add(level.getClientPlayer());
			int index = random.nextInt(entities.size());
			rand = entities.get(index);
		}

		if (rand != null) {
			if (!(rand instanceof Particle) && !(rand instanceof ParticleSpawner) && rand instanceof Player && firerate <= 0) {
				double dx = rand.getX() - x;
				double dy = rand.getY() - y;
				double dir = Math.atan2(dy, dx);
				if(inventory.contains(starterFireStaff)) {
					shoot(x, y, dir, this );
					firerate = SorceressProjectile.FIRE_RATE;
				}
			}
		}
	}

	protected void shootClosest() {
		List<Entity> entities = level.getEntities(this, 336);
		entities.add(level.getClientPlayer());

		double min = 0;
		Entity closest = null;
		for (int i = 0; i < entities.size(); i++) {
			Entity e = entities.get(i);
			double distance = Vector2i.getDistance(new Vector2i((int) x, (int) y), new Vector2i((int) e.getX(), (int) e.getY()));
			if (i == 0 || distance < min) {
				min = distance;
				closest = e;
			}
		}

		if (closest != null) {
			if (!(closest instanceof Particle) && !(closest instanceof ParticleSpawner) && closest instanceof Player && firerate <= 0) {
				double dx = closest.getX() - x;
				double dy = closest.getY() - y;
				double dir = Math.atan2(dy, dx);
				if(inventory.contains(starterFireStaff)) {
					shoot(x, y, dir, this);
					firerate = SorceressProjectile.FIRE_RATE;
				}
			}
		}
	}

	public void render(Screen screen) {
		sprite = animSprite.getSprite();
		screen.renderMob((int) x - 16, (int) y - 16, sprite);
		hitbox.render((int) x - 10, (int) y - 16, screen);
	}
}
