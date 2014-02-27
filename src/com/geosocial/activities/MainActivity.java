package com.geosocial.activities;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.GridView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.geosocial.R;
import com.geosocial.adapters.FlickrAdapter;
import com.geosocial.helpers.VolleySingleton;
import com.geosocial.models.Flickr;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

public class MainActivity extends Activity {
	
    private ArrayList<Flickr> flickrList;
    private Type typeList = new TypeToken<List<Flickr>>(){}.getType();
    private GridView gridView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		gridView = (GridView) findViewById(R.id.lvMainList);
		loadData();
	}

	private void loadData() {
		String URL = null; //TODO Get flickr API URL
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, URL, null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        // display response
                        JSONArray photos = new JSONArray();
                        try {
                            photos = response.getJSONArray("photos");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Gson gson = new GsonBuilder().create();
                        flickrList = new ArrayList<Flickr>();
                        flickrList = gson.fromJson(photos.toString(), typeList);
                        gridView.setAdapter(new FlickrAdapter(MainActivity.this, flickrList));
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
        VolleySingleton.getInstance(getActivity()).getRequestQueue().add(getRequest);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
