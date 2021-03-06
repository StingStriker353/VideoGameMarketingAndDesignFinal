package com.riphtix.vgmad.entity.mob;

import com.riphtix.vgmad.Game;
import com.riphtix.vgmad.entity.exp.Experience;
import com.riphtix.vgmad.entity.items.Inventory;
import com.riphtix.vgmad.entity.items.Item;
import com.riphtix.vgmad.entity.items.armor.Armor;
import com.riphtix.vgmad.entity.items.basic.ResourceItem;
import com.riphtix.vgmad.entity.items.weapons.RangedWeapon;
import com.riphtix.vgmad.entity.items.weapons.Weapon;
import com.riphtix.vgmad.entity.projectile.FireMageProjectile;
import com.riphtix.vgmad.entity.projectile.Projectile;
import com.riphtix.vgmad.gfx.AnimatedSprite;
import com.riphtix.vgmad.gfx.Screen;
import com.riphtix.vgmad.gfx.Sprite;
import com.riphtix.vgmad.gfx.SpriteSheet;
import com.riphtix.vgmad.gfx.ui.*;
import com.riphtix.vgmad.gfx.ui.UIManager;
import com.riphtix.vgmad.handler.Keyboard;
import com.riphtix.vgmad.handler.Mouse;
import com.riphtix.vgmad.handler.Sound;
import com.riphtix.vgmad.level.tile.hitbox.PlayerHitbox;
import com.riphtix.vgmad.util.ImageUtils;
import com.riphtix.vgmad.util.Vector2i;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.Random;

public class Player extends Mob {

	private String name;
	private Keyboard input;
	private Sprite sprite;
	public boolean walking;

	private AnimatedSprite down = new AnimatedSprite(SpriteSheet.maleElf_down, 32, 32, 3);
	private AnimatedSprite up = new AnimatedSprite(SpriteSheet.maleElf_up, 32, 32, 3);
	private AnimatedSprite left = new AnimatedSprite(SpriteSheet.maleElf_left, 32, 32, 3);
	private AnimatedSprite right = new AnimatedSprite(SpriteSheet.maleElf_right, 32, 32, 3);

	private AnimatedSprite animSprite = down;

	private int firerate = 0;
	public double xa, ya;

	private UIManager ui;
	public UIPanel panel;
	public UILabel helpLabel;
	public UILabel help1Label;
	public UILabel slot1Label, slot2Label, slot3Label;
	public UILabel slot4Label, slot5Label, slot6Label;
	public UILabel slot7Label, slot8Label, slot9Label;
	private UILabel nameLabel;
	private UILabel lvlLabel;
	private UILabel lvlRankLabel;
	private UILabel xpCounterLabel;
	private UILabel hpCounterLabel;
	private UILabel mpCounterLabel;
	private UILabel armorStatLabel;
	private UIProgressBar uiHealthBar;
	private UIProgressBar uiManaBar;
	private UIProgressBar uiExperienceBar;
	UIProgressMark uiHP25Percent, uiHP50Percent, uiHP75Percent;
	UIProgressMark uiMP25Percent, uiMP50Percent, uiMP75Percent;
	UIProgressMark uiXP25Percent, uiXP50Percent, uiXP75Percent;

	private UIButton uiButtonOptions;
	private UIButton uiButtonImageTest;

	private BufferedImage image;

	public PlayerHitbox hitbox;

	private int time = 0;

	public Player(String name, int x, int y, Keyboard input) {
		this.name = name;
		this.x = x;
		this.y = y;
		this.input = input;
		sprite = animSprite.getSprite();
		firerate = FireMageProjectile.FIRE_RATE;
		hitbox = new PlayerHitbox(Sprite.hitbox21x32);
		inventory = new Inventory();
		range = 336;

		rightXOffset = 8;
		leftXOffset = -10;
		topYOffset = 0;
		bottomYOffset = 14;

		ui = Game.getUIManager();
		panel = (UIPanel) new UIPanel(new Vector2i((300 - 80) * 3, 0), new Vector2i(80 * 3, 300 * 3)).setColor(0xff505050);
		ui.addPanel(panel);

		nameLabel = new UILabel(new Vector2i(40, 200), name);
		nameLabel.setColor(0xffa0a0a0);
		nameLabel.setFont(new Font("Verdana", Font.PLAIN, 24));
		nameLabel.dropShadow = true;
		panel.addComponent(nameLabel);

		lvlLabel = new UILabel(new Vector2i(nameLabel.position.x + 65, nameLabel.position.y), "LVL:");
		lvlLabel.setColor(0xffa0a0a0);
		lvlLabel.setFont(new Font("Verdana", Font.PLAIN, 10));
		lvlLabel.dropShadow = true;
		lvlLabel.dropShadowOffset = 1;
		panel.addComponent(lvlLabel);

		lvlRankLabel = new UILabel(new Vector2i(130, 200), "" + rank);
		lvlRankLabel.setColor(0xffa0a0a0);
		lvlRankLabel.setFont(new Font("Verdana", Font.PLAIN, 13));
		lvlRankLabel.dropShadow = true;
		lvlRankLabel.dropShadowOffset = 1;
		panel.addComponent(lvlRankLabel);

		uiHealthBar = new UIProgressBar(new Vector2i(nameLabel.position.x - 5, nameLabel.position.y + 10), new Vector2i(180, 15));
		uiHealthBar.setColor(0xff5f5f5f);
		uiHealthBar.setForegroundColor(new Color(0xffc70000));
		uiHealthBar.dropShadow = true;
		uiHealthBar.dropShadowOffset = 1;
		panel.addComponent(uiHealthBar);
		UILabel hpLabel = new UILabel(new Vector2i(uiHealthBar.position.x - 28, uiHealthBar.position.y + 12), "HP:");
		hpLabel.setColor(0xffa0a0a0);
		hpLabel.setFont(new Font("Verdana", Font.BOLD, 15));
		hpLabel.dropShadow = true;
		hpLabel.dropShadowOffset = 1;
		panel.addComponent(hpLabel);
		uiHP25Percent = new UIProgressMark(new Vector2i(uiHealthBar.position.x + 45, uiHealthBar.position.y), new Vector2i(1, 15));
		panel.addComponent(uiHP25Percent);
		uiHP50Percent = new UIProgressMark(new Vector2i(uiHealthBar.position.x + 90, uiHealthBar.position.y), new Vector2i(1, 15));
		panel.addComponent(uiHP50Percent);
		uiHP75Percent = new UIProgressMark(new Vector2i(uiHealthBar.position.x + 135, uiHealthBar.position.y), new Vector2i(1, 15));
		panel.addComponent(uiHP75Percent);
		hpCounterLabel = new UILabel(new Vector2i(uiHealthBar.position.x, uiHealthBar.position.y + 10), (int) health + "/" + (int) maxHealth);
		hpCounterLabel.setColor(0xffa0a0a0);
		hpCounterLabel.setFont(new Font("Verdana", Font.PLAIN, 10));
		hpCounterLabel.dropShadow = true;
		hpCounterLabel.dropShadowOffset = 1;
		panel.addComponent(hpCounterLabel);

		uiManaBar = new UIProgressBar(new Vector2i(uiHealthBar.position.x, uiHealthBar.position.y + 25), new Vector2i(180, 15));
		uiManaBar.setColor(0xff5f5f5f);
		uiManaBar.setForegroundColor(new Color(0xff009696));
		uiManaBar.dropShadow = true;
		uiManaBar.dropShadowOffset = 1;
		panel.addComponent(uiManaBar);
		UILabel mpLabel = new UILabel(new Vector2i(uiManaBar.position.x - 30, uiManaBar.position.y + 12), "MP:");
		mpLabel.setColor(0xffa0a0a0);
		mpLabel.setFont(new Font("Verdana", Font.BOLD, 15));
		mpLabel.dropShadow = true;
		mpLabel.dropShadowOffset = 1;
		panel.addComponent(mpLabel);
		uiMP25Percent = new UIProgressMark(new Vector2i(uiManaBar.position.x + 45, uiManaBar.position.y), new Vector2i(1, 15));
		panel.addComponent(uiMP25Percent);
		uiMP50Percent = new UIProgressMark(new Vector2i(uiManaBar.position.x + 90, uiManaBar.position.y), new Vector2i(1, 15));
		panel.addComponent(uiMP50Percent);
		uiMP75Percent = new UIProgressMark(new Vector2i(uiManaBar.position.x + 135, uiManaBar.position.y), new Vector2i(1, 15));
		panel.addComponent(uiMP75Percent);
		mpCounterLabel = new UILabel(new Vector2i(uiManaBar.position.x, uiManaBar.position.y + 10), (int) mana + "/" + (int) maxMana);
		mpCounterLabel.setColor(0xffa0a0a0);
		mpCounterLabel.setFont(new Font("Verdana", Font.PLAIN, 10));
		mpCounterLabel.dropShadow = true;
		mpCounterLabel.dropShadowOffset = 1;
		panel.addComponent(mpCounterLabel);

		uiExperienceBar = new UIProgressBar(new Vector2i(uiManaBar.position.x, uiManaBar.position.y + 25), new Vector2i(180, 15));
		uiExperienceBar.setColor(0xff5f5f5f);
		uiExperienceBar.setForegroundColor(new Color(0xffff7700));
		uiExperienceBar.dropShadow = true;
		uiExperienceBar.dropShadowOffset = 1;
		panel.addComponent(uiExperienceBar);
		UILabel xpLabel = new UILabel(new Vector2i(uiExperienceBar.position.x - 28, uiExperienceBar.position.y + 12), "XP:");
		xpLabel.setColor(0xffa0a0a0);
		xpLabel.setFont(new Font("Verdana", Font.BOLD, 15));
		xpLabel.dropShadow = true;
		xpLabel.dropShadowOffset = 1;
		panel.addComponent(xpLabel);
		uiXP25Percent = new UIProgressMark(new Vector2i(uiExperienceBar.position.x + 45, uiExperienceBar.position.y), new Vector2i(1, 15));
		panel.addComponent(uiXP25Percent);
		uiXP50Percent = new UIProgressMark(new Vector2i(uiExperienceBar.position.x + 90, uiExperienceBar.position.y), new Vector2i(1, 15));
		panel.addComponent(uiXP50Percent);
		uiXP75Percent = new UIProgressMark(new Vector2i(uiExperienceBar.position.x + 135, uiExperienceBar.position.y), new Vector2i(1, 15));
		panel.addComponent(uiXP75Percent);
		xpCounterLabel = new UILabel(new Vector2i(uiExperienceBar.position.x, uiExperienceBar.position.y + 10), xp + "/" + (int) Experience.getXPToNextLevel());
		xpCounterLabel.setColor(0xffa0a0a0);
		xpCounterLabel.setFont(new Font("Verdana", Font.PLAIN, 10));
		xpCounterLabel.dropShadow = true;
		xpCounterLabel.dropShadowOffset = 1;
		panel.addComponent(xpCounterLabel);

		//Stat display
		armorStatLabel = new UILabel(new Vector2i(uiExperienceBar.position.x, uiExperienceBar.position.y + 30), "Armor: " + armor);
		armorStatLabel.setColor(0xffa0a0a0);
		armorStatLabel.setFont(new Font("Verdana", Font.PLAIN, 10));
		armorStatLabel.dropShadow = true;
		armorStatLabel.dropShadowOffset = 1;
		panel.addComponent(armorStatLabel);

		//Inventory display
		slot1Label = new UILabel(new Vector2i(armorStatLabel.position.x - 25, armorStatLabel.position.y + 30), "");
		slot1Label.setColor(0xffa0a0a0);
		slot1Label.setFont(new Font("Verdana", Font.PLAIN, 10));
		slot1Label.dropShadow = true;
		slot1Label.dropShadowOffset = 1;
		panel.addComponent(slot1Label);

		slot2Label = new UILabel(new Vector2i(slot1Label.position.x + 70, slot1Label.position.y), "|");
		slot2Label.setColor(0xffa0a0a0);
		slot2Label.setFont(new Font("Verdana", Font.PLAIN, 10));
		slot2Label.dropShadow = true;
		slot2Label.dropShadowOffset = 1;
		panel.addComponent(slot2Label);

		slot3Label = new UILabel(new Vector2i(slot2Label.position.x + 70, slot2Label.position.y), "|");
		slot3Label.setColor(0xffa0a0a0);
		slot3Label.setFont(new Font("Verdana", Font.PLAIN, 10));
		slot3Label.dropShadow = true;
		slot3Label.dropShadowOffset = 1;
		panel.addComponent(slot3Label);

		slot4Label = new UILabel(new Vector2i(slot1Label.position.x, slot1Label.position.y + 15), "");
		slot4Label.setColor(0xffa0a0a0);
		slot4Label.setFont(new Font("Verdana", Font.PLAIN, 10));
		slot4Label.dropShadow = true;
		slot4Label.dropShadowOffset = 1;
		panel.addComponent(slot4Label);

		slot5Label = new UILabel(new Vector2i(slot4Label.position.x + 70, slot4Label.position.y), "|");
		slot5Label.setColor(0xffa0a0a0);
		slot5Label.setFont(new Font("Verdana", Font.PLAIN, 10));
		slot5Label.dropShadow = true;
		slot5Label.dropShadowOffset = 1;
		panel.addComponent(slot5Label);

		slot6Label = new UILabel(new Vector2i(slot5Label.position.x + 70, slot5Label.position.y), "|");
		slot6Label.setColor(0xffa0a0a0);
		slot6Label.setFont(new Font("Verdana", Font.PLAIN, 10));
		slot6Label.dropShadow = true;
		slot6Label.dropShadowOffset = 1;
		panel.addComponent(slot6Label);

		slot7Label = new UILabel(new Vector2i(slot4Label.position.x, slot4Label.position.y + 15), "");
		slot7Label.setColor(0xffa0a0a0);
		slot7Label.setFont(new Font("Verdana", Font.PLAIN, 10));
		slot7Label.dropShadow = true;
		slot7Label.dropShadowOffset = 1;
		panel.addComponent(slot7Label);

		slot8Label = new UILabel(new Vector2i(slot7Label.position.x + 70, slot7Label.position.y), "|");
		slot8Label.setColor(0xffa0a0a0);
		slot8Label.setFont(new Font("Verdana", Font.PLAIN, 10));
		slot8Label.dropShadow = true;
		slot8Label.dropShadowOffset = 1;
		panel.addComponent(slot8Label);

		slot9Label = new UILabel(new Vector2i(slot8Label.position.x + 70, slot8Label.position.y), "|");

		slot9Label.setColor(0xffa0a0a0);
		slot9Label.setFont(new Font("Verdana", Font.PLAIN, 10));
		slot9Label.dropShadow = true;
		slot9Label.dropShadowOffset = 1;
		panel.addComponent(slot9Label);

		helpLabel = new UILabel(new Vector2i(7, Game.getWindowHeight() - 24), "");
		helpLabel.setColor(0xffa0a0a0);
		helpLabel.setFont(new Font("Verdana", Font.PLAIN, 15));
		helpLabel.dropShadow = true;
		helpLabel.dropShadowOffset = 1;
		panel.addComponent(helpLabel);

		help1Label = new UILabel(new Vector2i(helpLabel.position.x, helpLabel.position.y + 15), "");
		help1Label.setColor(0xffa0a0a0);
		help1Label.setFont(new Font("Verdana", Font.PLAIN, 15));
		help1Label.dropShadow = true;
		help1Label.dropShadowOffset = 1;
		panel.addComponent(help1Label);

		//player default attributes
		maxHealth = 100;
		health = maxHealth;
		maxMana = 100;
		mana = maxMana;
		xp = 0;
		lives = 10;
		rank = 1;
		armor = 0.0;
		protectSpell = 0.0;

		uiButtonOptions = new UIButton(new Vector2i(144, 178), new Vector2i(75, 24), new UIActionListener() {
			public void performAction() {
				JFrame frame = new JFrame("Controls");

				JPanel jPanel = new JPanel();
				jPanel.setLayout(new FlowLayout());

				JLabel jLabel = new JLabel("<html>> W - Move up<br>> A - Move left<br>> S - Move down<br>> D - Move right<br>> Left click - Shoot</html>");

				jPanel.add(jLabel);

				frame.add(jPanel);
				frame.setResizable(false);
				frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				frame.pack();
				frame.setLocationRelativeTo(null);
				frame.setVisible(true);
			}
		});
		uiButtonOptions.setButtonListener(new UIButtonListener() {
			public void buttonPressed(UIButton button) {
				super.buttonPressed(button);
				button.performAction();
				button.ignoreNextPress();
			}
		});
		uiButtonOptions.setText("Controls");
		uiButtonOptions.setFont(new Font("Verdana", Font.BOLD, 15));
		uiButtonOptions.setDropShadow(true, 1);
		uiButtonOptions.dropShadow = true;
		panel.addComponent(uiButtonOptions);

		/*to use changeBrightness method in ImageUtils class type:
		BufferedImage imageHover = ImageUtils.changeBrightness(image, amount);
		positive amount is brighter
		negative amount is darker
		*/

		try {
			URL url = this.getClass().getResource("/ui/home.png");
			image = ImageIO.read(url);
			System.out.println(image.getType());
		} catch (IOException e) {
			e.printStackTrace();
		}

		uiButtonImageTest = new UIButton(new Vector2i(206, 468), image, new UIActionListener() {
			public void performAction() {
				System.exit(0);
			}
		});
		uiButtonImageTest.setButtonListener(new UIButtonListener() {
			public void mouseEnteredButtonBounds(UIButton button) {
				button.setImage(ImageUtils.changeBrightness(image, 50));
			}

			public void mouseExitedButtonBounds(UIButton button) {
				button.setImage(image);
			}

			public void buttonPressed(UIButton button) {
				button.setImage(ImageUtils.changeBrightness(image, 75));
			}

			public void buttonReleased(UIButton button) {
				button.setImage(image);
			}
		});
		uiButtonImageTest.dropShadow = true;
		panel.addComponent(uiButtonImageTest);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void tick() {//public void update()
		time++;
		xa = 0;
		ya = 0;
		if (walking) {
			if (time % 20 == 0) {
				Random random = new Random();
				int randomInt = random.nextInt(3) + 1;
				if (randomInt == 1) {
					Sound.SoundEffect.FOOTSTEP_1.volume = Sound.SoundEffect.Volume.MEDIUM;
					Sound.SoundEffect.FOOTSTEP_1.play();
				} else if (randomInt == 2) {
					Sound.SoundEffect.FOOTSTEP_2.volume = Sound.SoundEffect.Volume.MEDIUM;
					Sound.SoundEffect.FOOTSTEP_2.play();
				} else if (randomInt == 3) {
					Sound.SoundEffect.FOOTSTEP_3.volume = Sound.SoundEffect.Volume.MEDIUM;
					Sound.SoundEffect.FOOTSTEP_3.play();
				}
			}
			animSprite.tick();
		} else animSprite.setFrame(0);
		if (firerate > 0) firerate--;
		double speed = 1.5;


		int hbSpriteWidth = hitbox.sprite.getWidth();
		int hbSpriteHeight = hitbox.sprite.getHeight();
		Item closest = level.getClosestItem(this, (int) x, (int) y, hbSpriteWidth, hbSpriteHeight);

		if (closest != null) {
			if (closest instanceof ResourceItem) {
				ResourceItem closestItem = (ResourceItem) closest;
				if (itemCollision(closestItem.hitbox, this.hitbox)) {
					inventory.add(closest);
					closest.remove();
					Sound.SoundEffect.COLLECT_ITEM_POP.play();
				}
			} else if (closest instanceof Weapon && !inventory.contains(closest) && !inventory.containsType(Item.ItemType.WEAPON)) {
				Weapon closestItem = (Weapon) closest;
				if (closestItem instanceof RangedWeapon) {
					RangedWeapon closestWeapon = (RangedWeapon) closestItem;
					if (itemCollision(closestWeapon.hitbox, this.hitbox)) {
						for (int i = 0; i < inventory.size(); i++) {
							inventory.remove(inventory.get(i));
						}
						inventory.add(closest);
						weapon = (RangedWeapon) closest;
						closest.remove();
						Sound.SoundEffect.COLLECT_ITEM_POP.play();
					}
				}
			} else if (closest instanceof Armor) {
				Armor closestItem = (Armor) closest;
				if (itemCollision(closestItem.hitbox, this.hitbox)) {
					inventory.add(closest);
					closest.remove();
					Sound.SoundEffect.COLLECT_ITEM_POP.play();
				}
			}
		}

		if (inventory.size() > 0) {
			for (int i = 0; i < inventory.size() && i < 9; i++) {
				if (i == 0) {
					slot1Label.setText(inventory.get(i).get(0).getName() + ": " + inventory.get(i).size());
				}

				if (i == 1) {
					slot2Label.setText("| " + inventory.get(i).get(0).getName() + ": " + inventory.get(i).size());
				}

				if (i == 2) {
					slot3Label.setText("| " + inventory.get(i).get(0).getName() + ": " + inventory.get(i).size());
				}

				if (i == 3) {
					slot4Label.setText(inventory.get(i).get(0).getName() + ": " + inventory.get(i).size());
				}

				if (i == 4) {
					slot5Label.setText("| " + inventory.get(i).get(0).getName() + ": " + inventory.get(i).size());
				}

				if (i == 5) {
					slot6Label.setText("| " + inventory.get(i).get(0).getName() + ": " + inventory.get(i).size());
				}

				if (i == 6) {
					slot7Label.setText(inventory.get(i).get(0).getName() + ": " + inventory.get(i).size());
				}

				if (i == 7) {
					slot8Label.setText("| " + inventory.get(i).get(0).getName() + ": " + inventory.get(i).size());
				}

				if (i == 8) {
					slot9Label.setText("| " + inventory.get(i).get(0).getName() + " " + inventory.get(i).size());
				}
			}
		}

		if (input.UP) {
			animSprite = up;
			ya -= speed;
		} else if (input.DOWN) {
			animSprite = down;
			ya += speed;
		}
		if (input.LEFT) {
			animSprite = left;
			xa -= speed;
		} else if (input.RIGHT) {
			animSprite = right;
			xa += speed;
		}
		if (xa != 0 || ya != 0) {
			move(xa, rightXOffset, leftXOffset, ya, topYOffset, bottomYOffset);
			walking = true;
		} else walking = false;

		clear();
		tickShooting();
		//getStatusEffects();

		if (health <= 0) {
			health = 100;
		}

		if (health < maxHealth) {
			health += .1;
		}
		if (health >= maxHealth - .25 && health <= maxHealth) {
			health = maxHealth;
		}

		if (mana < maxMana) {
			mana += .25;
		}

		Experience.init(level);

		if (xp >= Experience.getXPToNextLevel()) {
			xp -= Experience.getXPToNextLevel();
			rank++;
			maxHealth = Experience.calculateHealth();
			maxMana = Experience.calculateMana();
			armor = Experience.calculateArmor();
		}


		lvlRankLabel.setText("" + rank);
		hpCounterLabel.setText((int) health + "/" + (int) maxHealth);
		mpCounterLabel.setText((int) mana + "/" + (int) maxMana);
		xpCounterLabel.setText(xp + "/" + (int) Experience.getXPToNextLevel());
		armorStatLabel.setText("Armor: " + armor);

		uiHealthBar.setProgress(health / maxHealth);
		uiManaBar.setProgress(mana / maxMana);
		uiExperienceBar.setProgress(xp / Experience.getXPToNextLevel());
	}

	public void playerDamaged(double damage) {
		// can have a multiplier here to reduce health damage due to spells or armor
		if (armor > 0 & protectSpell > 0) {
			health -= (damage - (damage / armor) - (damage / protectSpell));
		} else health -= damage;

		if (isDead()) {
			if (lives > 0) {
				lives -= 1;
				Sound.SoundEffect.PLAYER_DEAD.play();
				Sound.SoundEffect.LIFE_LOST.play();
			} else level.gameOver();
		}
	}

	public boolean isDead() {
		return (health <= 0);
	}

	private void clear() {
		for (int i = 0; i < level.getProjectiles().size(); i++) {
			Projectile p = level.getProjectiles().get(i);
			if (p.isRemoved()) {
				level.getProjectiles().remove(i);
			}
		}
	}

	private void tickShooting() {
		if (Mouse.getX() > 660)
			return;

		if (Mouse.getButton() == 1 && firerate <= 0) {
			double dx = Mouse.getX() - Game.getWindowWidth() / 2;
			double dy = Mouse.getY() - Game.getWindowHeight() / 2;
			double dir = Math.atan2(dy, dx);
			if (mana - FireMageProjectile.cost >= 0) {
				if (inventory.contains(starterFireStaff) || inventory.contains(commonFireStaff)) {
					shoot(x, y, dir, this);
					mana -= FireMageProjectile.cost;
				}
			}

			firerate = FireMageProjectile.FIRE_RATE;
		}
	}

	public void render(Screen screen) {
		sprite = animSprite.getSprite();
		screen.renderMob((int) (x - 16), (int) (y - 16), sprite);
		hitbox.render((int) (x - 11), (int) (y - 16), screen);
	}
}