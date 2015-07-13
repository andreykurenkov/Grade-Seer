package edu.gatech.gradeseer.gui.components;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;

import javax.swing.JButton;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

public class TextStdIOPane extends ToolbaredTextArea {
	// private InputStream redirect;
	private static JTextArea text;
	private static TextStdIOPane instance;
	private InputStream storedStream;

	public static TextStdIOPane getInstance() {
		if (instance == null) {
			text = new JTextArea();
			instance = new TextStdIOPane(text);
		}
		return instance;
	}

	private TextStdIOPane(JTextArea area) {
		super(area);
		this.redirectSystemStreams();
		// final PipedOutputStream redirectFrom = new PipedOutputStream();
		/*
		 * redirect = new PipedInputStream(); System.setIn(redirect); try { ((PipedInputStream)
		 * redirect).connect(redirectFrom); } catch (IOException e) { e.printStackTrace(); }
		 */

		text.setEditable(true);
		storedStream = System.in;
		final JButton restoreStdin = new JButton("Restore stdin to System.in");
		restoreStdin.setEnabled(false);
		restoreStdin.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				System.setIn(storedStream);
				restoreStdin.setEnabled(false);
			}

		});

		JButton setToIn = new JButton("Set text to stdin");
		final StringBuilder inputBuilder = new StringBuilder();
		setToIn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				restoreStdin.setEnabled(true);
				text.addKeyListener(new KeyAdapter() {
					@Override
					public void keyTyped(KeyEvent event) {
						try {
							try {
								String txt = "" + text.getText();
								byte[] byteArray = txt.getBytes("UTF-8"); // choose // a
								ByteArrayInputStream baos = new ByteArrayInputStream(byteArray);
								System.setIn(baos);
							} catch (Exception e) {
								e.printStackTrace();
							}

						} catch (Exception e) {
							e.printStackTrace();
						}

					}

				});
			}

		});

		super.addToolbarComponent(setToIn);
		super.addToolbarComponent(restoreStdin);

		/*
		 * this.getDocument().addDocumentListener(new DocumentListener() {
		 * 
		 * @Override public void changedUpdate(DocumentEvent de) { try { String inserted =
		 * de.getDocument().getText(de.getOffset(), de.getLength()); redirectFrom.write(inserted.getBytes()); } catch
		 * (BadLocationException e) { e.printStackTrace(); } catch (IOException e) { e.printStackTrace(); } }
		 * 
		 * @Override public void insertUpdate(DocumentEvent de) { try { String inserted =
		 * de.getDocument().getText(de.getOffset(), de.getLength()); redirectFrom.write(inserted.getBytes()); } catch
		 * (BadLocationException e) { e.printStackTrace(); } catch (IOException e) { e.printStackTrace(); }
		 * 
		 * }
		 * 
		 * @Override public void removeUpdate(DocumentEvent arg0) {
		 * 
		 * }
		 * 
		 * });
		 */
	}

	private void updateTextArea(final String text) {

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				TextStdIOPane.text.append(text);
			}
		});
	}

	private void redirectSystemStreams() {
		OutputStream out = new OutputStream() {
			@Override
			public void write(int b) throws IOException {
				updateTextArea(String.valueOf((char) b));
			}

			@Override
			public void write(byte[] b, int off, int len) throws IOException {
				updateTextArea(new String(b, off, len));
			}

			@Override
			public void write(byte[] b) throws IOException {
				write(b, 0, b.length);
			}
		};

		System.setOut(new PrintStream(out, true));
		System.setErr(new PrintStream(out, true));
	}

	public JTextArea getArea() {
		return text;
	}

}
