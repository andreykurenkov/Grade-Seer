package edu.gatech.gradeseer.fileio.smartfiles;

import java.io.File;
import java.io.IOException;
import java.net.URI;

import edu.gatech.gradeseer.fileio.SmartFile;

/**
 * Abstract Compilable File. Used to allow for possible future support for python/C.
 * 
 * @author andrey
 */
public abstract class CompiledSmartFile extends SmartFile {
	/**
	 * Constructor chaining to super constructor in File
	 * 
	 * @param path
	 *            path to file
	 * @throws IOException
	 */
	public CompiledSmartFile(String path) throws IOException {
		super(path);
	}

	/**
	 * Constructor chaining to super constructor in File
	 * 
	 * @param path
	 *            path to file
	 * @throws IOException
	 */
	public CompiledSmartFile(URI path) throws IOException {
		super(path);
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
	public CompiledSmartFile(File parent, String child) throws IOException {
		super(parent, child);
	}

	/**
	 * Constructor chaining to super constructor in File.
	 * 
	 * @param path
	 *            path to file
	 * @throws IOException
	 */
	public CompiledSmartFile(File f) throws IOException {
		this(f.getAbsolutePath());
	}

	/**
	 * Checks that the file is a .class file.
	 */
	protected abstract boolean isValid();

	/**
	 * Determines if the file is runnable
	 * 
	 * @return true if a .class file with a main method
	 */
	public abstract boolean isRunnable();

	/**
	 * If the File is runnable, runs the file and returns its Process.
	 * 
	 * @return the Process instance that results from running the file, or null if it is not runnable or there are errors
	 */
	public abstract Process run();

	/**
	 * Returns a ProcessBuilder that would allow other code to start running this file.
	 * 
	 * @return return ProcessBuilder for file, or null if not runnable
	 */
	public abstract ProcessBuilder getRunProcessBuilder();
}
