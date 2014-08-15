/**
 * Class to play sound files from a remote url
 */

package com.monitordroid.app;

import java.io.IOException;


import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;




public class AudioPlayer {
	private static MediaPlayer mPlayer;
	
	/**
	 * @param url - direct link to a sound file. ex: "http://www.sound.com/file.mp3"
	 */
	
	public void playMedia(Context c, String url) {
		mPlayer = new MediaPlayer();
		mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
		try {
			mPlayer.setDataSource(url);
		} catch (IllegalArgumentException e) {

		} catch (SecurityException e) {

		} catch (IllegalStateException e) {

		} catch (IOException e) {

		}
		try {
			mPlayer.prepare();
		} catch (IllegalStateException e) {

		} catch (IOException e) {

		}
		mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
		public void onCompletion(MediaPlayer mp) {
			stop();
		}
	});
		mPlayer.start();
	}
	
	public void stop() {
		if (mPlayer != null) {
			mPlayer.release();
			mPlayer = null;
		}
	}
	

}
