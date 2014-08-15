/**
 * Class to handle turning the flashlight on or off
 * Known Bugs: Only known to work on the Samsung Galaxy Series of Phones 
 */

package com.monitordroid.app;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;


public class Flashlight {
	
	public static Camera cam;
	public static boolean isOn;
	
	public void flashOn(Context context) {
		
		if (!isOn) {
		boolean hasFlash = context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
		
		if (hasFlash) {
			isOn = true;
			cam = Camera.open();     
			Parameters p = cam.getParameters();
			p.setFlashMode(Parameters.FLASH_MODE_TORCH);
			cam.setParameters(p);
			cam.startPreview();			
		}
		}
	}
	
	public void flashOff(Context context) {
		if (isOn) {
		isOn = false;
		cam.stopPreview();
		cam.release();
		}
	}

}
