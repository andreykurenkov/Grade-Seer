package edu.gatech.gradeseer.gradinggui.components;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

/**
 * This class is meant to display text and take input for a Java process in a JTextArea.
 * 
 * @author Andrey Kurenkov
 * @version 1.0
 */
public class ProcessTextArea extends ToolbaredTextArea {
	private Process myProcess;
	private boolean finished;
	private JButton close;

	/**
	 * Contructor that sets up the process area.
	 * 
	 * @param area
	 *            this is the JtextArea to dispaly the process in
	 * @param areaProcess
	 *            the process to run
	 * @param closeButton
	 *            boolean to include close button or not
	 */
	public ProcessTextArea(final JTextArea area, Process areaProcess, boolean closeButton) {
		super(area);
		myProcess = areaProcess;
		final BufferedWriter out = new BufferedWriter(new OutputStreamWriter(myProcess.getOutputStream()));
		area.addKeyListener(new KeyAdapter() {
			public void keyTyped(KeyEvent evt) {
				try {
					if (!finished) {
						out.write(evt.getKeyChar());
						out.flush();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		final SwingWorker<String, Void> pipe = new SwingWorker<String, Void>() {
			public String doInBackground() {
				boolean run = true;
				Scanner scan = new Scanner(myProcess.getInputStream());
				Scanner errorScan = new Scanner(myProcess.getErrorStream());
				scan.useDelimiter("");
				errorScan.useDelimiter("");
				while (run) {
					while (scan.hasNext()) {
						updateTextArea(scan.next());
					}
					while (errorScan.hasNext()) {
						updateTextArea(errorScan.next());
					}
					try {
						int val = myProcess.exitValue();
						finished = true;
						close.setEnabled(false);
						run = false;
						updateTextArea("\nProccess terminated with value " + val);
					} catch (Exception notClosed) {
						run = true;
					}
				}
				return null;
			}
		};
		pipe.execute();

		if (closeButton) {
			close = new JButton("Terminate");
			close.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					if (!finished)
						myProcess.destroy();
					else
						updateTextArea("\nProcess already closed");
				}
			});
			addToolbarComponent(close);
		}
	}

	/**
	 * Updates the JTextArea to the
	 * 
	 * @param newText
	 */
	private void updateTextArea(final String newText) {

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				text.append(newText);
			}
		});
	}

	/**
	 * Simple check method to see if done.
	 * 
	 * @return true if the process is done
	 */
	public boolean processFinished() {
		return finished;
	}

	public void closeProcess() {
		if (!processFinished())
			myProcess.destroy();
	}

	public class SafeCloseAdapter extends WindowAdapter {
		public void windowClosing(WindowEvent we) {
			if (!processFinished())
				myProcess.destroy();
		}
	}
}