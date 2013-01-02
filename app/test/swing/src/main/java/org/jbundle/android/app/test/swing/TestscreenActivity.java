package org.jbundle.android.app.test.swing;

import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Hashtable;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.JApplet;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.border.LineBorder;

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

public class TestscreenActivity extends JApplet {
    
    private static final long serialVersionUID = 1L;

    /**
     *  OrderEntry Class Constructor.
     */
    public TestscreenActivity()
    {
        super();
    }
    /**
     * For Stand-alone.
     */
    public static void main(String[] args)
    {
        JFrame frame;
        TestscreenActivity applet = new TestscreenActivity();
        try {
            frame = new JFrame("Calendar");
            frame.addWindowListener(new AppCloser(frame, applet));
        } catch (java.lang.Throwable ivjExc) {
            frame = null;
            System.out.println(ivjExc.getMessage());
            ivjExc.printStackTrace();
        }
        frame.getContentPane().add(BorderLayout.CENTER, applet);
        Dimension size = applet.getSize();
        if ((size == null) || ((size.getHeight() < 100) | (size.getWidth() < 100)))
            size = new Dimension(640, 400);
        frame.setSize(size);

        applet.init();       // Simulate the applet calls
        frame.setTitle("Sample calendar application");
        applet.start();

        frame.setVisible(true);
    }
    /**
     * Initialize this applet.
     */
    public void init()
    {
        this.getContentPane().setLayout(new BorderLayout());
        JPanel panel = new JPanel();
        this.getContentPane().add(panel);
        this.addSubPanels(panel);
    }
    /**
     * Called to start the applet.  You never need to call this directly; it
     * is called when the applet's document is visited.
     */
    public void start()
    {
        super.start();
    }
    /**
     * Add any applet sub-panel(s) now.
     */
    JTextField text;
    JTextField text2;
    public boolean addSubPanels(Container parent)
    {
        Container panel = parent;
        
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        
        text = new JTextField();
        text.setBorder(new LineBorder(Color.green, 1));
        text.addFocusListener(new FocusAdapter()
        {
            /**
             * Invoked when a component gains the keyboard focus.
             */
            public void focusGained(FocusEvent e) {
                super.focusGained(e);
            }

            /**
             * Invoked when a component loses the keyboard focus.
             */
            public void focusLost(FocusEvent e) {
                super.focusLost(e);
                String string = text.getText().toString();
                new ReadRecordTask(string).execute();
            }            
        });
        panel.add(text);

        text2 = new JTextField();
        text2.setBorder(new LineBorder(Color.black, 1));
        panel.add(text2);

        
        return true;
    }
    /**
     * Cleans up whatever resources are being held.  If the applet is active
     * it is stopped.
     */
    public void destroy()
    {
        super.destroy();
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
    
    private class ReadRecordTask extends SwingWorker<String, Object> {
        String key;
        ReadRecordTask(String key)
        {
            this.key = key;
        }
        @Override
        public String doInBackground() {
            boolean found = false;
            TestTable record = getRecord();
            try {
                record.initRecord(true);
                record.setKeyArea("TestCode");
                record.getField("TestCode").setString(key);
                found = record.getTable().seek(null);
            } catch (DBException e) {
                e.printStackTrace();
            }
            String string = record.getField("TestName").getString();
            return string;
        }

        @Override
        protected void done() {
            try { 
                String value = get();
                text2.setText(value);
            } catch (Exception ignore) {
            }
        }
    }
    /**
     * Create the thin table.
     */
    public Table setupRemoteThinTable()
    {
        Record record = null;
//        BaseApplet.main(null);
        String[] args = {"remote=Client", "local=Client", "table=Client", "codebase=www.jbundle.org/", "remotehost=www.jbundle.org", "connectionType=proxy"};
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
    
    

    /**
     * AppCloser quits the application when the user closes the window.
     */
    public static class AppCloser extends WindowAdapter
    {
        JFrame frame = null;
        Applet app = null;
        /**
         * Constructor.
         */
        public AppCloser(JFrame frame, Applet app)
        {
            super();
            this.frame = frame;
            this.app = app;
        }
        /**
         * Close the window.
         */
        public void windowClosing(WindowEvent e)
        {
            app.stop();     // Simulate the applet calls
            app.destroy();
            frame.setVisible(false);
            frame.dispose();
            System.exit(0);
        }
    }
}