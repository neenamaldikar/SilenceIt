// Copyright 2011 Google Inc. All Rights Reserved.

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
                calendarRequest.setKey("<INPUT_YOUR_API_KEY>");
              }
            }).setHttpRequestInitializer(accessProtectedResource).build();
    Log.d(Constants.TAG,"Calendar Service: " + service.toString());
    return service;
  }
}
