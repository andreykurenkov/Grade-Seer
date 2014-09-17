package edu.gatech.gradeseer.fileio.filefilters;

import java.io.File;
import java.io.FileFilter;
import java.util.List;

/**
 * 
 * Date modified: Sep 17, 2014
 * 
 * @author Andrey Kurenkov
 */
public class FileNameFilter implements FileFilter {
	private List<String> names;

	/**
	 * 
	 * @param names
	 */
	public FileNameFilter(List<String> names) {
		super();
		this.names = names;
	}

	@Override
	public boolean accept(File file) {
		return names.contains(file.getName());
	}

}
