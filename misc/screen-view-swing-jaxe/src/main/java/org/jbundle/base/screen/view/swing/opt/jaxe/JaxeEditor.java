/*
 * JaxeEditor.java
 *
 * Created on October 1, 2005, 4:00 AM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.

 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.base.screen.view.swing.opt.jaxe;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;

import org.jbundle.base.field.XmlField;
import org.jbundle.base.model.DBConstants;
import org.jbundle.base.model.Utility;
import org.jbundle.base.screen.view.swing.VCannedBox;

import jaxe.Jaxe;
import jaxe.JaxeFrame;
import jaxe.Preferences;


/**
 *
 * @author don
 */
public class JaxeEditor 
    implements VCannedBox.Editor
{
    public final static String XML_LEAD_LINE = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
    
    public static final char RETURN = '\n';
    public static final String TEMP_TAG_NAME = "HELP";
    public static final String START_TEMP_TAG = "<" + TEMP_TAG_NAME + ">";
    public static final String END_TEMP_TAG = "</" + TEMP_TAG_NAME + ">";
//    public static final String SCHEMA_PATH = "/var/tourapp/java/other/jaxe/src/jaxe/dist/Jaxe/config/";
    public static final String SCHEMA_PATH = "docs/styles/xsl/jaxe/";
    
    protected VCannedBox m_control = null;
    protected File file = null;
    
    /** Creates a new instance of JaxeEditor */
    public JaxeEditor() 
    {
    }
    
    public boolean startEditor(VCannedBox control, String string, String schema)
    {
        m_control = control;
        
        String strXML = string;
        if ((strXML.length() < 5)
            || (!strXML.substring(0, 5).equalsIgnoreCase(XML_LEAD_LINE.substring(0, 5))))
                strXML = XML_LEAD_LINE + START_TEMP_TAG + RETURN + strXML + RETURN + END_TEMP_TAG;    // Always

        if (schema == null)
            schema = XmlField.DEFAULT_SCHEMA;
        schema = schema + "_en_config.xml";
        String schemaTempName = System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") + schema;
        File configFile = new File(schemaTempName);
//        if (!configFile.exists())
        {   // Shouldn't have to copy this config file every time.
            schema = SCHEMA_PATH + schema;
            URL url = control.getScreenField().getParentScreen().getTask().getApplication().getResourceURL(schema, null);
            Utility.transferURLStream(url.toString(), schemaTempName, null);
        }
        
        try {
            file = File.createTempFile("temp", ".xml");
            FileWriter out = new FileWriter(file);
            out.write(strXML);
            out.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        JaxeFrame jframe = null;

        Preferences.chargerPref();

        new Jaxe(null);
        jframe = new JaxeFrame()
        {
            private static final long serialVersionUID = 1L;

            public boolean fermer(boolean quit) 
            {
                quit = true;
                boolean flag = super.fermer(quit);
                doneEditing(); // Tell calling program I'm done
                return flag;
            }
        };
        Jaxe.ouvrirAvecConf(file, configFile, jframe);

        return true;
    }

    public void doneEditing()
    {
        try {
            FileReader reader = new FileReader(file);
            String string = Utility.transferURLStream(null, null, reader);
            string = this.stripXMLExtra(string);
            m_control.setEditFile(string);
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
    } 
    
    public String stripXMLExtra(String string)
    {
        if (string == null)
            return DBConstants.BLANK;
        int beginIndex = string.indexOf(START_TEMP_TAG);
        int endIndex = string.lastIndexOf(END_TEMP_TAG);
        if (beginIndex == -1)
            beginIndex = 0;
        else
        {
            beginIndex = beginIndex + START_TEMP_TAG.length();
            if (string.charAt(beginIndex) == RETURN)
                beginIndex++;
        }
        if (endIndex == -1)
            endIndex = string.length();
        else
            if (string.charAt(endIndex - 1) == RETURN)
                endIndex--;
        string = string.substring(beginIndex, endIndex);
        return string;
    }
}
