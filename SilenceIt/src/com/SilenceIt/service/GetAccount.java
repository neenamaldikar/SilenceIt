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
