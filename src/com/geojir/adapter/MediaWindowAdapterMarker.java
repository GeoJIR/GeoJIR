package com.geojir.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.geojir.R;
import com.geojir.medias.Callback;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.model.Marker;

public class MediaWindowAdapterMarker implements InfoWindowAdapter
{

	private Marker markerShowingInfoWindow;
    private Context mContext;
    public MediaWindowAdapterMarker(Context context) {
        mContext = context;
    }
    
    @Override
    public View getInfoContents(Marker marker) {

        markerShowingInfoWindow = marker;
        
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        
        // Getting view from the layout file info_window_layout
        View popUp = inflater.inflate(R.layout.layout_popup, null);

        TextView popUpTitle = (TextView) popUp.findViewById(R.id.popup_title);
        TextView popUpContent = (TextView) popUp.findViewById(R.id.popup_content);
        ImageView popUpImage = (ImageView) popUp.findViewById(R.id.popup_image);

        popUpTitle.setText(marker.getTitle());
        popUpContent.setText(marker.getSnippet());
        
        // Load the image thumbnail
        final String imagePath = markers.get(marker.getId());
        ImageLoader imageLoader = ((AppConfig)mContext.getApplicationContext()).getImageLoader();
        imageLoader.loadBitmap(imagePath, popUpImage, 0, 0, onImageLoaded);
        
        // Returning the view containing InfoWindow contents
        return popUp;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        
        return null;
    }
    
    /**
     * This method is called after the bitmap has been loaded. It checks if the currently displayed
     * info window is the same info window which has been saved. If it is, then refresh the window
     * to display the newly loaded image.
     */
    private Callback onImageLoaded = new Callback() {
        
        @Override
        public void execute(String result) {
            if (markerShowingInfoWindow != null && markerShowingInfoWindow.isInfoWindowShown()) {
                markerShowingInfoWindow.hideInfoWindow();
                markerShowingInfoWindow.showInfoWindow();
            }
        }
    };


}
