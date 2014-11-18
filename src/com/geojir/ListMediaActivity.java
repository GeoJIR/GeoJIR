package com.geojir;

import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Loader;
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
import com.geojir.db.MediaContentProvider;
import com.geojir.view.CustomImageView;

public class ListMediaActivity extends ParentMenuActivity implements
		LoaderCallbacks<Cursor>
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
		
		cursorAdapter = new SimpleCursorAdapter(this, R.layout.list_item, null,
				new String[] { MediasDb.FILE_NAME_COLUMN,
						MediasDb.REMARK_COLUMN, MediasDb.FILTER_COLUMN },
				new int[] { R.id.imageIcon, R.id.remark }, 0);
		
		// create content provider
		getLoaderManager().initLoader(0, null, this);
		
		updateChildrenVisibility();
		
		// Convert String to image for ImageView
		cursorAdapter.setViewBinder(new ViewBinder() {
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
		
		listView.setAdapter(cursorAdapter);
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
	
	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args)
	{
		Uri CONTENT_URI = MediaContentProvider.CONTENT_URI;
		
		String columns[] = new String[] { MediasDb._ID,
				MediasDb.FILE_NAME_COLUMN, MediasDb.REMARK_COLUMN,
				MediasDb.FILTER_COLUMN };
		
		return new CursorLoader(this, CONTENT_URI, columns, null, null, null);
		
	}
	
	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data)
	{
		cursorAdapter.swapCursor(data);
		updateChildrenVisibility();
	}
	
	@Override
	public void onLoaderReset(Loader<Cursor> loader)
	{
		// If the Cursor is being placed in a CursorAdapter, you should use the
		// swapCursor(null) method to remove any references it has to the
		// Loader's data.
		cursorAdapter.swapCursor(null);
		updateChildrenVisibility();
	}
}
