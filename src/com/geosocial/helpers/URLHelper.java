package com.geosocial.helpers;

import android.location.Location;

public class URLHelper {
	
	public static String getFlickrURL(Location location) {
		return "http://www.flickr.com/services/rest/?method=flickr.photos.search&api_key=efd1e597f88f2fa88049449fd1605c11" +
				"&lat="+location.getLatitude()+
				"&lon="+location.getLongitude()+
				"&per_page=20&page=1" +
				"&nojsoncallback=1&format=json" +
				"&extras=url_m";
	}

}
