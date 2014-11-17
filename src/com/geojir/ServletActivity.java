package com.geojir;

import retrofit.RestAdapter;
import retrofit.RestAdapter.Builder;
import retrofit.http.GET;
import retrofit.http.POST;
import rx.Subscriber;
import rx.android.events.OnClickEvent;
import rx.android.observables.ViewObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

public class ServletActivity extends ParentMenuActivity
{
	protected Subscriber<? super Cursor> subscriber;
	private TextView affichage;
	private Button getButton;
	private Button postButton;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_servlet);

		affichage = (TextView) findViewById(R.id.get_servlet_response);
		getButton = (Button) findViewById(R.id.getServlet);
		postButton = (Button) findViewById(R.id.postServlet);
		
		Builder builder = new RestAdapter.Builder();
		builder.setEndpoint(Constants.RETRO_URL_SERVLET); // Root URL of the server
		RestAdapter build = builder.build();
		final HelloGetService serviceGet = build.create(HelloGetService.class);
		final HelloPostService servicePost = build.create(HelloPostService.class);

		ViewObservable.clicks(getButton)
		.observeOn(Schedulers.io())	//pour gérer le résultat du click (accès réseau) dans un thread autre que le mainThread
		.map(new Func1<OnClickEvent, String>()
		{
			@Override
			public String call(OnClickEvent arg0)
			{
				String observable = serviceGet.getHello();
				return observable;
			}
		})
		.observeOn(AndroidSchedulers.mainThread())
		.subscribe(new Action1<String>()
		{
			@Override
			public void call(String message)
			{
				affichage.setText(message);
			}
		});

		ViewObservable.clicks(postButton)
		.observeOn(Schedulers.io())	//pour gérer le résultat du click (accès réseau) dans un thread autre que le mainThread
		.map(new Func1<OnClickEvent, String>()
		{
			@Override
			public String call(OnClickEvent arg0)
			{
				String observable = servicePost.postHello();
				return observable;
			}
		})
		.observeOn(AndroidSchedulers.mainThread())
		.subscribe(new Action1<String>()
		{
			@Override
			public void call(String message)
			{
				affichage.setText(message);
			}
		});

		//POST
/*
				@POST(Constants.RETRO_URL_SERVLET) String postHello(@Path("testing") String postString);
*/
	}

	public interface HelloPostService 
	{
		@POST("/"+Constants.RETRO_PROJECT) // only the “relative” part of the URL
		String postHello();
	}
	
	public interface HelloGetService 
	{
		@GET("/"+Constants.RETRO_PROJECT) // only the “relative” part of the URL
		String getHello();
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
		return super.onOptionsItemSelected(item);
	}
}
