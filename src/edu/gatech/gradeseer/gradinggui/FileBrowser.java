package edu.gatech.gradeseer.gradinggui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import edu.gatech.gradeseer.fileio.ConfigManager;
import edu.gatech.gradeseer.fileio.SmartFile;
import edu.gatech.gradeseer.gradinggui.components.BackResetNextButtons;
import edu.gatech.gradeseer.gradinggui.components.CodeTextPane;
import edu.gatech.gradeseer.gradinggui.components.ProcessTextArea;
import edu.gatech.gradeseer.gradinggui.components.TabCloseComponent;
import edu.gatech.gradeseer.gradinggui.helpers.ConstraintsHelper;
import edu.gatech.gradeseer.gradinggui.helpers.Dialogs;
import edu.gatech.gradeseer.gradingmodel.Configuration;
import edu.gatech.gradeseer.gradingmodel.Student;

public class FileBrowser extends JPanel {
	// TODO: getting a bit huge. Maybe split-upable? ALso figoure out how
	// weights are actually supposed to be used?
	private Student selectedStudent;
	private final String exten = "Submission attachment(s)";
	private JList dirChooser, fileChooser;// not generic to support Java 1.6
	private File currentDir;
	private File baseDir;
	private JButton view, compile, run, viewAll, compileAll, runAll, tabClose;
	private ArrayList<SmartFile> currentFiles;
	private JTabbedPane filePane;
	private final double weightDominant = 10;
	private final double weightDemanded = 0.1;
	private final double weightNormal = 1;
	public static final int[] mnemonics = { KeyEvent.VK_1, KeyEvent.VK_2, KeyEvent.VK_3, KeyEvent.VK_4, KeyEvent.VK_5,
			KeyEvent.VK_6, KeyEvent.VK_7, KeyEvent.VK_8, KeyEvent.VK_9, KeyEvent.VK_0 };

	public FileBrowser(Dimension dim, final JTabbedPane fileDisplay, final Configuration config) {
		filePane = fileDisplay;
		currentFiles = new ArrayList<SmartFile>();
		fileChooser = new JList();
		fileChooser.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		fileChooser.setLayoutOrientation(JList.VERTICAL);

		dirChooser = new JList();
		dirChooser.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		dirChooser.setLayoutOrientation(JList.VERTICAL);
		dirChooser.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				if (dirChooser.getSelectedIndex() == -1) {
					currentDir = null;
					fileChooser.setListData(new File[0]);
				} else {
					FileBrowser.this.updateFiles();
				}
			}
		});

		fileChooser.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				currentFiles = new ArrayList<SmartFile>();
				Object[] files = fileChooser.getSelectedValues();
				boolean readable, compilable, runnable;
				readable = compilable = runnable = files.length > 0;
				for (Object o : files) {
					SmartFile cur = (SmartFile) o;
					currentFiles.add(cur);
					if (!cur.isReadable()) {
						readable = false;
					}
					if (!cur.isRunnable() && !cur.isHtml()) {
						runnable = false;
					}
					if (!cur.isCompilable()) {
						compilable = false;
					}

				}
				view.setEnabled(readable);
				compile.setEnabled(compilable);
				run.setEnabled(runnable);
			}

		});
		JScrollPane dirChooserScroller = new JScrollPane(dirChooser);
		JScrollPane fileChooserScroller = new JScrollPane(fileChooser);

		dirChooserScroller.setPreferredSize(new Dimension((int) dim.getWidth() / 2, (int) dim.getHeight()));
		fileChooserScroller.setPreferredSize(new Dimension((int) dim.getWidth() / 2, (int) dim.getHeight()));
		this.setLayout(new GridBagLayout());
		GridBagConstraints c = ConstraintsHelper.makeConstraints(0, 0, GridBagConstraints.BOTH, weightDominant,
				weightDominant, GridBagConstraints.RELATIVE, GridBagConstraints.RELATIVE, GridBagConstraints.WEST);
		this.add(dirChooserScroller, c);
		c = ConstraintsHelper.makeConstraints(1, 0, GridBagConstraints.BOTH, weightNormal, weightDemanded,
				GridBagConstraints.RELATIVE, GridBagConstraints.REMAINDER, GridBagConstraints.WEST);
		JSeparator sepv = new JSeparator(JSeparator.VERTICAL);
		sepv.setVisible(true);
		sepv.setEnabled(true);
		this.add(sepv, c);
		c = ConstraintsHelper.makeConstraints(0, 1, GridBagConstraints.BOTH, weightNormal, weightDemanded,
				GridBagConstraints.RELATIVE, GridBagConstraints.RELATIVE, GridBagConstraints.WEST);
		JSeparator seph = new JSeparator(JSeparator.HORIZONTAL);
		seph.setVisible(true);
		seph.setEnabled(true);
		this.add(seph, c);
		c = ConstraintsHelper.makeConstraints(0, 2, GridBagConstraints.BOTH, weightNormal, weightDemanded,
				GridBagConstraints.RELATIVE, GridBagConstraints.REMAINDER, GridBagConstraints.WEST);
		BackResetNextButtons brn = new BackResetNextButtons(true, true);
		brn.setResetText("configure");
		brn.addResetActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				NewUserDisplay.getStudentsFile(null, config.getStudentSet(), config);
				ConfigManager.getInstance().save(config);
				FileBrowser.this.setStudents(config.getStudentSet().getSet());
			}

		});

		brn.addNextActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				dirChooser.setSelectedIndex((dirChooser.getSelectedIndex() + 1) % (dirChooser.getLastVisibleIndex() + 1));
			}
		});
		brn.addBackActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				dirChooser.setSelectedIndex((Math.abs(dirChooser.getSelectedIndex() - 1)));
			}
		});

		this.add(brn, c);

		c = ConstraintsHelper.makeConstraints(2, 0, GridBagConstraints.BOTH, weightDominant, weightDominant,
				GridBagConstraints.REMAINDER, GridBagConstraints.RELATIVE, GridBagConstraints.WEST);
		this.add(fileChooserScroller, c);
		this.add(new JSeparator(),
				ConstraintsHelper.makeConstraints(2, 1, GridBagConstraints.HORIZONTAL, weightDemanded, weightDemanded));
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridLayout(3, 2));
		buttonPanel.setPreferredSize(new Dimension((int) dim.getWidth() / 4, (int) dim.getHeight() / 6));
		view = new JButton("Read");
		viewAll = new JButton("Read All");
		view.addActionListener(new ViewListener(false));
		viewAll.addActionListener(new ViewListener(true));
		buttonPanel.add(view);
		buttonPanel.add(viewAll);

		compile = new JButton("Compile");
		compileAll = new JButton("Compile All");
		compile.addActionListener(new CompileListener(false));
		compileAll.addActionListener(new CompileListener(true));
		buttonPanel.add(compile);
		buttonPanel.add(compileAll);

		run = new JButton("Run");
		runAll = new JButton("Run All");
		run.addActionListener(new RunListener(false));
		runAll.addActionListener(new RunListener(true));
		buttonPanel.add(run);
		buttonPanel.add(runAll);

		c = ConstraintsHelper.makeConstraints(2, 2, GridBagConstraints.BOTH, weightDominant, weightDemanded,
				GridBagConstraints.REMAINDER, GridBagConstraints.REMAINDER, GridBagConstraints.SOUTH);
		this.add(buttonPanel, c);
	}

	public void addFileSelectionListener(ListSelectionListener listener) {
		this.fileChooser.addListSelectionListener(listener);

	}

	public void addFolderSelectionListener(ListSelectionListener listener) {
		this.dirChooser.addListSelectionListener(listener);
	}

	public void setGradingDir(File set) {
		baseDir = set;
	}

	public File getBaseDir() {
		return baseDir;
	}

	public void setStudents(Student[] setTo) {
		dirChooser.setListData(setTo);
	}

	public SmartFile[] getCurrentFiles() {
		return currentFiles.toArray(new SmartFile[currentFiles.size()]);
	}

	private void updateFiles() {
		selectedStudent = (Student) dirChooser.getSelectedValue();
		File folder = selectedStudent.findFolder(baseDir);

		if (folder == null) {
			JOptionPane.showMessageDialog(this, "No folder for student " + selectedStudent + "\nIn grading folder "
					+ baseDir.getAbsolutePath() + "\nGrading folder must be unzipped HW folder.", "Error Message",
					JOptionPane.ERROR_MESSAGE);
		} else {
			currentDir = new File(folder.getAbsolutePath() + "/" + exten);
			if (!currentDir.exists()) {
				currentDir = null;
			}
			if (currentDir == null || currentDir.listFiles() == null)
				JOptionPane
						.showMessageDialog(
								this,
								"Not a valid directory!\nCurrent dir must be unzipped HW folder."
										+ "\n(Choose folder such as Homework 1,\nwhich is unzipped from a bulk download\n and which has student folders inside it).",
								"Directory error", JOptionPane.ERROR_MESSAGE);
			else {
				SmartFile[] smart = SmartFile.makeSmarter(currentDir.listFiles());
				Arrays.sort(smart);
				fileChooser.setListData(SmartFile.makeSmarter(smart));
			}
		}
	}

	public JButton getTabCloseButton() {
		if (tabClose == null) {
			tabClose = new JButton("Close tabs");
			tabClose.setVisible(false);
			tabClose.setEnabled(false);
			tabClose.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					int tabCount = filePane.getTabCount();
					for (int i = tabCount - 1; i >= 0; i--) {
						if (filePane.getTabComponentAt(i) instanceof TabCloseComponent)// TODO: make more robust
							filePane.removeTabAt(i);
					}
					tabClose.setVisible(false);
					tabClose.setEnabled(false);
				}

			});
		}
		return tabClose;

	}

	private class CompileListener implements ActionListener {
		private boolean all;

		public CompileListener(boolean all) {
			this.all = all;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			ArrayList<SmartFile> toCompile = currentFiles;
			if (all) {
				ArrayList<SmartFile> files = new ArrayList<SmartFile>();
				for (File f : currentDir.listFiles()) {
					SmartFile sm = new SmartFile(f);
					if (sm.isCompilable()) {
						boolean compiled = (sm.compile(new OutputStreamWriter(System.err)) == null);
						if (!compiled) {
							JOptionPane.showMessageDialog(compile, sm.getName() + " Did not compile - see IO tab.",
									"Compilation Error", JOptionPane.ERROR_MESSAGE);
						}
					}

				}
				toCompile = files;
			} else {
				SmartFile sm = (SmartFile) fileChooser.getSelectedValue();
				if (sm.isCompilable()) {
					boolean compiled = (sm.compile(new OutputStreamWriter(System.err)) == null);
					if (!compiled) {
						JOptionPane.showMessageDialog(compile, sm.getName() + " Did not compile - see IO tab.",
								"Compilation Error", JOptionPane.ERROR_MESSAGE);
					}
				}
			}
			FileBrowser.this.updateFiles();
		}
	}

	private class ViewListener implements ActionListener {
		private boolean all;

		public ViewListener(boolean all) {
			this.all = all;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			ArrayList<SmartFile> toView = currentFiles;
			if (all) {
				ArrayList<SmartFile> files = new ArrayList<SmartFile>();
				for (File f : currentDir.listFiles()) {
					SmartFile sm = new SmartFile(f);
					if (sm.isReadable())
						files.add(sm);
				}
				toView = files;
			}
			int i = filePane.getTabCount();
			if (toView.size() > 0) {
				tabClose.setEnabled(true);
				tabClose.setVisible(true);
			}
			for (SmartFile file : toView) {
				String contents = file.read();
				if (contents != null) {
					CodeTextPane text = new CodeTextPane(contents);
					text.setName(file.getName());
					text.setCaretPosition(0);
					JScrollPane pane = new JScrollPane(text);
					JScrollBar vertical = pane.getVerticalScrollBar();
					InputMap im = vertical.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
					im.put(KeyStroke.getKeyStroke((char) KeyEvent.VK_DOWN), "positiveUnitIncrement");
					im.put(KeyStroke.getKeyStroke((char) KeyEvent.VK_UP), "negativeUnitIncrement");
					filePane.addTab(text.getName(), pane);
					filePane.setSelectedComponent(pane);
					int tabCount = filePane.getTabCount();
					if (tabCount <= 10) {
						filePane.setMnemonicAt(tabCount - 1, mnemonics[tabCount - 1]);
					}
				}
			}
			for (; i < filePane.getTabCount(); i++) {
				filePane.setTabComponentAt(i, new TabCloseComponent(filePane));
			}

		}
	}

	private class RunListener implements ActionListener {
		private boolean all;

		public RunListener(boolean all) {
			this.all = all;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			ArrayList<SmartFile> toRun = currentFiles;
			if (all) {
				ArrayList<SmartFile> files = new ArrayList<SmartFile>();
				for (File f : currentDir.listFiles()) {
					SmartFile sm = new SmartFile(f);
					if (sm.isRunnable())
						files.add(sm);
					if (sm.isHtml())
						files.add(sm);
				}
				toRun = files;
			}

			for (final SmartFile file : toRun) {
				final Process p;
				if (file.isHtml())
					p = file.runWithAppletViewer();
				else
					p = file.run();
				if (p == null) {
					Dialogs.showError("Could not run file " + file.getNonExtensionName() + "!");
				} else {
					final JTextArea area = new JTextArea();

					Dimension current = FileBrowser.this.getSize();
					final JFrame frame = new JFrame(dirChooser.getSelectedValue() + " - " + file.getNonExtensionName());
					frame.setPreferredSize(new Dimension(current.width * 2, current.height / 2));
					final ProcessTextArea display = new ProcessTextArea(area, p, true);
					Dialogs.makeSafeToCloseWithCustomAction(frame, display.new SafeCloseAdapter());
					Dialogs.makeSafeToCloseWithCustomAction(frame, new WindowAdapter() {
						public void windowClosing(WindowEvent we) {
							frame.dispose();
						}
					});
					frame.add(display);
					final JButton runAgain = new JButton("Rerun");
					// TODO: so painfully bad. Should get processbuilder from file .whatevs
					runAgain.addActionListener(new ActionListener() {
						private ProcessTextArea currentDisplay;

						@Override
						public void actionPerformed(ActionEvent arg0) {
							Process rerun = null;
							if (file.isHtml())
								rerun = file.runWithAppletViewer();
							else
								rerun = file.run();

							if (!display.processFinished())
								p.destroy();
							if (currentDisplay == null) {
								frame.remove(display);
								display.closeProcess();

							} else {
								frame.remove(currentDisplay);
								currentDisplay.closeProcess();
							}
							area.append("\n");
							currentDisplay = new ProcessTextArea(area, rerun, true);
							currentDisplay.addToolbarComponent(runAgain);
							Dialogs.makeSafeToCloseWithCustomAction(frame, currentDisplay.new SafeCloseAdapter());
							frame.add(currentDisplay);
							frame.pack();
							frame.repaint();
						}

					});
					display.addToolbarComponent(runAgain);
					frame.pack();
					frame.setVisible(true);
				}
			}
		}
	}

	public File getCurrentDir() {
		return currentDir;
	}

	public Student getStudent() {
		return selectedStudent;
	}
}
