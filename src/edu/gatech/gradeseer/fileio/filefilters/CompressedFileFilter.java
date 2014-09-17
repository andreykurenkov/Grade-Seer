package edu.gatech.gradeseer.fileio.filefilters;

import java.io.File;
import java.io.FileFilter;

import edu.gatech.gradeseer.fileio.FileIOUtil;

/**
 * 
 * Date modified: Sep 17, 2014
 * 
 * @author Andrey Kurenkov
 */
public class CompressedFileFilter implements FileFilter {
	@Override
	public boolean accept(File file) {
		return FileIOUtil.isCompressed(file);
	}
}
