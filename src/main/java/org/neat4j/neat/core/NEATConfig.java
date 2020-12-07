/*
 * Created on Sep 29, 2004
 *
 */
package org.neat4j.neat.core;

import org.neat4j.core.AIConfig;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Holds an object representation of the NEAT config
 * @author MSimmerson
 *
 */
public class NEATConfig implements AIConfig {
	private Map<String, Object> config;


	//Make copy of config
	public NEATConfig (AIConfig originalConfig){
		this.config = new HashMap(originalConfig.getMap());
	}


	public NEATConfig(HashMap config) {
		this.config = new HashMap(config);
	}

	// default
	public NEATConfig() {
		this.config = new HashMap();
	}

	public NEATConfig(String filePath) {
		this.config = new HashMap();
	}


	public Object getConfigElementByName(String elementKey) {
		return this.config.get(elementKey);
	}

	@Override
	public void updateConfig(String elementKey, Object elementValue) {
		this.config.put(elementKey, elementValue);
	}


	@Override
	public boolean saveConfig(File dest) throws IOException {
		if(dest != null) {
			BufferedWriter writer = new BufferedWriter(new FileWriter(dest));
			writer.write("");
			this.config.entrySet().stream().forEach(o -> {
				Map.Entry<String, Object> entry = o;
				try {
					writer.append(entry.getKey() +"="+entry.getValue()+"\n");
				} catch (IOException e) {
					e.printStackTrace();
				}

			});
			writer.flush();
			writer.close();
			return true;
		} else {
			return false;
		}
	}

	@Override
	public Map<String,Object> getMap() {
		return this.config;
	}

}
