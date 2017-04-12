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
		addMandatoryPackage("org.springframework.test.context.ContextConfiguration");
		addMandatoryPackage("com.enkindle.config.SqlConfig");
		addMandatoryPackage("com.enkindle.persistance.GenericDao");
		addMandatoryPackage("com.enkindle.persistance.model.AddressTypeCd");
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
		// String 				dbName 				=	Configuration.getDbName();
		// String 				dbVersion			=	Configuration.getDbVersion();
		String				targetDirectory		=	Configuration.getTargetDirectory();
		String 				modelFileName 		= 	Configuration.getModelFileName(getEntity().getName());
		String				entityCanonicalName	=	getEntity().getModelCanonicalName();
		String				lineSeparator		=	StringHelper.lineSeparator;
		String				tabulation			=	StringHelper.tabulation;
		boolean 			hasStateCd 			= 	false;
		String 				idGetterAsStr		=	null;
		String				pk					=	null;
		String				ignoreRowCreationDate =   "rowCreationDate";
		String              ignoreRowUpdatedDate = "rowUpdatedDate";
		
		ERM2BeansHelper.writeFile("#"+className, targetDirectory, "queries.properties", true);

		for(Attribut attr : getEntity().getAlAttr()){
			
			if(attr.getRawName().equalsIgnoreCase("state_cd")){
				hasStateCd = true;
				break;
			}
			
		}
		
		output.append("package "+getEntity().getDaoPackage()+";"+lineSeparator);
		
		output.append(lineSeparator);
		
		//Add StateCd package only if necessary
		if(hasStateCd){
			addMandatoryPackage("com.enkindle.core.persistence.model.code.StateCd");
		}
		
		output.append("import com.enkindle.core.persistence.model." + modelFileName + ";" + lineSeparator);
		for(String pckg : mandatoryPckgs){
			output.append(pckg+lineSeparator);
		}


		output.append(lineSeparator+lineSeparator);


		if (getEntity().hasInterface()) {

			interfacesToOuput = " implements ";

			String interfaceToOuput = null;
			
			ArrayList<String> interFaces = getEntity().getInterfaces();

			for (int i = 0, interfaceCanonicalNamesLenght = interFaces.size(); i < interfaceCanonicalNamesLenght; i++) {

				interfaceToOuput = interFaces.get(i);

				interfacesToOuput += interfaceToOuput
						+ (i == (interfaceCanonicalNamesLenght - 1)
								? " "
								: ", ");

			}

		}

		
	//	output.append(ERM2BeansHelper.generateClassComment(dbName, dbVersion) + lineSeparator);

		output.append("public class " + className
				+ (getEntity().hasParent() ? " extends " + getEntity().getParentClass() : "")
				+ (interfacesToOuput != null ? interfacesToOuput : "") + " {"
				+ lineSeparator + lineSeparator);

		// Constructor
		output.append(tabulation+"public " + className
				+ "(Model model) {" + lineSeparator);
		output.append(tabulation+tabulation+"super(model);" + lineSeparator);
		output.append(tabulation+"}" + lineSeparator + lineSeparator);
		
		/* list without parameter */
		output.append(tabulation+"@Override" + lineSeparator);
		output.append(tabulation+"public List<Model> list() throws SomsBaseException {"+ lineSeparator);
		output.append(tabulation + tabulation + "try (WrappedEvent event = ULog.newRelativeWrappedEvent(ULog.E.DAO_LIST, WrappedLogLevel.DEBUG)" +
				lineSeparator + tabulation + tabulation + tabulation + tabulation + ".attr(ULog.A.CLASS_NAME, this.getClass().getSimpleName())" +
				lineSeparator + tabulation + tabulation + tabulation + tabulation + ".publish()) {\n" +
				lineSeparator + tabulation + tabulation + tabulation + "return listLogic();" +
				lineSeparator + tabulation + tabulation + "}" +
				lineSeparator + tabulation + tabulation + "catch (SomsBaseException e) {" +
				lineSeparator + tabulation + tabulation + tabulation + "throw e;" +
				lineSeparator + tabulation + tabulation + "}" +
				lineSeparator + tabulation + tabulation + "catch (Exception e) {" +
				lineSeparator + tabulation + tabulation + tabulation + "throw ErrorFactory.buildException(ErrorFactory.INTERNAL_SERVER_ERROR, e);" +
				lineSeparator + tabulation + tabulation + "}" +	
				lineSeparator + tabulation + "}\n\n");
		output.append(tabulation+"protected List<Model> listLogic() throws SomsBaseException {"+ lineSeparator);
		query = PrepareStatementBuilder.create(getEntity(), Action.LIST);
		ERM2BeansHelper.addQueryInProp(targetDirectory, getEntity(), Action.LIST,
				query);

		String classNameLowerCaseForList = classNameLowerCase + "s";
		
		output.append(tabulation+tabulation+"List<Model> " + classNameLowerCaseForList + " = new ArrayList<Model>();" + lineSeparator);
		output.append(tabulation+tabulation+"try (PreparedStatement ps = super.getPreparedStatement(LIST)) {" + lineSeparator);
		output.append(tabulation + tabulation + tabulation + "try (ResultSet rs = ps.executeQuery()) {" + lineSeparator);
		output.append(tabulation + tabulation + tabulation + tabulation + classNameLowerCaseForList + " = buildModelListFromResultSet(rs);"+lineSeparator);
		output.append(tabulation + tabulation + tabulation + "}" + lineSeparator);
		output.append(tabulation+tabulation+"} catch (SQLException e) {" + lineSeparator);
		output.append(tabulation+tabulation+tabulation+"throw ErrorFactory.buildException(ErrorFactory.DATABASE_EXCEPTION, e);" + lineSeparator);
		output.append(tabulation + tabulation + "}" + lineSeparator + lineSeparator);
		
		output.append(tabulation+tabulation+"return " + classNameLowerCaseForList + ";"
				+ lineSeparator);
		
		output.append(tabulation+"}" + lineSeparator);
		
		/* End of list without parameter */

		/* list */
		output.append(lineSeparator);
		output.append(tabulation+"@Override" + lineSeparator);
		output.append(tabulation+"public List<Model> list(Identifier id, String whereClause) throws SomsBaseException {"+ lineSeparator);
		output.append(tabulation + tabulation + "try (WrappedEvent event = ULog.newRelativeWrappedEvent(ULog.E.DAO_LIST, WrappedLogLevel.DEBUG)" +
				lineSeparator + tabulation + tabulation + tabulation + tabulation + ".attr(ULog.A.CLASS_NAME, this.getClass().getSimpleName())" +
				lineSeparator + tabulation + tabulation + tabulation + tabulation + ".attr(ULog.A.ID, String.valueOf(id))" +
				lineSeparator + tabulation + tabulation + tabulation + tabulation + ".attr(ULog.A.WHERE_CLAUSE, whereClause)" +
				lineSeparator + tabulation + tabulation + tabulation + tabulation + ".publish()) {\n" +
				lineSeparator + tabulation + tabulation + tabulation + "return listLogic(id, whereClause);" +
				lineSeparator + tabulation + tabulation + "}" +
				lineSeparator + tabulation + tabulation + "catch (SomsBaseException e) {" +
				lineSeparator + tabulation + tabulation + tabulation + "throw e;" +
				lineSeparator + tabulation + tabulation + "}" +
				lineSeparator + tabulation + tabulation + "catch (Exception e) {" +
				lineSeparator + tabulation + tabulation + tabulation + "throw ErrorFactory.buildException(ErrorFactory.INTERNAL_SERVER_ERROR, e);" +
				lineSeparator + tabulation + tabulation + "}" +	
				lineSeparator + tabulation + "}\n\n");
		output.append(tabulation+"protected List<Model> listLogic(Identifier id, String whereClause) throws SomsBaseException {"+ lineSeparator + lineSeparator);
		query = PrepareStatementBuilder.create(getEntity(), Action.LIST);
		ERM2BeansHelper.addQueryInProp(targetDirectory, getEntity(), Action.LIST,
				query);
		
		output.append(tabulation+tabulation+"List<Model> " + classNameLowerCaseForList + " = new ArrayList<Model>();" + lineSeparator);
		output.append(tabulation+tabulation+"try (PreparedStatement ps = super.getPreparedStatement(LIST + whereClause)) {" + lineSeparator);
		preparementSet = DbHelper.createPreparementSet("ps", 1, "Integer", null,
				"Integer.parseInt(id.getId())", false);
		output.append(tabulation+tabulation+tabulation+preparementSet + lineSeparator);

		output.append(tabulation + tabulation + tabulation + "try (ResultSet rs = ps.executeQuery()) {" + lineSeparator);
		output.append(tabulation + tabulation + tabulation + tabulation + classNameLowerCaseForList + " = buildModelListFromResultSet(rs);"+lineSeparator);
		output.append(tabulation + tabulation + tabulation + "}" + lineSeparator);
		output.append(tabulation+tabulation+"} catch (SQLException e) {" + lineSeparator);
		output.append(tabulation+tabulation+tabulation+"throw ErrorFactory.buildException(ErrorFactory.DATABASE_EXCEPTION, e);" + lineSeparator);
		output.append(tabulation + tabulation + "}" + lineSeparator + lineSeparator);
		
		output.append(tabulation+tabulation+"return " + classNameLowerCaseForList + ";"
				+ lineSeparator);
		
		output.append(tabulation+"}" + lineSeparator);

		/* = list */

		output.append(lineSeparator);

		/* get */

		output.append(tabulation+"@Override" + StringHelper.lineSeparator);
		output.append(tabulation+"public Model get(Identifier id) throws SomsBaseException {"+ lineSeparator);
		output.append(tabulation + tabulation + "try (WrappedEvent event = ULog.newRelativeWrappedEvent(ULog.E.DAO_GET, WrappedLogLevel.DEBUG)" +
				lineSeparator + tabulation + tabulation + tabulation + tabulation + ".attr(ULog.A.CLASS_NAME, this.getClass().getSimpleName())" +
				lineSeparator + tabulation + tabulation + tabulation + tabulation + ".attr(ULog.A.ID, String.valueOf(id))" +
				lineSeparator + tabulation + tabulation + tabulation + tabulation + ".publish()) {\n" +
				lineSeparator + tabulation + tabulation + tabulation + "return getLogic(id);" +
				lineSeparator + tabulation + tabulation + "}" +
				lineSeparator + tabulation + tabulation + "catch (SomsBaseException e) {" +
				lineSeparator + tabulation + tabulation + tabulation + "throw e;" +
				lineSeparator + tabulation + tabulation + "}" +
				lineSeparator + tabulation + tabulation + "catch (Exception e) {" +
				lineSeparator + tabulation + tabulation + tabulation + "throw ErrorFactory.buildException(ErrorFactory.INTERNAL_SERVER_ERROR, e);" +
				lineSeparator + tabulation + tabulation + "}" +	
				lineSeparator + tabulation + "}\n\n");
		output.append(tabulation + "protected Model getLogic(Identifier id) throws SomsBaseException {"+ lineSeparator);
		output.append(tabulation + tabulation + modelFileName + " " + classNameLowerCase + " = new " + modelFileName + "();" + lineSeparator);
		output.append(tabulation + tabulation + "try (PreparedStatement ps = super.getPreparedStatement(GET)) {" + lineSeparator);
		output.append(tabulation + tabulation + tabulation + "ps.setInt(1, Integer.parseInt(id.getId()));" + lineSeparator);
		output.append(tabulation + tabulation + tabulation + "try (ResultSet rs = ps.executeQuery()) {" + lineSeparator);
		output.append(tabulation + tabulation + tabulation + tabulation + "if ((rs != null) && rs.next()) {" + lineSeparator);
		output.append(tabulation + tabulation + tabulation + tabulation + tabulation + classNameLowerCase + " = (" + modelFileName + ") buildModelFromResultSet(rs);" + lineSeparator);
		output.append(tabulation + tabulation + tabulation + tabulation + "}" + lineSeparator);
		output.append(tabulation + tabulation + tabulation + "}" + lineSeparator);
		output.append(tabulation + tabulation + "}" + lineSeparator);
		output.append(tabulation + tabulation + "catch (NumberFormatException e) {" + lineSeparator);
		output.append(tabulation + tabulation + tabulation + "throw ErrorFactory.buildException(ErrorFactory.DATABASE_EXCEPTION);" + lineSeparator);
		output.append(tabulation + tabulation + "}" + lineSeparator);
		output.append(tabulation + tabulation + "catch (SQLException e) {" + lineSeparator);
		output.append(tabulation + tabulation + tabulation + "throw ErrorFactory.buildException(ErrorFactory.DATABASE_EXCEPTION);" + lineSeparator);
		output.append(tabulation + tabulation + "}" + lineSeparator);
		output.append(tabulation + tabulation + "return " + classNameLowerCase + ";" + lineSeparator);
		output.append(tabulation + "}" + lineSeparator);
		output.append(lineSeparator);
		
		output.append(tabulation+"@Override" + StringHelper.lineSeparator);
		output.append(tabulation+"public Model get(Identifier id, String whereClause) throws SomsBaseException {"+ lineSeparator);
		output.append(tabulation + tabulation + "try (WrappedEvent event = ULog.newRelativeWrappedEvent(ULog.E.DAO_GET, WrappedLogLevel.DEBUG)" +
				lineSeparator + tabulation + tabulation + tabulation + tabulation + ".attr(ULog.A.CLASS_NAME, this.getClass().getSimpleName())" +
				lineSeparator + tabulation + tabulation + tabulation + tabulation + ".attr(ULog.A.ID, String.valueOf(id))" +
				lineSeparator + tabulation + tabulation + tabulation + tabulation + ".attr(ULog.A.WHERE_CLAUSE, whereClause)" +
				lineSeparator + tabulation + tabulation + tabulation + tabulation + ".publish()) {\n" +
				lineSeparator + tabulation + tabulation + tabulation + "return getLogic(id, whereClause);" +
				lineSeparator + tabulation + tabulation + "}" +
				lineSeparator + tabulation + tabulation + "catch (SomsBaseException e) {" +
				lineSeparator + tabulation + tabulation + tabulation + "throw e;" +
				lineSeparator + tabulation + tabulation + "}" +
				lineSeparator + tabulation + tabulation + "catch (Exception e) {" +
				lineSeparator + tabulation + tabulation + tabulation + "throw ErrorFactory.buildException(ErrorFactory.INTERNAL_SERVER_ERROR, e);" +
				lineSeparator + tabulation + tabulation + "}" +	
				lineSeparator + tabulation + "}\n\n");
		output.append(tabulation + "protected Model getLogic(Identifier id, String whereClause) throws SomsBaseException {" + lineSeparator);
		
		query = PrepareStatementBuilder.create(getEntity(), Action.GET);
		ERM2BeansHelper.addQueryInProp(targetDirectory,
				getEntity(), Action.GET, query);
		
		output.append(lineSeparator);
		output.append(tabulation + tabulation + modelFileName + " " + classNameLowerCase + " = new " + modelFileName + "();" + lineSeparator);
		output.append(tabulation + tabulation + "try (PreparedStatement ps = super.getPreparedStatement(GET+ whereClause)) {"+ lineSeparator);
		for (int i = 0, attributesLenght = attributes.size(); i < attributesLenght; i++) {

			attribut = attributes.get(i);

			if (attribut.isPrimaryKey()) {

				if (!attribut.getJavaType().equalsIgnoreCase("Boolean")) {
					methodName = StringHelper.saniziteForClassName(attribut
							.getName());
					methodName = "get" + methodName;
				} else {
					methodName = StringHelper.getMethodNameForBoolean(StringHelper.sanitizeForAttributName(attribut.getName()));
				}

				methodName += "()";

				// output.append("ps.setInt("+(i+1)+", "+classNameLowerCase+"."+methodName+");"+StringHelper.lineSeparator);

				preparementSet = DbHelper.createPreparementSet("ps", (i + 1), "Integer", attribut.getSqlType(),
						"Integer.parseInt(id.getId())", false);

				output.append(tabulation+tabulation+tabulation+preparementSet + lineSeparator);
				break;

			}

		}
		output.append(tabulation + tabulation + tabulation + "try (ResultSet rs = ps.executeQuery()) {" + lineSeparator);
		output.append(tabulation + tabulation + tabulation + tabulation + "if ((rs != null) && rs.next()) {" + lineSeparator);
		output.append(tabulation + tabulation + tabulation + tabulation + tabulation + classNameLowerCase + " = (" + modelFileName + ") buildModelFromResultSet(rs);" + lineSeparator);
		output.append(tabulation + tabulation + tabulation + tabulation + "}" + lineSeparator);
		output.append(tabulation + tabulation + tabulation + "}" + lineSeparator);
		output.append(tabulation + tabulation+"}" + lineSeparator);
		
		output.append(tabulation + tabulation + "catch (NumberFormatException e) {" + lineSeparator);
		output.append(tabulation + tabulation + tabulation + "throw ErrorFactory.buildException(ErrorFactory.DATABASE_EXCEPTION);" + lineSeparator);
		output.append(tabulation + tabulation + "}" + lineSeparator);
		output.append(tabulation + tabulation + "catch (SQLException e) {" + lineSeparator);
		output.append(tabulation + tabulation + tabulation + "throw ErrorFactory.buildException(ErrorFactory.DATABASE_EXCEPTION);" + lineSeparator);
		output.append(tabulation + tabulation + "}" + lineSeparator);
		output.append(lineSeparator);
		output.append(tabulation+tabulation+"return " + classNameLowerCase + ";" + lineSeparator);
		output.append(tabulation+"}" + lineSeparator);
		
		/* create */
		
		output.append(lineSeparator);

		output.append(tabulation+"@Override" + lineSeparator);
		output.append(tabulation+"public Model create(Model model, Identifier id) throws SomsBaseException {"+ lineSeparator);
		output.append(tabulation + tabulation + "try (WrappedEvent event = ULog.newRelativeWrappedEvent(ULog.E.DAO_CREATE, WrappedLogLevel.DEBUG)" +
				lineSeparator + tabulation + tabulation + tabulation + tabulation + ".attr(ULog.A.CLASS_NAME, this.getClass().getSimpleName())" +
				lineSeparator + tabulation + tabulation + tabulation + tabulation + ".attr(ULog.A.ID, String.valueOf(id))" +
				lineSeparator + tabulation + tabulation + tabulation + tabulation + ".publish()) {\n" +
				lineSeparator + tabulation + tabulation + tabulation + "return createLogic(model, id);" +
				lineSeparator + tabulation + tabulation + "}" +
				lineSeparator + tabulation + tabulation + "catch (SomsBaseException e) {" +
				lineSeparator + tabulation + tabulation + tabulation + "throw e;" +
				lineSeparator + tabulation + tabulation + "}" +
				lineSeparator + tabulation + tabulation + "catch (Exception e) {" +
				lineSeparator + tabulation + tabulation + tabulation + "throw ErrorFactory.buildException(ErrorFactory.INTERNAL_SERVER_ERROR, e);" +
				lineSeparator + tabulation + tabulation + "}" +	
				lineSeparator + tabulation + "}\n\n");
		output.append(tabulation + "protected Model createLogic(Model model, Identifier id) throws SomsBaseException {"+ lineSeparator);
		output.append(tabulation + tabulation + modelFileName + " " + classNameLowerCase + " = new " + modelFileName + "();" + lineSeparator);
		query = PrepareStatementBuilder.create(getEntity(), Action.CREATE);
		ERM2BeansHelper.addQueryInProp(targetDirectory, getEntity(),
				Action.CREATE, query);
		
//		query = PrepareStatementBuilder.create(getEntity(), Action.GET_LAST);
//		ERM2BeansHelper.addQueryInProp(targetDirectory, getEntity(),
//				Action.GET_LAST, query);
		
		output.append(lineSeparator);

		output.append(tabulation + tabulation+"try (PreparedStatement ps = fillPreparedStatementFromModel(super.getInsertPreparedStatement(CREATE), model)) {"
				+ lineSeparator);
		output.append(tabulation + tabulation + tabulation + "ps.executeUpdate();" + lineSeparator);

		output.append(tabulation + tabulation + tabulation + "try (ResultSet resultKeys = ps.getGeneratedKeys()) {" + lineSeparator);
		
		output.append(tabulation + tabulation + tabulation + tabulation + "if (resultKeys != null && resultKeys.next()) {" + lineSeparator);

		output.append(tabulation + tabulation + tabulation + tabulation + tabulation + "int key = resultKeys.getInt(1);" + lineSeparator);

		output.append(tabulation + tabulation + tabulation + tabulation + tabulation + classNameLowerCase + " = (" + modelFileName + ") get(new Identifier(key));" + lineSeparator);
	
		output.append(tabulation + tabulation + tabulation + tabulation + "}" + lineSeparator);

		output.append(tabulation + tabulation + tabulation + "}" + lineSeparator);

		output.append(tabulation + tabulation + "} catch (SQLException e) {" + lineSeparator);
		output.append(tabulation + tabulation + tabulation + "throw ErrorFactory.buildException(ErrorFactory.DATABASE_EXCEPTION, e);" + lineSeparator);
		output.append(tabulation + tabulation + "}" + lineSeparator + lineSeparator);
		
		output.append(tabulation+tabulation+"return " + classNameLowerCase + ";"
				+ lineSeparator);
		
		output.append(tabulation+"}" + lineSeparator);

		
		/* = create */

		output.append(lineSeparator);
		
		/* delete permanently */
		
		output.append(tabulation+"@Override" + lineSeparator);
		output.append(tabulation+"public int deletePermanently(Identifier id, boolean cascade) throws SomsBaseException {"+ lineSeparator);
		output.append(tabulation + tabulation + "try (WrappedEvent event = ULog.newRelativeWrappedEvent(ULog.E.DAO_CREATE, WrappedLogLevel.DEBUG)" +
				lineSeparator + tabulation + tabulation + tabulation + tabulation + ".attr(ULog.A.CLASS_NAME, this.getClass().getSimpleName())" +
				lineSeparator + tabulation + tabulation + tabulation + tabulation + ".attr(ULog.A.ID, String.valueOf(id))" +
				lineSeparator + tabulation + tabulation + tabulation + tabulation + ".attr(ULog.A.CASCADE, cascade)" +
				lineSeparator + tabulation + tabulation + tabulation + tabulation + ".publish()) {\n" +
				lineSeparator + tabulation + tabulation + tabulation + "return deletePermanentlyLogic(id, cascade);" +
				lineSeparator + tabulation + tabulation + "}" +
				lineSeparator + tabulation + tabulation + "catch (SomsBaseException e) {" +
				lineSeparator + tabulation + tabulation + tabulation + "throw e;" +
				lineSeparator + tabulation + tabulation + "}" +
				lineSeparator + tabulation + tabulation + "catch (Exception e) {" +
				lineSeparator + tabulation + tabulation + tabulation + "throw ErrorFactory.buildException(ErrorFactory.INTERNAL_SERVER_ERROR, e);" +
				lineSeparator + tabulation + tabulation + "}" +	
				lineSeparator + tabulation + "}\n\n");
		output.append(tabulation+"protected int deletePermanentlyLogic(Identifier id, boolean cascade) throws SomsBaseException {"+ lineSeparator);
		
		query = PrepareStatementBuilder.create(getEntity(), Action.DELETE_PERMANENTLY);
		ERM2BeansHelper.addQueryInProp(targetDirectory, getEntity(),
				Action.DELETE_PERMANENTLY, query);
		
		output.append(tabulation + tabulation + "int result = -1;" + lineSeparator);
		output.append(tabulation + tabulation + "try (PreparedStatement ps = super.getPreparedStatement(DELETE_PERMANENTLY)) {"+ lineSeparator);
		output.append(tabulation + tabulation + tabulation + "ps.setInt(1, Integer.parseInt(id.getId()));" + lineSeparator);
		output.append(tabulation + tabulation + tabulation + "result = ps.executeUpdate();" + lineSeparator);
		output.append(tabulation + tabulation + "} catch (SQLException e) {" + lineSeparator);
		output.append(tabulation + tabulation + tabulation +"throw ErrorFactory.buildException(ErrorFactory.DATABASE_EXCEPTION, e);" +lineSeparator);
		output.append(tabulation + tabulation + "}" + lineSeparator);
		
		output.append(lineSeparator);
		
		output.append(tabulation + tabulation + "return result;" + lineSeparator);
		output.append(tabulation + "}" + lineSeparator);
		
		/*= delete permanently */
		
		/* delete permanently whereClause */
		
		output.append(tabulation+"@Override" + lineSeparator);
		output.append(tabulation+"public int deletePermanently(Identifier id, String whereClause, boolean cascade) throws SomsBaseException {"+ lineSeparator);
		output.append(tabulation + tabulation + "try (WrappedEvent event = ULog.newRelativeWrappedEvent(ULog.E.DAO_CREATE, WrappedLogLevel.DEBUG)" +
				lineSeparator + tabulation + tabulation + tabulation + tabulation + ".attr(ULog.A.CLASS_NAME, this.getClass().getSimpleName())" +
				lineSeparator + tabulation + tabulation + tabulation + tabulation + ".attr(ULog.A.ID, String.valueOf(id))" +
				lineSeparator + tabulation + tabulation + tabulation + tabulation + ".attr(ULog.A.WHERE_CLAUSE, whereClause)" +
				lineSeparator + tabulation + tabulation + tabulation + tabulation + ".attr(ULog.A.CASCADE, cascade)" +
				lineSeparator + tabulation + tabulation + tabulation + tabulation + ".publish()) {\n" +
				lineSeparator + tabulation + tabulation + tabulation + "return deletePermanentlyLogic(id, whereClause, cascade);" +
				lineSeparator + tabulation + tabulation + "}" +
				lineSeparator + tabulation + tabulation + "catch (SomsBaseException e) {" +
				lineSeparator + tabulation + tabulation + tabulation + "throw e;" +
				lineSeparator + tabulation + tabulation + "}" +
				lineSeparator + tabulation + tabulation + "catch (Exception e) {" +
				lineSeparator + tabulation + tabulation + tabulation + "throw ErrorFactory.buildException(ErrorFactory.INTERNAL_SERVER_ERROR, e);" +
				lineSeparator + tabulation + tabulation + "}" +	
				lineSeparator + tabulation + "}\n\n");
		output.append(tabulation + "protected int deletePermanentlyLogic(Identifier id, String whereClause, boolean cascade) throws SomsBaseException {"+ lineSeparator);
		output.append(tabulation + tabulation + "int result = -1;" + lineSeparator);
		output.append(tabulation + tabulation + "try (PreparedStatement ps = super.getPreparedStatement(DELETE_PERMANENTLY + whereClause)) {"+ lineSeparator);
		output.append(tabulation + tabulation + tabulation + "ps.setInt(1, Integer.parseInt(id.getId()));" + lineSeparator);
		output.append(tabulation + tabulation + tabulation + "result = ps.executeUpdate();" + lineSeparator);
		output.append(tabulation + tabulation + "} catch (SQLException e) {" + lineSeparator);
		output.append(tabulation + tabulation + tabulation +"throw ErrorFactory.buildException(ErrorFactory.DATABASE_EXCEPTION, e);" +lineSeparator);
		output.append(tabulation + tabulation + "}" + lineSeparator);
		
		output.append(lineSeparator);
		
		output.append(tabulation + tabulation + "return result;" + lineSeparator);
		output.append(tabulation + "}" + lineSeparator);
		
		/*= delete permanently whereClause */
		
		output.append(lineSeparator);

		/* update */

		output.append(tabulation + "@Override" + lineSeparator);
		output.append(tabulation + "public Model update(Model model, Identifier id) throws SomsBaseException {"+ lineSeparator);
		output.append(tabulation + tabulation + "try (WrappedEvent event = ULog.newRelativeWrappedEvent(ULog.E.DAO_UPDATE, WrappedLogLevel.DEBUG)" +
				lineSeparator + tabulation + tabulation + tabulation + tabulation + ".attr(ULog.A.CLASS_NAME, this.getClass().getSimpleName())" +
				lineSeparator + tabulation + tabulation + tabulation + tabulation + ".attr(ULog.A.ID, String.valueOf(id))" +
				lineSeparator + tabulation + tabulation + tabulation + tabulation + ".publish()) {\n" +
				lineSeparator + tabulation + tabulation + tabulation + "return updateLogic(model, id);" +
				lineSeparator + tabulation + tabulation + "}" +
				lineSeparator + tabulation + tabulation + "catch (SomsBaseException e) {" +
				lineSeparator + tabulation + tabulation + tabulation + "throw e;" +
				lineSeparator + tabulation + tabulation + "}" +
				lineSeparator + tabulation + tabulation + "catch (Exception e) {" +
				lineSeparator + tabulation + tabulation + tabulation + "throw ErrorFactory.buildException(ErrorFactory.INTERNAL_SERVER_ERROR, e);" +
				lineSeparator + tabulation + tabulation + "}" +	
				lineSeparator + tabulation + "}\n\n");
		output.append(tabulation + "protected Model updateLogic(Model model, Identifier id) throws SomsBaseException {"+ lineSeparator);
		output.append(tabulation + tabulation + modelFileName + " " + classNameLowerCase + " = (" + modelFileName + ") model;" + lineSeparator);
		
		query = PrepareStatementBuilder.create(getEntity(), Action.UPDATE);
		ERM2BeansHelper.addQueryInProp(targetDirectory, getEntity(),
				Action.UPDATE, query);
		
		output.append(tabulation + tabulation + "try (PreparedStatement ps = super.getPreparedStatement(UPDATE)) {"+ lineSeparator);
		
		output.append(tabulation + tabulation + tabulation + "fillPreparedStatementFromModel(ps, model);" + lineSeparator);
		
		pk = null;
		
		for (Attribut attr : attributes){
			if(attr.isPrimaryKey()){
				pk = attr.getName();
				break;
			}
		}
		
		idGetterAsStr = classNameLowerCase+".get"+StringHelper.saniziteForClassName(pk)+"()";
		output.append(tabulation + tabulation + tabulation + "ps.setInt(ps.getParameterMetaData().getParameterCount(), "+idGetterAsStr+");" + lineSeparator);
		output.append(tabulation + tabulation + tabulation + "ps.executeUpdate();" + lineSeparator);
		output.append(tabulation + tabulation + tabulation + classNameLowerCase+" = ("
				+ modelFileName + ") get(new Identifier(new NamedId(\"\",\"\"+"+idGetterAsStr+")));" + lineSeparator);

		output.append(tabulation + tabulation + "} catch (SQLException e) {" + lineSeparator);
		output.append(tabulation + tabulation + tabulation +"throw ErrorFactory.buildException(ErrorFactory.DATABASE_EXCEPTION, e);" +lineSeparator);
		output.append(tabulation + tabulation + "}" + lineSeparator);

		output.append(lineSeparator);
		
		output.append(tabulation + tabulation + "return " + classNameLowerCase + ";" + lineSeparator);
		
		output.append(tabulation+"}" + lineSeparator);

		/* = update */

		output.append(StringHelper.lineSeparator);
		

		if(hasStateCd){
			
			/* delete */

			output.append(tabulation+"@Override" + lineSeparator);
			output.append(tabulation+"public Model delete(Model model, Identifier id) throws SomsBaseException {"+ lineSeparator);
			
			output.append(lineSeparator);
			
			output.append(tabulation+tabulation+getEntity().getModelPackage() + "."
					+ getEntity().getName()+ " " + getEntity().getName().toLowerCase() +" = ("+getEntity().getModelPackage() + "."
					+ getEntity().getName()+") model;"+lineSeparator);
			output.append(tabulation+tabulation+classNameLowerCase+".setStateCd(StateCd.DELETED);"+lineSeparator);
			
			output.append(lineSeparator);
			
			output.append(tabulation+tabulation+"return update("+classNameLowerCase+", id);"+lineSeparator);
			
			output.append(lineSeparator);
			
			output.append(tabulation+"}" + lineSeparator);

			/* = delete */
			
			output.append(lineSeparator);
			
			/* active */

			output.append(tabulation+"@Override" + lineSeparator);
			output.append(tabulation+"public Model activate(Model model, Identifier id) throws SomsBaseException {" + lineSeparator);
			output.append(lineSeparator);
			output.append(tabulation+tabulation+entityCanonicalName + " " + classNameLowerCase +" = ("+entityCanonicalName+") model;"+lineSeparator);
			output.append(tabulation+tabulation+classNameLowerCase+".setStateCd(StateCd.ACTIVE);"+lineSeparator);
			
			output.append(lineSeparator);
			
			output.append(tabulation+tabulation+"return update("+classNameLowerCase+", id);"+lineSeparator);
			
			output.append(lineSeparator);
			
			output.append(tabulation+"}" + lineSeparator);

			/* active */
			
			output.append(lineSeparator);
			
			/* deActive */

			output.append(tabulation+"@Override" + lineSeparator);
			output.append(tabulation+"public Model deActivate(Model model, Identifier id) throws SomsBaseException {" + lineSeparator);
			
			output.append(lineSeparator);
			
			output.append(tabulation+tabulation+entityCanonicalName+ " " + classNameLowerCase +" = ("+entityCanonicalName+") model;"+lineSeparator);
			output.append(tabulation+tabulation+classNameLowerCase+".setStateCd(StateCd.INACTIVE);"+lineSeparator);
			
			output.append(lineSeparator);
			
			output.append(tabulation+tabulation+"return update("+classNameLowerCase+", id);"+lineSeparator);
			
			output.append(lineSeparator);
			
			output.append(tabulation+"}" + lineSeparator);

			/* deActive */
			
			
		}

		output.append(lineSeparator);
		// buildModelFromResultSet
		output.append(tabulation+"@Override"+lineSeparator);
		output.append(tabulation+"protected Model buildModelFromResultSet(ResultSet rs) throws SQLException {"+lineSeparator);
		
		output.append(lineSeparator);
		
		output.append(tabulation + tabulation + modelFileName + " " + classNameLowerCase + " = new "+ modelFileName + "();" + lineSeparator);
		
		for (int i = 0, attributesLenght = attributes.size(); i < attributesLenght; i++) {
			attribut = attributes.get(i);
			String canonicalName;
			try {
				canonicalName = DbHelper.simpleNameToCanonicalName(attribut.getJavaType());
			} catch (Exception ex) {
				throw new Exception(ex.getMessage());
			}
			output.append(tabulation+tabulation+canonicalName + " " + attribut.getName() + " = null;"+ lineSeparator);
		}


		for (int i = 0, attributesLenght = attributes.size(); i < attributesLenght; i++) {
			attribut = attributes.get(i);
			output.append(StringHelper.lineSeparator);
			String rsCreated = DbHelper.createResulSet("rs", attribut.getJavaType(), attribut.getSqlType(),
					attribut.getRawName());
			output.append(tabulation+tabulation+attribut.getName() + " = " + rsCreated + ";"+ lineSeparator);
			if (!attribut.getJavaType().equalsIgnoreCase("Boolean")) {
				methodName = StringHelper.saniziteForClassName(attribut
						.getName());
				methodName = "set" + methodName;
			} else {
				methodName = "set"
						+ StringHelper.getMethodNameForBoolean(StringHelper
								.sanitizeForAttributName(attribut.getName()));
			}
			String setObj = classNameLowerCase + "." + methodName + "("
					+ attribut.getName() + ")";
			output.append(tabulation+tabulation+setObj + ";" + lineSeparator);
			output.append(lineSeparator);
		}
		output.append(tabulation+tabulation+"return "+classNameLowerCase+";"+lineSeparator);
		output.append(lineSeparator);
		output.append(tabulation+"}"+lineSeparator);
		
		output.append(lineSeparator);
		// fillPreparedStatementFromModel
		output.append(tabulation+"@Override"+lineSeparator);
		output.append(tabulation+"protected PreparedStatement fillPreparedStatementFromModel(PreparedStatement ps, Model model) throws SQLException{"+lineSeparator);
		output.append(lineSeparator);
		output.append(tabulation + tabulation + modelFileName + " " + classNameLowerCase + " = ("+ modelFileName +") model;" + lineSeparator);
		output.append(lineSeparator);
		int statementIndex = 0;
		for (int i = 0, attributesLenght = attributes.size(); i < attributesLenght; i++) {
			attribut = attributes.get(i);
			if (attribut.getName().equalsIgnoreCase(ignoreRowCreationDate) || attribut.getName().equalsIgnoreCase(ignoreRowUpdatedDate)) {
				continue;
			}
			if (!attribut.isPrimaryKey() || !attribut.isAutoincrement()) {
				statementIndex++;
				if (!attribut.getJavaType().equalsIgnoreCase("Boolean")) {
					methodName = StringHelper.saniziteForClassName(attribut.getName());
					methodName = "get" + methodName;
				} else {
					methodName = StringHelper.getMethodNameForBoolean(StringHelper.sanitizeForAttributName(attribut.getName()));
				}
				methodName += "()";
				// output.append("ps.setInt("+(i+1)+", "+classNameLowerCase+"."+methodName+");"+StringHelper.lineSeparator);
				preparementSet = DbHelper.createPreparementSet("ps", (statementIndex),
						attribut.getJavaType(), attribut.getSqlType(), classNameLowerCase + "."	+ methodName, true);
				output.append(tabulation+tabulation+preparementSet + lineSeparator);
			}

		}

		output.append(tabulation+tabulation+"return ps;"+lineSeparator);
		
		output.append(lineSeparator);
		
		output.append(tabulation+"}"+lineSeparator);
		
		output.append(lineSeparator);
		
		output.append(tabulation+"@Override"+lineSeparator);
		output.append(tabulation+"protected List<Model> buildModelListFromResultSet(ResultSet rs) throws SQLException {"+lineSeparator);
		
		output.append(lineSeparator);
		
		output.append(tabulation+tabulation+"List<Model> models = new ArrayList<Model>();"+lineSeparator);
		
		output.append(lineSeparator);
		
		output.append(tabulation+tabulation+"while(rs.next()){"+lineSeparator);
		output.append(tabulation+tabulation+tabulation+"models.add(buildModelFromResultSet(rs));"+lineSeparator);
		output.append(tabulation+tabulation+"}");
		
		output.append(lineSeparator);
		
		output.append(tabulation+tabulation+"return models;"+lineSeparator);
		
		output.append(lineSeparator);
		
		output.append(tabulation+"}"+lineSeparator);
		
		output.append(lineSeparator);
		output.append(lineSeparator);
		
		output.append("}");

		return output.toString();
		
	}

}
