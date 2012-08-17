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
package com.SilenceIt.model;

/**
 * @author neenam
 *
 */

public class Constants {

  public static final String TAG = "SilenceIt";
  public static final String VERSION = "1.0";

  /**
   * onActivityResult request codes:
   */
  public static final int GET_LOGIN = 0;
  public static final int AUTHENTICATED = 1;
  public static final int CREATE_EVENT = 2;

  /**
   * The type of account that we can use for API operations.
   */
  public static final String ACCOUNT_TYPE = "com.google";

  /**
   * The name of the service to authorize for.
   */
  public static final String OAUTH_SCOPE = "oauth2:https://www.googleapis.com/auth/calendar";
  
  /**
   * Date format for displaying the meeting date and time.
   */
  public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
  
  /**
   * Constants passed while calling setAlarmService from MainActivity.
   */
  public final static String ACTION_BEGIN = "com.example.test.ACTION_BEGIN";
  public final static String ACTION_END = "com.example.test.ACTION_END";
  public final static String BEGIN = "com.example.test.BEGIN";
  public final static String END = "com.example.test.END";
  public final static String CALL_SERVICE = "com.example.test.CALL_SERVICE";
  public final static String GET_ACCOUNT = "GetAccount";
  public final static String SILENCE = "silence";
  public final static String NORMAL = "normal";
  public final static String STOP_SETALARM = "StopSetAlarm";

  /* 
   * Display Text message for next meeting, no meeting and application stopped
   */
  public final static String NEXT_MEETING_MSG = "Next meeting @ ";
  public final static String STOP_MSG = "Application is stopped.";
  public final static String NO_MEETING_MSG = "No meeting for next one hour. Will check later."; 
  
  /*
   * String added to the SharedPreferences for stopping an application.
   */
  public final static String ACTION_STOP = "action_stop";
  public final static String STOP = "stop";
  
  /*
   * Minutes after which the calendar will be checked again.
   */
  public final static int DELAY = 59;
  
  /*
   * Message displayed when the phone turns silent or normal.
   */
  public final static String SILENCE_MSG = "SilenceIt: Turning your phone to silent.";
  public final static String NORMAL_MSG = "SilenceIt: Turning your phone to normal.";
  
  /*
   * Extras for silenceItService.
   */
  public final static String ALARM_MSG = "alarm_message";
  
}
