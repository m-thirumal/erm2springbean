package com.thirumal.render;

import com.thirumal.config.Configuration;
import com.thirumal.entities.Entite;

/**
 * 
 * @author Lo�c FALKLAND
 *
 */
public abstract class BaseClassRender {

	private Entite			entity;
	private Configuration	configuration;
	
	public	BaseClassRender(Entite entity, Configuration configuration){
		this.entity			= entity;
		this.configuration	=	configuration;
	}
	
	public Entite getEntity(){
		return entity;
	}
	
	public Configuration getConfiguration(){
		return configuration;
	}
	
	public abstract String render() throws Exception;

}
