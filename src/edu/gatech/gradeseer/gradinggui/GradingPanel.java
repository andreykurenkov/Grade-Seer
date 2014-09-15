package edu.gatech.gradeseer.gradinggui;

import java.awt.BorderLayout;
import java.awt.Color;
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

import edu.gatech.gradeseer.fileio.SmartFile;
import edu.gatech.gradeseer.gradinggui.helpers.ConstraintsHelper;
import edu.gatech.gradeseer.gradingmodel.Configuration;
import edu.gatech.gradeseer.gradingmodel.Student;

//TODO: tons of stuff? (at least indication of save/load)
public class GradingPanel extends JPanel {
	private File saveDir;
	private File loadDir;
	private String saveLoadName;
	private ArrayList<StudentPanel> panels;
	private JButton saveFiles, saveTxt, clearAll, loadFiles, loadTxt;
	private final int NAME_SPACE = 25;

	public GradingPanel(Student[] students) {
		this.setLayout(new BorderLayout());
		JPanel studentsPanel = new JPanel(new GridLayout(students.length, 1));
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridLayout(2, 3));
		saveFiles = new JButton("Save to tsquare files");
		clearAll = new JButton("Clear all");
		loadFiles = new JButton("Load from tsquare files");
		saveTxt = new JButton("Save to single txt file ");
		loadTxt = new JButton("Load from single txt file");

		saveFiles.addActionListener(new SaveListener(false));
		saveTxt.addActionListener(new SaveListener(true));
		loadFiles.addActionListener(new LoadListener(false));
		loadTxt.addActionListener(new LoadListener(true));
		clearAll.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				for (StudentPanel sp : panels) {
					sp.grade.setValue(50);
					sp.comments.setText("");
				}
			}
		});

		buttonPanel.add(saveTxt);
		JLabel title = new JLabel("<-Save/Load Panel->");
		title.setHorizontalAlignment(JLabel.CENTER);
		title.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		buttonPanel.add(title);
		buttonPanel.add(loadTxt);
		buttonPanel.add(saveFiles);
		buttonPanel.add(clearAll);
		buttonPanel.add(loadFiles);
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
		private boolean txt;

		public LoadListener(boolean txt) {
			this.txt = txt;
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			if (loadDir == null) {
				// TODO:ask for dir(currently set same as time as files dir)
			}
			if (saveLoadName == null) {
				// TODO: ask for name(currently set as default Grades.txt)
			}
			load(txt);

		}

	}

	public void load(boolean txt) {
		if (txt) {
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
						fill.gradeModel.setValue(grade == null ? 50 : grade);
						fill.comments.setText(parts.length > 2 ? parts[2] : "");
					}
				}
			} catch (NullPointerException e) {
				JOptionPane.showMessageDialog(this, "No save file for grading is found", "No file",
						JOptionPane.ERROR_MESSAGE);
			} catch (FileNotFoundException e) {
				JOptionPane.showMessageDialog(this, "No save file for grading is found", "No file",
						JOptionPane.ERROR_MESSAGE);
			} catch (@SuppressWarnings("hiding") IOException ex) {
				JOptionPane.showMessageDialog(this, "Could not read file", "Bad file", JOptionPane.ERROR_MESSAGE);
			}
		} else {
			for (int i = 0; i < panels.size(); i++) {
				StudentPanel fill = panels.get(i);

				try {
					Student student = fill.student;
					if (!student.folderKnown()) {
						student.setFolder(student.findFolder(Configuration.getGlobal().getGradingDir()));
					}
					if (fill.student.folderKnown()) {
						SmartFile smartComment = new SmartFile(new File(student.getFolder(), "comments.txt"));
						String content = smartComment.read();
						fill.comments.setText(content.trim());
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	public File getSaveLoadFile() {
		return new File(saveDir.getAbsolutePath(), "." + saveLoadName);
	}

	private class SaveListener implements ActionListener {
		private boolean txt;

		public SaveListener(boolean txt) {
			this.txt = txt;
		}

		public void actionPerformed(ActionEvent arg0) {
			if (saveDir == null) {
				// TODO:ask for dir(currently set same as time as files dir)
			}
			if (saveLoadName == null) {
				// TODO: ask for name(currently set as default Grades.txt)
			}
			File saved = getSaveLoadFile();
			if (saved.exists()) {
				int doSave = JOptionPane.showConfirmDialog(txt ? saveTxt : saveFiles,
						"Are you sure you want to overwrite the current save?", "Save?", JOptionPane.YES_NO_OPTION);
				if (doSave == JOptionPane.YES_OPTION)
					save(txt);
			} else
				save(txt);

		}
	}

	/*
	 * TODO: make this upload to TSquare logic work private class UploadListener implements ActionListener {
	 * 
	 * public void actionPerformed(ActionEvent e) {
	 * 
	 * JPanel panel = new JPanel(new BorderLayout(5, 5));
	 * 
	 * JPanel label = new JPanel(new GridLayout(0, 1, 2, 2)); label.add(new JLabel("GT Username", SwingConstants.RIGHT));
	 * label.add(new JLabel("Password", SwingConstants.RIGHT)); label.add(new JLabel("Section", SwingConstants.RIGHT));
	 * panel.add(label, BorderLayout.WEST);
	 * 
	 * JPanel controls = new JPanel(new GridLayout(0, 1, 2, 2)); JTextField username = new JTextField();
	 * controls.add(username); JPasswordField password = new JPasswordField(); controls.add(password); JComboBox<String>
	 * section = new JComboBox<String>(new String[] { "GR", "A1", "A2", "A3", "A4" }); section.setSelectedIndex(2);
	 * controls.add(section); panel.add(controls, BorderLayout.CENTER);
	 * 
	 * int ans = JOptionPane.showConfirmDialog(upload, panel, "Login", JOptionPane.OK_CANCEL_OPTION);
	 * JOptionPane.showMessageDialog(upload, "Beaming up firefox...\n Make sure to press 'Save Changes' yourself!");
	 * 
	 * String gradingDir = Configuration.getGlobal().getGradingDir().getName(); String[] hwNum = gradingDir.split(" ");
	 * 
	 * if (ans == JOptionPane.OK_OPTION) { // System.out.println(username.getText() + " " + new
	 * String(password.getPassword())); if (hwNum.length > 1) { try { PostUtil pu = new PostUtil(); pu.login(hwNum[1],
	 * username.getText(), new String(password.getPassword()), (String) section.getSelectedItem());
	 * System.out.println("Pre entering foreach loop"); for (StudentPanel sp : panels) { System.out.println(sp.getName() +
	 * " " + (Integer) sp.grade.getValue() + " " + sp.comments.getText()); pu.input(sp.student.getName(), (Integer)
	 * sp.grade.getValue(), sp.comments.getText()); } System.out.println("Post entering foreach loop"); } catch (Exception
	 * ex) { System.out.println(ex.getMessage()); }
	 * 
	 * } else JOptionPane.showMessageDialog(upload, "Please Select a Valid Homework Directory"); }
	 * 
	 * } }
	 */
	public void save(boolean txt) {
		try {
			if (!txt) {
				File gradingDir = Configuration.getGlobal().getGradingDir();
				for (StudentPanel sp : panels) {
					if (!sp.student.folderKnown()) {
						sp.student.setFolder(sp.student.findFolder(gradingDir));
					}
					if (sp.student.folderKnown()) {
						SmartFile smartComment = new SmartFile(new File(sp.student.getFolder(), "comments.txt"));
						smartComment.createNewFile();
						smartComment.append(sp.comments.getText());
					}

					SmartFile gradesFile = new SmartFile(new File(gradingDir, "grades.csv"));
					if (gradesFile.exists()) {
						Scanner scan = new Scanner(gradesFile);
						StringBuilder builder = new StringBuilder();
						while (scan.hasNextLine()) {
							String line = scan.nextLine();
							if (line.contains(sp.student.getID()) || line.contains(sp.student.getName())) {
								line = line.substring(0, line.lastIndexOf(',') + 1);
								line += sp.gradeModel.getNumber().toString();
							}
							builder.append(line + (scan.hasNextLine() ? "\n" : ""));
						}
						gradesFile.createNewFile();
						gradesFile.append(builder.toString());
					}
				}
			} else {
				File saveToParsable = this.getSaveLoadFile();
				File saveToReadable = new File(saveDir.getAbsolutePath() + saveLoadName);
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
				JOptionPane.showMessageDialog(txt ? saveTxt : saveFiles,
						"Succesfully saved ;\nfiles in current grading directory.");
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private class StudentPanel extends JPanel {
		public JLabel name;
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

			gradeModel = new SpinnerNumberModel(50, 0, 100, 1);
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
