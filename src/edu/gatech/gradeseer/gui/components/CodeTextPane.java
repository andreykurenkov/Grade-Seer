package edu.gatech.gradeseer.gui.components;

import java.awt.Color;

import edu.gatech.gradeseer.fileio.SmartFile;
import edu.gatech.gradeseer.fileio.smartfiles.CompilableSmartFile;
import edu.gatech.gradeseer.fileio.smartfiles.JavaSmartFile;

public class CodeTextPane extends JColorableTextPane {
	private CompilableSmartFile codeFile;

	// TODO currently depends on JavaSmartFile... should be more generic
	public static CodeTextPane getJavaTextPane(JavaSmartFile file) {
		final String[] PURPLE_COLORING = { "\npublic ", "\nprivate ", " public ", " private ", " protected ", "true",
				"false" };
		final String[] RED_COLORING = { " final ", " static ", " class ", " void " };
		final String[] PRIMITIVE_COLORING = { " int ", " double ", " char ", " boolean ", " byte ", " short " };
		String[][] words = { PURPLE_COLORING, RED_COLORING, PRIMITIVE_COLORING };
		Color purple = new Color(128, 0, 128);
		Color red = new Color(218, 44, 67);
		Color blue = new Color(44, 117, 255);

		Color[] wordColors = { purple, red, blue };
		CodeTextPane javaTextPane = new CodeTextPane(file);
		for (int i = 0; i < 3; i++)
			javaTextPane.addWordColoring(words[i], wordColors[i]);

		javaTextPane.addRegionColoring("\"", "\"", Color.blue);
		javaTextPane.addRegionColoring("/*", "*/", Color.gray);
		javaTextPane.addRegionColoring("//", "\n", Color.gray);
		javaTextPane.colorAllText();
		return javaTextPane;
	}

	public CodeTextPane(CompilableSmartFile file) {
		super(file.read());
		codeFile = file;
	}

	public CodeTextPane(String text) {
		super(text);
	}

	public SmartFile getOriginFile() {
		return codeFile;
	}

	public CompilableSmartFile getCodeFile() {
		return codeFile;
	}

}
