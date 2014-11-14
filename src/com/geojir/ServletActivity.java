package com.geojir;

import retrofit.RestAdapter;
import retrofit.RestAdapter.Builder;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class ServletActivity extends ParentMenuActivity
{
	protected Subscriber<? super Cursor> subscriber;
	private EditText affichage;
	private Button getButton;
	private Button postButton;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_servlet);
/*

		affichage = (EditText) findViewById(R.id.get_servlet_response);
		getButton = (Button) findViewById(R.id.getServlet);
		postButton = (Button) findViewById(R.id.postServlet);
		Builder builder = new RestAdapter.Builder();
		builder.setEndpoint(Constants.RETRO_URL_SERVLET); // Root URL of the server
		RestAdapter build = builder.build();
		
		OnClickGet stringGetHello = build.create(OnClickGet.class);
		OnClickPost stringPostHello = build.create(OnClickPost.class);
*/		
		//GET
/*		Account observable = service.getAccount("12345");
		final OnClickGet onSubscribeGet = new OnClickGet(affichage);
		final Observable<String> myObservableGet = Observable.create(onSubscribeGet);
		final Action1<String> onNextActionGet = new Action1<String>()
		{
			@Override
			public void call(String message)
			{
				affichage.setText(message);
			}
		};
		myObservableGet.subscribe(onNextActionGet);
*/		
		//POST
/*		final OnClickPost onSubscribePost = new OnClickPost(message);
		final Observable<String> myObservablePost = Observable.create(onSubscribePost);
		final Action1<String> onNextActionPost = new Action1<String>()
		{
			@Override
			public void call(String s)
			{
				affichage.setText(s);
			}
		};
		myObservablePost.subscribe(onNextActionPost);
*/
/*
		getButton.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
//				@GET(Constants.RETRO_URL_SERVLET) String getHello(@Path("") String getString);
				// on informe l'observable
//				onSubscribeGet.onClick(message);
			}
		});

		postButton.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
//				@POST(Constants.RETRO_URL_SERVLET) String postHello(@Path("testing") String postString);
				// on informe l'observable
//				onSubscribePost.onClick(message);
			}
		});
*/
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.servlet, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings)
		{
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

/*
	private final class OnClickGet implements Observable.OnSubscribe<String>
	{
		private final String message;
		private Subscriber<? super String> subscriber;

		private OnClickGet(String message)
		{
			this.message = message;
		}

		@Override
		public void call(Subscriber<? super String> subscriber)
		{
			this.subscriber = subscriber;

		}

		public void onClick()
		{
			this.subscriber.onNext(message);
		}
	}

	private final class OnClickPost implements Observable.OnSubscribe<String>
	{
		private final String message;
		private Subscriber<? super String> subscriber;

		private OnClickPost(String message)
		{
			this.message = message;
		}

		@Override
		public void call(Subscriber<? super String> subscriber)
		{
			this.subscriber = subscriber;

		}

		public void onClick()
		{
			this.subscriber.onNext(message);
		}
	}
*/	
}
