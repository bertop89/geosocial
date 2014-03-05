package com.geosocial.adapters;

import java.util.ArrayList;

import twitter4j.Status;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.geosocial.R;
import com.geosocial.helpers.VolleySingleton;
import com.geosocial.models.Flickr;

/**
 * Created by Alberto Polidura on 27/02/14.
 */
public class MixedAdapter extends BaseAdapter {

    Context context;
    private ArrayList data;
    private static LayoutInflater inflater = null;
    private ImageLoader mImageLoader;

    public MixedAdapter(Context context, ArrayList data) {
        this.context=context;
        this.data=data;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mImageLoader = VolleySingleton.getInstance(context).getImageLoader();
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int i) {
        return data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }
    
    public int getViewTypeCount() {
        return 2;
    }
    
    @Override
	public int getItemViewType(int position) {
    	if (data.get(position) instanceof Flickr) {
    		return 0;
    	} else {
    		return 1;
    	}
	}

	@Override
    public View getView(int i, View view, ViewGroup viewGroup) {


        final FlickrHolder flickrHolder;
        final TweetHolder tweetHolder;

        if (data.get(i) instanceof Flickr) {
	        if (view == null) {
	            view = inflater.inflate(R.layout.flickr, viewGroup, false);
	
	            flickrHolder = new FlickrHolder();
	            flickrHolder.laImage = (NetworkImageView) view.findViewById(R.id.ivFlickr);
	            flickrHolder.laText = (TextView) view.findViewById(R.id.tvFlickr);
	            view.setTag(flickrHolder);
	        } else {
	        	flickrHolder = (FlickrHolder) view.getTag();
	        }
	
	        Flickr flickr = (Flickr)data.get(i);
	        
	        flickrHolder.laText.setText(flickr.getTitle());
	
	        if (flickrHolder.laImage!=null) {
	        	flickrHolder.laImage.setImageUrl(flickr.getUrl_m(),mImageLoader);
	        }
	
	        return view;
        } else {
        	if (view == null) {
	            view = inflater.inflate(R.layout.tweet, viewGroup, false);
	
	            tweetHolder = new TweetHolder();
	            tweetHolder.tweetText = (TextView) view.findViewById(R.id.tvTweet);
	            view.setTag(tweetHolder);
	        } else {
	        	tweetHolder = (TweetHolder) view.getTag();
	        }
	
	        Status status = (Status)data.get(i);
	        
	        tweetHolder.tweetText.setText(status.getText());
	
	        return view;
        }
    }

    static class FlickrHolder {
        NetworkImageView laImage;
        TextView laText;
    }
    
    static class TweetHolder {
    	TextView tweetText;
    }


}
