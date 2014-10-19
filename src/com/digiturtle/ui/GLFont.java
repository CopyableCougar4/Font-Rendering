package com.digiturtle.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import static org.lwjgl.opengl.GL11.*;

public class GLFont {
	
	private final Font FONT = null;
	public static HashMap<Float, Texture> textures = new HashMap<Float, Texture>();
	public static HashMap<Float, IntObject[]> objectMap = new HashMap<Float, IntObject[]>();
	public static HashMap<Float, BufferedImage> imageMap = new HashMap<Float, BufferedImage>();
	public static HashMap<Float, FontMetrics> metricsMap = new HashMap<Float, FontMetrics>();
	public static HashMap<Float, StaticVBO[]> vboMap = new HashMap<Float, StaticVBO[]>();
	
	private Font font;
	private float fontsize;
	private IntObject[] objects = new IntObject[256];
	private StaticVBO[] vbos = new StaticVBO[256];
	private FontMetrics metrics;
	private Texture fontTexture;
	private BufferedImage image;
	private int fontHeight = 0;
	
	public static class IntObject {
		public int x, y, width, height;
		public void debug() {
			System.out.println("IntObject[x=" + x + ",y=" + y + ",width=" + width + ",height=" + height + "]");
		}
	}
	
	public float getSize() {
		return fontsize;
	}
	
	public float getHeight(String input) {
		String[] check = input.split("\\n");
		return check.length * (getSize() + 5);
	}
	
	public GLFont(float fontsize, Font FONT) {
		this(FONT, fontsize);
	}
	public GLFont(Font _font, float fontsize) {
		this.fontsize = fontsize;
		font = _font.deriveFont(fontsize);
		if (textures.get(fontsize) != null) {
			fontTexture = new Texture(imageMap.get(fontsize), "");
			objects = objectMap.get(fontsize);
			metrics = metricsMap.get(fontsize);
			vbos = vboMap.get(fontsize);
		} else {
			createTexture();
			textures.put(fontsize, fontTexture);
			imageMap.put(fontsize, image);
			objectMap.put(fontsize, objects);
			metricsMap.put(fontsize, metrics);
			vboMap.put(fontsize, vbos);
		}
	}
	
	public float getSplitWidth(String input) {
		String[] lines = input.split("\\n");
		float maxwidth = 0;
		for (String line : lines) {
			maxwidth = Math.max(maxwidth, getWidth(line));
		}
		return maxwidth;
	}
	public float getWidth(String input) {
		if (input.contains("\\n")) {
			String[] lines = input.split("\\n");
			float maxwidth = 0;
			for (String line : lines) {
				maxwidth = Math.max(maxwidth, getWidth(line));
			}
			return maxwidth;
		} else {
			float width = 0;
			for (char letter : input.toCharArray()) {
				width += metrics.charWidth(letter);
			}
			return width;
		}
	}
	
	private float textureWidth = 512, textureHeight = 512;
	public void drawText(float x, float y, String text, Color color, float angleDegrees) {
		glPushMatrix();
		glEnable(GL_BLEND);
	    glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
	    glTranslatef(x, y, 0);
	    glRotatef(angleDegrees, 0, 0, 1);
		glColor4f(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, color.getAlpha() / 255.0f);
		fontTexture.bind();
		float drawX = 0;
		float drawY = 0;
		glBegin(GL_QUADS);
		String[] lines = text.split("\\n");
		for (String line : lines) {
			drawX = 0;
			for (char letter : line.toCharArray()) {
				IntObject intObject = objects[(int)letter];
				if (intObject == null) {
					continue;
				}
				float drawX2 = drawX + intObject.width;
				float drawY2 = drawY + intObject.height;
				float srcX = intObject.x; 
				float srcY = intObject.y; //
				float srcX2 = srcX + intObject.width;
				float srcY2 = srcY + intObject.height;
				float DrawWidth = drawX2 - drawX;
				float DrawHeight = drawY2 - drawY;
				float TextureSrcX = srcX / textureWidth; 
				float TextureSrcY = srcY / textureHeight; 
				float SrcWidth = srcX2 - srcX;
				float SrcHeight = srcY2 - srcY;
				float RenderWidth = (SrcWidth / textureWidth);
				float RenderHeight = (SrcHeight / textureHeight);
				// Draw the letter
				glTexCoord2f(TextureSrcX, TextureSrcY);
				glVertex2f(drawX, drawY);
				glTexCoord2f(TextureSrcX, TextureSrcY + RenderHeight);
				glVertex2f(drawX, drawY + DrawHeight);
				glTexCoord2f(TextureSrcX + RenderWidth, TextureSrcY + RenderHeight);
				glVertex2f(drawX + DrawWidth, drawY + DrawHeight);
				glTexCoord2f(TextureSrcX + RenderWidth, TextureSrcY);
				glVertex2f(drawX + DrawWidth, drawY);
				drawX += intObject.width;
			}
			drawY += getSize() + 5;
		}
		glEnd();
		glColor3f(1.0f, 1.0f, 1.0f);
		glPopMatrix();
	}
	public void drawText(float x, float y, String text, Color color) {
	    glEnable(GL_BLEND);
	    glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		glColor4f(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, color.getAlpha() / 255.0f);
		fontTexture.bind();
		float drawX = x;
		float drawY = y;
		String[] lines = text.split("\\n");
		for (String line : lines) {
			drawX = x;
			for (char letter : line.toCharArray()) {
				IntObject intObject = objects[(int) letter];
				if (intObject == null) {
					continue;
				}
				// Draw the letter
				StaticVBO vbo = vbos[(int) letter];
				vbo.render(drawX, drawY);
				drawX += intObject.width;
			}
			drawY += getSize() + 5;
		}
		glColor3f(1.0f, 1.0f, 1.0f);
	}
	
	public DisplayList drawCachedText(final float x, final float y, final String text, final Color color) {
		return new DisplayList(new Renderable() {
			public void render() {
				drawText(x, y, text, color);
			}
		});
	}
	
	public void debug() {
		fontTexture.bind();
		glBegin(GL_QUADS);
		glTexCoord2f(0, 0);
		glVertex2f(0, 0);
		glTexCoord2f(1, 0);
		glVertex2f(512, 0);
		glTexCoord2f(1, 1);
		glVertex2f(512, 512);
		glTexCoord2f(0, 1);
		glVertex2f(0, 512);
		glEnd();
	}
	
	public void createTexture() {
		image = new BufferedImage(512, 512, BufferedImage.TYPE_INT_ARGB);
		BufferedImage scratch = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics = scratch.createGraphics();
		// get the dimensions
		graphics.setFont(font);
		metrics = graphics.getFontMetrics();
		// prepare the graphics
		Graphics2D realGraphics = image.createGraphics();
		realGraphics.setBackground(new Color(255, 255, 255, 0));
		realGraphics.clearRect(0, 0, 512, 512);  
		realGraphics.setColor(Color.WHITE);
		realGraphics.setFont(font);
		realGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		// real work
		int rowHeight = 0;
		int positionX = 0;
		int positionY = 0;
		for (int index = 0; index < 256; index++) {
			char letter = (char) index;
			int charwidth = metrics.charWidth(letter);
			if (charwidth <= 0) {
				charwidth = 1;
			}
			int charheight = metrics.getHeight();
			if (charheight <= 0) {
				charheight = Math.round(fontsize);
			}
			IntObject intObject = new IntObject();
			intObject.width = charwidth;
			intObject.height = charheight;
			if (positionX + intObject.width >= 512) {
				positionX = 0;
				positionY += rowHeight + 10;
				rowHeight = 0;
			}
			intObject.x = positionX;
			intObject.y = positionY;
			if (intObject.height > fontHeight) {
				fontHeight = intObject.height;
			}
			if (intObject.height > rowHeight) {
				rowHeight = intObject.height;
			}
			// Log.print("Rendering char " + letter + " as " + String.valueOf(letter) + " @ " + positionX + ", " + positionY);
			realGraphics.drawString(String.valueOf(letter), positionX, positionY + metrics.getAscent());
			positionX += intObject.width;
			objects[index] = intObject;
		}
		fontTexture = new Texture(image, "");
		for (int index = 0; index < 256; index++) {
			IntObject intObject = objects[index];
			float srcX = intObject.x; 
			float srcY = intObject.y; //
			float srcX2 = srcX + intObject.width;
			float srcY2 = srcY + intObject.height;
			float TextureSrcX = srcX / textureWidth; 
			float TextureSrcY = srcY / textureHeight; 
			float SrcWidth = srcX2 - srcX;
			float SrcHeight = srcY2 - srcY;
			float RenderWidth = (SrcWidth / textureWidth);
			float RenderHeight = (SrcHeight / textureHeight);
			StaticVBO vbo = StaticVBO.getVBO(fontTexture, new ComponentRegion(0, 0, intObject.width, intObject.height), TextureSrcX, TextureSrcY, TextureSrcX + RenderWidth, TextureSrcY + RenderHeight);
			vbos[index] = vbo;
		}
	}

}
