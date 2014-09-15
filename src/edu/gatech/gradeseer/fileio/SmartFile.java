package edu.gatech.gradeseer.fileio;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;

/**
 * An extension of Java's File class that provides various utility methods. This is meant to be used in conjunction with an
 * existing File object or as a new File object with more functionality.
 * 
 * @author Andrey Kurenkov
 * @version 1.1
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
	 */
	public SmartFile(String path) {
		super(path);
	}

	/**
	 * Constructor chaining to super constructor in File
	 * 
	 * @param path
	 *            path to file
	 */
	public SmartFile(URI path) {
		super(path);
	}

	/**
	 * Constructor chaining to super constructor in File
	 * 
	 * @param parent
	 *            path to file
	 * @param child
	 *            the child file to the parent (usually file within parent directory)
	 */
	public SmartFile(File parent, String child) {
		super(parent, child);
	}

	/**
	 * Constructor chaining to super constructor in File.
	 * 
	 * @param path
	 *            path to file
	 */
	public SmartFile(File f) {
		this(f.getAbsolutePath());
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
	 * Returns if the File is compilable (currently only supports Java compilation)
	 * 
	 * @return true if a java file
	 */
	public boolean isCompilable() {
		return this.getName().endsWith(".java");
	}

	/**
	 * Determines if the file is runnable
	 * 
	 * @return true if a .class file with a main method
	 */
	public boolean isRunnable() {
		String name = this.getName();
		int index = 0;
		if ((index = name.indexOf('.')) <= 0)
			return false;
		if (!name.substring(index).equals(".class"))
			return false;
		try {
			URL[] load = { new File(this.getAbsolutePath().substring(0, this.getAbsolutePath().indexOf(this.getName())))
					.toURI().toURL() };
			URLClassLoader loader = new URLClassLoader(load);
			Class<?> myClass = loader.loadClass(this.getNonExtensionName());
			myClass.getMethod("main", String[].class);
		} catch (MalformedURLException e) {
			e.printStackTrace();// TODO: proper error-handling
			return false;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return false;
		} catch (SecurityException e) {
			e.printStackTrace();
			return false;
		} catch (NoSuchMethodException e) {
			return false;
		}
		return true;

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
	 * Tried to compile file and returns a boolean to indicate success.
	 * 
	 * @return true if success in compiling, false otherwise
	 */
	public String compile() {
		return this.compile(null);
	}

	/**
	 * Tried to compile file and returns a boolean to indicate success. Also writes all io/status info to the provided
	 * Writer.
	 * 
	 * @param writer
	 *            output writer
	 * @return true if success in compiling, false otherwise
	 */
	public String compile(Writer writer) {
		if (this.isCompilable()) {
			JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
			if (compiler == null) {// if null do something else like basically
				// command line
				ProcessBuilder builder = new ProcessBuilder("javac", this.getName());
				builder.directory(this.getDirectory().getAbsoluteFile());
				try {
					Process compile = builder.start();
					return (compile.waitFor() == 0) ? null : ERROR;
				} catch (IOException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
			StandardJavaFileManager fm = compiler.getStandardFileManager(diagnostics, null, null);
			List<File> dir = new ArrayList<File>();
			dir.add(getParentFile());
			try {
				fm.setLocation(StandardLocation.CLASS_PATH, dir);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			ArrayList<File> file = new ArrayList<File>(1);
			file.add(this);
			JavaCompiler.CompilationTask task = null;
			task = compiler.getTask(writer, fm, diagnostics, null, null, fm.getJavaFileObjectsFromFiles(file));

			boolean success = task.call();
			if (success) {
				return null;
			} else {
				StringBuilder message = new StringBuilder();
				for (Diagnostic<? extends JavaFileObject> d : diagnostics.getDiagnostics()) {
					message.append(d.toString());
					try {
						if (writer != null) {
							writer.append(d.toString());
							writer.flush();
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				return message.toString();
			}
		} else
			return NOT_COMPILABLE;
	}

	/**
	 * Utility method to compile a batch of SmartFiles at once.
	 * 
	 * @param writer
	 *            the writer to output info to
	 * @param files
	 *            any number of SmartFile to compile
	 * @return true if all compiled, false otherwise
	 */
	public static String compile(Writer writer, SmartFile... files) {
		ArrayList<SmartFile> arrayFiles = new ArrayList<SmartFile>();
		for (SmartFile file : files)
			arrayFiles.add(file);
		return compile(arrayFiles, writer);
	}

	/**
	 * A method to compile many SmartFiles together (can be more efficient than doing so one by one, though not
	 * significantly)
	 * 
	 * @param files
	 *            all files to compile
	 * @param writer
	 *            the writer to output into to
	 * @return true if all compiled
	 */
	public static String compile(ArrayList<SmartFile> files, Writer writer) {
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		if (compiler == null) {
			StringBuilder errors = new StringBuilder();
			for (SmartFile file : files) {
				String error = file.compile(writer);
				if (error != null)
					errors.append("\n" + error);
			}
			String error = errors.toString();
			if (error.length() == 0)
				return null;
			return errors.toString();
		}

		List<File> dir = new ArrayList<File>();
		for (SmartFile sm : files) {
			if (!sm.isCompilable())
				return NOT_COMPILABLE;
			else {
				dir.add(sm.getParentFile());
			}
		}

		DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
		StandardJavaFileManager fm = compiler.getStandardFileManager(diagnostics, Locale.ENGLISH, Charset.defaultCharset());

		try {
			fm.setLocation(StandardLocation.CLASS_PATH, dir);
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		JavaCompiler.CompilationTask task = null;
		task = compiler.getTask(writer, fm, diagnostics, null, null, fm.getJavaFileObjectsFromFiles(files));
		boolean success = task.call();
		if (success) {
			return null;
		} else {
			StringBuilder message = new StringBuilder();
			for (Diagnostic<? extends JavaFileObject> d : diagnostics.getDiagnostics()) {
				message.append(d.toString());
				try {
					if (writer != null) {
						writer.append(d.toString());
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			return message.toString();
		}
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
		SmartFile[] smart = new SmartFile[stupid.length];
		for (int i = 0; i < stupid.length; i++)
			smart[i] = new SmartFile(stupid[i].getAbsolutePath());
		return smart;
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
		return new SmartFile(getParentFile());
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
		return this.isTxt() || this.isHtml() || this.isCompilable();
	}

}