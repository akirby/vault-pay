package com.vaultpayment.vault;

import com.vaultpayment.vault.R;
import com.vaultpayment.vault.lib.OBPRestClient;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.widget.Toast;

public class AuthenticateActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		tryToLaunchMainActivity(getIntent());

		setContentView(R.layout.activity_authenticate);
	}

	public void getAccessToken(String verifyCode) {
		new AccessTokenTask(this).execute(verifyCode);
	}

	private class AccessTokenTask extends AsyncTask<String, Void, Boolean> {

		private Activity callingActivity;
		
		private AccessTokenTask(Activity act) {
			this.callingActivity = act;
		}
		
		@Override
		protected Boolean doInBackground(String... s) {
			if (s.length != 1) {
				return false;
			} else {
				String verifierCode = s[0];
				boolean accessTokenSet = OBPRestClient.getAndSetAccessToken(
						callingActivity, verifierCode);
				return accessTokenSet;
			}
		}

		@Override
		protected void onPostExecute(Boolean success) {
			if (success) {
				launchMainActivity();
			} else
				Toast.makeText(callingActivity, "Incorrect verification code!",
						Toast.LENGTH_LONG).show();
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_enter_verifier, menu);
		return true;
	}

	private void launchMainActivity() {
		Intent mainActivity = new Intent(this, MainActivity.class);
		startActivity(mainActivity);
		finish();
	}

	private void tryToLaunchMainActivity(Intent intent) {
		// Check if we've got the verifier code available
		Uri data = intent.getData();
		if (data != null) {
			String code = data.getQueryParameter("oauth_verifier");
			if (code != null) {
				getAccessToken(code);
			}
		} else {
			// Something weird has happened, so we'll try starting the OAuth
			// cycle again
			Intent i = new Intent(this, OAuthActivity.class);
			startActivity(i);
			finish();
		}
	}

	@Override
	/**
	 * This gets called when the browser api login redirects the user to the custom url scheme
	 * handled by this activity (see AndroidManifest.xml: <data android:scheme="@string/customAppProtocol"/>)
	 */
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		tryToLaunchMainActivity(intent);
	}

}
