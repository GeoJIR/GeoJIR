package com.geojir;

import java.util.List;

import android.app.Activity;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;

public class PhotoActivity extends Activity implements SurfaceHolder.Callback
{
	private Camera camera;
	private SurfaceView surfaceCamera;
	private Boolean isPreview;

	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);

	    // Nous mettons l'application en plein écran et sans barre de titre
	    getWindow().setFormat(PixelFormat.TRANSLUCENT);
	    requestWindowFeature(Window.FEATURE_NO_TITLE);
	    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
	            WindowManager.LayoutParams.FLAG_FULLSCREEN);

	    isPreview = false;

	    // Nous appliquons notre layout
	    setContentView(R.layout.activity_photo);

	    // Nous récupérons notre surface pour le preview
	    surfaceCamera = (SurfaceView) findViewById(R.id.surfaceViewCamera);
/*
	    Camera.Parameters parameters = camera.getParameters();
        List<Camera.Size> previewSizes = parameters.getSupportedPreviewSizes();
        Camera.Size previewSize = previewSizes.get(0);
*/        
	    // Méthode d'initialisation de la caméra
	    InitializeCamera();
        
	}
	
	  public static Camera isCameraAvailiable(){
	      Camera object = null;
	      try {
	         object = Camera.open(); 
	      }
	      catch (Exception e){
	      }
	      return object; 
	   }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.photo, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
        
        // Nous prenons le contrôle de la camera
	    if (camera == null)
	        camera = Camera.open();
	}

	@Override
	public void surfaceCreated(SurfaceHolder arg0) {
	    // Nous arrêtons la camera et nous rendons la main
	    if (camera != null) {
	        camera.stopPreview();
	        
	        isPreview = false;
	        camera.release();
	    }
	}

	
	public void InitializeCamera() 
	{
		// Nous attachons nos retours du holder à notre activité
	    camera = isCameraAvailiable();
		surfaceCamera.getHolder().addCallback(this);
		
		// Nous spécifiions le type du holder en mode SURFACE_TYPE_PUSH_BUFFERS
/*
		surfaceCamera.getHolder().setType(
		SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
*/
	}
	
	public void surfaceDestroyed(SurfaceHolder holder) {
	    // Nous arrêtons la camera et nous rendons la main
	    if (camera != null) {
	        camera.stopPreview();
	        isPreview = false;
	        camera.release();
	    }
	}
	
	// Retour sur l'application
	@Override
	public void onResume() {
	    super.onResume();
	    camera = Camera.open();
	}

	// Mise en pause de l'application
	@Override
	public void onPause() {
	    super.onPause();

	    if (camera != null) {
	        camera.release();
	        camera = null;
	    }
	}
}
