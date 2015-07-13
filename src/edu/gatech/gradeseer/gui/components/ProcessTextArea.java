package edu.gatech.gradeseer.gui.components;

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
	private ProcessBuilder processBuilder;
	private BufferedWriter out;
	private boolean finished;
	private JButton close;
	private JButton rerun;

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
	public ProcessTextArea(final JTextArea area, ProcessBuilder processBuilder) {
		super(area);
		this.processBuilder = processBuilder;

		close = new JButton("Terminate");
		close.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				closeProcess();
			}
		});
		addToolbarComponent(close);

		rerun = new JButton("Rerun");
		rerun.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				closeProcess();
				startProcess();
			}
		});
		addToolbarComponent(rerun);

		area.addKeyListener(new KeyAdapter() {
			private StringBuilder inputBuilder = new StringBuilder();

			public void keyPressed(KeyEvent evt) {
				try {
					if (!finished) {
						int keyCode = evt.getKeyCode();
						if (keyCode == KeyEvent.VK_ENTER) {
							out.append(inputBuilder.toString());
							out.append(evt.getKeyChar());
							inputBuilder.delete(0, inputBuilder.length());
							out.flush();
						} else if (keyCode == KeyEvent.VK_BACK_SPACE) {
							if (inputBuilder.length() > 0)
								inputBuilder.deleteCharAt(inputBuilder.length() - 1);
						} else if (keyCode != KeyEvent.VK_SHIFT && keyCode != KeyEvent.VK_ALT
								&& keyCode != KeyEvent.VK_CAPS_LOCK) {
							inputBuilder.append(evt.getKeyChar());
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		startProcess();
	}

	public void closeProcess() {
		if (!finished)
			myProcess.destroy();
		close.setEnabled(false);
	}

	private void startProcess() {
		try {
			myProcess = processBuilder.start();
			close.setEnabled(true);
		} catch (IOException e1) {
			text.append("IOException when starting process");
			e1.printStackTrace();
		}
		out = new BufferedWriter(new OutputStreamWriter(myProcess.getOutputStream()));

		/*
		 * This SwingWorker takes the stdin of the process and pipes it to the text area.
		 */
		final SwingWorker<String, Void> pipe = new SwingWorker<String, Void>() {
			public String doInBackground() {
				boolean run = true;
				Scanner inputScan = new Scanner(myProcess.getInputStream());
				Scanner errorScan = new Scanner(myProcess.getErrorStream());
				inputScan.useDelimiter("");
				errorScan.useDelimiter("");
				while (run) {
					while (inputScan.hasNext()) {
						updateTextArea(inputScan.next());
					}
					while (errorScan.hasNext()) {
						updateTextArea(errorScan.next());
					}
					try {
						int val = myProcess.exitValue();
						finished = true;
						close.setEnabled(false);
						run = false;
						updateTextArea("\nProccess terminated with value " + val + "\n");
					} catch (Exception notClosed) {
						run = true;
					}
				}
				return null;
			}
		};
		pipe.execute();
		close.setEnabled(true);
		finished = false;
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

	public class SafeCloseAdapter extends WindowAdapter {
		public void windowClosing(WindowEvent we) {
			if (!processFinished())
				myProcess.destroy();
		}
	}
}