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

import java.io.IOException;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Service;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import com.SilenceIt.R;
import com.SilenceIt.model.Constants;
import com.google.api.client.googleapis.extensions.android2.auth.GoogleAccountManager;

/**
 * OAuthManager let's the entire application retrieve an OAuth 2.0 access token
 * from the AccountManager.
 */
public class OAuthManager {

  /**
   * An interface for receiving updates once the user has selected the account.
   */
  public interface AuthHandler {
    /**
     * Handle the account being selected.
     * 
     * @param account The selected account or null if none could be found
     * @param authToken The authorization token or null if access has been
     *        denied.
     */
    public void handleAuth(Account account, String authToken);
  }

  /** The chosen account. */
  private Account account;

  /** The most recently fetched auth token or null if none is available. */
  private String authToken;

  /** Singleton instance. */
  private static OAuthManager instance;

  /**
   * Private constructor.
   */
  private OAuthManager() {
  }

  /**
   * Get the singleton instance of AccountChooser.
   * 
   * @return The instance of AccountChooser.
   */
  public static OAuthManager getInstance() {
    if (instance == null) {
      instance = new OAuthManager();
      Log.d(Constants.TAG,"created new instance: " + instance.toString());
    }
    return instance;
  }

  /**
   * Returns the current account.
   * 
   * @return The current account or null if no account has been chosen.
   */
  public Account getAccount() {
    return account;
  }

  /**
   * Returns the current auth token. Response may be null if no valid auth token
   * has been fetched.
   * 
   * @return The current auth token or null if no auth token has been fetched
   */
  public String getAuthToken() {
    return authToken;
  }

  /**
   * Log-in using the preferred account, prompting the user to choose if no
   * preferred account is found.
   * 
   * @param invalidate Whether or not to invalidate the token.
   * @param activity The activity to use to display the prompt, get the
   *        AcountManager instance and SharedPreference instance.
   * @param callback The callback to call when an account and token has been
   *        found.
   */
  public void doLogin(boolean invalidate, Activity activity, AuthHandler callback) {
    if (account != null) {
    	Log.d(Constants.TAG,"account not null: " + account.toString());
      doLogin(account.name, invalidate, activity, callback);
    } else {
      SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(activity);

      doLogin(preference.getString("selected_account_preference", ""), invalidate, activity,
          callback);
    }
  }


  /**
   * Log-in using the specified account name. If an empty account name is
   * provided, a prompt is shown to the user to choose the account to use and
   * saved in the preferences.
   * 
   * @param accountName The account name to use.
   * @param invalidate Whether or not to invalidate the token.
   * @param activity The activity to use to display the prompt, get the
   *        AcountManager instance and SharedPreference instance.
   * @param callback The callback to call when an account and token has been
   *        found.
   */
  public void doLogin(String accountName, boolean invalidate, Activity activity,
      AuthHandler callback) {
    if (account != null && accountName.equals(account.name)) {
    	Log.d(Constants.TAG,"Accc not null & eq acc name");
      if (!invalidate && authToken != null) {
    	  Log.d(Constants.TAG,"invalidate false " + "authToken: " + authToken.toString()); 
        callback.handleAuth(account, authToken);
      } else {
        if (authToken != null && invalidate) {
      	  Log.d(Constants.TAG,"invalidate true " + "authToken: " + authToken.toString()); 
          final AccountManager accountManager = AccountManager.get(activity);
          accountManager.invalidateAuthToken(Constants.ACCOUNT_TYPE, authToken);
          Log.d(Constants.TAG,"authToken after invalidating: " + authToken.toString());
          invalidate = false;
        }
        authorize(account, invalidate, activity, callback);
      }
    } else {
      chooseAccount(accountName, invalidate, activity, callback);
    }
  }
  


  /**
   * Request authorization to call the Calendar API on behalf of the specified
   * account.
   * 
   * @param account The account to request authorization for.
   * @param invalidate Whether or not to invalidate the token.
   * @param context The context to use to retrieve the AccountManager.
   * @param callback The callback to call when a token as been retrieved.
   */
private void authorize(final Account account, final boolean invalidate, final Activity context,
      final AuthHandler callback) {
    final AccountManager accountManager = AccountManager.get(context);
    
    accountManager.getAuthToken(account, Constants.OAUTH_SCOPE,null, true,
        new AccountManagerCallback<Bundle>() {
          @Override
          public void run(AccountManagerFuture<Bundle> future) {
            try {
              Bundle result = future.getResult();

              // AccountManager needs user to grant permission
              if (result.containsKey(AccountManager.KEY_INTENT)) {
            	  Log.d(Constants.TAG,"key intent: " + result.containsKey(AccountManager.KEY_INTENT)); 
                Intent intent = (Intent) result.getParcelable(AccountManager.KEY_INTENT);
                intent.setFlags(intent.getFlags() & ~Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivityForResult(intent, Constants.GET_LOGIN);
                return;
              } else if (result.containsKey(AccountManager.KEY_AUTHTOKEN)) {
            	  Log.d(Constants.TAG,"Key authtoken: " + result.containsKey(AccountManager.KEY_AUTHTOKEN));
                Log.e(Constants.TAG, "Got auth token: " + invalidate);
                authToken = result.getString(AccountManager.KEY_AUTHTOKEN);
                Log.d(Constants.TAG,"account manager authtoken: " + authToken.toString());
                if (invalidate) {
                  // Invalidate the current token and request a new one.
                  accountManager.invalidateAuthToken(Constants.ACCOUNT_TYPE, authToken);
                  authorize(account, false, context, callback);
                } else {
                  // Return the token to the callback.
                	Log.d(Constants.TAG,"return authtoken: " + authToken.toString());
                  callback.handleAuth(account, authToken);
                }
              }
            } catch (OperationCanceledException e) {
              Log.e(Constants.TAG, "Operation Canceled", e);
              callback.handleAuth(null, null);
            } catch (IOException e) {
              Log.e(Constants.TAG, "IOException", e);
              callback.handleAuth(null, null);
            } catch (AuthenticatorException e) {
              Log.e(Constants.TAG, "Authentication Failed", e);
              callback.handleAuth(null, null);
            }
          }
        }, null /* handler */);
    //Log.d(Constants.TAG, "authToken after getAuth: " +accountManager.peekAuthToken(account, Constants.OAUTH_SCOPE));
  }

/**
 * When called from a service: Neena
 * Log-in using the preferred account, prompting the user to choose if no
 * preferred account is found.
 * 
 * @param invalidate Whether or not to invalidate the token.
 * @param activity The activity to use to display the prompt, get the
 *        AcountManager instance and SharedPreference instance.
 * @param callback The callback to call when an account and token has been
 *        found.
 */
public void doLogin(boolean invalidate, Service service, AuthHandler callback) {
	  Log.d(Constants.TAG, "OAuth - doLogin - first");
	  if (account != null) {
		  Log.d(Constants.TAG, "Not Null Account: " + account.toString());
		  doLogin(account.name, invalidate, service, callback);
	  } else {
		  Log.d(Constants.TAG, "Null Account");
		  SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(service);
		  doLogin(preference.getString("selected_account_preference", ""), invalidate, service,
        callback);
	  }
}

/**
 * When called from a service - Neena
 * Log-in using the specified account name. If an empty account name is
 * provided, a prompt is shown to the user to choose the account to use and
 * saved in the preferences.
 * 
 * @param accountName The account name to use.
 * @param invalidate Whether or not to invalidate the token.
 * @param activity The activity to use to display the prompt, get the
 *        AcountManager instance and SharedPreference instance.
 * @param callback The callback to call when an account and token has been
 *        found.
 */
public void doLogin(String accountName, boolean invalidate, Service service,
    AuthHandler callback) {
	Log.d(Constants.TAG, "OAuth - doLogin - second");
	Log.d(Constants.TAG, "account" + account.toString());
	Log.d(Constants.TAG, "accountName: " + accountName.toString());
	Log.d(Constants.TAG, "authToken: " + authToken.toString());
  if (account != null && accountName.equals(account.name)) {
    if (!invalidate && authToken != null) {
  	  Log.d(Constants.TAG, "!invalidate && authToken != null");
      callback.handleAuth(account, authToken);
    } else {
      if (authToken != null && invalidate) {
      	Log.d(Constants.TAG, "authToken != null && invalidate");
        final AccountManager accountManager = AccountManager.get(service);
        accountManager.invalidateAuthToken(Constants.ACCOUNT_TYPE, authToken);
        invalidate = false;
      }
      authorize(account, invalidate, service, callback);
    }
  } else {
  	Log.d(Constants.TAG, "Account null");
  	//chooseAccount(accountName, invalidate, serivce, callback);
      final AlertDialog.Builder builder = new AlertDialog.Builder(service);
      builder.setTitle(R.string.app_name);
      builder.setMessage("Please restart SilenceIt app from the main menu");
      builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
      	  account = null;
        }
      });
      builder.setOnCancelListener(new OnCancelListener() {
        @Override
        public void onCancel(DialogInterface dialog) {
      	  account = null;
        }
      });
      builder.show();
      if (account == null) {
      	Log.d(Constants.TAG, "Account null after builder");
      	callback.handleAuth(null, null);
      }
    }
}

  /**
   * When called from a service: Neena
   * Request authorization to call the Calendar API on behalf of the specified
   * account.
   * 
   * @param account The account to request authorization for.
   * @param invalidate Whether or not to invalidate the token.
   * @param context The context to use to retrieve the AccountManager.
   * @param callback The callback to call when a token as been retrieved.
   */
  @SuppressWarnings("deprecation")
private void authorize(final Account account, final boolean invalidate, final Service context,
      final AuthHandler callback) {
    final AccountManager accountManager = AccountManager.get(context);

    accountManager.getAuthToken(account, Constants.OAUTH_SCOPE, true,
        new AccountManagerCallback<Bundle>() {
          @Override
          public void run(AccountManagerFuture<Bundle> future) {
            try {
            	Log.d(Constants.TAG, "AccountManagerCallback run");
            	Bundle result = future.getResult();
            	if (result.containsKey(AccountManager.KEY_AUTHTOKEN)) {
                Log.e(Constants.TAG, "Got auth token: " + invalidate);
                authToken = result.getString(AccountManager.KEY_AUTHTOKEN);
                if (invalidate) {
                  // Invalidate the current token and request a new one.
                	Log.d(Constants.TAG, "invalidate current and request new");
                  accountManager.invalidateAuthToken(Constants.ACCOUNT_TYPE, authToken);
                  authorize(account, false, context, callback);
                } else {
                  // Return the token to the callback.
                	Log.d(Constants.TAG, "Return the tocken to the callback");
                  callback.handleAuth(account, authToken);
                }
              }
            } catch (OperationCanceledException e) {
              Log.e(Constants.TAG, "Operation Canceled", e);
              callback.handleAuth(null, null);
            } catch (IOException e) {
              Log.e(Constants.TAG, "IOException", e);
              callback.handleAuth(null, null);
            } catch (AuthenticatorException e) {
              Log.e(Constants.TAG, "Authentication Failed", e);
              callback.handleAuth(null, null);
            }
          }
        }, null /* handler */);
  }
  
  /**
   * Prompt the user to choose an account if more than one account is found and
   * none is matching the provided account name.
   * 
   * @param accountName Account name to look for.
   * @param invalidate Whether or not to invalidate the autToken.
   * @param activity The activity to use to display the prompt.
   * @param callback The callback to call when an account and token has been
   *        found.
   */
  private void chooseAccount(String accountName, final boolean invalidate, final Activity activity,
      final AuthHandler callback) {
    final Account[] accounts = new GoogleAccountManager(activity).getAccounts();

    if (accounts.length < 1) {
      callback.handleAuth(null, null);
    } else if (accounts.length == 1) {
      gotAccount(accounts[0], invalidate, activity, callback);
    } else if (accountName != null && accountName.length() > 0) {
      for (Account account : accounts) {
        if (account.name.equals(accountName)) {
          gotAccount(account, invalidate, activity, callback);
          return;
        }
      }
    } else {
      // Let the user choose.
      Log.e(Constants.TAG, "Multiple matching accounts found.");

      // Build dialog.
      String[] choices = new String[accounts.length];
      for (int i = 0; i < accounts.length; i++) {
        choices[i] = accounts[i].name;
      }

      final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
      builder.setTitle(R.string.choose_account_title);
      builder.setItems(choices, new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
          gotAccount(accounts[which], invalidate, activity, callback);
        }
      });
      builder.setOnCancelListener(new OnCancelListener() {
        @Override
        public void onCancel(DialogInterface dialog) {
          callback.handleAuth(null, null);
        }
      });
      builder.show();
    }
  }

  /**
   * Save chosen account into the instance and shared preferences.
   * 
   * @param account Chosen account.
   * @param invalidate Whether or not to invalidate the auth token.
   * @param activity Activity to run thread on.
   * @param callback Method to call when auth token has been retrieved.
   */
  private void gotAccount(Account account, boolean invalidate, Activity activity,
      AuthHandler callback) {
    SharedPreferences.Editor editor =
        PreferenceManager.getDefaultSharedPreferences(activity).edit();
    editor.putString("selected_account_preference", account.name);
    editor.commit();

    this.account = account;

    authorize(account, invalidate, activity, callback);
  }
}