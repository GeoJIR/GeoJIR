package com.geojir.menus;

import java.util.ArrayList;
import java.util.List;

import android.view.View;
import android.widget.ImageView;
import butterknife.ButterKnife;
import butterknife.ButterKnife.Setter;

public class TabImageMenu
{
	// Lists of iconsMenu and corresponding layout
	protected ArrayList<ImageView> listeIcons = new ArrayList<ImageView>();
	protected ArrayList<View> listeLayout = new ArrayList<View>();
	
	// Setter for alpha of media icon
	static final Setter<View, Boolean> ENABLED = new Setter<View, Boolean>()
	{
		@Override
		public void set(View view, Boolean value, int index)
		{
			if (value)
				view.setAlpha((float) 1);
			else
				view.setAlpha((float) .3);
		}
	};

	// Setter for mask/display layouts
	static final Setter<View, Boolean> VISIBILITY = new Setter<View, Boolean>()
	{
		@Override
		public void set(View view, Boolean value, int index)
		{
			if (value)
				view.setVisibility(View.VISIBLE);
			else
				view.setVisibility(View.GONE);

			view.setEnabled(value);
		}
	};
	
	// Adding Icons and Layouts
	public void addAll(List<ImageView> mediasIcons, List<View> mediasLayout)
	{
		addIcons(mediasIcons);
		addLayouts(mediasLayout);
	}
	
	public void addIcons(List<ImageView> mediasIcons)
	{
		for (int i=0; i<mediasIcons.size(); i++)
			this.addIcon(mediasIcons.get(i));
	}

	protected void addIcon(ImageView imageView)
	{
		listeIcons.add(imageView);
	}

	public void addLayouts(List<View> mediasIcons)
	{
		for (int i=0; i<mediasIcons.size(); i++)
			this.addLayout(mediasIcons.get(i));
	}

	protected void addLayout(View imageView)
	{
		listeLayout.add(imageView);
	}
	
	// Active current tab with icon clicked parameter
	public void activeTab(ImageView currentView)
	{
		// Disable all media icons
		ButterKnife.apply(listeIcons, ENABLED, false);
		// Mask all medias layout
		ButterKnife.apply(listeLayout, VISIBILITY, false);
		
		int index_temp = listeIcons.indexOf(currentView);
		// Active and display element depending current media
		if (index_temp != -1)
		{
			ENABLED.set(listeIcons.get(index_temp), true, 0);
			VISIBILITY.set(listeLayout.get(index_temp), true, 0);
		}
	}
}
