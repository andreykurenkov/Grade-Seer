package edu.gatech.gradeseer.fileio.smartfiles;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.net.URI;

import edu.gatech.gradeseer.fileio.SmartFile;

/**
 * Abstract Compilable File. Used to allow for possible future support for python/C.
 * 
 * @author andrey
 */
public abstract class CompilableSmartFile extends SmartFile {
	protected CompiledSmartFile lastCompilation;

	/**
	 * Constructor chaining to super constructor in File
	 * 
	 * @param path
	 *            path to file
	 * @throws IOException
	 */
	public CompilableSmartFile(String path) throws IOException {
		super(path);
	}

	/**
	 * Constructor chaining to super constructor in File
	 * 
	 * @param path
	 *            path to file
	 * @throws IOException
	 */
	public CompilableSmartFile(URI path) throws IOException {
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
	public CompilableSmartFile(File parent, String child) throws IOException {
		super(parent, child);
	}

	/**
	 * Constructor chaining to super constructor in File.
	 * 
	 * @param path
	 *            path to file
	 * @throws IOException
	 */
	public CompilableSmartFile(File f) throws IOException {
		this(f.getAbsolutePath());
	}

	/**
	 * @return true
	 */
	public boolean isJavaFile() {
		return true;
	}

	public abstract CompiledSmartFile checkForCompiledFile();

	/**
	 * 
	 * @return If this file is compilabe and has ever been succesfully compiled, return true. Else false.
	 */
	public abstract boolean isCompiled();

	/**
	 * Tries to compile using both JavaCompiler and command line statement
	 * 
	 * @return null if success in compiling, error string otherwise
	 */
	public abstract String compile();

	/**
	 * Tried to compile file and returns a String to indicate success. Also writes all io/status info to the provided Writer.
	 * 
	 * @param writer
	 *            output writer
	 * @return null if success in compiling, error string otherwise
	 */
	public abstract String compile(Writer writer);

	/**
	 * Returns the result of the last compilation.
	 * 
	 * @return null if not compiled, else result of compile() or checkForCompiledFile()
	 */
	public CompiledSmartFile getLastCompilation() {
		return lastCompilation;
	}

}
