package edu.gatech.gradeseer.gradingmodel;

/**
 * Abstract class for holding info about individual problems.
 * 
 * 
 * @author Andrey Kurenkov [akurenkov3@gatech.edu]
 */
public class AssignmentProblem {
	private String name;
	private String type;
	private double maxGrade;

	public AssignmentProblem(String name, String type, double max) {
		this.name = name;
		this.type = type;
		this.maxGrade = max;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * 
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @return the maxScore
	 */
	public double getMaxGrade() {
		return maxGrade;
	}

	public int hashCode() {
		return toString().hashCode();
	}

	public String toString() {
		return name + "(" + type + ")";
	}

	public boolean equals(Object other) {
		if (!(other instanceof AssignmentProblem))
			return false;
		AssignmentProblem otherProblem = (AssignmentProblem) other;
		return this.toString().equals(otherProblem.toString());
	}
}
