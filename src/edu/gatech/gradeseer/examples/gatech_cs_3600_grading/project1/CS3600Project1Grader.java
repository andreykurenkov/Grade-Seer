package edu.gatech.gradeseer.examples.gatech_cs_3600_grading.project1;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import edu.gatech.gradeseer.autograding.AssignmentGrader;
import edu.gatech.gradeseer.autograding.AssignmentProblemGrader;
import edu.gatech.gradeseer.examples.gatech_cs_3600_grading.CS3600AssignmentProblemGrader;
import edu.gatech.gradeseer.examples.gatech_cs_3600_grading.TSquareSubmissionIterator;
import edu.gatech.gradeseer.fileio.extensions.TextAndCSVPrinter;
import edu.gatech.gradeseer.gradingmodel.Assignment;
import edu.gatech.gradeseer.gradingmodel.AssignmentProblem;

/**
 * 
 * Date modified: Sep 15, 2014
 * 
 * @author Andrey Kurenkov
 */
public class CS3600Project1Grader {

	public static void main(String[] args) {
		File baseFile = new File("").getAbsoluteFile();
		File project1 = new File(baseFile.getParentFile(), "Project1Grading");
		File gradeDir = new File(project1, "gradeDir");
		File studentsDir = new File(project1, "submissions");

		String[] codeFiles = { "search.py", "searchAgents.py" };
		String[] questions = { "q1", "q2", "q3", "q4", "q5", "q6", "q7", "q8", "extra" };
		double[] maxPoints = { 4, 3, 5, 6, 7, 4, 3, 7, 7 };// TODO: correct
		String[] instructorNames = { "Kurenkov", "Wang", "Clark", "Thomaz" };

		ArrayList<AssignmentProblem> problems = new ArrayList<AssignmentProblem>();
		CS3600AssignmentProblemGrader problemGrader = new CS3600AssignmentProblemGrader(gradeDir, 300.0, true);
		HashMap<AssignmentProblem, AssignmentProblemGrader> map = new HashMap<AssignmentProblem, AssignmentProblemGrader>();
		for (int i = 0; i < questions.length; i++) {
			problems.add(new AssignmentProblem(questions[i], "Pacman problem", maxPoints[i]));
			map.put(problems.get(i), problemGrader);
		}

		Assignment assignment = new Assignment("Project 1", problems, Arrays.asList(codeFiles));
		TSquareSubmissionIterator iterator = new TSquareSubmissionIterator(studentsDir, instructorNames, assignment);
		TextAndCSVPrinter out = new TextAndCSVPrinter(true, new File(project1, "grades.txt"), new File(project1,
				"grades.csv"));
		AssignmentGrader assignmentGrader;
		try {
			assignmentGrader = new AssignmentGrader(assignment, iterator, map, out);
			assignmentGrader.runGrader();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
