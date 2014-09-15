package edu.gatech.gradeseer.gradinggui.helpers;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

public class Dialogs {
	public static final int CANCEL_INT = Integer.MIN_VALUE;

	public static int getIntSafely(Component in, String title, String checkMessage, int min, int max) {
		boolean validInput = false;
		int input = -1;
		while (!validInput) {
			try {
				String inputStr = JOptionPane.showInputDialog(in, checkMessage, title, JOptionPane.QUESTION_MESSAGE);
				if (inputStr == null)
					return CANCEL_INT;
				else
					input = Integer.parseInt(inputStr);
				validInput = input <= max && input >= min;
				if (!validInput)
					JOptionPane.showMessageDialog(null, "Not valid. Min value: " + min + " and max " + max + ".");
			} catch (Exception e) {
				JOptionPane.showMessageDialog(null, ":(. Thats not a valid int input. Try again.");
				validInput = false;
			}
		}
		return input;
	}

	public static File getFile(Window in, String title, String inquisition, File startDir, int selection) {
		final JDialog dialog = new JDialog(in, title, Dialog.ModalityType.APPLICATION_MODAL);
		makeSafeToClose(dialog);
		dialog.add(new JLabel(inquisition), BorderLayout.NORTH);
		final JFileChooser chooser = new JFileChooser();
		dialog.add(chooser, BorderLayout.CENTER);
		if (startDir != null)
			chooser.setCurrentDirectory(startDir);
		chooser.setFileSelectionMode(selection);
		chooser.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent action) {
				if (action.getActionCommand().equals("CancelSelection")) {
					chooser.setSelectedFile(null);
					dialog.setVisible(false);
				}
				if (action.getActionCommand().equals("ApproveSelection")) {
					if (chooser.getSelectedFile() != null) {
						dialog.setVisible(false);
					} else
						JOptionPane.showMessageDialog(dialog, "Select something!", "No selection :(",
								JOptionPane.ERROR_MESSAGE);

				}
			}
		});
		dialog.pack();
		dialog.setVisible(true);
		while (dialog.isVisible()) {
		}
		File toReturn = chooser.getSelectedFile();
		dialog.dispose();
		return toReturn;
	}

	public static void askIfExitApplication(Component in) {
		int quit = JOptionPane.showConfirmDialog(in, "Do you want to quit the application?", "Quit?",
				JOptionPane.YES_NO_OPTION);
		if (quit == JOptionPane.YES_OPTION)
			System.exit(0);
	}

	public static void showError(String error) {
		Dialogs.showError(null, error, "Error!");
	}

	public static void showError(String error, String title) {
		Dialogs.showError(null, error, title);
	}

	public static void showError(Component in, String error, String title) {
		JOptionPane.showMessageDialog(in, error, title, JOptionPane.ERROR_MESSAGE);
	}

	public static void makeSafeToCloseWithCustomAction(final JDialog make, WindowAdapter action) {
		make.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		make.addWindowListener(action);
	}

	public static void makeSafeToClose(final JDialog make) {
		makeSafeToCloseWithCustomAction(make, new WindowAdapter() {
			public void windowClosing(WindowEvent we) {
				Dialogs.askIfExitApplication(make);
			}
		});
	}

	public static void makeSafeToCloseWithCustomAction(final JFrame make, WindowAdapter action) {
		make.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		make.addWindowListener(action);
	}

	public static void makeSafeToClose(final JFrame make) {
		makeSafeToCloseWithCustomAction(make, new WindowAdapter() {
			public void windowClosing(WindowEvent we) {
				Dialogs.askIfExitApplication(make);
			}
		});
	}
}
