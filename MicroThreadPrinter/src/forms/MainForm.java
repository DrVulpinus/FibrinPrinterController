package forms;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.EventQueue;

import javax.swing.JFrame;

import java.awt.BorderLayout;

import javax.swing.JTabbedPane;
import javax.swing.JPanel;

import machinecontrol.GrblControl;
import machinecontrol.IOPortControl;
import machinecontrol.PumpControl;
import net.miginfocom.swing.MigLayout;

import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import javax.swing.JButton;
import javax.swing.JOptionPane;

import application.ProfileManager;
import application.SettingsManager;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JCheckBox;

import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.PreferenceChangeListener;

import javax.swing.JTextField;

import processcontrol.PathCreator;
import processcontrol.ProcessExecution;
import processcontrol.ProcessLogger;
import processcontrol.ProcessStage;
import processcontrol.ProcessStageListener;

import javax.swing.SwingConstants;
import javax.swing.JFormattedTextField;
import javax.swing.text.MaskFormatter;
import javax.swing.text.NumberFormatter;
import javax.swing.DefaultComboBoxModel;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JSplitPane;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

import java.beans.PropertyChangeEvent;

import java.awt.Font;
import java.awt.Color;

import javax.swing.UIManager;

import java.beans.PropertyChangeListener;

public class MainForm implements PreferenceChangeListener, ProcessStageListener{

	private ProfileManager profileMan = new ProfileManager();
	JFrame frame;
	private JTabbedPane tabbedPane;
	private JPanel pnlCfg;
	private JLabel lblGrblPort;
	private JLabel lblPumpPort;
	private JComboBox<String> cb_GrblPort;
	private JComboBox<String> cb_PumpPort;
	private boolean isConnected = false;
	private boolean isUpdating = false;
	SettingsManager prefs = new SettingsManager();
	GrblControl grblDev;
	PumpControl pumpDev;
	PathCreator pathCreator = new PathCreator();
	ProcessExecution processExec;
	JPanel pnlExtrusion;
	JPanel pnlStretch;
	JPanel pnlRun;
	JCheckBox chckbxGenerateLogFiles;
	JTextField tfLogDir;
	JButton btnBrowseLogDir;
	JLabel lblJobName;
	JLabel lblJobDescription;
	JTextField txtJobName;
	JTextField txtJobDescription;
	JButton btnRunOperation;
	JLabel lblSyringeDiameter;
	JFormattedTextField frmtdtxtfldSyringeDiameter;
	JLabel lblSyringeUnits;
	JComboBox<String> cbUnits;
	JSplitPane sPExtrusion;
	JPanel pnlExtCfg;
	JCheckBox chckbxPerformExtrusion;
	JPanel pnlExtSet;
	JLabel lblExtrusionRate;
	JLabel lblExtrusionRateUnits;
	JLabel lblFeedrate;
	JLabel lblMms;
	JLabel lblThreadSpacing;
	JLabel lblMm;
	JLabel lblPolymerizationTime;
	JFormattedTextField frmtdtxtfldPolyTime;
	JLabel lblThreadStartPause;
	JLabel lblSec;
	JSplitPane sPStretch;
	JPanel pnlStretchCfg;
	JCheckBox chckbxPerformStretch;
	JPanel pnlStretchSet;
	JLabel lblStretchRate;
	JLabel lblStretchAmount;
	JLabel lblMms_1;
	JLabel label;
	JPanel pnlExtDraw;
	JLabel lblSideMargins;
	JLabel lblMm_1;
	JLabel lblThreadLength;
	JSpinner spThreadLength;
	JSpinner spExtRate;
	JSpinner spFeed;
	JSpinner spThreadSpace;
	JSpinner spTSPT;
	JSpinner spSideMargins;
	JLabel lblMm_2;
	JSpinner spBedY;
	JLabel lblBedLength;
	JLabel lblMm_3;
	JSpinner spBedX;
	JLabel lblMm_4;
	JLabel lblBedWidth;
	JSpinner spStretchPer;
	JSpinner spStretchRate;
	JLabel lblBarThickness;
	JSpinner spBarThick;
	JLabel lblMm_5;
	JLabel lblOfThreads;
	JSpinner spNumThreads;
	JButton btnAutoConfigurePorts;
	JButton btnSaveCurrentProfile;
	JButton btnLoadProfile;
	MachineGraphic mG;
	MachineGraphic mG2;
	JTextField txtProcesstimer;
	JPanel pnlStretchDraw;
	JButton btnRunOperationAuto;
	JLabel lblInitializeRun;
	JLabel lblReadyToHome;
	JLabel lblHoming;
	JLabel lblAdjustingStretchBar;
	JLabel lblReadyToPurge;
	JLabel lblPurging;
	JLabel lblReadyToExtrude;
	JLabel lblWaitingForCleaningpolymerizing;
	JLabel lblPolymerizing;
	JLabel lblReadyToStretch;
	JLabel lblStretching;
	JLabel lblOperationComplete;
	ArrayList<JLabel> stageLabels = new ArrayList<JLabel>();
	JLabel lblExtruding;
	JCheckBox chckbxManualPumpCtrl;
	JTextField txtExtrudeTimer;
	JTextField txtStretchTimer;
	JTextField txtOperationTimer;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainForm window = new MainForm();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
	}

	/**
	 * Create the application.
	 */
	public MainForm() {
		initialize();
		updatePorts();
		getChckbxGenerateLogFiles().setSelected(prefs.getLogFiles());
		getChckbxManualPumpCtrl().setSelected(prefs.getManualPump());
		if (getChckbxGenerateLogFiles().isSelected()){
			getTfLogDir().setText(prefs.getLogFileDir());
			getBtnBrowseLogDir().setEnabled(true);
		}
		else{
			getTfLogDir().setText("File Logging Disabled");
			getBtnBrowseLogDir().setEnabled(false);
		}
		getFrmtdtxtfldSyringeDiameter().setText(prefs.getSyringeDia());
		for (int i = 0; i < getCbUnits().getItemCount(); i++) {
			if (getCbUnits().getItemAt(i).equals(prefs.getSyringeUnits())){
				getCbUnits().setSelectedIndex(i);
				return;
			}
			
		}
		
		prefs.addPreferenceChangeListener(this);
		
	}
	void logFileGenerationTest(){
		ProcessLogger logger = new ProcessLogger(prefs, getTxtJobName().getText(), getTxtJobDescription().getText());
		logger.recordStartTime();
		logger.addParam("Test1", 100);
		logger.addParam("Test2", 9945.34561);
		logger.recordEndTime();
		try {
			logger.generateLogFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				prefs.setWindowHeight(frame.getHeight());
				prefs.setWindowWidth(frame.getWidth());
			}
		});
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent arg0) {
				if (grblDev != null){
					grblDev.disconnect();
				}
				if (pumpDev != null){
					pumpDev.disconnect();
				}
				
			}
		});
		frame.setBounds(100, 100, 885, 594);
		frame.setMinimumSize(new Dimension(500, 500));
		frame.setMaximumSize(new Dimension(10000, 10000));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new BorderLayout(0, 0));
		frame.getContentPane().add(getTabbedPane(), BorderLayout.CENTER);
		
		updateRanges();
	}
	void selectedPortChanged(){
		if (!isUpdating){
		if (getCb_GrblPort().getSelectedItem() != getCb_PumpPort().getSelectedItem()){
			
			
			
			if (getCb_GrblPort().getSelectedItem() != null){
				prefs.setGrblPort(getCb_GrblPort().getSelectedItem().toString());
				//System.out.print("Grbl Port: ");
				//System.out.println(prefs.getGrblPort());
				
			}
			
			
			if (getCb_PumpPort().getSelectedItem() != null){
				prefs.setPumpPort(getCb_PumpPort().getSelectedItem().toString());
				//System.out.print("Pump Port: ");
				//System.out.println(prefs.getPumpPort());
				
			}
		}
		}
	}
	
	void verifyAllSettings(){
		 if (verifyPortSettings() && verifyLoggingSettings()){
			 getBtnRunOperation().setEnabled(true);
			 getBtnRunOperationAuto().setEnabled(true);
		 }
		 else{
			 getBtnRunOperation().setEnabled(false);
			 getBtnRunOperationAuto().setEnabled(false);
		 }
		
	}
	private boolean verifyPortSettings(){
		boolean settingsOK = false;
		try {
			grblDev = new GrblControl(getCb_GrblPort().getSelectedItem().toString());
			if (getChckbxManualPumpCtrl().isSelected()){
				pumpDev = null;
				if (grblDev.verifySettings()){
					settingsOK = true;
					System.out.println("Verification Succeeded");
				}
			}
			else{
				pumpDev = new PumpControl(prefs,getCb_PumpPort().getSelectedItem().toString());
				if (grblDev.verifySettings() && pumpDev.verifySettings()){
					settingsOK = true;
					System.out.println("Verification Succeeded");
				}
			}					
		} catch (Exception e) {
			//Catch any exceptions thrown for illegal selection of items
		}
		
		
		if (settingsOK == false){			
			JOptionPane.showMessageDialog(frame,"Port settings verification failed, please recheck port settings and device connection integrity before  continuing.", "Port Settings Verification Failure", JOptionPane.ERROR_MESSAGE);
		
		}
		
		return settingsOK;
	}
	private boolean verifyLoggingSettings(){
		if (prefs.getLogFiles()){
			File logFile = new File(prefs.getLogFileDir());
			if (!logFile.exists()){
				if(!logFile.mkdirs()){
					JOptionPane.showMessageDialog(frame,"Logging is enabled, but the folder in which to store log files is invalid. Please either disable logging or try browsing for a different directory.", "Log File Directory Invalid", JOptionPane.ERROR_MESSAGE);
					getTabbedPane().setSelectedIndex(0);
					return false;
				}
			}
		}
		return true;
	}
	void updatePorts(){
		isUpdating = true;
		if (!isConnected){
			
			getCb_GrblPort().setEnabled(true);
			getCb_PumpPort().setEnabled(true);
			getCb_GrblPort().removeAllItems();
			getCb_PumpPort().removeAllItems();
			for (String portName : IOPortControl.getPorts()) {
				getCb_GrblPort().addItem(portName);
				getCb_PumpPort().addItem(portName);
				
			}
			//System.out.println(prefs.getGrblPort());
			//System.out.println(prefs.getPumpPort());
			
			
			for (int j = 0; j < getCb_GrblPort().getItemCount(); j++) {
				if(getCb_GrblPort().getItemAt(j).equals(prefs.getGrblPort())){
					getCb_GrblPort().setSelectedIndex(j);
				}
			}
			for (int j = 0; j < getCb_PumpPort().getItemCount(); j++) {
				if(getCb_PumpPort().getItemAt(j).equals(prefs.getPumpPort())){
					getCb_PumpPort().setSelectedIndex(j);
				}
			}
			
		}
		else{
			getCb_GrblPort().setEnabled(false);
			getCb_PumpPort().setEnabled(false);			
		}
		if(getChckbxManualPumpCtrl().isSelected()){
			getCb_PumpPort().removeAllItems();
			getCb_PumpPort().setEnabled(false);
		}
		isUpdating = false;
		selectedPortChanged();
	}
	JTabbedPane getTabbedPane() {
		if (tabbedPane == null) {
			tabbedPane = new JTabbedPane(SwingConstants.TOP);
			tabbedPane.setFont(new Font("Tahoma", Font.PLAIN, 14));
			tabbedPane.addTab("General Settings", null, getPnlCfg(), "This page contains settings for the COM ports");
			tabbedPane.addTab("Extrusion Settings", null, getPnlExtrusion(), null);
			tabbedPane.addTab("Stretch Settings", null, getPnlStretch(), null);
			tabbedPane.addTab("Run Operation", null, getPnlRun(), null);
		}
		return tabbedPane;
	}
	private JPanel getPnlCfg() {
		if (pnlCfg == null) {
			pnlCfg = new JPanel();
			
			pnlCfg.setLayout(new MigLayout("", "[][grow][grow]", "[][][][][][][][]"));
			pnlCfg.add(getBtnAutoConfigurePorts(), "cell 0 0");
			pnlCfg.add(getLblGrblPort(), "cell 1 0,alignx trailing");
			pnlCfg.add(getCb_GrblPort(), "cell 2 0,growx");
			pnlCfg.add(getChckbxManualPumpCtrl(), "cell 0 1");
			pnlCfg.add(getLblPumpPort(), "cell 1 1,alignx trailing");
			pnlCfg.add(getCb_PumpPort(), "cell 2 1,growx");
			pnlCfg.add(getChckbxGenerateLogFiles(), "cell 0 2");
			pnlCfg.add(getTfLogDir(), "cell 1 2,growx");
			pnlCfg.add(getBtnBrowseLogDir(), "cell 2 2");
			pnlCfg.add(getLblSyringeDiameter(), "cell 1 3,alignx trailing");
			pnlCfg.add(getFrmtdtxtfldSyringeDiameter(), "cell 2 3,growx");
			pnlCfg.add(getLblSyringeUnits(), "cell 1 4,alignx trailing");
			pnlCfg.add(getCbUnits(), "cell 2 4,growx");
			pnlCfg.add(getLabel_1_5(), "cell 0 5,alignx right");
			pnlCfg.add(getSpBedY(), "cell 1 5,growx");
			pnlCfg.add(getLabel_1_6(), "cell 2 5");
			pnlCfg.add(getLabel_1_8(), "cell 0 6,alignx right");
			pnlCfg.add(getSpBedX(), "cell 1 6,growx");
			pnlCfg.add(getLabel_1_7(), "cell 2 6");
			pnlCfg.add(getLabel_1_9(), "cell 0 7,alignx right");
			pnlCfg.add(getSpBarThick(), "cell 1 7,growx");
			pnlCfg.add(getLabel_1_10(), "cell 2 7");
		}
		return pnlCfg;
	}
	private JLabel getLblGrblPort() {
		if (lblGrblPort == null) {
			lblGrblPort = new JLabel("GrbL Port:");
			lblGrblPort.setFont(new Font("Tahoma", Font.PLAIN, 14));
		}
		return lblGrblPort;
	}
	private JLabel getLblPumpPort() {
		if (lblPumpPort == null) {
			lblPumpPort = new JLabel("Pump Port:");
			lblPumpPort.setFont(new Font("Tahoma", Font.PLAIN, 14));
		}
		return lblPumpPort;
	}
	JComboBox<String> getCb_GrblPort() {
		if (cb_GrblPort == null) {
			cb_GrblPort = new JComboBox<String>();
			cb_GrblPort.setFont(new Font("Tahoma", Font.PLAIN, 14));
			cb_GrblPort.setToolTipText("Select the com port for the Grbl device.");
			cb_GrblPort.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					selectedPortChanged();
				}
			});
		}
		return cb_GrblPort;
	}
	JComboBox<String> getCb_PumpPort() {
		if (cb_PumpPort == null) {
			cb_PumpPort = new JComboBox<String>();
			cb_PumpPort.setFont(new Font("Tahoma", Font.PLAIN, 14));
			cb_PumpPort.setToolTipText("Select the com port for the syringe pump.");
			cb_PumpPort.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					selectedPortChanged();
				}
			});
		}
		
		
		return cb_PumpPort;
	}
	private JPanel getPnlExtrusion() {
		if (pnlExtrusion == null) {
			pnlExtrusion = new JPanel();
			pnlExtrusion.addComponentListener(new ComponentAdapter() {
				@Override
				public void componentShown(ComponentEvent arg0) {
					getMG().repaint();
				}
			});
			pnlExtrusion.setLayout(new BorderLayout(0, 0));
			pnlExtrusion.add(getSplitPane_1(), BorderLayout.CENTER);
		}
		return pnlExtrusion;
	}
	private JPanel getPnlStretch() {
		if (pnlStretch == null) {
			pnlStretch = new JPanel();
			pnlStretch.addComponentListener(new ComponentAdapter() {
				@Override
				public void componentShown(ComponentEvent e) {
					getMG2().repaint();
				}
			});
			pnlStretch.setLayout(new BorderLayout(0, 0));
			pnlStretch.add(getSPStretch(), BorderLayout.CENTER);
		}
		return pnlStretch;
	}
	private JPanel getPnlRun() {
		if (pnlRun == null) {
			pnlRun = new JPanel();
			pnlRun.addComponentListener(new ComponentAdapter() {
				@Override
				public void componentShown(ComponentEvent arg0) {
					verifyAllSettings();
				}
			});
			pnlRun.setLayout(new MigLayout("", "[][grow][grow]", "[][][][][][][][][][][][][][][][]"));
			pnlRun.add(getLblJobName(), "cell 0 0,alignx trailing");
			pnlRun.add(getTxtJobName(), "cell 1 0,growx");
			pnlRun.add(getLblJobDescription(), "cell 0 1,alignx trailing");
			pnlRun.add(getTxtJobDescription(), "cell 1 1,growx");
			pnlRun.add(getBtnLoadProfile(), "cell 0 2,growx");
			pnlRun.add(getLabelInitializeRun(), "cell 1 2");
			pnlRun.add(getBtnSaveCurrentProfile(), "cell 0 3,growx");
			pnlRun.add(getLabelReadyToHome(), "cell 1 3");
			pnlRun.add(getLabelHoming(), "cell 1 4");
			pnlRun.add(getBtnRunOperation(), "cell 0 5,growx");
			pnlRun.add(getLabelAdjustStretchBar(), "cell 1 5");
			pnlRun.add(getBtnRunOperationAuto(), "cell 0 6");
			pnlRun.add(getLabelReadyToPurge(), "cell 1 6");
			pnlRun.add(getLabelPurging(), "cell 1 7");
			pnlRun.add(getLabelReadyToExtrude(), "cell 1 8");
			pnlRun.add(getLabelExtruding(), "cell 1 9,alignx left");
			pnlRun.add(getTxtExtrudeTimer(), "cell 2 9,growx");
			pnlRun.add(getLabelWaitingForCleaning(), "cell 1 10");
			pnlRun.add(getLabelPolymerizing(), "cell 1 11");
			pnlRun.add(getTxtProcesstimer(), "cell 2 11,alignx left");
			pnlRun.add(getLabelReadyToStretch(), "cell 1 12");
			pnlRun.add(getLabelStretching(), "cell 1 13,alignx left");
			pnlRun.add(getTxtStretchTimer(), "cell 2 13,growx");
			pnlRun.add(getLabelOperationComplete(), "cell 1 14,alignx left");
			pnlRun.add(getTxtOperationTimer(), "cell 2 14,growx");
		}
		return pnlRun;
	}
	private JCheckBox getChckbxGenerateLogFiles() {
		if (chckbxGenerateLogFiles == null) {
			chckbxGenerateLogFiles = new JCheckBox("Generate Log Files");
			chckbxGenerateLogFiles.setFont(new Font("Tahoma", Font.PLAIN, 14));
			chckbxGenerateLogFiles.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					prefs.setLogFiles(chckbxGenerateLogFiles.isSelected());
					if (chckbxGenerateLogFiles.isSelected()){
						getTfLogDir().setText(prefs.getLogFileDir());
						getBtnBrowseLogDir().setEnabled(true);
					}
					else{
						getTfLogDir().setText("");
						getBtnBrowseLogDir().setEnabled(false);
					}
				}
			});
		}
		return chckbxGenerateLogFiles;
	}
	JTextField getTfLogDir() {
		if (tfLogDir == null) {
			tfLogDir = new JTextField();
			tfLogDir.setFont(new Font("Tahoma", Font.PLAIN, 14));
			tfLogDir.setEditable(false);
			tfLogDir.setColumns(10);
		}
		return tfLogDir;
	}
	JButton getBtnBrowseLogDir() {
		if (btnBrowseLogDir == null) {
			btnBrowseLogDir = new JButton("Choose Log Folder");
			btnBrowseLogDir.setFont(new Font("Tahoma", Font.PLAIN, 14));
			btnBrowseLogDir.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					JFileChooser fc = new JFileChooser();
					fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
					int result = fc.showOpenDialog(frame);
					if (result == JFileChooser.APPROVE_OPTION){
						File file = fc.getSelectedFile();
						prefs.setLogFileDir(file.getAbsolutePath());
						getTfLogDir().setText(prefs.getLogFileDir());
					}
				}
			});
		}
		return btnBrowseLogDir;
	}
	private JLabel getLblJobName() {
		if (lblJobName == null) {
			lblJobName = new JLabel("Job Name:");
			lblJobName.setFont(new Font("Tahoma", Font.PLAIN, 14));
		}
		return lblJobName;
	}
	private JLabel getLblJobDescription() {
		if (lblJobDescription == null) {
			lblJobDescription = new JLabel("Job Description:");
			lblJobDescription.setFont(new Font("Tahoma", Font.PLAIN, 14));
		}
		return lblJobDescription;
	}
	private JTextField getTxtJobName() {
		if (txtJobName == null) {
			txtJobName = new JTextField();
			txtJobName.setFont(new Font("Tahoma", Font.PLAIN, 14));
			txtJobName.setToolTipText("Type a name for this job here, it will be used as the filename for the log file, if no name is given the job timestamp will be used instead.");
			txtJobName.setColumns(10);
			
		}
		return txtJobName;
	}
	private JTextField getTxtJobDescription() {
		if (txtJobDescription == null) {
			txtJobDescription = new JTextField();
			txtJobDescription.setFont(new Font("Tahoma", Font.PLAIN, 14));
			txtJobDescription.setToolTipText("Type a brief description of the job here for it to be recorded at the top of the log file.");
			txtJobDescription.setColumns(10);
		}
		return txtJobDescription;
	}

	@Override
	public void preferenceChange(PreferenceChangeEvent evt) {
		
		if (!prefs.getLogFiles()){
			getTxtJobName().setEnabled(false);
			getTxtJobDescription().setEnabled(false);
			getTxtJobName().setText("Logging is Disabled");
			getTxtJobDescription().setText("Logging is Disabled");
			getTxtJobName().setToolTipText("Job name cannot be set because logging is disabled.");
			getTxtJobDescription().setToolTipText("Job description cannot be set because logging is disabled.");
		}
		else{
			getTxtJobName().setEnabled(true);
			getTxtJobDescription().setEnabled(true);
			getTxtJobName().setText("");
			getTxtJobDescription().setText("");
			getTxtJobName().setToolTipText("Type a name for this job here, it will be used as the filename for the log file, if no name is given the job timestamp will be used instead.");
			getTxtJobDescription().setToolTipText("Type a brief description of the job here for it to be recorded at the top of the log file.");
		}
		
	}
	private JButton getBtnRunOperation() {
		if (btnRunOperation == null) {
			btnRunOperation = new JButton("Run Operation");
			btnRunOperation.setFont(new Font("Tahoma", Font.PLAIN, 14));
			btnRunOperation.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					runOperation(true);					
				}
			});
		}
		return btnRunOperation;
	}
	
	/**
	 * This method takes all of the input parameters and executes a run.
	 * This method does not do any error or sanity checking on parameters, it simply feeds the
	 * parameters to the execution class and which in turn executes the operation
	 * @param manualPoly Set this to true to manually time polymerization of the threads, set it to false to
	 * automatically time polymerization
	 */
	public void runOperation(boolean manualPoly){
		
		//This recolors all of the process stage labels to red to indicate that stage has not been started
		for (JLabel label1 : stageLabels) {
			label1.setForeground(Color.RED);
		}
		
		//This block of code parses the polymerization time string in the Formatted text box 
		//and then converts that time into a number of seconds
		int polyTime = 0;
		String[] timeString = getFrmtdtxtfldPolyTime().getText().split(":");
		int hours = Integer.parseInt(timeString[0]);
		int minutes = Integer.parseInt(timeString[1]);
		int seconds = Integer.parseInt(timeString[2]);					
		polyTime = (hours*3600) + (minutes*60) + (seconds);
		
		//Create a path creator, configure it, and then generate the path.
		pathCreator = new PathCreator();
		pathCreator.setParams((int) getSpBedX().getValue(),
				(int) getSpBedY().getValue(),
				(int) getSpSideMargins().getValue(),
				(int) getSpExtRate().getValue(),
				(int) getSpThreadLength().getValue(),
				(int) getSpFeed().getValue(),
				(int) getSpThreadSpace().getValue(),
				(int) getSpTSPT().getValue(),
				polyTime,
				(int) getSpBarThick().getValue(),
				(int) getSpNumThreads().getValue(),
				(int) getSpStretchPer().getValue(),
				(int) getSpStretchRate().getValue());
		
		pathCreator.doCalculations();
		if (getChckbxManualPumpCtrl().isSelected()){
			pumpDev = null;
		}
		else{
			pumpDev.connect();
		}
		grblDev.connect();
		processExec = new ProcessExecution(pumpDev, grblDev, pathCreator, getTxtProcesstimer(),getTxtExtrudeTimer(),getTxtStretchTimer(),getTxtOperationTimer());
		if (getChckbxPerformExtrusion().isSelected()){
			if(getChckbxPerformStretch().isSelected()){
				if (manualPoly){
				processExec.setupCompleteRunManual();
				}
				else{
					processExec.setupCompleteRunSemiAuto();
				}
			}
			else{
				processExec.setupExtrudeOnlyAuto();
				//TODO Turn stage labels to not be run dark grey
			}
		}
		else{
			processExec.setupStretchOnlyAuto();
			//TODO Turn stage labels to not be run dark grey
		}
		processExec.addStageListener(MainForm.this);
		processExec.start();
	}
	private JLabel getLblSyringeDiameter() {
		if (lblSyringeDiameter == null) {
			lblSyringeDiameter = new JLabel("Syringe Diameter (mm):");
			lblSyringeDiameter.setFont(new Font("Tahoma", Font.PLAIN, 14));
			lblSyringeDiameter.setHorizontalAlignment(SwingConstants.RIGHT);
		}
		return lblSyringeDiameter;
	}
	private JFormattedTextField getFrmtdtxtfldSyringeDiameter() {
		if (frmtdtxtfldSyringeDiameter == null) {
			NumberFormat nf = NumberFormat.getInstance();
			nf.setMinimumFractionDigits(2);
			nf.setMinimumIntegerDigits(2);
			nf.setMaximumIntegerDigits(2);
			nf.setMaximumFractionDigits(2);
			nf.setParseIntegerOnly(false);			
			nf.setGroupingUsed(false);
			NumberFormatter nfr = new NumberFormatter(nf);
			nfr.setAllowsInvalid(false);
			nfr.setOverwriteMode(false);
			frmtdtxtfldSyringeDiameter = new JFormattedTextField(nfr);			
			frmtdtxtfldSyringeDiameter.setFont(new Font("Tahoma", Font.PLAIN, 14));
			frmtdtxtfldSyringeDiameter.addKeyListener(new KeyAdapter() {
				@Override
				public void keyReleased(KeyEvent e) {
					//System.out.println(frmtdtxtfldSyringeDiameter.getText());
					prefs.setSyringeDia(frmtdtxtfldSyringeDiameter.getText());
				}
			});
			
		}
		return frmtdtxtfldSyringeDiameter;
	}
	
	
	protected static MaskFormatter createFormatter(String s) {
	    MaskFormatter formatter = null;
	    try {
	        formatter = new MaskFormatter(s);
	        
	    } catch (java.text.ParseException exc) {
	        System.err.println("formatter is bad: " + exc.getMessage());
	        System.exit(-1);
	    }
	    return formatter;
	}
	private JLabel getLblSyringeUnits() {
		if (lblSyringeUnits == null) {
			lblSyringeUnits = new JLabel("Syringe Units:");
			lblSyringeUnits.setFont(new Font("Tahoma", Font.PLAIN, 14));
			lblSyringeUnits.setHorizontalAlignment(SwingConstants.RIGHT);
		}
		return lblSyringeUnits;
	}
	private JComboBox<String> getCbUnits() {
		if (cbUnits == null) {
			cbUnits = new JComboBox<String>();
			cbUnits.setFont(new Font("Tahoma", Font.PLAIN, 14));
			cbUnits.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					prefs.setSyringeUnits((String) cbUnits.getSelectedItem());
					lblExtrusionRateUnits.setText(prefs.getSyringeUnits());
				}
			});
			cbUnits.setModel(new DefaultComboBoxModel<String>(new String[] {"ul/m", "ul/h", "ml/m", "ml/h"}));
		}
		return cbUnits;
	}
	JSplitPane getSplitPane_1() {
		if (sPExtrusion == null) {
			sPExtrusion = new JSplitPane();
			sPExtrusion.addPropertyChangeListener(new PropertyChangeListener() {
				public void propertyChange(PropertyChangeEvent arg0) {
					prefs.setDividerWidth(sPExtrusion.getDividerLocation());
					if (getSPStretch().getDividerLocation() != sPExtrusion.getDividerLocation()){
						getSPStretch().setDividerLocation(sPExtrusion.getDividerLocation());
					}
					
				}
			});
			sPExtrusion.setRightComponent(getPnlExtCfg());
			sPExtrusion.setLeftComponent(getPnlExtDraw());
			sPExtrusion.setDividerLocation(prefs.getDividerWidth());
		}
		return sPExtrusion;
	}
	private JPanel getPnlExtCfg() {
		if (pnlExtCfg == null) {
			pnlExtCfg = new JPanel();
			pnlExtCfg.setLayout(new MigLayout("", "[grow]", "[][grow]"));
			pnlExtCfg.add(getChckbxPerformExtrusion(), "cell 0 0");
			pnlExtCfg.add(getPnlExtSet(), "cell 0 1,grow");
		}
		return pnlExtCfg;
	}
	private JCheckBox getChckbxPerformExtrusion() {
		if (chckbxPerformExtrusion == null) {
			chckbxPerformExtrusion = new JCheckBox("Perform Extrusion");
			chckbxPerformExtrusion.setFont(new Font("Tahoma", Font.PLAIN, 14));
			chckbxPerformExtrusion.setSelected(true);
			chckbxPerformExtrusion.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (chckbxPerformExtrusion.isSelected()){
						for (Component component : getPnlExtSet().getComponents()) {
							component.setEnabled(true);
						}
					}
					else{
						for (Component component : getPnlExtSet().getComponents()) {
							component.setEnabled(false);
						}
					}
				}
			});
			chckbxPerformExtrusion.setToolTipText("<html>Check this box if extrusion should be performed during this operation</html>");
		}
		return chckbxPerformExtrusion;
	}
	JPanel getPnlExtSet() {
		if (pnlExtSet == null) {
			pnlExtSet = new JPanel();
			pnlExtSet.setLayout(new MigLayout("", "[][grow][]", "[][][][][][][][]"));
			pnlExtSet.add(getLabel_1_1(), "cell 0 0,alignx trailing");
			pnlExtSet.add(getSpSideMargins(), "cell 1 0,growx");
			pnlExtSet.add(getLabel_1_2(), "cell 2 0");
			pnlExtSet.add(getLblExtrusionRate(), "cell 0 1,alignx right");
			pnlExtSet.add(getSpExtRate(), "cell 1 1,growx");
			pnlExtSet.add(getLblExtrusionRateUnits(), "cell 2 1");
			pnlExtSet.add(getLblFeedrate(), "cell 0 2,alignx trailing");
			pnlExtSet.add(getSpFeed(), "cell 1 2,growx");
			pnlExtSet.add(getLblMms(), "cell 2 2");
			pnlExtSet.add(getLblThreadSpacing(), "cell 0 3,alignx trailing");
			pnlExtSet.add(getSpThreadSpace(), "cell 1 3,growx");
			pnlExtSet.add(getLblMm(), "cell 2 3");
			pnlExtSet.add(getLblThreadStartPause(), "cell 0 4,alignx trailing");
			pnlExtSet.add(getSpTSPT(), "cell 1 4,growx");
			pnlExtSet.add(getLblSec(), "cell 2 4");
			pnlExtSet.add(getLblPolymerizationTime(), "cell 0 5,alignx trailing");
			pnlExtSet.add(getFrmtdtxtfldPolyTime(), "cell 1 5,growx");
			pnlExtSet.add(getLabel_1_3(), "cell 0 6,alignx right");
			pnlExtSet.add(getSpThreadLength(), "cell 1 6,growx");
			pnlExtSet.add(getLabel_1_4(), "cell 2 6");
			pnlExtSet.add(getLabel_1_11(), "cell 0 7,alignx right");
			pnlExtSet.add(getSpNumThreads(), "cell 1 7,growx");
		}
		return pnlExtSet;
	}
	private JLabel getLblExtrusionRate() {
		if (lblExtrusionRate == null) {
			lblExtrusionRate = new JLabel("Extrusion Rate:");
			lblExtrusionRate.setFont(new Font("Tahoma", Font.PLAIN, 14));
		}
		return lblExtrusionRate;
	}
	private JLabel getLblExtrusionRateUnits() {
		if (lblExtrusionRateUnits == null) {
			lblExtrusionRateUnits = new JLabel("(units)");
			lblExtrusionRateUnits.setFont(new Font("Tahoma", Font.PLAIN, 14));
			lblExtrusionRateUnits.setHorizontalAlignment(SwingConstants.LEFT);
		}
		return lblExtrusionRateUnits;
	}
	private JLabel getLblFeedrate() {
		if (lblFeedrate == null) {
			lblFeedrate = new JLabel("Crosshead Speed:");
			lblFeedrate.setFont(new Font("Tahoma", Font.PLAIN, 14));
			lblFeedrate.setHorizontalAlignment(SwingConstants.RIGHT);
		}
		return lblFeedrate;
	}
	private JLabel getLblMms() {
		if (lblMms == null) {
			lblMms = new JLabel("mm/min");
			lblMms.setFont(new Font("Tahoma", Font.PLAIN, 14));
		}
		return lblMms;
	}
	private JLabel getLblThreadSpacing() {
		if (lblThreadSpacing == null) {
			lblThreadSpacing = new JLabel("Thread Spacing:");
			lblThreadSpacing.setFont(new Font("Tahoma", Font.PLAIN, 14));
			lblThreadSpacing.setHorizontalAlignment(SwingConstants.RIGHT);
		}
		return lblThreadSpacing;
	}
	private JLabel getLblMm() {
		if (lblMm == null) {
			lblMm = new JLabel("mm");
			lblMm.setFont(new Font("Tahoma", Font.PLAIN, 14));
		}
		return lblMm;
	}
	private JLabel getLblPolymerizationTime() {
		if (lblPolymerizationTime == null) {
			lblPolymerizationTime = new JLabel("Polymerization Time:");
			lblPolymerizationTime.setFont(new Font("Tahoma", Font.PLAIN, 14));
		}
		return lblPolymerizationTime;
	}
	private JFormattedTextField getFrmtdtxtfldPolyTime() {
		if (frmtdtxtfldPolyTime == null) {
			frmtdtxtfldPolyTime = new JFormattedTextField(createFormatter("##:##:##"));
			frmtdtxtfldPolyTime.setFont(new Font("Tahoma", Font.PLAIN, 14));
			frmtdtxtfldPolyTime.addKeyListener(new KeyAdapter() {
				@Override
				public void keyTyped(KeyEvent arg0) {
					prefs.setPolyTime(frmtdtxtfldPolyTime.getText());
				}
			});
			frmtdtxtfldPolyTime.setText("00:00:00");
			frmtdtxtfldPolyTime.setToolTipText("<html>This is the delay between extrusion and stretching<br>Enter the time in this format: HH:MM:SS<br>For manual delay (the user must give input to continue to stretching) enter: 00:00:00</html>");
			frmtdtxtfldPolyTime.setText(prefs.getPolyTime());
		}
		return frmtdtxtfldPolyTime;
	}
	private JLabel getLblThreadStartPause() {
		if (lblThreadStartPause == null) {
			lblThreadStartPause = new JLabel("Thread Start Pause Time:");
			lblThreadStartPause.setFont(new Font("Tahoma", Font.PLAIN, 14));
			lblThreadStartPause.setHorizontalAlignment(SwingConstants.RIGHT);
		}
		return lblThreadStartPause;
	}
	private JLabel getLblSec() {
		if (lblSec == null) {
			lblSec = new JLabel("sec.");
			lblSec.setFont(new Font("Tahoma", Font.PLAIN, 14));
		}
		return lblSec;
	}
	JSplitPane getSPStretch() {
		if (sPStretch == null) {
			sPStretch = new JSplitPane();
			sPStretch.addPropertyChangeListener(new PropertyChangeListener() {
				public void propertyChange(PropertyChangeEvent evt) {
					getSplitPane_1().setDividerLocation(sPStretch.getDividerLocation());
				}
			});
			sPStretch.setRightComponent(getPnlStretchCfg());
			sPStretch.setLeftComponent(getPnlStretchDraw());
			sPExtrusion.setDividerLocation(prefs.getDividerWidth());
		}
		return sPStretch;
	}
	private JPanel getPnlStretchCfg() {
		if (pnlStretchCfg == null) {
			pnlStretchCfg = new JPanel();
			pnlStretchCfg.setLayout(new MigLayout("", "[grow]", "[][grow]"));
			pnlStretchCfg.add(getChckbxPerformStretch(), "cell 0 0");
			pnlStretchCfg.add(getPnlStretchSet(), "cell 0 1,grow");
		}
		return pnlStretchCfg;
	}
	
	private JCheckBox getChckbxPerformStretch() {
		if (chckbxPerformStretch == null) {
			chckbxPerformStretch = new JCheckBox("Perform Stretch");
			chckbxPerformStretch.setFont(new Font("Tahoma", Font.PLAIN, 14));
			chckbxPerformStretch.setToolTipText("<html>Check this box if stretching should be performed during this operation</html>");
			chckbxPerformStretch.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					if (chckbxPerformStretch.isSelected()){
						for (Component component : getPnlStretchSet().getComponents()) {
							component.setEnabled(true);
						}
					}
					else{
						getSpStretchPer().setValue(100);
						for (Component component : getPnlStretchSet().getComponents()) {
							component.setEnabled(false);
						}
					}
				}
			});
			chckbxPerformStretch.setSelected(true);
			
		}
		return chckbxPerformStretch;
	}
	JPanel getPnlStretchSet() {
		if (pnlStretchSet == null) {
			pnlStretchSet = new JPanel();
			pnlStretchSet.setLayout(new MigLayout("", "[][grow][]", "[][]"));
			pnlStretchSet.add(getLabel_1(), "cell 0 0,alignx trailing");
			pnlStretchSet.add(getSpStretchRate(), "cell 1 0,growx");
			pnlStretchSet.add(getLabel_3(), "cell 2 0");
			pnlStretchSet.add(getLabel_2(), "cell 0 1,alignx trailing");
			pnlStretchSet.add(getSpStretchPer(), "cell 1 1,growx");
			pnlStretchSet.add(getLabel_4(), "cell 2 1");
		}
		return pnlStretchSet;
	}
	private JLabel getLabel_1() {
		if (lblStretchRate == null) {
			lblStretchRate = new JLabel("Stretch Rate:");
			lblStretchRate.setFont(new Font("Tahoma", Font.PLAIN, 14));
			lblStretchRate.setHorizontalAlignment(SwingConstants.RIGHT);
		}
		return lblStretchRate;
	}
	private JLabel getLabel_2() {
		if (lblStretchAmount == null) {
			lblStretchAmount = new JLabel("Stretch Amount:");
			lblStretchAmount.setFont(new Font("Tahoma", Font.PLAIN, 14));
			lblStretchAmount.setHorizontalAlignment(SwingConstants.RIGHT);
		}
		return lblStretchAmount;
	}
	private JLabel getLabel_3() {
		if (lblMms_1 == null) {
			lblMms_1 = new JLabel("mm/min");
			lblMms_1.setFont(new Font("Tahoma", Font.PLAIN, 14));
		}
		return lblMms_1;
	}
	private JLabel getLabel_4() {
		if (label == null) {
			label = new JLabel("%");
			label.setFont(new Font("Tahoma", Font.PLAIN, 14));
		}
		return label;
	}
	MachineGraphic getMG(){
		if(mG == null){
			mG = new MachineGraphic(pathCreator);
		}
		return mG;
	}
	MachineGraphic getMG2(){
		if(mG2 == null){
			mG2 = new MachineGraphic(pathCreator);
		}
		return mG2;
	}
	
	private JPanel getPnlExtDraw() {
		if (pnlExtDraw == null) {
			pnlExtDraw = new JPanel();
			pnlExtDraw.setLayout(new BorderLayout(0, 0));
			pnlExtDraw.setMinimumSize(new Dimension(50,50));
			pnlExtDraw.add(getMG());
		}
		return pnlExtDraw;
	}
	private JLabel getLabel_1_1() {
		if (lblSideMargins == null) {
			lblSideMargins = new JLabel("Side Margins:");
			lblSideMargins.setFont(new Font("Tahoma", Font.PLAIN, 14));
			lblSideMargins.setHorizontalAlignment(SwingConstants.RIGHT);
		}
		return lblSideMargins;
	}
	private JLabel getLabel_1_2() {
		if (lblMm_1 == null) {
			lblMm_1 = new JLabel("mm");
			lblMm_1.setFont(new Font("Tahoma", Font.PLAIN, 14));
		}
		return lblMm_1;
	}
	private JLabel getLabel_1_3() {
		if (lblThreadLength == null) {
			lblThreadLength = new JLabel("Thread Length:");
			lblThreadLength.setFont(new Font("Tahoma", Font.PLAIN, 14));
			lblThreadLength.setHorizontalAlignment(SwingConstants.RIGHT);
		}
		return lblThreadLength;
	}
	private JSpinner getSpThreadLength() {
		if (spThreadLength == null) {
			spThreadLength = new JSpinner();
			spThreadLength.setFont(new Font("Tahoma", Font.PLAIN, 14));
			spThreadLength.setToolTipText("<html>The length that the threads should be made (between the end bars).<br>This parameter is limited by the bed length, the bar thickness, and the stretch percentage</html>");
			spThreadLength.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {
					updateRanges();
					prefs.setThreadLength((int)spThreadLength.getValue());
				}
			});
			spThreadLength.setModel(new SpinnerNumberModel(1, 1, 500, 1));
			spThreadLength.setValue(prefs.getThreadLength());
		}
		return spThreadLength;
	}
	private JSpinner getSpExtRate() {
		if (spExtRate == null) {
			spExtRate = new JSpinner();
			spExtRate.setFont(new Font("Tahoma", Font.PLAIN, 14));
			spExtRate.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent arg0) {
					prefs.setExtrusionRate((int) spExtRate.getValue());
				}
			});
			spExtRate.setToolTipText("<html>This is the rate at which the syringe pump will be run (in the indicated units)</html>");
			spExtRate.setModel(new SpinnerNumberModel(1, 1, 20000, 1));
			spExtRate.setValue(prefs.getExtrusionRate());
		}
		return spExtRate;
	}
	private JSpinner getSpFeed() {
		if (spFeed == null) {
			spFeed = new JSpinner();
			spFeed.setFont(new Font("Tahoma", Font.PLAIN, 14));
			spFeed.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {
					prefs.setFeedrate((int) spFeed.getValue());
				}
			});
			spFeed.setToolTipText("<html>This is the speed at which the nozzle will move throughout the operation.</html>");
			spFeed.setModel(new SpinnerNumberModel(600, 1, 20000, 1));
			spFeed.setValue(prefs.getFeedrate());
		}
		return spFeed;
	}
	private JSpinner getSpThreadSpace() {
		if (spThreadSpace == null) {
			spThreadSpace = new JSpinner();
			spThreadSpace.setFont(new Font("Tahoma", Font.PLAIN, 14));
			spThreadSpace.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {
					updateRanges();
					prefs.setThreadSpacing((int)spThreadSpace.getValue());
				}
			});
			spThreadSpace.setModel(new SpinnerNumberModel(2, 2, 50, 1));
			spThreadSpace.setValue(prefs.getThreadSpacing());
		}
		return spThreadSpace;
	}
	private JSpinner getSpTSPT() {
		if (spTSPT == null) {
			spTSPT = new JSpinner();
			spTSPT.setFont(new Font("Tahoma", Font.PLAIN, 14));
			spTSPT.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {
					prefs.setTSPT((int)spTSPT.getValue());
				}
			});
			spTSPT.setToolTipText("<html>This is the amount of time the machine should pause at the beginning of each thread.<br>This pause is to allow a small \"blob\" to form for better adhesion</html>");
			spTSPT.setModel(new SpinnerNumberModel(0, 0, 1000, 1));
			spTSPT.setValue(prefs.getTSPT());
		}
		return spTSPT;
	}
	private JSpinner getSpSideMargins() {
		if (spSideMargins == null) {
			spSideMargins = new JSpinner();
			spSideMargins.setFont(new Font("Tahoma", Font.PLAIN, 14));
			spSideMargins.setToolTipText("<html>This is the distance from the side of the extrusion area to act as a buffer zone that threads should not be made in</html>");
			spSideMargins.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {
					updateRanges();
					prefs.setSideMargins((int)spSideMargins.getValue());
				}
			});
			spSideMargins.setModel(new SpinnerNumberModel(0, 0, 50, 1));
			spSideMargins.setValue(prefs.getSideMargins());
		}
		return spSideMargins;
	}
	private JLabel getLabel_1_4() {
		if (lblMm_2 == null) {
			lblMm_2 = new JLabel("mm");
			lblMm_2.setFont(new Font("Tahoma", Font.PLAIN, 14));
		}
		return lblMm_2;
	}
	private JSpinner getSpBedY() {
		if (spBedY == null) {
			spBedY = new JSpinner();
			spBedY.setFont(new Font("Tahoma", Font.PLAIN, 14));
			spBedY.setToolTipText("This parameter sets the total length of the bed.  This is the total available length available to the machine.  This is limited to a minimum of 25 mm, anything else would be ridiculous...");
			spBedY.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {
					updateRanges();
					prefs.setBedY((int) spBedY.getValue());
				}
			});
			spBedY.setModel(new SpinnerNumberModel(310, 25, 320, 1));
			spBedY.setValue(prefs.getBedY());
		}
		return spBedY;
	}
	private JLabel getLabel_1_5() {
		if (lblBedLength == null) {
			lblBedLength = new JLabel("Bed Length:");
			lblBedLength.setFont(new Font("Tahoma", Font.PLAIN, 14));
			lblBedLength.setHorizontalAlignment(SwingConstants.RIGHT);
		}
		return lblBedLength;
	}
	private JLabel getLabel_1_6() {
		if (lblMm_3 == null) {
			lblMm_3 = new JLabel("mm");
			lblMm_3.setFont(new Font("Tahoma", Font.PLAIN, 14));
		}
		return lblMm_3;
	}
	private JSpinner getSpBedX() {
		if (spBedX == null) {
			spBedX = new JSpinner();
			spBedX.setFont(new Font("Tahoma", Font.PLAIN, 14));
			spBedX.setToolTipText("This parameter is for setting the width of the extrusion bed.\r\nThe bed can be as wide as desired, but it is limited to a minimum of at least 25 mm wide, anything less would be kinda ridiculous...");
			spBedX.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {
					updateRanges();
					prefs.setBedX((int)spBedX.getValue());
				}
			});
			spBedX.setModel(new SpinnerNumberModel(150, 25, 150, 1));
			spBedX.setValue(prefs.getBedX());
		}
		return spBedX;
	}
	private JLabel getLabel_1_7() {
		if (lblMm_4 == null) {
			lblMm_4 = new JLabel("mm");
			lblMm_4.setFont(new Font("Tahoma", Font.PLAIN, 14));
		}
		return lblMm_4;
	}
	private JLabel getLabel_1_8() {
		if (lblBedWidth == null) {
			lblBedWidth = new JLabel("Bed Width:");
			lblBedWidth.setFont(new Font("Tahoma", Font.PLAIN, 14));
			lblBedWidth.setHorizontalAlignment(SwingConstants.RIGHT);
		}
		return lblBedWidth;
	}
	JSpinner getSpStretchPer() {
		if (spStretchPer == null) {
			spStretchPer = new JSpinner();
			spStretchPer.setFont(new Font("Tahoma", Font.PLAIN, 14));
			spStretchPer.setModel(new SpinnerNumberModel(100, 100, 700, 1));
			spStretchPer.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent arg0) {
					updateRanges();
					prefs.setStretchPercent((int)spStretchPer.getValue());
				}
			});
			spStretchPer.setToolTipText("This determines the percentage of the original length the threads will be stretched.\r\n100% = The threads will remain their same length\r\n50% = The threads will be compressed to half of their length\r\n200% = The threads will be stretched to double their length");
			spStretchPer.setValue(prefs.getStretchPercent());
		}
		return spStretchPer;
	}
	private JSpinner getSpStretchRate() {
		if (spStretchRate == null) {
			spStretchRate = new JSpinner();
			spStretchRate.setFont(new Font("Tahoma", Font.PLAIN, 14));
			spStretchRate.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {
					prefs.setStretchRate((int) spStretchRate.getValue());
				}
			});
			spStretchRate.setModel(new SpinnerNumberModel(1, 1, 20000, 1));
			spStretchRate.setValue(prefs.getStretchRate());
		}
		return spStretchRate;
	}
	private JLabel getLabel_1_9() {
		if (lblBarThickness == null) {
			lblBarThickness = new JLabel("Bar Thickness:");
			lblBarThickness.setFont(new Font("Tahoma", Font.PLAIN, 14));
			lblBarThickness.setHorizontalAlignment(SwingConstants.RIGHT);
		}
		return lblBarThickness;
	}
	private JSpinner getSpBarThick() {
		if (spBarThick == null) {
			spBarThick = new JSpinner();
			spBarThick.setFont(new Font("Tahoma", Font.PLAIN, 14));
			spBarThick.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {
					updateRanges();
					prefs.setBarThickness((int) spBarThick.getValue());
				}
			});
			spBarThick.setValue(prefs.getBarThickness());
		}
		return spBarThick;
	}
	private JLabel getLabel_1_10() {
		if (lblMm_5 == null) {
			lblMm_5 = new JLabel("mm");
			lblMm_5.setFont(new Font("Tahoma", Font.PLAIN, 14));
		}
		return lblMm_5;
	}
	private static void changeSpinner(JSpinner _spinner, int _min, int _max){
		int curVal = (int)_spinner.getValue();
		if (curVal < _min){
			curVal = _min;
		}
		if (curVal > _max){
			curVal = _max;
		}
		int min = _min;
		int max = _max;
		if (_min >= _max){
			int holder = _min;
			min = _max;
			max = holder;
		}
		SpinnerNumberModel model = (SpinnerNumberModel) _spinner.getModel();
		_spinner.setModel(new SpinnerNumberModel(curVal, min, max, model.getStepSize()));
	}
	void updateRanges(){
		int availWid = (int)getSpBedX().getValue() - ((int)getSpSideMargins().getValue()*2);
		int maxLen = 0;
		maxLen = (int) ((int)getSpBedY().getValue() - (((int)getSpBarThick().getValue())*2) - ((int)getSpThreadLength().getValue()*(((int)getSpStretchPer().getValue()-100d)/100d)));
		System.out.println(maxLen);
		changeSpinner(getSpThreadLength(),1,maxLen);
		int maxMargin = (((int)getSpBedX().getValue()) - ((int)getSpThreadSpace().getValue()*(int)getSpNumThreads().getValue()))/2 - 3;
		changeSpinner(getSpSideMargins(),1,maxMargin);
		int maxSpacing = availWid/((int)getSpNumThreads().getValue());
		changeSpinner(getSpThreadSpace(),2,maxSpacing);
		int maxThreads = availWid/((int)getSpThreadSpace().getValue());
		changeSpinner(getSpNumThreads(),1,maxThreads);
		
		int polyTime = 0;
		String[] timeString = getFrmtdtxtfldPolyTime().getText().split(":");
		int hours = Integer.parseInt(timeString[0]);
		int minutes = Integer.parseInt(timeString[1]);
		int seconds = Integer.parseInt(timeString[2]);					
		polyTime = (hours*3600) + (minutes*60) + (seconds);
		pathCreator.setParams((int) getSpBedX().getValue(),
				(int) getSpBedY().getValue(),
				(int) getSpSideMargins().getValue(),
				(int) getSpExtRate().getValue(),
				(int) getSpThreadLength().getValue(),
				(int) getSpFeed().getValue(),
				(int) getSpThreadSpace().getValue(),
				(int) getSpTSPT().getValue(),
				polyTime,
				(int) getSpBarThick().getValue(),
				(int) getSpNumThreads().getValue(),
				(int) getSpStretchPer().getValue(),
				(int) getSpStretchRate().getValue());
		
		pathCreator.doCalculations();
		if(getMG().isShowing()){
			getMG().repaint();
		}
		
		if(getMG2().isShowing()){
		getMG2().repaint();
		}
	}
	private JLabel getLabel_1_11() {
		if (lblOfThreads == null) {
			lblOfThreads = new JLabel("# of Threads:");
			lblOfThreads.setFont(new Font("Tahoma", Font.PLAIN, 14));
			lblOfThreads.setHorizontalAlignment(SwingConstants.RIGHT);
		}
		return lblOfThreads;
	}
	private JSpinner getSpNumThreads() {
		if (spNumThreads == null) {
			spNumThreads = new JSpinner();
			spNumThreads.setFont(new Font("Tahoma", Font.PLAIN, 14));
			spNumThreads.setToolTipText("<html>This is the number of threads that will be made.<br>NOTE: If this number is increased too high, the thread spacing will be reduced to accomodate the number of threads</html>");
			spNumThreads.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {
					updateRanges();
					prefs.setNumThreads((int)spNumThreads.getValue());
				}
			});
			spNumThreads.setModel(new SpinnerNumberModel(1, 1, 20000, 1));
			spNumThreads.setValue(prefs.getNumThreads());
		}
		return spNumThreads;
	}
	
	void disableEverythingBelow(Component root){
		root.setEnabled(false);
		try {
			Container c = (Container) root;
			for (Component component : c.getComponents()) {
				disableEverythingBelow(component);
			}
		} catch (Exception e) {
			root.setEnabled(false);
		}

	}
	void enableEverythingBelow(Component root){
		root.setEnabled(true);
		try {
			Container c = (Container) root;
			for (Component component : c.getComponents()) {
				enableEverythingBelow(component);
			}
		} catch (Exception e) {
			root.setEnabled(true);
		}

	}	

	private JButton getBtnAutoConfigurePorts() {
		if (btnAutoConfigurePorts == null) {
			btnAutoConfigurePorts = new JButton("Auto. Configure Ports");
			btnAutoConfigurePorts.setFont(new Font("Tahoma", Font.PLAIN, 14));
			btnAutoConfigurePorts.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					disableEverythingBelow(getTabbedPane());
					
					updatePorts();
					GrblControl gCtrl = new GrblControl();		
					try {
						getCb_GrblPort().setSelectedItem(gCtrl.getPort());
						
					} catch (Exception e) {
						//Catch any exceptions thrown for illegal selection of items
					}
					//Only auto configure pump port if automatic pump control is enabled
					if (!getChckbxManualPumpCtrl().isSelected()){
						PumpControl pCtrl = new PumpControl();
						try {
							getCb_PumpPort().setSelectedItem(pCtrl.getPort());
						} catch (Exception e) {
							//Catch any exceptions thrown for illegal selection of items
						}
						
					}
					enableEverythingBelow(getTabbedPane());
					getCb_PumpPort().setEnabled(!getChckbxManualPumpCtrl().isSelected());
				}
			});
		}
		return btnAutoConfigurePorts;
	}
	private JButton getBtnSaveCurrentProfile() {
		if (btnSaveCurrentProfile == null) {
			btnSaveCurrentProfile = new JButton("Save Current Profile");
			btnSaveCurrentProfile.setFont(new Font("Tahoma", Font.PLAIN, 14));
			btnSaveCurrentProfile.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					saveProfile();
				}
			});
		}
		return btnSaveCurrentProfile;
	}
	public void saveProfile(){
		profileMan.setProfileOption("BedLength", getSpBedY().getValue());
		profileMan.setProfileOption("BedWidth", getSpBedX().getValue());
		profileMan.setProfileOption("BarThickness", getSpBarThick().getValue());
		profileMan.setProfileOption("JobName", getTxtJobName().getText());	//Job Name
		profileMan.setProfileOption("JobDescription", getTxtJobDescription().getText());	//Job Description
		profileMan.setProfileOption("PerformStretch", getChckbxPerformStretch().isSelected());	//Perform Stretch
		profileMan.setProfileOption("StretchRate", getSpStretchRate().getValue());	//Stretch Rate
		profileMan.setProfileOption("StretchPercentage", getSpStretchPer().getValue());	//Stretch Percentage
		profileMan.setProfileOption("PerformExtrusion", getChckbxPerformExtrusion().isSelected());	//Perform Extrusion
		profileMan.setProfileOption("SideMargins", getSpSideMargins().getValue());	//Side Margins
		profileMan.setProfileOption("ExtrusionRate", getSpExtRate().getValue());	//Extrusion Rate
		profileMan.setProfileOption("Feedrate", getSpFeed().getValue());	//Feedrate
		profileMan.setProfileOption("ThreadSpacing", getSpThreadSpace().getValue());	//Thread Spacing
		profileMan.setProfileOption("ThreadStartPauseTime", getSpTSPT().getValue());	//Thread Start Pause Time
		if (getFrmtdtxtfldPolyTime().getValue() != null){
			profileMan.setProfileOption("PolymerizationTime", getFrmtdtxtfldPolyTime().getText());	//Polymerization Time
		}
		else{
			profileMan.setProfileOption("PolymerizationTime", "00:00:00");	//Polymerization Time
		}
		profileMan.setProfileOption("ThreadLength", getSpThreadLength().getValue());	//Thread Length
		profileMan.setProfileOption("ThreadNum", getSpNumThreads().getValue());	//# of Threads
		
		profileMan.setProfileOption("SyringeDia", getFrmtdtxtfldSyringeDiameter().getText());
		
		profileMan.setProfileOption("PumpUnits", getCbUnits().getSelectedItem());
		profileMan.saveProfile();
	}
	
	public void loadProfile(){
		profileMan.loadProfile();
		getSpBedY().setValue(profileMan.getProfileOption("BedLength"));
		getSpBedX().setValue(profileMan.getProfileOption("BedWidth"));
		getFrmtdtxtfldSyringeDiameter().setText((String) profileMan.getProfileOption("SyringeDia"));
		getSpBarThick().setValue(profileMan.getProfileOption("BarThickness"));
		getCbUnits().setSelectedItem(profileMan.getProfileOption("PumpUnits"));
		getTxtJobName().setText((String) profileMan.getProfileOption("JobName"));
		getTxtJobDescription().setText((String) profileMan.getProfileOption("JobDescription"));
		getChckbxPerformStretch().setSelected((boolean) profileMan.getProfileOption("PerformStretch"));
		getSpStretchRate().setValue(profileMan.getProfileOption("StretchRate"));
		getSpStretchPer().setValue(profileMan.getProfileOption("StretchPercentage"));
		getChckbxPerformExtrusion().setSelected((boolean) profileMan.getProfileOption("PerformExtrusion"));
		getSpSideMargins().setValue(profileMan.getProfileOption("SideMargins"));
		getSpExtRate().setValue(profileMan.getProfileOption("ExtrusionRate"));
		getSpFeed().setValue(profileMan.getProfileOption("Feedrate"));
		getSpThreadSpace().setValue(profileMan.getProfileOption("ThreadSpacing"));
		getSpTSPT().setValue(profileMan.getProfileOption("ThreadStartPauseTime"));
		getFrmtdtxtfldPolyTime().setValue(profileMan.getProfileOption("PolymerizationTime"));
		getSpThreadLength().setValue(profileMan.getProfileOption("ThreadLength"));
		getSpNumThreads().setValue(profileMan.getProfileOption("ThreadNum"));
		
	}
	private JButton getBtnLoadProfile() {
		if (btnLoadProfile == null) {
			btnLoadProfile = new JButton("Load Profile");
			btnLoadProfile.setFont(new Font("Tahoma", Font.PLAIN, 14));
			btnLoadProfile.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					loadProfile();
				}
			});
		}
		return btnLoadProfile;
	}

	@Override
	public void stageCompleted(ProcessStage completeStage) {
		switch (completeStage){
		case ALIGNING_STRETCH_BAR:
			setLabelColorToComplete(getLabelAdjustStretchBar());
			break;
		case CLEAN_PUMP:
			setLabelColorToComplete(getLabelWaitingForCleaning());
			break;
		case EXTRUDING:
			setLabelColorToComplete(getLabelExtruding());
			break;
		case EXTRUSION_COMPLETE:
			
			break;
		case HOMING:
			setLabelColorToComplete(getLabelHoming());
			break;
		case INITIALIZE_PUMP:
			setLabelColorToComplete(getLabelInitializeRun());
			break;
		case OPERATION_COMPLETE:
			setLabelColorToComplete(getLabelOperationComplete());
			break;
		case POLYMERIZING:
			setLabelColorToComplete(getLabelPolymerizing());
			break;
		case PRESTART:
			
			break;
		case PRE_EXT_WIPE:
			break;
		case PURGING_PUMP:
			setLabelColorToComplete(getLabelPurging());
			break;
		case READY_TO_ALIGN_STRETCH:
			
			break;
		case MANUALPOLYMERIZING:
			setLabelColorToComplete(getLabelPolymerizing());
			break;
		case READY_TO_EXTRUDE:
			setLabelColorToComplete(getLabelReadyToExtrude());
			break;
		case READY_TO_HOME:
			setLabelColorToComplete(getLabelReadyToHome());
			break;
		case READY_TO_START_PUMP:
			setLabelColorToComplete(getLabelReadyToPurge());
			break;
		case READY_TO_STRETCH:
			setLabelColorToComplete(getLabelReadyToStretch());
			break;
		case STRETCHING:
			setLabelColorToComplete(getLabelStretching());
			break;
		case STRETCH_COMPLETE:
			break;
		default:
			break;
		
		}		
	}
	
	@Override
	public void stageStarted(ProcessStage newStage) {
		switch (processExec.getCurrentStage()){
		case ALIGNING_STRETCH_BAR:
			setLabelColorToRunning(getLabelAdjustStretchBar());
			break;
		case CLEAN_PUMP:
			setLabelColorToRunning(getLabelWaitingForCleaning());
			break;
		case EXTRUDING:
			setLabelColorToRunning(getLabelExtruding());
			break;
		case EXTRUSION_COMPLETE:
			
			break;
		case HOMING:
			setLabelColorToRunning(getLabelHoming());
			break;
		case INITIALIZE_PUMP:
			setLabelColorToRunning(getLabelInitializeRun());
			break;
		case OPERATION_COMPLETE:
			setLabelColorToRunning(getLabelOperationComplete());
			//grblDev.homeGrbl();
			break;
		case POLYMERIZING:
			setLabelColorToRunning(getLabelPolymerizing());
			break;
		case PRESTART:
			
			break;
		case PRE_EXT_WIPE:
			break;
		case PURGING_PUMP:
			setLabelColorToRunning(getLabelPurging());
			break;
		case MANUALPOLYMERIZING:
			setLabelColorToRunning(getLabelPolymerizing());
			break;
		case READY_TO_ALIGN_STRETCH:
			
			break;
		case READY_TO_EXTRUDE:
			setLabelColorToRunning(getLabelReadyToExtrude());
			break;
		case READY_TO_HOME:
			setLabelColorToRunning(getLabelReadyToHome());
			break;
		case READY_TO_START_PUMP:
			setLabelColorToRunning(getLabelReadyToPurge());
			break;
		case READY_TO_STRETCH:
			setLabelColorToRunning(getLabelReadyToStretch());
			break;
		case STRETCHING:
			setLabelColorToRunning(getLabelStretching());
			break;
		case STRETCH_COMPLETE:
			break;
		default:
			break;
		
		}		
	}
	private static void setLabelColorToRunning(JLabel _label){
		_label.setForeground(Color.BLUE);
	}
	private static void setLabelColorToComplete(JLabel _label){
		_label.setForeground(Color.GREEN);
	}
	@Override
	public void waitingForExternal() {
		switch (processExec.getCurrentStage()) {
		case READY_TO_HOME:
			JOptionPane.showMessageDialog(frame,"<html>Please configure the machine for homing.<br>Homing will begin immediately upon acknowledging this message.</html>", "Ready to Begin Homing Procedure", JOptionPane.INFORMATION_MESSAGE);
			break;
		case READY_TO_ALIGN_STRETCH:
			JOptionPane.showMessageDialog(frame,"<html>Please configure the machine for aligning the stretch bar.<br>Alignment will begin immediately upon acknowledging this message.<br>To avoid damage to machine, please ensure nozzle is not attached</html>", "Ready to Align Stretch Bar", JOptionPane.INFORMATION_MESSAGE);
			break;
		case READY_TO_START_PUMP:
			if (getChckbxManualPumpCtrl().isSelected()){
				JOptionPane.showMessageDialog(frame,"<html>Please configure the machine for starting and purging the syringe pump.<br>Move first shoe so that nozzle is positioned over the back of the shoe.<br>Please start pumping immediately upon acknowledging this message.</html>", "Ready to Start/Purge Pump", JOptionPane.INFORMATION_MESSAGE);
			}
			else{
				JOptionPane.showMessageDialog(frame,"<html>Please configure the machine for starting and purging the syringe pump.<br>Move first shoe so that nozzle is positioned over the back of the shoe.<br>Pumping will begin immediately upon acknowledging this message.</html>", "Ready to Start/Purge Pump", JOptionPane.INFORMATION_MESSAGE);
			}
			break;
		case READY_TO_EXTRUDE:
			JOptionPane.showMessageDialog(frame,"<html>Please make sure the machine is ready to begin extruding.<br>The pump should be purged and running, and the nozzle should be hooked up and ready.<br>Extrusion will begin immediately after acknowledging this message.</html>", "Ready to Begin Extrusion", JOptionPane.INFORMATION_MESSAGE);
			break;
		case CLEAN_PUMP:
			JOptionPane.showMessageDialog(frame,"<html>Extrusion is complete.<br>Please disconnect and clean nozzle assembly.<br>Machine will return to home immediately after acknowledging this message.<br>Failure to remove nozzle may result in damage to the machine and/or threads.</html>", "Extrusion Complete", JOptionPane.INFORMATION_MESSAGE);
			break;
		case READY_TO_STRETCH:
			JOptionPane.showMessageDialog(frame,"<html>Please configure the machine for stretching.<br>Stretching will begin immediately upon acknowledging this message.<br>Be sure to stop the pump and remove the nozzle from the machine before continuing to avoid damage.</html>", "Ready to Begin Stretching", JOptionPane.INFORMATION_MESSAGE);
			break;
		default:
			break;
		}
		
		processExec.finishHold();
		
	}
	private JTextField getTxtProcesstimer() {
		if (txtProcesstimer == null) {
			txtProcesstimer = new JTextField();
			txtProcesstimer.setFont(new Font("Tahoma", Font.BOLD, 24));
			txtProcesstimer.setHorizontalAlignment(SwingConstants.LEFT);
			txtProcesstimer.setEditable(false);
			txtProcesstimer.setText("00:00:00");
			txtProcesstimer.setColumns(10);
		}
		return txtProcesstimer;
	}
	private JPanel getPnlStretchDraw() {
		if (pnlStretchDraw == null) {
			pnlStretchDraw = new JPanel();
			pnlStretchDraw.setMinimumSize(new Dimension(50, 10));
			pnlStretchDraw.setLayout(new BorderLayout(0, 0));
			pnlStretchDraw.add(getMG2());
		}
		return pnlStretchDraw;
	}
	private JButton getBtnRunOperationAuto() {
		if (btnRunOperationAuto == null) {
			btnRunOperationAuto = new JButton("Run Operation Auto Poly");
			btnRunOperationAuto.setFont(new Font("Tahoma", Font.PLAIN, 14));
			btnRunOperationAuto.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					runOperation(false);
				}
			});
		}
		return btnRunOperationAuto;
	}
	private JLabel getLabelInitializeRun() {
		if (lblInitializeRun == null) {
			lblInitializeRun = new JLabel(" Initialize Run");
			lblInitializeRun.setForeground(Color.BLACK);
			lblInitializeRun.setBackground(UIManager.getColor("Button.background"));
			lblInitializeRun.setFont(new Font("Tahoma", Font.BOLD, 24));
			lblInitializeRun.setHorizontalAlignment(SwingConstants.CENTER);
			stageLabels.add(lblInitializeRun);
		}
		return lblInitializeRun;
	}
	private JLabel getLabelReadyToHome() {
		if (lblReadyToHome == null) {
			lblReadyToHome = new JLabel(" Ready To Home");
			lblReadyToHome.setFont(new Font("Tahoma", Font.BOLD, 24));
			stageLabels.add(lblReadyToHome);
		}
		return lblReadyToHome;
	}
	private JLabel getLabelHoming() {
		if (lblHoming == null) {
			lblHoming = new JLabel(" Homing");
			lblHoming.setFont(new Font("Tahoma", Font.BOLD, 24));
			stageLabels.add(lblHoming);
		}
		return lblHoming;
	}
	private JLabel getLabelAdjustStretchBar() {
		if (lblAdjustingStretchBar == null) {
			lblAdjustingStretchBar = new JLabel(" Adjusting Stretch Bar");
			lblAdjustingStretchBar.setFont(new Font("Tahoma", Font.BOLD, 24));
			stageLabels.add(lblAdjustingStretchBar);
		}
		return lblAdjustingStretchBar;
	}
	private JLabel getLabelReadyToPurge() {
		if (lblReadyToPurge == null) {
			lblReadyToPurge = new JLabel(" Ready To Purge");
			lblReadyToPurge.setFont(new Font("Tahoma", Font.BOLD, 24));
			stageLabels.add(lblReadyToPurge);
		}
		return lblReadyToPurge;
	}
	private JLabel getLabelPurging() {
		if (lblPurging == null) {
			lblPurging = new JLabel(" Purging");
			lblPurging.setFont(new Font("Tahoma", Font.BOLD, 24));
			stageLabels.add(lblPurging);
		}
		return lblPurging;
	}
	private JLabel getLabelReadyToExtrude() {
		if (lblReadyToExtrude == null) {
			lblReadyToExtrude = new JLabel(" Ready To Extrude");
			lblReadyToExtrude.setFont(new Font("Tahoma", Font.BOLD, 24));
			stageLabels.add(lblReadyToExtrude);
		}
		return lblReadyToExtrude;
	}
	private JLabel getLabelWaitingForCleaning() {
		if (lblWaitingForCleaningpolymerizing == null) {
			lblWaitingForCleaningpolymerizing = new JLabel(" Waiting for Cleaning/Polymerizing");
			lblWaitingForCleaningpolymerizing.setFont(new Font("Tahoma", Font.BOLD, 24));
			stageLabels.add(lblWaitingForCleaningpolymerizing);
		}
		return lblWaitingForCleaningpolymerizing;
	}
	private JLabel getLabelPolymerizing() {
		if (lblPolymerizing == null) {
			lblPolymerizing = new JLabel(" Polymerizing");
			lblPolymerizing.setFont(new Font("Tahoma", Font.BOLD, 24));
			stageLabels.add(lblPolymerizing);
		}
		return lblPolymerizing;
	}
	private JLabel getLabelReadyToStretch() {
		if (lblReadyToStretch == null) {
			lblReadyToStretch = new JLabel(" Ready to Stretch");
			lblReadyToStretch.setFont(new Font("Tahoma", Font.BOLD, 24));
			stageLabels.add(lblReadyToStretch);
		}
		return lblReadyToStretch;
	}
	private JLabel getLabelStretching() {
		if (lblStretching == null) {
			lblStretching = new JLabel(" Stretching");
			lblStretching.setFont(new Font("Tahoma", Font.BOLD, 24));
			stageLabels.add(lblStretching);
		}
		return lblStretching;
	}
	private JLabel getLabelOperationComplete() {
		if (lblOperationComplete == null) {
			lblOperationComplete = new JLabel(" Operation Complete");
			lblOperationComplete.setFont(new Font("Tahoma", Font.BOLD, 24));
			stageLabels.add(lblOperationComplete);
		}
		return lblOperationComplete;
	}
	private JLabel getLabelExtruding() {
		if (lblExtruding == null) {
			lblExtruding = new JLabel(" Extruding");
			lblExtruding.setFont(new Font("Tahoma", Font.BOLD, 24));
			stageLabels.add(lblExtruding);
		}
		return lblExtruding;
	}
	JCheckBox getChckbxManualPumpCtrl() {
		if (chckbxManualPumpCtrl == null) {
			chckbxManualPumpCtrl = new JCheckBox("Manual Pump Ctrl");
			chckbxManualPumpCtrl.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					updatePorts();
				}
			});
			chckbxManualPumpCtrl.setFont(new Font("Tahoma", Font.PLAIN, 14));
		}
		return chckbxManualPumpCtrl;
	}
	private JTextField getTxtExtrudeTimer() {
		if (txtExtrudeTimer == null) {
			txtExtrudeTimer = new JTextField();
			txtExtrudeTimer.setText("00:00:00");
			txtExtrudeTimer.setHorizontalAlignment(SwingConstants.LEFT);
			txtExtrudeTimer.setFont(new Font("Tahoma", Font.BOLD, 24));
			txtExtrudeTimer.setEditable(false);
			txtExtrudeTimer.setColumns(10);
		}
		return txtExtrudeTimer;
	}
	private JTextField getTxtStretchTimer() {
		if (txtStretchTimer == null) {
			txtStretchTimer = new JTextField();
			txtStretchTimer.setText("00:00:00");
			txtStretchTimer.setHorizontalAlignment(SwingConstants.LEFT);
			txtStretchTimer.setFont(new Font("Tahoma", Font.BOLD, 24));
			txtStretchTimer.setEditable(false);
			txtStretchTimer.setColumns(10);
		}
		return txtStretchTimer;
	}
	private JTextField getTxtOperationTimer() {
		if (txtOperationTimer == null) {
			txtOperationTimer = new JTextField();
			txtOperationTimer.setText("00:00:00");
			txtOperationTimer.setHorizontalAlignment(SwingConstants.LEFT);
			txtOperationTimer.setFont(new Font("Tahoma", Font.BOLD, 24));
			txtOperationTimer.setEditable(false);
			txtOperationTimer.setColumns(10);
		}
		return txtOperationTimer;
	}
}

