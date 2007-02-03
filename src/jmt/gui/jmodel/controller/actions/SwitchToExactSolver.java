/**    
  * Copyright (C) 2006, Laboratorio di Valutazione delle Prestazioni - Politecnico di Milano

  * This program is free software; you can redistribute it and/or modify
  * it under the terms of the GNU General Public License as published by
  * the Free Software Foundation; either version 2 of the License, or
  * (at your option) any later version.

  * This program is distributed in the hope that it will be useful,
  * but WITHOUT ANY WARRANTY; without even the implied warranty of
  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  * GNU General Public License for more details.

  * You should have received a copy of the GNU General Public License
  * along with this program; if not, write to the Free Software
  * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
  */
  
package jmt.gui.jmodel.controller.actions;

import jmt.gui.jmodel.controller.Mediator;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

/**

 * @author Massimo Cattai
 * Date: 13-ott-2003
 * Time: 11.58.15

 */

/**
 * Action calls the <code>ExactWizard</code> and disposes the current application.
 */
public class SwitchToExactSolver extends AbstractJmodelAction {

	/**
	 * Defines an <code>Action</code> object with a default
	 * description string and default icon.
	 */
	public SwitchToExactSolver(Mediator mediator) {
		super("Export to JMVA...", "toJMVA", mediator);
		putValue(SHORT_DESCRIPTION, "Export current model to JMVA...");
		putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_M));
        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_M, KeyEvent.ALT_MASK));
		setEnabled(false);
	}

	/**
	 * Invoked when an action occurs.
	 */
	public void actionPerformed(ActionEvent e) {
		//if (mediator.checkForSave("<html>Save changes before switching?</html>")) return;
		mediator.toJMVA();
	}
}
