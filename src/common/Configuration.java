/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package common;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Configuration manager, responsible to get and set configurations from/to a
 * configuration file.
 *
 * @author Cleber de Souza Alcântara <cleber.93cd@gmail.com>
 */
public class Configuration {

	private static final String configFileName = "common/muia.properties";

	public static final String MUIA_HOST_NAME = "muia_host_name";
	public static final String MUIA_HOST_IP = "muia_host_ip";
	public static final String MUIA_HOST_PORT = "muia_host_port";
	
	public static final String CONNECTION_SERVER_PORT = "connection_server_port";
	public static final String CONNECTION_SERVER_IP = "connection_server_ip";
	public static final String LOG_LEVEL = "log_level";
	public static final String LOG_FILE = "log_file";

	public static String get(String configurationName) {
		Properties properties = new Properties();
		InputStream inputStream = null;
		String configValue = null;
		try {
			// The line bellow prints the classpath
			//System.out.println(Configuration.class.getProtectionDomain().getCodeSource().getLocation().getPath());
			inputStream = Configuration.class.getClassLoader()
					.getResourceAsStream(configFileName);
			if (inputStream == null) {
				String logMessage = "Unable to find " + configFileName + ".";
				if (Logger.isReady()) {
					Logger.error(logMessage);
				} else {
					System.out.println(logMessage);
				}
			} else {
				properties.load(inputStream);
				configValue = properties.getProperty(configurationName);
			}
		} catch (IOException ex) {
			String logMessage = "Unable to load " + configFileName +"! Error: "
					+ ex.getMessage();
			if (Logger.isReady()) {
				Logger.error(logMessage);
			} else {
				System.out.println(logMessage);
			}
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException ex) {
					String logMessage = "Unable to close the configuration file "
							+ "input stream! Error: " + ex.getMessage();
					if (Logger.isReady()) {
						Logger.error(logMessage);
					} else {
						System.out.println(logMessage);
					}
				}
			}
		}
		return configValue;
	}
}
