package com.geojir;

import android.provider.BaseColumns;

public class FollowerContract
{

	// To prevent someone from accidentally instantiating the
	// contract class, give it an empty and/or private constructor.
	private FollowerContract()
	{
	}

	/* Inner class that defines the table contents */
	public static abstract class FollowDb implements BaseColumns
	{
		public static final String FOLLOWER_TABLE_NAME = "followers";
		public static final String FOLLOWED_TABLE_NAME = "followeds";

		public static final String FOLLOWER_IDENTIFIER = "identifiant_follower";
		public static final String FOLLOWED_IDENTIFIER = "identifiant_followed";
	}

}
