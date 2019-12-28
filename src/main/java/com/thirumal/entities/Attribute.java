package com.thirumal.entities;



import java.util.logging.Logger;

import com.thirumal.utility.*;

public class Attribute {
	
	private	static	final	Logger	LOGGER		=	Logger.getLogger(Attribute.class.getName());

	private String name;
	private String type;
	private Integer size;
	private boolean isIncluded;
	private String javaType;
	private String javaPackagePath;
	private boolean primaryKey;
	private boolean foreignKey;
	private boolean autoincrement;
	private String sqlType;
	private String rawName;
	
	public Attribute(String name, String type, Integer size) {
		
		rawName		=	name;
		this.name 	= 	ERM2BeansHelper.StringHelper.sanitizeForAttributName(name.toLowerCase());
		this.type 	= 	type;
		this.size 	= 	size;
		isIncluded 	= 	true;
		primaryKey	= 	false;
		foreignKey  =   false; 
		setSqlType(type);
		
		doJavaMapping();

	}
	
	private void doJavaMapping(){
		try {
			javaType	=	DbHelper.sqlTypeToJavaType(type);
		} catch (Exception e) {
			LOGGER.severe(e.getMessage());
		}
		
		try {
			javaPackagePath	=	DbHelper.simpleNameToCanonicalName(javaType);
		} catch (Exception e) {
			LOGGER.severe(e.getMessage());
		}
	}

	//Getter / Setter
	public String getRawName(){
		return rawName;
	}
	
	public boolean isIncluded() {
		return isIncluded;
	}

	public void setIncluded(boolean isIncluded) {
		this.isIncluded = isIncluded;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = ERM2BeansHelper.StringHelper.sanitizeForAttributName(name);
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
		doJavaMapping();
	}

	public Integer getSize() {
		return size;
	}

	public void setSize(Integer size) {
		this.size = size;
	}

	public String getJavaType() {
		return javaType;
	}

	public String getJavaPackagePath() {
		return javaPackagePath;
	}

	public boolean isAutoincrement() {
		return autoincrement;
	}

	public void setAutoincrement(boolean autoincrement) {
		this.autoincrement = autoincrement;
	}

	public boolean isDate(){
		
		boolean result = false;
		
		if(javaType.equals("Date") ||
		   javaType.equals("Time") ||
		   javaType.equals("Timestamp") ||
		   javaType.equals("DateTimeOffset")){
			result = true;
		}
		
		return result;
		
	}

	public boolean isPrimaryKey() {
		return primaryKey;
	}

	public void setPrimaryKey(boolean primaryKey) {
		this.primaryKey = primaryKey;
	}

	/**
	 * @return the foreignKey
	 */
	public boolean isForeignKey() {
		return foreignKey;
	}

	/**
	 * @param foreignKey the foreignKey to set
	 */
	public void setForeignKey(boolean foreignKey) {
		this.foreignKey = foreignKey;
	}

	public String getSqlType() {
		return sqlType;
	}

	public void setSqlType(String sqlType) {
		this.sqlType = sqlType;
	}

	@Override
	public String toString() {
		return "Attribute [name=" + name + ", type=" + type + ", size=" + size + ", isIncluded=" + isIncluded
				+ ", javaType=" + javaType + ", javaPackagePath=" + javaPackagePath + ", primaryKey=" + primaryKey
				+ ", foreignKey=" + foreignKey + ", autoincrement=" + autoincrement + ", sqlType=" + sqlType
				+ ", rawName=" + rawName + "]";
	}
}
