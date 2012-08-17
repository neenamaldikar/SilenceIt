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

import android.util.Log;

import com.SilenceIt.model.Constants;
import com.google.api.client.extensions.android2.AndroidHttp;
import com.google.api.client.googleapis.auth.oauth2.draft10.GoogleAccessProtectedResource;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.json.JsonHttpRequest;
import com.google.api.client.http.json.JsonHttpRequestInitializer;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarRequest;

/**
 * @author alainv
 * 
 */
public class CalendarServiceBuilder {

  /**
   * Builds a Calendar service object.
   * 
   * @param accessToken Access token to use to authorize requests.
   * @return Calendar service object.
   */
  public static Calendar build(String accessToken) {
	  Log.d(Constants.TAG,"accessToken" + accessToken);
    HttpTransport transport = AndroidHttp.newCompatibleTransport();
    JacksonFactory jsonFactory = new JacksonFactory();

    GoogleAccessProtectedResource accessProtectedResource =
        new GoogleAccessProtectedResource(accessToken);

    Calendar service =
        Calendar.builder(transport, jsonFactory).setApplicationName("SilenceIt/1.0")
            .setJsonHttpRequestInitializer(new JsonHttpRequestInitializer() {
              @Override
              public void initialize(JsonHttpRequest request) {
                CalendarRequest calendarRequest = (CalendarRequest) request;

                // TODO: Get an API key from Google's APIs Console:
                // https://code.google.com/apis/console.
                calendarRequest.setKey("AIzaSyCxLvfBrhMk9-RDAD5YG7L0FqEaCynoAFg");
              }
            }).setHttpRequestInitializer(accessProtectedResource).build();
    Log.d(Constants.TAG,"Calendar Service: " + service.toString());
    return service;
  }
}
