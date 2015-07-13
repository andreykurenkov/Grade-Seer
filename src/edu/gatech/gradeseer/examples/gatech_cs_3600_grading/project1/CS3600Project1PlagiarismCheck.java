package edu.gatech.gradeseer.examples.gatech_cs_3600_grading.project1;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import edu.gatech.gradeseer.examples.gatech_cs_3600_grading.TsquareSubmissionIterator;
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
public class CS3600Project1PlagiarismCheck {
	public static void main(String[] args) {
		File baseFile = new File("/Users/Haamoon/Desktop/I/University/AI_TA/Project1/Grades/Project 1");
		File studentsDir = new File(baseFile, "Submissions");

		String[] codeFiles = { "search.py", "searchAgents.py" };
		String[] instructorNames = { "Kurenkov", "Wang", "Clark", "Thomaz", "Amirreza" };

		ArrayList<AssignmentProblem> problems = new ArrayList<AssignmentProblem>();

		Assignment assignment = new Assignment("Project 1", problems, Arrays.asList(codeFiles));
		TsquareSubmissionIterator iterator = new TsquareSubmissionIterator(studentsDir, instructorNames, assignment);

		PlagiarismDetector detector = PlagiarismDetector.getInstance("moss", "python 600058214 100");

		File file1 = new File("/Users/Haamoon/Desktop/I/University/AI_TA/Project1/Grades/Project 1/BaseFiles/search.py");
		File file2 = new File(
				"/Users/Haamoon/Desktop/I/University/AI_TA/Project1/Grades/Project 1/BaseFiles/searchAgents.py");

		detector.addBaseFile(file1);
		detector.addBaseFile(file2);

		while (iterator.hasNext()) {
			AssignmentSubmission as = iterator.next();
			Collection<File> files = as.getSubmissionFiles().values();
			System.out.println("Adding " + as.getStudent().getName() + " files..");
			detector.addStudentFiles(as.getStudent().getName(), files.toArray(new File[0]));
		}

		try {
			System.out.println("Generating report..");
			String report = detector.run();
			System.out.println(report);
		} catch (PlagiarismDetectorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
