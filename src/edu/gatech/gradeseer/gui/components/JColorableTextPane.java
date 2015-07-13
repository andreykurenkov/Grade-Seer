package edu.gatech.gradeseer.gui.components;

import java.awt.Color;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.swing.JTextPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Document;
import javax.swing.text.Highlighter;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

import edu.gatech.gradeseer.fileio.SmartFile;

public class JColorableTextPane extends JTextPane {
	private HashMap<String[], Color> wordColorings;
	private HashMap<ColoringRegion, Color> regionColorings;
	private StyleContext styleContext;
	private SmartFile originFile;

	public static class ColoringRegion {
		public String start;
		public String end;

		public ColoringRegion(String start, String end) {
			this.start = start;
			this.end = end;
		}
	}

	public JColorableTextPane() {
		this("");
	}

	public JColorableTextPane(String text) {
		wordColorings = new HashMap<String[], Color>();
		regionColorings = new HashMap<ColoringRegion, Color>();
		styleContext = StyleContext.getDefaultStyleContext();
		this.setText(text);
		this.setEditable(false);
	}

	public JColorableTextPane(SmartFile file) throws IOException {
		if (file != null && file.isReadable()) {
			wordColorings = new HashMap<String[], Color>();
			regionColorings = new HashMap<ColoringRegion, Color>();
			styleContext = StyleContext.getDefaultStyleContext();
			this.originFile = file;
			this.setText(file.read());
			this.setEditable(false);
		}
		throw new IOException("This file is not a readable file");

	}

	public SmartFile getOriginFile() {
		return originFile;
	}

	public void setTextFromFile(SmartFile file) throws IOException {
		if (file != null && file.isReadable()) {
			styleContext = StyleContext.getDefaultStyleContext();
			this.originFile = file;
			this.setText(file.read());
			this.setEditable(false);
		}
		throw new IOException("This file is not a text file");
	}

	public void setText(String text) {
		setText(text, true);
	}

	protected void colorAllText() {
		String text = this.getText();
		this.colorText(0, text.length(), Color.BLACK);
		for (Entry<String[], Color> coloring : wordColorings.entrySet())
			for (String word : coloring.getKey())
				colorWord(word, coloring.getValue());

		for (Entry<ColoringRegion, Color> regionColoring : regionColorings.entrySet())
			colorRegion(regionColoring.getKey().start, regionColoring.getKey().end, regionColoring.getValue());

		// handle last line comment
		// TODO: clean up
		int lastComment = text.lastIndexOf("//");
		if (lastComment != -1) {
			int close = text.indexOf("\n", lastComment + 1);
			if (close == -1) {
				colorText(lastComment, text.length() - lastComment, Color.gray);
			}
		}
	}

	public void setText(String text, boolean doColoring) {
		super.setText(text);
		colorAllText();
		setCaretPosition(0);
	}

	private void colorWord(String word, Color color) {
		String text = this.getText();
		int index = text.indexOf(word);
		while (index != -1) {
			this.colorText(index, word.length(), color);
			index = text.indexOf(word, index + 1);
		}
	}

	private void colorRegion(String start, String end, Color color) {
		String text = this.getText();
		int startIndex = text.indexOf(start);
		while (startIndex != -1) {
			int endIndex = text.indexOf(end, startIndex + 1);
			if (endIndex != -1) {
				this.colorText(startIndex, endIndex - startIndex + end.length(), color);
				startIndex = text.indexOf(start, endIndex + end.length());
			} else
				startIndex = -1;
		}
	}

	public void colorText(final int start, final int length, Color color) {
		final AttributeSet colorSet = styleContext.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, color);
		getStyledDocument().setCharacterAttributes(start, length, colorSet, false);
	}

	public void appendToPane(JTextPane tp, String msg, Color c) {
		StyleContext sc = StyleContext.getDefaultStyleContext();
		AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, c);

		aset = sc.addAttribute(aset, StyleConstants.FontFamily, "Lucida Console");
		aset = sc.addAttribute(aset, StyleConstants.Alignment, StyleConstants.ALIGN_JUSTIFIED);

		int len = tp.getDocument().getLength();
		tp.setCaretPosition(len);
		tp.setCharacterAttributes(aset, false);
		tp.replaceSelection(msg);
	}

	// Creates highlights around all occurrences of pattern in textComp
	public void highlight(String pattern, Color color) {
		// First remove all old highlights

		try {
			Highlighter hilite = getHighlighter();
			DefaultHighlighter.DefaultHighlightPainter painter = new DefaultHighlighter.DefaultHighlightPainter(color);
			Document doc = getDocument();
			String text = doc.getText(0, doc.getLength());
			int pos = 0;

			// Search for pattern
			while ((pos = text.indexOf(pattern, pos)) >= 0) {
				// Create highlighter using private painter and apply around pattern
				hilite.addHighlight(pos, pos + pattern.length(), painter);
				pos += pattern.length();
			}
		} catch (BadLocationException e) {
		}
	}

	public void addWordColoring(String[] words, Color color) {
		wordColorings.put(words, color);
	}

	public void addRegionColoring(String start, String end, Color color) {
		regionColorings.put(new ColoringRegion(start, end), color);
	}
}
