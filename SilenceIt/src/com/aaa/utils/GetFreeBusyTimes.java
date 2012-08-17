/**
 * 
 */
package com.aaa.utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.util.Log;

import com.aaa.model.Constants;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpResponseException;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.Calendar.Freebusy;
import com.google.api.services.calendar.model.FreeBusyCalendar;
import com.google.api.services.calendar.model.FreeBusyRequest;
import com.google.api.services.calendar.model.FreeBusyRequestItem;
import com.google.api.services.calendar.model.FreeBusyResponse;
import com.google.api.services.calendar.model.TimePeriod;

/**
 * @author achatt4
 * 
 */
public class GetFreeBusyTimes {

	private Calendar service;

	/**
	 * Pass accesstoken to create a calender service
	 * 
	 * @param accessToken
	 */
	public GetFreeBusyTimes(String accessToken) {
		this.service = CalendarServiceBuilder.build(accessToken);
	}

	/**
	 * Pass an existing service. Will make this to point to that
	 * 
	 * @param service
	 */
	public GetFreeBusyTimes(Calendar service) {
		this.service = service;
	}

	/**
	   * 
	   */
	public Map<String, List<TimePeriod>> getBusyTimes(String myid,
			Date startDate, int timeSpan) throws Exception {
		Map<String, List<TimePeriod>> result = new HashMap<String, List<TimePeriod>>();
		List<FreeBusyRequestItem> requestItems = new ArrayList<FreeBusyRequestItem>();
		FreeBusyRequest request = new FreeBusyRequest();

		request.setTimeMin(getDateTime(startDate, 0));
		request.setTimeMax(getDateTime(startDate, timeSpan));
		requestItems.add(new FreeBusyRequestItem().setId(myid));
				
		request.setItems(requestItems);

		Log.d(Constants.TAG,"In Get busy times 0");
		FreeBusyResponse busyTimes;
		try {
			Log.d(Constants.TAG,"In Get busy times 1" + service.toString());
			Log.d(Constants.TAG,"In Get busy times 2" + request.toString());
			Freebusy.Query query = service.freebusy().query(request);
			// Use partial GET to only retrieve needed fields.
			query.setFields("calendars");
			busyTimes = query.execute();
			for (Map.Entry<String, FreeBusyCalendar> busyCalendar : 
					busyTimes.getCalendars().entrySet()) {
				result.put(busyCalendar.getKey(), busyCalendar.getValue().getBusy());
			}
		} catch (Exception e) {
			Log.e(Constants.TAG,
					"Exception occured while retrieving busy times: "
							+ e.toString());
			if (e instanceof HttpResponseException) {
				HttpResponse response = ((HttpResponseException) e)
						.getResponse();
				int statusCode = response.getStatusCode();
				if (statusCode == 401) {
					// The token might have expired, throw the exception to let
					// calling Activity know.
					throw e;
				}
			}
		}
		return result;
	}

	/**
	 * Create a new DateTime object initialized at the current day +
	 * {@code daysToAdd}.
	 * 
	 * @param startDate
	 *            The date from which to compute the DateTime.
	 * @param daysToAdd
	 *            The number of days to add to the result.
	 * 
	 * @return The new DateTime object initialized at the current day +
	 *         {@code daysToAdd}.
	 */
	private DateTime getDateTime(Date startDate, int daysToAdd) {
		java.util.Calendar date = new GregorianCalendar();
		date.setTime(startDate);
		date.add(java.util.Calendar.DAY_OF_YEAR, daysToAdd);
		return new DateTime(date.getTime().getTime(), 0);
	}
}
