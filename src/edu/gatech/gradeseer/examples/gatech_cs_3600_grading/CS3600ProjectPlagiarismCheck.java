package edu.gatech.gradeseer.examples.gatech_cs_3600_grading;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import edu.gatech.gradeseer.gradingmodel.Assignment;
import edu.gatech.gradeseer.gradingmodel.AssignmentProblem;
import edu.gatech.gradeseer.gradingmodel.AssignmentSubmission;
import edu.gatech.gradeseer.plagiarismdetection.PlagiarismDetector;
import edu.gatech.gradeseer.plagiarismdetection.PlagiarismDetectorException;

/**
 * An example plagiarism check using MOSS detector.
 * 
 * @author Amirreza Shaban
 */
public class CS3600ProjectPlagiarismCheck {
	public static void main(String[] args) {
		File baseFile = new File("/home/andrey/School/2014-2015/Spring/TA/Grading/Project 2");
		File studentsDir = new File(baseFile, "Submissions");
		File gradingDir = new File(baseFile, "Grading Dir");

		String[] codeFiles = { "BinaryCSP.py" };
		String[] instructorNames = { "Kurenkov", "Rehg", "Fletcher", "Siu", "Zhefan", "Jing" };
		ArrayList<AssignmentProblem> problems = new ArrayList<AssignmentProblem>();

		Assignment assignment = new Assignment("Project 2", problems, Arrays.asList(codeFiles));
		TsquareSubmissionIterator iterator = new TsquareSubmissionIterator(studentsDir, instructorNames, assignment);

		PlagiarismDetector detector = PlagiarismDetector.getInstance("moss", "python 600058214 100");

		File file1 = new File(gradingDir, "BinaryCSP.py");
		detector.addBaseFile(file1);
		while (iterator.hasNext()) {
			AssignmentSubmission as = iterator.next();
			Collection<File> files = as.getSubmissionFiles().values();
			System.out.println("Adding " + as.getStudent().getName() + " files.." + files.size());
			if (files.size() > 0) {
				detector.addStudentFiles(as.getStudent().getName().replace(' ', '_'), files.toArray(new File[0]));
			}
		}
		File oldStudentsDir = new File(baseFile, "Old Submissions");
		iterator = new TsquareSubmissionIterator(oldStudentsDir, instructorNames, assignment);

		while (iterator.hasNext()) {
			AssignmentSubmission as = iterator.next();
			Collection<File> files = as.getSubmissionFiles().values();
			if (files.size() > 0)
				detector.addStudentFiles("Old" + as.getStudent().getName().replace(' ', '_'), files.toArray(new File[0]));
		}

		// File internetSubmissions = new File(baseFile, "Internet Submissions");
		// for (File sub : internetSubmissions.listFiles()) {
		// detector.addStudentFiles("InternetCode" + sub.getName(), sub.listFiles());
		// }

		try {
			System.out.println("Generating report..");
			String report = detector.run();
			System.out.println(report);
		} catch (PlagiarismDetectorException e) {
			e.printStackTrace();
		}
	}
}
