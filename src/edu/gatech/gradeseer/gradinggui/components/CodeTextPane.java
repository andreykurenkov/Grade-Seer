package edu.gatech.gradeseer.gradinggui.components;

import java.awt.Color;

import javax.swing.JTextPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Document;
import javax.swing.text.Highlighter;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

public class CodeTextPane extends JTextPane {
	private final String[] PURPLE_COLORING = { "\npublic ", "\nprivate ", " public ", " private ", " protected " };
	private final String[] RED_COLORING = { " final ", " static ", " class ", " void " };
	private final String[] PRIMITIVE_COLORING = { " int ", " double ", " char ", " boolean ", " byte ", " short " };
	private StyleContext styleContext;

	public CodeTextPane() {
		styleContext = StyleContext.getDefaultStyleContext();
		this.setEditable(false);
	}

	public CodeTextPane(String text) {
		styleContext = StyleContext.getDefaultStyleContext();
		this.setText(text);
		this.setEditable(false);
	}

	public void setText(String text) {
		setText(text, true);
	}

	public void setText(String text, boolean doColoring) {
		super.setText(text);
		this.colorText(0, text.length(), Color.BLACK);
		if (doColoring) {
			Color purple = new Color(128, 0, 128);
			for (String purpleWord : PURPLE_COLORING) {
				this.colorWord(purpleWord, purple);
			}
			Color red = new Color(218, 44, 67);
			for (String redWord : RED_COLORING) {
				this.colorWord(redWord, red);
			}
			Color blue = new Color(44, 117, 255);
			for (String greyWord : PRIMITIVE_COLORING) {
				this.colorWord(greyWord, blue);
			}
			this.colorRegion("\"", "\"", Color.blue);
			this.colorRegion("/*", "*/", Color.gray);
			this.colorRegion("//", "\n", Color.gray);
			// handle last line comment
			int lastComment = text.lastIndexOf("//");
			if (lastComment != -1) {
				int close = text.indexOf("\n", lastComment + 1);
				if (close == -1) {
					this.colorText(lastComment, text.length() - lastComment, Color.gray);
				}
			}
		}

		this.setCaretPosition(0);

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
}
