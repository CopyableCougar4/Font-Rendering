package com.digiturtle.ui;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;

import javax.imageio.ImageIO;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL12;

import static org.lwjgl.opengl.GL11.*;

public class Texture {
	
	private static HashMap<String, Integer> texturesLoaded = new HashMap<String, Integer>();
	
	private int textureID;
	private int width;
	private int height;
	private BufferedImage image;
	public String filename;
	public ByteBuffer _bytebuffer;
	public IntBuffer _buffer;
	
	public static Texture loadTexture(String filename) {
		return loadTexture(filename, Texture.class.getResourceAsStream(filename));
	}
	public static Texture loadTexture(String filename, InputStream file) {
		if (file == null) {
			return new Texture();
		}
		try {
			BufferedImage bImage = ImageIO.read(file);
			return new Texture(bImage, filename);
		} catch (IOException e) {
			if (file != null) {
				
			}
			e.printStackTrace();
			return null;
		}
	}
	public static Texture loadTexture(BufferedImage bImage, String filename) {
		return new Texture(bImage, filename);
	}
	
	public Texture() {
		// null texture
		textureID = -1;
	}
	
	static int ID = 0;
	public Texture(BufferedImage image, String filename) {
		System.out.println(filename);
		this.filename = filename;
		if (texturesLoaded.get(filename) != null) {
			textureID = texturesLoaded.get(filename);
		} else {
			this.image = image;
			textureID = glGenTextures();
			while (texturesLoaded.values().contains(textureID)) {
				textureID = glGenTextures();
			}
			loadTexture(image);
			if (!filename.equalsIgnoreCase("")) {
				texturesLoaded.put(filename, textureID);
			}
		}
	}
	
	public void bind() {
		unbind();
		glEnable(GL_TEXTURE_2D);
		glBindTexture(GL_TEXTURE_2D, textureID);
	}
	
	public static void unbind() {
		glDisable(GL_TEXTURE_2D);
		glEnable(GL_TEXTURE_2D);
		glBindTexture(GL_TEXTURE_2D, 0);
	}
	
	public int getHeight() {
		return height;
	}
	
	public int getWidth() {
		return width;
	}
	
	public BufferedImage getImage() {
		return image;
	}
	
	public void loadTexture(BufferedImage image) {
		glEnable(GL_TEXTURE_2D);
		width = image.getWidth();
		height = image.getHeight();
		int[] pixels = new int[image.getWidth() * image.getHeight()];
		image.getRGB(0, 0, image.getWidth(), image.getHeight(), pixels, 0, image.getWidth());
		_buffer = IntBuffer.wrap(pixels);
		_bytebuffer = BufferUtils.createByteBuffer(image.getWidth() * image.getHeight() * 4); 	
		for (int y = 0; y < image.getHeight(); y++) {
			for (int x = 0; x < image.getWidth(); x++) {
				int pixel = pixels[y * image.getWidth() + x];
				_bytebuffer.put((byte) ((pixel >> 16) & 0xFF));
				_bytebuffer.put((byte) ((pixel >> 8) & 0xFF));
				_bytebuffer.put((byte) (pixel & 0xFF));
				_bytebuffer.put((byte) ((pixel >> 24) & 0xFF));
			}
		}
		_bytebuffer.flip(); //FOR THE LOVE OF GOD DO NOT FORGET THIS
		glBindTexture(GL_TEXTURE_2D, textureID); //Bind texture ID
		//Setup wrap mode
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
		//Setup texture scaling filtering
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		//Send texel data to OpenGL
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, image.getWidth(), image.getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, _bytebuffer);
		glBindTexture(GL_TEXTURE_2D, 0); // Clear the texture
	}
	
	public void reloadTexture(File file) {
		try {
			BufferedImage bImage = ImageIO.read(file);
			reloadTexture(bImage);
		} catch (IOException e) {
		}
	}
	public void reloadTexture(BufferedImage image) {
		glEnable(GL_TEXTURE_2D);
		int[] pixels = new int[image.getWidth() * image.getHeight()];
		image.getRGB(0, 0, image.getWidth(), image.getHeight(), pixels, 0, image.getWidth());
		_bytebuffer = BufferUtils.createByteBuffer(image.getWidth() * image.getHeight() * 4); 	
		for (int y = 0; y < image.getHeight(); y++) {
			for (int x = 0; x < image.getWidth(); x++) {
				int pixel = pixels[y * image.getWidth() + x];
				_bytebuffer.put((byte) ((pixel >> 16) & 0xFF));
				_bytebuffer.put((byte) ((pixel >> 8) & 0xFF));
				_bytebuffer.put((byte) (pixel & 0xFF));
				_bytebuffer.put((byte) ((pixel >> 24) & 0xFF));
			}
		}
		_bytebuffer.flip(); //FOR THE LOVE OF GOD DO NOT FORGET THIS
		glBindTexture(GL_TEXTURE_2D, textureID); //Bind texture ID
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, image.getWidth(), image.getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, _bytebuffer);
	//	glTexSubImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, image.getWidth(), image.getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
		glBindTexture(GL_TEXTURE_2D, 0); // Clear the texture
	}
	public int getID() {
		return textureID;
	}

}
