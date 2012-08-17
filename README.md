/*******************************************************************
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
*******************************************************************/

SilenceIt
=========

Description of the project:
---------------------------
This is an open source project for the Android platform. It is an Android App that 
changes your phone volume according to your calendar.

When the user clicks on "Start" button, the app fetches the user account details 
from the Google Account settings on the phone. If there are multiple accounts, it 
displays all the accounts and lets the user choose one. If there is only one account,
it does not ask user to select it. Once the account is selected, it looks up the 
Google calendar for that account and cheks for the first calendar appointment with 
status 'busy' within next one hour. 

If there is no appointment within next one hour, it schedules a service to run and 
check the appointment again after one hour. If there is an appointment with status 
busy within next one hour, it gets the begin and end time for the appointment. Then 
it schedules a service to change the phone volume to silent at the beginning of the
appointment and normal at the end of the appointment. It also displays a notification
when the phone voume changes. At the end of the appointment, in addition to resetting
the volume to normal, it also checks for the next appointment with busy status in 
next one hour. 

When the user clicks on "Stop" button, it stops the appllication and does not run any
services scheduled in future by the app. 

Future Enhancements
------------------------

1. Currently when user clicks "Stop" button, it edits the SharedPreferences file and 
   updates value of 'action_stop' as Stop. Any service triggered by an alarm checks 
   this value and then stops itself without performing any of its operations. This is
   not the best way to stop the application activities. The right way is to cancel 
   all the alarms schedules by the app, when the user clicks on "Stop". There is a 
   dead code (method: stopAlarm)in 'SetAlarmService.java' module for this. This code
   does not work as expected. It should be fixed and the service should be called from
   'OnClickListener stopListener' in 'MainActivity'. 
   
2. There should be an incon on the top indicating that the service is running in the 
   background.

3. The application displays toast notifications, when the app changes the phone volume
   to silent or normal. This can be changed to interactive snooze notification that 
   will allow the user to snooze it.

4. Provide user with the setting that will allow to change between silent, vibrate and
   normal for the appointments.

NOTE: The application is still under testing phase so please let me know via email in
case of any issues. 

Platform:
---------
Android device (Google API platform 4.1 API Level 16)

Installation:
-------------
Please read "INSTALL.pdf" located at below location:

https://github.com/neenamaldikar/SilenceIt/blob/master/INSTALL.pdf

for complete instructions on how to install and configure this application
for use by developers. 

Step-by-Step guide on how to Use the application:
-------------------------------------------------
Please read "USER_MANUAL.pdf" located at below location:

https://github.com/neenamaldikar/SilenceIt/blob/master/USER_MANUAL.pdf

License Information:
--------------------
This application is licensed under GPLv3. Please read "LICENSE.txt" for full version 
of the license terms and conditions located at: 
https://github.com/neenamaldikar/SilenceIt/blob/master/LICENSE.txt

Attributions:
-------------
I am very grateful to all the people whose queries and answers on stackoverflow.com 
helped me in overcoming any technical difficulties.

Also, many thanks for the code samples @
http://code.google.com/p/google-meeting-scheduler/

