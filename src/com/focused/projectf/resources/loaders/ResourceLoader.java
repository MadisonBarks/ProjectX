package com.focused.projectf.resources.loaders;

import java.lang.reflect.TypeVariable;

import com.focused.projectf.resources.Resource;

/** 
 * Describes the mechanisms to load resources into the game.
 */
public abstract class ResourceLoader<T extends Resource> {

	/**
	 * All work that can be done on a non-GL thread should be completed here
	 */
	public abstract Object loadFromFile(String resource);
	/**
	 * All work involves OpenGL commands should be done here
	 */
	public abstract void pushToOpenGL(String resource, Object fileData, Resource placeHolder);
	/**
	 * Returns true if the specified file can be loaded using this resource loader.
	 * This if mostly a filetype check
	 */
	public abstract boolean canLoadFile(String resource);

	/**
	 * Returns a placeholder object 
	 */
	public abstract T instancePlaceHolder(String file);

	public TypeVariable<?> getLoadingType() { 
		return this.getClass().getTypeParameters()[0]; 
	}
}
