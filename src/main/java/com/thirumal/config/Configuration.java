package com.thirumal.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Properties;
import java.util.logging.Logger;

public final class Configuration extends ConfigurationKey {

	private static final Logger LOGGER = Logger.getLogger(Configuration.class.getName());

	private static String dbName;
	private static String dbVersion;

	private static String modelPackage;
	private static String DaoPackage;

	private static String targetDirectory;

	private static String targetDaoDirectory;
	private static String targetModelDirectory;

	private static String modelFileName;
	private static String daoFileName;
	
	private static String logFileName;

	private static Connection connection;

	private static Configuration instance;

	private Configuration() {

	}
	
	public static Configuration getInstance() {
		if (instance == null) {
			instance = new Configuration();
			try {
				init();
			} catch (Exception exception) {
				LOGGER.severe(exception.getMessage());
			}
		}
		return instance;
	}
	
	public static void init() throws Exception {
		Properties properties = new Properties();
		try {
			properties.load(new FileInputStream(new File(Configuration.CONFIG_PATH)));
		} catch (FileNotFoundException fileNotFoundException) {
			LOGGER.severe(fileNotFoundException.getMessage());
		} catch (IOException ioException) {
			LOGGER.severe(ioException.getMessage());
		}
		modelFileName = properties.getProperty(FILE_NAME_TEMPLATE_MODEL_KEY);
		if(modelFileName == null || modelFileName.isEmpty()) {
			throw new Exception("You must define the driver in the file "+CONFIG_PATH+" with the key "+FILE_NAME_TEMPLATE_MODEL_KEY);
		}
		daoFileName = properties.getProperty(FILE_NAME_TEMPLATE_DAO_KEY);
		
		if(daoFileName == null || daoFileName.isEmpty()){
			throw new Exception("You must define the driver in the file "+CONFIG_PATH+" with the key "+FILE_NAME_TEMPLATE_DAO_KEY);
		}
		DaoPackage = properties.getProperty(PCKG_DAO_KEY);
		if(DaoPackage == null || DaoPackage.isEmpty()){
			throw new Exception("You must define the driver in the file "+CONFIG_PATH+" with the key "+PCKG_DAO_KEY);
		}		
		modelPackage = properties.getProperty(PCKG_MODEL_KEY);		
		if(modelPackage == null || modelPackage.isEmpty()){
			throw new Exception("You must define the driver in the file "+CONFIG_PATH+" with the key "+PCKG_MODEL_KEY);
		}		
		targetModelDirectory = properties.getProperty(FOLDER_MODEL_KEY);		
		if(targetModelDirectory == null || targetModelDirectory.isEmpty()){
			throw new Exception("You must define the driver in the file "+CONFIG_PATH+" with the key "+FOLDER_MODEL_KEY);
		}		
		targetDaoDirectory = properties.getProperty(FOLDER_DAO_KEY);		
		if(targetDaoDirectory == null || targetDaoDirectory.isEmpty()){
			throw new Exception("You must define the driver in the file "+CONFIG_PATH+" with the key "+FOLDER_DAO_KEY);
		}
		targetDirectory	= properties.getProperty(FOLDER_ROOT_KEY);
		if(targetDirectory == null || targetDirectory.isEmpty()){
			throw new Exception("You must define the driver in the file "+CONFIG_PATH+" with the key "+FOLDER_ROOT_KEY);
		}	
		logFileName = properties.getProperty(LOG_FILE_NAME);
		if (logFileName == null || logFileName.isEmpty()) {
			throw new Exception("You must define the log file name in the file "+CONFIG_PATH+" with the key "+LOG_FILE_NAME);
		}
		/* DB Driver */		
		String driver =	properties.getProperty(DRIVER_PROP_KEY);
		if(driver == null || driver.isEmpty()){
			throw new Exception("You must define the driver in the file "+CONFIG_PATH+" with the key "+DRIVER_PROP_KEY);
		}		
		Class.forName(driver).newInstance();
		/* DB URL Scheme */		
		String dbURLScheme = properties.getProperty(SCHEME_PROP_KEY);
		if(dbURLScheme == null || dbURLScheme.isEmpty()){
			throw new Exception("You must define the driver in the file "+CONFIG_PATH+" with the key "+SCHEME_PROP_KEY);
		}	
		/*	DB Host */		
		String dbHost =	properties.getProperty(HOST_PROP_KEY);		
		if(dbHost == null || dbHost.isEmpty()){
			throw new Exception("You must define the driver in the file "+CONFIG_PATH+" with the key "+HOST_PROP_KEY);
		}		
		/*	DB Port */		
		String dbPort =	properties.getProperty(PORT_PROP_KEY);		
		if(dbPort == null || dbPort.isEmpty()){
			throw new Exception("You must define the driver in the file "+CONFIG_PATH+" with the key "+PORT_PROP_KEY);
		}
		/*	DB User */		
		String dbUser =	properties.getProperty(USER_PROP_KEY);		
		if(dbUser == null || dbUser.isEmpty()){
			throw new Exception("You must define the driver in the file "+CONFIG_PATH+" with the key "+USER_PROP_KEY);
		}		
		/*	DB Password */		
		String dbPwd = properties.getProperty(PWD_PROP_KEY);		
		if(dbPwd == null || dbPwd.isEmpty()){
			throw new Exception("You must define the driver in the file "+CONFIG_PATH+" with the key "+PWD_PROP_KEY);
		}		
		/*	DB Name */		
		dbName = properties.getProperty(NAME_PROP_KEY);		
		if(dbName == null || dbName.isEmpty()){
			throw new Exception("You must define the driver in the file "+CONFIG_PATH+" with the key "+NAME_PROP_KEY);
		}				
		//We are building the URL
		StringBuilder dbBuiltURL = new StringBuilder();
		dbBuiltURL.append(dbURLScheme);
		dbBuiltURL.append(dbHost);
		dbBuiltURL.append(":"+dbPort);
		dbBuiltURL.append("/" + dbName);
		LOGGER.info("connection URL: " + dbBuiltURL);
		// instantiate the connection
		connection = DriverManager.getConnection(dbBuiltURL.toString(), dbUser, dbPwd);
		LOGGER.info("connection Object: " + connection);
	}
	
	/**
	 * @return the dbName
	 */
	public static String getDbName() {
		return dbName;
	}

	/**
	 * @return the dbVersion
	 * @throws Exception 
	 */
	public static String getDbVersion() throws Exception {
		if (dbVersion == null) {
			Properties properties = new Properties();
			try {
				properties.load(new FileInputStream(new File(Configuration.CONFIG_PATH)));
			} catch (FileNotFoundException fileNotFoundException) {
				LOGGER.severe(fileNotFoundException.getMessage());
			} catch (IOException ioException) {
				LOGGER.severe(ioException.getMessage());
			}
			String query = properties.getProperty(DB_VERSION_PROP_KEY);
			if (query == null) {
				throw new Exception("Impossible to find the query to get DB Version null");
			}
			LOGGER.info("Query: " + query);
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(query);
			if(resultSet.next()) {
				dbVersion = resultSet.getString("");
			}			
			if (dbVersion == null) {
				dbVersion = "not defined";
			}
			System.out.println("db in config:" + dbVersion);
		}
		return dbVersion;
	}

	/**
	 * @return the pckgModel
	 */
	public static String getModelPackage() {
		return modelPackage;
	}

	/**
	 * @return the pckgDao
	 */
	public static String getDaoPackage() {
		return DaoPackage;
	}

	/**
	 * @return the folderRoot
	 */
	public static String getTargetDirectory() {
		return targetDirectory;
	}

	/**
	 * @return the folderDao
	 */
	public static String getTargetDaoDirectory() {
		return targetDirectory + File.separator + targetDaoDirectory;
	}

	/**
	 * @return the folderModel
	 */
	public static String getTargetModelDirectory() {
		return targetDirectory + File.separator + targetModelDirectory;
	}

	/**
	 * @return the modelFileNameTemplate
	 */
	public static String getModelFileName(String baseFileName) {
		return String.format(modelFileName, baseFileName);
	}

	/**
	 * @return the daoFileNameTemplate
	 */
	public static String getDaoFileName(String baseFileName) {
		return String.format(daoFileName, baseFileName);
	}

	public static String getLogFileName() {
		return logFileName;
	}

	public static void setLogFileName(String logFileName) {
		Configuration.logFileName = logFileName;
	}

	/**
	 * @return the connection
	 */
	public static Connection getConnection() {
		return connection;
	}
	
}
