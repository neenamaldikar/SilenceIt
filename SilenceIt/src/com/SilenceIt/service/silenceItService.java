/* **************************************************************************
 * Copyright© 2012 Neena Maldikar
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details. You should have
 * received a copy of the GNU General Public License along with this program. 
 * If not, see <http://www.gnu.org/licenses/>.
 *
 * Author: Neena Maldikar
 * Feedback: neena@pdx.edu
 *
 * SilenceIt - An android app that changes your phone volume as per your 
 * calendar event.
 ****************************************************************************/

package com.SilenceIt.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.SilenceIt.model.Constants;

public class silenceItService extends IntentService {
	private static String message;
	private Handler silenceItServiceHandler = new Handler();	
	
	public silenceItService() {
		super("silenceItService");
	}
	
	@Override
	protected void onHandleIntent(Intent intent) {
		Log.d(Constants.TAG, "service - onHandleIntent");
		
		Bundle bundle = intent.getExtras();
		message = bundle.getString(Constants.ALARM_MSG);
		Log.d(Constants.TAG, message);

		SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(this);
		if (preference.getString(Constants.ACTION_STOP, "").equals(Constants.STOP)) {
			Log.d(Constants.TAG, "action stop");
			if (message == Constants.NORMAL) {
				SharedPreferences.Editor editor =
						PreferenceManager.getDefaultSharedPreferences(this).edit();
				editor.putString(Constants.ACTION_STOP, "");
				editor.commit();
				this.stopSelf();
			}
		} else {	    
			silenceItServiceHandler.post(new Runnable() {
				@Override
				public void run() {
					if (message.equals(Constants.NORMAL)) {
						toastMessage(Constants.NORMAL_MSG);				
					}
					else if (message.equals(Constants.SILENCE)) {
						toastMessage(Constants.SILENCE_MSG);
					}					
				}
			});
			changeVolume();
		}
	}

	void toastMessage(String messsage)
	{
		Log.d(Constants.TAG, "Inside toastmessage. message = " + message);
		int duration = Toast.LENGTH_SHORT;
		Toast toast = Toast.makeText(getApplicationContext(), messsage, duration);
		toast.show();
	}

	void changeVolume()
	{
		Log.d(Constants.TAG, "In changeVolume");
		AudioManager am = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
		if (message.equals(Constants.SILENCE)) {
			Log.d(Constants.TAG, "message == silence");
			am.setRingerMode(AudioManager.RINGER_MODE_SILENT);
		} else if (message.equals(Constants.NORMAL)) {
			Log.d(Constants.TAG, "message == normal");
			am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
	    	Intent i = new Intent(this, GetAccount.class);
	    	//intent.putExtra(BEGIN, cal_begin.getTimeInMillis());
	    	startService(i);
		} else {
			Log.d(Constants.TAG,"otherwise");
			am.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
		}
	}
}
