/**
 * Class to initiate a call on the device
 */

package com.monitordroid.app;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;


public class CallPhone {
	
	 public void caller(Context context, String phoneNumber) {
		 String uri = "tel:" + phoneNumber.trim() ;
		 Log.i("Telephone Number: ", uri);
		 Intent intent = new Intent(Intent.ACTION_CALL);
		 intent.setFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);                     
		 intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  
		 intent.setData(Uri.parse(uri));
		 context.startActivity(intent);
	 	}
}
