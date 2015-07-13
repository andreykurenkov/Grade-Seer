package edu.gatech.gradeseer.fileio.smartfiles;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;

public class CompiledJavaSmartFile extends CompiledSmartFile {

	/**
	 * Constructor chaining to super constructor in File
	 * 
	 * @param path
	 *            path to file
	 * @throws IOException
	 */
	public CompiledJavaSmartFile(String path) throws IOException {
		super(path);
	}

	/**
	 * Constructor chaining to super constructor in File
	 * 
	 * @param path
	 *            path to file
	 * @throws IOException
	 */
	public CompiledJavaSmartFile(URI path) throws IOException {
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
	public CompiledJavaSmartFile(File parent, String child) throws IOException {
		super(parent, child);
	}

	/**
	 * Constructor chaining to super constructor in File.
	 * 
	 * @param path
	 *            path to file
	 * @throws IOException
	 */
	public CompiledJavaSmartFile(File f) throws IOException {
		this(f.getAbsolutePath());
	}

	/**
	 * Checks that the file is a .class file.
	 */
	protected boolean isValid() {
		if (!exists())
			return false;
		String name = this.getName();
		int index = 0;
		if ((index = name.indexOf('.')) <= 0)
			return false;
		if (!name.substring(index).equals(".class"))
			return false;
		return true;
	}

	/**
	 * Determines if the file is runnable
	 * 
	 * @return true if a .class file with a main method
	 */
	public boolean isRunnable() {
		try {
			URL[] load = { new File(this.getAbsolutePath().substring(0, this.getAbsolutePath().indexOf(this.getName())))
					.toURI().toURL() };
			URLClassLoader loader = new URLClassLoader(load);
			Class<?> myClass = loader.loadClass(this.getNonExtensionName());
			myClass.getMethod("main", String[].class);
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	/**
	 * If the File is runnable, runs the file and returns its Process.
	 * 
	 * @return the Process instance that results from running the file, or null if it is not runnable or there are errors
	 */
	public Process run() {
		if (this.isRunnable()) {
			ProcessBuilder builder = getRunProcessBuilder();
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
	 * Returns a ProcessBuilder that would allow other code to start running this file.
	 * 
	 * @return return ProcessBuilder for file, or null if not runnable
	 */
	public ProcessBuilder getRunProcessBuilder() {
		if (this.isRunnable()) {
			ProcessBuilder builder = new ProcessBuilder("java", this.getNonExtensionName());
			builder.directory(this.getDirectory().getAbsoluteFile());
			return builder;
		}
		return null;
	}

}
