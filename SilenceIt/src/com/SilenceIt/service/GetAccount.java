/*
 * SilenceIt - An open source project for the Android platform, that will silence your mobile according to your
 * calendar appointment.
 * Application written in Java
 * Application uses Google Calendar API Powered by GOOGLE
 * 
 * Copyright (C) 2012 Neena Maldikar
 *
 * Please see the file License in this distribution for license terms. Below is the link to the file License.
 * https://github.com/neenamaldikar/SilenceIt/blob/master/License
 *
 * Following is the link for the repository- https://github.com/neenamaldikar/SilenceIt.git
 *
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or (at
 * your option) any later version.
 *  
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
 * for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this library; if not, write to the Free Software Foundation,
 * Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 * Written by Neena Maldikar <neena@pdx.edu>
 * 
 * Calendar API Reference - http://code.google.com/p/google-meeting-scheduler/
 */
package com.SilenceIt.service;

import java.util.Calendar;

import android.accounts.Account;
import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;

import com.SilenceIt.model.Constants;
import com.SilenceIt.utils.GetCalEntries;
import com.SilenceIt.utils.OAuthManager;
import com.google.api.services.calendar.model.TimePeriod;

public class GetAccount extends IntentService{

	private static GetCalEntries getCalEntries;
	private static TimePeriod FirstEvent = new TimePeriod();
	private Handler handler = new Handler();
	
	public GetAccount() {
		super("GetAccount");
	}
	
	@Override
	protected void onHandleIntent(Intent intent) {
		
		Log.d(Constants.TAG, "Inside GetAccount Service");

        SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(this);
		if (preference.getString(Constants.ACTION_STOP, "").equals(Constants.STOP)) {
			Log.d(Constants.TAG, Constants.ACTION_STOP);
			SharedPreferences.Editor editor =
					PreferenceManager.getDefaultSharedPreferences(this).edit();
			editor.putString(Constants.ACTION_STOP, "");
			editor.commit();
			this.stopSelf();
		} else {
            OAuthManager.getInstance().doLogin(true, this,new OAuthManager.AuthHandler() {
			/* Neena - commented old code
			OAuthManager.getInstance().doLogin(false, this, new OAuthManager.AuthHandler() {
			*/
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
    		cal_begin.add(Calendar.MINUTE, Constants.DELAY);
        	intent.putExtra(Constants.BEGIN, cal_begin.getTimeInMillis());
        	intent.putExtra(Constants.CALL_SERVICE, Constants.GET_ACCOUNT);
        	startService(intent);
    	} else {
    		Log.d(Constants.TAG, "FirstEvent NOT null");
        	Log.d(Constants.TAG, "FirstEvent: " + FirstEvent.toString());
	    	Log.d(Constants.TAG, "begin_time: " + FirstEvent.getStart().getValue());
	        Log.d(Constants.TAG, "end_time: " + FirstEvent.getEnd().getValue());
	    	intent.putExtra(Constants.BEGIN, FirstEvent.getStart().getValue());
	    	intent.putExtra(Constants.END, FirstEvent.getEnd().getValue());    	
	    	intent.putExtra(Constants.ACTION_BEGIN, Constants.SILENCE);
	    	intent.putExtra(Constants.ACTION_END, Constants.NORMAL);
	    	startService(intent);
    	}
    }
}
