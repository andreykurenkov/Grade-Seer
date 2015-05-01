package edu.gatech.gradeseer.fileio;

import java.io.File;

/**
 * A wrapper class for File. It assigns virtualAbsolutePath to a file
 * @author Amirreza Shaban
 */

public class VirtualFile extends File {
	private String virtualAdr;

	public VirtualFile(String virtualAdr, File file) {
		super(file.getParent(), file.getName());
		this.virtualAdr = virtualAdr;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 7106561319998089739L;

	public String getAbsolutePath() {
		return this.virtualAdr;
	}
}
