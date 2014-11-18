package com.geojir;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.SimpleCursorAdapter.ViewBinder;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnItemClick;

import com.geojir.db.ListMediaContract.MediasDb;
import com.geojir.db.ListMediaDb;
import com.geojir.db.MediaContentProvider;
import com.geojir.view.CustomImageView;

public class ListMediaActivity extends ParentMenuActivity
{
	@InjectView(R.id.emptyListTextView)
	protected TextView emptyListTextView;
	@InjectView(R.id.listViewMedias)
	protected ListView listView;
	protected SimpleCursorAdapter cursorAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list_media);
		ButterKnife.inject(this);

		displayContentProvider();
	}

	@OnItemClick(R.id.listViewMedias)
	void onItemClick(int position)
	{
		// DON'T USE listView.getAtChild(position)
		// listView have only a part of child at a time
		View rowView = listView.getAdapter().getView(position, null, null);
		CustomImageView iconView = (CustomImageView) rowView
				.findViewById(R.id.imageIcon);
		iconView.playMedia();
	}
	
	// Create custom adapter
	protected void createAdapter(Cursor cursor)
	{
		// Display image and comment
		cursorAdapter = new SimpleCursorAdapter(this, R.layout.list_item,
				cursor, new String[] { MediasDb.FILE_NAME_COLUMN,
						MediasDb.REMARK_COLUMN, MediasDb.FILTER_COLUMN },
				new int[] { R.id.imageIcon, R.id.remark }, 0);

		updateChildrenVisibility();

		// Convert String to image for ImageView
		cursorAdapter.setViewBinder(new ViewBinder()
		{
			@Override
			public boolean setViewValue(View view, Cursor cursor,
					int columnIndex)
			{
				if (view instanceof CustomImageView)
				{
					CustomImageView imageView = (CustomImageView) view;

					// Path of media
					String path = cursor.getString(columnIndex);
					// display image depend on path and file existence
					imageView.setImagePath(path);

					int filterInt = cursor
							.getColumnIndex(MediasDb.FILTER_COLUMN);
					if (cursor.getInt(filterInt) == 1)
						imageView.blackAndWhiteMode(true);

					return true;
				}
				return false;
			}
		});
	}

	protected void updateChildrenVisibility()
	{
		if (cursorAdapter == null || cursorAdapter.isEmpty())
		{
			listView.setVisibility(View.INVISIBLE);
			emptyListTextView.setVisibility(View.VISIBLE);
		} else
		{
			listView.setVisibility(View.VISIBLE);
			emptyListTextView.setVisibility(View.INVISIBLE);
		}
	}

	protected void displayList()
	{
		// Clear old items
		listView.setAdapter(null);
		// Display new item list
		listView.setAdapter(cursorAdapter);
	}

	// CONTENT PROVIDER
	private void displayContentProvider()
	{
		String columns[] = new String[] { MediasDb._ID,
				MediasDb.FILE_NAME_COLUMN, MediasDb.REMARK_COLUMN,
				MediasDb.FILTER_COLUMN };
		Uri mContacts = MediaContentProvider.CONTENT_URI;
		Cursor cur = getContentResolver().query(mContacts, columns, null, null,
				MediasDb._ID + " DESC LIMIT " + ListMediaDb.NB_LIST_LAST_MEDIA);

		createAdapter(cur);
		displayList();
	}

}
