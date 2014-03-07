package com.geosocial.activities;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import twitter4j.GeoLocation;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.OAuth2Token;
import twitter4j.conf.ConfigurationBuilder;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.GridView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.geosocial.R;
import com.geosocial.adapters.MixedAdapter;
import com.geosocial.helpers.LocationUtils;
import com.geosocial.helpers.URLHelper;
import com.geosocial.helpers.VolleySingleton;
import com.geosocial.models.Flickr;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

public class MainActivity extends Activity implements GooglePlayServicesClient.ConnectionCallbacks,GooglePlayServicesClient.OnConnectionFailedListener, LocationListener {
	
    private ArrayList<Object> flickrList;
    private Type typeList = new TypeToken<List<Flickr>>(){}.getType();
    private GridView gridView;
    private MixedAdapter adapter;
    
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    // Stores the current instantiation of the location client in this object
    private LocationClient mLocationClient;
    // 	A request to connect to Location Services
    private LocationRequest mLocationRequest;
    // Global variable to hold the current location
    Location mCurrentLocation;
    
    
    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		gridView = (GridView) findViewById(R.id.lvMainList);
		flickrList = new ArrayList<Object>();
		adapter = new MixedAdapter(MainActivity.this, flickrList);
		gridView.setAdapter(adapter);
		
	    loadLocation();
		loadAds();

	}
    
    

	public class TwitterTask extends AsyncTask<Void, Void, QueryResult> {
    	
    	@Override
    	protected QueryResult doInBackground(Void... void1) {
    		QueryResult result = null;
    		ConfigurationBuilder builder=new ConfigurationBuilder();
            builder.setUseSSL(true);
            builder.setApplicationOnlyAuthEnabled(true);

            // setup
            Twitter twitter = new TwitterFactory(builder.build()).getInstance();

            // exercise & verify
            twitter.setOAuthConsumer("hdJdvKKdP9f6rf6neBaQ", "YYYOjmmlOqAn0KigHSDifLxVffm90famZ5oXxxc68rQ");
            
            try {
            	OAuth2Token token = twitter.getOAuth2Token();
            	Query query = new Query();
            	GeoLocation geolocation = new GeoLocation(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
            	query.setGeoCode(geolocation, 5, Query.MILES);
    			result = twitter.search(query);
    			Log.d("twitter", result.toString());
    		} catch (TwitterException e) {
    			e.printStackTrace();
    		}
            
    		return result;
    	}

    	protected void onPostExecute(QueryResult result) {
    		List<twitter4j.Status> tweets = result.getTweets();
    		flickrList.addAll(tweets);
    		Collections.shuffle(flickrList);
    		adapter.notifyDataSetChanged();
    	}
    	
    }
	
	private void loadLocation() {
    	mLocationClient = new LocationClient(this, this, this);
		// Create a new global location parameters object
	    mLocationRequest = LocationRequest.create();
	    mLocationRequest.setNumUpdates(1);
	}
		
	private void loadAds() {
		// Look up the AdView as a resource and load a request.
	    AdView adView = (AdView)this.findViewById(R.id.adView);
	    AdRequest adRequest = new AdRequest.Builder().build();
	    adView.loadAd(adRequest);
	}

	private void loadData() {
		String URL = URLHelper.getFlickrURL(mCurrentLocation); 
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, URL, null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        // display response
                        JSONArray photos = new JSONArray();
                        try {
                            response = response.getJSONObject("photos");
                            photos = response.getJSONArray("photo");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Gson gson = new GsonBuilder().create();
                        flickrList.addAll((Collection)gson.fromJson(photos.toString(), typeList));                        
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Error.Response", error.toString());
                    }
                }
        );

        // add it to the RequestQueue
        VolleySingleton.getInstance(this).getRequestQueue().add(getRequest);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	/*
     * Called when the Activity becomes visible.
     */
    @Override
    protected void onStart() {
        super.onStart();
        // Connect the client.
        mLocationClient.connect();
        
    }
    
    /*
     * Called when the Activity is no longer visible.
     */
    @Override
    protected void onStop() {
        // Disconnecting the client invalidates it.
        mLocationClient.disconnect();
        super.onStop();
    }
    
	/*
     * Handle results returned to this Activity by other Activities started with
     * startActivityForResult(). In particular, the method onConnectionFailed() in
     * LocationUpdateRemover and LocationUpdateRequester may call startResolutionForResult() to
     * start an Activity that handles Google Play services problems. The result of this
     * call returns here, to onActivityResult.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {

        // Choose what to do based on the request code
        switch (requestCode) {

            // If the request code matches the code sent in onConnectionFailed
            case LocationUtils.CONNECTION_FAILURE_RESOLUTION_REQUEST :

                switch (resultCode) {
                    // If Google Play services resolved the problem
                    case Activity.RESULT_OK:

                        // Log the result
                        Log.d(LocationUtils.APPTAG, getString(R.string.resolved));

                    break;

                    // If any other result was returned by Google Play services
                    default:
                        // Log the result
                        Log.d(LocationUtils.APPTAG, getString(R.string.no_resolution));

                    break;
                }

            // If any other request code was received
            default:
               // Report that this Activity received an unknown requestCode
               Log.d(LocationUtils.APPTAG,
                       getString(R.string.unknown_activity_request_code, requestCode));

               break;
        }
    }

    /**
     * Verify that Google Play services is available before making a request.
     *
     * @return true if Google Play services is available, otherwise false
     */
    private boolean servicesConnected() {

        // Check that Google Play services is available
        int resultCode =
                GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

        // If Google Play services is available
        if (ConnectionResult.SUCCESS == resultCode) {
            // In debug mode, log the status
            Log.d(LocationUtils.APPTAG, getString(R.string.play_services_available));

            // Continue
            return true;
        // Google Play services was not available for some reason
        } else {
            // Display an error dialog
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(resultCode, this, 0);
            dialog.show();
            return false;
        }
    }

	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {
		/*
         * Google Play services can resolve some errors it detects.
         * If the error has a resolution, try sending an Intent to
         * start a Google Play services activity that can resolve
         * error.
         */
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(
                        this,
                        CONNECTION_FAILURE_RESOLUTION_REQUEST);
                /*
                 * Thrown if Google Play services canceled the original
                 * PendingIntent
                 */
            } catch (IntentSender.SendIntentException e) {
                // Log the error
                e.printStackTrace();
            }
        } else {
            /*
             * If no resolution is available, display a dialog to the
             * user with the error.
             */
            showErrorDialog(connectionResult.getErrorCode());
        }
	}

	@Override
	public void onConnected(Bundle arg0) {

        if (servicesConnected()) {
        	Toast.makeText(getApplicationContext(), "Trying to get location", 
        			   Toast.LENGTH_LONG).show();
        	mLocationClient.requestLocationUpdates(mLocationRequest, this);
        }
			
		
	}

	@Override
	public void onDisconnected() {
		
	}
	
	/**
     * Show a dialog returned by Google Play services for the
     * connection error code
     *
     * @param errorCode An error code returned from onConnectionFailed
     */
    private void showErrorDialog(int errorCode) {

        // Get the error dialog from Google Play services
        Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(
            errorCode,
            this,
            LocationUtils.CONNECTION_FAILURE_RESOLUTION_REQUEST);

        // If Google Play services can provide an error dialog
        if (errorDialog != null) {

            errorDialog.show();
        }
    }

	@Override
	public void onLocationChanged(Location arg0) {
		Toast.makeText(getApplicationContext(), "Location obtained, loading data", 
				   Toast.LENGTH_LONG).show();
		mCurrentLocation = arg0;
    	Log.d("Location", LocationUtils.getLatLng(this, mCurrentLocation));
    	loadData();
    	TwitterTask tarea = new TwitterTask();
		tarea.execute();
	}
    
}
