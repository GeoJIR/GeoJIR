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
import android.widget.Button;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;

public class ServletActivity extends ParentMenuActivity
{
	protected Subscriber<? super Cursor> subscriber;
	@InjectView(R.id.get_servlet_response)
	TextView affichage;
	@InjectView(R.id.getServlet)
	Button getButton;
	@InjectView(R.id.postServlet)
	Button postButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_servlet);
		
		ButterKnife.inject(this);
		
		// Create builder with root url server
		Builder builder = new RestAdapter.Builder();
		builder.setEndpoint(Constants.RETRO_URL_SERVLET);
		
		RestAdapter build = builder.build();
		final HelloGetService serviceGet = build.create(HelloGetService.class);
		final HelloPostService servicePost = build
				.create(HelloPostService.class);
		
		// Call server get method
		ViewObservable
				.clicks(getButton)
				// Network out of mainThread
				.observeOn(Schedulers.io())
				.map(new Func1<OnClickEvent, String>() {
					@Override
					public String call(OnClickEvent arg0)
					{
						try
						{
							String observable = serviceGet.getHello();
							return observable;
						} catch (Exception e)
						{
							return getString(R.string.no_network_toast);
						}
					}
				}).observeOn(AndroidSchedulers.mainThread())
				.subscribe(new Action1<String>() {
					@Override
					public void call(String message)
					{
						if (message == getString(R.string.no_network_toast))
							toast(R.string.no_network_toast);
						else
							affichage.setText(message);
					}
				});
		
		// Call server post method
		ViewObservable
				.clicks(postButton)
				// Network out of mainThread
				.observeOn(Schedulers.io())
				.map(new Func1<OnClickEvent, String>() {
					@Override
					public String call(OnClickEvent arg0)
					{
						try
						{
							String observable = servicePost.postHello();
							return observable;
						} catch (Exception e)
						{
							return getString(R.string.no_network_toast);
						}
					}
				}).observeOn(AndroidSchedulers.mainThread())
				.subscribe(new Action1<String>() {
					@Override
					public void call(String message)
					{
						if (message == getString(R.string.no_network_toast))
							toast(R.string.no_network_toast);
						else
							affichage.setText(message);
					}
				});
		
		// toast(R.string.no_network_toast);
		
		// POST
		/*
		 * @POST(Constants.RETRO_URL_SERVLET) String postHello(@Path("testing")
		 * String postString);
		 */
	}
	
	public interface HelloPostService
	{
		@POST("/" + Constants.RETRO_PROJECT)
		// only the “relative” part of the URL
		String postHello();
	}
	
	public interface HelloGetService
	{
		@GET("/" + Constants.RETRO_PROJECT)
		// only the “relative” part of the URL
		String getHello();
	}
	
}
