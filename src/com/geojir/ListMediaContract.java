package com.geojir;

import android.provider.BaseColumns;

public class ListMediaContract
{
	
	// To prevent someone from accidentally instantiating the
	// contract class, give it an empty and/or private constructor.
	private ListMediaContract()
	{
	}
	
	/* Inner class that defines the table contents */
	public static abstract class MediasDb implements BaseColumns
	{
		public static final String TABLE_NAME = "ListMedia";
		
		public static final String FILE_NAME_COLUMN = "Filename";
		public static final String REMARK_COLUMN = "Remark";
		public static final String FILTER_COLUMN = "Filter";
	}
	
}
