package com.riphtix.vgmad.gfx;

import com.riphtix.vgmad.Game;
import com.riphtix.vgmad.util.AffineTransform;

import javax.swing.*;
import java.awt.*;
import java.awt.image.ImageConsumer;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;

public class Sprite {

	public final int SIZE;
	private int x;
	private int y;
	private int width;
	private int height;
	public int[] pixels;
	protected SpriteSheet sheet;

	//Tiles
	public static Sprite grassSprite = new Sprite(16, 0, 0, SpriteSheet.tiles);
	public static Sprite dirtSprite = new Sprite(16, 1, 0, SpriteSheet.tiles);
	public static Sprite stoneWallSprite = new Sprite(16, 2, 0, SpriteSheet.tiles);
	public static Sprite stoneBrickSprite = new Sprite(16, 3, 0, SpriteSheet.tiles);
	public static Sprite woodenPlankSprite = new Sprite(16, 4, 0, SpriteSheet.tiles);
	public static Sprite lockedGateSprite = new Sprite(16, 5, 0, SpriteSheet.tiles);
	public static Sprite unlockedGateSprite = new Sprite(16, 6, 0, SpriteSheet.tiles);
	public static Sprite ironBarsSprite = new Sprite(16, 7, 0, SpriteSheet.tiles);
	public static Sprite volcanicFloorSprite = new Sprite(16, 8, 0, SpriteSheet.tiles);
	public static Sprite volcanicBrickWallSprite = new Sprite(16, 9, 0, SpriteSheet.tiles);
	public static Sprite lavaSprite = new Sprite(16, 10, 0, SpriteSheet.tiles);
	public static Sprite waterSprite = new Sprite(16, 11, 0, SpriteSheet.tiles);
	public static Sprite stoneFloorSprite = new Sprite(16, 12, 0, SpriteSheet.tiles);
	public static Sprite voidSprite = new Sprite(16, 0x000000);

	//Items
	//Objects
	public static Sprite ironSprite = new Sprite(16, 0, 0, SpriteSheet.items);
	public static Sprite keySprite = new Sprite(16, 1, 0, SpriteSheet.items);
	public static Sprite dragonHeart = new Sprite(16, 2, 0, SpriteSheet.items);

	//Weapons
	public static Sprite fireStaffSprite = new Sprite(16, 0, 0, SpriteSheet.weapons);

	//Armor
	public static Sprite chestPlateSprite = new Sprite(16, 0, 0, SpriteSheet.armor);
	public static Sprite leggingsSprite = new Sprite(16, 1, 0, SpriteSheet.armor);
	public static Sprite helmetSprite = new Sprite(16, 2, 0, SpriteSheet.armor);

	//Entities
	//Mobs
	public static Sprite maleElfDefault = new Sprite(32, 0, 0, SpriteSheet.maleElf_down);
	public static Sprite femaleElfDefault = new Sprite(32, 0, 0, SpriteSheet.femaleElf_down);
	public static Sprite femaleFighterDefault = new Sprite(32, 0, 0, SpriteSheet.femaleFighter_down);
	public static Sprite ghostDefault = new Sprite(32, 0, 0, SpriteSheet.ghost_down);
	public static Sprite sorceressDefault = new Sprite(32, 0, 0, SpriteSheet.sorceress_down);

	//Projectile
	public static Sprite fireBoltSprite = new Sprite(16, 0, 0, SpriteSheet.projectiles);
	public static Sprite dragonBlastSprite = new Sprite(32, 0, 1, SpriteSheet.projectiles);
	public static Sprite arrowSprite = new Sprite(16, 1, 0, SpriteSheet.projectiles);
	public static Sprite cannonBallSprite = new Sprite(16, 2, 0, SpriteSheet.projectiles);

	//Particle
	public static Sprite defaultParticle = new Sprite(3, 0xffaaaaaa);

	//Health Bars
	public static Sprite healthBar0 = new Sprite(5, 1, 0xff00c700);
	public static Sprite healthBar25 = new Sprite(5, 1, 0xff00c700);
	public static Sprite healthBar50 = new Sprite(5, 1, 0xff00c700);
	public static Sprite healthBar75 = new Sprite(5, 1, 0xff00c700);

	//Debug
	public static Sprite aimBox = new Sprite(16, 0, 0, SpriteSheet.debug);

	//Hitbox
	public static Sprite hitbox21x32 = new Sprite(SpriteSheet.mobHitbox, 21, 32);
	public static Sprite hitbox16x16 = new Sprite(SpriteSheet.projectileHitbox,16, 16);
	public static Sprite hitbox16x8 = new Sprite(SpriteSheet.projectileHitbox, 16, 8);
	public static Sprite hitbox68x61 = new Sprite(SpriteSheet.bossHitbox, 68, 61);


	public Sprite(SpriteSheet sheet, int width, int height) {
		SIZE = (width == height) ? width : -1; //trick
		this.width = width;
		this.height = height;
		pixels = new int[width * height];
		this.x = x * width;
		this.y = y * height;
		this.sheet = sheet;
		load();
	}

	public Sprite(int size, int x, int y, SpriteSheet sheet) {
		SIZE = size;
		this.width = size;
		this.height = size;
		pixels = new int[SIZE * SIZE];
		this.x = x * size;
		this.y = y * size;
		this.sheet = sheet;
		load();
	}

	public Sprite(int width, int height, int color) {
		SIZE = -1;
		this.width = width;
		this.height = height;
		pixels = new int[width * height];
		setColor(color);
	}

	public Sprite(int size, int color) {
		SIZE = size;
		this.width = size;
		this.height = size;
		pixels = new int[SIZE * SIZE];
		setColor(color);
	}

	public Sprite(int[] pixels, int width, int height) {
		SIZE = (width == height) ? width : -1;
		this.width = width;
		this.height = height;
		this.pixels = new int[pixels.length];
		for (int i = 0; i < pixels.length; i++) {
			this.pixels[i] = pixels[i];
		}
	}

	public static Sprite rotate(Sprite sprite, double angle) {
		return new Sprite(rotate(sprite.pixels, sprite.width, sprite.height, angle), sprite.width, sprite.height);
	}

	private static int[] rotate(int[] pixels, int width, int height, double angle) {
		int[] result = new int[width * height];

		double nxx = rotateX(-angle, 1.0, 0.0);
		double nxy = rotateY(-angle, 1.0, 0.0);
		double nyx = rotateX(-angle, 0.0, 1.0);
		double nyy = rotateY(-angle, 0.0, 1.0);

		double x0 = rotateX(-angle, -width / 2.0, -height / 2.0) + width / 2.0;
		double y0 = rotateY(-angle, -width / 2.0, -height / 2.0) + height / 2.0;

		for (int y = 0; y < height; y++) {
			double x1 = x0;
			double y1 = y0;
			for (int x = 0; x < width; x++) {
				int xx = (int) x1;
				int yy = (int) y1;
				int col = 0;
				if (xx < 0 || xx >= width || yy < 0 || yy >= height) col = 0xffff00ff;
				else col = pixels[xx + yy * width];
				result[x + y * width] = col;
				x1 += nxx;
				y1 += nxy;
			}
			x0 += nyx;
			y0 += nyy;
		}

		return result;
	}

	private static double rotateX(double angle, double x, double y) {
		double cos = Math.cos(angle);
		double sin = Math.sin(angle);
		return x * cos + y * -sin;
	}

	private static double rotateY(double angle, double x, double y) {
		double cos = Math.cos(angle);
		double sin = Math.sin(angle);
		return x * sin + y * cos;
	}

	public static Sprite[] split(SpriteSheet sheet) {
		int amount = (sheet.getWidth() * sheet.getHeight()) / (sheet.SPRITE_WIDTH * sheet.SPRITE_HEIGHT);
		Sprite[] sprites = new Sprite[amount];
		int current = 0;
		int[] pixels = new int[sheet.SPRITE_WIDTH * sheet.SPRITE_HEIGHT];

		for (int yp = 0; yp < sheet.getHeight() / sheet.SPRITE_HEIGHT; yp++) {
			for (int xp = 0; xp < sheet.getWidth() / sheet.SPRITE_WIDTH; xp++) {

				for (int y = 0; y < sheet.SPRITE_HEIGHT; y++) {
					for (int x = 0; x < sheet.SPRITE_WIDTH; x++) {
						int xo = x + xp * sheet.SPRITE_WIDTH;
						int yo = y + yp * sheet.SPRITE_HEIGHT;
						pixels[x + y * sheet.SPRITE_WIDTH] = sheet.getPixels()[xo + yo * sheet.getWidth()];
					}
				}

				sprites[current++] = new Sprite(pixels, sheet.SPRITE_WIDTH, sheet.SPRITE_HEIGHT);
			}
		}


		return sprites;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public void setColor(int color) {
		for (int i = 0; i < width * height; i++) {
			pixels[i] = color;
		}
	}

	private void load() {
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				pixels[x + y * width] = sheet.pixels[(x + this.x) + (y + this.y) * sheet.getWidth()];
			}
		}
	}
}
