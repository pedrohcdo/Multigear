package com.org.multigear.physics;

import com.org.multigear.general.utils.Vector2;

/**
 * 
 * Shape Object.
 * 
 * @author PedroH, RaphaelB
 *
 * Property Createlier.
 */
final public class Shape {
	
	// Private Variables
	final private Vector2[] mVertices;
	
	/*
	 * Construtor
	 */
	public Shape(final int size) {
		mVertices = new Vector2[size];
		for(int i=0; i<size; i++)
			mVertices[i] = new Vector2(0, 0);
	}
	
	/*
	 * Seta uma posição da forma
	 */
	final public void setVertice(final int index, final Vector2 vertice) {
		mVertices[index] = vertice;
	}
	
	/*
	 * Pega uma posição da forma
	 */
	final public Vector2 getVertice(final int index) {
		return mVertices[index];
	}
	
	/*
	 * Retorna o pacote de vertices
	 */
	final public Vector2[] getVertices() {
		return mVertices;
	}
	
	/*
	 * Retorna o tamanho da forma
	 */
	final public int getSize() {
		return mVertices.length;
	}
	
	/**
	 * Copy vertices to Shape, base on float[] vertices.
	 * The vertices have to be organized like this:
	 * 
	 * [Ax, Ay, Bx, By, ...
	 * 
	 * @param vertices Package of float[] vertices
	 * @return
	 */
	final public boolean copy(final float[] vertices) {
		final int size = vertices.length / 2;
		final int mod = vertices.length % 2;
		if(mod != 0 || size != getSize())
			return false;
		for(int i=0; i<size; i++) {
			mVertices[i].x = vertices[i * 2];
			mVertices[i].y = vertices[i * 2 + 1];
		}
		return true;
	}
	
	/**
	 * Copy vertices to Shape, base on Utils.Ref2D package.
	 * 
	 * @param vertices Package of Utils.Ref2D vertices
	 * @return
	 */
	final public boolean copy(final Vector2[] vertices) {
		if(vertices.length != getSize())
			return false;
		for(int i=0; i<vertices.length; i++) {
			mVertices[i] = vertices[i].clone();
		}
		return true;
	}
	
	/**
	 * Create a Shape base on float[] vertices.
	 * The vertices have to be organized like this:
	 * 
	 * [Ax, Ay, Bx, By, ...
	 * 
	 * @param vertices Package of float[] vertices
	 * @return
	 */
	final static public Shape createShape(final float[] vertices) {
		final int size = vertices.length / 2;
		final int mod = vertices.length % 2;
		if(mod != 0)
			return null;
		final Shape shape = new Shape(size);
		for(int i=0; i<size; i++) {
			shape.mVertices[i].x = vertices[i * 2];
			shape.mVertices[i].y = vertices[i * 2 + 1];
		}
		return shape;
	}
	
	/**
	 * Create a Shape base on Utils.Ref2D package.
	 * 
	 * @param vertices Package of Utils.Ref2D vertices
	 * @return
	 */
	final static public Shape createShape(final Vector2[] vertices) {
		final Shape shape = new Shape(vertices.length);
		for(int i=0; i<vertices.length; i++) {
			shape.mVertices[i] = vertices[i].clone();
		}
		return shape;
	}
}
