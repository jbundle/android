package com.jbundle.android.app.test.testnav;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        Button button = (Button)findViewById(R.id.button1);
        button.setOnClickListener(new OnClickListener()
        {
			@Override
			public void onClick(View v) {
				// Map point based on address
				Uri location = Uri.parse("geo:0,0?q=1600+Amphitheatre+Parkway,+Mountain+View,+California");
				// Or map point based on latitude/longitude
				// Uri location = Uri.parse("geo:37.422219,-122.08364?z=14"); // z param is zoom level
				Intent mapIntent = new Intent(Intent.ACTION_VIEW, location);				// TODO Auto-generated method stub

				// Verify it resolves
				PackageManager packageManager = getPackageManager();
				List<ResolveInfo> activities = packageManager.queryIntentActivities(mapIntent, 0);
				boolean isIntentSafe = activities.size() > 0;
				ResolveInfo defaultActivity = null;
				for (ResolveInfo activity : activities) {
					if ("com.google.android.apps.maps".equals(activity.activityInfo.applicationInfo.packageName))
						defaultActivity = activity;
					if (activity.isDefault)
						defaultActivity = activity;
				}
				if (defaultActivity != null)
					mapIntent.setPackage(defaultActivity.activityInfo.applicationInfo.packageName);
				  
				// Start an activity if it's safe
				if (isIntentSafe) {
				    startActivity(mapIntent);
				}				
			}
        	
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    
}
