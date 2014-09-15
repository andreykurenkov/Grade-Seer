package edu.gatech.gradeseer.plagiarismdetection;

import it.zielke.moji.MossException;
import it.zielke.moji.SocketClient;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Set;
import java.util.Vector;

import edu.gatech.gradeseer.fileio.VirtualFile;

//http://zielke.it/moji/
public class MOSS extends PlagiarismDetector {
	private String language;
	private String userID;
	private long optM;

	public MOSS(String args) {
		String[] tokens = args.split(" ");
		if (tokens.length < 2) {
			throw new IllegalArgumentException("MOSS args should be in \"lang userid [maxNum]\" format!");
		}
		this.language = tokens[0];
		this.userID = tokens[1];
		if (tokens.length > 2) {
			this.optM = Long.parseLong(tokens[2]);
		}
	}

	/* This part is taken from MOJI example, see MOJI project for more details */
	@Override
	public String run() throws PlagiarismDetectorException {
		try {
			// a list of students' source code files located in the prepared
			// directory.
			File[] sourceFiles = this.createDirectories();

			// get a new socket client to communicate with the MOSS server.
			SocketClient socketClient = new SocketClient();

			// set your MOSS user ID
			socketClient.setUserID(this.userID);

			// set the programming language of all student source codes
			socketClient.setLanguage(this.language);

			// Set the maximum amount of times a source code passage may appear before
			// it is ignored and treated as base code
			socketClient.setOptM(this.optM);

			// initialize connection and send parameters
			socketClient.run();

			// upload all base files
			for (File f : this.baseFiles) {
				socketClient.uploadBaseFile(f);
			}

			// upload all source files of students
			for (File f : sourceFiles) {
				socketClient.uploadFile(f);
			}

			// finished uploading, tell server to check files
			socketClient.sendQuery();

			// get URL with MOSS results and do something with it
			URL results = socketClient.getResultURL();

			return "Results available at " + results.toString();

		} catch (UnknownHostException e) {
			throw new PlagiarismDetectorException(e.getMessage());
		} catch (MossException e) {
			throw new PlagiarismDetectorException(e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
			throw new PlagiarismDetectorException(e.getMessage());
		}
	}

	private File[] createDirectories() throws IOException {
		Vector<VirtualFile> sourceFiles = new Vector<VirtualFile>();

		Set<String> keySet = this.sourceMap.keySet();
		for (String name : keySet) {
			File[] files = this.sourceMap.get(name);
			for (File file : files) {
				sourceFiles.add(new VirtualFile(name + "/" + file.getName(), file));
			}
		}

		return sourceFiles.toArray(new VirtualFile[sourceFiles.size()]);
	}
}
