/*
 * Copyright (c) 2010 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

/*
 * NOTICE: This file has been modified for SilnceIt application. 
 */
package com.SilenceIt.utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import android.util.Log;

import com.SilenceIt.model.Constants;
import com.google.api.services.calendar.model.TimePeriod;
import com.google.common.collect.Iterables;

public class GetCalEntries {

	/** UI attributes. */
	private GetFreeBusyTimes getFreeBusyTimes;
	private TimePeriod FirstEvent = new TimePeriod();

	/**
	 * Constructor. 
	 * Will make the object and get the times
	 * 
	 */
	public GetCalEntries() {
		getFreeBusyTimes = new GetFreeBusyTimes(OAuthManager.getInstance().getAuthToken());
	}
	
    public TimePeriod getEntries() {
    	
    	  Date nowtime = new Date();
    	  
    	  Map<String, List<TimePeriod>> busytimes;
    	  try{
    		  	busytimes = getFreeBusyTimes.getBusyTimes(OAuthManager.getInstance().getAccount().name, nowtime, 1);
    		  	for(List<TimePeriod> busy : busytimes.values()){
    		  		Log.d(Constants.TAG, "busy: " + busy.toString());
    		  	}
    		  	Log.d(Constants.TAG, "size of busytimes: " + busytimes.values().size());
    		  	Log.d(Constants.TAG, "is empty busytimes: " + busytimes.values().isEmpty());
    		  	List<TimePeriod> defaultFirst = new ArrayList<TimePeriod>();
    		  	Log.d(Constants.TAG, "defaultFirst: " + defaultFirst.toString());
    		  	Log.d(Constants.TAG,"First: " + Iterables.getFirst(busytimes.values(), defaultFirst));

		  		try {
		  			if (Iterables.getFirst(busytimes.values(), defaultFirst).isEmpty()) {
    		  			Log.d(Constants.TAG, "No events");
		  			} else {
		  			FirstEvent = Iterables.get(busytimes.values(), 0).get(0);
		  			}
		  		} catch (IndexOutOfBoundsException e1) {
		  			Log.d(Constants.TAG, "Index Out of Bound Exception");
		  		}
    	  }
    	  catch (final Exception e){
    		  Log.e(Constants.TAG,"Some Serious ERROR");
    		  e.printStackTrace();
    		  //Log.e(Constants.TAG, e.getStackTrace().toString());
    	  }
        
    	Log.d(Constants.TAG, "Returning FirstEvent: " + FirstEvent.toString());
    	return FirstEvent;
    }
}
