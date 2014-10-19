package com.digiturtle.ui;

import static org.lwjgl.opengl.ARBBufferObject.*;
import static org.lwjgl.opengl.ARBVertexBufferObject.*;
import static org.lwjgl.opengl.GL11.*;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;

public class StaticVBO {
	
	protected FloatBuffer vertexBuffer;
	protected FloatBuffer textureBuffer;
	protected int vertexHandle;
	protected int textureHandle;
	private int textureID, vertices;
	private int form = GL_QUADS;
	
	public StaticVBO(int vertices, int textureID) {
		this.vertices = vertices;
		vertexBuffer = BufferUtils.createFloatBuffer(vertices * 3);
		textureBuffer = BufferUtils.createFloatBuffer(vertices * 2);
		IntBuffer intBuffer = BufferUtils.createIntBuffer(2);
		glGenBuffersARB(intBuffer);
		vertexHandle = intBuffer.get(0);
		textureHandle = intBuffer.get(1);
		intBuffer.put(0, vertexHandle);
		intBuffer.put(1, textureHandle);
		this.textureID = textureID;
	}
	public StaticVBO(int vertices, int textureID, int form) {
		this(vertices, textureID);
		this.form = form;
	}
	
	public void uploadVertices(ComponentRegion region) {
		Vertex v1 = Vertex.get(region.x, region.y);
		Vertex v2 = Vertex.get(region.x + region.width, region.y);
		Vertex v3 = Vertex.get(region.x + region.width, region.y + region.height);
		Vertex v4 = Vertex.get(region.x, region.y + region.height);
		uploadVertices(v1, v2, v3, v4);
	}
	public void uploadVertices(Vertex... vertices) {
		vertexBuffer.clear();
		// Upload all the vertex data
		for (Vertex vertex : vertices) {
			vertexBuffer.put(vertex.x);
			vertexBuffer.put(vertex.y);
			vertexBuffer.put(vertex.z);
		}
		vertexBuffer.flip();
		// Bind this data to the VBO
		glEnableClientState(GL_VERTEX_ARRAY);
		glBindBufferARB(GL_ARRAY_BUFFER_ARB, vertexHandle);
	    glBufferDataARB(GL_ARRAY_BUFFER_ARB, vertexBuffer, GL_STATIC_DRAW_ARB);
	    glBindBufferARB(GL_ARRAY_BUFFER_ARB, 0);
		glDisableClientState(GL_VERTEX_ARRAY);
	}
	
	public void uploadTextures(float x1, float y1, float x2, float y2) {
		uploadTextures(new TexCoord(x1, y1), new TexCoord(x2, y1), new TexCoord(x2, y2), new TexCoord(x1, y2));
	}
	public void uploadTextures(TexCoord... texcoords) {
		textureBuffer.clear();
		// Upload all the color data
		for (TexCoord texCoord : texcoords) {
			textureBuffer.put(texCoord.s);
			textureBuffer.put(texCoord.t);
		}
		textureBuffer.flip();
		// Bind this data to the VBO
		glEnableClientState(GL_TEXTURE_COORD_ARRAY);
		glBindBufferARB(GL_ARRAY_BUFFER_ARB, textureHandle);
	    glBufferDataARB(GL_ARRAY_BUFFER_ARB, textureBuffer, GL_STATIC_DRAW_ARB);
	    glBindBufferARB(GL_ARRAY_BUFFER_ARB, 0);
		glDisableClientState(GL_TEXTURE_COORD_ARRAY);
	}
	
	public void speedRender(float x, float y) {
		glPushMatrix();
		glTranslatef(x, y, 0);
		
		speedRender();
		
		glPopMatrix();
	}
	public void render(float x, float y) {
		glPushMatrix();
		glTranslatef(x, y, 0);
		
		render();
		
		glPopMatrix();
	}
	
	public void speedRender() {
		glEnableClientState(GL_VERTEX_ARRAY);
		glEnableClientState(GL_TEXTURE_COORD_ARRAY);

		glBindBufferARB(GL_ARRAY_BUFFER_ARB, vertexHandle);
		glVertexPointer(3, GL_FLOAT, /* stride */3 << 2, 0L);

		glBindBufferARB(GL_ARRAY_BUFFER_ARB, textureHandle);
		glTexCoordPointer(2, GL_FLOAT, /* stride */2 << 2, 0L);

		glDrawArrays(form, 0, vertices /* elements */);

		glBindBufferARB(GL_ARRAY_BUFFER_ARB, 0);

		glDisableClientState(GL_TEXTURE_COORD_ARRAY);
		glDisableClientState(GL_VERTEX_ARRAY);
	}
	
	public void render() {
		glEnable(GL_TEXTURE_2D);
		glBindTexture(GL_TEXTURE_2D, textureID);
		
		glEnableClientState(GL_VERTEX_ARRAY);
		glEnableClientState(GL_TEXTURE_COORD_ARRAY);

		glBindBufferARB(GL_ARRAY_BUFFER_ARB, vertexHandle);
		glVertexPointer(3, GL_FLOAT, /* stride */3 << 2, 0L);

		glBindBufferARB(GL_ARRAY_BUFFER_ARB, textureHandle);
		glTexCoordPointer(2, GL_FLOAT, /* stride */2 << 2, 0L);

		glDrawArrays(form, 0, vertices /* elements */);

		glBindBufferARB(GL_ARRAY_BUFFER_ARB, 0);

		glDisableClientState(GL_TEXTURE_COORD_ARRAY);
		glDisableClientState(GL_VERTEX_ARRAY);
		
		glDisable(GL_TEXTURE_2D);
	}
	
	public static class Vertex {
		
		public float x, y, z;
		public Vertex(float x, float y) {
			this(x, y, 0);
		}
		public Vertex(float x, float y, float z) {
			this.x = x;
			this.y = y;
			this.z = z;
		}
		
		public static Vertex get(float x, float y) {
			return new Vertex(x, y);
		}
		
	}
	
	public static class TexCoord {
		
		public float s, t;
		public TexCoord(float s, float t) {
			this.s = s;
			this.t = t;
		}
		
	}

	public static StaticVBO getVBO(Texture texture, ComponentRegion region, float tx, float ty, float tx2, float ty2) {
		StaticVBO vbo = new StaticVBO(4, texture.getID());
		vbo.uploadVertices(region);
		vbo.uploadTextures(tx, ty, tx2, ty2);
		return vbo;
	}

}
