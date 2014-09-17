package edu.gatech.gradeseer.examples.gatech_cs_3600_grading.project1;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import edu.gatech.gradeseer.autograding.AssignmentGrader;
import edu.gatech.gradeseer.autograding.AssignmentProblemGrader;
import edu.gatech.gradeseer.examples.gatech_cs_3600_grading.CS3600AssignmentProblemGrader;
import edu.gatech.gradeseer.examples.gatech_cs_3600_grading.TsquareSubmissionIterator;
import edu.gatech.gradeseer.fileio.printerimpl.TextAndCSVPrinter;
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
		File baseFile = new File("/home/andrey/School/2014-2015/TA/Grading/Project 1");
		File gradeDir = new File(baseFile, "Grading Dir");
		File studentsDir = new File(baseFile, "Submissions");

		String[] codeFiles = { "search.py", "searchAgents.py" };
		String[] questions = { "q1", "q2", "q3", "q4", "q5", "q6", "q7", "q8", "extra" };
		double[] maxPoints = { 2, 2, 2, 3, 2, 3, 3, 2, 0 };
		String[] instructorNames = { "Kurenkov", "Wang", "Clark", "Thomaz" };

		ArrayList<AssignmentProblem> problems = new ArrayList<AssignmentProblem>();
		CS3600AssignmentProblemGrader problemGrader = new CS3600AssignmentProblemGrader(gradeDir, 3.0, true);
		HashMap<AssignmentProblem, AssignmentProblemGrader> map = new HashMap<AssignmentProblem, AssignmentProblemGrader>();
		for (int i = 0; i < questions.length; i++) {
			problems.add(new AssignmentProblem(questions[i], "Pacman problem", maxPoints[i]));
			map.put(problems.get(i), problemGrader);
		}

		Assignment assignment = new Assignment("Project 1", problems, Arrays.asList(codeFiles));
		TsquareSubmissionIterator iterator = new TsquareSubmissionIterator(studentsDir, instructorNames, assignment);
		TextAndCSVPrinter out = new TextAndCSVPrinter(true, new File(baseFile, "grades.txt"), new File(baseFile,
				"grades.csv"));
		try {
			AssignmentGrader assignmentGrader = new AssignmentGrader(assignment, iterator, map, out);
			assignmentGrader.runGrader();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
