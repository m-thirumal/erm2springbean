package com.thirumal.render;

import java.util.HashMap;
import java.util.Map;

import com.thirumal.config.Configuration;
import com.thirumal.entities.Attribute;
import com.thirumal.entities.Entity;
import com.thirumal.utility.ERM2BeansHelper.StringHelper;

/**
 * 
 * @author Thirumal
 *
 */
public class ModelClassRender extends BaseClassRender {

	public ModelClassRender(Entity entity, Configuration configuration) {
		super(entity, configuration);
	}

	@Override
	public String render() throws Exception {
		StringBuffer output = new StringBuffer();
		
		String lineSeparator 	= 	StringHelper.lineSeparator;
		String tabulation		=	StringHelper.tabulation;
		Entity entity			=	getEntity();
		System.out.println(entity.getName());
		
		entity.addInterface("java.io.Serializable");
		
		output.append("package " + entity.getModelPackage() + ";");
		
		output.append(lineSeparator + lineSeparator);
		
		/* avoid redondant import */
		
		HashMap<String, String> javaTypesPckgPaths = new HashMap<String, String>();
		String javaType = null;
		String pckgPath = null;
		
		for(Attribute eachattr : entity.getAlAttr()){
			// System.out.println("attribute: " + eachattr.getJavaPackagePath() + " " + eachattr.getJavaType() + " " + eachattr.getName() + " " + eachattr.getRawName() + " " + eachattr.getSqlType());;
			javaType = eachattr.getJavaType();
			pckgPath = eachattr.getJavaPackagePath();
			
			if(!javaTypesPckgPaths.containsKey(javaType)){
				
				javaTypesPckgPaths.put(javaType, pckgPath);
				
			}
			
		}
		
		for(String path : javaTypesPckgPaths.values()){
			output.append("import "+path+";"+lineSeparator);
		}
		
		output.append(lineSeparator);
		
		/*= avoid redondant import */
		
		String interfacesToOuput =  null;
		
		if(entity.hasInterface()){
			
			interfacesToOuput = " implements ";

			String interfaceToOuput = null;
			
			for(int i = 0, interfaceCanonicalNamesLenght = entity.getInterfaces().size(); i < interfaceCanonicalNamesLenght; i++){
				
				interfaceToOuput = entity.getInterfaces().get(i);
				
				interfacesToOuput += interfaceToOuput + (i == (interfaceCanonicalNamesLenght -1) ? " " : ", ");
				
			}
			
			
		}
		
		output.append("/**" + lineSeparator + " *" + lineSeparator + " * @author Thirumal" + lineSeparator + " *" + lineSeparator + " */" + lineSeparator);
		
		
		output.append("public class " + entity.getName() +(entity.hasParent() ? " extends "+entity.getParentClass() : "")+(interfacesToOuput != null ? interfacesToOuput : "")+" {" + lineSeparator + lineSeparator);

		output.append(tabulation+"private static final long serialVersionUID = 1L;"+lineSeparator+lineSeparator);

		//Declarating fields
		output.append(tabulation+"//Declarating fields" + lineSeparator);
		
		for (Attribute eachattr : entity.getAlAttr()){
			output.append("	private " + eachattr.getJavaType() + " " + eachattr.getName() + ";" + lineSeparator);
			if (eachattr.isForeignKey() && eachattr.getRawName().toLowerCase().endsWith("_cd")) {
				output.append("	private String " + eachattr.getName().substring(0, eachattr.getName().length() - 2) + "Locale" + ";" 
						+ lineSeparator);
			}
		}
		
		
		HashMap<String, Integer> constantes = entity.getConstantes();
		
		if(constantes != null && constantes.size() > 0){
			
			output.append(lineSeparator);
			
			output.append(tabulation+"//Code table value(s) as constante(s)"+lineSeparator);
			for(Map.Entry<String, Integer> constante : constantes.entrySet()){
				output.append(tabulation+"public static int "+constante.getKey()+" = "+constante.getValue()+";"+lineSeparator);
			}

			output.append(lineSeparator);
			
		}
		else {
			output.append(lineSeparator);
		}
		//Default constructor
		output.append(tabulation+ "//Default constructor" + lineSeparator);	
		output.append("	public " + entity.getName() + "() {}" + lineSeparator);
		output.append(lineSeparator);
		output.append(tabulation + "//Parameterized constructor" + lineSeparator);	
		output.append("	public " + entity.getName() + "(");
		
		for (int i = 0; i < entity.getAlAttr().toArray().length; i++){
			output.append(entity.getAlAttr().get(i).getJavaType() + " " + entity.getAlAttr().get(i).getName());
			if (!(i == entity.getAlAttr().toArray().length-1)){
				output.append(", ");
			}
			if (entity.getAlAttr().get(i).isForeignKey() && entity.getAlAttr().get(i).getRawName().toLowerCase().endsWith("_cd")) {
				output.append("String " + entity.getAlAttr().get(i).getName().substring(0, 
						entity.getAlAttr().get(i).getName().length() - 2) + "Locale");
				if (!(i == entity.getAlAttr().toArray().length-1)){
					output.append(", ");
				}
			}			
		}
		
		output.append(") {" + lineSeparator);
		
		for (Attribute eachattr : entity.getAlAttr()){
			output.append("		this." + eachattr.getName() + " = " + eachattr.getName() + ";" + lineSeparator);
			if (eachattr.isForeignKey() && eachattr.getRawName().toLowerCase().endsWith("_cd")) {
				output.append("		this." + eachattr.getName().substring(0, eachattr.getName().length() - 2) + "Locale = " + 
						eachattr.getName().substring(0, eachattr.getName().length() - 2) + "Locale;" + lineSeparator);
			}
		}
		
		output.append("	}" + lineSeparator);		
		output.append(lineSeparator);
		output.append(tabulation + "//Getters & Setters" + lineSeparator);
		
		String resultGetSetName = null;
		
		for (Attribute eachattr : entity.getAlAttr()){
			//Getter
			output.append("	public " + eachattr.getJavaType() + " ");
			if(!eachattr.getJavaType().equalsIgnoreCase("Boolean")){
				resultGetSetName = "get" + StringHelper.saniziteForClassName(eachattr.getName());				
			}
			else {
				resultGetSetName = StringHelper.getMethodNameForBoolean(StringHelper.sanitizeForAttributName(eachattr.getName()));
			}
			output.append(resultGetSetName);
			output.append("() {" + lineSeparator);
			output.append("		return " + eachattr.getName() + ";" + lineSeparator);
			output.append("	}" + lineSeparator + lineSeparator);
			if (eachattr.isForeignKey() && eachattr.getRawName().toLowerCase().endsWith("_cd")) { //Foreign key
				output.append("	public String " + resultGetSetName.substring(0, resultGetSetName.length() - 2) + "Locale() {" + lineSeparator  +
						"		return " + eachattr.getName().substring(0, eachattr.getName().length() - 2) + "Locale" + ";" + lineSeparator +
						"	}" + lineSeparator + lineSeparator); 
			}
			
			//Setter
			output.append("	public void set");
			if(!eachattr.getJavaType().equalsIgnoreCase("Boolean")){
				resultGetSetName = StringHelper.saniziteForClassName(eachattr.getName());				
			}
			else {
				resultGetSetName = StringHelper.getMethodNameForBoolean(eachattr.getName());
			}
			output.append(resultGetSetName);
			output.append("(" + eachattr.getJavaType() + " " + eachattr.getName() + ") {" + lineSeparator);
			output.append("		this." + eachattr.getName() + " = " + eachattr.getName() + ";" + lineSeparator);
			output.append("	}" + lineSeparator + lineSeparator);
			if (eachattr.isForeignKey() && eachattr.getRawName().toLowerCase().endsWith("_cd")) { //Foreign key
				String locale = eachattr.getName().substring(0, eachattr.getName().length() - 2) + "Locale";
				output.append(" 	public void set" + resultGetSetName.substring(0, resultGetSetName.length() - 2) + "Locale(String "+  
						locale + ") {" + lineSeparator );
				output.append("		this." + locale + " = " + locale + ";" + lineSeparator);
				output.append("	}" + lineSeparator + lineSeparator);
			}
			
			
		}
		// toString()
		output.append(tabulation + "/* (non-Javadoc)" + lineSeparator);
		output.append(tabulation + " * @see java.lang.Object#toString()" + lineSeparator);
		output.append(tabulation + " */" + lineSeparator);
		output.append(tabulation + "@Override" + lineSeparator + tabulation + "public String toString() {" + lineSeparator);
		output.append(tabulation + tabulation + "return \"" + entity.getName() + " [ \"");
		for (int i = 0; i < entity.getAlAttr().size(); i++) {
			if (i == 0) {
				output.append(" + " + "\"" + entity.getAlAttr().get(i).getRawName() + " = \" + " + entity.getAlAttr().get(i).getName() + lineSeparator);
			} else if ((i + 1) == entity.getAlAttr().size() )  {
				output.append(tabulation + tabulation + tabulation + tabulation +  " + " +  "\", " + entity.getAlAttr().get(i).getName() + " = \" + " + entity.getAlAttr().get(i).getName() + " + \"]\";");
			} else {
				output.append(tabulation + tabulation + tabulation + tabulation +  " + " + "\", " + entity.getAlAttr().get(i).getName() + " = \" + " + entity.getAlAttr().get(i).getName() + lineSeparator);
			}
			if (entity.getAlAttr().get(i).isForeignKey() && entity.getAlAttr().get(i).getRawName().toLowerCase().endsWith("_cd")) {
				String locale = entity.getAlAttr().get(i).getName().substring(0, entity.getAlAttr().get(i).getName().length() - 2) + "Locale";
				output.append(tabulation + tabulation + tabulation + tabulation +  " + " + "\", " + locale + " = \" + " + locale + lineSeparator);
			}
		}
		output.append(lineSeparator + tabulation + "}" + lineSeparator + lineSeparator);
		
		output.append("}");
		
		
		return output.toString();
		
	}

}
