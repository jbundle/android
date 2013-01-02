package org.jbundle.android.app.test.thin;

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

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class TestscreenActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        EditText textViewCode = (EditText)findViewById(R.id.editTextCode);
        textViewCode.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {
//?            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                // TODO Auto-generated method stub
                return false;
            }
            
        });
        textViewCode.setOnFocusChangeListener(new OnFocusChangeListener()
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
    }

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
        	Resources res = getResources();
            EditText textViewName = (EditText)findViewById(R.id.editTextName);
            TestTable record = getRecord();
            String string = record.getField("TestName").getString();
            textViewName.setText(string);

            ImageView imageView = (ImageView)findViewById(R.id.imageView1);
            PortableImage image = (PortableImage)record.getField("TestImage").getData();
            Bitmap bm = null;
            if (image != null)
            	bm = (Bitmap)image.getImage();
            else
            	bm = BitmapFactory.decodeResource(res, R.id.imageView1);
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