package kr.codesolutions.gms.gcm;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import kr.codesolutions.gms.R;
import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

public class GCM {

	GoogleCloudMessaging gcm;
    String SERVER_URL;
    String SENDER_ID;

    Activity context;
	Resources resources;

	String registrationId;
	String userId;
	String name;
	String phoneNumber;

	public GCM(Activity context) {
		this.context = context;
		this.resources = context.getResources();
		SERVER_URL = resources.getString(R.string.GMS_SERVER_URL);
		SENDER_ID = resources.getString(R.string.GMS_SENDER_ID);
	}

	public boolean start(String userId, String name) {
		if(userId == null || userId.isEmpty()){
			Log.e(GCMConstants.TAG, "userId is required.");
			return false;
		}
		this.userId = userId;
		this.name = name == null ? "-" : name;
		this.phoneNumber = getPhoneNumber(context);

		NotificationManager NM =
				(NotificationManager)context.getSystemService(Activity.NOTIFICATION_SERVICE);
		NM.cancel(GCMConstants.NOTIFICATION_ID);

		// Check device for Play Services APK. If check succeeds, proceed with
		// GCM registration.
		if (checkPlayServices()) {
			gcm = GoogleCloudMessaging.getInstance(context);
			registrationId = getRegistrationId(context);

			//if (registrationId.isEmpty()) {
				registerInBackground();
			//} else {
				Log.i(GCMConstants.TAG, "registrationId = " + registrationId);
			//}
			Log.i(GCMConstants.TAG, "GCM started");
		} else {
			Log.i(GCMConstants.TAG, "No valid Google Play Services APK found.");
			return false;
		}
		return true;
	}

	public void onDestroy() {
		gcm.close();
		Log.i(GCMConstants.TAG, "GCM destroyed");
	}

	/**
	 * Check the device to make sure it has the Google Play Services APK. If it
	 * doesn't, display a dialog that allows users to download the APK from the
	 * Google Play Store or enable it in the device's system settings.
	 */
	private boolean checkPlayServices() {
		int resultCode = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(context);
		if (resultCode != ConnectionResult.SUCCESS) {
			if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
				GooglePlayServicesUtil.getErrorDialog(resultCode, context,
						GCMConstants.PLAY_SERVICES_RESOLUTION_REQUEST).show();
			} else {
				Log.i(GCMConstants.TAG, "This device is not supported.");
			}
			return false;
		}
		return true;
	}

	/**
	 * Stores the registration ID and the app versionCode in the application's
	 * {@code SharedPreferences}.
	 * 
	 * @param context application's context.
	 * @param registrationId registration ID
	 */
	private void storeRegistrationId(Context context, String registrationId) {
		final SharedPreferences prefs = getGcmPreferences(context);
		int appVersion = getAppVersion(context);
		Log.i(GCMConstants.TAG, "Saving registrationId on app version "
				+ appVersion);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(GCMConstants.PROPERTY_REG_ID, registrationId);
		editor.putInt(GCMConstants.PROPERTY_APP_VERSION, appVersion);
		editor.commit();
	}

	/**
	 * Gets the current registration ID for application on GCM service, if there
	 * is one.
	 * <p>
	 * If result is empty, the app needs to register.
	 * 
	 * @return registration ID, or empty string if there is no existing
	 *         registration ID.
	 */
	public String getRegistrationId(Context context) {
		final SharedPreferences prefs = getGcmPreferences(context);
		String registrationId = prefs.getString(GCMConstants.PROPERTY_REG_ID,
				"");
		if (registrationId.isEmpty()) {
			Log.i(GCMConstants.TAG, "Registration not found.");
			return "";
		}
		// Check if app was updated; if so, it must clear the registration ID
		// since the existing registrationId is not guaranteed to work with the
		// new app version.
		int registeredVersion = prefs.getInt(GCMConstants.PROPERTY_APP_VERSION,
				Integer.MIN_VALUE);
		int currentVersion = getAppVersion(context);
		if (registeredVersion != currentVersion) {
			Log.i(GCMConstants.TAG, "App version changed.");
			unregisterInBackground();
			return "";
		}
		return registrationId;
	}

   /**
     * Registers the application with GCM servers asynchronously.
     * <p>
     * Stores the registration ID and the app versionCode in the application's
     * shared preferences.
     */
    private void registerInBackground() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
					if (gcm == null) {
						gcm = GoogleCloudMessaging.getInstance(context);
					}
					registrationId = gcm.register(SENDER_ID);
					Log.i(GCMConstants.TAG, "Device registered, registration ID="	+ registrationId);
		
					// You should send the registration ID to your server over HTTP, so
					// it can use GCM/HTTP or CCS to send messages to your app.
					registerOnServer(registrationId, userId, name, phoneNumber);
		
					// Persist the registrationId - no need to register again.
					storeRegistrationId(context, registrationId);
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
            	if(!msg.isEmpty()){
	        		Intent intent = new Intent(GCMConstants.ERROR_MESSAGE_ACTION);
	        		intent.putExtra(GCMConstants.GMS_ERRORCODE, GCMConstants.GMS_ERROR_REGISTOR_FAILED);
	        		intent.putExtra(GCMConstants.GMS_ERROR, msg);
	        		context.sendBroadcast(intent);
            	}
            }
        }.execute(null, null, null);
    }
    
    private void unregisterInBackground() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
					// You should send the registration ID to your server over HTTP
					unregisterOnServer(registrationId);
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
            	if(!msg.isEmpty()){
	        		Intent intent = new Intent(GCMConstants.ERROR_MESSAGE_ACTION);
	        		intent.putExtra(GCMConstants.GMS_ERRORCODE, GCMConstants.GMS_ERROR_REGISTOR_FAILED);
	        		intent.putExtra(GCMConstants.GMS_ERROR, msg);
	        		context.sendBroadcast(intent);
            	}
            }
        }.execute(null, null, null);
    }

	/**
	 * @return Application's version code from the {@code PackageManager}.
	 */
	public int getAppVersion(Context context) {
		try {
			PackageInfo packageInfo = context.getPackageManager()
					.getPackageInfo(context.getPackageName(), 0);
			return packageInfo.versionCode;
		} catch (NameNotFoundException e) {
			// should never happen
			throw new RuntimeException("Could not get package name: " + e);
		}
	}

	public String getPhoneNumber(Context context) {
		TelephonyManager telephonyManager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
	    String phoneNumber = telephonyManager.getLine1Number();
		Log.d(GCMConstants.TAG, "PhoneNumber : " + phoneNumber);
	    
	    return (phoneNumber==null || phoneNumber.isEmpty())?"-":phoneNumber;
	}	
	/**
	 * @return Application's {@code SharedPreferences}.
	 */
	public SharedPreferences getGcmPreferences(Context context) {
		// This sample app persists the registration ID in shared preferences,
		// but how you store the registrationId in your app is up to you.
		return context.getSharedPreferences(context.getPackageName(),
				Context.MODE_PRIVATE);
	}

	/**
	 * Sends the registration ID to your server over HTTP, so it can use
	 * GCM/HTTP or CCS to send messages to your app. Not needed for this demo
	 * since the device sends upstream messages to a server that echoes back the
	 * message using the 'from' address in the message.
	 */
	public void registerOnServer(String registrationId, String userId,
			String name, String phoneNumber) throws IOException {
		Log.i(GCMConstants.TAG, "registering device (registrationId = "
				+ registrationId + ")");
		String serverUrl = SERVER_URL + "/gms/user/register";
		Map<String, String> params = new HashMap<String, String>();
		params.put("registrationId", registrationId);
		params.put("userId", userId);
		params.put("name", name);
		params.put("phoneNumber", phoneNumber);
		// Once GCM returns a registration id, we need to register it in the
		// server
		Log.d(GCMConstants.TAG, "Attempt to register");
		try {
			post(serverUrl, params);
			Log.i(GCMConstants.TAG, "From GMS Server: successfully added device!");
		} catch (IOException e) {
			// Here we are simplifying and retrying on any error; in a real
			// application, it should retry only on unrecoverable errors
			// (like HTTP error code 503).
			Log.e(GCMConstants.TAG, "Failed to register on attempt :" + e);
			throw e;
		}
	}
	
	/**
	 * App버전이 바뀐경우 메시지 수신에 실패할 수 있으므로 기 등록된 registrationId를 해제하고
	 * 새로 발급받은 registrationId를 등록한다. 
	 * 
	 * @param registrationId
	 * @param userId
	 * @throws IOException
	 */
	public void unregisterOnServer(String registrationId) throws IOException {
		Log.i(GCMConstants.TAG, "unregistering device (registrationId = "
				+ registrationId + ")");
		String serverUrl = SERVER_URL + "/gms/user/unregister";
		Map<String, String> params = new HashMap<String, String>();
		params.put("registrationId", registrationId);
		Log.d(GCMConstants.TAG, "Attempt to unregister");
		try {
			post(serverUrl, params);
			Log.i(GCMConstants.TAG, "From GMS Server: successfully removed device!");
		} catch (IOException e) {
			Log.e(GCMConstants.TAG, "Failed to unregister on attempt :" + e);
			throw e;
		}
	}

	/**
	 * 수신받은 메시지에 대해 수신확인 메시지를 서버로 보낸다.
	 * 
	 * @param registrationId
	 * @param messageId
	 * @param ownType ('0':개인메시지, '1':공지메시지)
	 * @throws IOException
	 */
	public void readOnServer(String registrationId, String messageId, String ownType) throws IOException {
		Log.i(GCMConstants.TAG, "reading message (messageId = "
				+ messageId + ")");
		String serverUrl = SERVER_URL + "/gms/message/read";
		Map<String, String> params = new HashMap<String, String>();
		params.put("registrationId", registrationId);
		params.put("id", messageId);
		params.put("ownType", ownType);
		Log.d(GCMConstants.TAG, "Attempt to read");
		try {
			post(serverUrl, params);
			Log.i(GCMConstants.TAG, "From GMS Server: successfully read a message!");
		} catch (IOException e) {
			Log.e(GCMConstants.TAG, "Failed to read on attempt :" + e);
			throw e;
		}
	}

	/**
	 * Issue a POST request to the server.
	 * 
	 * @param endpoint POST address.
	 * @param params request parameters.
	 * @throws IOException propagated from POST.
	 */
	private static void post(String endpoint, Map<String, String> params)
			throws IOException {
		URL url;
		try {
			url = new URL(endpoint);
		} catch (MalformedURLException e) {
			throw new IllegalArgumentException("invalid url: " + endpoint);
		}
		StringBuilder bodyBuilder = new StringBuilder();
		Iterator<Entry<String, String>> iterator = params.entrySet().iterator();
		// constructs the POST body using the parameters
		while (iterator.hasNext()) {
			Entry<String, String> param = iterator.next();
			bodyBuilder.append(param.getKey()).append('=')
					.append(param.getValue());
			if (iterator.hasNext()) {
				bodyBuilder.append('&');
			}
		}
		String body = bodyBuilder.toString();
		Log.v(GCMConstants.TAG, "Posting '" + body + "' to " + url);
		byte[] bytes = body.getBytes();
		HttpURLConnection conn = null;
		try {
			conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(3000);
			conn.setDoOutput(true);
			conn.setUseCaches(false);
			conn.setFixedLengthStreamingMode(bytes.length);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded;charset=UTF-8");
			// post the request
			OutputStream out = conn.getOutputStream();
			out.write(bytes);
			out.close();
			// handle the response
			int status = conn.getResponseCode();
			if (status != 200) {
				throw new IOException("Post failed with error code " + status);
			}
		} finally {
			if (conn != null) {
				conn.disconnect();
			}
		}
	}

	/*
	// Send an upstream message.
	public void sendMessage(final String message) {

		new AsyncTask<Void, Void, String>() {
			@Override
			protected String doInBackground(Void... params) {
				String msg = "";
				try {
					Bundle data = new Bundle();
					data.putString("message", message);
					data.putString("action", "kr.codesolutions.gms.ECHO_NOW");
					String id = Integer.toString(msgId.incrementAndGet());
					gcm.send(GCMConstants.SENDER_ID + "@gcm.googleapis.com",
							id, data);
					msg = "Sent message";
				} catch (IOException ex) {
					msg = "Error :" + ex.getMessage();
				}
				return msg;
			}

			@Override
			protected void onPostExecute(String msg) {
				Log.i(GCMConstants.TAG, msg);
			}
		}.execute(null, null, null);
	}
	*/
}
