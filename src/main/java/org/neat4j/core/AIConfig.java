/*
 * Created on Sep 27, 2004
 *
 */
package org.neat4j.core;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * @author MSimmerson
 *
 */
public interface AIConfig {
	/**
	 * Allows access to given configuration getNetOutputs
	 * @param elementKey
	 * @return The configuration value for the given key
	 */
	Object getConfigElementByName(String elementKey);
	/**
	 * Allows updating of configuration getNetOutputs
	 * @param elementKey
	 * @param elementValue
	 */
	void updateConfig(String elementKey, Object elementValue);

	boolean saveConfig(File file) throws IOException;

	Map getMap();

}
