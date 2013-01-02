package org.jbundle.android.util.biorhythm;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.ResourceBundle;
import java.util.SimpleTimeZone;
import java.util.TimeZone;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.Display;
import android.view.WindowManager;

public class View extends android.view.View
    implements Constants
{

    public View(Context context) {
        super(context);
        this.init(this.fakeController(context));
    }
    public View(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.init(this.fakeController(context));
    }
    public View(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.init(this.fakeController(context));
    }

    public static int MINIMUM_DAY_WIDTH = 30;
    public Controller fakeController(Context context)
    {
        Model model = new Model(this);

        // get the supported ids for GMT-08:00 (Pacific Standard Time)
        String[] ids = TimeZone.getAvailableIDs(-8 * 60 * 60 * 1000);
        // if no ids were returned, something is wrong. get out.
        if (ids.length == 0)
            System.exit(0);
     // create a Pacific Standard Time time zone
        SimpleTimeZone pdt = new SimpleTimeZone(-8 * 60 * 60 * 1000, ids[0]);

        // set up rules for daylight savings time
        pdt.setStartRule(Calendar.APRIL, 1, Calendar.SUNDAY, 2 * 60 * 60 * 1000);
        pdt.setEndRule(Calendar.OCTOBER, -1, Calendar.SUNDAY, 2 * 60 * 60 * 1000);

        // create a GregorianCalendar with the Pacific Daylight time zone
        // and the current date and time
        Calendar calendar = new GregorianCalendar(pdt);
        Date trialTime = new Date();
        calendar.setTime(trialTime);
        
        Date today = new Date();
        Date dateBirth = calendar.getTime();
        
        Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        int width = display.getWidth();
        calendar.setTime(today);
        Date dateStart = calendar.getTime();
        Date dateEnd = calendar.getTime();
        if ((width == 0) || (width / MINIMUM_DAY_WIDTH >= 30))
        {   // If I have enough space, display the whole month
            calendar.set(Calendar.DATE, 1);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            dateStart = calendar.getTime();
            calendar.add(Calendar.MONTH, 1);
            calendar.add(Calendar.SECOND, -1);
            dateEnd = calendar.getTime();
        }
        else
        {   // Otherwise display a range that will clearly display each day
            int gridLines = width / MINIMUM_DAY_WIDTH;
            calendar.add(Calendar.DATE, -(gridLines / 2));
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            dateStart = calendar.getTime();
            calendar.add(Calendar.DATE, gridLines);
            calendar.add(Calendar.SECOND, -1);
            dateEnd = calendar.getTime();            
        }
        Controller controller = this.createController(context, model, dateBirth, dateStart, dateEnd);
        return controller;
    }
    
    Controller createController(Context context, Model model, Date dateBirth, Date dateStart, Date dateEnd)
    {
        return new Controller(context, model, dateBirth, dateStart, dateEnd);
    }
    
    /**
     * Controller that this view is connected to.
     */
    protected Controller m_controller = null;

    /**
     * Creates new View.
     * Remember to set the background color after insatiating this object.
     * @param controller Controller that this view is connected to.
     */
    public View(Controller controller)
    {
        this((Context)null);    //+
        this.init(controller);
    }
    /**
     * Creates new View.
     * @param controller Controller that this view is connected to.
     */
    public void init(Controller controller)
    {
        this.setController(controller);
//        this.setOpaque(false);      // Make this transparent (by default)
    }
    /**
     * Set the controller for this view.
     * @param controller Controller that this view is connected to.
     */
    public void setController(Controller controller)
    {
        m_controller = controller;
    }
    /**
     * Get the controller for this view.
     * @return Controller that this view is connected to.
     */
    public Controller getController()
    {
        return m_controller;
    }
    /**
     * Paint this portion of the panel (overidden from awt).
     * @param g Graphics object to paint to.
     */
//    public void paint(Graphics g)
//    {
//    }
    protected void onDraw(Canvas canvas) {
        this.drawWaves(canvas);
//        float cx = 5;
  //      float cy = 5;
    //    float radius = 20;
      //  Paint paint = new Paint();
        //paint.setColor(Color.RED);
        //canvas.drawCircle(cx, cy, radius, paint);
    }
    /**
     * Draw the sine waves.
     * @param g Graphics object to paint to.
     */
    private void drawWaves(Canvas canvas)
    {
        int iWidth = this.getWidth();
        for (int x = 0 ; x < iWidth ; x++)
        {
            
            Paint paint = this.getPaint();
            paint.setStrokeWidth(7);
            paint.setColor(this.getElementColor(PHYSICAL_CYCLE));
            this.drawGraphLine(canvas, x, PHYSICAL_CYCLE, paint);
            paint.setColor(this.getElementColor(EMOTIONAL_CYCLE));
            this.drawGraphLine(canvas, x, EMOTIONAL_CYCLE, paint);
            paint.setColor(this.getElementColor(INTELLECTUAL_CYCLE));
            this.drawGraphLine(canvas, x, INTELLECTUAL_CYCLE, paint);
        }
    }
    /**
     * Draw a line from x to x+1 on the canvas.
     * @param g Graphics object to paint to.
     * @param x x value to use to compute the Y value
     * @param iCycleLength Length of the cycle to compute
     */
    public void drawGraphLine(Canvas canvas, int x, int iCycleLength, Paint paint)
    {
        if (paint.getStrokeWidth() <= 1)
            canvas.drawLine(x, this.convertXtoY(x, iCycleLength), x + 1, this.convertXtoY(x + 1, iCycleLength), paint);
        else
            canvas.drawLine(x, this.convertXtoY(x, iCycleLength), x + 2, this.convertXtoY(x + 1, iCycleLength), paint);
    }
    /**
     * Convert this x value to a y value.
     * 1) Convert the x value to a time
     * 2) Convert the time to a strength (in radians)
     * 3) Convert the strength to a Y value
     * @param x x value to use to compute the Y value
     * @param iCycleLength Length of the cycle to compute.
     * @returns The Y value.
     */
    public int convertXtoY(int x, int iCycleLength)
    {
        long lTimeX = this.convertXtoTime(x);
        double dGraphYValue = this.getController().getModel().convertTimeToStrength(lTimeX, iCycleLength);
        int y = this.convertStrengthToY(dGraphYValue);
        return y;
    }
    /**
     * Convert the grid x value to the time.
     * @param x The X value to convert.
     * @return The time.
     */
    public long convertXtoTime(int x)
    {
        long lStartTime = this.getController().getStartDate().getTime();
        long lEndTime = this.getController().getEndDate().getTime();
        long lWidth = (long)this.getWidth();
        long lTimeX = (lEndTime - lStartTime) / lWidth;
        return lStartTime + (x * lTimeX);
    }
    /**
     * Convert the value (range 1.0000 to -1.0000) to a grid y value.
     * @param gGraphYValue Strength to convert to a Y value.
     * @return Y value.
     */
    public int convertStrengthToY(double dGraphYValue)
    {
        int iGraphMargin;
        int height = this.getHeight();
//        Dimension m_dim = this.getBounds().getSize();
        if (height > 150)
            iGraphMargin = 14 * 2;
        else
            iGraphMargin = (int)(height * .2);    // 80%
// Now, Convert to the y position on the screen
        int m_iOrigin = height / 2;
        int m_iScaleValue = m_iOrigin - iGraphMargin;

        int y = (int)(dGraphYValue * m_iScaleValue);    // Weight of value
        y = -y;                                                                                 // " on reversed axis 
        y = y + m_iOrigin;                                                          // Shift by the amount of the origin
        return y;
    }
    /**
     * Get the color to paint this wave.
     * @param iCycleLength Type of cycle to paint.
     * @return The color of the element.
     */
    public int getElementColor(int iCycleLength)
    {
        if (iCycleLength == PHYSICAL_CYCLE)
            return Color.RED;
        if (iCycleLength == EMOTIONAL_CYCLE)
            return Color.BLUE;
        if (iCycleLength == INTELLECTUAL_CYCLE)
            return Color.GREEN;
        if (iCycleLength == BACKGROUND)
            return 0xFFFFCCCC; //Color.PINK
        if (iCycleLength == GRID)
            return Color.GRAY;
        if (iCycleLength == BASELINE)
            return Color.BLACK;
        if (iCycleLength == DATE)
            return Color.BLACK;
        if (iCycleLength == TODAY)
            return Color.WHITE;
        return Color.BLACK;
    }
    /**
     * 
     * @return
     */
    public Paint getPaint()
    {
        if (paint == null)
        {
            paint = new Paint();
            int textSize = Integer.parseInt(this.getResources().getString(R.string.font_size));
            paint.setTextSize(textSize);
            paint.setStrokeWidth(10);
        }
        return paint;
    }
    protected Paint paint = null;
    /**
     * 
     * @param paint
     * @param text
     * @return
     */
    public int getTextWidth(Paint paint, String text)
    {
        float widths[] = new float[text.length()];
        paint.getTextWidths(text, widths);
        int totalWidth = 0;
        for (float width : widths)
            totalWidth += width;
        return totalWidth;
    }
    /**
     * Get the resource bundle.
     * Note: Override this to supply the resource bundle.
     * @return Local string resources.
     */
    public ResourceBundle getResource()
    {
        return null;
    }
}
