
package com.vaultpayment.vault.lib;

import java.io.IOException;
import java.net.MalformedURLException;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;
import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthProvider;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.exception.OAuthNotAuthorizedException;
import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import com.vaultpayment.vault.R;

public class OBPRestClient {

	/**
	 * To get the values for the following fields, please register your client
	 * here:
	 * 
	 * https://apisandbox.openbankproject.com/consumer-registration
	 * 
	 * Note: If you intend to use a different api instance (i.e. not the sandbox
	 * data), you will need to visit /consumer-registration for that api
	 * instance. You will also need to change the value of BASE_URL.
	 */
	private static final String OBP_AUTH_KEY = "Put your Open Bank Proj. Auth Key";
	private static final String OBP_SECRET_KEY = "Put your OBP Secret Key";
	
	private static final String BASE_URL = "https://citizensbank.openbankproject.com";
	//HSBC API
    //private static final String BASE_URL="https://openlab.openbankproject.com";

	private static final String REQUEST_TOKEN_URL = calcFullPath("/oauth/initiate");
	private static final String ACCESS_TOKEN_URL = calcFullPath("/oauth/token");
	private static final String AUTHORIZE_WEBSITE_URL = calcFullPath("/oauth/authorize");

	private static final String PREF_FILE = "OBP_API_PREFS";
	private static final String CONSUMER_TOKEN = "CONSUMER_TOKEN";
	private static final String CONSUMER_SECRET = "CONSUMER_SECRET";
	private static final String PREF_NOT_SET = "";

	private static final String LOG_TAG = OBPRestClient.class.getClass()
			.getName();

	private static OAuthConsumer consumer = new CommonsHttpOAuthConsumer(
			OBP_AUTH_KEY, OBP_SECRET_KEY);

	private static OAuthProvider provider = new CommonsHttpOAuthProvider(
			REQUEST_TOKEN_URL, ACCESS_TOKEN_URL, AUTHORIZE_WEBSITE_URL);

	private static String calcFullPath(String relativePath) {
		return BASE_URL + relativePath;
	}

	public static String getAuthoriseAppUrl(Context context)
			throws OAuthMessageSignerException, OAuthNotAuthorizedException,
			OAuthExpectationFailedException, OAuthCommunicationException {

		/**
		 * You will want to set customProtocol to something unique to avoid
		 * having Android give the user more than one app to pick from to handle
		 * the oauth callback. A good option would be to use something derived
		 * from your package name.
		 */
		String customProtocol = context.getResources().getString(
				R.string.customAppProtocol);

		return provider.retrieveRequestToken(consumer, customProtocol
				+ "://oauth");
	}

	/**
	 * @return true if the access token was loaded and set from shared
	 *         preferences
	 */
	public static boolean setAccessTokenFromSharedPrefs(Activity activity) {
		SharedPreferences settings = activity
				.getSharedPreferences(PREF_FILE, 0);
		String token = settings.getString(CONSUMER_TOKEN, PREF_NOT_SET);
		String secret = settings.getString(CONSUMER_SECRET, PREF_NOT_SET);

		boolean exists = !token.equals(PREF_NOT_SET)
				&& !secret.equals(PREF_NOT_SET);
		// set it if it exists
		if (exists)
			consumer.setTokenWithSecret(token, secret);

		return exists;
	}

	public static boolean clearAccessToken(Activity activity) {
		Editor editor = activity.getSharedPreferences(PREF_FILE, 0).edit();
		editor.putString(CONSUMER_TOKEN, PREF_NOT_SET);
		editor.putString(CONSUMER_SECRET, PREF_NOT_SET);
		consumer.setTokenWithSecret("", "");
		return editor.commit();
	}

	public static boolean getAndSetAccessToken(Activity activity,
			String verifyCode) {
		try {
			provider.retrieveAccessToken(consumer, verifyCode);
			String token = consumer.getToken();
			String secret = consumer.getTokenSecret();
			if (token != null && secret != null) {
				Editor editor = activity.getSharedPreferences(PREF_FILE,
						Activity.MODE_PRIVATE).edit();
				editor.putString(CONSUMER_TOKEN, token);
				editor.putString(CONSUMER_SECRET, secret);
				return editor.commit();
			} else
				return false;
		} catch (OAuthMessageSignerException e) {
			Log.w(LOG_TAG, Log.getStackTraceString(e));
		} catch (OAuthNotAuthorizedException e) {
			Log.w(LOG_TAG, Log.getStackTraceString(e));
		} catch (OAuthExpectationFailedException e) {
			Log.w(LOG_TAG, Log.getStackTraceString(e));
		} catch (OAuthCommunicationException e) {
			Log.w(LOG_TAG, Log.getStackTraceString(e));
		}
		return false;
	}

	public static JSONObject getOAuthedJson(String urlString)
			throws ExpiredAccessTokenException, ObpApiCallFailedException {

		try {
			HttpClient httpclient = new DefaultHttpClient();
			HttpGet request = new HttpGet(urlString);
			consumer.sign(request);
			org.apache.http.HttpResponse response = httpclient.execute(request);
			HttpEntity entity = response.getEntity();

			int statusCode = response.getStatusLine().getStatusCode();

			switch (statusCode) {
			case 200:
			case 201:
				return parseJsonResponse(entity);
			case 401:
				try {
					JSONObject responseJson = parseJsonResponse(entity);
					if (responseJson.optString("error").contains(
							consumer.getToken())) {
						// We have an expired access token (probably?)
						throw new ExpiredAccessTokenException();
					} else {
						// It wasn't (probably?) an expired token error
						Log.w(LOG_TAG, responseJson.toString());
						throw new ObpApiCallFailedException();
					}
				} catch (JSONException e) {
					// Api response wasn't json -> unexpected
					Log.w(LOG_TAG, Log.getStackTraceString(e));
					throw new ObpApiCallFailedException();
				}
			default:
				throw new ObpApiCallFailedException();
			}
		} catch (MalformedURLException e) {
			Log.w(LOG_TAG, Log.getStackTraceString(e));
			throw new ObpApiCallFailedException();
		} catch (OAuthMessageSignerException e) {
			Log.w(LOG_TAG, Log.getStackTraceString(e));
			throw new ObpApiCallFailedException();
		} catch (OAuthExpectationFailedException e) {
			Log.w(LOG_TAG, Log.getStackTraceString(e));
			throw new ObpApiCallFailedException();
		} catch (OAuthCommunicationException e) {
			Log.w(LOG_TAG, Log.getStackTraceString(e));
			throw new ObpApiCallFailedException();
		} catch (ClientProtocolException e) {
			Log.w(LOG_TAG, Log.getStackTraceString(e));
			throw new ObpApiCallFailedException();
		} catch (IOException e) {
			Log.w(LOG_TAG, Log.getStackTraceString(e));
			throw new ObpApiCallFailedException();
		} catch (JSONException e) {
			Log.w(LOG_TAG, Log.getStackTraceString(e));
			throw new ObpApiCallFailedException();
		} catch (ObpApiCallFailedException e) {
			Log.w(LOG_TAG, Log.getStackTraceString(e));
			throw new ObpApiCallFailedException();
		}

	}

	private static JSONObject parseJsonResponse(HttpEntity entity)
			throws ParseException, JSONException, IOException {
		return new JSONObject(EntityUtils.toString(entity));
	}



}
