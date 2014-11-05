package com.geojir;

import com.geojir.ListMediaContract.MediasDb;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.SimpleCursorAdapter.ViewBinder;
import butterknife.ButterKnife;
import butterknife.InjectView;

public class ListMediaActivity extends ParentMenuActivity
{
	@InjectView(R.id.listViewMedias)
	protected ListView vue;
	protected SimpleCursorAdapter cursorAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list_media);
		ButterKnife.inject(this);
		
		// database instantiate
		ListMediaDb listeMedia = new ListMediaDb(getApplicationContext());
			Observable.create(listeMedia)
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(new Action1<Cursor>()
				{
					@Override
					public void call(Cursor cursor)
					{
						createAdapter(cursor);
						displayList();
					}
				});
			
			listeMedia.getCursorMedias();
	}
	
	protected void createAdapter(Cursor cursor)
	{
		cursorAdapter = new SimpleCursorAdapter(this,
				R.layout.list_item,
				cursor,
				new String[] { MediasDb.FILE_NAME_COLUMN, MediasDb.REMARK_COLUMN },
				new int[] {R.id.pathFileName, R.id.remark}
				, 0
		);
		
		cursorAdapter.setViewBinder(new ViewBinder()
		{
			@Override
			public boolean setViewValue(View view, Cursor cursor,
					int columnIndex)
			{
				if (view.getClass().isInstance(ImageView.class))
				{
					ImageView imageView = (ImageView) view;
					imageView.setImageURI(Uri.parse(cursor.getString(columnIndex)));
					return true;
				}
				return false;
			}
			
		});
	}

	protected void displayList()
	{
		// Clear old items
		vue.setAdapter(null);
		// Display new item list
		vue.setAdapter(cursorAdapter);
	}
}
