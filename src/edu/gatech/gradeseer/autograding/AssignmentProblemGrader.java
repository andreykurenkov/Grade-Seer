package edu.gatech.gradeseer.autograding;

import edu.gatech.gradeseer.gradingmodel.Assignment;
import edu.gatech.gradeseer.gradingmodel.AssignmentProblem;
import edu.gatech.gradeseer.gradingmodel.AssignmentSubmission;

/**
 * 
 * 
 * @author Andrey Kurenkov
 */
public interface AssignmentProblemGrader {

	public abstract double gradeQuestion(Assignment assignment, AssignmentProblem problem, AssignmentSubmission submission);
}
