package com.monitordroid.app;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;


import com.google.android.gcm.GCMRegistrar;

public class LocationUpdate extends Service {
public static final String BROADCAST_ACTION = "Location Service";
private static final int TWO_MINUTES = 1000 * 60 * 2;
public LocationManager locationManager;
public MyLocationListener listener;
public Location previousBestLocation = null;

String regId = "";
Intent intent;
int counter = 0;

@Override
public void onCreate() {
    super.onCreate();
    intent = new Intent(BROADCAST_ACTION);      
}

@Override
public void onStart(Intent intent, int startId) {    
    locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    listener = new MyLocationListener();        
    //Location updates every 2 minutes
    locationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, listener, null);
    locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, listener, null);
}

@Override
public IBinder onBind(Intent intent) {
    return null;
}

protected boolean isBetterLocation(Location location, Location currentBestLocation) {
    if (currentBestLocation == null) {
        // A new location is always better than no location
        return true;
    }

    // Check whether the new location fix is newer or older
    long timeDelta = location.getTime() - currentBestLocation.getTime();
    boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
    boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
    boolean isNewer = timeDelta > 0;

    // If it's been more than two minutes since the current location, use the new location
    // because the user has likely moved
    if (isSignificantlyNewer) {
        return true;
    // If the new location is more than two minutes older, it must be worse
    } else if (isSignificantlyOlder) {
        return false;
    }

    // Check whether the new location fix is more or less accurate
    int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
    boolean isLessAccurate = accuracyDelta > 0;
    boolean isMoreAccurate = accuracyDelta < 0;
    boolean isSignificantlyLessAccurate = accuracyDelta > 200;

    // Check if the old and new location are from the same provider
    boolean isFromSameProvider = isSameProvider(location.getProvider(),
            currentBestLocation.getProvider());

    // Determine location quality using a combination of timeliness and accuracy
    if (isMoreAccurate) {
        return true;
    } else if (isNewer && !isLessAccurate) {
        return true;
    } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
        return true;
    }
    return false;
}

/** Checks whether two providers are the same */
private boolean isSameProvider(String provider1, String provider2) {
    if (provider1 == null) {
      return provider2 == null;
    }
    return provider1.equals(provider2);
}

@Override
public void onDestroy() {       
   // handler.removeCallbacks(sendUpdatesToUI);     
    super.onDestroy();
    locationManager.removeUpdates(listener);        
}   

public static Thread performOnBackgroundThread(final Runnable runnable) {
    final Thread t = new Thread() {
        @Override
        public void run() {
            try {
                runnable.run();
            } finally {

            }
        }
    };
    t.start();
    return t;
}


public class MyLocationListener implements LocationListener
{


	public void onLocationChanged(final Location loc)
    {
        if(isBetterLocation(loc, previousBestLocation)) {
            loc.getLatitude();
            loc.getLongitude();             
            intent.putExtra("Latitude", loc.getLatitude());
            intent.putExtra("Longitude", loc.getLongitude());     
            intent.putExtra("Provider", loc.getProvider());                 
            sendBroadcast(intent);          
            String newLat = String.valueOf(loc.getLatitude());
            String newLong = String.valueOf(loc.getLongitude());
        	regId = GCMRegistrar.getRegistrationId(LocationUpdate.this);
        	new MyAsyncTask().execute(newLat, newLong);
        	LocationUpdate.this.stopSelf();
        }                               
    }

    public void onProviderDisabled(String provider)
    {
    }


    public void onProviderEnabled(String provider)
    {
    }


    public void onStatusChanged(String provider, int status, Bundle extras)
    {

    }

}

private class MyAsyncTask extends AsyncTask<String, String, Double>{
	 
	@Override
	protected Double doInBackground(String... params) {
	postData(params[0], params[1]);
	return null;
	}
	 
	protected void onPostExecute(Double result){
	}


	@SuppressWarnings("unused")
	public void postData(String value1, String value2) {
	// Create a new HttpClient and Post Header
	HttpClient httpclient = new DefaultHttpClient();
	HttpPost httppost = new HttpPost("http://www.monitordroid.com/app/postlocation.php");
	 
	try {
	List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
	nameValuePairs.add(new BasicNameValuePair("Latitude", value1));
	nameValuePairs.add(new BasicNameValuePair("Longitude", value2));
	nameValuePairs.add(new BasicNameValuePair("regName", regId));
	httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
	 
	// Execute HTTP Post Request
	HttpResponse response = httpclient.execute(httppost);
	 
	} catch (ClientProtocolException e) {
	} catch (IOException e) {
	}
	}
	 
	}
}