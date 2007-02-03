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

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

/**

 * @author Federico Granata
 * Date: 4-giu-2003
 * Time: 10.36.36

 */
public class CloseModel extends AbstractJmodelAction {

	/**
	 * Defines an <code>Action</code> object with a default
	 * description string and default icon.
	 */
	public CloseModel(Mediator mediator) {
		super("Close", mediator);
		putValue(SHORT_DESCRIPTION, "close opened model");
		putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_L));
		setEnabled(false);
	}

	/**
	 * Invoked when an action occurs.
	 */
	public void actionPerformed(ActionEvent e) {
		mediator.closeModel();
	}
}
