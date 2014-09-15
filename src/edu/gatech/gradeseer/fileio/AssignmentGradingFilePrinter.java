package edu.gatech.gradeseer.fileio;

import java.io.FileNotFoundException;

import edu.gatech.gradeseer.gradingmodel.Assignment;
import edu.gatech.gradeseer.gradingmodel.AssignmentSubmission;

/**
 * 
 * Date modified: Sep 15, 2014
 * 
 * @author Andrey Kurenkov
 */
public interface AssignmentGradingFilePrinter {

	public void open(Assignment assignment) throws FileNotFoundException;

	public void printStudentSubmission(AssignmentSubmission submission);

	public void printAssignment(Assignment assignment);

	public void close();
}
