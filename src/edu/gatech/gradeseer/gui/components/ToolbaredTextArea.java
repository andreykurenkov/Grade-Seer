package edu.gatech.gradeseer.gui.components;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;

public class ToolbaredTextArea extends JPanel {
	protected JTextArea text;
	protected JToolBar toolbar;

	public ToolbaredTextArea(JTextArea area) {
		this.text = area;
		toolbar = new JToolBar();
		toolbar.setFloatable(true);
		this.setLayout(new BorderLayout());
		JScrollPane pane = new JScrollPane(text);
		this.add(pane, BorderLayout.CENTER);
		this.add(toolbar, BorderLayout.NORTH);
		JButton clearAll = new JButton("Clear");
		clearAll.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				text.setText("");
			}
		});
		toolbar.add(clearAll);
	}

	public void addToolbarComponent(Component toAdd) {
		toolbar.add(toAdd);
	}

	public void removeToolbarComponent(Component remove) {
		toolbar.remove(remove);
	}

	public JTextArea getTextArea() {
		return text;
	}
}
