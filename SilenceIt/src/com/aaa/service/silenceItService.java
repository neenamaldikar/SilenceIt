package com.aaa.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

public class silenceItService extends IntentService {
	public static final String TAG = "sileceIt";
	
	public silenceItService() {
		super("silenceItService");
	}
	
	@Override
	protected void onHandleIntent(Intent intent) {
		Log.d(TAG, "service - onHandleIntent");
		
		Bundle bundle = intent.getExtras();
		String message = bundle.getString("alarm_message");
		Log.d(TAG, message);

		SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(this);
		if (preference.getString("action_stop", "").equals("stop")) {
			Log.d(TAG, "action stop");
			if (message == "normal") {
				SharedPreferences.Editor editor =
						PreferenceManager.getDefaultSharedPreferences(this).edit();
				editor.putString("action_stop", "");
				editor.commit();
				this.stopSelf();
			}
		} else {	    
			AudioManager am = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
			if (message.equals("silence")) {
				Log.d(TAG, "message == silence");
				am.setRingerMode(AudioManager.RINGER_MODE_SILENT);
			} else if (message.equals("normal")) {
				Log.d(TAG, "message == normal");
				am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
		    	Intent i = new Intent(this, GetAccount.class);
		    	//intent.putExtra(BEGIN, cal_begin.getTimeInMillis());
		    	startService(i);
			} else {
				Log.d(TAG,"otherwise");
				am.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
			}
		}
	}
}
