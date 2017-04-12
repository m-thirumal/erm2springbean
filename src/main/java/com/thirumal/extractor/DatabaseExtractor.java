package com.thirumal.extractor;

import java.util.List;

import com.thirumal.config.Configuration;
import com.thirumal.entities.Entite;

public abstract class DatabaseExtractor {
	
	private Configuration configuration;
	
	public DatabaseExtractor(Configuration configuration){
		this.configuration	= configuration;
	}
	
	public abstract List<Entite> getEntities() throws Exception;
	
	public Configuration getConfiguration(){
		return configuration;
	}

}
