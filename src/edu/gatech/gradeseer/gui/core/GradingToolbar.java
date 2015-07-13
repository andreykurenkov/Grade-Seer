package edu.gatech.gradeseer.gui.core;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;

import org.jpedal.exception.PdfException;

import edu.gatech.gradeseer.fileio.smartfiles.CompilableSmartFile;
import edu.gatech.gradeseer.fileio.smartfiles.CompiledSmartFile;
import edu.gatech.gradeseer.gui.components.CodeTextPane;
import edu.gatech.gradeseer.gui.components.FileGetterButton;
import edu.gatech.gradeseer.gui.components.FileGetterButton.FileChangeListener;
import edu.gatech.gradeseer.gui.components.ProcessTextArea;

public class GradingToolbar extends JToolBar {
	private JButton compile, run, seperateGrading;
	private FileGetterButton openPDF;
	private DisplayTabsPane displayPanes;
	private InteractTabsPane interactTabs;
	private CompilableSmartFile compileFocus;

	public GradingToolbar(final DisplayTabsPane displayPanels, InteractTabsPane interactPanels) {
		this.setFloatable(false);
		displayPanes = displayPanels;
		interactTabs = interactPanels;
		compile = new JButton("Compile");
		compile.setEnabled(false);

		compile.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (compileFocus != null) {
					String result = compileFocus.compile();
					if (result != null) {
						JOptionPane.showMessageDialog(compile, "Did not compile. See IO tab.", "Compilation Error",
								JOptionPane.ERROR_MESSAGE);
						System.err.print(result);
						interactTabs.setSelectedIndex(interactTabs.getTabCount() >= 2 ? 1 : 0);
					} else {
						System.out.println(compileFocus.getName() + " compiled succesfully");
						updateButtonState();

					}
				}
			}
		});
		run = new JButton("Run");

		run.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (compileFocus != null) {
					CompiledSmartFile compiled = compileFocus.checkForCompiledFile();
					if (compiled != null) {
						ProcessBuilder builder = compiled.getRunProcessBuilder();
						ProcessTextArea processPane = new ProcessTextArea(new JTextArea(), builder);
						interactTabs.addProcessTab(processPane, compiled.getName());
					}
				}
			}
		});
		run.setEnabled(false);
		add(compile);
		add(run);

		addSeparator();

		seperateGrading = new JButton("Seperate Grading Panel");
		seperateGrading.addActionListener(new ActionListener() {
			boolean seperated = false;

			@Override
			public void actionPerformed(ActionEvent arg0) {
				interactTabs.toggleGradingSeperation();
				seperated = !seperated;
				seperateGrading.setText(seperated ? "Tab Grading Panel" : "Seperate Grading Panel");
			}

		});
		add(seperateGrading);
		addSeparator();
		openPDF = new FileGetterButton("Open Grading Guide");
		openPDF.setFileFilter(new FileFilter() {

			@Override
			public boolean accept(File file) {
				return !file.getName().contains(".") || file.getName().endsWith(".pdf");
			}

			@Override
			public String getDescription() {
				return "PDF Files";
			}

		});
		openPDF.addFileChangeListener(new FileChangeListener() {

			@Override
			public void onFileChange(File newFile) {
				try {
					displayPanes.addPDF(newFile);
				} catch (PdfException e) {
					e.printStackTrace();
				}
			}
		});
		add(openPDF);
		displayPanes.addChangeListener(new DisplayChangeListener());

	}

	protected void updateButtonState() {
		Component newTabComponent = displayPanes.getSelectedComponent();
		run.setEnabled(false);
		if (newTabComponent instanceof JScrollPane) {
			newTabComponent = ((JScrollPane) newTabComponent).getViewport().getView();
		}
		if (newTabComponent instanceof CodeTextPane) {
			CodeTextPane codeText = (CodeTextPane) newTabComponent;
			if (codeText.getCodeFile() != null && codeText.getOriginFile().isJavaFile()) {
				compileFocus = codeText.getCodeFile();
				compile.setEnabled(true);
				if (compileFocus.isCompiled() || compileFocus.checkForCompiledFile() != null) {
					if (compileFocus.getLastCompilation() == null)
						compileFocus.checkForCompiledFile();
					if (compileFocus.getLastCompilation() != null)
						run.setEnabled(compileFocus.getLastCompilation().isRunnable());
				}
			}
		}
	}

	private class DisplayChangeListener implements ChangeListener {
		@Override
		public void stateChanged(ChangeEvent e) {
			updateButtonState();
		}
	}
}
