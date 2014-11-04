package com.geojir;

import java.util.ArrayList;
import java.util.Map;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import android.os.Bundle;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

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
		values = new ArrayList<Map<String, String>>();
		
		Observable.create(listeMedia)
			.map(new Func1<Map<String, String>, Map<String, String>>()
			{
				@Override
				public Map<String, String> call(Map<String, String> item)
				{
					values.add(item);
					return item;
				}
			})
			.subscribeOn(Schedulers.io())
			.observeOn(AndroidSchedulers.mainThread())
			.subscribe(new Action1<Map<String, String>>()
			{
				@Override
				public void call(Map<String, String> item)
				{
					displayList();
				}
			});
	}

	protected void displayList()
	{
		ListAdapter adapterSimple = new SimpleAdapter(this, values,
				R.layout.list_item, new String[] { "pathFileName", "remark" },
				new int[] {R.id.pathFileName, R.id.remark}
				// new int[] { R.id.icon, R.id.remark }
		);
		
		// Get ListView object from xml
		vue = (ListView) findViewById(R.id.listViewMedias);
		// Clear old items
		vue.setAdapter(null);
		// Display new item list
		vue.setAdapter(adapterSimple);
	}
}
