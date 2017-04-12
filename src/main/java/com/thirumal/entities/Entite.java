package com.thirumal.entities;

import java.util.ArrayList;
import java.util.HashMap;

import com.thirumal.utility.*;

public class Entite {

	private	String 				nom;
	private String 				dbName;
	private String 				tablePrefix;
	private ArrayList<Attribut> alAttr;
	private String				parentClass;
	private ArrayList<String>	interfaces;
	private String				modelPckg;
	private String				daoPckg;
	private String 				rawName;
	
	private boolean codeTable;
	private HashMap<String, Integer> constantes;
	
	public Entite(String dbName, String tablePrefix, String nom) {
		this.dbName 		=	dbName;
		this.tablePrefix 	= 	tablePrefix;
		this.nom 			= 	ERM2BeansHelper.StringHelper.saniziteForClassName(nom);
		this.rawName		= 	nom;
		interfaces 			= 	new ArrayList<String>();
	}

	//Getter / Setter
	public String getRawName(){
		return rawName;
	}

	public String getDaoPackage() {
		return daoPckg;
	}

	public void setDaoPackage(String daoPckg) {
		this.daoPckg = daoPckg;
	}

	public String getModelPackage(){
		return modelPckg;
	}
	
	public void setModelPackage(String modelPckg){
		this.modelPckg	=	modelPckg;
	}
	
	public String getModelCanonicalName(){
		return modelPckg != null && !modelPckg.isEmpty() ? modelPckg+"."+nom : nom;
	}
	
	public String getDaoCanonicalName(){
		return daoPckg != null && !daoPckg.isEmpty() ? daoPckg+"."+nom : nom;
	}

	public void addInterface(String interFace){
		interfaces.add(interFace);
	}
	
	public void removeInterfaces(){
		interfaces = new ArrayList<String>();
	}
	
	public ArrayList<String> getInterfaces(){
		return interfaces;
	}
	
	public boolean hasInterface(){
		return interfaces != null && interfaces.size() > 0;
	}
	
	public boolean hasParent(){
		return parentClass != null && !parentClass.isEmpty();
	}

	public String getParentClass() {
		return parentClass;
	}

	public void setParentClass(String parentClass) {
		this.parentClass = parentClass;
	}

	public String getNom() {
		return nom;
	}

	public void setNom(String nom) {
		this.nom = ERM2BeansHelper.StringHelper.saniziteForClassName(nom);
	}

	public ArrayList<Attribut> getAlAttr() {
		return alAttr;
	}

	public void setAlAttr(ArrayList<Attribut> alAttr) {
		this.alAttr = alAttr;
	}

	public String getTablePrefix() {
		return tablePrefix;
	}

	public void setTablePrefix(String tablePrefix) {
		this.tablePrefix = tablePrefix;
	}

	public String getDbName() {
		return dbName;
	}

	public void setDbName(String dbName) {
		this.dbName = dbName;
	}

	public HashMap<String, Integer> getConstantes() {
		return constantes;
	}

	public void setConstantes(HashMap<String, Integer> constantes) {
		this.constantes = constantes;
	}

	public boolean isCodeTable() {
		return codeTable;
	}

	public void setCodeTable(boolean codeTable) {
		this.codeTable = codeTable;
	}

}
