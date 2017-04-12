package com.thirumal.utility;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.security.InvalidParameterException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.thirumal.entities.Entite;
import com.thirumal.utility.PrepareStatementBuilder.Action;

/**
 * 
 * @author Lo�c FALKLAND
 *
 */
public final class ERM2BeansHelper {
	
	private ERM2BeansHelper(){}
	
	public static void clearQueries(String pathToSave){
		
		File file = new File(pathToSave, "queries.properties");
		
		if(file.exists()){
			file.delete();
		}
		
	}
	
	public static void addQueryInProp(String pathToSave, Entite entity, Action action,String query) throws Exception{
		
		if(query == null){
			throw new IllegalArgumentException(entity.getNom()+" "+action.name());
		}
		
		File file 			= null;
		String queryEscaped = null;

		/*
		File folder = new File(new File(pathToSave).getParentFile().getPath()+File.separatorChar+"resources");
		File file = null;
		
		
		if(!folder.exists()){
			folder.mkdirs();
		}*/
		
		file = new File(pathToSave, "queries.properties");

		FileWriter fileWriter = null;
		BufferedWriter buffWriter = null;
		
		try {
			
			if(!file.exists()){
				file.createNewFile();
			}

			fileWriter = new FileWriter(file, true);
			
			buffWriter = new BufferedWriter(fileWriter);
			
			if(query.contains("=")){
				queryEscaped = query.replaceAll("=", "\\=");
			}
			else {
				queryEscaped = query;
			}
				
			
			
			String queryBuilt = entity.getNom()+"."+action.name().toLowerCase()+"="+queryEscaped+StringHelper.lineSeparator;
			byte[] queryBuiltAsBits = queryBuilt.getBytes("UTF-8");
			
			buffWriter.write(new String(queryBuiltAsBits,"UTF-8"));
			
			buffWriter.close();
			
		} catch (FileNotFoundException e) {
			throw new Exception(e.getMessage());
		} catch (UnsupportedEncodingException e) {
			throw new Exception(e.getMessage());
		} catch (IOException e) {
			throw new Exception(e.getMessage());
		}
		finally {

			if(buffWriter != null){
				try {
					buffWriter.close();
				} catch (IOException e) {
					throw new Exception(e.getMessage());
				}
			}
			
			if(fileWriter != null){
				try {
					fileWriter.close();
				} catch (IOException e) {
					throw new Exception(e.getMessage());
				}
			}

		}
		
	}
	
	public static final String generateClassComment(String dbName, String dbVersion){
		
		StringBuilder strBuilder = new StringBuilder();
		System.out.println("Genereate Comment");
		strBuilder.append("/**"+StringHelper.lineSeparator);
		strBuilder.append("*"+StringHelper.lineSeparator);
		strBuilder.append("*"+StringHelper.tabulation+"@Author  ERM2Beans"+StringHelper.lineSeparator);
		strBuilder.append("*"+StringHelper.tabulation+"@since   "
				+ (new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(Calendar
						.getInstance().getTime())) + StringHelper.lineSeparator);
		
		System.out.println("str: " + strBuilder);
		strBuilder.append("*"+StringHelper.tabulation+"@version "+dbVersion+StringHelper.lineSeparator);
		
		strBuilder.append("*"+StringHelper.lineSeparator);
		strBuilder.append("*"+StringHelper.lineSeparator);

		strBuilder.append("*"+StringHelper.tabulation+"Target Database: "+dbName+StringHelper.lineSeparator);
		
		strBuilder.append("*"+StringHelper.lineSeparator);
		strBuilder.append("*/");
		
		
		return strBuilder.toString();
		
	}
	
	public static void createDirectory(String directoryPath) throws Exception{
		
		File directory = new File(directoryPath);

		  // if the directory does not exist, create it
		  if (!directory.exists()) {

		    try{
		        directory.mkdir();
		     } catch(SecurityException ex){
		        throw new Exception(ex.getMessage());
		     }        

		  }
		  
	}
	
	
	public static void writeFile(String content, String targetDirectory, String fileName, boolean append) throws Exception{
		
		if(targetDirectory == null || targetDirectory.isEmpty()){
			throw new InvalidParameterException("the parameter target is null or empty String");
		}
		
		if(fileName == null || fileName.isEmpty()){
			throw new InvalidParameterException("the fileName target is null or empty String");
		}

		
		String 			fullPath 	= targetDirectory+File.separator+fileName;
		PrintWriter 	out 		= null;
		
		try {
			
			createDirectory(targetDirectory);
			
			File file = new File(fullPath);
			
			
			
			if(!append){
				
				if(file.exists()){
					file.delete();
				}
				
				file.createNewFile();
				
			}
			
			

			out = new PrintWriter(new BufferedWriter(new FileWriter(file, append)));
			out.println(content);
			
		} catch (IOException ex) {
			throw new Exception(ex.getMessage());
		} catch (Exception ex) {
			throw new Exception(ex.getMessage());
		}
		finally {
			if(out != null){
				out.close();
			}
		}

	}
	
	public final static class StringHelper {
		
		public static 	final 	String 	lineSeparator 	= 	System.getProperty("line.separator");
		public static	final 	String	tabulation		=	"\t";
		
		
		public static final String getMethodNameForBoolean(String attrName){
			
			String result = saniziteForClassName(attrName);
			
			/*
			if(result.toCharArray().length >= 2 && !result.substring(0, 2).equalsIgnoreCase("is")){
				result = "Is"+result;
			}*/
			
			return "Is"+result;
			
		}
		
		@Deprecated
		public static final String getJavaClassFileName(String className){
			
			StringBuilder result = new StringBuilder();
			
			className = sanitizeForAttributName(className);
			
			result.append(className.substring(0, 1).toUpperCase());
			result.append(className.substring(1));
			result.append(".java");
			
			return result.toString();
			
		}

		public static final String saniziteForClassName(String className){

			StringBuilder result = new StringBuilder();
			String[] classNameAsStrArray = className.split("_");

			for(String str : classNameAsStrArray){
				
				result.append(str.substring(0, 1).toUpperCase());
				result.append(str.substring(1));
			}
			
			
			return result.toString();

		}
		
		public static final String sanitizeForAttributName(String attrName){
			
			StringBuilder result = new StringBuilder();
			String[] classNameAsStrArray = attrName.split("_");
			int classNameAsStrArrayLength = classNameAsStrArray.length;
	        String str = new String();
	        
			for(int i = 0; i < classNameAsStrArrayLength; i++){
				
				str = classNameAsStrArray[i];
				
				if(i > 0){
					
					str = str.substring(0, 1).toUpperCase() + str.substring(1);
					
				}
				
				result.append(str);
				
			}
			
			
			return result.toString();
			
		}
		
	}

	
}
