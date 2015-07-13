package edu.gatech.gradeseer.gui.core;

import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;

import org.jpedal.exception.PdfException;

import edu.gatech.gradeseer.fileio.SmartFile;
import edu.gatech.gradeseer.fileio.smartfiles.JavaSmartFile;
import edu.gatech.gradeseer.gui.components.CodeTextPane;
import edu.gatech.gradeseer.gui.components.JColorableTextPane;
import edu.gatech.gradeseer.gui.components.PDFViewer;
import edu.gatech.gradeseer.gui.components.TabCloseComponent;

public class DisplayTabsPane extends JTabbedPane {
	private JColorableTextPane currentPane;

	public DisplayTabsPane() {
		this.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		currentPane = new JColorableTextPane();
		addTab("Current", new JScrollPane(currentPane));
	}

	public void setCurrent(JComponent current) {
		setComponentAt(0, current);
		setSelectedIndex(0);
	}

	public void addTextPane(SmartFile file) {
		if (file != null && file.isReadable()) {
			boolean fine = true;
			JTextPane textPane = new JTextPane();
			if (file.isJavaFile()) {
				try {
					// TODO hacky
					textPane = CodeTextPane.getJavaTextPane(new JavaSmartFile(file));
				} catch (IOException e) {
					e.printStackTrace();
					fine = false;
				}
			} else {
				textPane.setText(file.read());
			}
			if (fine) {
				textPane.setName(file.getName());
				textPane.setCaretPosition(0);
				JScrollPane pane = new JScrollPane(textPane);
				JScrollBar vertical = pane.getVerticalScrollBar();
				InputMap im = vertical.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
				im.put(KeyStroke.getKeyStroke((char) KeyEvent.VK_DOWN), "positiveUnitIncrement");
				im.put(KeyStroke.getKeyStroke((char) KeyEvent.VK_UP), "negativeUnitIncrement");
				addCloseableTab(textPane.getName(), pane);
			}
		}
	}

	public void addCloseableTab(String name, JComponent component) {
		addTab(name, component);
		setSelectedComponent(component);
		setTabComponentAt(getTabCount() - 1, new TabCloseComponent(DisplayTabsPane.this));
	}

	public void addPDF(File file) throws PdfException {
		if (file != null && file.getName().endsWith(".pdf")) {
			addCloseableTab(file.getName(), new PDFViewer(file));
		}
	}

	public void addTextPanes(ArrayList<SmartFile> files) {
		for (SmartFile file : files)
			addTextPane(file);
	}
}
