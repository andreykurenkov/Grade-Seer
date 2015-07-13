package edu.gatech.gradeseer.gui.core;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.SpinnerNumberModel;

import edu.gatech.gradeseer.gradingmodel.Student;
import edu.gatech.gradeseer.gradingmodel.StudentSet;
import edu.gatech.gradeseer.gui.helpers.ConstraintsHelper;

/**
 * Panel holding text fields and spinners for grading a set of students. Also handles save/load.
 * 
 * @author Anddrey Kurenkov
 * @todo seperate save/load/model logic into appropriate package/class
 */
public class GradingPanel extends JPanel {
	private File saveDir;
	private File loadDir;
	private String saveLoadName;
	private ArrayList<StudentPanel> panels;
	private JButton saveTxt, loadTxt, clearAll;
	private final int NAME_SPACE = 25;

	public GradingPanel(StudentSet studentSet) {
		saveLoadName = " Grades";
		Student[] students = studentSet.getSet();
		this.setLayout(new BorderLayout());
		JPanel studentsPanel = new JPanel(new GridLayout(students.length, 1));
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridLayout(1, 3));
		saveTxt = new JButton("Save");
		clearAll = new JButton("Clear");
		loadTxt = new JButton("Load");

		saveTxt.addActionListener(new SaveListener());
		loadTxt.addActionListener(new LoadListener());

		clearAll.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				for (StudentPanel sp : panels) {
					sp.grade.setValue(100);
					sp.comments.setText("");
				}
			}
		});

		buttonPanel.add(saveTxt);
		buttonPanel.add(clearAll);
		buttonPanel.add(loadTxt);
		this.add(buttonPanel, BorderLayout.NORTH);

		panels = new ArrayList<StudentPanel>(students.length);
		for (int i = 0; i < students.length; i++) {
			StudentPanel sp = new StudentPanel(students[i]);
			panels.add(sp);
			studentsPanel.add(sp);
		}
		this.add(new JScrollPane(studentsPanel), BorderLayout.CENTER);

	}

	public void setSaveDir(File setTo) {
		saveDir = setTo;
	}

	public void setLoadDir(File setTo) {
		loadDir = setTo;
	}

	public void setSaveLoadName(String setTo) {
		saveLoadName = setTo;
	}

	public class LoadListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			if (loadDir == null) {
				// TODO:ask for dir(currently set same as time as files dir)
			}
			if (saveLoadName == null) {
				// TODO: ask for name(currently set as default Grades.txt)
			}
			load();

		}

	}

	public void load() {
		try {
			File reading = getSaveLoadFile();
			if (reading.exists()) {
				Scanner scan = new Scanner(reading);
				scan.useDelimiter("<<>>");
				for (int i = 0; i < panels.size(); i++) {
					StudentPanel fill = panels.get(i);
					String line = scan.next();
					String[] parts = line.split("<>");
					StringBuilder build = new StringBuilder();
					String name = parts[0];
					build.append(name);// For equals spacing
					if (name.length() < NAME_SPACE)
						for (int n = name.length(); n < NAME_SPACE - (i == 0 ? 1 : 0); n++)
							build.append(' ');
					fill.name.setText(build.toString());
					Integer grade = null;
					try {
						grade = Integer.parseInt(parts[1].trim());
					} catch (NumberFormatException ex) {
						// null makes it show up actually centered, unlike the GradingPanel itself being passed
						JOptionPane.showMessageDialog(null, "Error loading file - malformed grade (" + parts[1]
								+ ") not an integer.", "Load Error", JOptionPane.ERROR_MESSAGE);
					}
					fill.gradeModel.setValue(grade == null ? 100 : grade);
					fill.comments.setText(parts.length > 2 ? parts[2] : "");
				}
			} else {
				JOptionPane.showMessageDialog(this, "Could find file " + reading.getAbsolutePath(), "Bad file",
						JOptionPane.ERROR_MESSAGE);
			}
		} catch (NullPointerException e) {
			JOptionPane.showMessageDialog(this, "No save file for grading is found", "No file", JOptionPane.ERROR_MESSAGE);
		} catch (FileNotFoundException e) {
			JOptionPane.showMessageDialog(this, "No save file for grading is found", "No file", JOptionPane.ERROR_MESSAGE);
		} catch (@SuppressWarnings("hiding") IOException ex) {
			JOptionPane.showMessageDialog(this, "Could not read file", "Bad file", JOptionPane.ERROR_MESSAGE);
		}

	}

	public File getSaveLoadFile() {
		return new File(saveDir.getParentFile().getAbsolutePath(), "." + saveLoadName);
	}

	private class SaveListener implements ActionListener {

		public void actionPerformed(ActionEvent arg0) {
			if (saveDir == null) {
				// TODO:ask for dir(currently set same as time as files dir)
			}
			if (saveLoadName == null) {
				// TODO: ask for name(currently set as default Grades.txt)
			}
			File saved = getSaveLoadFile();
			if (saved.exists()) {
				int doSave = JOptionPane.showConfirmDialog(saveTxt, "Are you sure you want to overwrite the current save?",
						"Save?", JOptionPane.YES_NO_OPTION);
				if (doSave == JOptionPane.YES_OPTION)
					save();
			} else
				save();

		}
	}

	public void save() {
		try {
			File saveToParsable = this.getSaveLoadFile();
			File saveToReadable = new File(saveDir.getParentFile(), saveLoadName);
			FileOutputStream out = new FileOutputStream(saveToParsable);
			FileOutputStream outRead = new FileOutputStream(saveToReadable);
			for (StudentPanel sp : panels) {

				out.write(sp.name.getText().trim().getBytes());
				outRead.write((sp.name.getText() + "\n").getBytes());
				out.write("<>".getBytes());
				// " " avoids null values
				out.write((" " + sp.gradeModel.getNumber().toString()).getBytes());
				outRead.write(("Grade: " + sp.gradeModel.getNumber().toString() + "\n").getBytes());
				out.write("<>".getBytes());
				out.write((sp.comments.getText() + "").getBytes());
				outRead.write(("Comment:\n" + sp.comments.getText() + "\n\n").getBytes());
				out.write("<<>>\n".getBytes());

			}
			out.close();
			outRead.close();
			JOptionPane.showMessageDialog(saveTxt, "Succesfully saved ;\nfiles in current grading directory.");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private class StudentPanel extends JPanel {
		public JLabel name;
		@SuppressWarnings("unused")
		public Student student;
		public JSpinner grade;
		public SpinnerNumberModel gradeModel;
		public JTextArea comments;
		public JScrollPane commentScroll;

		public StudentPanel(Student student) {
			this.setLayout(new BorderLayout());
			this.student = student;
			StringBuilder build = new StringBuilder();
			String name = student.getName();
			build.append(name);// For equals spacing
			if (name.length() < NAME_SPACE)
				for (int i = name.length(); i < NAME_SPACE - (i == 0 ? 1 : 0); i++)
					build.append(' ');
			this.name = new JLabel(build.toString());
			Font uniformFont = new Font("Courier", Font.PLAIN, 12);
			if (uniformFont != null) {
				this.name.setFont(uniformFont);
			}

			gradeModel = new SpinnerNumberModel(100, 0, 100, 1);
			this.grade = new JSpinner(gradeModel);
			this.comments = new JTextArea(5, 30);
			comments.setBorder(BorderFactory.createEmptyBorder());
			comments.setEditable(true);
			commentScroll = new JScrollPane(comments);
			commentScroll.setBorder(BorderFactory.createEmptyBorder());
			commentScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			commentScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
			// below is NECESSARY for scrollbars to function
			comments.setLineWrap(true);
			JPanel infoPanel = new JPanel(new GridBagLayout());
			GridBagConstraints constraint = ConstraintsHelper.makeConstraints(0, 0, GridBagConstraints.HORIZONTAL, 1, 1,
					GridBagConstraints.RELATIVE, GridBagConstraints.RELATIVE, GridBagConstraints.WEST);
			infoPanel.add(this.name, constraint);
			constraint = ConstraintsHelper.makeConstraints(0, 1, GridBagConstraints.NONE, 1, 1, GridBagConstraints.RELATIVE,
					GridBagConstraints.REMAINDER, GridBagConstraints.WEST);
			infoPanel.add(grade, constraint);
			constraint = ConstraintsHelper.makeConstraints(1, 0, GridBagConstraints.VERTICAL, 1, 1,
					GridBagConstraints.RELATIVE, GridBagConstraints.REMAINDER, GridBagConstraints.WEST);
			infoPanel.add(new JSeparator(JSeparator.VERTICAL), constraint);
			this.add(infoPanel, BorderLayout.WEST);
			this.add(commentScroll, BorderLayout.CENTER);
			this.add(new JSeparator(JSeparator.HORIZONTAL), BorderLayout.SOUTH);
		}

		public void paint(Graphics g) {
			super.paint(g);
		}
	}
}
