/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.base.screen.view.android;

/**
 * @(#)ScreenField.java   0.00 12-Feb-97 Don Corley
 *
 * Copyright © 2012 tourgeek.com. All Rights Reserved.
 *      don@tourgeek.com
 */
import java.awt.Component;

import javax.swing.JPasswordField;
import javax.swing.text.JTextComponent;

import org.jbundle.base.model.DBConstants;
import org.jbundle.base.screen.model.ScreenField;
import org.jbundle.model.screen.ScreenComponent;


/**
 * An edit control that doesn't echo characters.
 */
public class APasswordField extends AEditText
{

    /**
     * Constructor.
     */
    public APasswordField()
    {
        super();
    }
    /**
     * Constructor.
     * @param model The model object for this view object.
     * @param bEditableControl Is this control editable?
     */
    public APasswordField(ScreenField model, boolean bEditableControl)
    {
        this();
        this.init(model, bEditableControl);
    }
    /**
     * Constructor.
     * @param model The model object for this view object.
     * @param bEditableControl Is this control editable?
     */
    public void init(ScreenComponent model, boolean bEditableControl)
    {
        super.init(model, bEditableControl);
    }
    /**
     * Free.
     */
    public void free()
    {
        if (this.getControl(DBConstants.CONTROL_TO_FREE) != null)
            if (m_bEditableControl)
        {
            this.getControl(DBConstants.CONTROL_TO_FREE).removeFocusListener(this);
            this.getControl(DBConstants.CONTROL_TO_FREE).removeKeyListener(this);
            m_bEditableControl = false;     // Don't remove more listeners in super
        }
        super.free();
    }
    /**
     * Create the physical control.
     * @param bEditableControl Is this control editable?
     * @return The new control.
     */
    public Component setupControl(boolean bEditableControl)
    {
        int cols = 10;
        if (this.getScreenField().getConverter() != null)
            cols = this.getScreenField().getConverter().getMaxLength();
        JTextComponent control = new JPasswordField(cols);

        control.addFocusListener(this);
        control.addKeyListener(this);
        return control;
    }
}
