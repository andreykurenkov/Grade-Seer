package edu.gatech.gradeseer.fileio;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

import edu.gatech.gradeseer.gradingmodel.Student;
import edu.gatech.gradeseer.gradingmodel.StudentSet;

/**
 * A counterpart of ConfigManager that serializes and de-serializes a StudentSet
 * 
 * @author Andrey Kurenkov
 * @version 1.0
 */
public class StudentConfigManager {
	private final static File defStudentsFile = new File("config/students.config");

	/**
	 * Empty constructor
	 */
	private StudentConfigManager() {
	}

	/**
	 * Loads from default save file.
	 * 
	 * @return loaded student set
	 */
	protected static StudentSet loadFromFile() {
		return loadFromFile(defStudentsFile);
	}

	/**
	 * Loads from given file
	 * 
	 * @param loadFrom
	 *            the file to load from
	 * @return the set loaded
	 */
	protected static StudentSet loadFromFile(File loadFrom) {
		ArrayList<Student> set = new ArrayList<Student>();
		Scanner scan = null;
		try {
			scan = new Scanner(loadFrom);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
		while (scan.hasNext()) {
			String lastName = scan.next();
			String firstName = scan.next();
			String id = scan.next().trim();
			String name = lastName + " " + firstName.trim();
			set.add(new Student(name, id));

		}
		Collections.sort(set);
		return new StudentSet(set.toArray(new Student[set.size()]));
	}

	/**
	 * Saves to default file.
	 * 
	 * @param save
	 *            StudentSet to save.
	 * @return true if successfully saved
	 */
	protected static boolean saveToFile(StudentSet save) {
		return saveToFile(save, defStudentsFile);
	}

	/**
	 * Saves to given file
	 * 
	 * @param save
	 *            the StudentSet to save
	 * @param saveTo
	 *            the file to save to
	 * @return true if successfully saved
	 */
	protected static boolean saveToFile(StudentSet save, File saveTo) {
		boolean success = false;
		try {
			PrintStream stream;
			stream = new PrintStream(saveTo);
			for (Student student : save.getSet())
				stream.print(student.getName() + "\t" + student.getID() + "\n");
			success = true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return success;
	}

}
