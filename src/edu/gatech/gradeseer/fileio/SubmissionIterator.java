package edu.gatech.gradeseer.fileio;

import java.util.Iterator;

import edu.gatech.gradeseer.gradingmodel.AssignmentSubmission;

/**
 * Simply an interface specifying an iterator over StudentSubmissions.
 * 
 * Date modified: Sep 14, 2014
 * 
 * @author Andrey Kurenkov [akurenkov3@gatech.edu]
 */
public interface SubmissionIterator extends Iterator<AssignmentSubmission> {

}
