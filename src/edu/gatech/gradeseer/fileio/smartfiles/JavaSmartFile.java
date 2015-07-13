package edu.gatech.gradeseer.fileio.smartfiles;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;

import edu.gatech.gradeseer.fileio.SmartFile;

/**
 * JavaSmartFile that implements all compilation logic.
 * 
 * @author andrey
 */
public class JavaSmartFile extends CompilableSmartFile {
	private boolean isCompiled;

	/**
	 * Constructor chaining to super constructor in File
	 * 
	 * @param path
	 *            path to file
	 * @throws IOException
	 */
	public JavaSmartFile(String path) throws IOException {
		super(path);
	}

	/**
	 * Constructor chaining to super constructor in File
	 * 
	 * @param path
	 *            path to file
	 * @throws IOException
	 */
	public JavaSmartFile(URI path) throws IOException {
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
	public JavaSmartFile(File parent, String child) throws IOException {
		super(parent, child);
	}

	/**
	 * Constructor chaining to super constructor in File.
	 * 
	 * @param path
	 *            path to file
	 * @throws IOException
	 */
	public JavaSmartFile(File f) throws IOException {
		this(f.getAbsolutePath());
	}

	/**
	 * Checks that the file is a .java file
	 * 
	 * @return true if java file, else false
	 */
	protected boolean isValid() {
		return super.isValid() && super.isJavaFile();
	}

	public CompiledSmartFile checkForCompiledFile() {
		SmartFile directory = this.getDirectory();
		for (File file : directory.listFiles()) {
			if (file.getName().equals(this.getNonExtensionName() + ".class"))
				try {
					lastCompilation = new CompiledJavaSmartFile(file);
					return lastCompilation;
				} catch (IOException e) {
					e.printStackTrace();// should not happen, but just in caese
				}
		}
		return null;
	}

	/**
	 * 
	 * @return If this file is compilabe and has ever been succesfully compiled, return true. Else false.
	 */
	public boolean isCompiled() {
		return isCompiled;
	}

	/**
	 * Protected compiled setter for the static mass compilation to notify each individual file of success.
	 */
	protected void setCompiledToTrue() {
		isCompiled = true;
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
			this.isCompiled = true;
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
	public static String compile(Writer writer, JavaSmartFile... files) {
		ArrayList<JavaSmartFile> arrayFiles = new ArrayList<JavaSmartFile>();
		for (JavaSmartFile file : files)
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
	public static String compile(ArrayList<JavaSmartFile> files, Writer writer) {
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		if (compiler == null) {
			StringBuilder errors = new StringBuilder();
			for (JavaSmartFile file : files) {
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
		for (JavaSmartFile sm : files) {
			if (!sm.isJavaFile())
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
			for (JavaSmartFile sm : files)
				sm.setCompiledToTrue();
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
}
