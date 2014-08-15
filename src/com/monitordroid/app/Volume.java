package com.monitordroid.app;

import android.content.Context;
import android.media.AudioManager;

public class Volume {
	public static AudioManager myAudioManager;
	
	@SuppressWarnings("static-access")
	public void loud(Context context) {
		myAudioManager = (AudioManager)context.getSystemService(context.AUDIO_SERVICE);
		myAudioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
	}
	
	@SuppressWarnings("static-access")
	public void vibrate(Context context) {
		myAudioManager = (AudioManager)context.getSystemService(context.AUDIO_SERVICE);	
		myAudioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
	}
	
	@SuppressWarnings("static-access")
	public void silent(Context context) {
		myAudioManager = (AudioManager)context.getSystemService(context.AUDIO_SERVICE);	
		myAudioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);		
	}
	
	@SuppressWarnings("static-access")
	public void raiseVolume(Context context) {
		myAudioManager = (AudioManager)context.getSystemService(context.AUDIO_SERVICE);			
		myAudioManager.adjustVolume(AudioManager.ADJUST_RAISE, AudioManager.FLAG_PLAY_SOUND + AudioManager.FLAG_SHOW_UI);
	}
	
	@SuppressWarnings("static-access")
	public void lowerVolume(Context context) {
		myAudioManager = (AudioManager)context.getSystemService(context.AUDIO_SERVICE);			
		myAudioManager.adjustVolume(AudioManager.ADJUST_LOWER, AudioManager.FLAG_PLAY_SOUND + AudioManager.FLAG_SHOW_UI);
	}
	}


