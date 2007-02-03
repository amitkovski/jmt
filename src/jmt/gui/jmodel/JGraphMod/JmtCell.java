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
  
package jmt.gui.jmodel.JGraphMod;

import org.jgraph.JGraph;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.DefaultPort;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.Port;

import javax.swing.*;
import javax.swing.tree.TreeNode;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Hashtable;
import java.util.Map;

/** vertex cell for jmt

 * @author Federico Granata
 * Date: 11-lug-2003
 * Time: 13.48.34
 *
 * @author Bertoli Marco

 */
public abstract class JmtCell extends DefaultGraphCell {
    private Dimension imageDimension;
    // Used to determine if parent of this cell has changed (to detect enter and exit from
    // blocking regions)
    private TreeNode parentRef;
    /**
	 * Different kind of nodes.
	 */
	public static final int SOURCE = 0;
	public static final int TERMINAL = 1;
	public static final int DELAY = 2;
	public static final int SERVER = 3;
	public static final int SINK = 4;
	public static final int FORK = 5;
	public static final int JOIN = 6;
    public static final int LDSERVER = 7;

	public int type;

    /**
     * Tells if this component can be placed on JGraph or has been disabled. This is
     * useful to load old models with disabled components (like terminals)
     */
    public static final boolean canBePlaced = true;

    /**
	 * Creates a graph cell and initializes it with the specified user object.
	 *
 	 * @param userObject an Object provided by the user that constitutes
	 *                   the cell's data
     *
     * Conti Andrea  01-09-2003
     * Bertoli Marco 04-giu-2005
	 */
	public JmtCell(ImageIcon icon, Object userObject) {
		super(userObject);
		GraphConstants.setIcon(attributes, icon);
        imageDimension = new Dimension(icon.getIconWidth(), icon.getIconHeight());
		GraphConstants.setSizeable(attributes, false);
        GraphConstants.setSize(attributes, imageDimension);
	}

    /**
     * Return Cell's real size (this method considers Icon and name size)
     * @param graph <code>JGraph</code> object to retrive font dimension informations
     * @return cell's real size
     *
     * Bertoli Marco  4-giu-2005
     */
    public Dimension getSize(JGraph graph) {
        Dimension cellDimension = (Dimension) imageDimension.clone();
        // Gets the graph font
		Font font = graph.getFont();
		// Gets the graphical context
		Graphics2D g2D = (Graphics2D) graph.getGraphics();
		// Gets the bounds of the cell name
		FontRenderContext frc = g2D.getFontRenderContext();
        Rectangle r = font.getStringBounds(getUserObject().toString(), frc).getBounds();
		// Sets the cell dimension
		cellDimension.height += r.height + 5;
		cellDimension.width = Math.max(cellDimension.width, r.width + 10);
        return cellDimension;
    }

	/**creats the ports for this vertex
	 *
	 * @return array of ports
	 */
	public abstract Port[] createPorts();

	/**
	 * Sets all the attribults like background colour, dimensions, port number
	 * & position
	 * @param pt
	 * @return created map
	 */
	public Hashtable setAttributes(Point2D pt, JGraph graph) {
		//contains attribute of the cell & ports
		Hashtable nest = new Hashtable();

        Dimension cellDimension = getSize(graph);
		//contains attrib of cell
		Map attr = getAttributes();
        GraphConstants.setBounds(attr, new Rectangle2D.Double(pt.getX(), pt.getY(), cellDimension.getWidth(), cellDimension.getHeight()));
		GraphConstants.setEditable(attr, false);
		GraphConstants.setBackground(attr, graph.getBackground());
		nest.put(this, attr);

		//create ports
		Port[] ports = createPorts();
		Icon icon = GraphConstants.getIcon(attr);
		for (int i = 0; i < ports.length; i++) {
			attr = new Hashtable();
			// 03/11/03 - Massimo Cattai //////////////////////////////////////////
			// Old Code - if(ports[i] instanceof InputPort)
			// Old Code - 	GraphConstants.setOffset(attr, new Point(0, GraphConstants.PERCENT / 2));
			// Old Code - else
			// Old Code - 	GraphConstants.setOffset(attr, new Point(GraphConstants.PERCENT, GraphConstants.PERCENT / 2));
			if (ports[i] instanceof InputPort)
				GraphConstants.setOffset(attr, getInPortOffset(attr, icon, cellDimension));
			else {
				GraphConstants.setOffset(attr, getOutPortoffset(attr, icon, cellDimension));
			}
			// 03/11/03 - end /////////////////////////////////////////////////////
			nest.put(ports[i], attr);
			add((DefaultPort) ports[i]);
		}
        return nest;
	}

	protected Point getInPortOffset(Map attr, Icon icon, Dimension cellDimension) {
		int iconHeight = icon.getIconHeight();
		int iconWidth = icon.getIconWidth();
		int xOff = (cellDimension.width - iconWidth)
		        / 2 * 1000 / cellDimension.width;
		int yOff = iconHeight / 2 * 1000 / cellDimension.height;
		return new Point(xOff, yOff);
	}

	protected Point getOutPortoffset(Map attr, Icon icon, Dimension cellDimension) {
		int iconHeight = icon.getIconHeight();
		int iconWidth = icon.getIconWidth();
		int xOff = (cellDimension.width - iconWidth)
		        / 2 * 1000 / cellDimension.width
		        + iconWidth * 1000 / cellDimension.width;
		int yOff = iconHeight / 2 * 1000 / cellDimension.height;
		return new Point(xOff, yOff);
	}

	/**
	 * is true if the InputPort of this cell is on the left side
	 * @return true if the InputPort of this cell is on the left side
	 */
	public boolean isLeftInputCell() {
		return true;
	}

    /**
     * Resets stored parent information for this cell
     */
    public void resetParent() {
        parentRef = getParent();
    }

    /**
     * Tells if this cell parent was changed since last call to resetParent() method
     * @return true if parent changed, false otherwise
     * @see #resetParent()
     */
    public boolean parentChanged() {
        if (parentRef == null && getParent() == null)
            return false;
        else if (parentRef == null || getParent() == null)
            return true;
        else
            return !parentRef.equals(getParent());
    }

    /**
     * Tells if this station generates or destroys jobs (useful for blocking region
     * management)
     * @return true if this station generates or destroy jobs, false otherwise
     */
    public boolean generateOrDestroyJobs() {
        return false;
    }

    /**
     * Returns previous parent of this cell (the one present when resetParent()
     * method was called)
     * @return previous parent of this cell
     * @see #resetParent()
     */
    public TreeNode getPrevParent() {
        return parentRef;
    }
}
