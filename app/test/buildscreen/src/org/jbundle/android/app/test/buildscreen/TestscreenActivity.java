package org.jbundle.android.app.test.buildscreen;

import java.util.Hashtable;
import java.util.Map;

import org.jbundle.model.App;
import org.jbundle.model.DBException;
import org.jbundle.model.util.PortableImage;
import org.jbundle.model.util.Util;
import org.jbundle.thin.app.test.test.db.TestTable;
import org.jbundle.thin.base.db.FieldList;
import org.jbundle.thin.base.db.FieldTable;
import org.jbundle.thin.base.db.client.RemoteFieldTable;
import org.jbundle.thin.base.remote.RemoteTable;
import org.jbundle.thin.base.remote.RemoteTask;
import org.jbundle.thin.base.util.Application;

import android.R.attr;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class TestscreenActivity extends Activity {
	EditText editTextCode = null;
	EditText editTextName = null;
	EditText editTextID = null;
	ImageView imageView = null;
	int imageViewId = 0;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        int id = 100;
        Context context = getBaseContext();
        
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        setContentView(layout);
        layout.setId(id++);

        TextView textView = new TextView(context);
        textView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        textView.setText("Code");
        textView.setTextAppearance(context, attr.textAppearanceMedium);
        textView.setId(id++);
        layout.addView(textView);

        editTextCode = new EditText(context);
        editTextCode.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        editTextCode.setId(id++);
        layout.addView(editTextCode);
        editTextCode.requestFocus();
        editTextCode.setOnFocusChangeListener(new OnFocusChangeListener()
        {
//?            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    String string = ((EditText)v).getText().toString();
                    Log.i("tag", "lost focus, value = " + string);
                    new ReadRecordTask().execute(string);
                }
            }
        });

        TextView textView2 = new TextView(context);
        textView2.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        textView2.setText("Name");
        textView2.setTextAppearance(context, attr.textAppearanceMedium);
        textView2.setId(id++);
        layout.addView(textView2);
        
        editTextName = new EditText(context);
        editTextName.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        editTextName.setId(id++);
        layout.addView(editTextName);

        TextView textView3 = new TextView(context);
        textView3.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        textView3.setText("ID");
        textView3.setTextAppearance(context, attr.textAppearanceMedium);
        textView3.setId(id++);
        layout.addView(textView3);
        
        editTextID = new EditText(context);
        editTextID.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        editTextID.setId(id++);
        layout.addView(editTextID);

        TextView textView4 = new TextView(context);
        textView4.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        textView4.setText("Image");
        textView4.setTextAppearance(context, attr.textAppearanceMedium);
        textView4.setId(id++);
        layout.addView(textView4);
        
        imageView = new ImageView(context);
        imageView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        imageViewId = id;
        imageView.setId(id++);
        
        Drawable drawable = null; //new Drawable();
        //android:src="@drawable/ic_launcher" />
        imageView.setImageDrawable(drawable);
        layout.addView(imageView);

//        RemoteViews views = new RemoteViews(context.getPackageName(),100);
//        views.setTextViewText(R.id.widget_control1, value1);
//        views.setTextViewText(R.id.widget_control2, value2);        
/*        setContentView(R.layout.main);
        
        this.findViewById(R.layout);
        EditText textView = new EditText(this);
        textView.setText("this is a test");
        this.addContentView(textView, null);
        
*/    }

    protected TestTable getRecord()
    {
        if (testTable == null)
        {
            FieldTable table = setupRemoteThinTable();
            if (table != null) // Always
                if (table.getRecord() instanceof TestTable) // Always
                    testTable = (TestTable)table.getRecord();
        }
        return testTable;
    }
    private TestTable testTable = null;
    
    private class ReadRecordTask extends AsyncTask<String, Boolean, Boolean> {
        protected Boolean doInBackground(String... key) {
            boolean found = false;
            TestTable record = getRecord();
            try {
                record.initRecord(true);
                record.setKeyArea("TestCode");
                record.getField("TestCode").setString(key[0]);
                found = record.getTable().seek(null);
            } catch (DBException e) {
                e.printStackTrace();
            }
            return found;
        }
    
        protected void onProgressUpdate(Boolean... progress) {
    //        progressBar.setIndeterminate(progress[0]);
        }
    
        protected void onPostExecute(Boolean result) {
//            mEditText.setText(result);
//        	Resources res = getResources();
            TestTable record = getRecord();
            String string = record.getField("TestName").getString();
            editTextName.setText(string);

            PortableImage image = (PortableImage)record.getField("TestImage").getData();
            Bitmap bm = null;
            if (image != null)
            	bm = (Bitmap)image.getImage();
//            else
//            	bm = BitmapFactory.decodeResource(res, imageViewId);
            imageView.setImageBitmap(bm);
        }
    }
    /**
     * Create the thin table.
     */
    public FieldTable setupRemoteThinTable()
    {
        FieldList record = null;
//        BaseApplet.main(null);
//        String[] args = {"remote=Client", "local=Client", "table=Client", "codebase=www.jbundle.org", "remotehost=www.jbundle.org", "connectionType=proxy"};
//        String[] args = {"remotehost=www.jbundle.org"};
        String[] args = {"remotehost=192.168.1.70:8181"};
//        args = Test.fixArgs(args);
//        BaseApplet applet = new BaseApplet(args);
        Map<String,Object> properties = null;
        if (args != null)
        {
            properties = new Hashtable<String,Object>();
            Util.parseArgs(properties, args);
        }
        App app = new Application(null, properties, null);

        if (record == null)
            record = new TestTable(null);

        FieldTable fieldTable = null;
        RemoteTask server = null;
        RemoteTable remoteTable = null;
        try   {
            server = (RemoteTask)app.getRemoteTask(null);
            Map<String, Object> dbProperties = app.getProperties();
            remoteTable = server.makeRemoteTable(record.getRemoteClassName(), null, null, dbProperties);
//?                    remoteTable = new CachedRemoteTable(remoteTable);
        } catch (Exception ex)  {
            ex.printStackTrace();
        }
        fieldTable = new RemoteFieldTable(record, remoteTable, server);
        
        return fieldTable;
    }
}
