package com.geojir;

import java.util.ArrayList;
import java.util.Map;

import android.os.Bundle;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class ListMediaActivity extends ParentMenuActivity
{

	ArrayList<Map<String, String>> values;
	ListView vue;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list_media);

		// database instantiate
		ListMediaDb listeMedia = new ListMediaDb(getApplicationContext());

		// last X entries
		values = listeMedia.getAllMedias();

		// Get ListView object from xml
		vue = (ListView) findViewById(R.id.listViewMedias);

		ListAdapter adapterSimple = new SimpleAdapter(this, values,
				R.layout.list_item, new String[]
				{ "pathFileName", "remark" },
				// new int[] {R.id.pathFileName, R.id.remark}
				new int[]
				{ R.id.icon, R.id.remark });

		vue.setAdapter(adapterSimple);
	}
}
