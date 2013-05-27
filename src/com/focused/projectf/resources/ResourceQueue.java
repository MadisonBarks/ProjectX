package com.focused.projectf.resources;

import com.focused.projectf.resources.loaders.ResourceLoader;

public class ResourceQueue<T extends Resource> {
	public String file;
	public T backReference;
	public Object fileDat;
	public ResourceLoader<T> loader;
}
