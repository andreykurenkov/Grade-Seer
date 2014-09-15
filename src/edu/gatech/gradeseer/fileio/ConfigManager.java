package edu.gatech.gradeseer.fileio;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Scanner;

import edu.gatech.gradeseer.gradingmodel.Configuration;

/**
 * Handles saving and loading of configuration files - the model is seperate from its storage since the former is just a
 * hashmap. Currently mainly used with a default config file, though written to support different files.
 * 
 * @author Andrey Kurenkov
 * @version 1.0
 */
public class ConfigManager {
	private static ConfigManager instance;
	private final static File defFile = new File("config/base.config");

	/**
	 * Singleton getter method
	 * 
	 * @return returns an instance of this class that is always the same
	 */
	public static ConfigManager getInstance() {
		if (instance == null)
			instance = new ConfigManager();
		return instance;
	}

	/**
	 * Empty constructor that just creates an instance.
	 */
	private ConfigManager() {

	}

	/**
	 * Loads the configuration from the default file.
	 * 
	 * @return the configuration read from the standard file.
	 */
	public Configuration load() {
		return load(defFile);
	}

	/**
	 * Loads the configuration from the provided file.
	 * 
	 * @param readFrom
	 *            the file to read from
	 * @return configuration read from that file
	 */
	public Configuration load(File readFrom) {
		HashMap<String, String> properties = new HashMap<String, String>();
		try {
			Scanner scan = new Scanner(readFrom);
			while (scan.hasNextLine()) {
				String line = scan.nextLine();
				int at = line.indexOf(':');
				properties.put(line.substring(0, at), line.substring(at + 1));
			}
		} catch (FileNotFoundException noFile) {
			return null;// Fine - just indicates config is not set
		}
		Configuration parsed = new Configuration(properties);
		if (parsed.getStudentFile() != null)
			parsed.setStudentSet(StudentConfigManager.loadFromFile(parsed.getStudentFile()));
		else
			parsed.setStudentSet(StudentConfigManager.loadFromFile());
		return parsed;

	}

	/**
	 * Saves the given configuration to the default configuration file.
	 * 
	 * @param save
	 *            the configuration to save
	 * @return status string to indicate success or failure (null for success)
	 */
	public String save(Configuration save) {
		return save(save, defFile);
	}

	/**
	 * Saves the configuration to the provided file
	 * 
	 * @param save
	 *            the configuration to save
	 * @param saveTo
	 *            the file to save to
	 * @return status string to indicate success or failure (null for success)
	 */
	public String save(Configuration save, File saveTo) {
		boolean success = false;
		if (save.getStudentFile() == null)
			success = StudentConfigManager.saveToFile(save.getStudentSet());
		else
			success = StudentConfigManager.saveToFile(save.getStudentSet(), save.getStudentFile());
		if (!success) {
			return "ConfigManager-saveToFile error:could not save students";
		}
		success = false;
		try {
			PrintStream stream;
			stream = new PrintStream(saveTo);
			for (java.util.Map.Entry<String, String> entry : save.getProperties().entrySet())
				stream.print(entry.getKey() + ":" + entry.getValue() + "\n");
			success = true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return "Configuration load error - File not found";
		}
		return null;
	}

}
