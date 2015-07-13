package edu.gatech.gradeseer.gui.helpers;

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
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;

import edu.gatech.gradeseer.gradingmodel.Configuration;
import edu.gatech.gradeseer.gradingmodel.Student;
import edu.gatech.gradeseer.gradingmodel.StudentSet;

/**
 * Monostate sort of class with lots of helper dialog methods for new users.
 * 
 * @author Andrey K.
 * 
 */
public class NewUserDisplay {
	// TODO: put string constants into some sort of constants file? Maaaybe.
	private static final String newPersonMessage = "Hello! You, hopefully, are a CS1331 TA. This is a thing made by a random 1331 TA for HW grading.\n"
			+ "I got annoyed by the need to change directories and compile and run stuff manually, so I just wrote this!\n"
			+ "I liked it so much, I decided to share. Hopefully it wont break. And will be useful.\n\n";
	private static final String totallyNewPersonMessage = "To use: You need to set the current directory to be an extracted version of one of the TSquare homework folders. \n"
			+ "This is after you set up your students, which will happen right after this. Other stuff is hopefully pretty clear.\n\n"
			+ "So yeah, hopefully this will make grading less of a pain. Have fun!\n" + "-Random 1331 TA";
	private static final String getStudentsMessage = "Hello again! Now you need to set up your students.\n"
			+ "Go to Tsquare->CS-1331->Section Info->Student Membership and sort by Banner Course Section.\n";
	private static final String studentsPaste = "Then simply copy and paste (cntrl v)all the text of you grading section(names, ids, etc. - all rows & columns).";
	private static final String setBaseDir = "Now to set the base directory.\n"
			+ "This will simply make the program always start in this directory.";

	private NewUserDisplay() {
	}

	public static File introduceAndGetFiles(Dimension dim, Configuration config) {
		JFrame stuffFrame = new JFrame("Intro stuff!");
		stuffFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		stuffFrame.setPreferredSize(dim);// TODO: figure out if this should be visible
		showIntro(stuffFrame, true);
		getStudentsFile(stuffFrame, config);
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

	public static void showIntro(Component in, boolean totallyNew) {
		if (totallyNew) {
			Object[] params = { newPersonMessage, totallyNewPersonMessage };
			JOptionPane.showMessageDialog(in, params, "New Person Message :)", JOptionPane.INFORMATION_MESSAGE);
		} else
			JOptionPane.showMessageDialog(in, newPersonMessage + "\n-Random 1331 TA guy", "New Person Message :)",
					JOptionPane.INFORMATION_MESSAGE);

	}

	public static void getStudentsFile(final Window in, final Configuration config) {
		final JTextArea pasteArea = new JTextArea(25, 50);
		StudentSet current = config.getStudentSet();
		if (current != null) {
			StringBuilder builder = new StringBuilder();
			Student[] students = current.getSet();
			for (int i = 0; i < students.length; i++) {
				builder.append(students[i].getName());
				builder.append('\n');
			}
			pasteArea.setText(builder.toString());
		}
		JScrollPane scroll = new JScrollPane(pasteArea);

		JButton sample = new JButton("See sample input");
		Object[] params = { getStudentsMessage + studentsPaste, sample, scroll };
		sample.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				JOptionPane.showMessageDialog(in, "Sample input is:\n" + "Last1, First1 <any other stuff>\n"
						+ "Last2, First2 <any other stuff>\n" + "Last3, First3   <any other stuff>\n"
						+ "Last4, First4   <any other stuff>\n" + "Last5, First5        <any other stuff>\n" + ".\n" + ".\n"
						+ ".\n"
						+ "The \", \" need to be exactly that, but anything after First name can be anything otherwise.\n",
						"Input Format", JOptionPane.INFORMATION_MESSAGE);

			}

		});
		final JOptionPane pane = new JOptionPane(params, JOptionPane.QUESTION_MESSAGE);

		final JDialog dialog = new JDialog(in, "Set up students", Dialog.ModalityType.APPLICATION_MODAL);

		/**
		 * tableOption.addActionListener(new ActionListener() {
		 * 
		 * @Override public void actionPerformed(ActionEvent arg0) { dialog.setVisible(false); set =
		 *           getStudentsFileWithTable(in, current); } });
		 */
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
						StudentSet set = StudentSet.parseFromString(pasteArea.getText());
						if (set == null) {
							error = "Incorrect format. Please retrieve the student list from tsquare as requested or input manually.";
						}
						if (error == null) {
							config.setStudentSet(set);
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

	public static StudentSet getStudentsFileWithTable(final Window in, final StudentSet current) {
		StudentSet set = null;
		int temp = Dialogs.getIntSafely(in, "Student count", "How many students do you have?", 1, 50);
		if (temp == Dialogs.CANCEL_INT)
			return null;
		while (temp == -1) {
			Dialogs.askIfExitApplication(in);
			temp = Dialogs.getIntSafely(in, "Student count", "How many students do you have?", 1, 50);
		}
		final int numStudents = temp;
		Object[] columnNames = { "First Name", "Last Name", "gtname(bperson3)" };
		Object[][] input = new Object[numStudents][2];
		if (current != null) {
			Student[] students = current.getSet();
			int num = Math.min(numStudents, students.length);
			for (int i = 0; i < num; i++) {
				String name = students[i].getName();
				String[] nameParts = name.split(", ");
				input[i][0] = nameParts[1];
				input[i][1] = nameParts[0];
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

		dialog.dispose();
		return set;
	}

}
