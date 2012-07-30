package com.aaa.service;

import java.util.Calendar;

import android.accounts.Account;
import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;

import com.aaa.model.Constants;
import com.aaa.utils.GetCalEntries;
import com.aaa.utils.OAuthManager;
import com.google.api.services.calendar.model.TimePeriod;

public class GetAccount extends IntentService{

	private static GetCalEntries getCalEntries;
	private static TimePeriod FirstEvent = new TimePeriod();
	private Handler handler = new Handler();
	
	public final static String ACTION_BEGIN = "com.example.test.ACTION_BEGIN";
	public final static String ACTION_END = "com.example.test.ACTION_END";
	public final static String BEGIN = "com.example.test.BEGIN";
	public final static String END = "com.example.test.END";
	public final static String CALL_SERVICE = "com.example.test.CALL_SERVICE";
	
	public GetAccount() {
		super("GetAccount");
	}
	
	@Override
	protected void onHandleIntent(Intent intent) {
		
		Log.d(Constants.TAG, "Inside GetAccount Service");

        SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(this);
		if (preference.getString("action_stop", "").equals("stop")) {
			Log.d(Constants.TAG, "action stop");
			SharedPreferences.Editor editor =
					PreferenceManager.getDefaultSharedPreferences(this).edit();
			editor.putString("action_stop", "");
			editor.commit();
			this.stopSelf();
		} else {
			OAuthManager.getInstance().doLogin(false, this, new OAuthManager.AuthHandler() {
				@Override
				public void handleAuth(Account account, String authToken) {
					if (account != null) {
						Log.d(Constants.TAG, "before runnable");
						Runnable runnable = new Runnable() {
							@Override
							public void run() {
								Log.d(Constants.TAG, "inside runnable");
			    		        getCalEntries = new GetCalEntries();
			    	        	FirstEvent = getCalEntries.getEntries();
			    	        	Log.d(Constants.TAG, "Got first event");
			    	        	
			    	        	handler.post(new Runnable() {
			    	        		@Override
			    	        		public void run() {
			    	        			Log.d(Constants.TAG, "inside handler");
			    	        			startSetAlarmService();
			    	        			}
			    	        		});
			    	        	}
							};
							new Thread(runnable).start();
							}
					}
				});
			}
		}
	
    public void startSetAlarmService() {
    	
    	Log.d(Constants.TAG, "In startSetAlarmService");
    	Intent intent = new Intent(this, SetAlarmService.class);
    	if (FirstEvent.isEmpty()) {
    		Log.d(Constants.TAG, "FirstEvent == null");
        	// get a Calendar object with current time
    		Calendar cal_begin = Calendar.getInstance();
    		// add 60 minutes to the calendar object
    		cal_begin.add(Calendar.MINUTE, 60);
        	intent.putExtra(BEGIN, cal_begin.getTimeInMillis());
        	intent.putExtra(CALL_SERVICE, "GetAccount");
        	startService(intent);
    	} else {
    		Log.d(Constants.TAG, "FirstEvent NOT null");
        	Log.d(Constants.TAG, "FirstEvent: " + FirstEvent.toString());
	    	Log.d(Constants.TAG, "begin_time: " + FirstEvent.getStart().getValue());
	        Log.d(Constants.TAG, "end_time: " + FirstEvent.getEnd().getValue());
	    	intent.putExtra(BEGIN, FirstEvent.getStart().getValue());
	    	intent.putExtra(END, FirstEvent.getEnd().getValue());    	
	    	intent.putExtra(ACTION_BEGIN, "silence");
	    	intent.putExtra(ACTION_END, "normal");
	    	startService(intent);
    	}
    }
}
