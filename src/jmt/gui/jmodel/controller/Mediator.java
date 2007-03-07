/**
  * Copyright (C) 2007, Laboratorio di Valutazione delle Prestazioni - Politecnico di Milano

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

package jmt.gui.jmodel.controller;

import jmt.framework.gui.components.JMTMenuBar;
import jmt.framework.gui.components.JMTToolBar;
import jmt.framework.gui.listeners.MenuAction;
import jmt.gui.common.controller.DispatcherThread;
import jmt.gui.common.controller.ModelChecker;
import jmt.gui.common.controller.PADispatcherThread;
import jmt.gui.common.definitions.*;
import jmt.gui.common.editors.DefaultsEditor;
import jmt.gui.common.panels.*;
import jmt.gui.common.panels.parametric.PAProgressWindow;
import jmt.gui.common.panels.parametric.PAResultsWindow;
import jmt.gui.common.panels.parametric.ParametricAnalysisPanel;
import jmt.gui.common.resources.JMTImageLoader;
import jmt.gui.common.xml.ModelLoader;
import jmt.gui.common.xml.XMLWriter;
import jmt.gui.exact.ExactModel;
import jmt.gui.exact.ExactWizard;
import jmt.gui.jmodel.DialogFactory;
import jmt.gui.jmodel.JGraphMod.*;
import jmt.gui.jmodel.controller.actions.*;
import jmt.gui.jmodel.definitions.JMODELModel;
import jmt.gui.jmodel.definitions.JmodelClassDefinition;
import jmt.gui.jmodel.definitions.JmodelStationDefinition;
import jmt.gui.jmodel.mainGui.ComponentBar;
import jmt.gui.jmodel.mainGui.MainWindow;
import jmt.gui.jmodel.panels.JModelProblemsWindow;
import jmt.gui.jmodel.panels.StationNamePanel;
import jmt.gui.jmodel.panels.jmodelClassesPanel;
import org.jgraph.JGraph;
import org.jgraph.graph.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.util.*;
import java.util.List;

/**
 * This class mantains a reference to all the main copmponents of the Gui,
 * in this way it's possible to divide the responsability of the actions &
 * every object know only about of himself & the mediator.
 * Other actions are made through the mediator without knowing who will actually
 * do it.
 *

 * @author Federico Granata
 * Date: 3-giu-2003
 * Time: 16.54.45

 * Heavily modified by Bertoli Marco 2-giu-2005

 * Modified by Francesco D'Aquino

 * Modyfied by Bertoli Marco to support JGraph 5.8 - 21/mar/2006

 */
public class Mediator implements GuiInterface {
    // making it final allows the compiler to skip code generation when false
    private GraphMouseListner mouseListner;
    // Dialog factory
    private DialogFactory dialogFactory;
    // Cell factory
    private CellFactory cellFactory;

    private AbstractJmodelAction closeModel, newModel, openHelp, openModel,
    saveModel, 	setConnect, actionCopy , actionCut, setOptions, actionPaste,
    setSelect, actionDelete, simulate, solveAnalitic, solveApp,
    editUserClasses, editMeasures, switchToExactSolver, exit,
    // Bertoli Marco
    editDefaults, saveModelAs, pauseSimulation, stopSimulation, editSimParams,
    showResults, about, addBlockingRegion, takeScreenShot,
    //end

    // Conti Andrea
    editUndo, editRedo;
    //end

    private JmtJGraph graph;
    private MainWindow mainWindow;

    protected Object[] cells;
    protected Map cellsAttr;

    public static boolean advanced;
    private Cursor cursor;
    private Cursor oldCursor;

    // Bertoli Marco
    private JMODELModel model;
    private JFrame resultsWindow;
    private JmtClipboard clipboard;
    private DispatcherThread dispatcher = null; // To control simulation
    private ModelLoader modelLoader = new ModelLoader(ModelLoader.JMODEL); // To Save / Load
    private JMTToolBar componentBar;
    // end

    // Francesco D'Aquino
    private ModelChecker mc;
    private JModelProblemsWindow pw;
    private PADispatcherThread batchThread;
    private PAProgressWindow progressWindow;
    private AbstractJmodelAction editPAParams;
    // end

    public Mediator(final JmtJGraph graph, MainWindow mainWindow) {
        this.mainWindow = mainWindow;
        dialogFactory = new DialogFactory(mainWindow);
        cellFactory = new CellFactory(this);
        this.graph = graph;
        closeModel = new CloseModel(this);
        newModel = new NewModel(this);
        openHelp = new OpenHelp(this);
        openModel = new OpenModel(this);
        saveModel = new SaveModel(this);
        setConnect = new SetConnectState(this);
        actionCopy = new ActionCopy(this);
        actionCut = new ActionCut(this);
        setOptions = new SetOptions(this);
        actionPaste = new ActionPaste(this);
        setSelect = new SetSelectState(this);
        actionDelete = new ActionDelete(this);
        simulate = new Simulate(this);

        solveAnalitic = new SolveAnalytic(this);
        solveApp = new SolveApprox(this);
        //Conti Andrea - undo
        undoManager = new GraphUndoManager();
        undoProxy = new UndoManagerProxy(undoManager);
        editUndo = new ActionUndo(this, undoManager);
        editRedo = new ActionRedo(this, undoManager);
        // end
        // Bertoli Marco
        pauseSimulation = new PauseSimulation(this);
        stopSimulation = new StopSimulation(this);
        exit = new Exit(this);
        clipboard = new JmtClipboard(this);
        editDefaults = new EditDefaults(this);
        saveModelAs = new SaveModelAs(this);
        editSimParams = new EditSimParams(this);
        editPAParams = new EditPAParams(this);
        showResults = new ShowResults(this);
        about = new About(this);
        addBlockingRegion = new AddBlockingRegion(this);
        takeScreenShot = new TakeScreenShot(this);
        editUserClasses = new EditUserClasses(this);
        editMeasures = new EditMeasures(this);
        switchToExactSolver = new SwitchToExactSolver(this);
        // Initialize new Component bar
        componentBar = new ComponentBar(this);
    }
    
    /**
     * Creates a toolbar to be displayed in main window.
     * @return created toolbar.
     */
    public JMTToolBar createToolbar() {
        JMTToolBar toolbar = new JMTToolBar(JMTImageLoader.getImageLoader());
        // Builds an array with all actions to be put in the toolbar
        AbstractJmodelAction[] actions = new AbstractJmodelAction[] {
                newModel, openModel, saveModel, null, 
                //editUndo, editRedo, null,
                actionCut, actionCopy, actionPaste, null,
                editUserClasses, editMeasures, editSimParams, editPAParams, null,
                switchToExactSolver, null, simulate, pauseSimulation, stopSimulation, showResults, null,
                editDefaults, openHelp
        };
        toolbar.populateToolbar(actions);
        return toolbar;
    }
    
    /**
     * Creates a menu to be displayed in main window.
     * @return created menu.
     */
    public JMTMenuBar createMenu() {
        JMTMenuBar menu = new JMTMenuBar(JMTImageLoader.getImageLoader());
        // File menu
        MenuAction action = new MenuAction("File", new AbstractJmodelAction[] {
                newModel, openModel, saveModel, saveModelAs, closeModel, null, exit
        });
        menu.addMenu(action);
        
        // Edit menu
        action = new MenuAction("Edit", new AbstractJmodelAction[] {
                //editUndo, editRedo, null
                actionCut, actionCopy, actionPaste, actionDelete, null, takeScreenShot
        });
        menu.addMenu(action);
        
        // Define menu
        action = new MenuAction("Define", new AbstractJmodelAction[] {
                editUserClasses, editMeasures, editSimParams, editPAParams, null, editDefaults
        });
        menu.addMenu(action);
        
        // Solve menu
        action = new MenuAction("Solve", new AbstractJmodelAction[] {
                simulate, pauseSimulation, stopSimulation, null, switchToExactSolver, null, showResults
        });
        menu.addMenu(action);

        // Help menu
        action = new MenuAction("Help", new AbstractJmodelAction[] {
                openHelp, null, about
        });
        menu.addMenu(action);

        return menu;
    }
    
    public JMTToolBar getComponentBar() {
        return componentBar;
    }

    public void setMouseListner(GraphMouseListner mouseListner) {
        this.mouseListner = mouseListner;
    }

    private File openedArchive;

    // Bertoli Marco
    public AbstractJmodelAction getEditUserClasses() {
        return editUserClasses;
    }

    public AbstractJmodelAction getExit() {
        return exit;
    }

    public JmodelStationDefinition getStationDefinition() {
        return model;
    }

    public JmodelClassDefinition getClassDefinition() {
        return model;
    }

    public SimulationDefinition getSimulationDefinition() {
        return model;
    }

    public BlockingRegionDefinition getBlockingRegionDefinition() {
        return model;
    }

    public void enableAddBlockingRegion(boolean state) {
        addBlockingRegion.setEnabled(state);
    }

    public AbstractJmodelAction getEditDefaults() {
        return editDefaults;
    }

    public AbstractJmodelAction getAddBlockingRegion() {
        return addBlockingRegion;
    }

    public AbstractJmodelAction getSaveModelAs() {
        return saveModelAs;
    }

    public AbstractJmodelAction getPauseSimulation() {
        return pauseSimulation;
    }

    public AbstractJmodelAction getStopSimulation() {
        return stopSimulation;
    }

    public AbstractJmodelAction getEditSimParams() {
        return editSimParams;
    }

    public AbstractJmodelAction getEditPAParams() {
        return editPAParams;
    }

    public AbstractJmodelAction getShowResults() {
        return showResults;
    }

    public AbstractJmodelAction getAbout() {
        return about;
    }
    //end



    //Conti Andrea - undo
    private GraphUndoManager undoManager;
    private UndoManagerProxy undoProxy;

    public void undo() {
        undoManager.undo();
    }

    public void redo() {
        undoManager.redo();
    }

    public void enableUndoAction(boolean state) {
        editUndo.setEnabled(state);
    }

    public void enableRedoAction(boolean state) {
        editRedo.setEnabled(state);
    }

    public AbstractJmodelAction getUndoAction() {
        return editUndo;
    }

    public AbstractJmodelAction getRedoAction() {
        return editRedo;
    }

    public GraphUndoManager getUndoManager() {
        return undoManager;
    }

    public void setUndoManager(GraphUndoManager um) {
        undoManager = um;
    }

    //end


    public void setConnectState() {
        setSelect.setEnabled(true);
        //DEK (Federico Granata) 14-11-2003
        oldCursor = cursor;
        cursor = new Cursor(Cursor.CROSSHAIR_CURSOR);
        setGraphCursor(cursor);

        //end 14-11-2003
        mouseListner.setConnectState();
    }

    public void setCopyState() {
        setSelect.setEnabled(true);
//		mouseListner.setCopyState();
    }

    public void setCutState() {
        setSelect.setEnabled(true);
//		mouseListner.setCutState();
    }

    public void setInsertState(String className) {
        setSelect.setEnabled(true);
        mouseListner.setInsertState(className);
    }

    public void setSelectState() {
        setSelect.setEnabled(true);
        mouseListner.setSelectState();
        //DEK (Federico Granata)
//		mainWindow.getAlbero().setSelectedButton(true);
    }

    public AbstractJmodelAction getDeleteAction() {
        return actionDelete;
    }

    public void activateSelect() {
        setSelect.setEnabled(true);
        componentBar.clickButton(setSelect);
    }

    public void enableCutAction(boolean state) {
        actionCut.setEnabled(state);
    }

    public void enablePasteAction(boolean state) {
        actionPaste.setEnabled(state);
    }

    public void enableCopyAction(boolean state) {
        actionCopy.setEnabled(state);
    }

    public void enableDeleteAction(boolean state) {
        actionDelete.setEnabled(state);
    }

    public AbstractJmodelAction getTakeScreenShot() {
        return takeScreenShot;
    }

    public void setHandle(CellHandle handle) {
        this.mouseListner.setHandle(handle);
    }

    public AbstractJmodelAction getCloseModel() {
        return closeModel;
    }

    public AbstractJmodelAction getNewModel() {
        return newModel;
    }

    public AbstractJmodelAction getOpenHelp() {
        return openHelp;
    }

    public AbstractJmodelAction getOpenModel() {
        return openModel;
    }

    public AbstractJmodelAction getSaveModel() {
        return saveModel;
    }

    public AbstractJmodelAction getSetConnect() {
        return setConnect;
    }

    public AbstractJmodelAction getCopyAction() {
        return actionCopy;
    }

    public AbstractJmodelAction getCutAction() {
        return actionCut;
    }

    public AbstractJmodelAction getSetOptions() {
        return setOptions;
    }

    public AbstractJmodelAction getPasteAction() {
        return actionPaste;
    }

    public AbstractJmodelAction getSetSelect() {
        return setSelect;
    }

    public AbstractJmodelAction getSimulate() {
        return simulate;
    }

    public AbstractJmodelAction getSolveAnalitic() {
        return solveAnalitic;
    }

    public AbstractJmodelAction getSolveApp() {
        return solveApp;
    }

    /**
     * Gets cell factory to create new graph cells
     * @return cell factory
     */
    public CellFactory getCellFactory() {
        return cellFactory;
    }

    public void newModel() {
        if (checkForSave("<html>Save changes before creating a new model?</html>")) return;
        resetMouseState();
        graph = new JmtJGraph(this);
        graph.setModel(new DefaultGraphModel());
        // Sets the cloneable flag to 'false'
        graph.setCloneable(false);
        graph.setGridSize(20);
        graph.setGridVisible(true);
        if (advanced) graph.setBackground(new Color(120, 120, 120));

        graph.addMouseListener(mouseListner);
        graph.addMouseMotionListener(mouseListner);

        //Conti Andrea
        undoProxy.discardAllEdits();
        graph.getModel().addUndoableEditListener(undoProxy);
        //end

        // Bertoli Marco
        // Instantiates a new JMODELModel data structure to store the entire model
        model = new JMODELModel();
        // end

        mainWindow.setGraph(graph);
        closeModel.setEnabled(true);
        saveModel.setEnabled(true);
        saveModelAs.setEnabled(true);
        editMeasures.setEnabled(true);
        // Bertoli Marco
        // Show only insert options on ComponentBar
        componentBar.clearButtonGroupSelection(0);
        componentBar.enableButtonGroup(0, true);

        // Disables show results button and measure definition, until simulation
        showResults.setSelected(false);
        showResults.setEnabled(false);
        if (resultsWindow != null)
            resultsWindow.dispose();
        resultsWindow = null;

        // Disables cut/copy/delete (leave paste enabled as clipboard is not flushed)
        enableCopyAction(false);
        enableCutAction(false);
        enableDeleteAction(false);
        //end

        // Disables creation of blocking region
        enableAddBlockingRegion(false);
        setConnect.setEnabled(false);
        setSelect.setEnabled(false);

        // Enable the action to perform editing user classes
        editUserClasses.setEnabled(true);
        switchToExactSolver.setEnabled(true);
        // Enables the botton to start simualtion
        simulate.setEnabled(true);
        editSimParams.setEnabled(true);
        editPAParams.setEnabled(true);
        takeScreenShot.setEnabled(true);
        openedArchive = null;
        // Free same resources by forcing a garbage collection
        System.gc();
    }

    /**
     * Opens a model from a data file.
     * <br> Author: Bertoli Marco
     */
    public void openModel() {
        if (checkForSave("<html>Save changes before opening a saved model?</html>")) return;
        JMODELModel tmpmodel = new JMODELModel();
        int state = modelLoader.loadModel(tmpmodel, mainWindow);
        if (state == ModelLoader.SUCCESS || state == ModelLoader.WARNING) {
            resetMouseState();
            // Avoid checkForSave again...
            if (model != null)
                model.resetSaveState();
            newModel();
            // At this point loading was successful, so substitutes old model with loaded one
            model = tmpmodel;
            this.populateGraph();
            setSelect.setEnabled(true);
            componentBar.clickButton(setSelect);
            openedArchive = modelLoader.getSelectedFile();
            // Removes selection
            graph.clearSelection();
            // If model contains results, enable Results Window
            if (model.containsSimulationResults()) {
                if (model.isParametricAnalysisEnabled()) {
                    this.setResultsWindow(new PAResultsWindow(model.getParametricAnalysisModel(),(PAResultsModel)model.getSimulationResults()));
                    showResults.setEnabled(true);
                }
                else{
                    this.setResultsWindow(new ResultsWindow(model.getSimulationResults()));
                    showResults.setEnabled(true);
                }
            }
            model.resetSaveState();
            System.gc();
        }
        else if (state == ModelLoader.FAILURE)
            showErrorMessage(modelLoader.getFailureMotivation());
        // Shows warnings if any
        if (state == ModelLoader.WARNING) {
            new WarningWindow(modelLoader.getLastWarnings(), mainWindow).show();
        }


    }

    public void closeModel() {
        // Checks if there's an old graph to save
        if (checkForSave("<html>Save changes before closing?</html>")) return;
        resetMouseState();

        //clear undo history
        graph.getModel().removeUndoableEditListener(undoProxy);
        undoProxy.discardAllEdits();
        //end
        //graph.setModel(null); //wreaks quite a bit of havoc
        mainWindow.removeGraph();
        graph = null;
        closeModel.setEnabled(false);
        saveModel.setEnabled(false);
        editMeasures.setEnabled(false);
        saveModelAs.setEnabled(false);
        componentBar.clearButtonGroupSelection(0);
        componentBar.enableButtonGroup(0, false);
        setConnect.setEnabled(false);
        actionCopy.setEnabled(false);
        actionCut.setEnabled(false);
        actionPaste.setEnabled(false);
        actionDelete.setEnabled(false);
        setSelect.setEnabled(false);
        simulate.setEnabled(false);
        solveAnalitic.setEnabled(false);
        solveApp.setEnabled(false);
        editUserClasses.setEnabled(false);
        editMeasures.setEnabled(false);
        switchToExactSolver.setEnabled(false);
        // Disables the botton to start simualtion
        simulate.setEnabled(false);
        editSimParams.setEnabled(false);
        editPAParams.setEnabled(false);
        takeScreenShot.setEnabled(false);
        // Disables show results button and measure definition
        showResults.setSelected(false);
        showResults.setEnabled(false);
        if (resultsWindow != null)
            resultsWindow.dispose();
        resultsWindow = null;
        openedArchive = null;
        // Free same resources by forcing a garbage collection
        System.gc();
    }

    /** Inserts a new cell (vertex) in the desired point into the graph.
     *
     * @param newCell the new cell
     * @param pt point in absolute coordinates in the
     */
    public void InsertCell(Point2D pt, JmtCell newCell) {
        pt = graph.snap(pt);
        Object[] arg = new Object[]{newCell};
        graph.getModel().insert(arg, newCell.setAttributes(pt, graph), null, null, null);
        // Puts new cell on back to go under blocking regions
        graph.getModel().toBack(new Object[]{newCell});
        newCell.resetParent();
        setConnect.setEnabled(true);
    }

    /** Set the state of mouse listner to select & passes the event to the
     * listner as if press event is generated.
     *
     * @param e
     */
    public void selectAt(MouseEvent e) {
        activateSelect();
        mouseListner.mousePressed(e);
    }

    /**
     * Determines whether this component is enabled. An enabled component
     * can respond to user input and generate events. Components are
     * enabled initially by default. A component may be enabled or disabled by
     * calling its <code>setEnabled</code> method.
     * @return <code>true</code> if the component is enabled,
     * 		<code>false</code> otherwise
     * @since JDK1.0
     */
    public boolean isGraphEnabled() {
        return graph.isEnabled();
    }

    public void graphRequestFocus() {
        graph.requestFocus();
    }

    public int getTolerance() {
        return graph.getTolerance();
    }

    public Rectangle2D fromScreen(Rectangle2D r) {
        return graph.fromScreen(r);
    }

    public Point2D fromScreen(Point2D p) {
        return graph.fromScreen(p);
    }

    /**
     * Returns this graph's graphics context, which lets you draw
     * on a component. Use this method get a <code>Graphics</code> object and
     * then invoke operations on that object to draw on the component.
     * @return this components graphics context
     */
    public Graphics2D getGraphGraphics() {
        //DEK (Federico Granata) 17-11-2003
        return (Graphics2D) graph.getGraphics();
        //end 17-11-2003
//		return mainWindow.getGraphics();
    }


    public CellView getNextViewAt(CellView current, double x, double y) {
        return graph.getNextViewAt(current, x, y);
    }

    /**
     * Returning true signifies the marquee handler has precedence over
     * other handlers, and is receiving subsequent mouse events.
     */
    public boolean isForceMarqueeEvent(MouseEvent e) {
        return ((JmtGraphUI) graph.getUI()).isForceMarqueeEvent(e);
    }

    /**
     * Returns the number of clicks for editing of the graph to start.
     */
    public int getEditClickCount() {
        return graph.getEditClickCount();
    }

    /**
     * Returning true signifies a mouse event on the cell should toggle
     * the selection of only the cell under mouse.
     */
    public boolean isToggleSelectionEvent(MouseEvent e) {
        return ((JmtGraphUI) graph.getUI()).isToggleSelectionEvent(e);
    }

    /**
     * Returns true if the cell is currently selected.
     * @param cell an object identifying a cell
     * @return true if the cell is selected
     */
    public boolean isCellSelected(Object cell) {
        return graph.isCellSelected(cell);
    }

    /**
     * Messaged to update the selection based on a MouseEvent over a
     * particular cell. If the event is a toggle selection event, the
     * cell is either selected, or deselected. Otherwise the cell is
     * selected.
     */
    public void selectCellForEvent(Object cell, MouseEvent e) {
        ((JmtGraphUI) graph.getUI()).selectCellForEvent(cell, e);
    }

    /**
     * Scroll the graph for an event at <code>p</code>.
     */
    public void autoscroll(Point p) {
        JmtGraphUI.autoscroll(graph, p);
    }

    /**
     * Gets the cursor set in the graph. If the graph does
     * not have a cursor set, the cursor of its parent is returned.
     * If no cursor is set in the entire hierarchy,
     * <code>Cursor.DEFAULT_CURSOR</code> is returned.
     */
    public Cursor getGraphCursor() {
        return graph.getCursor();
    }

    /**
     * Sets graph cursor
     * @param cursor to be setted
     */
    public void setGraphCursor(Cursor cursor) {
        graph.setCursor(cursor);
    }

    /**
     * Returns true if the graph is being edited.  The item that is being
     * edited can be returned by getEditingCell().
     */
    public boolean isGraphEditing() {
        return graph.getUI().isEditing(graph);
    }

    /**
     * Returns the given point applied to the grid.
     * @param p a point in screen coordinates.
     * @return the same point applied to the grid.
     */
    public Point2D snap(Point2D p) {
        return graph.snap(p);
    }

    /**
     * Upscale the given point in place, ie.
     * using the given instance.
     * @param p the point to be upscaled
     * @return the upscaled point instance
     */
    public Point2D toScreen(Point2D p) {
        return graph.toScreen(p);
    }

    /**
     * Gets the background color of graph.
     * @return this component's background color; if this component does
     * 		not have a background color,
     *		the background color of its parent is returned
     */
    public Color getGraphBackground() {
        return graph.getBackground();
    }

    /**
     * Returns the current marquee color of the graph.
     */
    public Color getGraphMarqueeColor() {
        return graph.getMarqueeColor();
    }


    public void connect(Point2D start, Point2D current, PortView inPort, PortView outPort) {
        Point2D p = fromScreen(start);
        Point2D p2 = fromScreen(current);
        if (inPort != null && outPort != null) {
            ArrayList list = new ArrayList();
            list.add(p);
            list.add(p2);
            Map map = new Hashtable();
            GraphConstants.setPoints(map, list);
            GraphConstants.setRouting(map, GraphConstants.ROUTING_SIMPLE);
            GraphConstants.setRouting(map, JmtGraphConstants.ROUTING_JMT);
            GraphConstants.setEndFill(map, true);

            // 24/09/03 - Massimo Cattai //////////////////////////////////////////
            // Add a Line End Attribute
            GraphConstants.setLineEnd(map, GraphConstants.ARROW_CLASSIC);
            // 24/09/03 - end /////////////////////////////////////////////////////
            Map viewMap = new Hashtable();
            // ---- Adds connection into underlayng data structure -- BERTOLI MARCO
            Object sourceKey = ((CellComponent)((JmtCell)((OutputPort)(outPort.getCell())).getUserObject()).getUserObject()).getKey();
            Object targetKey = ((CellComponent)((JmtCell)((InputPort)(inPort.getCell())).getUserObject()).getUserObject()).getKey();
            JmtEdge connection = new JmtEdge(sourceKey, targetKey);
            viewMap.put(connection, map);
            Object[] insert = new Object[]{connection};
            ConnectionSet cs = new ConnectionSet();
            cs.connect(connection, outPort.getCell(), true);
            cs.connect(connection, inPort.getCell(), false);
            // Visualize connection only if it can be created into data structure
            if (model.setConnected(sourceKey, targetKey, true)) {
                graph.getModel().insert(insert, viewMap, cs, null, null);
            }
            // ---- End -- BERTOLI MARCO
        }

    }

    /**
     * Creates a connection between given source and target JmtCells
     * @param source source cell
     * @param target target cell
     * @return created component or null if connection between source and target cannot be created
     *
     * Author: Bertoli Marco
     */
    public JmtEdge connect (JmtCell source, JmtCell target) {
        return connect(source, target, false);
    }


    /**
     * Creates a connection between given source and target JmtCells
     * @param source source cell
     * @param target target cell
     * @param forced true if connection must be shown also if could not be created into data structure.
     * @return created component or null if connection between source and target cannot be created
     *
     * Author: Bertoli Marco
     */
    public JmtEdge connect (JmtCell source, JmtCell target, boolean forced) {
        // If one of parameter is null, returns null
        if (source == null || target == null)
            return null;
        // Retrives source and target keys to create connection
        Object sourceKey = ((CellComponent)source.getUserObject()).getKey();
        Object targetKey = ((CellComponent)target.getUserObject()).getKey();
        // Initializes correct layout for routing edges
        Map map = new Hashtable();
        GraphConstants.setRouting(map, GraphConstants.ROUTING_SIMPLE);
        GraphConstants.setRouting(map, JmtGraphConstants.ROUTING_JMT);
        GraphConstants.setLineEnd(map, GraphConstants.ARROW_CLASSIC);
        GraphConstants.setEndFill(map, true);
        Map viewMap = new Hashtable();
        JmtEdge connection = new JmtEdge(sourceKey, targetKey);
        viewMap.put(connection, map);
        Object[] insert = new Object[]{connection};
        ConnectionSet cs = new ConnectionSet();
        // Finds sourcePort
        Iterator it;
        it = source.getChildren().iterator();
        DefaultPort tmpPort, sourcePort, targetPort;
        sourcePort = null;
        while (it.hasNext()) {
            tmpPort = (DefaultPort)it.next();
            if (tmpPort instanceof OutputPort)
                sourcePort = tmpPort;
        }
        // Finds targetPort
        it = target.getChildren().iterator();
        targetPort = null;
        while (it.hasNext()) {
            tmpPort = (DefaultPort)it.next();
            if (tmpPort instanceof InputPort)
                targetPort = tmpPort;
        }
        if (sourcePort != null && targetPort != null) {
            cs.connect(connection, sourcePort, true);
            cs.connect(connection, targetPort, false);
            // Adds connection to the graph only if it can be created into data structure
            if (model.setConnected(sourceKey, targetKey, true) || forced) {
                graph.getModel().insert(insert, viewMap, cs, null, null);
                return connection;
            }
        }
        return null;
    }

    /**
     * repaints the graph component
     */
    public void graphRepaint() {
        graph.repaint();
    }

    /**
     * Returns the parent of <I>child</I> in the model.
     * <I>child</I> must be a node previously obtained from
     * this data source. This returns null if <i>child</i> is
     * a root in the model.
     *
     * @param   child  a node in the graph, obtained from this data source
     * @return  the parent of <I>child</I>
     */
    public Object getParent(Object child) {
        return graph.getModel().getParent(child);
    }

    /** gets the first portView of the input port of the cell at position
     *
     * @param x
     * @param y
     * @return portView of the input port
     */
    public PortView getInPortViewAt(int x, int y) {
        return (PortView) graph.getGraphLayoutCache().
                getMapping(graph.getInPortAt(x, y), false);
    }

    /** gets the first portView of the output port of the cell at position
     *
     * @param x
     * @param y
     * @return portView of the output port
     */
    public PortView getOutPortViewAt(int x, int y) {
        return (PortView) graph.getGraphLayoutCache().
                getMapping(graph.getOutPortAt(x, y), false);
    }

    /**
     * Returns if a given cell is visible on graph
     * @param cell
     * @return true iff cell is visible
     */
    public boolean isCellVisible(Object cell) {
        return ((JmtGraphUI) graph.getUI()).getGraphLayoutCache().isVisible(cell);
    }

    /**
     * Returns the views for the specified array of cells. Returned
     * array may contain null pointers if the respective cell is not
     * mapped in this view and <code>create</code> is <code>false</code>.
     */
    public CellView getViewOfCell(Object cell, boolean create) {
        return ((JmtGraphUI) graph.getUI()).getGraphLayoutCache().getMapping(cell, create);
    }

    /**
     * Selects the specified cell and initiates editing.
     * The edit-attempt fails if the <code>CellEditor</code>
     * does not allow
     * editing for the specified item.
     */
    public void startEditingAtCell(Object cell) {
        graph.startEditingAtCell(cell);
        if ((cell != null) && (cell instanceof JmtCell)) {
            StationParameterPanel stationPanel = new jmt.gui.common.panels.StationParameterPanel(model, model,
                            ((CellComponent)((JmtCell)cell).getUserObject()).getKey());
            // Adds on the top a panel to change station name
            stationPanel.add(
                    new StationNamePanel(model, ((CellComponent)((JmtCell)cell).getUserObject()).getKey()),
                    BorderLayout.NORTH);
            dialogFactory.getDialog(stationPanel,
                    "Editing " + ((JmtCell)cell).getUserObject().toString() + " Properties...");
        }
        // Blocking region editing
        else if ((cell != null) && (cell instanceof BlockingRegion)) {
            Object regionKey = ((BlockingRegion)cell).getKey();
            dialogFactory.getDialog(new BlockingRegionParameterPanel(model,
                    model,
                    regionKey),
                    "Editing " + model.getRegionName(regionKey) + " Properties...");
        }
    }

    /**
     * Cuts the selection of the graph.
     */
    public void cutSelection() {
        // Bertoli Marco
        clipboard.cut();
    }

    /**
     * Pastes the selection on the graph.
     */
    public void pasteSelection() {
        // Bertoli Marco
        clipboard.paste();

        // If more than one stations are present enables link button
        if (graph.getModel().getRootCount() > 1)
            setConnect.setEnabled(true);
        // If one station is present show select button
        if (graph.getModel().getRootCount() >= 1) {
            activateSelect();
        }
    }

    /**
     * Copies the selection of the graph.
     */
    public void copySelection() {
        // Bertoli Marco
        clipboard.copy();
    }

    /**
     * Displays an error message in the panel that is responable to make the
     * user understand why a certain operation is not valid.
     *
     * @param message error to be displayed.
     */
    public void displayGraphErrMsg(String message) {
        //per ora faccio printare nell'output, assolutamente provvisorio.
        System.out.println("message = " + message);
    }

    /**
     * Deletes all the vertex & edgees that are selected. it deletes also the
     * edges that are connected to the eliminated vertexes.

     * Bertoli Marco 03-06-2005
     */
    public void deleteSelected() {
        Object cells[] = graph.getSelectionCells();
        GraphModel graphmodel = graph.getModel();

        // If a cell is a blocking region avoid removing its edges and
        // select its element at the end of the removal process
        Set edges = new HashSet();
        Set select = new HashSet();

        // Set with all regions that can be deleted as its child were removed
        Set regions = new HashSet();
        // Set with all JmtCells that we are removing
        Set jmtCells = new HashSet();

        for (int i=0; i<cells.length;i++)
            if (!(cells[i] instanceof BlockingRegion)) {
                // Adds edge for removal
                edges.addAll(DefaultGraphModel.getEdges(graphmodel, new Object[]{cells[i]}));
                // Stores parents information and cell
                if (cells[i] instanceof JmtCell) {
                    if (((JmtCell)cells[i]).getParent() instanceof BlockingRegion)
                        regions.add(((JmtCell)cells[i]).getParent());
                    jmtCells.add(cells[i]);
                }
            }
            else {
                // Adds node for selection
                Object[] nodes = graph.getDescendants(new Object[]{cells[i]});
                for (int j=0; j<nodes.length; j++)
                    if (nodes[j] instanceof JmtCell || nodes[j] instanceof JmtEdge)
                    select.add(nodes[j]);
                // Removes blocking region from data structure
                model.deleteBlockingRegion(((BlockingRegion)cells[i]).getKey());
            }


        if (!edges.isEmpty()) {
            graphmodel.remove(edges.toArray());
        }
        //removes cells from graph
        graphmodel.remove(cells);

        // Checks if all children of a blocking region have been removed
        Iterator it = regions.iterator();
        while(it.hasNext()) {
            jmtCells.add(null);
            BlockingRegion region = (BlockingRegion) it.next();
            List child = region.getChildren();
            boolean empty = true;
            for (int i=0; i<child.size(); i++)
                if (child.get(i) instanceof JmtCell && !jmtCells.contains(child.get(i))) {
                    empty = false;
                    break;
                }
            if (empty) {
                model.deleteBlockingRegion(region.getKey());
                graphmodel.remove(new Object[] {region});
            }
        }

        // Removes cells from data structure
        for (int i=0; i<cells.length; i++)
            if (cells[i] instanceof JmtCell)
                model.deleteStation(((CellComponent)((JmtCell)cells[i]).getUserObject()).getKey());
            else if (cells[i] instanceof JmtEdge) {
                JmtEdge link = (JmtEdge)cells[i];
                model.setConnected(link.getSourceKey(), link.getTargetKey(), false);
            }

        // If no stations remains gray select and link buttons
        if (graph.getModel().getRootCount() == 0) {
            componentBar.clearButtonGroupSelection(0);
            setConnect.setEnabled(false);
            setSelect.setEnabled(false);
        }

        // Selects components from removed blocking regions
        if (select.size() > 0) {
            graph.setSelectionCells(select.toArray());
            // Resets parent information of cells that changed parent
            it = select.iterator();
            while (it.hasNext()) {
                Object next = it.next();
                if (next instanceof JmtCell)
                    ((JmtCell)next).resetParent();
            }
        }
    }

    /**
     * Shows the panel that opens when the rigth button is clicked.
     *
     * @param p point where the right button is cliecked on the graph
     */
    public void showOPanel(Point p) {

    }


    /**
     * Saves the current model into current file if exists, otherwise calls saveModelAs()
     * Author: Bertoli Marco
     */
    public void saveModel() {
        if (openedArchive == null) {
            saveModelAs();
            return;
        }

        // Updates station positions into data structure
        updateStationPositions();
        int status = modelLoader.saveModel(model, mainWindow, openedArchive);
        switch (status) {
            case ModelLoader.SUCCESS:
                model.resetSaveState();
                break;
            case ModelLoader.FAILURE:
                showErrorMessage(modelLoader.getFailureMotivation());
                break;
        }
    }

    /**
     * Saves the current model into a user specified file.
     * Author: Bertoli Marco
     */
    public void saveModelAs() {
        // Updates station positions into data structure
        updateStationPositions();
        int status = modelLoader.saveModel(model, mainWindow, null);
        switch (status) {
            case ModelLoader.SUCCESS:
                model.resetSaveState();
                openedArchive = modelLoader.getSelectedFile();
                break;
            case ModelLoader.FAILURE:
                showErrorMessage(modelLoader.getFailureMotivation());
                break;
        }
    }

    /**
     * Updates station positions into data structure to reflect the one shown on jgraph
     * window. This method is called before saving model.
     * Author: Bertoli Marco
     */
    public void updateStationPositions() {
        Object key;
        Object[] cells = graph.getDescendants(graph.getRoots());
        for (int i = 0; i < cells.length; i++) {
            // Gets the cell object
            Object cell = cells[i];
            if (cell instanceof JmtCell) {
                key = ((CellComponent)((JmtCell) cell).getUserObject()).getKey();
                // Sets cell coordinate into data structure
                model.setStationPosition(key, getCellCoordinates((JmtCell)cell));
            }
        }
    }

    /**
     * Uses information retrived from data structure to recreate graph structure.
     * This method has to be called after loading a model.
     * <br>Author: Bertoli Marco
     */
    public void populateGraph() {
        Object[] stations = model.getStationKeys().toArray();
        HashMap cells = new HashMap();
        JmtCell cell;

        // Variables for auto-placement. Currently items are placed on a grid... Need to be improved!!!
        int count = 0;
        int X = 150; // distance on the X axis
        int Y = 50;  // distance on the Y axis
        int X0 = 50;
        int Y0 = 15;
        int colCount = (graph.getHeight() - 2*Y0) / Y;

        // Shows stations
        for (int i=0; i<stations.length; i++) {
            cell = cellFactory.createStationCell(stations[i]);
            Point2D position = model.getStationPosition(stations[i]);
            // If position is not present, auto-position this station
            // TODO implement a good algorithm for auto-placement... This is silly but avoids exceptions
            while (position == null) {
                Point tmp = new Point(X0+X*(count / colCount), Y0+Y*(count % colCount));
                if (!overlapCells(tmp, cell)) {
                    position = tmp;
                }
                count++;
            }
            InsertCell(position, cell);
            cells.put(stations[i], cell);
        }
        Vector forwardConnections;
        // Shows connections
        for (int i=0; i<stations.length; i++) {
            forwardConnections = model.getForwardConnections(stations[i]);
            for (int j=0; j<forwardConnections.size(); j++) {
                // Forces connection as it's already present into data structure
                connect((JmtCell)cells.get(stations[i]),
                        (JmtCell)cells.get(forwardConnections.get(j)), true);
            }

        }
        // Now adds blocking regions
        Vector regions = model.getRegionKeys();
        for (int i=0; i<regions.size(); i++) {
            Object key = regions.get(i);
            Set regionStation = new HashSet();
            Iterator stationKeys = model.getBlockingRegionStations(key).iterator();
            while (stationKeys.hasNext())
                regionStation.add(cells.get(stationKeys.next()));
            // Adds cells to blocking region
            addCellsToBlockingRegion(regionStation.toArray(), key);
        }

        graph.repaint();
    }

    /**
     * @return the system <code>JGraph</code>.
     */
    public JGraph getGraph() {
        return graph;
    }

    /**
     * Launches the <code>UserClass</code> editor.
     */
    public void editUserClasses() {
        dialogFactory.getDialog(new jmodelClassesPanel(model, model), "Define customer classes");
    }

    /**
     * Enables or not the <code>UserClass</code> editor function.
     */
    public void enableEditUserClasses(boolean state) {
        editUserClasses.setEnabled(state);
    }

    /**
     * Searches the cell with the given <i>name</i>.
     * @param name A given cell name.
     * @return <code>true</code> - if the searched cell will be found.
     */
    public boolean existCell(String name) {
        int nCells = graph.getModel().getRootCount();
        for (int i = 0; i < nCells; i++) {
            Object cell = graph.getModel().getRootAt(i);
            if (cell instanceof JmtCell) {
//				Map attributes = ((JmtCell) cell).getAttributes();
//				String cellName = (String) attributes.get("NAME");
                String cellName = ((JmtCell) cell).getUserObject().toString();
                if (cellName.equals(name))
                    return true;
            }
        }
        return false;
    }

    /**
     * This function will put selected cells in place avoiding overlapping with other cells
     * in graph window
     * <br>
     * Author: Bertoli Marco
     */
    public void putSelectedCellsInGoodPlace() {
        Object[] cells = graph.getDescendants(graph.getSelectionCells());
        for (int i=0; i<cells.length;i++)
            if(cells[i] instanceof JmtCell)
                putCellInGoodPlace((JmtCell) cells[i]);
    }

    /**
     * This function will put given cell in place avoiding overlapping with other cells
     * in graph window
     * <br>
     * Author: Bertoli Marco
     * @param cell Identifier of the cell to be moved
     */
    public void putCellInGoodPlace(JmtCell cell) {
        Rectangle bounds = GraphConstants.getBounds(cell.getAttributes()).getBounds();
        // Avoids negative starting point
        if (bounds.getX() < 0)
            bounds.setLocation(0, (int)bounds.getY());
        if (bounds.getY() < 0)
            bounds.setLocation((int)bounds.getX(), 0);

        Object[] overlapping = graph.getDescendants(graph.getRoots(bounds));
        Point2D zero = new Point(0,0);
        while (overlapping.length > 0) {
            // Moves bounds until it doesn't overlap with anything
            Point2D last = (Point2D) zero.clone();
            for (int j=0; j<overlapping.length; j++) {
                // Puts last to last corner of overlapping cells
                if (overlapping[j] instanceof JmtCell && overlapping[j] != cell) {
                    Rectangle2D b = GraphConstants.getBounds(((JmtCell)overlapping[j]).getAttributes());
                    // Consider only rectangles that intersects with given bound
                    if (b.intersects(bounds)) {
                        if (b.getMaxX() > last.getX())
                            last.setLocation(b.getMaxX(), last.getY());
                        if (b.getMaxY() > last.getY())
                            last.setLocation(last.getX(), b.getMaxY());
                    }
                }
            }
            // if last is still zero, only Blocking section were found overlapping
            // so leave everyting as before
            if (last.equals(zero))
                break;
            // Rounds last and moves bounds to found point
            bounds.setLocation(new Point((int)(last.getX()+.5),(int)(last.getY()+.5)));
            overlapping = graph.getDescendants(graph.getRoots(bounds));
        }

        // Puts this cell in found position
        GraphConstants.setBounds(cell.getAttributes(), bounds);
    }

    /**
     * Retrives the location of the given cell.
     * @param cell The given cell
     * @return The cell location
     */
    public Point2D getCellCoordinates(JmtCell cell) {
        Rectangle2D bounds = GraphConstants.getBounds(cell.getAttributes());
        return new Point2D.Double(bounds.getMinX(), bounds.getMinY());
    }

    /**
     * Checks whether the given cell overlaps an existing cell with its bounds.
     * @param p The point where the given cell will be inserted.
     * @param cell The given cell.
     * @return <code>true</code> - whether there's an overlapping situation.
     */
    public boolean overlapCells(Point2D p, JmtCell cell) {
        Map attributes;

        // Creates a rectangle representing the new cell bounds and position
        Dimension cellsize = cell.getSize(graph);
        Rectangle r = new Rectangle2D.Double(p.getX(), p.getY(), cellsize.getWidth(), cellsize.getHeight()).getBounds();

        // Gets all cells that can overlap with given one
        Object[] cells = graph.getDescendants(graph.getRoots(r));
        for (int i = 0; i < cells.length; i++) {
            // Gets the i-th cell
            Object c = cells[i];
            if (c instanceof JmtCell) {
                if (!c.equals(cell)) {
                    // Retrives the i-th cell attributes
                    attributes = ((JmtCell) c).getAttributes();
                    // Is there an intersection ?
                    if (GraphConstants.getBounds(attributes).intersects(r)) {
                        // Yes
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Checks whether the given region overlaps an existing cell with its bounds.
     * This method is used to control overlapping before inserting new cell into the
     * Jgraph.
     * @param p The point where the given cell will be inserted.
     * @param d The dimensions of cell to be inserted
     * @return <code>true</code> - whether there's an overlapping situation.
     *
     * Author: Bertoli Marco
     */
    public boolean overlapCells(Point p, Dimension d) {
         Rectangle r = new Rectangle(p, d);
         return graph.getRoots(r).length > 0;
    }

    /**
     * Generates the xml to send to simulation engine.
     *
     * Author: Bertoli Marco
     *
     * Modified by Francesco D'Aquino
     */
    public void startSimulation() {
        //if simulation is not in pause state
        if(!stopSimulation.isEnabled()){
            // Asks for confirmation before overwriting previous simulation data
            if (model.containsSimulationResults()) {
                // Find frame to show confirm dialog
                Component parent = mainWindow;
                if (resultsWindow != null && resultsWindow.isFocused())
                    parent = resultsWindow;

                int resultValue = JOptionPane.showConfirmDialog(parent,
                    "This operation will overwrite old simulation results."+
                    "Continue anyway?",
                    "JMT - Warning",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);
                if (resultValue == JOptionPane.NO_OPTION) {
                    return;
                }
            }
            // Correct eventual problems on preloading for closed classes
            model.manageJobs();
            mc = new ModelChecker(model,model,model, model,false);
            pw = new JModelProblemsWindow(mainWindow,mc,this);
            if (!mc.isEverythingOkNormal()) {
                    pw.show();
            }
            if ( mc.isEverythingOkNormal() || ((!mc.isEverythingOkNormal())&&(pw.continued()))){
                if (!model.isParametricAnalysisEnabled()) {
                    try {
                        // Removes previous ResultsWindow
                        if (resultsWindow != null) {
                            resultsWindow.dispose();
                            showResults.setEnabled(false);
                        }
                        File temp = File.createTempFile("~JModelSimulation",".xml");
                        temp.deleteOnExit();
                        XMLWriter.writeXML(temp, model);
                        // Creates results data structure
                        model.setSimulationResults(new ResultsModel(model.getPollingInterval()));
                        showResults.setEnabled(true);
                        dispatcher = new DispatcherThread(this, model,
                        (ResultsModel)model.getSimulationResults());
                        dispatcher.startSimulation(temp);
                    }
                    catch (Exception e) {
                    handleException(e);
                    }
                }
                else {
                    // Removes previous ResultsWindow
                    showResults.setEnabled(false);
                    if (resultsWindow != null) {
                        resultsWindow.dispose();
                    }
                    if (progressWindow == null) {
                        progressWindow = new PAProgressWindow(mainWindow,simulate,pauseSimulation,stopSimulation,model.getParametricAnalysisModel());
                    }
                    batchThread = new PADispatcherThread(this,model,progressWindow);
                    changeSimActionsState(false, true, true);
                    progressWindow.initialize(model.getParametricAnalysisModel().getNumberOfSteps());
                    progressWindow.start();
                    progressWindow.show();
                    batchThread.start();
                }
            }
        }else{
            if (!model.isParametricAnalysisEnabled()) dispatcher.restartSimulation();
            else batchThread.restartSimulation();
        }
    }

    /**
     * Stops current simulation, aborting all measures
     *
     * Author: Bertoli Marco
     */
    public void stopSimulation() {
        if(stopSimulation.isEnabled()) {
            if (!model.isParametricAnalysisEnabled()) dispatcher.stopSimulation();
            else {
                batchThread.stopSimulation();
            }
        }
    }

    /**
     * Pauses current simulation
     *
     * Author: Bertoli Marco
     *
     * Modified by Francesco D'Aquino
     */
    public void pauseSimulation() {
        if (model.isParametricAnalysisEnabled()) {
            batchThread.pauseSimulation();
        }
        else dispatcher.pauseSimulation();
    }

    /**
     * Changes simulation action status. This method is called by DispatcherThread.
     * @param start state for start action
     * @param pause state for pause action
     * @param stop state for stop action
     */
    public void changeSimActionsState(boolean start,  boolean pause, boolean stop){
        simulate.setEnabled(start);
        stopSimulation.setEnabled(stop);
        pauseSimulation.setEnabled(pause);
    }


    /////////////////////////////////////////////
    // METHODS THAT MANAGE MEASURES

    /**
     * Launches the <code>Measure</code> editor.
     * Author: Bertoli Marco
     */
    public void editMeasures() {
        dialogFactory.getDialog(new MeasurePanel(model, model, model), "Define performance indices");
    }

    /**
     * @return A refernce to the action <code>EditMeasures</code>.
     */
    public AbstractJmodelAction getEditMeasures() {
        return editMeasures;
    }
    /////////////////////////////////////////////

    /**
     * Checks if there's an old graph to save. This methods is called when creates/closes/opens a graph.
     * @param msg The message to display.
     * @return <code>true</code> - whether the user accepts to save the graph, or he cancels the current action.
     */
    public boolean checkForSave(String msg) {
        // Checks if there's an old graph to save
        if (model != null && model.toBeSaved()) {
            int resultValue = JOptionPane.showConfirmDialog(mainWindow,
                    msg,
                    "JMODEL - Warning",
                    JOptionPane.YES_NO_CANCEL_OPTION,
                    JOptionPane.WARNING_MESSAGE);
            if (resultValue == JOptionPane.YES_OPTION) {
                saveModel();
                return true;
            }
            if (resultValue == JOptionPane.CANCEL_OPTION) {
                return true;
            }
        }
        return false;
    }

    /**
     * @return A refernce to the action <code>SwitchToExactSolver</code>.
     */
    public AbstractJmodelAction getSwitchToWizard() {
        return switchToExactSolver;
    }

    // 13/10/03 - end /////////////////////////////////////////////////////



    public Cursor getOldCursor() {
        return oldCursor;
    }

    public void setOldCursor(Cursor oldCursor) {
        this.oldCursor = oldCursor;
    }

    public void setCursor(Cursor cursor) {
        oldCursor = this.cursor;
        this.cursor = cursor;
        setGraphCursor(cursor);
    }

    // --- Bertoli Marco ---------------------
    /**
     * Sends an exit signal to main window
     */
    public void exit() {
        // Send a closing signat to main window.
        mainWindow.dispatchEvent(new WindowEvent(mainWindow, WindowEvent.WINDOW_CLOSING));
    }

    /**
     * Shows a DefaultEditor to edit Defaults parameters
     */
    public void showDefaultsEditor() {
        DefaultsEditor.getInstance(mainWindow, DefaultsEditor.JMODEL).show();
    }

    /**
     * Used to reset mouseListener to default (to avoid File_Save / File_New
     * operations while in inserting mode)
     */
    public void resetMouseState() {
        mouseListner.setDefaultState();
        componentBar.clearButtonGroupSelection(0);
        if (graph != null)
            setGraphCursor(Cursor.getDefaultCursor());
    }

    /**
     * Returns true iff specified cell is editable. This is used by <code>SelectState</code>
     * to check if editor has to be showed upon double click event.
     * @param cell specified cell
     * @return true iff cell is editable
     */
    public boolean isCellEditable(Object cell) {
        return cell instanceof JmtCell || cell instanceof BlockingRegion;
    }

    /**
     * Shows a panel with catched exception
     * @param e exception to be shown
     */
    public void handleException(Exception e){
        e.printStackTrace();
        showErrorMessage(e.getMessage());
    }

    /**
     * Shows a panel with an error message
     * @param message specified error message
     */
    public synchronized void showErrorMessage(String message){
        Component parent = mainWindow;
        if (resultsWindow != null && resultsWindow.hasFocus())
            parent = resultsWindow;
        JOptionPane.showMessageDialog(parent, message,
                "Error", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Switch current model to JMVA exact solver
     */
    public void toJMVA(){
        mc = new ModelChecker(model,model,model, model,true);
        pw = new JModelProblemsWindow(mainWindow,mc,this);
        if (!mc.isEverythingOkToJMVA()) {
            pw.show();
        }
        if ( mc.isEverythingOkToJMVA() || ((!mc.isEverythingOkToJMVA())&&(pw.continued()))){
            if (checkForSave("<html>Save changes before switching?</html>")) return;
            //try {
                // New Converter by Bertoli Marco
                ExactModel output = new ExactModel();
                Vector res = ModelConverter.convertJSIMtoJMVA(model, output);
                ExactWizard jmva = new ExactWizard(output);
                // If problems are found, shows warnings
                if (res.size() > 0)
                    new WarningWindow(res, jmva).show();

                /* Old code to use XSLT transformer (really bugged and unfinished)

                File xmlTempFile = File.createTempFile("~SIMtoMVA", ".xml");
                xmlTempFile.deleteOnExit();
                File destFile = File.createTempFile("~MVA", ".xml");
                destFile.deleteOnExit();
                InputStream stream = XSDSchemaLoader.loadSchemaAsStream(XSDSchemaLoader.JSIM_TO_JMVA);
                if(stream==null){
                    System.out.println("stream is null");
                    return;
                }
                XMLWriter.writeXML(xmlTempFile, model);
                InputStream is = new BufferedInputStream(stream);
                Transformer transformer = TransformerFactory.newInstance().newTransformer(new StreamSource(is));
                StreamSource ssrc = new StreamSource(xmlTempFile);
                StreamResult srst = new StreamResult(destFile);
                transformer.transform(ssrc, srst);
                xmlTempFile.delete();
                ExactModel xm = new ExactModel();
                xm.loadDocument(new XMLUtils().loadXML(destFile));
                new ExactWizard(xm);
            }catch (Exception e) {
                handleException(e);
            }*/
        }
    }

    /**
     * Called when EditSimParams action is triggered
     */
    public void editSimulationParameters() {
        dialogFactory.getDialog(new SimulationPanel(model, model,model,this), "Define Simulation Parameters");
    }

    /**
     * Called when EditPAParams action is triggered
     */
    public void editPAParameters() {
        dialogFactory.getDialog(new ParametricAnalysisPanel(model, model,model,this), "Define What-if analysis parameters");
    }

    /**
     * Sets resultWindow to be shown. This method is used by pollerThread
     * @param rsw window to be set as current ResultsWindow
     */
    public void setResultsWindow(JFrame rsw) {
        this.resultsWindow = rsw;
        if (rsw instanceof ResultsWindow) {
            // Sets action for toolbar buttons
            ((ResultsWindow)rsw).addButtonActions(simulate, pauseSimulation, stopSimulation);
        }
        else {
            showResults.setEnabled(true);
        }
        // Adds a listener that will unselect Show results button upon results window closing
        rsw.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                showResults.setSelected(false);
            }
        });
    }

    /**
     * Called when showResults action is triggered
     * @param selected Tells if show results button is selected or not
     *
     * Modified by Francesco D'Aquino
     */
    public synchronized void showResultsWindow(boolean selected) {
        if (selected) {
            if (resultsWindow != null)
                resultsWindow.show();
        }
        else {
            if (resultsWindow != null)
                resultsWindow.hide();
        }
    }

    /**
     * Shows results window and forces show results button to be selected
     */
    public void showResultsWindow() {
        showResults.setSelected(true);
        showResultsWindow(true);
    }

    /**
     * Returns current ResultsWindow
     * @return current ResultsWindow or null if none was created
     */
    public JFrame getResultsWindow() {
        return resultsWindow;
    }

    /**
     * Returns current PAProgressWindow
     * @return current PAProgressWindow or null if none was created
     */
    public PAProgressWindow getPAProgressWindow() {
        return progressWindow;
    }

    /**
     * Shows about window
     */
    public void about() {
        AboutDialogFactory.showJMODEL(mainWindow);
    }

    /**
     * Tells if something is selected into graph window
     * @return true if something is selected
     */
    public boolean isSomethingSelected() {
        return graph.getSelectionCell() != null;
    }

    /**
     * Takes a screenshot of current jgraph. Shows a dialog to select image type and name
     */
    public void takeScreenShot() {
        graph.clearSelection();
        graph.showScreenShotDialog();
    }
    // --- end Bertoli Marco ---------------------

    // ---------------------------------  Francesco D'Aquino -----------------------
    /**
     *  Shows the panel to solve a problem
     */
    public void showRelatedPanel(int problemType,int problemSubType, Object relatedStation, Object relatedClass) {
        //if it is a no class error show the class panel
        if ((problemSubType == ModelChecker.NO_CLASSES_ERROR) && (problemType == ModelChecker.ERROR_PROBLEM) ) {
            dialogFactory.getDialog(new jmodelClassesPanel(model,model),"Manage User Classes");
            model.manageJobs();  //a close class may be added
        }
        //if it is a no station error show an error message dialog
        else if ((problemSubType == ModelChecker.NO_STATION_ERROR) && (problemType == ModelChecker.ERROR_PROBLEM)){
            JOptionPane.showMessageDialog(null,"Please insert at least one server or delay before starting simulation.", "Error",JOptionPane.ERROR_MESSAGE);
        }
        else if ( (problemSubType == ModelChecker.SIMULATION_ERROR) && (problemType == ModelChecker.ERROR_PROBLEM)) {
            dialogFactory.getDialog(new MeasurePanel(model,model,model),"Edit Performance Indices");
        }
        //if a measure is inconsistent (i.e have one or more 'null' field) show performance indices panel
        else if ( (problemSubType == ModelChecker.INCONSISTENT_MEASURE_ERROR) && (problemType == ModelChecker.ERROR_PROBLEM)) {
            dialogFactory.getDialog(new MeasurePanel(model,model,model),"Edit Performance Indices");
        }
        //if a measure was defined more than once ask to erase all redundant measure
        else if ((problemSubType == ModelChecker.DUPLICATE_MEASURE_ERROR) && (problemType == ModelChecker.ERROR_PROBLEM)) {
            int k = JOptionPane.showConfirmDialog(null,"Delete all redundant performance indices?\n","Redundant performance indices found",JOptionPane.ERROR_MESSAGE);
            if (k == 0) {
                mc.deleteRedundantMeasure();
            }
        }
        //if it is a reference station error show the class panel
        else if ((problemSubType == ModelChecker.REFERENCE_STATION_ERROR) && (problemType == ModelChecker.ERROR_PROBLEM)) {
            dialogFactory.getDialog(new jmodelClassesPanel(model,model),"Manage User Classes");
            model.manageJobs(); //a close class may be added
        }
        //if a source has been inserted in the model but no open classes defined show the class panel
        else if ((problemSubType == ModelChecker.SOURCE_WITH_NO_OPEN_CLASSES_ERROR) && (problemType == ModelChecker.ERROR_PROBLEM)) dialogFactory.getDialog(new jmodelClassesPanel(model,model),"Manage User Classes");
        //if there is a routing error show the station parameter panel
        else if ((problemSubType == ModelChecker.ROUTING_ERROR) && (problemType == ModelChecker.ERROR_PROBLEM)) {
            StationParameterPanel tempPanel = new StationParameterPanel(model,model,relatedStation);
            String stationName = model.getStationName(relatedStation);
            //set the station parameter panel to show the routing section
            tempPanel.showRoutingSectionPanel(relatedClass);
            dialogFactory.getDialog(tempPanel,"Editing " + stationName + " Properties...");
        }
        //if a class may be routed into a station whose forward stations are all sink show an error message
        else if ((problemSubType == ModelChecker.ALL_FORWARD_STATION_ARE_SINK_ERROR) && (problemType == ModelChecker.ERROR_PROBLEM)) {
            String stationName = model.getStationName(relatedStation);
            String className = model.getClassName(relatedClass);
            JOptionPane.showMessageDialog(null,"Close class " + className + " may be routed into "+stationName+" whose forward station are all sink.", "Error",JOptionPane.ERROR_MESSAGE);
        }
        //if no open classes defined but at least a sink has been defined show the class panel
        else if ((problemSubType == ModelChecker.SINK_BUT_NO_OPEN_CLASSES_ERROR) && (problemType == ModelChecker.ERROR_PROBLEM)){
            dialogFactory.getDialog(new jmodelClassesPanel(model,model),"Manage User Classes");
            //JOptionPane.showConfirmDialog(null,"Add an open class to the model?", "Error",JOptionPane.OK_CANCEL_OPTION,JOptionPane.ERROR_MESSAGE);
        }
        //if an open class defined but no sink have been defined show an error message
        else if ((problemSubType == ModelChecker.NO_SINK_WITH_OPEN_CLASSES_ERROR) && (problemType == ModelChecker.ERROR_PROBLEM)){
            JOptionPane.showMessageDialog(null,"Open classes defined but no sink, add a sink to the model.", "Error",JOptionPane.ERROR_MESSAGE);
        }
        //if an open class defined but no source show an error message
        else if ((problemSubType == ModelChecker.OPEN_CLASS_BUT_NO_SOURCE_ERROR) && (problemType == ModelChecker.ERROR_PROBLEM)){
            JOptionPane.showMessageDialog(null,"Open classes defined but no source, add a source to the model.", "Error",JOptionPane.ERROR_MESSAGE);
        }
        //if there is a station link error show an error message
        else if ((problemSubType == ModelChecker.STATION_LINK_ERROR) && (problemType == ModelChecker.ERROR_PROBLEM)) {
            String stationName = model.getStationName(relatedStation);
            JOptionPane.showMessageDialog(null,"The station " + stationName + " is not forward linked, add a forward link", "Error",JOptionPane.ERROR_MESSAGE);
        }
        else if ((problemType == ModelChecker.ERROR_PROBLEM) && (problemSubType == ModelChecker.JOIN_WITHOUT_FORK_ERROR)) {
            JOptionPane.showMessageDialog(null,"One or more join found but no fork. Please, remove all join or add a fork");
        }
        //if it is a reference station error show the class panel
        /*else if ((problemSubType == ModelChecker.OPEN_CLASS_REFERENCE_STATION_ERROR) && (problemType == ModelChecker.ERROR_PROBLEM)) {
            DialogFactory.getDialog(new jmodelClassesPanel(model,model),"Manage User Classes");
            model.manageJobs(); //a close class may be added
        }*/
        //used only in JMVA conversion
        else if ((problemSubType == ModelChecker.BCMP_DIFFERENT_QUEUEING_STRATEGIES_WARNING) && (problemType == ModelChecker.WARNING_PROBLEM)) {
            String name = this.getStationDefinition().getStationName(relatedStation);
            int k = JOptionPane.showConfirmDialog(null,"According to BCMP theorem hypothesis each station must have the same queue\nstrategy for each class, but different per class queue strategy were found at " + name + ".\nDo you want to edit "+ name+" queue strategy?\n\n","Mixed queue strategy found",JOptionPane.WARNING_MESSAGE);
            if (k==0) {
                StationParameterPanel tempPanel = new StationParameterPanel(model,model,relatedStation);
                //set the station parameter panel to show the queue section
                tempPanel.showQueueSectionPanel();
                dialogFactory.getDialog(tempPanel,"Editing " + name + " Properties...");
            }
        }
        //used only in JMVA conversion
        else if ((problemSubType == ModelChecker.BCMP_FCFS_DIFFERENT_SERVICE_TYPES_WARNING) && (problemType == ModelChecker.WARNING_PROBLEM)) {
            String name = this.getStationDefinition().getStationName(relatedStation);
            int k = JOptionPane.showConfirmDialog(null,"According to BCMP theorem hypothesis, a FCFS server must have the same service times for each class,\nbut at "+name+" the service strategy is mixed, i.e. both load dependent and independent were found.\nDo you want to edit " +name+ " service parameters?\n\n","Mixed service strategies found",JOptionPane.WARNING_MESSAGE);
            if (k==0) {
                StationParameterPanel tempPanel = new StationParameterPanel(model,model,relatedStation);
                //set the station parameter panel to show the queue section
                tempPanel.showServiceSectionPanel();
                dialogFactory.getDialog(tempPanel,"Editing " + name + " Properties...");
            }
        }
        //used only in JMVA conversion
        else if ((problemSubType == ModelChecker.BCMP_FCFS_EXPONENTIAL_WARNING) && (problemType == ModelChecker.WARNING_PROBLEM)) {
            String name = this.getStationDefinition().getStationName(relatedStation);
            int k = JOptionPane.showConfirmDialog(null,"According to BCMP theorem hypothesis, in a FCFS server all the service time distribution\nmust be exponential, but at "+name+" at least one non exponential distribution was found.\nDo you want to edit " +name+ " service parameters?\n\n","Non exponential distribution in FCFS server",JOptionPane.WARNING_MESSAGE);
            if (k==0) {
                StationParameterPanel tempPanel = new StationParameterPanel(model,model,relatedStation);
                //set the station parameter panel to show the queue section
                tempPanel.showServiceSectionPanel();
                dialogFactory.getDialog(tempPanel,"Editing " + name + " Properties...");
            }
        }
        //used only in JMVA conversion
        else if ((problemSubType == ModelChecker.BCMP_FCFS_DIFFERENT_SERVICE_TIMES_WARNING) && (problemType == ModelChecker.WARNING_PROBLEM)) {
            String name = this.getStationDefinition().getStationName(relatedStation);
            int k = JOptionPane.showConfirmDialog(null,"According to BCMP theorem hypothesis, in a FCFS server all the per class service time mean values\nmust be the same. If the service strategies are load dependent the mean value in each range\nhas to be the same for each class.\nDo you want to edit " +name+ " service parameters?\n\n","Non exponential distribution in FCFS server",JOptionPane.WARNING_MESSAGE);
            if (k==0) {
                StationParameterPanel tempPanel = new StationParameterPanel(model,model,relatedStation);
                //set the station parameter panel to show the queue section
                tempPanel.showServiceSectionPanel();
                dialogFactory.getDialog(tempPanel,"Editing " + name + " Properties...");
            }
        }
        //used only for to JMVA conversion, for non Random Routing routing strategy errors
        else if ((problemType == ModelChecker.WARNING_PROBLEM) && (problemSubType == ModelChecker.BCMP_NON_STATE_INDEPENDENT_ROUTING_WARNING)) {
            int k = JOptionPane.showConfirmDialog(null,"Convert all non state independent routing strategies to Random Routing?\n\nAccording to the BCMP theorem the routing probabilities must be independent from the state of the model.\nChoosing ok all non state independent routing strategies inside a station will be converted to Random Routing.\nDo you want to convert all non state independent routing strategies to Random Routing?\n\n","BCMP hypothesis not verified",JOptionPane.ERROR_MESSAGE);
            if (k == 0) mc.setAllStateDependentRoutingStrategyToRandomRouting();
        }
        //if there are more than one sink show a warning message
        else if  ((problemSubType == ModelChecker.MORE_THAN_ONE_SINK_WARNING) && (problemType == ModelChecker.WARNING_PROBLEM)) {
            JOptionPane.showMessageDialog(null, "If more than one sink is reacheable by the same open class the computed throughput may not be accurate.\nPlease check the model before starting simulation.", "Warning",JOptionPane.WARNING_MESSAGE);
        }
        //if a station (server or delay) is not backward connected show a warning message
        else if ((problemSubType == ModelChecker.NO_BACKWARD_LINK_WARNING) && (problemType == ModelChecker.WARNING_PROBLEM)) {
            String stationName = model.getStationName(relatedStation);
            JOptionPane.showMessageDialog(null, "The station " + stationName + " is not backward linked. Please check the model before starting simulation.", "Warning",JOptionPane.WARNING_MESSAGE);
        }
        else if ((problemSubType == ModelChecker.PARAMETRIC_ANALYSIS_MODEL_MODIFIED_WARNING) && (problemType == ModelChecker.WARNING_PROBLEM)) {
            String message = "Check parametric analysis model?\n\nThe parametric analysis model previously defined had become inconsistent with the \nsimulation model. It will be automatically modified when simulation will be started.\nDo you want to autocorrect and check parametric analysis panel?\n\n";
            int k = JOptionPane.showConfirmDialog(null,message,"Inconsistent parametric analysis model",JOptionPane.WARNING_MESSAGE);
            if (k==0) {
                model.getParametricAnalysisModel().checkCorrectness(true);
                ParametricAnalysisPanel paPanel = new ParametricAnalysisPanel(model,model,model,this);
                dialogFactory.getDialog(paPanel,"Edit what-if analysis parameters");
            }
        }
        else if ((problemSubType == ModelChecker.PARAMETRIC_ANALYSIS_NO_MORE_AVAIBLE_WARNING) && (problemType == ModelChecker.WARNING_PROBLEM)) {
            String message = "Parametric analysis was set, but no parametric analysis is now avaible,\nsince the simulation model was changed. It is only possible to execute normal simulation.\nDo you wish to continue anyway?\n\n";
            int k = JOptionPane.showConfirmDialog(null,message,"Parametric analysis not avaible",JOptionPane.WARNING_MESSAGE);
            if (k == 0) {
                model.setParametricAnalysisEnabled(false);
                model.setParametricAnalysisModel(null);
            }
        }
        else if ((problemSubType == ModelChecker.FORK_WITHOUT_JOIN_WARNING) && (problemType == ModelChecker.WARNING_PROBLEM)) {
            JOptionPane.showMessageDialog(null,"A fork was found but no join. Please check the topology");
        }
        else if ((problemSubType == ModelChecker.EMPTY_BLOCKING_REGION)) {
            int k = JOptionPane.showConfirmDialog(null,"Delete empty finite capacity regions?\n","Empty finite capacity regions found",JOptionPane.ERROR_MESSAGE);
            if (k == 0) {
                mc.deleteEmptyBlockingRegions();
            }
        }
        else if (problemSubType == ModelChecker.PRELOADING_WITH_BLOCKING) {
            editSimulationParameters();
        }
        
    }

    /**
     * Shows the class panel
     */
    public void showClassPanel() {
        dialogFactory.getDialog(new jmodelClassesPanel(model,model),"Manage User Classes");
    }

    /**
     * Used to discover if the instance can display simulation animation
     *
     * @return true if the instance can display simulation animation
     */
    public boolean isAnimationDisplayable() {
        return true;
    }

    /**
     * Gets the Dimension of a specified cell
     * @param cell
     * @return the cell Dimension
     */
    public Rectangle2D getCellDimension(JmtCell cell) {
        return GraphConstants.getBounds(cell.getAttributes());
    }

// --- Methods to handle blocking regions - Bertoli Marco -------------------------------------
    /**
     * Adds given JmtCells to a freshly created blocking station. This method will not modify
     * data structure and is used during load operation
     */
    public void addCellsToBlockingRegion(Object[] cells, Object regionKey) {
        BlockingRegion bl = new BlockingRegion(this, regionKey);
        bl.addStations(cells);
    }

    /**
     * Adds a new Blocking region that contains selected cells,
     * if this doesn't overlap with existing one
     */
    public void addSelectionToNewBlockingRegion () {
        Object[] cells = graph.getSelectionCells();
        // Data structure to hold all selected stations and their search's key
        HashMap stations = new HashMap();
        boolean canBeAdded = true;
        Object regionKey = model.addBlockingRegion();
        for (int i=0; i<cells.length; i++) {
            if (cells[i] instanceof JmtCell) {
                Object stationKey = ((CellComponent)((JmtCell)cells[i]).getUserObject()).getKey();
                if (!model.canRegionStationBeAdded(regionKey, stationKey)) {
                    canBeAdded = false;
                    break;
                }
                else
                    stations.put(cells[i], stationKey);
            }
            else if (cells[i] instanceof BlockingRegion) {
                // A blocking region cannot overlap another one
                canBeAdded = false;
                break;
            }
        }
        // If blocking region can be added, adds it to graph window, otherwise deletes it
        if (canBeAdded && stations.size() > 0) {
            BlockingRegion bl = new BlockingRegion(this, regionKey);
            Object[] stationCells = stations.keySet().toArray();
            bl.addStations(stationCells);
            // Adds stations to blocking region into data structure
            for (int i=0; i<stationCells.length; i++)
                model.addRegionStation(regionKey, stations.get(stationCells[i]));
        }
        else
            model.deleteBlockingRegion(regionKey);
    }

    /**
     * This method is used to reflect drag in and out a blocking region on data
     * structure and to move dragged cells to background to use transparency of
     * blocking region over them
     */
    public void handlesBlockingRegionDrag() {
        Object[] cells = graph.getDescendants(graph.getSelectionCells());
        // Put cells not in a blocking region to back
        HashSet putBack = new HashSet();
        for (int i=0; i<cells.length; i++) {
            if (cells[i] instanceof JmtCell && ((JmtCell)cells[i]).parentChanged()) {
                // This cell was moved in, out or between blocking regions
                JmtCell cell = (JmtCell) cells[i];
                Object key = ((CellComponent)cell.getUserObject()).getKey();
                Object oldRegionKey, newRegionKey;
                if (!(cell.getParent() instanceof BlockingRegion)) {
                    // Object removed from blocking region
                    putBack.add(cells[i]);
                    oldRegionKey = ((BlockingRegion)cell.getPrevParent()).getKey();
                    model.removeRegionStation(oldRegionKey, key);
                    // If region is empty, removes region too
                    if (model.getBlockingRegionStations(oldRegionKey).size() == 0)
                        model.deleteBlockingRegion(oldRegionKey);
                    // Allow adding of removed objects to a new blocking region
                    enableAddBlockingRegion(true);
                }
                else if (cell.getPrevParent() instanceof BlockingRegion) {
                    // Object changed blocking region
                    oldRegionKey = ((BlockingRegion)cell.getPrevParent()).getKey();
                    model.removeRegionStation(oldRegionKey, key);
                    // If region is empty, removes region too
                    if (model.getBlockingRegionStations(oldRegionKey).size() == 0)
                        model.deleteBlockingRegion(oldRegionKey);
                    newRegionKey = ((BlockingRegion)cell.getParent()).getKey();
                    model.addRegionStation(newRegionKey, key);
                }
                else {
                    // Object added to a blocking region
                    newRegionKey = ((BlockingRegion)cell.getParent()).getKey();
                    if (!model.addRegionStation(newRegionKey, key)) {
                        // object cannot be added to blocking region (for example it's a source)
                        cell.removeFromParent();
                        graph.getModel().insert(new Object[]{cell}, null, null, null, null);
                        putBack.add(cell);
                    }
                    // Doesn't allow adding of selected objects to a new blocking region
                    enableAddBlockingRegion(false);
                }
                // Resets parent for this cell
                cell.resetParent();
            }
            // Avoid insertion of a blocking region in an other
            else if (cells[i] instanceof BlockingRegion) {
                BlockingRegion region = (BlockingRegion) cells[i];
                if (region.getParent() != null) {
                    region.removeFromParent();
                    graph.getModel().insert(new Object[]{region}, null, null, null, null);
                }
            }
        }
        // Puts cells removed from blocking regiont on background
        graph.getModel().toBack(putBack.toArray());
    }
// --------------------------------------------------------------------------------------------

}