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

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.SilenceIt.model.Constants;

public class SetAlarmService extends IntentService{
	public static final String TAG = "SetAlarm";
	public static int requestCode = 1, requestCode1 = 2, requestCode2 = 3;
	static AlarmManager myAlarmManager;
	
	public SetAlarmService() {
		super("SetAlarmService");
	}
	
	@Override
	protected void onHandleIntent(Intent i) {
		Log.d(Constants.TAG, "SetAlarmService - onHandleIntent");

		Bundle bundle = i.getExtras();
		Log.d(Constants.TAG, "call_service: " + bundle.getString(Constants.CALL_SERVICE));
		if (bundle.getString(Constants.CALL_SERVICE) == null) {
				Log.d(Constants.TAG, "call service == null");
				setSileceItAlarm(bundle.getLong(Constants.BEGIN),
								 bundle.getLong(Constants.END),
								 bundle.getString(Constants.ACTION_BEGIN),
								 bundle.getString(Constants.ACTION_END));
			} else if (bundle.getString(Constants.CALL_SERVICE).equals("GetAccount")) {
				setGetAccountAlarm(bundle.getLong(Constants.BEGIN), bundle.getString(Constants.CALL_SERVICE));
			} 
			/*
			 * else if (bundle.getString(Constants.CALL_SERVICE).equals("StopSetAlarm")) { 
			 *	stopAlarm();
			 *			}
			 */
	}

	protected void setGetAccountAlarm(long begin_time, String call_service)
	{
		Log.d(Constants.TAG, "begin_time: " + begin_time);
	    Log.d(Constants.TAG, "call_service: " + call_service);
		 
		Intent intent = new Intent(this, GetAccount.class);
		PendingIntent sender2 = PendingIntent.getService(this, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		Log.d(Constants.TAG, "PendingIntent GetAccount: " + sender2.toString());
		 
		// Get the AlarmManager service
		AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
		am.set(AlarmManager.RTC_WAKEUP, begin_time, sender2);
	}

	protected void setSileceItAlarm(long begin_time, long end_time, String action_begin, String action_end)
	{
		Log.d(Constants.TAG, "begin_time: " + begin_time);
	    Log.d(Constants.TAG, "end_time: " + end_time);
	    Log.d(Constants.TAG, action_begin);
	    Log.d(Constants.TAG, action_end);

		Intent intent = new Intent(this, silenceItService.class);
		intent.putExtra("alarm_message", action_begin);
		PendingIntent sender = PendingIntent.getService(this, requestCode1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		Log.d(Constants.TAG, "PendingIntent silence: " + sender.toString());
		 
		// Get the AlarmManager service
		AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
		am.set(AlarmManager.RTC_WAKEUP, begin_time, sender);
		
		Intent intent1 = new Intent(this, silenceItService.class);
		intent1.putExtra("alarm_message", action_end);
		PendingIntent sender1 = PendingIntent.getService(this, requestCode2, intent1, PendingIntent.FLAG_UPDATE_CURRENT);
		Log.d(Constants.TAG, "PendingIntent normal: " + sender1.toString());

		// Get the AlarmManager service
		AlarmManager am1 = (AlarmManager) getSystemService(ALARM_SERVICE);
		am1.set(AlarmManager.RTC_WAKEUP, end_time, sender1);
	}

	/*
	protected void stopAlarm()
	{
		Log.d(TAG, "In stopAlarm");
		 
		//cancel if there is any pending GetAccount alarm.
		Intent intent1 = new Intent(this, GetAccount.class);
		PendingIntent sender1 = PendingIntent.getService(this, requestCode, intent1, PendingIntent.FLAG_UPDATE_CURRENT);
		Log.d(Constants.TAG, "PendingIntent GetAccount: " + sender1.toString());
		try {
			myAlarmManager.cancel(sender1);
		} catch(Exception e1){
			Log.d(Constants.TAG, "No pending GetAccount alarms");
		}
		
		//cancel if there is any silence pending alarms.
		Intent intent2 = new Intent(this, silenceItService.class);
		PendingIntent sender2 = PendingIntent.getService(this, requestCode1, intent2, PendingIntent.FLAG_UPDATE_CURRENT);
		intent2.putExtra("alarm_message", "silence");
		Log.d(Constants.TAG, "PendingIntent silence: " + sender2.toString());
		try {
			myAlarmManager.cancel(sender2);
		} catch (Exception e2) {
			Log.d(Constants.TAG, "No pending silence alarms");
		}

		//cancel if there is any normal pending alarms.
		Intent intent3 = new Intent(this, silenceItService.class);
		PendingIntent sender3 = PendingIntent.getService(this, requestCode2, intent3, PendingIntent.FLAG_UPDATE_CURRENT);
		intent2.putExtra("alarm_message", "normal");
		Log.d(Constants.TAG, "PendingIntent normal: " + sender3.toString());
		try { 
			myAlarmManager.cancel(sender3);
		} catch (Exception e3) {
			Log.d(Constants.TAG, "No pending normal alarms");
		}
	}
	*/
}
