package org.jbundle.android.util.biorhythm;

import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Display;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.Toast;

public class BiorhythmActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        // capture our View elements
        view = (View) findViewById(R.id.view1);
        
        controller = view.getController();
        
        SharedPreferences preferences = this.getPreferences(MODE_PRIVATE);
        ((LegendController)controller).setPreferences(preferences);
        
        gestureDetector = new GestureDetector(new MyGestureDetector());
    
        if (preferences.getLong("birthdate", 0l) == 0l)
            new DisplayToastTask().execute(7);
    }

    private class DisplayToastTask extends AsyncTask<Integer, Boolean, Long> {
        protected Long doInBackground(Integer... waitSeconds) {
            synchronized (Thread.currentThread())
            {
                try {
                    Thread.currentThread().wait(waitSeconds[0] * 1000);
                } catch (InterruptedException e) {
                }
            }
            return Long.MAX_VALUE;
        }

        protected void onPostExecute(Long result) {
            Context context = getApplicationContext();
            CharSequence text = view.getResource().getString("EnterBirthdate");
            int duration = Toast.LENGTH_LONG;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }
    }

    class MyGestureDetector extends SimpleOnGestureListener {
        @Override
        public boolean onScroll (MotionEvent e1, MotionEvent e2, float distanceX, float distanceY)
        {
            try {
                if (Math.abs(e1.getX() - e2.getX()) > SWIPE_MIN_DISTANCE)
                {
                    Date startDate = controller.getStartDate();
                    Date endDate = controller.getEndDate();
                    Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
                    int width = display.getWidth();
                    float flingPercent = (e1.getX() - e2.getX()) / width;
                    long timeChange = endDate.getTime() - startDate.getTime();
                    startDate = new Date((long)(startDate.getTime() + timeChange * flingPercent));
                    endDate = new Date((long)(endDate.getTime() + timeChange * flingPercent));
                    controller.setEndDate(endDate);
                    controller.setStartDate(startDate);
                    return true;
                }
            } catch (Exception e) {
                // nothing
            }
            return false;
        }
        @Override
        public boolean onSingleTapConfirmed(MotionEvent e)
        {
            boolean success = super.onSingleTapConfirmed(e);
            showDialog(DATE_DIALOG_ID);
            return success;
        }
    }
    
    private static final int SWIPE_MIN_DISTANCE = 40;
    private GestureDetector gestureDetector;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (gestureDetector.onTouchEvent(event))
            return true;
        else
            return false;
    }
    // the callback received when the user "sets" the date in the dialog
    private DatePickerDialog.OnDateSetListener mDateSetListener =
            new DatePickerDialog.OnDateSetListener() {

                public void onDateSet(DatePicker view, int year, 
                                      int monthOfYear, int dayOfMonth) {
                    Calendar c = Calendar.getInstance();
                    c.set(Calendar.YEAR, year);
                    c.set(Calendar.MONTH, monthOfYear);
                    c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    c.set(Calendar.HOUR_OF_DAY, 12);
                    c.set(Calendar.MINUTE, 0);
                    c.set(Calendar.SECOND, 0);
                    c.set(Calendar.MILLISECOND, 0);
                    Date dateBirth = c.getTime();
                    controller.setBirthdate(dateBirth);
                }
            };
            
            @Override
            protected Dialog onCreateDialog(int id) {
                switch (id) {
                case DATE_DIALOG_ID:
                    // get the current date
                    Date birthdate = controller.getBirthdate();
                    Calendar c = Calendar.getInstance();
                    c.setTime(birthdate);
                    int mYear = c.get(Calendar.YEAR);
                    int mMonth = c.get(Calendar.MONTH);
                    int mDay = c.get(Calendar.DAY_OF_MONTH);

                    return new DatePickerDialog(this,
                                mDateSetListener,
                                mYear, mMonth, mDay);
                }
                return null;
            }
            
    private Controller controller = null;
    private View view = null;
    
    static final int DATE_DIALOG_ID = 0;
}