package edu.gatech.gradeseer.fileio;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * An extension of Java's File class that provides various utility methods. This is meant to be used in conjunction with an
 * existing File object or as a new File object with more functionality.
 * 
 * @author Andrey Kurenkov
 * @version 1.75
 * 
 */
@SuppressWarnings("serial")
public class SmartFile extends File {
	public final static String ERROR = "Error_Compiling";
	public final static String NOT_COMPILABLE = "Not compilable";
	public final static String NOT_RUNNABLE = "Not runnable";

	/**
	 * Constructor chaining to super constructor in File
	 * 
	 * @param path
	 *            path to file
	 * @throws IOException
	 */
	public SmartFile(String path) throws IOException {
		super(path);
		throwIfInvalid();
	}

	/**
	 * Constructor chaining to super constructor in File
	 * 
	 * @param path
	 *            path to file
	 * @throws IOException
	 */
	public SmartFile(URI path) throws IOException {
		super(path);
		throwIfInvalid();
	}

	/**
	 * Constructor chaining to super constructor in File
	 * 
	 * @param parent
	 *            path to file
	 * @param child
	 *            the child file to the parent (usually file within parent directory)
	 * @throws IOException
	 */
	public SmartFile(File parent, String child) throws IOException {
		super(parent, child);
		throwIfInvalid();
	}

	/**
	 * Constructor chaining to super constructor in File.
	 * 
	 * @param path
	 *            path to file
	 * @throws IOException
	 */
	public SmartFile(File f) throws IOException {
		this(f.getAbsolutePath());
		throwIfInvalid();
	}

	/**
	 * Checks that the file exists and be overriden for specific standards.
	 * 
	 * @return true if exists, false otherwise.
	 */
	protected boolean isValid() {
		return this.exists();
	}

	/**
	 * Included in all SmartFile constructors to allow subclasses easy checking for validity.
	 * 
	 * @throws IOException
	 */
	protected void throwIfInvalid() throws IOException {
		if (!isValid()) {
			throw new IOException("This file does not exist or is not the right format.");
		}
	}

	/**
	 * ToString implementation of SmartFile.
	 * 
	 * @returns the name of the file.
	 */
	public String toString() {
		return this.getName();
	}

	/**
	 * @return true if a java file
	 */
	public boolean isJavaFile() {
		return this.getName().endsWith(".java");
	}

	/**
	 * Returns the name of the file lacking its extension.
	 * 
	 * @return name sans-extension
	 */
	public String getNonExtensionName() {
		String name = this.getName();
		int index = 0;
		if ((index = name.indexOf('.')) > 0)
			return name.substring(0, index);
		return this.getName();
	}

	/**
	 * Returns the extension of this file, not including the period.
	 * 
	 * @return an extension not containing its period, such as "java"
	 */
	public String getExtension() {
		return getExtension(false);
	}

	/**
	 * Returns the extension of this file, possibly containig its period.
	 * 
	 * @param period
	 *            whether the extension should contain its period
	 * @return an extension containing
	 */
	public String getExtension(boolean period) {
		String name = this.getName();
		int index = 0;
		if ((index = name.indexOf('.')) > 0)
			return name.substring(index + (!period && (index < name.length() - 1) ? 1 : 0));
		return this.getName();
	}

	/**
	 * If the File is an html file, runs the file with appletviewer and returns its Process.
	 * 
	 * @return the Process instance that results from running the file, or null if it is not html or there are errors
	 */
	public Process runWithAppletViewer() {
		if (this.isHtml()) {
			ProcessBuilder builder = getRunBuilderForAppletViewer();
			try {
				Process runningProcess = builder.start();
				return runningProcess;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * Returns a ProcessBuilder that would allow other code to start running this html file with an appletviewer.
	 * 
	 * @return return ProcessBuilder for file, or null if not html
	 */
	public ProcessBuilder getRunBuilderForAppletViewer() {
		if (this.isHtml()) {
			ProcessBuilder builder = new ProcessBuilder("appletviewer", this.getName());
			builder.directory(this.getDirectory().getAbsoluteFile());

			return builder;
		}
		return null;
	}

	/**
	 * Simply attempts to read the file and return its String representation.
	 * 
	 * @return contents of file, or null if not readable
	 */
	public String read() {
		if (this.canRead()) {
			Scanner scan = null;
			try {
				scan = new Scanner(this);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			String res = "";
			String next = "";
			while (scan.hasNextLine())
				if ((next = scan.nextLine()) != null && next.length() > 0)
					res += "\n" + next;
			scan.close();
			return res;
		} else
			return null;
	}

	/**
	 * Appends Strings to the end of this file.
	 * 
	 * @param append
	 *            the string to append
	 * @return true if allowed to write to file and done succesfully, false otherwise
	 */
	public boolean append(String append) {
		if (this.canWrite()) {
			FileWriter writer;
			try {
				writer = new FileWriter(this);
				writer.append(append);
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
			return true;
		} else
			return false;
	}

	/**
	 * Makes a batch of normal File objects into SmartFile objects
	 * 
	 * @param stupid
	 *            all the normal File objects
	 * @return array of converted SmartFile objects
	 */
	public static SmartFile[] makeSmarter(File... stupid) {
		ArrayList<SmartFile> smart = new ArrayList<SmartFile>();
		for (int i = 0; i < stupid.length; i++)
			try {
				smart.add(new SmartFile(stupid[i].getAbsolutePath()));
			} catch (IOException e) {
				e.printStackTrace();
			}
		return smart.toArray(new SmartFile[smart.size()]);
	}

	/**
	 * Copies this File to another location.
	 * 
	 * @param to
	 *            the location of where to copy the other file
	 * @throws IOException
	 *             if copying is flawed, should be handled in calling code.
	 * @return true if copied fine, false otherwise
	 */
	public boolean copyTo(File to) throws IOException {

		if (this.exists() && this.canRead()) {
			BufferedInputStream in = new BufferedInputStream(new FileInputStream(this));
			BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(to));
			int c;

			while ((c = in.read()) != -1)
				out.write(c);

			in.close();
			out.close();
			return true;
		}
		return false;
	}

	/**
	 * Gets the directory of this file
	 * 
	 * @return if a directory, itself. Otherwise, the directory the File is in.
	 */
	public SmartFile getDirectory() {
		if (this.isDirectory())
			return this;
		try {
			return new SmartFile(getParentFile());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static boolean checkRightExtension(File file, String wantedExten) {
		try {
			SmartFile sfile = new SmartFile(file);
			return sfile.getExtension().equals(wantedExten);
		} catch (IOException e) {
			return false;
		}
	}

	/**
	 * Checks if the file is a .csv file
	 * 
	 * @return true if ends with .csv
	 */
	public boolean isCsv() {
		return this.getExtension(true).equals(".csv");
	}

	/**
	 * Checks if the file is a .txt file
	 * 
	 * @return true if ends with .txt
	 */
	public boolean isTxt() {
		return this.getExtension(true).equals(".txt");
	}

	/**
	 * Checks if the file is a .html file
	 * 
	 * @return true if ends with .html
	 */
	public boolean isHtml() {
		return this.getExtension(true).equals(".html");
	}

	/**
	 * Checks if file can be read by program.
	 * 
	 * @return true if txt html or java file
	 */
	public boolean isReadable() {
		return isCsv() || isTxt() || isHtml() || isJavaFile();
	}

}