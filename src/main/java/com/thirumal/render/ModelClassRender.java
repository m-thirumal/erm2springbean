package com.thirumal.render;

import java.util.HashMap;
import java.util.Map;

import com.thirumal.config.Configuration;
import com.thirumal.entities.Attribut;
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
		Entity each				=	getEntity();
		
		each.addInterface("java.io.Serializable");
		
		output.append("package "+each.getModelPackage()+";");
		
		output.append(lineSeparator + lineSeparator);
		
		/* avoid redondant import */
		
		HashMap<String, String> javaTypesPckgPaths = new HashMap<String, String>();
		String javaType = null;
		String pckgPath = null;
		
		for(Attribut eachattr : each.getAlAttr()){
			System.out.println("attribute: " + eachattr.getJavaPackagePath() + " " + eachattr.getJavaType() + " " + eachattr.getName() + " " + eachattr.getRawName() + " " + eachattr.getSqlType());;
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
		
		if(each.hasInterface()){
			
			interfacesToOuput = " implements ";

			String interfaceToOuput = null;
			
			for(int i = 0, interfaceCanonicalNamesLenght = each.getInterfaces().size(); i < interfaceCanonicalNamesLenght; i++){
				
				interfaceToOuput = each.getInterfaces().get(i);
				
				interfacesToOuput += interfaceToOuput + (i == (interfaceCanonicalNamesLenght -1) ? " " : ", ");
				
			}
			
			
		}
		
		output.append("/**" + lineSeparator + " * @author Thirumal" + lineSeparator + " *" + lineSeparator + " */" + lineSeparator);
		
		
		output.append("public class " + each.getName() +(each.hasParent() ? " extends "+each.getParentClass() : "")+(interfacesToOuput != null ? interfacesToOuput : "")+" {" + lineSeparator + lineSeparator);

		output.append(tabulation+"private static final long serialVersionUID = 1L;"+lineSeparator+lineSeparator);

		output.append(tabulation+"//Declarating fields" + lineSeparator);
		
		for (Attribut eachattr : each.getAlAttr()){
			//TODO: parser le nom de la bd snakeCase en camelCase
			output.append("	private " + eachattr.getJavaType() + " " + eachattr.getName() + ";" + lineSeparator);
		}
		
		
		HashMap<String, Integer> constantes = each.getConstantes();
		
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

		output.append(tabulation+ "//Default constructor" + lineSeparator);	
		output.append("	public " + each.getName() + "() {}" + lineSeparator);
		output.append(lineSeparator);
		output.append(tabulation + "//Parameterized constructor" + lineSeparator);	
		output.append("	public " + each.getName() + "(");
		
		for (int i = 0; i < each.getAlAttr().toArray().length; i++){
			output.append(each.getAlAttr().get(i).getJavaType() + " " + each.getAlAttr().get(i).getName());
			if (!(i == each.getAlAttr().toArray().length-1)){
				output.append(", ");
			}
		}
		
		output.append(") {" + lineSeparator);
		
		for (Attribut eachattr : each.getAlAttr()){
			output.append("		this." + eachattr.getName() + " = " + eachattr.getName() + ";" + lineSeparator);
		}
		
		output.append("	}" + lineSeparator);		
		output.append(lineSeparator);
		output.append(tabulation + "//Getters & Setters" + lineSeparator);
		
		String resultGetSetName = null;
		
		for (Attribut eachattr : each.getAlAttr()){
			//Getter
			output.append("	public " + eachattr.getJavaType() + " ");
			
			if(!eachattr.getJavaType().equalsIgnoreCase("Boolean")){
				resultGetSetName = "get"+StringHelper.saniziteForClassName(eachattr.getName());				
			}
			else {
				resultGetSetName = StringHelper.getMethodNameForBoolean(StringHelper.sanitizeForAttributName(eachattr.getName()));
			}
			
			
			output.append(resultGetSetName);
			output.append("() {" + lineSeparator);
			output.append("		return " + eachattr.getName() + ";" + lineSeparator);
			output.append("	}" + lineSeparator + lineSeparator);
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
		}
		
		
		output.append("}");
		
		
		return output.toString();
		
	}

}
