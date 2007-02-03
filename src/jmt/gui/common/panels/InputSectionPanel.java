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
  
package jmt.gui.common.panels;

import jmt.framework.gui.table.editors.ComboBoxCellEditor;
import jmt.framework.gui.wizard.WizardPanel;
import jmt.gui.common.CommonConstants;
import jmt.gui.common.definitions.ClassDefinition;
import jmt.gui.common.definitions.StationDefinition;
import jmt.gui.common.editors.GrayCellRenderer;
import jmt.gui.common.editors.ImagedComboBoxCellEditorFactory;
import jmt.gui.exact.table.DisabledCellRenderer;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TableModelEvent;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: orsotronIII
 * Date: 30-giu-2005
 * Time: 9.33.59
 * Modified by Bertoli Marco 7-oct-2005
 *                           9-jan-2006  --> ComboBoxCellEditor
 */
public class InputSectionPanel extends WizardPanel implements CommonConstants {
    /**
     * Used to define queuing policy
     */
    protected Object[] queuePolicy = new Object[]
            {   QUEUE_STRATEGY_FCFS,
                QUEUE_STRATEGY_FCFS_PRIORITY,
                QUEUE_STRATEGY_LCFS,
                QUEUE_STRATEGY_LCFS_PRIORITY
    };
    /**
     * Used to define drop rules
     */
    protected Object[] dropRules = new Object[] {
            CommonConstants.FINITE_WAITING,
            CommonConstants.FINITE_BLOCK,
            CommonConstants.FINITE_DROP
    };
    private ButtonGroup queueLengthGroup;
    private JRadioButton infiniteQueueSelector,
    finiteQueueSelector;
    private JSpinner queueLengthSpinner;
    private QueueTable queueTable;
    /** Used to display classes with icon */
    protected ImagedComboBoxCellEditorFactory classEditor;


    protected StationDefinition data;
    protected ClassDefinition classData;
    protected Object stationKey;

    public InputSectionPanel(StationDefinition sd, ClassDefinition cd, Object stationKey){
        classEditor = new ImagedComboBoxCellEditorFactory(cd);
        setData(sd, cd, stationKey);
        initComponents();
        addDataManagers();
        getQueueLength();
    }

    private void initComponents(){
        this.setBorder(new EmptyBorder(5,5,5,5));
        this.setLayout(new BorderLayout(5,5));
        infiniteQueueSelector = new JRadioButton("infinite");
        finiteQueueSelector = new JRadioButton("finite");
        queueLengthGroup = new ButtonGroup();
        queueLengthGroup.add(infiniteQueueSelector);
        queueLengthGroup.add(finiteQueueSelector);
        queueLengthSpinner = new JSpinner();
        queueLengthSpinner.setValue(new Integer(1));
        queueLengthSpinner.setPreferredSize(DIM_BUTTON_XS);
        queueTable = new QueueTable();

        //queue details panel
        JPanel queuePolicyPanel = new JPanel(new GridLayout(1,1));
        queuePolicyPanel.setBorder(new TitledBorder(new EtchedBorder(), "Queue Policy"));
        queuePolicyPanel.add(new WarningScrollTable(queueTable, WARNING_CLASS));
        JPanel queueLengthPanel = new JPanel(new GridLayout(3,1,3,3));
        queueLengthPanel.setBorder(new TitledBorder(new EtchedBorder(), "Capacity"));

        queueLengthPanel.setAlignmentY(JPanel.BOTTOM_ALIGNMENT);
        queueLengthPanel.add(infiniteQueueSelector);
        queueLengthPanel.add(finiteQueueSelector);
        JPanel spinnerPanel = new JPanel();
        new BoxLayout(spinnerPanel, BoxLayout.Y_AXIS);
        spinnerPanel.add(new JLabel("length: "));
        spinnerPanel.add(queueLengthSpinner);
        queueLengthPanel.add(spinnerPanel);

        this.add(queueLengthPanel, BorderLayout.WEST);
        this.add(queuePolicyPanel, BorderLayout.CENTER);
    }

    public void setData(StationDefinition sd, ClassDefinition cd, Object stationKey){
        data = sd;
        classData = cd;
        this.stationKey = stationKey;
        classEditor.setData(cd);
        if(queueLengthGroup != null) getQueueLength();
    }

    private void addDataManagers(){
        queueLengthSpinner.addChangeListener(new ChangeListener(){
            public void stateChanged(ChangeEvent e) {
                if(queueLengthSpinner.isEnabled()){
                    Integer queueLength = (Integer)queueLengthSpinner.getValue();
                    if(queueLength.intValue() < 1){
                        queueLength = new Integer(1);
                        queueLengthSpinner.setValue(queueLength);
                    }
                    data.setStationQueueCapacity(queueLength, stationKey);
                }
            }
        });

        ChangeListener buttonListener = new ChangeListener(){
            public void stateChanged(ChangeEvent e) {
                setQueueLength();
            }
        };

        infiniteQueueSelector.addChangeListener(buttonListener);
        finiteQueueSelector.addChangeListener(buttonListener);
    }

    private void getQueueLength(){
        Integer queueLength = data.getStationQueueCapacity(stationKey);
        if(queueLength.intValue() < 0){
            queueLengthGroup.setSelected(infiniteQueueSelector.getModel(), true);
            queueLengthSpinner.setEnabled(false);
        }else{
            queueLengthGroup.setSelected(finiteQueueSelector.getModel(), true);
            queueLengthSpinner.setEnabled(true);
            queueLengthSpinner.setValue(queueLength);
        }
    }

    private void setQueueLength(){
        if(infiniteQueueSelector.isSelected()){
            queueLengthSpinner.setEnabled(false);
            data.setStationQueueCapacity(new Integer(-1), stationKey);
            queueTable.repaint();
        }else{
            queueLengthSpinner.setEnabled(true);
            data.setStationQueueCapacity((Integer)queueLengthSpinner.getValue(), stationKey);
            queueTable.repaint();
        }
    }

    /**
     * @return the panel's name
     */
    public String getName() {
        return "Queue Section";
    }

    /**
     * called by the Wizard when the panel becomes active
     */
    public void gotFocus() {
        classEditor.clearCache();
    }

    /**
     * called by the Wizard before when switching to another panel
     */
    public void lostFocus() {
        // Aborts editing of table
        TableCellEditor editor = queueTable.getCellEditor();
        if (editor != null)
            editor.stopCellEditing();
    }

    protected class QueueTable extends JTable{
        private DisabledColumnRenderer disRend = new DisabledColumnRenderer(CommonConstants.INFINITE_CAPACITY);
        public QueueTable(){
            super();
            setModel(new QueueTableModel());
            sizeColumns();
            setRowHeight(ROW_HEIGHT);
        }

        private void sizeColumns(){
            for(int i=0; i<getColumnCount(); i++){
                getColumnModel().getColumn(i).setPreferredWidth(
                        ((QueueTableModel)getModel()).columnSizes[i]
                );
            }
        }

        public TableCellEditor getCellEditor(int row, int column){
            if(column == 1){
                return ComboBoxCellEditor.getEditorInstance(queuePolicy);
            }else if(column == 2){
                return ComboBoxCellEditor.getEditorInstance(dropRules);
            }else
                return super.getCellEditor(row, column);
        }

        public TableCellRenderer getCellRenderer(int row, int column) {
            if (column == 0)
                return classEditor.getRenderer();
            else if (column == 1)
                return ComboBoxCellEditor.getRendererInstance();
            else if (column == 2)
                return disRend;
            else
                return super.getCellRenderer(row, column);
        }
    }


    protected class QueueTableModel extends AbstractTableModel{

        private String[] columnNames = new String[]{"Class", "Queue Policy", "Drop Rule"};
        private Class[] columnClasses = new Class[]{String.class, String.class, String.class};
        public int[] columnSizes = new int[]{90, 60, 60};

        public int getRowCount() {
            return classData.getClassKeys().size();
        }

        public int getColumnCount() {
            return columnNames.length;
        }

        public String getColumnName(int columnIndex) {
            return columnNames[columnIndex];
        }

        public Class getColumnClass(int columnIndex) {
            return columnClasses[columnIndex];
        }

        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return columnIndex == 1 || (columnIndex == 2 && data.getStationQueueCapacity(stationKey).intValue() >= 0);
        }

        public Object getValueAt(int rowIndex, int columnIndex) {
            Object classKey = indexToKey(rowIndex);
            if(columnIndex == 0){
                return classKey;
            }else if(columnIndex == 1){
                return data.getQueueStrategy(stationKey, classKey);
            }else return data.getDropRule(stationKey, classKey);
        }

        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            Object classKey = indexToKey(rowIndex);
            if(columnIndex == 1){
                data.setQueueStrategy(stationKey, classKey, (String)aValue);
            } else if (columnIndex == 2)
                data.setDropRule(stationKey, classKey, (String)aValue);
        }

        private Object indexToKey(int index){
            return classData.getClassKeys().get(index);
        }

    }
     
    /**
     * <p><b>Name:</b> DisabledColumnRenderer</p> 
     * <p><b>Description: </b> A special renderer that will show disabled text when
     * queue capacity is infinite, otherwise will show a combobox. 
     * 
     * </p>
     * <p><b>Date:</b> 21/ott/06
     * <b>Time:</b> 15:59:56</p>
     * @author Bertoli Marco
     * @version 1.0
     */
    private class DisabledColumnRenderer extends ComboBoxCellEditor {
        private JLabel label;
        
        public DisabledColumnRenderer(String text) {
            label = new JLabel(text);
            label.setEnabled(false);
        }
        
        /* (non-Javadoc)
         * @see jmt.gui.common.editors.ComboBoxCellEditor#getTableCellRendererComponent(javax.swing.JTable, java.lang.Object, boolean, boolean, int, int)
         */
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            if (data.getStationQueueCapacity(stationKey).intValue() < 0) {
                if (isSelected)
                    label.setBackground(table.getSelectionBackground());
                else
                    label.setBackground(table.getBackground());
                return label;
            }
            else
                return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        }
        
    }


}
