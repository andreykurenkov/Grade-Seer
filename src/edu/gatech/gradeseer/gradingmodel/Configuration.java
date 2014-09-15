/**
 * 
 */
package edu.gatech.gradeseer.gradingmodel;

import java.io.File;
import java.util.HashMap;

/**
 * A key-value hashmap approach to saving properties. Written before knowledge of the Properties class and with an intention
 * for extension (thus hiding the hashmap instead of extending it)
 * 
 * @author Andrey Kurenkov
 * @version 1.1
 */
public class Configuration {
	public final static String KEY_STUDENTS = "StudentsFile";
	public final static String KEY_BASE_DIR = "BaseDir";
	public final static String KEY_STUDENT_DIR = "StudentDir";
	public final static String KEY_GRADES_DIR = "gradeDir";
	public final static String KEY_SHOW_INTRO = "ShowIntro";
	private StudentSet cachedStudentSet;
	private static Configuration global;
	private HashMap<String, String> properties;

	public Configuration(HashMap<String, String> properties) {
		this.properties = properties;
	}

	public static void setGlobal(Configuration newGlobal) {
		global = newGlobal;
	}

	public static Configuration getGlobal() {
		return global;
	}

	public Configuration() {
		properties = new HashMap<String, String>();
	}

	/**
	 * Initial level of configuration provided through public methods(setDir,setStudentSet). However, as additional
	 * functionality will be needed other classes can be written as interfaces to other packages for those configurations,
	 * and they will use this method.
	 * 
	 * @param key
	 * @param value
	 * @throws NullPointerException
	 */
	protected void setProperty(String key, String value) throws NullPointerException {
		if (key != null && value != null) {
			properties.put(key, value);
		} else {
			throw new NullPointerException("Tried to enter " + key + " " + value + " as a config property");
		}
	}

	public void setShowIntro(boolean to) {
		properties.put(KEY_SHOW_INTRO, Boolean.toString(to));
	}

	public boolean getShowIntro() {
		if (properties.containsKey(KEY_SHOW_INTRO))
			return Boolean.parseBoolean(properties.get(KEY_SHOW_INTRO));
		return false;
	}

	public void setBaseDir(File toDir) {
		properties.put(KEY_BASE_DIR, toDir.getAbsolutePath());
	}

	public File getBaseDir() {
		if (properties.containsKey(KEY_BASE_DIR)) {
			return new File(properties.get(KEY_BASE_DIR));
		} else
			return null;
	}

	public void setGradingDir(File toDir) {
		properties.put(KEY_GRADES_DIR, toDir.getAbsolutePath());
	}

	public File getGradingDir() {
		if (properties.containsKey(KEY_GRADES_DIR)) {
			return new File(properties.get(KEY_GRADES_DIR));
		} else
			return null;
	}

	public StudentSet getStudentSet() {
		return cachedStudentSet;
	}

	public void setStudentSet(StudentSet students) {
		cachedStudentSet = students;
	}

	public void setStudentFile(File studentFile) {
		this.properties.put(KEY_STUDENTS, studentFile.getAbsolutePath());
	}

	public File getStudentFile() {
		if (properties.containsKey(KEY_STUDENTS)) {
			return new File(properties.get(KEY_STUDENTS));
		}
		return null;
	}

	public HashMap<String, String> getProperties() {
		return properties;
	}
}
