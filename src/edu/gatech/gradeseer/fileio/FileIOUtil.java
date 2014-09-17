package edu.gatech.gradeseer.fileio;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import edu.gatech.gradeseer.fileio.filefilters.CompressedFileFilter;
import edu.gatech.gradeseer.fileio.filefilters.DirectoryFilter;

/**
 * 
 * 
 * Date modified: Sep 15, 2014
 * 
 * @author Andrey Kurenkov [akurenkov3@gatech.edu]
 */
public class FileIOUtil {

	/**
	 * 
	 * 
	 * @param from
	 * @param to
	 * @return
	 * @throws IOException
	 */
	public static File copyTo(File from, File to) throws IOException {
		if (from.exists() && from.canRead()) {
			BufferedInputStream in = new BufferedInputStream(new FileInputStream(from));
			BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(to));
			int c;

			while ((c = in.read()) != -1)
				out.write(c);

			in.close();
			out.close();
			return to;
		}
		return null;
	}

	/**
	 * Decompress a Tar.gz or Zip compressed file
	 * 
	 * @param compressed
	 *            The compressed file.
	 * @return True if succesfully decompressed
	 * @throws IOException
	 *             Can be thrown by decomperession process
	 */
	public static boolean decompress(File compressed, File decompressTo) throws IOException {
		if (compressed.getName().endsWith(".rar")) {
			throw new UnsupportedOperationException("Cannot extract rar yet.");
		}
		ProcessBuilder builder = null;
		if (compressed != null && compressed.getName().endsWith(".zip")) {
			// spawn unzip prpocesses
			builder = new ProcessBuilder("jar", "xf", compressed.getAbsolutePath());
		} else if (compressed != null && compressed.getName().endsWith(".tar.gz")) {
			// spawn untar prpocesses
			builder = new ProcessBuilder("tar", "zxvf", compressed.getAbsolutePath());
		}
		if (builder == null)
			return false;

		builder.directory(decompressTo);
		Process extract = builder.start();
		boolean done = false;
		while (!done) {
			try {
				extract.exitValue();
				done = true;
			} catch (Exception e) {
				done = false;
			}
		}
		return true;
	}

	public static boolean isCompressed(File file) {
		return file.getName().endsWith(".zip") || file.getName().endsWith(".tar.gz") || file.getName().endsWith(".rar");
	}

	/**
	 * Utility method to scan a directory and all its subdirectories for files that match a filter. This method attempts to
	 * avoid loops by checking for link/schortcut folder files, but this has not been thoroughly tested as of yet. TODO: just
	 * do graph search instead of checking link?
	 * 
	 * @param startFrom
	 *            The directory to start in (will scan this and all subdirectories as BFS/DFS)
	 * @param filter
	 *            The filter to accept/deny files
	 * @param DFS
	 *            Whether to search with DFS or BFS. Mainly significant for memory.
	 * @return Array of all files found that match the filter
	 */
	public static List<File> recursiveFileFind(File startFrom, FileFilter filter, boolean DFS, boolean decompress) {
		if (startFrom == null || filter == null || !startFrom.isDirectory())
			return null;
		FileFilter directoryFilter = new DirectoryFilter();
		FileFilter compressedFilter = new CompressedFileFilter();
		LinkedList<File> folders = new LinkedList<File>();
		ArrayList<File> files = new ArrayList<File>();
		folders.add(startFrom);
		while (!folders.isEmpty()) {
			File folder = null;
			if (DFS)
				folder = folders.removeLast();
			else
				folder = folders.removeFirst();
			if (decompress) {
				for (File file : folder.listFiles(compressedFilter)) {
					try {
						decompress(file, folder);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			for (File file : folder.listFiles(filter)) {
				files.add(file);
			}
			for (File file : folder.listFiles(directoryFilter)) {
				folders.add(file);
			}

		}
		return files;
	}

	/**
	 * Method testing whether a file is a shortcut/soft link.
	 * 
	 * @param file
	 *            file to check
	 * @return true if shortcut/symlink
	 * @throws IOException
	 */
	public static boolean isSymlink(File file) throws IOException {
		if (file == null)
			throw new NullPointerException("File must not be null");
		File canon;
		if (file.getParent() == null) {
			canon = file;
		} else {
			File canonDir = file.getParentFile().getCanonicalFile();
			canon = new File(canonDir, file.getName());
		}
		boolean testOne = !canon.getCanonicalFile().equals(canon.getAbsoluteFile());
		String path1 = file.getAbsoluteFile().getParentFile().getCanonicalPath() + File.separatorChar + file.getName();
		String path2 = file.getAbsoluteFile().getCanonicalPath();
		boolean testTwo = !(path1.equals(path2));
		return testOne && testTwo;
	}
}
