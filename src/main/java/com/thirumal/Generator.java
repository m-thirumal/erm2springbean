package com.thirumal;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

import com.thirumal.config.Configuration;
import com.thirumal.entities.Entity;
import com.thirumal.extractor.PostgreSQLDBExtractor;
import com.thirumal.render.BaseClassRender;
import com.thirumal.render.DaoClassRender;
import com.thirumal.render.ModelClassRender;
import com.thirumal.utility.ERM2BeansHelper;

/**
 * @author Thirumal
 *
 */
public class Generator {
	
	private	static	final	Logger	LOGGER		=	Logger.getLogger(Generator.class.getName());
	
    public static void main( String[] args ) throws SQLException    {
        LOGGER.info("Starting generator");
		FileHandler 	fileHandler 	= 	null;
		String			logPath			=	Configuration.getTargetDirectory() + File.separator + Configuration.getLogFileName();
		try {
			fileHandler = new FileHandler(logPath);
		} catch (SecurityException ex1) {
			LOGGER.severe("Impossible to create the log file "+logPath+" Message:"+ex1.getMessage());
		} catch (IOException ex2) {
			LOGGER.severe("Impossible to create the log file "+logPath+" Message:"+ex2.getMessage());
		} catch (Exception ex3) {
			LOGGER.severe("Impossible to create the log file "+logPath+" Message:"+ex3.getMessage());
		}
		//We saving the log
		if(fileHandler != null){
			LOGGER.addHandler(fileHandler);
		}
		LOGGER.addHandler(new ConsoleHandler());
		LOGGER.info("DB name: "+ Configuration.getDbName());
		LOGGER.info("Extracting entities");		
		@SuppressWarnings("resource")
		Scanner scanner = new Scanner(System.in);
		/*System.out.println("Enter Y for all schema and N to specify schema");
		String schema = scanner.next();
		if (schema.equalsIgnoreCase("Y")) {
			Statement  statement =  Configuration.getInstance().getConnection().createStatement();
			ResultSet resultSet = statement.executeQuery("select schema_name from information_schema.schemata");
			while(resultSet.next()) {
				generator(resultSet.getString("schema_name"));
			}
		} else {*/
			System.out.println("Enter the schema Name: ");
			generator(scanner.next());
	//	}
	
    }
    
    static void generator(String schemaName) {
    		PostgreSQLDBExtractor dbExtractor		= 	new PostgreSQLDBExtractor(Configuration.getInstance());
		ArrayList<Entity> entities	=	null;		
		try {
			entities = (ArrayList<Entity>) dbExtractor.getEntities(schemaName);
		} catch (Exception ex) {
			LOGGER.severe(ex.getMessage());
		}		
		LOGGER.info(entities.size()+" entities extracted");		
		LOGGER.info("Saving entities model, dao and queries");		
		String 			fileName		=	null;
		String 			className		=	null;
		String 			targetDirectory	= 	Configuration.getTargetDirectory();
		BaseClassRender classRender		=	null;
		String 			classContent	=	null;
		String 			entityPckg		=	null;		
		LOGGER.info("Creating output directory: " + targetDirectory);		
		try {
			ERM2BeansHelper.createDirectory(targetDirectory);
		} catch (Exception e) {
			LOGGER.info("Impossible to create the root folder: "+targetDirectory);
		}		
		targetDirectory	= Configuration.getTargetModelDirectory();
		entityPckg 		= Configuration.getModelPackage();		
		LOGGER.info("Saving entities model at "+ targetDirectory);	
		
		/* Writing model*/
		for(Entity entity : entities){			
			//Setting pckg from the Configuration
			entity.setModelPackage(entityPckg);			
			className 		= 	entity.getName();
			fileName		    =	Configuration.getModelFileName(className) + ".java";
			classRender		=	new ModelClassRender(entity, Configuration.getInstance());
			try {
				classContent = classRender.render();
			} catch (Exception ex) {
				LOGGER.severe(ex.getMessage());
				break;
			}
			try {
				LOGGER.info("Create model " + className + ". Target path: " + targetDirectory + File.separator + fileName);
				ERM2BeansHelper.writeFile(classContent, targetDirectory, fileName, false);
			} catch (Exception ex) {
				LOGGER.severe("Impossible to create the Model "+className+". Exception message: "+ex.getMessage());
				break;
			}
			
		}
		/* Writing DAOs */
		
		targetDirectory = 	Configuration.getTargetDaoDirectory();
		entityPckg 		= 	Configuration.getDaoPackage();
		classContent	=	new String();
		LOGGER.info("Saving entities DAO at "+ targetDirectory);
		ERM2BeansHelper.clearQueries(Configuration.getTargetDirectory());
		for(Entity entity : entities) {
			//Setting pckg from the Configuration
			entity.setDaoPackage(entityPckg);
			entity.removeInterfaces();
			entity.addInterface("GenericDao<" + entity.getName() + " , Integer, String>");
			className 		= 	entity.getName();
			fileName		=	Configuration.getDaoFileName(className)+".java";
			classRender		=	new DaoClassRender(entity, Configuration.getInstance());		
			try {
				classContent	=	classRender.render();
			} catch (Exception ex) {
				LOGGER.severe("Impossible to create the DAO "+className+". Exception message: "+ex.getMessage());
			}
			try {
				LOGGER.info("Create dao "+className+". Target path: "+targetDirectory+File.separator+fileName);
				ERM2BeansHelper.writeFile(classContent, targetDirectory, fileName, false);
			} catch (Exception ex) {
				LOGGER.severe("Impossible to create the DAO "+className+". Exception message: "+ex.getMessage());
				break;
			}
		}
		LOGGER.info("Operation complete");
    }
    
}
