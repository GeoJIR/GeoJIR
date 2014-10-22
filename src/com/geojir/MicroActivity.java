package com.geojir;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import android.widget.LinearLayout;
import android.widget.Toast;
import android.os.Bundle;
import android.os.Environment;
import android.view.ViewGroup;
import android.widget.Button;
import android.view.View;
import android.view.View.OnClickListener;
import android.content.Context;
import android.util.Log;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaRecorder;
import android.media.MediaPlayer;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MicroActivity extends Activity {
	
	
	private static final String LOG_TAG = "AudioRecordTest";
    private static String mFileName = null;

    private RecordButton mRecordButton = null;
    private MediaRecorder mRecorder = null;

    private PlayButton   mPlayButton = null;
    private MediaPlayer   mPlayer = null;

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
	    Toast.makeText(getApplicationContext(), "Playing audio", Toast.LENGTH_LONG).show();
    }
    

    private void stopPlaying() {
        mPlayer.release();
        mPlayer = null;
        //on reactive le boutton d'enregistrement
        mRecordButton.setEnabled(true);
        
        Toast.makeText(getApplicationContext(), "Stop playing audio", Toast.LENGTH_LONG).show();
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
        	
        	Toast.makeText(getApplicationContext(), "Début de l'enregistrement", Toast.LENGTH_LONG).show();

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
        
        Toast.makeText(getApplicationContext(), "Audio. L'enregistrement s'est bien pass�.",
        Toast.LENGTH_LONG).show();
    }

    class RecordButton extends Button {
        boolean mStartRecording = true;

        OnClickListener clicker = new OnClickListener() {
            public void onClick(View v) {
                onRecord(mStartRecording);
                if (mStartRecording) {
                    setText("Stop recording");
                } else {
                    setText("Start recording");
                }
                mStartRecording = !mStartRecording;
            }
        };

        public RecordButton(Context ctx) {
            super(ctx);
            setText("Start recording");
            setOnClickListener(clicker);
        }
    }

    class PlayButton extends Button {
        boolean mStartPlaying = true;

        OnClickListener clicker = new OnClickListener() {
            public void onClick(View v) {
                onPlay(mStartPlaying);
                if (mStartPlaying) {
                    setText("Stop playing");
                } else {
                    setText("Start playing");
                }
                mStartPlaying = !mStartPlaying;
            }
        };

        public PlayButton(Context ctx) {
            super(ctx);
            setText("Start playing");
            setOnClickListener(clicker);
        }
    }

    public void createFileAudioRecord() {
    	//on enregistre le son sur le storage exterieur (carte SD)
        mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        //on genere un nom avec la date pour ne pas ecraser les ancien sons
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        mFileName += "/"+timeStamp+"audiorecord.3gp";
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        //AudioRecordTest();
        
        LinearLayout ll = new LinearLayout(this);
        mRecordButton = new RecordButton(this);
        ll.addView(mRecordButton,
            new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                0));
        mPlayButton = new PlayButton(this);
        ll.addView(mPlayButton,
            new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                0));
        
        if(mFileName !=null)
        {
        	mPlayButton.setEnabled(true);
        }
        else mPlayButton.setEnabled(false);
        
        setContentView(ll);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mRecorder != null) {
            mRecorder.release();
            mRecorder = null;
        }

        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }
    }
	
	

    //////
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        //Test de push de Igor
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
}
