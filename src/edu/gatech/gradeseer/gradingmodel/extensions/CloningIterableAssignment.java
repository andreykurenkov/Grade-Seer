package edu.gatech.gradeseer.gradingmodel.extensions;

import java.util.Iterator;
import java.util.List;

import edu.gatech.gradeseer.fileio.extensions.CloneableSubmissionIterator;
import edu.gatech.gradeseer.gradingmodel.AssignmentProblem;
import edu.gatech.gradeseer.gradingmodel.AssignmentSubmission;

/**
 * 
 * Date modified: Sep 15, 2014
 * 
 * TODO: should this use a factory instead of cloneable? Maybe...
 * 
 * @author Andrey Kurenkov
 */
public class CloningIterableAssignment<E extends CloneableSubmissionIterator> extends IterableAssignment {
	protected E cloneableIterator;

	/**
	 * 
	 * @param name
	 * @param problems
	 * @param fileNames
	 */
	public CloningIterableAssignment(String name, List<AssignmentProblem> problems, List<String> fileNames,
			E cloneableInstance) {
		super(name, problems, fileNames);
		this.cloneableIterator = cloneableInstance;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Iterable#iterator()
	 */
	@Override
	public Iterator<AssignmentSubmission> iterator() {
		return (Iterator<AssignmentSubmission>) cloneableIterator.makeClone();
	}

}
