package com.appsrox.remindme;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

public class ShowcaseActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_showcase);
	}
	
	public void onClick(View v) {
		finish();
	}

	@Override
	protected void onPause() {
		RemindMe.setShowcase();
		super.onPause();
	}

}
