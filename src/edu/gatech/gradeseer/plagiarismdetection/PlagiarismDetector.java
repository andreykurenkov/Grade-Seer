package edu.gatech.gradeseer.plagiarismdetection;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 * Abstract class for Plagiarism detectors.
 * 
 * @author Amirreza Shaban
 */

public abstract class PlagiarismDetector {

	private static Map<String, Class<?>> nameToClassMap;
	protected Map<String, File[]> sourceMap;
	protected Vector<File> baseFiles;

	static {
		PlagiarismDetector.nameToClassMap = new HashMap<String, Class<?>>();

		/* add <name, class> tuples for implemented detectors */
		PlagiarismDetector.nameToClassMap.put("moss", MOSS.class);
	}

	protected PlagiarismDetector() {
		this.sourceMap = new HashMap<String, File[]>();
		this.baseFiles = new Vector<File>();
	}

	/* return a PlagiarismDetector instance with the given name */
	public static PlagiarismDetector getInstance(String name, String args) {
		if (PlagiarismDetector.nameToClassMap.containsKey(name)) {
			try {
				return (PlagiarismDetector) PlagiarismDetector.nameToClassMap.get(name).getConstructor(String.class)
						.newInstance(args);
			} catch (InstantiationException e) {
				// wont be thrown
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// wont be thrown
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// wont be thrown
				e.printStackTrace();
			} catch (SecurityException e) {
				// wont be thrown
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// wont be thrown
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				// wont be thrown
				e.printStackTrace();
			}
			return null;
		} else {
			throw new IllegalArgumentException("Could not find PlagiarismDetector class with name " + name);
		}
	}

	/* adds a base file to the repository */
	public void addBaseFile(File file) {
		this.baseFiles.add(file);
	}

	/* adds the files under the given student name to the repository */
	public void addStudentFiles(String name, File[] files) {
		if (this.sourceMap.containsKey(name)) {
			throw new IllegalArgumentException("The student name " + name + " is not unique!");
		}
		this.sourceMap.put(name, files);
	}
	
	public void addStudentFiles(String name, List<File> files) {
		this.sourceMap.put(name, files.toArray(new File[files.size()]));
	}
	

	/* delete the repository */
	public void reset() {
		this.sourceMap.clear();
	}

	/* run the plagiarism detector and return the report */
	public abstract String run() throws PlagiarismDetectorException;

}
