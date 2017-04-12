package com.thirumal.utility;


import java.util.ArrayList;

import com.thirumal.entities.Attribut;
import com.thirumal.entities.Entity;



public final class PrepareStatementBuilder {
	
	
	public enum Action {
		LIST,
		GET,
		CREATE,
		UPDATE,
		DELETE,
		DELETE_PERMANENTLY;
	}
	
	private PrepareStatementBuilder(){}
	
	
	public static String create(Entity dao, Action action){

		if(dao == null){
			throw new IllegalArgumentException("DAO object in parameter is null");
		}
		
		String result = null;
		
		switch(action){
			
			case GET:
			     result = get(dao);
			break;
				
			case LIST:
				result = list(dao);
			break;
			
			case CREATE:
				result = create(dao);
			break;
			
			case UPDATE:
				result = update(dao);
			break;
			
			case DELETE:
				result = delete(dao);
			break;
			
			case DELETE_PERMANENTLY:
				result	=	deletePermanently(dao);
			break;

		}
		
		
		return result;
		
	}
	
	
	
	
	//Private Methods
	
	/*private static final String getLast(Entite entity){
		
		String result = null;
		String pk = null;
		ArrayList<Attribut> attributes = entity.getAlAttr();
		
		for(Attribut attribut : attributes){
			
			
			if(attribut.isPrimaryKey()){
				pk = attribut.getRawName();
				break;
			}
			
		}
		
		
		if(pk != null){
			result = "SELECT * FROM "+entity.getTablePrefix()+"."+entity.getRawName()+" ORDER BY Row_Creation_Date"+" DESC limit 1";
		}
		
		return result;
	
	}*/
	
	private static final String delete(Entity entity){
		return update(entity);
	}
	
	
	private static final String update(Entity entity){
		
		StringBuilder strBuilder = new StringBuilder();
		
		strBuilder.append("UPDATE " + entity.getTablePrefix() + "." + entity.getRawName());
		
		strBuilder.append(" SET ");
		
		Attribut attribut = null;
		ArrayList<Attribut> attributes = entity.getAlAttr();
		
		String tmp = null;
		
		boolean toInclude = false;
		
		for(int i = 0, attributesLength = attributes.size(); i < attributesLength; i++){
			
			attribut = attributes.get(i);
			if (attribut.getName().equalsIgnoreCase("rowCreationDate") || attribut.getName().equalsIgnoreCase("rowUpdatedDate")) {
				continue;
			}
			toInclude = !attribut.isPrimaryKey() || !attribut.isAutoincrement();
			
			if(toInclude){
				
				tmp = attribut.getRawName()+" = ?";
				
				if(i < attributesLength - 1){
					tmp += ", ";
				}
				
				strBuilder.append(tmp);
				
			}

		}
		
		String whereStatement = null;
		
		for(Attribut attr : attributes){
			if (attribut.getName().equalsIgnoreCase("rowCreationDate") || attribut.getName().equalsIgnoreCase("rowUpdatedDate")) {
				continue;
			}
			if(attr.isPrimaryKey()){
				
				whereStatement = attr.getRawName()+" = ?";
				break;
				
			}
			
		}
		
		strBuilder.append(" WHERE "+whereStatement);

		return strBuilder.toString();
		
	}
	
	
	private static final String deletePermanently(Entity entity){
		
		StringBuilder strBuilder = new StringBuilder();
		
		ArrayList<Attribut> attributes = entity.getAlAttr();
		
		strBuilder.append("DELETE FROM " + entity.getTablePrefix() + "." + entity.getRawName());
		
		String whereStatement = null;
		
		for(Attribut attr : attributes){
			
			if(attr.isPrimaryKey()){
				
				whereStatement = attr.getRawName()+" = ?";
				break;
				
			}
			
		}
		
		strBuilder.append(" WHERE "+whereStatement);
		
		
		return strBuilder.toString();
		
	}
	
	
	private static final String create(Entity dao){
		
		StringBuilder strBuilder = new StringBuilder();
		
		strBuilder.append("INSERT INTO " + dao.getTablePrefix() + "." + dao.getRawName());
		
		strBuilder.append("(");
		
		Attribut attribut = null;
		ArrayList<Attribut> attributes = dao.getAlAttr();
		
		String tmp = null;
		
		boolean toInclude = false;
		
		for(int i = 0, attributesLength = attributes.size(); i < attributesLength; i++){
			
			attribut = attributes.get(i);
			if (attribut.getName().equalsIgnoreCase("rowCreationDate") || attribut.getName().equalsIgnoreCase("rowUpdatedDate")) {
				continue;
			}
			/*
			if(attribut.isPrimaryKey() && attribut.isAutoincrement()){
				
			}
			*/
			
			toInclude = !attribut.isPrimaryKey() || !attribut.isAutoincrement();
			
			if(toInclude){
				
				//System.out.println(attribut.getName().toUpperCase()+" A INCLURE.");
				
				tmp = attribut.getRawName();
				
				if(i < attributesLength - 1){
					tmp += ", ";
				}
				
				strBuilder.append(tmp);
				
			}
			
		}
		
		strBuilder.append(") ");
		
		strBuilder.append("VALUES (");
		
		for(int i = 0, attributesLength = attributes.size(); i < attributesLength; i++){
			
			attribut = attributes.get(i);
			if (attribut.getName().equalsIgnoreCase("rowCreationDate") || attribut.getName().equalsIgnoreCase("rowUpdatedDate")) {
				continue;
			}
			toInclude = !attribut.isPrimaryKey() || !attribut.isAutoincrement();
			
			if(toInclude){

				tmp = "?";
				
				if(i < attributesLength - 1){
					tmp += ", ";
				}
				
				strBuilder.append(tmp);

			}

		}

		strBuilder.append(")");
		
		
		return strBuilder.toString();
		
	}
	
	
	private static final String list(Entity dao){
		return "SELECT * FROM " + dao.getTablePrefix() + "." + dao.getRawName();
	}
	
	
	private static final String get(Entity dao){
		
		StringBuilder strBuilder = new StringBuilder();

		strBuilder.append("SELECT * FROM " + dao.getTablePrefix() + "." + dao.getRawName());
		
		ArrayList<Attribut> attributes = dao.getAlAttr();
		
		String whereStatement = null;
		
		for(Attribut attribut : attributes){
			
			if(attribut.isPrimaryKey()){
				
				whereStatement = attribut.getRawName()+" = ?";
				break;
				
			}
			
		}
		
		strBuilder.append(" WHERE "+whereStatement);
		
		

		return strBuilder.toString();
		
	}
	
	

}
