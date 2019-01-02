package com.thirumal.utility;


import java.security.InvalidParameterException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;


/**
 * 
 * @author Thirumal
 *
 */
public final class DbHelper {
	
	private	static	final	Logger	LOGGER		=	Logger.getLogger(DbHelper.class.getName());
	
	private static HashMap<String, String> simpleNamesCanonicalNames;
	private static HashMap<String, String> sqlTypesJavaTypes;
	private static HashMap<String, String> javaTypesPreparedStatementSet;
	private static HashMap<String, String> javaTypesResultSet;
	
	
	private static void mapJavatypesResultSet(){
		
		javaTypesResultSet = new HashMap<String, String>();
		javaTypesResultSet.put("String", "%s.getString(%s)");
		javaTypesResultSet.put("BigDecimal", "%s.getBigDecimal(%s)");
		javaTypesResultSet.put("Boolean", "%s.getBoolean(%s)");
		javaTypesResultSet.put("Byte", "%s.getByte(%s)");
		javaTypesResultSet.put("Integer", "%s.getInt(%s)");
		javaTypesResultSet.put("Long", "%s.getLong(%s)");
		javaTypesResultSet.put("Float", "%s.getFloat(%s)");
		javaTypesResultSet.put("Double", "%s.getDouble(%s)");
		javaTypesResultSet.put("byte[]", "%s.getBytes(%s)");
		javaTypesResultSet.put("Date", "%s.getDate(%s)");
		javaTypesResultSet.put("Time", "%s.getTime(%s)");
		javaTypesResultSet.put("Timestamp", "%s.getTimestamp(%s)");
		javaTypesResultSet.put("Short", "%s.getShort(%s)");
		// microsoft.sql.DateTimeOffset
		javaTypesResultSet.put("DateTimeOffset",
				"?.getObject(?, microsoft.sql.DateTimeOffset)");
		//PostgreSQL
		javaTypesResultSet.put("boolean", "%s.getBoolean(%s)");
		javaTypesResultSet.put("bool", "%s.getBoolean(%s)");
		javaTypesResultSet.put("bpchar", "%s.getString(%s)");
		javaTypesResultSet.put("inet", "%s.getString(%s)");
		javaTypesResultSet.put("UUID", "%s.getObject(%s)");
		javaTypesResultSet.put("citext", "%s.getString(%s)");
		javaTypesResultSet.put("jsonb", "%s.getString(%s)");
		javaTypesResultSet.put("PGInterval", "%s.getObject(%s)");
		//Enum
		javaTypesResultSet.put("nature_of_money", "%s.getString(%s)");
		javaTypesResultSet.put("nature_of_debt", "%s.getString(%s)");
	}
	
	private static void mapJavaTypesPrepareStatementSet(){
		
		javaTypesPreparedStatementSet = new HashMap<String, String>();
		javaTypesPreparedStatementSet
				.put("String", "%s.setString(%s, %s)");
		javaTypesPreparedStatementSet.put("BigDecimal",
				"%s.setBigDecimal(%s, %s)");
		javaTypesPreparedStatementSet.put("Boolean",
				"%s.setBoolean(%s, %s)");
		javaTypesPreparedStatementSet.put("Byte", "%s.setByte(%s, %s)");
		javaTypesPreparedStatementSet.put("Integer", "%s.setInt(%s, %s)");
		javaTypesPreparedStatementSet.put("Long", "%s.setLong(%s, %s)");
		javaTypesPreparedStatementSet.put("Float", "%s.setFloat(%s, %s)");
		javaTypesPreparedStatementSet
				.put("Double", "%s.setDouble(%s, %s)");
		javaTypesPreparedStatementSet.put("byte[]", "%s.setBytes(%s, %s)");
		javaTypesPreparedStatementSet.put("Date", "%s.setObject(%s, %s)");
		javaTypesPreparedStatementSet.put("Time", "%s.setObject(%s, %s)");
		javaTypesPreparedStatementSet.put("Timestamp",
				"%s.setTimestamp(%s, %s)");
		javaTypesPreparedStatementSet.put("Short", "%s.setShort(%s, %s)");
		// microsoft.sql.DateTimeOffset
		javaTypesPreparedStatementSet.put("DateTimeOffset",
				"?.setObject(?, ?)");
		//PostgreSQL
		javaTypesPreparedStatementSet.put("boolean", "%s.setBoolean(%s, %s)");
		javaTypesPreparedStatementSet.put("bool", "%s.setBoolean(%s, %s)");
		javaTypesPreparedStatementSet.put("bpchar", "%s.setString(%s, %s)");
		javaTypesPreparedStatementSet.put("inet", "%s.setString(%s, %s)");
		javaTypesPreparedStatementSet.put("UUID", "%s.setObject(%s, %s)");
		javaTypesPreparedStatementSet.put("citext", "%s.setString(%s, %s)");
		javaTypesPreparedStatementSet.put("jsonb", "%s.setString(%s, %s)");
		javaTypesPreparedStatementSet.put("interval", "%s.setObject(%s, %s)");
		//Enum
		javaTypesPreparedStatementSet.put("nature_of_money", "%s.setString(%s, %s)");
		javaTypesPreparedStatementSet.put("nature_of_debt", "%s.setString(%s, %s)");
		
	}
	
	private static void	mapSQLTypesJavaTypes(){
		System.out.println("Mapping SQL types to Java type");
		sqlTypesJavaTypes	=	new HashMap<String, String>();
		
		sqlTypesJavaTypes.put("char", "String");
		sqlTypesJavaTypes.put("varchar", "String");
		sqlTypesJavaTypes.put("longvarchar", "String");
		sqlTypesJavaTypes.put("nvarchar", "String");
		sqlTypesJavaTypes.put("nchar", "String");
		sqlTypesJavaTypes.put("ntext", "String");
		sqlTypesJavaTypes.put("sysname", "String");
		sqlTypesJavaTypes.put("tinytext", "String");
		sqlTypesJavaTypes.put("text", "String");
		sqlTypesJavaTypes.put("mediumtext", "String");
		sqlTypesJavaTypes.put("longtext", "String");
		sqlTypesJavaTypes.put("uniqueidentifier", "String");		
		sqlTypesJavaTypes.put("xml", "String");
		sqlTypesJavaTypes.put("longvarchar", "String");
		sqlTypesJavaTypes.put("sql_variant", "String");
		sqlTypesJavaTypes.put("sysname", "String");
		
		sqlTypesJavaTypes.put("numeric", "BigDecimal");
		sqlTypesJavaTypes.put("decimal", "BigDecimal");
		sqlTypesJavaTypes.put("bit", "Boolean");
		sqlTypesJavaTypes.put("tinyint", "Byte");
		sqlTypesJavaTypes.put("smallint", "Short");
		sqlTypesJavaTypes.put("int", "Integer");
		sqlTypesJavaTypes.put("integer", "Integer");
		sqlTypesJavaTypes.put("bigint", "Long");
		sqlTypesJavaTypes.put("real", "Float");
		sqlTypesJavaTypes.put("double", "Double");
		sqlTypesJavaTypes.put("money", "BigDecimal");
		sqlTypesJavaTypes.put("smallmoney", "BigDecimal");
		sqlTypesJavaTypes.put("real", "Float");
		sqlTypesJavaTypes.put("float", "Double");
		sqlTypesJavaTypes.put("unsigned smallint", "Integer");
		sqlTypesJavaTypes.put("unsigned int", "Integer");
		sqlTypesJavaTypes.put("unsigned bigint", "BigDecimal");
		sqlTypesJavaTypes.put("int identity", "Integer");
		
		sqlTypesJavaTypes.put("binary", "byte[]");
		sqlTypesJavaTypes.put("varbinary", "byte[]");
		sqlTypesJavaTypes.put("tinyblob", "byte[]");
		sqlTypesJavaTypes.put("blob", "byte[]");
		sqlTypesJavaTypes.put("mediumblob", "byte[]");
		sqlTypesJavaTypes.put("longblob", "byte[]");
		sqlTypesJavaTypes.put("image", "byte[]");
		sqlTypesJavaTypes.put("udt", "byte[]");
		
		sqlTypesJavaTypes.put("bit", "Boolean");
		
		sqlTypesJavaTypes.put("date", "Date");
		
		/* BASE VERSION
		sqlTypesJavaTypes.put("time", "Time");
		sqlTypesJavaTypes.put("smalldatetime", "Timestamp");
		sqlTypesJavaTypes.put("datetime", "Timestamp");
		sqlTypesJavaTypes.put("datetime2", "Timestamp");
		sqlTypesJavaTypes.put("timestamp", "Timestamp");
		sqlTypesJavaTypes.put("datetimeoffset", "DateTimeOffset");*/
		
		sqlTypesJavaTypes.put("time", "Date");
		sqlTypesJavaTypes.put("smalldatetime", "Date");
		sqlTypesJavaTypes.put("datetime", "Date");
		sqlTypesJavaTypes.put("datetime2", "Date");
		sqlTypesJavaTypes.put("timestamp", "Date");
		sqlTypesJavaTypes.put("datetimeoffset", "Date");
		
		// PostgreSQL 
		sqlTypesJavaTypes.put("serial", "Long");
		sqlTypesJavaTypes.put("bool", "Boolean");
		sqlTypesJavaTypes.put("int2", "Integer");
		sqlTypesJavaTypes.put("int4", "Integer");
		sqlTypesJavaTypes.put("bpchar", "String");
		sqlTypesJavaTypes.put("chkpass", "String");
		sqlTypesJavaTypes.put("inet", "String");
		sqlTypesJavaTypes.put("uuid", "UUID");
		sqlTypesJavaTypes.put("bytea", "byte[]");
		sqlTypesJavaTypes.put("citext", "String");
		sqlTypesJavaTypes.put("jsonb", "String");
		sqlTypesJavaTypes.put("interval", "PGInterval");
		//ENUM
		sqlTypesJavaTypes.put("nature_of_money", "String");
		sqlTypesJavaTypes.put("nature_of_debt", "String");
	}
	
	private static void mapSimpleNameCanonicalName(){
		
		simpleNamesCanonicalNames	= new HashMap<String, String>();
		
		simpleNamesCanonicalNames.put("String", "java.lang.String");
		simpleNamesCanonicalNames.put("BigDecimal", "java.math.BigDecimal");
		simpleNamesCanonicalNames.put("Boolean", "java.lang.Boolean");
		simpleNamesCanonicalNames.put("Byte", "java.lang.Byte");
		simpleNamesCanonicalNames.put("Integer", "java.lang.Integer");
		simpleNamesCanonicalNames.put("Long", "java.lang.Long");
		simpleNamesCanonicalNames.put("Float", "java.lang.Float");
		simpleNamesCanonicalNames.put("Double", "java.lang.Double");
		simpleNamesCanonicalNames.put("byte[]", "byte[]");
		simpleNamesCanonicalNames.put("Date", "java.util.Date");
		simpleNamesCanonicalNames.put("Time", "java.sql.Time");
		simpleNamesCanonicalNames.put("Timestamp", "java.sql.Timestamp");
		simpleNamesCanonicalNames.put("UUID", "java.util.UUID");
		simpleNamesCanonicalNames.put("Short", "java.lang.Short");
		simpleNamesCanonicalNames.put("DateTimeOffset", "microsoft.sql.DateTimeOffset");
		simpleNamesCanonicalNames.put("jsonb", "java.lang.String");
		simpleNamesCanonicalNames.put("PGInterval", "org.postgresql.util.PGInterval");
		//Enum
		simpleNamesCanonicalNames.put("nature_of_money", "java.lang.String");
		simpleNamesCanonicalNames.put("nature_of_debt", "java.lang.String");
	}
	
	
	public static String javaTypeToResultSetGet(String javaType) throws Exception{
		
		if(javaType == null || javaType.isEmpty()){
			throw new InvalidParameterException("the parameter is null or empty String");
		}
		
		if(javaTypesResultSet == null){
			mapJavatypesResultSet();
		}
		
		
		if(javaTypesResultSet.containsKey(javaType)){
			return javaTypesResultSet.get(javaType);
		}
		
		throw new Exception("Impossible to map the Java type "+javaType+" with a ResultSet get method.");
		
	}
	
	public static String javaTypeToPrepareStatementSet(String javaType) throws Exception{
		
		if(javaType == null || javaType.isEmpty()){
			throw new InvalidParameterException("the parameter is null or empty String");
		}
		
		if(javaTypesPreparedStatementSet == null){
			mapJavaTypesPrepareStatementSet();
		}
		
		if(javaTypesPreparedStatementSet.containsKey(javaType)){
			return javaTypesPreparedStatementSet.get(javaType);
		}
		
		throw new Exception("Impossible to map the Java type "+javaType+" with a PreparedStatement set method.");
		
	}
	
	public static String sqlTypeToJavaType(String sqlType) throws Exception{
		
		if(sqlType == null || sqlType.isEmpty()){
			throw new InvalidParameterException("the parameter is null or empty String");
		}
		
		if(sqlTypesJavaTypes == null){
			mapSQLTypesJavaTypes();
		}
		
		String sqlTypeStrLowerCase = sqlType.toLowerCase();
		
		if(sqlTypesJavaTypes.containsKey(sqlTypeStrLowerCase)){
			return sqlTypesJavaTypes.get(sqlTypeStrLowerCase);
		}
		else {
			
			if(sqlTypeStrLowerCase.matches("varbinary[(]([0-9]{1,})[)]")){
				return "byte[]";
			}
			
			if(sqlTypeStrLowerCase.matches("varchar[(]([0-9]{1,})[)]")){
				return "String";
			}
			
			if(sqlTypeStrLowerCase.matches("datetimeoffset[(]([0-9]{1,})[)]")){
				return "Date";
			}
			
		}
		
		
		throw new Exception("Impossible to map the SQL type "+sqlType+" with a Java Type.");
		
	}
	

	public static String simpleNameToCanonicalName(String simpleName) throws Exception {
		
		if(simpleName == null || simpleName.isEmpty()){
			throw new InvalidParameterException("the parameter is null or empty String");
		}
		
		if(simpleNamesCanonicalNames == null){
			mapSimpleNameCanonicalName();
		}
		
		if(simpleNamesCanonicalNames.containsKey(simpleName)){
			return simpleNamesCanonicalNames.get(simpleName);
		}
		
		
		throw new Exception("Impossible to find the canonical name from the simpleName: "+simpleName);
		
	}
	
	
	public static boolean columnIsAutoincrement(Connection connection, String db, String tablePrefix, String tableName, String columnName) {

		boolean result = false;
		Statement stmt = null;
		ResultSet rs = null;
		ResultSetMetaData rsMetadata = null;

		try {
			stmt = connection.createStatement();
			/*System.out.println("re: " + columnName);
			System.out.println("re: " + tablePrefix);
			System.out.println("re: " + tableName);*/
			// rs = stmt.executeQuery("SELECT "+columnName+" FROM ["+tablePrefix+"].["+tableName+"]");
			rs = stmt.executeQuery("SELECT "+columnName+" FROM "+tablePrefix+"."+tableName);
			rs.next();
			rsMetadata = rs.getMetaData();
			result = rsMetadata.isAutoIncrement(1);
			//System.out.println("result: " + result);
		} catch (SQLException ex) {
			LOGGER.severe(ex.getMessage());
		}

		return result;
		
	}
	
	
	public static HashMap<String, Integer> retrieveCdForConstantes(Connection connection, String db, String tablePrefix, String tableName){
		
		HashMap<String, Integer> result = new HashMap<String, Integer>();
	
		if(tableName.contains("_Cd")){
			
			String tableNameToLookup = tableName.replace("_Cd", "_Locales");
			
			//Cd description in english fetch only
			String query = "SELECT ["+tableName+"], [Description] FROM ["+db+"].[Codes].["+tableNameToLookup+"] WHERE [Locale_Cd] = 1";
			
			Statement stmt = null;
			ResultSet rs = null;
			String key = null;
			Integer value = null;
			
			try {
				
				stmt = connection.createStatement();
				rs = stmt.executeQuery(query);
				
				while(rs.next()){
					
					key = rs.getString("Description");
					//formatting
					key = key.toUpperCase();
					key = key.replaceAll(" ", "_");
					key = key.replaceAll("\\(", "");
					key = key.replaceAll("\\)", "");
					key = key.replaceAll(",", "");
					key = key.replaceAll("-", "_");
					key = key.replaceAll("&", "");
					key = key.replaceAll("/", "");
					key = key.replaceAll("\\.", "_");
					key = StringUtils.stripAccents(key);
					key = key.replaceAll("[^a-zA-Z0-9_]", "_");
					
	
					//check if startsWithNumber
					//add underscore to conform to the Java namming allowed
					if(Character.isDigit(key.charAt(0))){
						key = "_"+key;
					}
					
					value = rs.getInt(tableName);
					
					
					result.put(key, value);
					
				}
				
				
			} catch (SQLException ex) {
				LOGGER.severe("Impossible to retrieve the Code table for the table name "+tableName+" impossible to reach table "+tableNameToLookup+". Exception message "+ex.getMessage());
			}
			
		}
		
		return result;
		
	}
	
	
	public static final String createPreparementSet(String ps, int index, String javaType, String sqlType,
			String objectSourceAsName, boolean canBeNull) {

		String result	= null;
		String psSet	= null;
		try {
			psSet = javaTypeToPrepareStatementSet(javaType);
		} catch (Exception ex) {
			LOGGER.severe(ex.getMessage());
		}

		boolean mappingExists = psSet != null && !ps.isEmpty();

		if (mappingExists) {

			String value = objectSourceAsName;
			
			if(sqlType != null){
				
				if(sqlType.equalsIgnoreCase("time") || sqlType.equalsIgnoreCase("smalldatetime") || 
						   sqlType.equalsIgnoreCase("datetime") || sqlType.equalsIgnoreCase("datetime2") || 
						   sqlType.equalsIgnoreCase("timestamp") || sqlType.equalsIgnoreCase("datetimeoffset")){
							
							objectSourceAsName = "new java.sql.Timestamp("+objectSourceAsName+".getTime())";
							
							javaType = "Timestamp";
							
				}
				else if(sqlType.equalsIgnoreCase("date")){
					
					objectSourceAsName = "new java.sql.Date("+objectSourceAsName+".getTime())";
					
				}
			}

			/*
			if(javaType.equals("Timestamp")){
				objectSourceAsName = "new java.sql.Timestamp("+objectSourceAsName+".getTime())";
			}
			
			if(javaType.equals("Date")){
				objectSourceAsName = "new java.sql.Date("+objectSourceAsName+".getTime())";
			} */
			
			result = String.format(psSet, ps, index, objectSourceAsName);
			if (canBeNull) {

				String result_tmp = "if(" + value
						+ " == null) {\r\n";
				result_tmp += "\t\t\t\t\t" + ps + ".setObject(" + index
						+ ", null);\r\n";
				result_tmp += "\t\t\t\t} else { \r\n";
				result_tmp += "\t\t\t\t\t" + result + ";\r\n";
				result_tmp += "\t\t\t\t}\r\n";

				result = result_tmp;

			} else {
				result += ";";
			}

		}

		return result;

	}

	public static String createResulSet(String rs, String javaType, String sqlType, String name) {
		String result	= null;
		String rsGet	= null;		
		try {
			rsGet = javaTypeToResultSetGet(javaType);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		boolean mappingExists = rsGet != null && !rsGet.isEmpty();

		if (mappingExists) {

			if(sqlType != null){
				
				if(sqlType.equalsIgnoreCase("time") || sqlType.equalsIgnoreCase("smalldatetime") || 
						   sqlType.equalsIgnoreCase("datetime") || sqlType.equalsIgnoreCase("datetime2") || 
						   sqlType.equalsIgnoreCase("timestamp") || sqlType.equalsIgnoreCase("datetimeoffset")){

							javaType = "Timestamp";
							
				}
				else if(sqlType.equalsIgnoreCase("date")){
					javaType = "Date";
				}

			}

			result = rs + ".getObject(\"" + name + "\") != null ? "	+ String.format(rsGet, rs, "\"" + name + "\"") + " : null";
			if (sqlType.equalsIgnoreCase("uuid")) {
				result = rs + ".getObject(\"" + name + "\") != null ? "	+ String.format(rsGet, ("(java.util.UUID) " + rs), "\"" + name + "\"") + " : null";
			}

		}

		return result;

	}

	
	
	
}
