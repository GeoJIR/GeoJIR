package com.geojir;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.geojir.MicroActivity.PlayButton;
import com.geojir.MicroActivity.RecordButton;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;


public class MainActivity extends Activity {
	
	//Ajout pour le microphone
	private static final String LOG_TAG = "AudioRecordTest";
    private static String mFileName = null;

    private Button mRecordButton = null;
    private MediaRecorder mRecorder = null;
    boolean mStartRecording = true;

    private Button   mPlayButton = null;
    private MediaPlayer   mPlayer = null;
    boolean mStartPlaying = true;
    //fin ajout microphone
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        ImageView photoButton = (ImageView) this.findViewById(R.id.imagePhotos);
        photoButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE); 
                startActivityForResult(cameraIntent, 1888); 
            }
        });
        
        //ajout bouton pour le microphone
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
        
        if(mFileName !=null)
        {
        	mPlayButton.setEnabled(true);
        }
        else mPlayButton.setEnabled(false);
        //fin ajout microphone
        
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {  
        if (requestCode == 1888 && resultCode == RESULT_OK) {  
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            
            ImageView view = (ImageView) this.findViewById(R.id.imageApercu);
            view.setImageBitmap(photo);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        
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
    

    //Microphone
    private void onRecord(boolean start) {
        if (start) {
            startRecording();
        } else {
            stopRecording();
        }
    }

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

    private void startPlaying() throws IllegalArgumentException,   
    SecurityException, IllegalStateException, IOException {
        mPlayer = new MediaPlayer();
        //on desactive le boutton d'enregistrement
        mRecordButton.setEnabled(false);
        
        mPlayer.setDataSource(mFileName);
	    mPlayer.prepare();
	    mPlayer.start();
	   
	    //on met un listenner pour savoir quand le player a finis de jouer le son
	    mPlayer.setOnCompletionListener(new
	    	    OnCompletionListener() {        
	    	        @Override
	    	        public void onCompletion(MediaPlayer arg0) {
	    	        	//on realise le click du bouton stop a la fin du son
	    	    	    mPlayButton.callOnClick();
	    	    }
	    	});
	    
	    //message pour dire que lon joue le son audio
	    Toast.makeText(getApplicationContext(), "Playing audio", Toast.LENGTH_SHORT).show();
    }
    
    private void stopPlaying() {
        mPlayer.release();
        mPlayer = null;
        //on reactive le boutton d'enregistrement
        mRecordButton.setEnabled(true);
        
        Toast.makeText(getApplicationContext(), "Stop playing audio", Toast.LENGTH_SHORT).show();
    }

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
        	
        	//on desactive le boutton pour jouer le son audio
        	mPlayButton.setEnabled(false);
        	
        	Toast.makeText(getApplicationContext(), "Début de l'enregistrement", Toast.LENGTH_SHORT).show();

         } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
         } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
         }

    }

    private void stopRecording() {
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
        
        //on reactive le boutton pour jouer le son audio
        mPlayButton.setEnabled(true);
        
        Toast.makeText(getApplicationContext(), "Audio. L'enregistrement s'est bien passé.",
        Toast.LENGTH_SHORT).show();
    }

    

    public void createFileAudioRecord() {
    	//on enregistre le son sur le storage exterieur (carte SD)
        mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        //on genere un nom avec la date pour ne pas ecraser les ancien sons
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        mFileName += "/"+timeStamp+"audiorecord.3gp";
    }
    //fin microphone
}
