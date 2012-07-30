package com.aaa.activity;


import java.util.Calendar;

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

import com.aaa.R;
import com.aaa.model.Constants;
import com.aaa.service.SetAlarmService;
import com.aaa.utils.GetCalEntries;
import com.aaa.utils.OAuthManager;
import com.google.api.services.calendar.model.TimePeriod;

public class MainActivity extends Activity {

	private static GetCalEntries getCalEntries;
	private static TimePeriod FirstEvent;
	private Handler handler = new Handler();
	
	public final static String ACTION_BEGIN = "com.example.test.ACTION_BEGIN";
	public final static String ACTION_END = "com.example.test.ACTION_END";
	public final static String BEGIN = "com.example.test.BEGIN";
	public final static String END = "com.example.test.END";
	public final static String CALL_SERVICE = "com.example.test.CALL_SERVICE";

	
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
          editor.putString("action_stop", "");
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
         editor.putString("action_stop", "stop");
         editor.commit();
         Log.d(Constants.TAG,"onClick() ended - stop button");
        } 
    };
    
    public void getAccount() {

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

    public void stopSetAlarmService() {
    	
    	Log.d(Constants.TAG, "In stopSetAlarmService");
    	Intent intent = new Intent(this, SetAlarmService.class);
        intent.putExtra(CALL_SERVICE, "StopSetAlarm");
        startService(intent);
    }
}
