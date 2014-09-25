package edu.gatech.gradeseer.plagiarismdetection;

/**
 * General exception for plagiarism detection task.
 * @author Amirreza Shaban
 */

public class PlagiarismDetectorException extends Exception {
	private static final long serialVersionUID = 8547957752746162529L;

	public PlagiarismDetectorException(String message) {
		super(message);
	}
}
