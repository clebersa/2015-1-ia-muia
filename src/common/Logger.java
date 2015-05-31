/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package common;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;

/**
 * A log class to log messages.
 *
 * @author Cleber de Souza Alcântara <cleber.93cd@gmail.com>
 */
public class Logger {

	private static String logFileName = null;
	private static int logLevel = -1;
	private static Logger logger;

	private Logger() {
		try {
			logFileName = Configuration.get(Configuration.LOG_FILE);
			if (logFileName == null) {
				throw new IllegalArgumentException("Log file name not defined.");
			}
			logFileName = Configuration.class.getProtectionDomain().
					getCodeSource().getLocation().getPath() + logFileName;

			String logLevelString = Configuration.get(Configuration.LOG_LEVEL);
			if (logLevelString == null) {
				throw new IllegalArgumentException("Log level not defined.");
			}

			logLevel = Integer.parseInt(logLevelString);

			if (logLevel < 0) {
				throw new IllegalArgumentException("Log level less than 0.");
			}

			Logger.info("Logger configuration loaded.");
			Logger.debug("Log file name = " + logFileName);
			Logger.debug("Log level = " + logLevel);
		} catch (IllegalArgumentException ex) {
			System.out.println("Unable to load the Logger configuration! Error: "
					+ ex.getMessage());
		}
	}

	public static boolean isReady() {
		return (logFileName != null && logLevel >= 0);
	}

	private static synchronized void log(Level level, String message) {
		if (!isReady()) {
			logger = new Logger();
			if (!isReady()) {
				return;
			}
		}

		File logFile = new File(logFileName);
		if (!logFile.exists()) {
			try {
				logFile.createNewFile();
			} catch (IOException ex) {
				System.out.println("Unable to create the log file! Error: "
						+ ex.getMessage());
			}
		}
		FileWriter fileWriter = null;
		try {
			fileWriter = new FileWriter(logFile.getAbsoluteFile(), true);
			String stringLevel;
			int intLevel;
			if (level.intValue() == Level.CONFIG.intValue()) {
				stringLevel = "DEBUG";
				intLevel = 4;
			} else if (level.intValue() == Level.INFO.intValue()) {
				stringLevel = "INFO";
				intLevel = 3;
			} else if (level.intValue() == Level.WARNING.intValue()) {
				stringLevel = "WARN";
				intLevel = 2;
			} else {
				stringLevel = "ERROR";
				intLevel = 1;
			}

			if (intLevel <= logLevel) {
				try {
					StackTraceElement[] stes = Thread.currentThread().getStackTrace();
					String fullClassPath, classPath;
					fullClassPath = stes[3].getClassName();
					classPath = fullClassPath.substring(
							fullClassPath.lastIndexOf('.') + 1, 
							fullClassPath.length());
					
					SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
					
					fileWriter.write("[" + stringLevel + "] " 
							+ sdf.format(new Date()) + " - " + classPath + "." 
							+ stes[3].getMethodName() + " - " + message 
							+ System.lineSeparator());
				} catch (IOException ex) {
					System.out.println("Unable to write in the log file! Error: "
							+ ex.getMessage());
				}
			}
		} catch (IOException ex) {
			System.out.println("Unable to get the log file! Error: "
					+ ex.getMessage());
		} finally {
			if(fileWriter != null){
				try {
					fileWriter.close();
				} catch (IOException ex) {
					System.out.println("Unable to close the file writer! Error: "
							+ ex.getMessage());
				}
			}
		}
	}

	public static void debug(String message) {
		Logger.log(Level.CONFIG, message);
	}

	public static void info(String message) {
		Logger.log(Level.INFO, message);
	}

	public static void warning(String message) {
		Logger.log(Level.WARNING, message);
	}

	public static void error(String message) {
		Logger.log(Level.SEVERE, message);
	}
}
