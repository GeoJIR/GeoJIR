package com.geojir.adapter;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.widget.SimpleAdapter;

public class ModifiableSimpleAdapter extends SimpleAdapter
{

	public ModifiableSimpleAdapter(Context context,
			List<? extends Map<String, ?>> data, int resource, String[] from,
			int[] to)
	{
		super(context, data, resource, from, to);
	}
	
}
