package com.aaa.service;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.aaa.activity.MainActivity;
import com.aaa.model.Constants;

public class SetAlarmService extends IntentService{
	public static final String TAG = "SetAlarm";
	public static int requestCode = 1, requestCode1 = 2, requestCode2 = 3;
	static AlarmManager myAlarmManager;
	
	public SetAlarmService() {
		super("SetAlarmService");
	}
	
	@Override
	protected void onHandleIntent(Intent i) {
		Log.d(TAG, "SetAlarmService - onHandleIntent");

		Bundle bundle = i.getExtras();
		Log.d(TAG, "call_service: " + bundle.getString(MainActivity.CALL_SERVICE));
		if (bundle.getString(MainActivity.CALL_SERVICE) == null) {
				Log.d(TAG, "call service == null");
				setSileceItAlarm(bundle.getLong(MainActivity.BEGIN),
								 bundle.getLong(MainActivity.END),
								 bundle.getString(MainActivity.ACTION_BEGIN),
								 bundle.getString(MainActivity.ACTION_END));
			} else if (bundle.getString(MainActivity.CALL_SERVICE).equals("GetAccount")) {
				setGetAccountAlarm(bundle.getLong(MainActivity.BEGIN), bundle.getString(MainActivity.CALL_SERVICE));
			} 
			/*
			 * else if (bundle.getString(MainActivity.CALL_SERVICE).equals("StopSetAlarm")) { 
			 *	stopAlarm();
			 *			}
			 */
	}

	protected void setGetAccountAlarm(long begin_time, String call_service)
	{
		Log.d(TAG, "begin_time: " + begin_time);
	    Log.d(TAG, "call_service: " + call_service);
		 
		Intent intent = new Intent(this, GetAccount.class);
		PendingIntent sender2 = PendingIntent.getService(this, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		Log.d(Constants.TAG, "PendingIntent GetAccount: " + sender2.toString());
		 
		// Get the AlarmManager service
		AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
		am.set(AlarmManager.RTC_WAKEUP, begin_time, sender2);
	}

	protected void setSileceItAlarm(long begin_time, long end_time, String action_begin, String action_end)
	{
		Log.d(TAG, "begin_time: " + begin_time);
	    Log.d(TAG, "end_time: " + end_time);
	    Log.d(TAG, action_begin);
	    Log.d(TAG, action_end);

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
