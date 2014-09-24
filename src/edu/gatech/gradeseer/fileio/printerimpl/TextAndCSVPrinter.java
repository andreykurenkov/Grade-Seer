package edu.gatech.gradeseer.fileio.printerimpl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Arrays;

import edu.gatech.gradeseer.fileio.AssignmentGradingFilePrinter;
import edu.gatech.gradeseer.gradingmodel.Assignment;
import edu.gatech.gradeseer.gradingmodel.AssignmentProblem;
import edu.gatech.gradeseer.gradingmodel.AssignmentSubmission;

/**
 * 
 * @author Andrey Kurenkov [akurenkov3@gatech.edu]
 */
public class TextAndCSVPrinter implements AssignmentGradingFilePrinter {
	protected boolean outputToStd;
	protected File txtOutput, csvOutput;
	protected PrintWriter txtWriter, csvWriter;
	protected boolean printToTXT, printToCSV;
	private Assignment forAssignment;

	public TextAndCSVPrinter(boolean outputToStd, File txtOutput, File csvOutput) {
		this.outputToStd = outputToStd;
		this.txtOutput = txtOutput;
		this.csvOutput = csvOutput;
		printToTXT = txtOutput != null;
		printToCSV = csvOutput != null;
	}

	public void printToCSV(String toPrint) {
		if (printToCSV)
			csvWriter.print(toPrint);
	}

	public void printToTxt(String print) {
		if (outputToStd)
			System.out.print(print);
		if (printToTXT)
			txtWriter.write(print);
	}

	public void println(String print) {
		printToTxt(print + "\n");
	}

	protected String formatOuputToPlaintext(AssignmentSubmission toFormat) {
		StringBuilder build = new StringBuilder();
		build.append("Grading student " + toFormat.getStudent().getName() + "\n");

		for (AssignmentProblem problem : forAssignment.getProblems()) { // TODO error check for it being there
			build.append("\tGrade for " + problem.getName() + ": " + toFormat.getGrades().get(problem) + "/"
					+ problem.getMaxGrade() + "\n");
			for (String comment : toFormat.getProblemComments(problem))
				build.append("\t\t" + comment + "\n");
		}
		for (String comment : toFormat.getComments())
			build.append("\t" + comment + "\n");
		return build.toString();
	}

	protected String formatOuputToCSV(AssignmentSubmission toFormat) {
		StringBuilder build = new StringBuilder();
		build.append("\n" + toFormat.getStudent().getName().replace(',', ' ') + ",");
		for (AssignmentProblem problem : forAssignment.getProblems()) { // TODO error check for it being there
			build.append(toFormat.getGrades().get(problem));
			build.append(",");
		}
		build.append(toFormat.getTotal() + ",");
		for (AssignmentProblem problem : forAssignment.getProblems()) { // TODO error check for it being there
			build.append(Arrays.toString(toFormat.getProblemComments(problem).toArray()).replace(',', '|')
					.replaceAll("[|]", ""));
			build.append(",");
		}
		build.append(Arrays.toString(toFormat.getComments().toArray()).replace(',', '|').replaceAll("[|]", ""));
		return build.toString();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.gatech.gradeseer.fileio.AssignmentGradingFilePrinter#open()
	 */
	@Override
	public void open(Assignment assignment) throws FileNotFoundException {
		this.forAssignment = assignment;
		if (printToTXT) {
			txtWriter = new PrintWriter(txtOutput);
		}
		if (printToCSV) {
			csvWriter = new PrintWriter(csvOutput);
			String questionsStr = Arrays.toString(assignment.getQuestionNames().toArray());
			questionsStr = questionsStr.substring(1, questionsStr.length() - 1);
			String questionComments = questionsStr.replaceAll(",", "Comments,") + "Comments";
			csvWriter.write("Student," + questionsStr + ",Total Grade," + questionComments + ",Overall Comments");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * edu.gatech.gradeseer.fileio.AssignmentGradingFilePrinter#printStudentSubmission(edu.gatech.gradeseer.gradingmodel.
	 * StudentSubmission)
	 */
	@Override
	public void printStudentSubmission(AssignmentSubmission submission) {
		printToTxt(formatOuputToPlaintext(submission));
		printToCSV(formatOuputToCSV(submission));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * edu.gatech.gradeseer.fileio.AssignmentGradingFilePrinter#printAssignment(edu.gatech.gradeseer.gradingmodel.Assignment)
	 */
	@Override
	public void printAssignment(Assignment assignment) {
		printToTxt("Report for: " + assignment.getName() + "\n");
		for (AssignmentSubmission submission : assignment.getSubmissions()) {
			printToTxt(formatOuputToPlaintext(submission));
			printToCSV(formatOuputToCSV(submission));
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.gatech.gradeseer.fileio.AssignmentGradingFilePrinter#close()
	 */
	@Override
	public void close() {
		if (printToCSV)
			csvWriter.close();
		if (printToTXT)
			txtWriter.close();
	}
}
