package org.jbundle.test.db;

import java.util.Hashtable;
import java.util.Map;

import org.jbundle.app.test.test.db.TestTable;
import org.jbundle.base.db.Record;
import org.jbundle.base.db.RecordOwner;
import org.jbundle.base.thread.BaseProcess;
import org.jbundle.base.util.Environment;
import org.jbundle.base.util.MainApplication;
import org.jbundle.model.DBException;
import org.jbundle.model.Task;
import org.jbundle.model.util.Util;
import org.jbundle.thin.base.thread.AutoTask;
import org.jbundle.thin.base.util.Application;

public class DBTest extends Object
{
    boolean memoryTest = true;
    
    public DBTest(boolean memoryTest)
    {
        super();
        this.memoryTest = memoryTest;
    }
    public String runTest()
    {
        String text = "test did not run";
        
        Record fieldTable = null;
        if (memoryTest)
            fieldTable = this.setupRemoteRecord();
        else
            fieldTable = this.setupRemoteRecord();
        text = this.addTestTableRecords(fieldTable);
        
        return text;
    }
        
    /**
     * Create the thin table.
     */
    public Record setupRemoteRecord()
    {
        String[] args = {"remote=Client", "local=Client", "table=Client", "codebase=www.jbundle.org", "remotehost=www.jbundle.org", "connectionType=proxy"};
        Map<String,Object> properties = null;
        if (args != null)
        {
            properties = new Hashtable<String,Object>();
            Util.parseArgs(properties, args);
        }
        Environment env = new Environment(properties);
        Application app = new MainApplication(env, properties, null);
        env.setDefaultApplication(app);
        Task task = new AutoTask(app, null, null);

        RecordOwner recordOwner = new BaseProcess(task, null, null);
        Record testTable = new TestTable(recordOwner);

        return testTable;
    }
    
    public String addTestTableRecords(Record testTable)
    {
        try {
            testTable.addNew();
            testTable.setKeyArea(TestTable.TEST_CODE_KEY);
            testTable.getField(TestTable.TEST_CODE).setString("A");
            boolean success = testTable.seek(null);
            if (success)
                ;
        } catch (DBException e) {
            e.printStackTrace();
        }
        
        return testTable.getField(TestTable.TEST_NAME).toString();
    }
}
