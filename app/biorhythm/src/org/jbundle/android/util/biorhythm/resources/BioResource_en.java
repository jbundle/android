/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.android.util.biorhythm.resources;

/*
 * Copyright © 2012 jbundle.org. All Rights Reserved.
 *	Copy freely, but don't sell this program or remove this copyright notice.
 *		Don_Corley@msn.com
 */

import java.util.*;

public class BioResource_en extends ListResourceBundle {
		static final Object[][] m_contents = {
			 // LOCALIZE THIS
			 		 {"Language", "English"},
			 		 {"LanguageInEnglish", "English"},
					 {"Biorhythm", "Biorhythm"},		// "Biorththm"
					 {"Birthdate", "Birthdate"},		// Input field labels
					 {"Start Date", "Start"},
					 {"End Date", "End"},
					 {"Emotional Cycle", "Emotional"},	// Cycle Descriptions
					 {"Physical Cycle", "Physical"},
					 {"Intellectual Cycle", "Intellectual"},
					 {"Critical", "Critical"},		// Critials/High/Low
					 {"High", "High"},
					 {"Low", "Low"},
					 {"EnterBirthdate", "Enter your birthdate in the Birthdate field."},
			 // END OF MATERIAL TO LOCALIZE
			 };

//----------------------------------------------------------------
// HsilgneDate - Default constructor (Same as EDate)
	public BioResource_en() {
		super();
	}
/**
 * getContents method comment.
 */
protected java.lang.Object[][] getContents() {
	return m_contents;
}
}
