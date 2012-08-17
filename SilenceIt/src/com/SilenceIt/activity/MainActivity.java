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
 */

package com.SilenceIt.activity;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.accounts.Account;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.SilenceIt.R;
import com.SilenceIt.model.Constants;
import com.SilenceIt.service.SetAlarmService;
import com.SilenceIt.utils.GetCalEntries;
import com.SilenceIt.utils.OAuthManager;
import com.google.api.services.calendar.model.TimePeriod;

public class MainActivity extends Activity {

	private static GetCalEntries getCalEntries;
	private static TimePeriod FirstEvent;
	private Handler handler = new Handler();
    private SimpleDateFormat sdf = new SimpleDateFormat (Constants.DATE_FORMAT);
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(Constants.TAG,"Main Activity: onCreate");
        
        Button buttonStart = (Button)findViewById(R.id.buttonStart);        
        buttonStart.setOnClickListener(startListener); 
         
        Button buttonStop = (Button)findViewById(R.id.buttonStop);        
        buttonStop.setOnClickListener(stopListener);
    }
    
    @Override
    public void onResume() {
        Log.d(Constants.TAG,"Main Activity: onResume");
        super.onResume();

   		displayTextMessage("");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
   
  //Create an anonymous implementation of OnClickListener
    private OnClickListener startListener = new OnClickListener() {
        public void onClick(View v) {
          Log.d(Constants.TAG,"onClick() called - start button");

          SharedPreferences.Editor editor =
        		  PreferenceManager.getDefaultSharedPreferences(MainActivity.this).edit();
          editor.putString(Constants.ACTION_STOP, "");
          editor.commit();

          getAccount();
          
          Log.d(Constants.TAG,"onClick() ended - start button");
        }
    };
     
    // Create an anonymous implementation of OnClickListener
    private OnClickListener stopListener = new OnClickListener() {
        public void onClick(View v) {
         Log.d(Constants.TAG,"onClick() called - stop button");
         //stopSetAlarmService();
         SharedPreferences.Editor editor =
        	        PreferenceManager.getDefaultSharedPreferences(MainActivity.this).edit();
         editor.putString(Constants.ACTION_STOP, Constants.STOP);
         editor.commit();
     	displayTextMessage(Constants.STOP_MSG);         
         Log.d(Constants.TAG,"onClick() ended - stop button");
        } 
    };
    
    public void getAccount() {
    	Log.d(Constants.TAG,"Inside getAccount");
		OAuthManager.getInstance().doLogin(true, this, new OAuthManager.AuthHandler() {
            @Override
            public void handleAuth(Account account, String authToken) {
              if (account != null) {
                Log.d(Constants.TAG, "Main:handleauth. authToken = " + authToken + "account: " + account.toString());
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
    
    public void startSetAlarmService() {
    	
    	Log.d(Constants.TAG, "In startSetAlarmService");
    	Intent intent = new Intent(this, SetAlarmService.class);
    	// get a Calendar object with current time
		Calendar cal_begin = Calendar.getInstance();
    	if (FirstEvent.isEmpty()) {
    		Log.d(Constants.TAG, "FirstEvent == null");
        	displayTextMessage(Constants.NO_MEETING_MSG);
    		// add 59 minutes to the calendar object
    		cal_begin.add(Calendar.MINUTE, Constants.DELAY);
        	intent.putExtra(Constants.BEGIN, cal_begin.getTimeInMillis());
        	intent.putExtra(Constants.CALL_SERVICE, Constants.GET_ACCOUNT);
        	startService(intent);
    	} else {
    		Log.d(Constants.TAG, "FirstEvent NOT null");
        	Log.d(Constants.TAG, "FirstEvent: " + FirstEvent.toString());
	    	Log.d(Constants.TAG, "begin_time: " + FirstEvent.getStart().getValue());
	        Log.d(Constants.TAG, "end_time: " + FirstEvent.getEnd().getValue());
	        
	        Date FirstEventStartDate = new Date(FirstEvent.getStart().getValue());
	        Log.d(Constants.TAG, "First Event begin date format: " + sdf.format(FirstEventStartDate));

	        displayTextMessage(Constants.NEXT_MEETING_MSG + sdf.format(FirstEventStartDate));
	        
	    	intent.putExtra(Constants.BEGIN, FirstEvent.getStart().getValue());
	    	intent.putExtra(Constants.END, FirstEvent.getEnd().getValue());    	
	    	intent.putExtra(Constants.ACTION_BEGIN, Constants.SILENCE);
	    	intent.putExtra(Constants.ACTION_END, Constants.NORMAL);
	    	startService(intent);
    	}
    }

    public void stopSetAlarmService() {
    	
    	Log.d(Constants.TAG, "In stopSetAlarmService");
    	Intent intent = new Intent(this, SetAlarmService.class);
        intent.putExtra(Constants.CALL_SERVICE, Constants.STOP_SETALARM);
        startService(intent);
    }
    
    public void displayTextMessage(String message) {

    	Log.d(Constants.TAG, "In displayTextMessage");
	    final TextView displayTextView = (TextView) findViewById(R.id.displayText);    		
   	    displayTextView.setText(message);
    }
}
