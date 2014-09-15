package edu.gatech.gradeseer.gradinggui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import edu.gatech.gradeseer.fileio.ConfigManager;
import edu.gatech.gradeseer.gradinggui.helpers.Dialogs;
import edu.gatech.gradeseer.gradingmodel.Configuration;

public class Menus extends JMenuBar {
	private JFrame madeFor;

	public Menus(JFrame madeFor, Configuration configuration) {
		this.madeFor = madeFor;
		ConfigMenu configMenu = new ConfigMenu(configuration);
		HelpMenu help = new HelpMenu(configuration);
		this.add(configMenu);
		this.add(help);
	}

	private class ConfigMenu extends JMenu {
		private Configuration config;
		private JMenuItem setBaseDir;
		private JMenuItem displayIntro;

		public ConfigMenu(Configuration config) {
			super("Config");
			this.config = config;
			setBaseDir = new JMenuItem("Set base dir");
			if (config.getShowIntro())
				displayIntro = new JMenuItem("Set dont show intro");
			else
				displayIntro = new JMenuItem("Set show intro");
			setBaseDir.addActionListener(new ItemListener());
			displayIntro.addActionListener(new ItemListener());
			this.add(setBaseDir);
			this.add(displayIntro);
		}

		public void setConfig(Configuration config) {
			this.config = config;
		}

		private class ItemListener implements ActionListener {

			@Override
			public void actionPerformed(ActionEvent event) {
				if (event.getSource() == setBaseDir) {
					File base = NewUserDisplay.getDirFile(madeFor, config.getBaseDir());
					if (base != null) {
						config.setBaseDir(base);
						ConfigManager.getInstance().save(config);
					}
				}
				if (event.getSource() == displayIntro) {
					if (displayIntro.getText().equals("Display intro")) {
						config.setShowIntro(true);
						displayIntro.setText("Dont show intro");
					} else {
						config.setShowIntro(false);
						displayIntro.setText("Display intro");
					}

				}

			}
		}

	}

	private class HelpMenu extends JMenu {
		private JMenuItem ioStatusInfo;
		private JMenuItem currentInfo;
		private JMenuItem gradingInfo;
		private JMenuItem images;
		private JMenuItem why;
		private JMenuItem overview;
		private Configuration config;

		public HelpMenu(Configuration config) {
			super("Help");
			this.config = config;
			overview = new JMenuItem("Show intro");
			ioStatusInfo = new JMenuItem("Explain IO/Status tab");
			currentInfo = new JMenuItem("Explain Current tab");
			gradingInfo = new JMenuItem("Explain grading view");
			images = new JMenuItem("Images/Files dont load?");
			why = new JMenuItem("Why?");
			overview.addActionListener(new ItemListener());
			ioStatusInfo.addActionListener(new ItemListener());
			currentInfo.addActionListener(new ItemListener());
			gradingInfo.addActionListener(new ItemListener());
			images.addActionListener(new ItemListener());
			why.addActionListener(new ItemListener());
			this.add(overview);
			this.add(ioStatusInfo);
			this.add(currentInfo);
			this.add(gradingInfo);
			this.add(images);
			this.add(why);
		}

		public void setConfig(Configuration config) {
			this.config = config;
		}

		private class ItemListener implements ActionListener {

			@Override
			public void actionPerformed(ActionEvent event) {
				if (event.getSource() == overview) {
					NewUserDisplay.introduce(madeFor, false, false, config);
				}
				if (event.getSource() == ioStatusInfo) {
					JOptionPane.showMessageDialog(madeFor,
							"The IO/Status tab shows various things such as error output or other assorted ouput.");
				}
				if (event.getSource() == currentInfo) {
					JOptionPane.showMessageDialog(madeFor,
							"The current tab displayes the text of the last selected readable file.");
				}
				if (event.getSource() == gradingInfo) {

					JOptionPane.showMessageDialog(madeFor,
							"The grading tab is what it seems - it is for grading within this program.\n"
									+ "It generally tends to work.");
				}
				if (event.getSource() == images) {
					JOptionPane.showMessageDialog(madeFor,
							"This may happen (though it should not). Grade the old fashioned way.\n");
				}
				if (event.getSource() == why) {
					JOptionPane.showMessageDialog(madeFor, "Why not?");
					Dialogs.askIfExitApplication(madeFor);
				}
			}

		}

	}
}
