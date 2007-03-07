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
  
package jmt.jmarkov;

import com.jgoodies.looks.Options;

import jmt.framework.gui.controller.Manager;
import jmt.gui.common.panels.AboutDialogFactory;
import jmt.jmarkov.Graphics.*;
import jmt.jmarkov.Graphics.constants.DrawBig;
import jmt.jmarkov.Graphics.constants.DrawConstrains;
import jmt.jmarkov.Graphics.constants.DrawNormal;
import jmt.jmarkov.Graphics.constants.DrawSmall;
import jmt.jmarkov.Queues.*;
import jmt.jmarkov.Queues.Exceptions.NonErgodicException;
import jmt.jmarkov.utils.Formatter;

import javax.help.HelpSet;
import javax.help.JHelp;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URL;
import java.util.Dictionary;
import java.util.Enumeration;


public class MMQueues extends JFrame implements Runnable {

    //NEW
    //@author Stefano Omini
    // introduced DEBUG var to skip System.out.println() calls in final release
    private static final boolean DEBUG = false;
    //end NEW

    private JLabel accelerationL;
    private BoundedRangeModel brm;
    private TitledBorder tb;
    private boolean nonErgodic;
    private Dimension initSize = new Dimension(800, 600);
    private double U; //Utilizzo [%]
    private double Q; //Numero medio di jobs
    private int buffer;
    private double timemultiplier = 1.0;
    private JPanel sPanel;
    private JPanel lambdaPanel;
    private JSlider sS;
    private JSlider lambdaS;
    private double sMultiplier = 1;
    private boolean paused = false;
    private JButton pauseB;
    private QueueDrawer queueDrawer;
    private StatiDrawer statiDrawer;
    private JobsDrawer jobsDrawer;
    private JEditorPane helpEP;
    private JComboBox topicCB;
    private JPanel helpP;
    private JTabbedPane outputTabP;
    private JScrollPane txtScroll;
    private TANotifier outputTA;
    private Notifier[] tan = new Notifier[4];
    public JButton stopB;
    private JPanel buttonsP;
    private JButton playB;
    private JTextField utilizationT;
    private JTextField mediaJobsT;
    private JPanel resultsP;
    private JPanel SimulationValuesP;
    public  JFrame mf;
    private JPanel outputP;
    private JPanel parametersP;
    private JPanel simulationP;
    private JSlider buffS = new JSlider();
    private JPanel buffPanel = new JPanel();
    private JPanel accelerationP = new JPanel();
    private JPanel jobsP = new JPanel();
    private JSlider accelerationS = new JSlider();
    private MMQueues mmqueue;

//Label  & Label strings
    private JLabel
        sL,
        lambdaL,
        mediaJobsL,
        utilizationL,
        buffL,
        thrL,
        responseL;

    private String
        sStrS = "Avg. Service Time (S): ",
        sStrE = " s",

        //OLD
        //lambdaStrS = "Avg. Arrival Time (l): ",
        //NEW
        //@author Stefano Omini
        //lambda is an arrival RATE, not an arrival TIME !
        lambdaStrS = "Avg. Arrival Rate (l): ",
        //end NEW

        lambdaStrE = " job/s",
        nStrS = "Avg. Queue Lenght (Q): ",
        nStrE = " jobs",
        uStrS = "Avg. Utilization (U): ",
        uStrE = "",
        bufStrS = "Buffer Size: ",
        bufStrE = " jobs",
        thrStrS = "Avg. Throughput: ",
        thrStrE = " job/s",
        respStrS = "Avg. Response Time (R): ",
        respStrE = " s";

//Settings
    private Color emptyC = Color.WHITE,
                  probC = Color.GREEN,
                  queueC = Color.BLUE,
                  animC = Color.RED;
    private boolean gradientF = false;
    private DrawConstrains dCst = new DrawNormal();
    private int BUFF_I = 16,
                LAMBDA_I = 20,
                S_I = 190;


//menu
    private JMenuBar menuB;

    //help
    private JMenu helpMenu;
    private JMenuItem helpMenuItem;

    //queue
    private JMenu queueMenu;
    private JRadioButtonMenuItem MM1Item, MM1dItem, gradientItem;
    private JMenuItem exitMenuItem;
    //settings
    private JMenu settingsMenu;
        //colors
        private JMenu colorsMenu;
        private JMenuItem queueColorItem;
        private JMenuItem queueFillColorItem;
        private JMenuItem statusColorItem;
        private JMenuItem statusFillColorItem;

        //size
        private JMenu sizeMenu;
        private JMenuItem drawSmallItem;
        private JMenuItem drawNormalItem;
        private JMenuItem drawBigItem;

        //l&f
        private JMenu plafMenu;
        private static String[] plafURIA = {"com.jgoodies.looks.plastic.Plastic3DLookAndFeel",
                                            "com.digitprop.tonic.TonicLookAndFeel",
                                     "com.incors.plaf.kunststoff.KunststoffLookAndFeel",
                                     "com.jgoodies.looks.plastic.PlasticXPLookAndFeel",
                                     "com.oyoaha.swing.plaf.oyoaha.OyoahaLookAndFeel",
                                     "com.shfarr.ui.plaf.fh.FhLookAndFeel",
                                     "com.stefankrause.xplookandfeel.XPLookAndFeel",
                                     "net.beeger.squareness.SquarenessLookAndFeel",
                                     "net.sourceforge.mlf.metouia.MetouiaLookAndFeel"};
        private static String[] plafName = {"Plastic",
                                            "Tonic",
                                     "Kunststoff",
                                     "PlasticXP",
                                     "Oyoaha",
                                     "FH",
                                     "XP",
                                     "Squareness",
                                     "Metouia"};
        private JMenuItem[] plafItem = new JMenuItem[plafName.length];
        private int plafcounter = 0;

    //Queues data:
    MM1Logic ql = new MM1Logic(0.0,0.0);
    QueueStack qs;
    Thread at, pt, qD, sD, jD, logT, mft;
    Arrivals arrival;
    Processor cpu;

    public MMQueues() {
        mf = this;
        mmqueue = this;
        initGUI();

    }


    protected void initGUI() {
        Toolkit kit = Toolkit.getDefaultToolkit();
        Dimension screenSize = kit.getScreenSize();
        this.setLocation((screenSize.width - initSize.width)/2, (screenSize.height - initSize.height)/2);
        try {

// Simulation data panel
            simulationP = new JPanel();
            SimulationValuesP = new JPanel();
            parametersP = new JPanel();
            lambdaPanel = new JPanel();
            lambdaL = new JLabel();
            lambdaS = new JSlider();
            sPanel = new JPanel();
            sS = new JSlider();
            resultsP = new JPanel();
            mediaJobsL = new JLabel();
            mediaJobsT = new JTextField();
            utilizationL = new JLabel();
            utilizationT = new JTextField();
            mediaJobsL = new JLabel();
            thrL = new JLabel();
            responseL = new JLabel();

// simulation output panels
            outputP = new JPanel();
            outputTabP = new JTabbedPane();
            txtScroll = new JScrollPane();
            outputTA = new TANotifier();
            //logD = new LogDrawer();
            statiDrawer = new StatiDrawer(ql);
            queueDrawer = new QueueDrawer(ql);
            jobsDrawer = new JobsDrawer();

// acceleration
           accelerationP.setLayout(new GridBagLayout());
           GridBagConstraints gbc = new GridBagConstraints();
           gbc.fill = GridBagConstraints.HORIZONTAL;
           gbc.gridx = 0;
           gbc.gridy = 0;
           gbc.weightx = 0;
           accelerationP.setBorder(addTitle("Simulation time", dCst.getSmallGUIFont()));
           accelerationL = new JLabel("Time x0.0");
           accelerationL.setFont(dCst.getNormalGUIFont());
           accelerationL.setHorizontalAlignment(JLabel.CENTER);
           accelerationP.add(accelerationL,gbc);
           accelerationS.setValue(50);
           accelerationS.setMaximum(100);
           accelerationS.setMinimum(1);
           accelerationS.setMajorTickSpacing(50);
           accelerationS.setMinorTickSpacing(1);
           accelerationS.setSnapToTicks(true);
           accelerationS.setPaintTicks(true);
           accelerationS.setPaintLabels(true);
           Dictionary ad = accelerationS.getLabelTable();
           Enumeration k = ad.keys();
           ad.put(new Integer(1), new JLabel("real time"));
           ad.put(new Integer(51), new JLabel("faster"));
           ad.put(new Integer(100), new JLabel("fastest"));
           accelerationS.setLabelTable(ad);
           gbc.gridy = 1;
           gbc.weightx = 1;
           accelerationP.add(accelerationS,gbc);
           accelerationS.addChangeListener(new ChangeListener() {
               public void stateChanged(ChangeEvent evt) {
                       ql.setTimeMultiplier(accelerationS.getValue());
                       accelerationL.setText("Time x" + Formatter.formatNumber(ql.getTimeMultiplier(), 2));
                       }
           });
           ql.setTimeMultiplier(accelerationS.getValue());
           accelerationL.setText("Time x" + Formatter.formatNumber(ql.getTimeMultiplier(), 2));

//			jobs panel
            jobsP.setBorder(addTitle("Jobs", dCst.getSmallGUIFont()));
            jobsP.setLayout(new GridLayout(1,1));
            jobsP.add(jobsDrawer);

//		   buttons
            buttonsP = new JPanel();
            playB = new JButton();
            stopB = new JButton();
            pauseB = new JButton();

//Adding to main frame
            GridBagConstraints c = new GridBagConstraints();
            c.fill = GridBagConstraints.BOTH;
            this.getContentPane().setLayout(new BorderLayout());
            this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            this.addWindowListener(new WindowAdapter() {
                /**
                 * Invoked when a window has been closed.
                 */
                public void windowClosed(WindowEvent e) {
                    Manager.exit(MMQueues.this);
                }
            });
            //OLD
            //this.setTitle("MMQueues");
            //NEW
            //@author Stefano Omini
            this.setTitle("jMCH - Markov Chain");

            this.setIconImage(jmt.gui.common.resources.JMTImageLoader.loadImage("JMCHIcon").getImage());
            //end NEW


            this.setSize(new java.awt.Dimension(initSize.width, initSize.height));
            simulationP.setLayout(new GridBagLayout());
            this.getContentPane().add(simulationP,BorderLayout.CENTER);
            parametersP.setLayout(new GridBagLayout());
            parametersP.setBorder(addTitle("Simulation Parameters", dCst.getSmallGUIFont()));
            c.weightx = 1;
            c.weighty = 0;
            c.gridx = 0;
            c.gridy = 0;
            simulationP.add(parametersP, c);

// lambda
            lambdaPanel.setLayout(new GridLayout(2,1));
            c.weightx = 0.5;
            parametersP.add(lambdaPanel,c);
            lambdaL.setAlignmentX(JLabel.CENTER);
            lambdaPanel.add(lambdaL);
            lambdaS.setMaximum(300);
            lambdaS.setMinimum(0);
            lambdaS.setMajorTickSpacing(50);
            lambdaS.setMinorTickSpacing(1);
            lambdaS.setPaintLabels(true);
            lambdaS.setSnapToTicks(true);
            lambdaPanel.add(lambdaS);
            lambdaL.setFont(dCst.getNormalGUIFont());
            lambdaS.setValue(LAMBDA_I);
            setLambdaSlider();
            lambdaS.addChangeListener(new ChangeListener() {
                public void stateChanged(ChangeEvent evt) {
                    lambdaSStateChanged(evt);
                }
            });
            lambdaS.repaint();

// S slider
            sPanel.setLayout(new GridLayout(2,1));
            c.gridx = 1;
            parametersP.add(sPanel,c);
            sL = new JLabel();
            sL.setAlignmentX(JLabel.CENTER);
            sPanel.add(sL);
            sS.setMaximum(200);
            sS.setMinimum(0);
            sS.setMajorTickSpacing(50);
            sS.setMinorTickSpacing(1);
            sS.setPaintLabels(true);
            sL.setFont(dCst.getNormalGUIFont());
            /*Dictionary d = sS.getLabelTable();
               for(int i = 0; i < 6; i++){
                   d.put(new Integer(i * 50), new JLabel("" + i * 0.5));
               }
               sS.setLabelTable(d);
               sL.setFont(dCst.getNormalGUIFont());
               sL.setText(sStrS + Formatter.formatNumber(sS.getValue() * 0.005 * sMultiplier,2) + sStrE);*/
            sPanel.add(sS);
            sS.setValue(S_I);
            setSSlider();
            sS.addChangeListener(new ChangeListener() {
                public void stateChanged(ChangeEvent evt) {
                    sSStateChanged(evt);
                }
            });

// queueBuffer slider
           buffPanel.setLayout(new GridLayout(2,1));
           c.gridx = 2;
           buffPanel.setVisible(false);
           parametersP.add(buffPanel,c);
           buffL = new JLabel();
           buffL.setAlignmentX(JLabel.CENTER);
           buffL.setFont(dCst.getNormalGUIFont());
           buffPanel.add(buffL);
           buffS.setValue(BUFF_I);
           buffS.setMaximum(20);
           buffS.setMinimum(0);
           buffS.setMajorTickSpacing(5);
           buffS.setMinorTickSpacing(1);
           buffS.setPaintLabels(true);
           buffS.setPaintTicks(true);
           buffPanel.add(buffS);
           buffL.setText(bufStrS + buffS.getValue() + bufStrE);
           buffS.addChangeListener(new ChangeListener() {
               public void stateChanged(ChangeEvent evt) {
                    buffSStateChanged(evt);
               }
           });

// results
            resultsP.setLayout(new GridLayout(2,2));
            resultsP.setBorder(addTitle("Simulation Results",dCst.getSmallGUIFont()));
            c.gridx = 0;
            c.gridy = 1;
            simulationP.add(resultsP, c);

            //media
            mediaJobsL.setText(nStrS + "0" + nStrE);
            mediaJobsL.setFont(dCst.getNormalGUIFont());
            resultsP.add(mediaJobsL);

            //utilizzo
            utilizationL.setText(uStrS + "0" + uStrE);
            utilizationL.setFont(dCst.getNormalGUIFont());
            resultsP.add(utilizationL);

            //throughput
            thrL.setText(thrStrS + "0" + thrStrE);
            thrL.setFont(dCst.getNormalGUIFont());
            resultsP.add(thrL);

            //response time
            responseL.setText(respStrS + "0" + respStrE);
            responseL.setFont(dCst.getNormalGUIFont());
            resultsP.add(responseL);

            updateFields();

            outputP.setLayout(new GridLayout(2, 1));
            c.weightx = 1;
            c.weighty = 0.7;
            c.gridy = 2;
            simulationP.add(outputP, c);
            outputP.add(outputTabP);
            txtScroll.setBorder(addTitle("Simulation Output", dCst.getSmallGUIFont()));
            txtScroll.setName("Text Output");
            outputTabP.add(statiDrawer);
            outputTabP.setTitleAt(0, "States");
            outputTabP.add(txtScroll);
            outputTabP.setTitleAt(1, "Log");
            outputTA.setEditable(false);
            outputTA.setAutoscrolls(true);
            txtScroll.add(outputTA);
            txtScroll.setViewportView(outputTA);
            //outputTabP.add(logD);
            //outputTabP.setTitleAt(2, "Results");
            outputP.add(queueDrawer);
            JPanel p = new JPanel(new GridLayout(1,2));
            p.add(accelerationP);
            p.add(jobsP);
            c.weightx = 0;
            c.weighty = 0;
            c.gridy = 3;
            simulationP.add(p, c);
            c.gridx = 1;
            c.gridx = 0;
            c.weightx = 0;
            c.gridy = 4;
            c.fill = GridBagConstraints.HORIZONTAL;
            simulationP.add(buttonsP, c);
            playB.setText("start");
            buttonsP.add(playB);
            playB.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                    playBActionPerformed(evt);
                }
            });
            stopB.setEnabled(false);
            stopB.setText("stop");
            buttonsP.add(stopB);
            stopB.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                    stopBActionPerformed(evt);
                }
            });
            pauseB.setEnabled(false);
            pauseB.setText("pause");
            buttonsP.add(pauseB);
            pauseB.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                    pauseBActionPerformed(evt);
                }
            });

//menu
        menuB = new JMenuBar();
        setJMenuBar(menuB);
            //queue
            queueMenu = new JMenu("Queue");
            MM1Item = new JRadioButtonMenuItem("M/M/1", true);
            MM1Item.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    MM1Item.setSelected(true);
                    MM1dItem.setSelected(false);
                    showQueue(0);
                }
            });
            queueMenu.add(MM1Item);
            MM1dItem = new JRadioButtonMenuItem("M/M/1/k", false);
            MM1dItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    MM1dItem.setSelected(true);
                    MM1Item.setSelected(false);
                    showQueue(1);
                }
            });

            queueMenu.add(MM1dItem);
            queueMenu.addSeparator();
            //exitMenuItem = new JMenuItem();
            Action exitAction = new AbstractAction("Exit"){
                public void actionPerformed(ActionEvent event)
                {
                    //action code goes here
                    dispose();
                    Manager.exit(MMQueues.this);
                }
            };
            queueMenu.add(exitAction);
            menuB.add(queueMenu);

            //settings
            settingsMenu = new JMenu("Settings");
            colorsMenu = new JMenu("Colors");
            Action queueCAction = new AbstractAction("Probability..."){
                public void actionPerformed(ActionEvent event)
                {
                    //action code goes here
                    Color tmpC;
                    tmpC = JColorChooser.showDialog(null,"Probability color", probC);
                    if (tmpC != null){
                        if (DEBUG) {
                            System.out.println("queueC - R:" + tmpC.getRed() +
                                           " G:" + tmpC.getGreen() +
                                           " B:" + tmpC.getBlue());
                        }
                        probC = tmpC;
                        changeColors();
                    }
                }
            };
            colorsMenu.add(queueCAction);
            Action queueFCAction = new AbstractAction("Queue..."){
                public void actionPerformed(ActionEvent event)
                {
                    //action code goes here
                    Color tmpC;
                    tmpC = JColorChooser.showDialog(null,"Queue color", queueC);
                    if (tmpC != null){
                        if (DEBUG) {
                            System.out.println("queueFC - R:" + tmpC.getRed() +
                                           " G:" + tmpC.getGreen() +
                                           " B:" + tmpC.getBlue());
                        }
                        queueC = tmpC;
                        changeColors();
                    }
                }
            };
            colorsMenu.add(queueFCAction);
            colorsMenu.addSeparator();
            Action statusCAction = new AbstractAction("Empty state..."){
                public void actionPerformed(ActionEvent event)
                {
                    //action code goes here
                    Color tmpC;
                    tmpC = JColorChooser.showDialog(null,"Empty state color", emptyC);
                    if (tmpC != null){
                        if (DEBUG) {
                            System.out.println("statusC - R:" + tmpC.getRed() +
                                    " G:" + tmpC.getGreen() +
                                    " B:" + tmpC.getBlue());
                        }
                        emptyC = tmpC;
                        changeColors();
                    }
                }
            };
            colorsMenu.add(statusCAction);
            Action animCAction = new AbstractAction("Animation..."){
                public void actionPerformed(ActionEvent event)
                {
                    //action code goes here
                    Color tmpC;
                    tmpC = JColorChooser.showDialog(null,"Animation color", animC);
                    if (tmpC != null){
                        if (DEBUG) {
                            System.out.println("animC - R:" + tmpC.getRed() +
                                    " G:" + tmpC.getGreen() +
                                    " B:" + tmpC.getBlue());
                        }
                        animC = tmpC;
                        changeColors();
                    }
                }
            };
            colorsMenu.add(animCAction);
            colorsMenu.addSeparator();

            //gradientItem = new JRadioButtonMenuItem("usa gradiente", false);
            gradientItem = new JRadioButtonMenuItem("Use gradient", false);
            gradientItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    gradientF = gradientItem.isSelected();
                    changeColors();
                }
            });
            colorsMenu.add(gradientItem);
            settingsMenu.add(colorsMenu);


            //sizeMenu = new JMenu("Dimensioni");
            sizeMenu = new JMenu("Icon size");

            //Action drawSmallAction = new AbstractAction("Piccole"){
            Action drawSmallAction = new AbstractAction("Small"){
                public void actionPerformed(ActionEvent event)
                {
                    //action code goes here
                    dCst = new DrawSmall();
                    changeSize();
                }
            };
            sizeMenu.add(drawSmallAction);

            //Action drawNormalAction = new AbstractAction("Normali"){
            Action drawNormalAction = new AbstractAction("Normal"){
                public void actionPerformed(ActionEvent event)
                {
                    //action code goes here
                    dCst = new DrawNormal();
                    changeSize();
                }
            };
            sizeMenu.add(drawNormalAction);
            //Action drawBigAction = new AbstractAction("Grandi"){
            Action drawBigAction = new AbstractAction("Large"){
                public void actionPerformed(ActionEvent event)
                {
                    //action code goes here
                    dCst = new DrawBig();
                    changeSize();
                }
            };
            sizeMenu.add(drawBigAction);
            settingsMenu.add(sizeMenu);



            menuB.add(settingsMenu);

            //help
            helpMenu = new JMenu("Help");

            //OLD
            // helpMenuItem = helpMenu.add("Help");
            //NEW
            //@author Stefano Omini
            Action helpAction = new AbstractAction("Help"){
                public void actionPerformed(ActionEvent event)
                {
                    //action code goes here
                    showHelp(event);
                }
            };

            helpMenuItem = helpMenu.add(helpAction);
            //end NEW

            //NEW Bertoli Marco
            helpMenu.addSeparator();
            JMenuItem about = new JMenuItem();
            about.setText("About...");
            about.addActionListener(new ActionListener() {

                /**
                 * Invoked when an action occurs.
                 */
                public void actionPerformed(ActionEvent e) {
                    AboutDialogFactory.showJMCH(MMQueues.this);
                }
            });

            helpMenu.add(about);
            //END new

            menuB.add(helpMenu);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Changes the size of the drawing text
     */
    protected void changeSize() {
        queueDrawer.changeDrawSettings(dCst);
        queueDrawer.repaint();
        statiDrawer.changeDrawSettings(dCst);
        statiDrawer.repaint();
        outputTA.changeDrawSettings(dCst);
        //logD.changeDrawSettings(dCst);
        validate();

    }
    /**
     *
     */
    protected void changeColors() {
        queueDrawer.setColors(emptyC, queueC, animC, gradientF);
        queueDrawer.repaint();
        statiDrawer.setColors(emptyC, queueC, probC, animC);
        statiDrawer.repaint();
    }
    /**
     * @param evt
     */
    protected void buffSStateChanged(ChangeEvent evt) {
        buffer = buffS.getValue();
        if (buffer < 1){
            buffS.setValue(1);
            buffer = 1;
        }
        ql.setMaxStates(buffer);
        queueDrawer.setMaxJobs(buffer);
        statiDrawer.setMaxJobs(buffer);
        buffL.setText(bufStrS + buffS.getValue() + bufStrE);
    }

    protected void showQueue(int queueType) {
        switch (queueType) {
            case 0:
                buffer = 0;
                 ql = new MM1Logic(
                     0.01 * lambdaS.getValue(),
                    0.005 * sS.getValue() * sMultiplier);
                buffPanel.setVisible(false);
                break;

             case 1:
                buffer = BUFF_I;
                ql = new MM1dLogic(
                    0.01 * lambdaS.getValue(),
                    0.005 * sS.getValue() * sMultiplier,
                    buffer);
                buffS.setValue(buffer);
                buffPanel.setVisible(true);
                break;

            default :
                break;
        }
        sS.setValue(S_I);
        lambdaS.setValue(LAMBDA_I);
        statiDrawer.updateLogic(ql);
        queueDrawer.setMaxJobs(buffer);
        statiDrawer.setMaxJobs(buffer);
        updateFields();
    }

    public static void main(String[] args) {
        int i = 0;
        configureUI(plafURIA[i]);
        if (DEBUG)
            System.out.println(plafName[i]);
        showGUI();
    }

    public static void showGUI() {
        try {
            MMQueues inst = new MMQueues();
            inst.setVisible(true);
            Manager.addWindow(inst);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void playBActionPerformed(ActionEvent evt) {
        boolean goOn = true;
        if (nonErgodic)
            if (JOptionPane.NO_OPTION == JOptionPane.showConfirmDialog(
                this,
                "Il processo � NON ergodico.\nSi vuole continuare comunque?",
                "Attenzione",
                JOptionPane.YES_NO_OPTION))
                goOn = false;
        if (goOn) {
            //Creo i threads che aggiungono e tolgono i processi dalla coda
            CustomDialog jobsDialog = new CustomDialog(mf);
            jobsDialog.pack();
            jobsDialog.setLocationRelativeTo(mf);
            jobsDialog.setVisible(true);
            qs = new QueueStack(jobsDialog.getValidatedValue());
            queueDrawer.setMediaJobs((int) Q);
            queueDrawer.setTotalJobs(jobsDialog.getValidatedValue());
            jobsDrawer.setTotalJobs(jobsDialog.getValidatedValue());
            tan[0] = outputTA;
            tan[1] = queueDrawer;
            tan[2] = statiDrawer;
            tan[3] = jobsDrawer;
            //tan[4] = logD;
            qD = new Thread(queueDrawer);
            sD = new Thread(statiDrawer);
            jD = new Thread(jobsDrawer);
            //logT = new Thread(logD);
            qD.setDaemon(true);
            sD.setDaemon(true);
            qD.start();
            sD.start();
            jD.start();
            //logT.start();
            arrival = new Arrivals(ql, qs, tan, (int)jobsDialog.getValidatedValue());
            cpu = new Processor(ql, qs, tan, jobsDialog.getValidatedValue());
            at = new Thread(arrival);
            pt = new Thread(cpu);
            at.setDaemon(true);
            pt.setDaemon(true);
            at.start();
            pt.start();
            playB.setEnabled(false);
            stopB.setEnabled(true);
            pauseB.setEnabled(true);
            MM1Item.setEnabled(false);
            MM1dItem.setEnabled(false);
            Thread mmt = new Thread(mmqueue);
            mmt.start();
        }
    }

    protected void stopBActionPerformed(ActionEvent evt) {
        stopProcessing();
    }

    /** Auto-generated event handler method */
    protected void pauseBActionPerformed(ActionEvent evt) {
        if (paused) {
            qD.resume();
            sD.resume();
            jD.resume();
            //logT.resume();
            at.resume();
            pt.resume();
            paused = false;
        } else {
            at.suspend();
            pt.suspend();
            qD.suspend();
            sD.suspend();
            jD.suspend();
            //logT.suspend();
            paused = true;
        }
    }

    /** Auto-generated event handler method */
    protected void lambdaSStateChanged(ChangeEvent evt) {
        if (lambdaS.getValue() == 0) lambdaS.setValue(1);
        ql.setLambda(0.01 * lambdaS.getValue());
        lambdaL.setText(lambdaStrS + Formatter.formatNumber(lambdaS.getValue() * 0.01,2) + lambdaStrE);
        setSSlider();
        updateFields();
    }

    protected void sSStateChanged(ChangeEvent evt) {
        setSSlider();
        updateFields();

    }

    private void updateFields() {
        try {
            Q = ql.mediaJobs();
            U = ql.utilization();
            utilizationL.setForeground(Color.BLACK);
            utilizationL.setText(uStrS + Formatter.formatNumber(U,2) + uStrE);
            mediaJobsL.setText(nStrS + Formatter.formatNumber(Q,2) + nStrE);
            thrL.setText(thrStrS + Formatter.formatNumber(ql.throughput(),2) + thrStrE);
            responseL.setText(respStrS + Formatter.formatNumber(ql.responseTime(),2) + respStrE);
            nonErgodic = false;
        } catch (NonErgodicException e) {
            Q = 0.0;
            U = 0.0;
            mediaJobsL.setText(nStrS + " -" + nStrE);
            utilizationL.setForeground(Color.RED);
            utilizationL.setText(uStrS + "non ergodic (>1)" + uStrE);
            thrL.setText(thrStrS + "-" + thrStrE);
            responseL.setText(respStrS + "-" + respStrE);
            nonErgodic = true;
        }
        queueDrawer.setMediaJobs((int) Q);
        statiDrawer.repaint();
    }


    private static void configureUI(String plafURI) {
        UIManager.put(Options.USE_SYSTEM_FONTS_APP_KEY, Boolean.TRUE);
        //Options.setGlobalFontSizeHints(FontSizeHints.MIXED);
        Options.setDefaultIconSize(new Dimension(18, 18));
        try {

            UIManager.setLookAndFeel(plafURI);
        } catch (Exception e) {
            System.err.println("Can't set look & feel:" + e);
        }
    }

    private TitledBorder addTitle(String title, Font f){
        return new TitledBorder(
            null,
            title,
            TitledBorder.LEADING,
            TitledBorder.TOP,
            f,
            new java.awt.Color(0, 0, 0));
    }

    public void stopProcessing(){
        at.stop();
        pt.stop();
        qs.clearQueue();
        outputTA.reset();
        queueDrawer.reset();
        statiDrawer.reset();
        //logD.reset();
        qD.stop();
        sD.stop();
        jD.stop();
        //logT.stop();
        playB.setEnabled(true);
        stopB.setEnabled(false);
        pauseB.setEnabled(false);
        MM1Item.setEnabled(true);
        MM1dItem.setEnabled(true);
    }


    /* (non-Javadoc)
      * @see java.lang.Runnable#run()
      */
    public void run() {
        while(true){
            try {
                Thread.sleep(3000);
                if(!cpu.haveMoreWorkToDo()){
                    stopProcessing();
                    }
            } catch (Exception e) {
            }
        }

    }

    public void setSSlider(){
        sMultiplier = ql.getMaxErgodicS();
        Dictionary d = sS.getLabelTable();
        for(int i = 0; i < 6; i++){
            d.put(new Integer(i * 50), new JLabel("" + Formatter.formatNumber(i * (sMultiplier / 4.0)), 2));
        }
        sS.setLabelTable(d);
        sL.setText(sStrS + Formatter.formatNumber(sS.getValue() * 0.005 * sMultiplier,2) + sStrE);
        sS.repaint();
        ql.setS(0.005 * sS.getValue() * sMultiplier);
    }

    public void setLambdaSlider(){
        Dictionary ld = lambdaS.getLabelTable();
        for(int i = 0; i < 8; i++){
            ld.put(new Integer(i * 50), new JLabel("" + i * 0.5));
        }
        lambdaS.setLabelTable(ld);
        ql.setLambda(0.01 * lambdaS.getValue());
        lambdaL.setText(lambdaStrS + Formatter.formatNumber(lambdaS.getValue() * 0.01,2) + lambdaStrE);
    }




    //NEW
    //@author Stefano Omini
    private void showHelp(ActionEvent event){

        JHelp helpViewer = null;
        try {
            // Get the classloader of this class.
            ClassLoader cl = this.getClass().getClassLoader();
            // Use the findHelpSet method of HelpSet to create a URL referencing the helpset file.
            URL url = HelpSet.findHelpSet(cl, "help/jMCH_it/MMQHelp.hs");
            // Create a new JHelp object with a new HelpSet.
            helpViewer = new JHelp(new HelpSet(cl, url));

            // Set the initial entry point in the table of contents.
            //helpViewer.setCurrentID("");
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Sorry, help is not available",
                    "Help not found", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Create a new frame.
        JFrame frame = new JFrame();
        // Set it's size.
        frame.setSize(650,510);
        // Add the created helpViewer to it.
        frame.getContentPane().add(helpViewer);
        // Set a default close operation.
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        // Make the frame visible.
        frame.setVisible(true);

    }
    //end NEW
}