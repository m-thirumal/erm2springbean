/**
 * 
 */
package com.thirumal.config;

import java.io.File;

/**
 * @author thirumal
 *
 */
public class ConfigurationKey {
	
	public static	final 	String	CONFIG_PATH =	"conf" + File.separator + "application.properties";
	
	/* Configuration keys */
	
	protected static final  String	DRIVER_PROP_KEY					=	"db.driver";
	protected static final	String	SCHEME_PROP_KEY 				=	"db.url.scheme";	
	protected static final	String	HOST_PROP_KEY					=	"db.url.host";
	protected static final	String	PORT_PROP_KEY					=	"db.url.port";
	protected static final	String	USER_PROP_KEY					=	"db.user";
	protected static final	String	PWD_PROP_KEY					=	"db.pwd";
	protected static final	String	NAME_PROP_KEY					=	"db.name";
	protected static final	String	DB_VERSION_PROP_KEY				= 	"db.query.dbVersion";
	protected static final	String	FOLDER_DAO_KEY					=	"folder.dao";
	protected static final	String	FOLDER_MODEL_KEY				=	"folder.model";
	protected static final	String	FOLDER_ROOT_KEY					=	"folder.root";
	protected static final	String	PCKG_MODEL_KEY					=	"package.model";
	protected static final	String	PCKG_DAO_KEY					=	"package.dao";
	protected static final	String	FILE_NAME_TEMPLATE_MODEL_KEY	=	"file.name.template.model";
	protected static final	String	FILE_NAME_TEMPLATE_DAO_KEY		=	"file.name.template.dao";
	protected static final  String  LOG_FILE_NAME                   =    "file.log";

}
