package com.thirumal.extractor;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.thirumal.config.Configuration;
import com.thirumal.entities.Attribute;
import com.thirumal.entities.Entity;
import com.thirumal.utility.DbHelper;


/**
 * 
 * @author Thirumal
 *
 */
public final class PostgreSQLDBExtractor extends DatabaseExtractor {

	public PostgreSQLDBExtractor(Configuration configuration) {
		super(configuration);
	}

	public List<Entity> getEntities(String schemaName) throws Exception {

		String dbName = Configuration.getDbName();
		Connection connection = Configuration.getConnection();
		DatabaseMetaData metadata = connection.getMetaData();
		ResultSet resultSet = metadata.getColumns(dbName, schemaName, null, null);
		List<Entity> alTables = new ArrayList<Entity>();
		Entity currentEntite = null;
		ArrayList<Attribute> alAttributs = null;

		String checkdenom = "";

		String tablename = null;
		String name = null;
		String type = null;
		Integer size = null;

		String tableSchem = null;
		String tablePrefix = null;

		String pkColumnName = null;
		String fkColumnName = null;
		//int i = 0;
		ArrayList<String> pkColumnNames = new ArrayList<String>();
		ArrayList<String> fkColumnNames = new ArrayList<String>();
		while (resultSet.next()) {
			tableSchem = resultSet.getString("TABLE_SCHEM");
			if (!"sys".equalsIgnoreCase(tableSchem) && !"information_schema".equalsIgnoreCase(tableSchem)
					&& !"dbo".equalsIgnoreCase(tableSchem)) {
				tablename = resultSet.getString("TABLE_NAME");
				tablePrefix = resultSet.getString("TABLE_SCHEM");
				//System.out.println("TableName  : " + tablename + "\nTablePrefix: "+ tablePrefix);
				name = resultSet.getString("COLUMN_NAME");
				type = resultSet.getString("TYPE_NAME");
				size = resultSet.getInt("COLUMN_SIZE");
				if (!checkdenom.equals(tablename)) { // runs total number of table times
					// retrieve PKs
					pkColumnNames = new ArrayList<String>();
					ResultSet rs = metadata.getPrimaryKeys(dbName, tablePrefix, tablename);
					while (rs.next()) {
						pkColumnName = rs.getString("COLUMN_NAME");
						pkColumnNames.add(pkColumnName);
					}
					// Retrive FK
					fkColumnNames = new ArrayList<String>();
					ResultSet rsFk = metadata.getImportedKeys(dbName, tablePrefix, tablename);
					while (rsFk.next()) {
						fkColumnName = rsFk.getString("FKCOLUMN_NAME");
						fkColumnNames.add(fkColumnName);
					}
					currentEntite = new Entity(dbName, tablePrefix, tablename);
					if (tablePrefix.equalsIgnoreCase("Codes")) {
						currentEntite.setCodeTable(true);
						// Table Locale_Cd does not refer to
						// Codes.Locale_Locales
						if (!"Locale_Cd".equalsIgnoreCase(tablename)) {
							currentEntite.setConstantes(
									DbHelper.retrieveCdForConstantes(connection, dbName, tablePrefix, tablename));
						}
					}
					alAttributs = new ArrayList<Attribute>();
					Attribute currentAttribut = new Attribute(name, type, size);
					for (String pkCol : pkColumnNames) {
					//	System.out.println("pkCol " + pkCol);
					//	System.out.println(currentAttribut.getName());
						if (pkCol.equalsIgnoreCase(currentAttribut.getRawName())) {
							currentAttribut.setPrimaryKey(true);
							if (DbHelper.columnIsAutoincrement(connection, dbName, tablePrefix, tablename, pkCol)) {
								currentAttribut.setAutoincrement(true);
							}
						}
					}
					for (String fkCol : fkColumnNames) {
						//	System.out.println("pkCol " + pkCol);
						//	System.out.println(currentAttribut.getName());
						if (fkCol.equalsIgnoreCase(currentAttribut.getRawName())) {
							currentAttribut.setForeignKey(true);
						}
					}
					alAttributs.add(currentAttribut);
					currentEntite.setAlAttr(alAttributs);
					if (!currentEntite.getName().equalsIgnoreCase("RndView")) {
						alTables.add(currentEntite);
						checkdenom = tablename;
					}
				} else {
					// Creation du nouvel attribut
					Attribute currentAttribut = new Attribute(name, type, size);
					for (String pkCol : pkColumnNames) {
						/*
						 * System.out.println("pkCol "+pkCol);
						 * System.out.println(currentAttribut.getName());
						 */
						if (pkCol.equalsIgnoreCase(currentAttribut.getRawName())) {
							currentAttribut.setPrimaryKey(true);
							if (DbHelper.columnIsAutoincrement(connection, dbName, tablePrefix, tablename, pkCol)) {
								currentAttribut.setAutoincrement(true);
							}
						}
					}
					for (String fkCol : fkColumnNames) {
						if (fkCol.equalsIgnoreCase(currentAttribut.getRawName())) {
							//System.out.println("fkCol "+fkCol);
							//System.out.println(currentAttribut.getName());
							currentAttribut.setForeignKey(true);
						}
					}
					alAttributs.add(currentAttribut);
					currentEntite.setAlAttr(alAttributs);
				}
			}
		}
		if (connection != null && !connection.isClosed()) {
			connection.close();
		}
		return alTables;
	}

}
