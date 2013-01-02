package org.jbundle.android.app.test.screen;

import java.util.Hashtable;
import java.util.Map;

import org.jbundle.android.app.test.screen.R;
import org.jbundle.app.test.test.db.TestTable;
import org.jbundle.base.db.Record;
import org.jbundle.base.model.RecordOwner;
import org.jbundle.base.thread.BaseProcess;
import org.jbundle.base.thread.ProcessRunnerTask;
import org.jbundle.base.util.MainApplication;
import org.jbundle.model.App;
import org.jbundle.model.DBException;
import org.jbundle.model.db.Table;
import org.jbundle.model.util.Util;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.EditText;
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
            Table table = setupRemoteThinTable();
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
            EditText textViewName = (EditText)findViewById(R.id.editTextName);
            TestTable record = getRecord();
            String string = record.getField("TestName").getString();
            textViewName.setText(string);
        }
    }
    /**
     * Create the thin table.
     */
    public Table setupRemoteThinTable()
    {
        Record record = null;
//        BaseApplet.main(null);
        String[] args = {"remote=Client", "local=Client", "table=Client", "codebase=www.jbundle.org", "remotehost=www.jbundle.org", "connectionType=proxy"};
//        String[] args = {"remotehost=www.jbundle.org"};
//        args = Test.fixArgs(args);
//        BaseApplet applet = new BaseApplet(args);
        Map<String,Object> properties = null;
        if (args != null)
        {
            properties = new Hashtable<String,Object>();
            Util.parseArgs(properties, args);
        }
        App app = new MainApplication(null, properties, null);
        ProcessRunnerTask task = new ProcessRunnerTask(app, null, null);
        Record recordMain = null;
        RecordOwner recordOwner = new BaseProcess(task, recordMain, properties);

        if (record == null)
            record = new TestTable(recordOwner);

        Table fieldTable = null;
        fieldTable = record.getTable();
        
        return fieldTable;
    }
}