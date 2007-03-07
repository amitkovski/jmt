package jmt.framework.gui.layouts;

import java.awt.*;
import java.util.Vector;

/**
 * A custom BorderLayout manager that supports multiple items on the same position
 * User: Marco Bertoli
 * Date: 1-giu-2005
 * Time: 11.32.36
 */
public class MultiBorderLayout extends BorderLayout {

    Vector north=new Vector();
    Vector south=new Vector();
    Vector west=new Vector();
    Vector east=new Vector();
    Vector center=new Vector();

    public MultiBorderLayout() {
        super();
    }

    public MultiBorderLayout(int hgap, int vgap) {
        super(hgap,vgap);
    }

    public void addLayoutComponent(String name, Component comp) {
        synchronized (comp.getTreeLock()) {
            if (name == null) {
                name = "Center";
            }

/* Assign the component to one of the known regions of the layout.
*/
            if ("Center".equals(name)) {
                center.add(comp);
            } else if ("North".equals(name)) {
                north.add(comp);
            } else if ("South".equals(name)) {
                south.add(comp);
            } else if ("East".equals(name)) {
                east.add(comp);
            } else if ("West".equals(name)) {
                west.add(comp);
            } else {
                throw new IllegalArgumentException("cannot add to layout: unknown constraint: " + name);
            }
        }
    }

    public void removeLayoutComponent(Component comp) {
        synchronized (comp.getTreeLock()) {
            south.remove(comp);
            north.remove(comp);
            center.remove(comp);
            west.remove(comp);
            east.remove(comp);
        }
    }

    public Dimension minimumLayoutSize(Container target) {
        synchronized (target.getTreeLock()) {
            Dimension dim = new Dimension(0, 0);

            Component c = null;

            if (east.size()>0) {
                for (int i=0; i<east.size();i++) {
                    c=(Component)east.get(i);
                    Dimension d = c.getMinimumSize();
                    dim.width += d.width + this.getHgap();
                    dim.height = Math.max(d.height, dim.height);
                }
            }
            if (west.size()>0) {
                for (int i=0; i<west.size();i++) {
                    c=(Component)west.get(i);
                    Dimension d = c.getMinimumSize();
                    dim.width += d.width + this.getHgap();
                    dim.height = Math.max(d.height, dim.height);
                }
            }
            if (center.size()>0) {
                for (int i=0; i<center.size();i++) {
                    c=(Component)center.get(i);
                    Dimension d = c.getMinimumSize();
                    dim.width += d.width;
                    dim.height = Math.max(d.height, dim.height);
                }
            }
            if (north.size()>0) {
                for (int i=0; i<north.size();i++) {
                    c=(Component)north.get(i);
                    Dimension d = c.getMinimumSize();
                    dim.width = Math.max(d.width, dim.width);
                    dim.height += d.height + this.getVgap();
                }
            }
            if (south.size()>0) {
                for (int i=0; i<south.size();i++) {
                    c=(Component)south.get(i);
                    Dimension d = c.getMinimumSize();
                    dim.width = Math.max(d.width, dim.width);
                    dim.height += d.height + this.getVgap();
                }
            }

            Insets insets = target.getInsets();
            dim.width += insets.left + insets.right;
            dim.height += insets.top + insets.bottom;

            return dim;
        }
    }

    public Dimension prefferedLayoutSize(Container target) {
        synchronized (target.getTreeLock()) {
            Dimension dim = new Dimension(0, 0);

            Component c = null;

            if (east.size()>0) {
                for (int i=0; i<east.size();i++) {
                    c=(Component)east.get(i);
                    Dimension d = c.getPreferredSize();
                    dim.width += d.width + this.getHgap();
                    dim.height = Math.max(d.height, dim.height);
                }
            }
            if (west.size()>0) {
                for (int i=0; i<west.size();i++) {
                    c=(Component)west.get(i);
                    Dimension d = c.getPreferredSize();
                    dim.width += d.width + this.getHgap();
                    dim.height = Math.max(d.height, dim.height);
                }
            }
            if (center.size()>0) {
                for (int i=0; i<center.size();i++) {
                    c=(Component)center.get(i);
                    Dimension d = c.getPreferredSize();
                    dim.width += d.width;
                    dim.height = Math.max(d.height, dim.height);
                }
            }
            if (north.size()>0) {
                for (int i=0; i<north.size();i++) {
                    c=(Component)north.get(i);
                    Dimension d = c.getPreferredSize();
                    dim.width = Math.max(d.width, dim.width);
                    dim.height += d.height + this.getVgap();
                }
            }
            if (south.size()>0) {
                for (int i=0; i<south.size();i++) {
                    c=(Component)south.get(i);
                    Dimension d = c.getPreferredSize();
                    dim.width = Math.max(d.width, dim.width);
                    dim.height += d.height + this.getVgap();
                }
            }

            Insets insets = target.getInsets();
            dim.width += insets.left + insets.right;
            dim.height += insets.top + insets.bottom;

            return dim;
        }
    }

    public void layoutContainer(Container target) {
        synchronized (target.getTreeLock()) {
            Insets insets = target.getInsets();
            int top = insets.top;
            int bottom = target.getHeight() - insets.bottom;
            int left = insets.left;
            int right = target.getWidth() - insets.right;

            Component c = null;

            if (north.size()>0) {
                for (int i=0; i<north.size(); i++) {
                    c=(Component)north.get(i);
                    Dimension d = c.getPreferredSize();
                    c.setSize(right - left,d.height);
                    c.setBounds(left, top, right - left, c.getHeight());
                    top += d.height;
                }
            }
            if (south.size()>0) {
                for (int i=0; i<south.size(); i++) {
                    c=(Component)south.get(i);
                    Dimension d = c.getPreferredSize();
                    c.setSize(right - left, d.height);
                    c.setBounds(left, bottom - d.height, right - left, c.getHeight());
                    bottom -= d.height;
                }
            }
            if (east.size()>0) {
                for (int i=0; i<east.size(); i++) {
                    c=(Component)east.get(i);
                    Dimension d = c.getPreferredSize();
                    c.setSize(d.width, bottom - top);
                    c.setBounds(right - d.width, top, c.getWidth(), bottom - top);
                    right -= d.width;
                }
            }
            if (west.size()>0) {
                for (int i=0; i<west.size(); i++) {
                    c=(Component)west.get(i);
                    Dimension d = c.getPreferredSize();
                    c.setSize(d.width, bottom - top);
                    c.setBounds(left, top, c.getWidth(), bottom - top);
                    left += d.width;
                }
            }
            if (center.size()>0) {
                for (int i=0; i<center.size(); i++) {
                    c=(Component)center.get(i);
                    c.setBounds(left, top, right - left, bottom - top);
                }
            }
        }
    }

}