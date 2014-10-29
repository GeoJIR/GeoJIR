package com.geojir;

//import java.io.File;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Toast;

public class CaptureActivity extends ParentMenuActivity {

	// chemin du fichier son
	private static String mFileName = null;

	private Button mRecordButton = null;
	private MediaRecorder mRecorder = null;
	boolean mStartRecording = true;

	private Button mPlayButton = null;
	private MediaPlayer mPlayer = null;
	boolean mStartPlaying = true;
	// fin ajout microphone

	// checkbox qui represente le filtre noir et blanc
	private CheckBox filterMonochrome;
	//boutton pour sauvegarder le filtre N&B
	private Button savefilterbutton;
	
	// photo
	// chemin du fichier photo
	public String mPhotoName;

	static final int REQUEST_TAKE_PHOTO = 436547;

	//

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_capture);
/******************************************POUR LES TESTS*************************************************************/
    	//Init des variables / constantes
    	Constants.initConstants(getApplicationContext());
/*
		//BOF : appel ListMedia activity
		Intent intent = new Intent(this, ListMediaActivity.class);
		startActivity(intent);
		//EOF : appel ListMedia activity
*/		
		//BOF : appel AccountActivity activity
		Intent intent2 = new Intent(this, AccountActivity.class);
		startActivity(intent2);
		//EOF : appel AccountActivity activity
/******************************************POUR LES TESTS*************************************************************/
        
		// on récupére le chemin de la photo s'il y en avait déjà une
		if (savedInstanceState != null) {
			mPhotoName = savedInstanceState.getString("maphoto");
		}

		//on recupere la checkbox et on lui applique un listenner qui executera le filtre N&B
		filterMonochrome = (CheckBox) findViewById(R.id.filterMonochrome);
		OnClickListener filterclicker = new OnClickListener() {
			public void onClick(View v) {
				monochromPic();
			}
		};
		filterMonochrome.setOnClickListener(filterclicker);
		
		//boutton de sauvegarde du filtre en BDD
		savefilterbutton = (Button) findViewById(R.id.savefilterbutton);
		OnClickListener filterSaveClicker = new OnClickListener() {
			public void onClick(View v) {
				saveMonochromPic();
			}
		};
		savefilterbutton.setOnClickListener(filterSaveClicker);
		
		if(mPhotoName == null || mPhotoName.isEmpty()) {
			filterMonochrome.setVisibility(android.view.View.INVISIBLE);
			savefilterbutton.setVisibility(android.view.View.INVISIBLE);
		} else {
			filterMonochrome.setVisibility(android.view.View.VISIBLE);
			savefilterbutton.setVisibility(android.view.View.VISIBLE);
		}

		// photo
		ImageView photoButton = (ImageView) this.findViewById(R.id.imagePhotos);
		photoButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent cameraIntent = new Intent(
						android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

				// enregistrement de la photo
				if (cameraIntent.resolveActivity(getPackageManager()) != null) {
					// Create the File where the photo should go
					File photoFile = null;
					try {
						photoFile = createImageFile();
					} catch (IOException ex) {
						// Error occurred while creating the File
						ex.printStackTrace();
					}
					// Continue only if the File was successfully created
					if (photoFile != null) {
						cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,
								Uri.fromFile(photoFile));
						startActivityForResult(cameraIntent, REQUEST_TAKE_PHOTO);
					}
				}
			}
		});
		// fin photo

		// ajout bouton et listener pour le microphone
		mRecordButton = (Button) findViewById(R.id.recordbutton);
		OnClickListener recordclicker = new OnClickListener() {
			public void onClick(View v) {
				onRecord(mStartRecording);
				if (mStartRecording) {
					mRecordButton.setText("Arréter l'enregistrement");
				} else {
					mRecordButton.setText("Enregistrer un son");
				}
				mStartRecording = !mStartRecording;
			}
		};
		mRecordButton.setOnClickListener(recordclicker);

		mPlayButton = (Button) findViewById(R.id.playbutton);
		OnClickListener playclicker = new OnClickListener() {
			public void onClick(View v) {
				onPlay(mStartPlaying);
				if (mStartPlaying) {
					mPlayButton.setText("Arrété de jouer le son");
				} else {
					mPlayButton.setText("Joué le son enregistrer");
				}
				mStartPlaying = !mStartPlaying;
			}
		};
		mPlayButton.setOnClickListener(playclicker);

		if (mFileName != null) {
			mPlayButton.setEnabled(true);
		} else
			mPlayButton.setEnabled(false);
		// fin ajout microphone

	}

	// on refefinis cette méthode car on veut pouvoir remettre la photo s'il en
	// existait une
	// lors d'un changement de rotation. Et il n'y a que dans cette méthode que
	// l'on peut
	// récupérer les paramétres nécéssaires a opération (L'image est complétment
	// initialiser ici alors que ce
	// n'est pas le cas dans le onCreate et le onRestore)
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);

		// on teste si il y a une photo deja présente
		if (mPhotoName != null) {
			if (!mPhotoName.isEmpty()) {
				// si cest le cas on la charge et on laffiche
				File maphoto = new File(mPhotoName);
				boolean p = maphoto.exists();
				if (p) {
					monochromPic();
				}
					
			}
		}
		// fin test si photo deja présente ou non

	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_TAKE_PHOTO) {

			ImageView view = (ImageView) this.findViewById(R.id.imageApercu);
			if (data != null && data.getExtras() != null) {
				Bundle extras = data.getExtras();
				Bitmap photo = (Bitmap) extras.get("data");
				view.setImageBitmap(photo);

			} else {

				if ((!mPhotoName.isEmpty()) && (mPhotoName != null)) {
					File maphoto = new File(mPhotoName);
					boolean p = maphoto.exists();
					if (p)
						setPic();
				}
			}
		}
	}

	// méthode permet dafficher la photo dans l'endroit prévu
	private void setPic() {
		ImageView view = (ImageView) this.findViewById(R.id.imageApercu);

		// Get the dimensions of the View
		int targetW = view.getWidth();
		int targetH = view.getHeight();
		if (targetW < 1)
			targetW = 1;
		if (targetH < 1)
			targetH = 1;

		// Get the dimensions of the bitmap
		BitmapFactory.Options bmOptions = new BitmapFactory.Options();
		bmOptions.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(mPhotoName, bmOptions);
		int photoW = bmOptions.outWidth;
		int photoH = bmOptions.outHeight;

		// Determine how much to scale down the image
		int scaleFactor = Math.min(photoW / targetW * 3, photoH / targetH * 3);

		// Decode the image file into a Bitmap sized to fill the View
		bmOptions.inJustDecodeBounds = false;
		bmOptions.inSampleSize = scaleFactor;

		Bitmap bitmap = BitmapFactory.decodeFile(mPhotoName, bmOptions);
		view.setImageBitmap(bitmap);
		if(filterMonochrome != null)
		{
			filterMonochrome.setVisibility(android.view.View.VISIBLE);
			savefilterbutton.setVisibility(android.view.View.VISIBLE);
		}
	}

	// méthode qui permet d'appliquer un effet noir et blanc sur la photo
	public void monochromPic() {
		if (filterMonochrome.isChecked()) 
		{
			ImageView view = (ImageView) this.findViewById(R.id.imageApercu);
			
			// On recupere les dimensions de l'image view
			int targetW = view.getWidth();
			int targetH = view.getHeight();
			if (targetW < 1) targetW = 1;
			if (targetH < 1) targetH = 1;

			// Get the dimensions of the bitmap
			BitmapFactory.Options bmOptions = new BitmapFactory.Options();
			bmOptions.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(mPhotoName, bmOptions);
			int photoW = bmOptions.outWidth;
			int photoH = bmOptions.outHeight;

			// On determine le redimensionnement
			int scaleFactor = Math.min(photoW / targetW * 3, photoH / targetH * 3);

			// Decode the image file into a Bitmap sized to fill the View
			bmOptions.inJustDecodeBounds = false;
			bmOptions.inSampleSize = scaleFactor;

			Bitmap bitmap = BitmapFactory.decodeFile(mPhotoName, bmOptions);   

			//Passage au N&B
		    Bitmap bmpGrayscale = bitmap.copy(Bitmap.Config.ARGB_8888, true);
		    Canvas c = new Canvas(bmpGrayscale);
		    ColorMatrix cm = new ColorMatrix();
		    cm.setSaturation(0);
		    ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
		    Paint paint = new Paint();
		    paint.setColorFilter(f);
		    c.drawBitmap(bmpGrayscale, 0, 0, paint);
		    
		    //sauvegarde du nouveau bitmap N&B
		    /*FileOutputStream out = null;
		    try {
		    	File filename = createImageFile();
		        out = new FileOutputStream(filename);
		        bmpGrayscale.compress(Bitmap.CompressFormat.PNG, 90, out);
		        
		    } catch (Exception e) {
		        e.printStackTrace();
		    }*/
		    
			// return final image
			view.setImageBitmap(bmpGrayscale);
		}
		else 
		{
			setPic();
		}
	}
	
	//Sauvegarde en base de donnée du filtre
	public void saveMonochromPic() {
		
	}

	// Microphone
	// méthode qui permet de lancer ou d'arreter l'enregistrement d'un son
	private void onRecord(boolean start) {
		if (start) {
			startRecording();
		} else {
			stopRecording();
		}
	}

	// methode qui permet de démarrer ou d'arreter le son enregistrer
	private void onPlay(boolean start) {
		if (start) {
			try {
				startPlaying();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			stopPlaying();
		}
	}

	// corps de la fonction que permet de jouer le son
	private void startPlaying() throws IllegalArgumentException,
			SecurityException, IllegalStateException, IOException {
		mPlayer = new MediaPlayer();
		// on desactive le boutton d'enregistrement
		mRecordButton.setEnabled(false);

		mPlayer.setDataSource(mFileName);
		mPlayer.prepare();
		mPlayer.start();

		// on met un listenner pour savoir quand le player a finis de jouer le
		// son
		mPlayer.setOnCompletionListener(new OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer arg0) {
				// on realise le click du bouton stop a la fin du son
				mPlayButton.callOnClick();
			}
		});

		// message pour dire que lon joue le son audio
		Toast.makeText(getApplicationContext(), "Playing audio",
				Toast.LENGTH_SHORT).show();
	}

	// corps de la fonction que permet d'arreter le son
	private void stopPlaying() {
		mPlayer.release();
		mPlayer = null;
		// on reactive le boutton d'enregistrement
		mRecordButton.setEnabled(true);

		Toast.makeText(getApplicationContext(), "Stop playing audio",
				Toast.LENGTH_SHORT).show();
	}

	// corps de la fonction que permet d'enregistrer le son
	private void startRecording() {
		createFileAudioRecord();

		mRecorder = new MediaRecorder();
		mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
		mRecorder.setOutputFile(mFileName);
		mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

		try {
			mRecorder.prepare();
			mRecorder.start();

			// on desactive le boutton pour jouer le son audio
			mPlayButton.setEnabled(false);

			Toast.makeText(getApplicationContext(),
					"Début de l'enregistrement", Toast.LENGTH_SHORT).show();

		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void stopRecording() {
		mRecorder.stop();
		mRecorder.release();
		mRecorder = null;

		// on reactive le boutton pour jouer le son audio
		mPlayButton.setEnabled(true);

		Toast.makeText(getApplicationContext(),
				"L'enregistrement s'est bien passé.", Toast.LENGTH_SHORT)
				.show();
	}

	public void createFileAudioRecord() {
		// on enregistre le son sur le storage exterieur (carte SD)
		mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
		// on genere un nom avec la date pour ne pas ecraser les ancien sons
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
				Locale.getDefault()).format(new Date());
		mFileName += "/" + timeStamp + "audiorecord.3gp";
	}

	// fin microphone

	// photo enregistrement
	private File createImageFile() throws IOException {
		// Create an image file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
				Locale.getDefault()).format(new Date());
		String imageFileName = "JPEG_" + timeStamp + "_";
		File storageDir = Environment
				.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
		File image = File.createTempFile(imageFileName, // prefix
				".jpg", // suffix
				storageDir // directory
				);

		// Save a file: path for use with ACTION_VIEW intents
		mPhotoName = image.getAbsolutePath();
		return image;
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		// Always call the superclass so it can save the view hierarchy state
		super.onSaveInstanceState(savedInstanceState);

		// on verifie que l'on est pas en train d'enregistrer un son.
		// Si c'est le cas on l'arrête avant de détruire complétmeent
		// l'application
		if (!mStartRecording) {
			stopRecording();
			mStartRecording = !mStartRecording;
		}

		if (!mStartPlaying) {
			stopPlaying();
			mStartPlaying = !mStartPlaying;
		}

		// Save the user's current game state
		savedInstanceState.putString("maphoto", mPhotoName);
	}

}
