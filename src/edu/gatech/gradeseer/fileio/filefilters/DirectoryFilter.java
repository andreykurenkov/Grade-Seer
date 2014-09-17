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
public class DirectoryFilter implements FileFilter {

	@Override
	public boolean accept(File file) {
		try {
			return file != null && file.isDirectory() && !file.isHidden() && !FileIOUtil.isSymlink(file);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
}