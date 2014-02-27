package com.geosocial.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.geosocial.R;
import com.geosocial.helpers.VolleySingleton;
import com.geosocial.models.Flickr;

/**
 * Created by Alberto Polidura on 27/02/14.
 */
public class FlickrAdapter extends BaseAdapter {

    Context context;
    private ArrayList data;
    private static LayoutInflater inflater = null;
    private ImageLoader mImageLoader;

    public FlickrAdapter(Context context, ArrayList data) {
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

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {


        final ViewHolder viewHolder;

        if (view == null) {
            view = inflater.inflate(R.layout.flickr, viewGroup, false);

            viewHolder = new ViewHolder();
            viewHolder.laImage = (NetworkImageView) view.findViewById(R.id.imageView1);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        Flickr flickr = (Flickr)data.get(i);

        if (viewHolder.laImage!=null) {
            viewHolder.laImage.setImageUrl(flickr.getUrl_m(),mImageLoader);
        }

        return view;
    }

    static class ViewHolder {
        NetworkImageView laImage;
    }


}
