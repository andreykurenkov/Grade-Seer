package edu.gatech.gradeseer.gradingmodel.assignmentextensions;

import java.util.List;

import edu.gatech.gradeseer.gradingmodel.Assignment;
import edu.gatech.gradeseer.gradingmodel.AssignmentProblem;
import edu.gatech.gradeseer.gradingmodel.AssignmentSubmission;

/**
 * 
 * Date modified: Sep 15, 2014
 * 
 * 
 * @author Andrey Kurenkov
 */
public abstract class IterableAssignment extends Assignment implements Iterable<AssignmentSubmission> {

	/**
	 * 
	 * @param name
	 * @param problems
	 * @param fileNames
	 */
	public IterableAssignment(String name, List<AssignmentProblem> problems, List<String> fileNames) {
		super(name, problems, fileNames);
	}

}
