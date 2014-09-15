package edu.gatech.gradeseer.gradingmodel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Scanner;

/**
 * A simple organized collection of students.
 * 
 * @author Andrey Kurenkov
 * 
 */
public class StudentSet implements Iterable<Student> {
	private HashMap<String, String> map;
	private ArrayList<Student> set;

	/**
	 * Given an array of students, this builds a map and stores the array in one class
	 * 
	 * @param students
	 *            array of students
	 */
	public StudentSet(Student[] students) {
		map = new HashMap<String, String>();
		set = new ArrayList<Student>();
		for (int i = 0; i < students.length; i++) {
			map.put(students[i].getName(), students[i].getID());
			set.add(students[i]);
		}
	}

	/**
	 * Instantiates empty
	 */
	public StudentSet() {
		set = new ArrayList<Student>();
		map = new HashMap<String, String>();
	}

	/**
	 * Receives 3 by n String array with first name|last name|id for each student in array
	 * 
	 * @param students
	 *            the string array
	 */
	public StudentSet(String[][] in) {
		this();
		int numStudents = in[0].length;
		for (int i = 0; i < numStudents; i++) {
			// Last, First | id
			String name = in[1][i].trim() + ", " + in[0][i].trim();
			Student another = new Student(name, in[2][i]);
			map.put(in[2][i], name);
			set.add(another);
		}
	}

	/**
	 * Parses student directly from normal text usually tsquare section listings).
	 * 
	 * @param from
	 *            the string to parse
	 * @return a set of the students found in the text
	 */
	public static StudentSet parseFromString(String from) {
		ArrayList<Student> set = new ArrayList<Student>();
		Scanner scanner = new Scanner(from);
		while (scanner.hasNextLine()) {
			String line = scanner.nextLine();
			Scanner scan = new Scanner(line);
			String lastName = scan.next();
			String firstName = scan.next();
			String id = scan.next().trim();
			String name = lastName + " " + firstName.trim();
			set.add(new Student(name, id));
			scan.close();
		}
		Collections.sort(set);
		scanner.close();
		return new StudentSet(set.toArray(new Student[set.size()]));
	}

	/**
	 * Returns an array of the students in set.
	 * 
	 * @return array of all the students
	 */
	public Student[] getSet() {
		return set.toArray(new Student[0]);
	}

	@Override
	public Iterator<Student> iterator() {
		return set.iterator();
	}
}
