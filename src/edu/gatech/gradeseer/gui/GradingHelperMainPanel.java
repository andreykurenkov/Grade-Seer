package edu.gatech.gradeseer.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;

import edu.gatech.gradeseer.fileio.ConfigManager;
import edu.gatech.gradeseer.gradingmodel.Configuration;
import edu.gatech.gradeseer.gradingmodel.StudentSet;
import edu.gatech.gradeseer.gui.core.BrowsingPanels;
import edu.gatech.gradeseer.gui.core.GradingPanels;
import edu.gatech.gradeseer.gui.core.Menus;
import edu.gatech.gradeseer.gui.helpers.Dialogs;
import edu.gatech.gradeseer.gui.helpers.NewUserDisplay;

/**
 * The class within which the entire application and gui is set up. This is written as a panel to easily embed into some
 * other frame. However, this class also currently houses the main method and sets up the frame.
 * 
 * @author Andrey Kurenkov
 * @version 1.75 (?)
 */
public class GradingHelperMainPanel extends JPanel {
	public final static Dimension INIT_FRAME_DIM = new Dimension(1200, 1000);// TODO:make based on screen size?
	public static final String frameName = "main";
	private Configuration config;
	private StudentSet mySet;
	private GradingPanels grading;
	private BrowsingPanels browsing;
	private boolean isNewUser;

	/**
	 * The main method of the whole application. Creates the frame, add the MainFrame to it, and start the program.
	 * 
	 * @param args
	 *            command line arguments - not used
	 */
	public static void main(String[] args) {
		// Technicaly how Swing threading is supposed to be done
		// (http://docs.oracle.com/javase/tutorial/uiswing/concurrency/initial.html)
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				GradingHelperMainPanel programPanel = new GradingHelperMainPanel();
				JFrame mainFrame = new JFrame("Homework Grading Helper");

				mainFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
				mainFrame.setName(frameName);
				mainFrame.setPreferredSize(INIT_FRAME_DIM);
				mainFrame.add(programPanel);
				mainFrame.setJMenuBar(new Menus(mainFrame, programPanel.getConfig()));
				mainFrame.addWindowListener(new ConfigSaveListener(mainFrame, programPanel.getConfig()));

				mainFrame.pack();
				mainFrame.setVisible(true);

				if (programPanel.isNewUser) {
					programPanel.handleNewUser();
				}
				/* Program start */
			}
		});
	}

	/**
	 * Constructor that initiates all the core gui elements and initializes all the top-level program variables.
	 */
	public GradingHelperMainPanel() {
		super(new BorderLayout());
		/* New user/config hangling */
		config = ConfigManager.getInstance().load();
		mySet = new StudentSet();
		isNewUser = false;
		if (config == null) {
			isNewUser = true;
			config = new Configuration();
			config.setStudentSet(mySet);
			config.setGradingDir(new File("./"));
		}

		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		grading = new GradingPanels(config);
		browsing = new BrowsingPanels(config, grading);
		splitPane.setLeftComponent(browsing);
		splitPane.setRightComponent(grading);
		splitPane.setDividerLocation(350);
		this.add(splitPane, BorderLayout.CENTER);

	}

	/**
	 * The program considers a user new if it does not find configuration flies in the config directory.
	 * 
	 * @return isNewUser (given value in constructor)
	 */
	public boolean isNewUser() {
		return isNewUser();
	}

	/**
	 * Returns the Configuration of the program, either loaded from file or new.
	 * 
	 * @return config
	 */
	public Configuration getConfig() {
		return config;
	}

	/**
	 * Helper method to handle new users by getting needed settings.
	 */
	private void handleNewUser() {
		File selectedDir = NewUserDisplay.introduceAndGetFiles(INIT_FRAME_DIM, config);
		mySet = config.getStudentSet();
		config.setGradingDir(selectedDir);
		ConfigManager.getInstance().save(config);// To be safe.
		browsing.reloadToConfig(config);
		grading.reloadToConfig(config);

	}

	/**
	 * A nice listener type class that runs when the program ends and saves the configuration upon exist (always).
	 * 
	 * @author Andrey Kurenkov
	 * 
	 */
	public static class ConfigSaveListener extends WindowAdapter {
		private Configuration toSave;
		private JFrame forFrame;

		/**
		 * The frame to not let close and the configuration to save are taken intp this constructor.
		 * 
		 * @param frame
		 * @param save
		 */
		public ConfigSaveListener(JFrame frame, Configuration save) {
			toSave = save;
			this.forFrame = frame;
		}

		/**
		 * The actual method that runs upon closing - it saves the configuration and then closes the JFrame.
		 */
		public void windowClosing(WindowEvent we) {
			ConfigManager.getInstance().save(toSave);
			Dialogs.askIfExitApplication(forFrame);
		}
	}
}
