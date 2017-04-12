package com.thirumal.render;

import com.thirumal.config.Configuration;
import com.thirumal.entities.Entity;

/**
 * 
 * @author Thirumal
 *
 */
public abstract class BaseClassRender {

	private Entity			entity;
	private Configuration	configuration;
	
	public	BaseClassRender(Entity entity, Configuration configuration){
		this.entity			= entity;
		this.configuration	=	configuration;
	}
	
	public Entity getEntity(){
		return entity;
	}
	
	public Configuration getConfiguration(){
		return configuration;
	}
	
	public abstract String render() throws Exception;

}
