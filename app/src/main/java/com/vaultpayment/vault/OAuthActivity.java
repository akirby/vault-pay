
package com.vaultpayment.vault;

import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.exception.OAuthNotAuthorizedException;

import com.vaultpayment.vault.R;
import com.vaultpayment.vault.lib.OBPRestClient;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.widget.Toast;

public class OAuthActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_oauth);

		/**
		 * There are a couple of things you need to do to get this project set
		 * up:
		 * 
		 * 1) Required: Get values for OBP_AUTH_KEY and OBP_SECRET_KEY, as
		 * described in OBPRestClient.java
		 * 
		 * 2) Optionally (but recommended) change customAppProtocol in
		 * res/values/strings.xml
		 * 
		 * 3) Optionally (but recommended) change the package name of the app
		 * 
		 * Finally, you will want to remove this setup warning exception

		if (1 == 1)
			throw new RuntimeException("Setup required! See OAuthActivity.java");*/

		// Start the main activity if we can get a token we saved earlier
		if (OBPRestClient.setAccessTokenFromSharedPrefs(this))
			startMainActivity();
		else
			getNewAccessToken();
	}

	private void startMainActivity() {
		Intent intent = new Intent(this, MainActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
		startActivity(intent);
	}

	private void getNewAccessToken() {
		new AuthorizeTask(this).execute();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_oauth, menu);
		return true;
	}

	private class AuthorizeTask extends AsyncTask<Void, Void, Uri> {

		private final String LOG_TAG = getClass().getName();
		private Activity callingActivity;
		
		private AuthorizeTask(Activity act) {
			this.callingActivity = act;
		}

		@Override
		protected Uri doInBackground(Void... arg0) {
			// Get the the url the user should be directed to in order to login
			try {
				String urlString = OBPRestClient
						.getAuthoriseAppUrl(callingActivity);
				Uri authoriseURI = Uri.parse(urlString);
				return authoriseURI;
			} catch (OAuthMessageSignerException e) {
				Log.w(LOG_TAG, Log.getStackTraceString(e));
			} catch (OAuthNotAuthorizedException e) {
				Log.w(LOG_TAG, Log.getStackTraceString(e));
			} catch (OAuthExpectationFailedException e) {
				Log.w(LOG_TAG, Log.getStackTraceString(e));
			} catch (OAuthCommunicationException e) {
				Log.w(LOG_TAG, Log.getStackTraceString(e));
			}
			return null;
		}

		@Override
		protected void onPostExecute(Uri authUri) {
			if (authUri != null) {
				/**
				 * We have a url to send the user to, so we will open a web
				 * browser and send them to it. The url we open contains a
				 * parameter specifying the callback url, which we have set up
				 * to be handled in AuthenticateActivity.
				 */
				Intent browser = new Intent(Intent.ACTION_VIEW);
				browser.setData(authUri);

				callingActivity.startActivity(browser);
				callingActivity.finish();
			} else {
				showError();
			}

		}

		private void showError() {
			String OAUTH_ERROR = callingActivity.getResources().getString(
					R.string.oauth_error);
			Toast.makeText(callingActivity, OAUTH_ERROR, Toast.LENGTH_LONG).show();
		}

	}

}
