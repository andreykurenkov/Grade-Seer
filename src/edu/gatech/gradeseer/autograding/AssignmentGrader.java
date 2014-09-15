package edu.gatech.gradeseer.autograding;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import edu.gatech.gradeseer.fileio.AssignmentGradingFilePrinter;
import edu.gatech.gradeseer.fileio.SubmissionIterator;
import edu.gatech.gradeseer.gradingmodel.Assignment;
import edu.gatech.gradeseer.gradingmodel.AssignmentProblem;
import edu.gatech.gradeseer.gradingmodel.AssignmentSubmission;
import edu.gatech.gradeseer.gradingmodel.extensions.IterableAssignment;

/**
 * Flexible grader for a large number of student submissions.
 * 
 * TODO file writing writing should really be done as an optional method in GradeReport, not here
 * 
 * @author Andrey Kurenkov[akurenkov3@gatech.edu]
 */
public class AssignmentGrader {
	private IterableAssignment assignment;
	private Map<AssignmentProblem, AssignmentProblemGrader> graders;
	private AssignmentGradingFilePrinter printer;

	private double totalGrade;
	private int nonZeroCount, totalCount;

	private HashMap<AssignmentProblem, Double> questionTotals;
	private HashMap<AssignmentProblem, Double> questionAverages;
	private HashMap<AssignmentProblem, Double> questionStandardDevs;
	private double gradeStandardDev;
	private double gradeAverage;

	/**
	 * 
	 * 
	 * @param toGrade
	 * @param iterator
	 * @param problemGraders
	 * @param printer
	 * @throws IOException
	 */
	public AssignmentGrader(Assignment toGrade, final SubmissionIterator iterator,
			Map<AssignmentProblem, AssignmentProblemGrader> problemGraders, AssignmentGradingFilePrinter printer)
			throws IOException {
		this(new IterableAssignment(toGrade.getName(), toGrade.getProblems(), toGrade.getFileNames()) {

			@Override
			public Iterator<AssignmentSubmission> iterator() {
				return iterator;
			}

		}, problemGraders, printer);
	}

	/**
	 * 
	 * 
	 * @param toGrade
	 * @param iterator
	 * @param problemGraders
	 * @param printer
	 * @throws IOException
	 */
	public AssignmentGrader(IterableAssignment toGrade, Map<AssignmentProblem, AssignmentProblemGrader> problemGraders,
			AssignmentGradingFilePrinter printer) throws IOException {
		this.assignment = toGrade;
		this.graders = problemGraders;
		this.printer = printer;

		questionTotals = new HashMap<AssignmentProblem, Double>();
		for (AssignmentProblem problem : assignment.getProblems())
			questionTotals.put(problem, 0.0);
		totalGrade = 0;
		nonZeroCount = 0;
		questionAverages = new HashMap<AssignmentProblem, Double>();
		questionStandardDevs = new HashMap<AssignmentProblem, Double>();
	}

	public void runGrader() throws FileNotFoundException {
		printer.open(assignment);
		for (AssignmentSubmission submission : assignment) {
			for (AssignmentProblem problem : assignment.getProblems()) {
				AssignmentProblemGrader grader = graders.get(problem);
				double grade = grader.gradeQuestion(assignment, problem, submission);
				submission.addGrade(problem, grade);
				questionTotals.put(problem, questionTotals.get(problem) + grade);
			}
			if (submission.getTotal() != 0)
				nonZeroCount += 1;
			printer.printStudentSubmission(submission);
		}
		printer.close();

		for (AssignmentProblem question : assignment.getProblems()) {
			questionAverages.put(question, questionTotals.get(question) / nonZeroCount);
			questionStandardDevs.put(question, 0.0);
		}

		gradeAverage = totalGrade / nonZeroCount;

		for (AssignmentProblem question : assignment.getProblems()) {
			questionAverages.put(question, questionTotals.get(question) / nonZeroCount);
			questionStandardDevs.put(question, 0.0);
		}

		gradeStandardDev = 0;
		for (AssignmentSubmission submission : assignment.getSubmissions()) {
			gradeStandardDev += Math.pow((submission.getTotal() - gradeAverage), 2);
			for (AssignmentProblem question : assignment.getProblems()) {
				double dev = Math.pow(submission.getGrades().get(question) - questionAverages.get(question), 2);
				questionStandardDevs.put(question, questionStandardDevs.get(question) + dev);
			}
		}
		gradeStandardDev = Math.sqrt(gradeStandardDev / nonZeroCount);
		for (AssignmentProblem question : assignment.getProblems()) {
			questionStandardDevs.put(question, Math.sqrt(questionStandardDevs.get(question) / nonZeroCount));
		}
	}

	public HashMap<AssignmentProblem, Double> getQuestionAverages() {
		return questionAverages;
	}

	public HashMap<AssignmentProblem, Double> getQuestionStandardDevs() {
		return questionStandardDevs;
	}

	public double getGradeStandardDev() {
		return gradeStandardDev;
	}

	public double getGradeAverage() {
		return gradeAverage;
	}
}
