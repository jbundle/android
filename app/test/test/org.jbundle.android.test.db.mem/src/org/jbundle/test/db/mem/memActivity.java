package org.jbundle.test.db.mem;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

public class memActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        Button mPickDate = (Button) findViewById(R.id.button1);
        mEditText = (EditText) findViewById(R.id.editText1);
        mCheckBox = (CheckBox) findViewById(R.id.checkBox1);
        // add a click listener to the button
        mPickDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                mEditText.setText("");
                new DBTestTask().execute(mCheckBox.isChecked());
            }
        });
    }

    EditText mEditText = null;
    CheckBox mCheckBox = null;

    private class DBTestTask extends AsyncTask<Boolean, Boolean, String> {
        protected String doInBackground(Boolean... memoryTest) {
            DBTest dbTest = new DBTest(memoryTest[0]);
            String result = dbTest.runTest();
            return result;
        }
    
        protected void onProgressUpdate(Boolean... progress) {
    //        progressBar.setIndeterminate(progress[0]);
        }
    
        protected void onPostExecute(String result) {
            mEditText.setText(result);
        }
    }
}