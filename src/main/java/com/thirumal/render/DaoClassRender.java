package com.thirumal.render;

import java.util.ArrayList;

import com.thirumal.config.Configuration;
import com.thirumal.entities.Attribut;
import com.thirumal.entities.Entity;
import com.thirumal.utility.DbHelper;
import com.thirumal.utility.ERM2BeansHelper;
import com.thirumal.utility.ERM2BeansHelper.StringHelper;
import com.thirumal.utility.PrepareStatementBuilder;
import com.thirumal.utility.PrepareStatementBuilder.Action;

public class DaoClassRender extends BaseClassRender {
	
	private ArrayList<String> 	mandatoryPckgs; 

	public DaoClassRender(Entity entity, Configuration configuration) {
		super(entity, configuration);
		mandatoryPckgs 		= 	new ArrayList<String>(); 		
		init();		
	}	
	
	private void init(){
		addMandatoryPackage("java.sql.Connection");
		addMandatoryPackage("java.sql.PreparedStatement");
		addMandatoryPackage("java.sql.ResultSet");		
		addMandatoryPackage("java.sql.SQLException");
		addMandatoryPackage("java.util.List");
		addMandatoryPackage("org.springframework.beans.factory.annotation.Autowired");
		addMandatoryPackage("org.springframework.core.env.Environment");
		addMandatoryPackage("org.springframework.jdbc.core.JdbcTemplate");
		addMandatoryPackage("org.springframework.jdbc.core.PreparedStatementCreator");
		addMandatoryPackage("org.springframework.jdbc.core.RowMapper");
		addMandatoryPackage("org.springframework.jdbc.support.GeneratedKeyHolder");
		addMandatoryPackage("org.springframework.jdbc.support.KeyHolder");
		addMandatoryPackage("org.springframework.stereotype.Repository");
		addMandatoryPackage("com.enkindle.persistence.GenericDao");
	}
	
	public void addMandatoryPackage(String classCanonicalName){
		mandatoryPckgs.add("import " + classCanonicalName + ";");
	}
	

	@Override
	public String render() throws Exception {
		
		StringBuffer 		output				=	new StringBuffer();
		Attribut 			attribut			=	null;
		ArrayList<Attribut> attributes 			=	getEntity().getAlAttr();
		String 				classNameLowerCase	=	getEntity().getName().toLowerCase();
		String 				methodName 			= 	null;
		String 				className 			= 	Configuration.getDaoFileName(getEntity().getName());
		String 				query				=	null;
		String 				preparementSet 		= 	null;
		String 				interfacesToOuput 	= 	null;
		String				targetDirectory		=	Configuration.getTargetDirectory();
		String 				modelFileName 		= 	Configuration.getModelFileName(getEntity().getName());
		String				lineSeparator		=	StringHelper.lineSeparator;
		String				tabulation			=	StringHelper.tabulation;
		String				pkInJavaType	    =	null;
		String				pkInRaw	    =	null;
		String				ignoreRowCreationDate =   "rowCreationDate";
		String              ignoreRowCreatedBy = "rowCreatedBy";
		       
		
		ERM2BeansHelper.writeFile("#"+className, targetDirectory, "queries.properties", true);
			
		for (Attribut attr : attributes){
			if(attr.isPrimaryKey()){
				pkInJavaType = attr.getName();
				pkInRaw = attr.getRawName();
				break;
			}
		}
		
		output.append("package "+getEntity().getDaoPackage()+";"+lineSeparator);
		output.append(lineSeparator);
		output.append("import com.enkindle.persistence.model." + modelFileName + ";" + lineSeparator);
		for(String pckg : mandatoryPckgs){
			output.append(pckg+lineSeparator);
		}
		output.append(lineSeparator);
		if (getEntity().hasInterface()) {
			interfacesToOuput = " implements ";
			String interfaceToOuput = null;
			ArrayList<String> interFaces = getEntity().getInterfaces();
			for (int i = 0, interfaceCanonicalNamesLenght = interFaces.size(); i < interfaceCanonicalNamesLenght; i++) {
				interfaceToOuput = interFaces.get(i);
				interfacesToOuput += interfaceToOuput + (i == (interfaceCanonicalNamesLenght - 1) ? " ": ", ");
			}
		}

		output.append("/**" + lineSeparator  + " *" + lineSeparator + " * @author Thirumal" + lineSeparator + " *" + lineSeparator + " */" + lineSeparator);
		output.append("@Repository" + lineSeparator);
		output.append("public class " + className
				+ (getEntity().hasParent() ? " extends " + getEntity().getParentClass() : "")
				+ (interfacesToOuput != null ? interfacesToOuput : "") + " {"
				+ lineSeparator + lineSeparator);
		output.append(tabulation + "private final JdbcTemplate jdbcTemplate;" + lineSeparator + lineSeparator + tabulation + "@Autowired" + lineSeparator);
		output.append(tabulation + "private Environment environment;" + lineSeparator + lineSeparator + tabulation + "@Autowired" +
				lineSeparator);
		// Constructor
		output.append(tabulation+"public " + className + "(JdbcTemplate jdbcTemplate) {" + lineSeparator);
		output.append(tabulation+tabulation+"this.jdbcTemplate = jdbcTemplate;" + lineSeparator);
		output.append(tabulation+"}" + lineSeparator + lineSeparator);
		
		/* Create */
		output.append(tabulation+"@Override" + lineSeparator);
		output.append(tabulation+"public "+ modelFileName + " create(" + modelFileName  + " " + classNameLowerCase + ") {"+ lineSeparator);
		output.append(tabulation + tabulation + "KeyHolder holder = new GeneratedKeyHolder();" + lineSeparator +
				tabulation + tabulation + "jdbcTemplate.update(new PreparedStatementCreator()  {" + lineSeparator +
				tabulation + tabulation + tabulation + "@Override" + lineSeparator +
				tabulation + tabulation + tabulation + "public PreparedStatement createPreparedStatement(Connection con) throws SQLException {" + lineSeparator +
				tabulation + tabulation + tabulation + "PreparedStatement ps = con.prepareStatement(environment.getProperty(\"" + 
				modelFileName + ".create\"), new String[] { \"" + pkInRaw + "\" });" + lineSeparator);
		int psIndex = 0;
		for (int i = 0, attributesLenght = attributes.size(); i < attributesLenght; i++) {
			attribut = attributes.get(i);
			if (attribut.getName().equalsIgnoreCase(ignoreRowCreationDate) || attribut.getSqlType().equalsIgnoreCase("uuid")) {
				continue;
			}
			if (!attribut.isPrimaryKey() || !attribut.isAutoincrement()) {
				psIndex++;
				if (!attribut.getJavaType().equalsIgnoreCase("Boolean")) {
					methodName = StringHelper.saniziteForClassName(attribut.getName());
					methodName = "get" + methodName;
				} else {
					methodName = StringHelper.getMethodNameForBoolean(StringHelper.sanitizeForAttributName(attribut.getName()));
				}
				methodName += "()";
				// output.append("ps.setInt("+(i+1)+", "+classNameLowerCase+"."+methodName+");"+StringHelper.lineSeparator);
				preparementSet = DbHelper.createPreparementSet("ps", (psIndex),
						attribut.getJavaType(), attribut.getSqlType(), classNameLowerCase + "."	+ methodName, true);
				output.append(tabulation+tabulation+ tabulation + tabulation + preparementSet + lineSeparator);
			}

		}
		output.append(tabulation+tabulation+ tabulation + tabulation +"return ps;" + lineSeparator);
		output.append(tabulation+tabulation+ tabulation + "}" + lineSeparator);
		output.append(tabulation+tabulation+  "}, holder);" + lineSeparator + tabulation
				+ tabulation + "return get(holder.getKey().intValue());" + lineSeparator);
		output.append(tabulation + "}" + lineSeparator + lineSeparator );
		/* get Method */
		output.append(tabulation + "@Override" + lineSeparator);
		output.append(tabulation+"public "+ modelFileName + " get(Integer id) {" +  lineSeparator);
		output.append(tabulation + tabulation + "return jdbcTemplate.queryForObject(environment.getProperty(\"" + modelFileName + ".get\"), new Object[] { id }, new " +
				modelFileName + "RowMapper());" + lineSeparator + tabulation + "}" + lineSeparator + lineSeparator);
		/* Get where method */
		output.append(tabulation + "@Override" + lineSeparator);
		output.append(tabulation+"public "+ modelFileName + " get(Integer id, String whereClause) {" +  lineSeparator);
		output.append(tabulation + tabulation + "return jdbcTemplate.queryForObject(environment.getProperty(\"" + modelFileName + ".getBy/* whereClause*/" +
				"\"), new Object[] { whereClause }, new " + modelFileName + "RowMapper());" + lineSeparator + tabulation + "}" + lineSeparator + lineSeparator);
		/* LIST METHOD*/
		output.append(tabulation + "@Override" + lineSeparator);
		output.append(tabulation+"public List<"+ modelFileName + "> list(String id) {" +  lineSeparator);
		output.append(tabulation + tabulation + "return jdbcTemplate.query(environment.getProperty(\"" + modelFileName + ".listBy/*whereClause*/" +
				"\"), new Object[] { id }, new " + modelFileName + "RowMapper());" + lineSeparator + tabulation +  "}" + lineSeparator + lineSeparator);
		/* List method*/
		output.append(tabulation + "@Override" + lineSeparator);
		output.append(tabulation+"public List<"+ modelFileName + "> list() {" +  lineSeparator);
		output.append(tabulation + tabulation + "return jdbcTemplate.query(environment.getProperty(\"" + modelFileName + ".list" +
				"\"), new " + modelFileName + "RowMapper());" + lineSeparator + tabulation +  "}" + lineSeparator + lineSeparator);
		/* update method*/
		output.append(tabulation + "@Override" + lineSeparator);
		output.append(tabulation+"public "+ modelFileName + " update(" + modelFileName + " " + classNameLowerCase + ") {" +  lineSeparator);
		output.append(tabulation + tabulation + "jdbcTemplate.update(environment.getProperty(\"" + modelFileName + ".update\"), " + lineSeparator);
		for (int i = 0, attributesLenght = attributes.size(); i < attributesLenght; i++) {
			attribut = attributes.get(i);
			if (attribut.getName().equalsIgnoreCase(ignoreRowCreationDate) || attribut.getName().equalsIgnoreCase(ignoreRowCreatedBy)) {
				continue;
			}
			if (!attribut.isPrimaryKey() || !attribut.isAutoincrement()) {
				if (!attribut.getJavaType().equalsIgnoreCase("Boolean")) {
					methodName = StringHelper.saniziteForClassName(attribut.getName());
					methodName = "get" + methodName;
				} else {
					methodName = StringHelper.getMethodNameForBoolean(StringHelper.sanitizeForAttributName(attribut.getName()));
				}
				methodName += "()";
				if ((i + 1) < attributesLenght) {
					output.append(tabulation + tabulation + tabulation + classNameLowerCase + "." + methodName + "," + lineSeparator);
				} else {
					output.append(tabulation + tabulation + tabulation + classNameLowerCase + "." + methodName + ");" + lineSeparator);
				}
			}
		}
		
		String pkUppercase = pkInJavaType.substring(0, 1).toUpperCase() + pkInJavaType.substring(1);
		output.append(tabulation + tabulation + "return get(" + classNameLowerCase + ".get" + pkUppercase
				 + "());" + lineSeparator + tabulation + "}" + lineSeparator + lineSeparator);
		/* Delete method */
		output.append(tabulation + "@Override" + lineSeparator);
		output.append(tabulation+"public int delete("+ modelFileName + " " + classNameLowerCase + ") {" +  lineSeparator);
		output.append(tabulation + tabulation + tabulation + "return jdbcTemplate.update(environment.getProperty(\"" + 
				modelFileName + ".delete\"), " + classNameLowerCase +  ".get" + pkUppercase +  "());" + lineSeparator + tabulation + "}" + lineSeparator
				+ lineSeparator);
		/* RowMapper Class */
		output.append(tabulation + "class " + modelFileName + "RowMapper implements RowMapper<" + modelFileName + "> {" + lineSeparator + lineSeparator);
		output.append(tabulation + tabulation + "@Override" + lineSeparator);
		output.append(tabulation + tabulation + "public " + modelFileName + " mapRow(ResultSet rs, int rowNum) throws SQLException {" + lineSeparator);
		output.append(tabulation + tabulation +tabulation + modelFileName + " " + classNameLowerCase + " = new " + modelFileName + "();" + lineSeparator);

		for (int i = 0, attributesLenght = attributes.size(); i < attributesLenght; i++) {
			attribut = attributes.get(i);
			String canonicalName;
			try {
				canonicalName = DbHelper.simpleNameToCanonicalName(attribut.getJavaType());
			} catch (Exception ex) {
				throw new Exception(ex.getMessage());
			}
			output.append(tabulation + tabulation + tabulation + canonicalName + " " + attribut.getName() + " = null;"+ lineSeparator);
		}


		for (int i = 0, attributesLenght = attributes.size(); i < attributesLenght; i++) {
			attribut = attributes.get(i);
			output.append(StringHelper.lineSeparator);
			String rsCreated = DbHelper.createResulSet("rs", attribut.getJavaType(), attribut.getSqlType(),
					attribut.getRawName());
			output.append( tabulation + tabulation + tabulation + attribut.getName() + " = " + rsCreated + ";"+ lineSeparator);
			if (!attribut.getJavaType().equalsIgnoreCase("Boolean")) {
				methodName = StringHelper.saniziteForClassName(attribut
						.getName());
				methodName = "set" + methodName;
			} else {
				methodName = "set" + StringHelper.getMethodNameForBoolean(StringHelper.sanitizeForAttributName(attribut.getName()));
			}
			String setObj = classNameLowerCase + "." + methodName + "("
					+ attribut.getName() + ")";
			output.append(tabulation + tabulation + tabulation + setObj + ";" + lineSeparator);
			output.append(lineSeparator);
		}
		output.append(tabulation + tabulation + tabulation + "return "+classNameLowerCase+";"+lineSeparator);
		output.append(tabulation + tabulation + "}" + lineSeparator);
		output.append(tabulation + "}" + lineSeparator);
		output.append(lineSeparator);
		output.append("}" + lineSeparator);
		output.append(lineSeparator);
		//output.append((tabulation + tabulation + tabulation + )
		
		/* Query generation */
		query = PrepareStatementBuilder.create(getEntity(), Action.LIST);
		ERM2BeansHelper.addQueryInProp(targetDirectory, getEntity(), Action.LIST,
				query);
		
		query = PrepareStatementBuilder.create(getEntity(), Action.LIST);
		ERM2BeansHelper.addQueryInProp(targetDirectory, getEntity(), Action.LIST,
				query);		
	
		query = PrepareStatementBuilder.create(getEntity(), Action.GET);
		ERM2BeansHelper.addQueryInProp(targetDirectory,
				getEntity(), Action.GET, query);
	
		query = PrepareStatementBuilder.create(getEntity(), Action.CREATE);
		ERM2BeansHelper.addQueryInProp(targetDirectory, getEntity(),
				Action.CREATE, query);
		
		query = PrepareStatementBuilder.create(getEntity(), Action.DELETE);
		ERM2BeansHelper.addQueryInProp(targetDirectory, getEntity(),
				Action.DELETE, query);
		
		query = PrepareStatementBuilder.create(getEntity(), Action.UPDATE);
		ERM2BeansHelper.addQueryInProp(targetDirectory, getEntity(),
				Action.UPDATE, query);
		
		return output.toString();		
	}

}
