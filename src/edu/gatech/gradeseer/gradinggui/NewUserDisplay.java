package edu.gatech.gradeseer.gradinggui;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;

import edu.gatech.gradeseer.gradinggui.helpers.Dialogs;
import edu.gatech.gradeseer.gradingmodel.Configuration;
import edu.gatech.gradeseer.gradingmodel.Student;
import edu.gatech.gradeseer.gradingmodel.StudentSet;

/**
 * Monostate sort of intro thing
 * 
 * @author Andrey K.
 * 
 */
public class NewUserDisplay {
	private final static NewUserDisplay instance = new NewUserDisplay();
	// TODO: put string constants into some sort of constants file? Maaaybe.
	private static final String newPersonMessage = "Hello! You, hopefully, are a CS1331 TA. This is a thing made by a random 1331 TA for HW grading.\n"
			+ "I got annoyed by the need to change directories and compile and run stuff manually, so I just wrote this!\n"
			+ "I liked it so much, I decided to share. Hopefully it wont break. And will be useful.\n\n";
	private static final String totallyNewPersonMessage = "To use: You need to set the current directory to be an extracted version of one of the TSquare homework folders. \n"
			+ "This is after you set up your students, which will happen right after this. Other stuff is hopefully pretty clear.\n\n"
			+ "So yeah, hopefully this will make grading less of a pain. Have fun!\n" + "-Random 1331 TA";
	private static final String getStudentsMessage = "Hello again! Now you need to set up your students.\n"
			+ "Go on TSquare and get the names and gtids of the members of your grading section.\n";
	private static final String studentsPaste = "Then simply copy and paste all the text of you grading section(names, ids, etc. - all rows & columns).\n"
			+ "Alternatively you can opt to enter the students in manually by pressing the button below.";
	private static final String studentsTable = "Then simply enter each name, as on TSquare,\n in the first and second columns and the corresponding tsquare id in the third.";
	private static final String setBaseDir = "Now to set the base directory.\n"
			+ "This will simply make the program always start in this directory.";// TODO: make newline actually show up

	private NewUserDisplay() {
	}

	public static File introduceAndGetFiles(Dimension dim, Configuration config) {
		JFrame stuffFrame = new JFrame("Intro stuff!");
		stuffFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		stuffFrame.setPreferredSize(dim);// TODO: figure out if this should be visible
		introduce(stuffFrame, true, true, config);
		getStudentsFile(stuffFrame, null, config);
		File toReturn = getDirFile(stuffFrame);
		while (toReturn == null) {
			Dialogs.askIfExitApplication(stuffFrame);
			toReturn = getDirFile(stuffFrame);
		}
		stuffFrame.dispose();
		return toReturn;
	}

	public static File getDirFile(Window in, File gradingDir) {
		return Dialogs.getFile(in, "Set base directory", "<html>" + setBaseDir + "</html>", gradingDir,
				JFileChooser.DIRECTORIES_ONLY);
	}

	public static File getDirFile(Window in) {
		return getDirFile(in, null);
	}

	public static void introduce(Component in, boolean checkBox, boolean newGuy, Configuration modify) {
		boolean dontShow = showIntro(in, checkBox, newGuy);
		if (newGuy)
			modify.setShowIntro(!dontShow);
	}

	private static boolean showIntro(Component in, boolean checkBox, boolean totallyNew) {
		JCheckBox checkbox = new JCheckBox("Do not show this message again?");
		if (checkBox && totallyNew) {
			Object[] params = { newPersonMessage, totallyNewPersonMessage, checkbox };
			JOptionPane.showMessageDialog(in, params, "New Person Message :)", JOptionPane.INFORMATION_MESSAGE);
		} else if (checkBox) {
			Object[] params = { newPersonMessage + "-Random 1331 TA guy", checkbox };
			JOptionPane.showMessageDialog(in, params, "New Person Message :)", JOptionPane.INFORMATION_MESSAGE);

		} else
			JOptionPane.showMessageDialog(in, newPersonMessage + "\n-Random 1331 TA guy", "New Person Message :)",
					JOptionPane.INFORMATION_MESSAGE);

		return checkbox.isSelected();
	}

	public static void getStudentsFile(final Window in, final StudentSet current, final Configuration modify) {
		final JTextArea pasteArea = new JTextArea(25, 50);
		if (current != null) {
			StringBuilder builder = new StringBuilder();
			Student[] students = current.getSet();
			for (int i = 0; i < students.length; i++) {
				builder.append(students[i].getName());
				builder.append("\t");
				builder.append(students[i].getID());
				builder.append('\n');
			}
			pasteArea.setText(builder.toString());
		}
		JScrollPane scroll = new JScrollPane(pasteArea);
		JButton tableOption = new JButton("Manually input students");

		JButton sample = new JButton("See sample input");
		Object[] params = { getStudentsMessage + studentsPaste, tableOption, sample, scroll };
		sample.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				JOptionPane.showMessageDialog(in, "Sample input is:\n" + "Last1, First1       id1 <any other stuff>\n"
						+ "Last2, First2        id2  <any other stuff>\n" + "Last3, First3  id3  <any other stuff>\n"
						+ "Last4, First4  id4  <any other stuff>\n" + "Last5, First5       id5  <any other stuff>\n" + ".\n"
						+ ".\n" + ".\n" + "The \", \" need to be exactly that, but whitespace can be anything otherwise.\n",
						"Input Format", JOptionPane.INFORMATION_MESSAGE);

			}

		});
		final JOptionPane pane = new JOptionPane(params, JOptionPane.QUESTION_MESSAGE);

		final JDialog dialog = new JDialog(in, "Set up students", Dialog.ModalityType.APPLICATION_MODAL);

		tableOption.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				dialog.setVisible(false);
				boolean success = getStudentsFileWithTable(in, current, modify);
				if (success)
					dialog.setEnabled(false);
				else
					dialog.setVisible(true);
			}
		});
		dialog.setContentPane(pane);
		Dialogs.makeSafeToClose(dialog);
		pane.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent e) {
				String prop = e.getPropertyName();
				/* Weird hack to keep this working for multiple presses of OK */
				if (!pane.getValue().equals(JOptionPane.NO_OPTION))
					if (dialog.isVisible() && (e.getSource() == pane) && (prop.equals(JOptionPane.VALUE_PROPERTY))) {
						String error = null;
						String content = pasteArea.getText();
						if (content.length() == 0)
							error = "Must write list of students - cannot leave empty";
						else if (!content.contains("\n"))
							error = "Must contain newlines";
						else if (!content.contains(", "))
							error = "Each name must precisely have ', ' in it";
						// TODO: various forms of error checking - especially REGEX
						if (error == null) {
							modify.setStudentSet(StudentSet.parseFromString(pasteArea.getText()));
							dialog.setVisible(false);
							dialog.setEnabled(false);
						} else {
							pane.setValue(JOptionPane.NO_OPTION);
							JOptionPane.showMessageDialog(in, error, "Error", JOptionPane.ERROR_MESSAGE);
						}
					}
			}
		});
		dialog.pack();
		dialog.setVisible(true);

		while (dialog.isEnabled()) {
		}

		dialog.dispose();
	}

	public static boolean getStudentsFileWithTable(final Window in, final StudentSet current, final Configuration modify) {
		int temp = Dialogs.getIntSafely(in, "Student count", "How many students do you have?", 1, 50);
		if (temp == Dialogs.CANCEL_INT)
			return false;
		while (temp == -1) {
			Dialogs.askIfExitApplication(in);
			temp = Dialogs.getIntSafely(in, "Student count", "How many students do you have?", 1, 50);
		}
		final int numStudents = temp;
		Object[] columnNames = { "First Name", "Last Name", "gtname(bperson3)" };
		Object[][] input = new Object[numStudents][3];
		if (current != null) {
			Student[] students = current.getSet();
			int num = Math.min(numStudents, students.length);
			for (int i = 0; i < num; i++) {
				String name = students[i].getName();
				String[] nameParts = name.split(", ");
				input[i][0] = nameParts[1];
				input[i][1] = nameParts[0];
				input[i][2] = students[i].getID();
			}
		}
		final JTable studentsTable = new JTable(input, columnNames);
		JScrollPane scroll = new JScrollPane(studentsTable);
		studentsTable.setFillsViewportHeight(true);

		Object[] params = { getStudentsMessage, scroll };
		final JOptionPane pane = new JOptionPane(params, JOptionPane.QUESTION_MESSAGE, JOptionPane.OK_CANCEL_OPTION);

		final JDialog dialog = new JDialog(in, "Set up students", Dialog.ModalityType.APPLICATION_MODAL);
		// TODO: allow to cancel and go back to pasting
		dialog.setContentPane(pane);
		Dialogs.makeSafeToClose(dialog);

		pane.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent e) {
				String prop = e.getPropertyName();
				/* Weird hack to keep this working for multiple presses of OK */

				if (!pane.getValue().equals(JOptionPane.NO_OPTION)) {
					if (dialog.isVisible() && (e.getSource() == pane) && (prop.equals(JOptionPane.VALUE_PROPERTY))) {
						if (pane.getValue().equals(JOptionPane.CANCEL_OPTION)) {
							dialog.setEnabled(false);
							dialog.setVisible(false);
						} else {
							if (studentsTable.getCellEditor() != null)
								studentsTable.getCellEditor().stopCellEditing();
							String error = null;
							String[][] out = new String[3][numStudents];
							for (int col = 0; col < 3; col++) {
								for (int row = 0; row < numStudents; row++) {
									out[col][row] = (String) studentsTable.getModel().getValueAt(row, col);
									// TODO: again, regex
									if (out[col][row] == null || out[col][row].trim().length() == 0)
										error = "Cannot leave cells empty";
								}
							}
							if (error == null) {
								modify.setStudentSet(new StudentSet(out));
								dialog.setEnabled(false);
								dialog.setVisible(false);
							} else {
								pane.setValue(JOptionPane.NO_OPTION);
								JOptionPane.showMessageDialog(in, error, "Error", JOptionPane.ERROR_MESSAGE);
							}
						}
					}
				}
			}
		});
		dialog.pack();
		dialog.setVisible(true);
		while (dialog.isEnabled() && dialog.isVisible()) {
		}

		boolean toReturn = pane.getValue().equals(JOptionPane.OK_OPTION);
		dialog.dispose();
		return toReturn;
	}

}
