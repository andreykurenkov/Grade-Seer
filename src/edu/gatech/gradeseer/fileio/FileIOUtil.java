package edu.gatech.gradeseer.fileio;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

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
}
