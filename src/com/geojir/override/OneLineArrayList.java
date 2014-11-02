package com.geojir.override;

import java.util.ArrayList;

// ArrayList with put function like add returning this
// Useful for create an ArrayList with successive put
public class OneLineArrayList<T> extends ArrayList<T>
{
	// serial Version needed
	private static final long serialVersionUID = 1L;
	
	
	public OneLineArrayList<T> put(T object)
	{
		this.add(object);
		return this;
	}
}
