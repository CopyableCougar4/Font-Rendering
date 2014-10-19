package com.digiturtle.ui;

import static org.lwjgl.opengl.GL11.*;

public class DisplayList {
	
	private int identifier = -1;
	public float scalex = 1, scaley = 1;
	
	public DisplayList(Renderable renderable) {
		identifier = glGenLists(1);
		glNewList(identifier, GL_COMPILE);
		renderable.render(); // Do all GL commands to be cached
		glEndList();
	}
	public DisplayList(final Runnable runnable) {
		this(new Renderable() {
			public void render() {
				runnable.run();
			}
		});
	}
	
	public void render() {
		glCallList(identifier);
	}
	public void renderAt(float x, float y) {
		glTranslatef(x, y, 0);
		glScalef(scalex, scaley, 0);
		render();
		glScalef(1 / scalex, 1 / scaley, 0);
		glTranslatef(-x, -y, 0);
	}
	public void renderAt(float x, float y, float z) {
	    glEnable(GL_DEPTH_TEST);
		glTranslatef(x, y, z);
		glScalef(scalex, scaley, 0);
		render();
		glScalef(1 / scalex, 1 / scaley, 0);
		glTranslatef(-x, -y, -z);
	    glDisable(GL_DEPTH_TEST);
	}
	
	public void dispose() {
		glDeleteLists(identifier, 1);
	}

}
