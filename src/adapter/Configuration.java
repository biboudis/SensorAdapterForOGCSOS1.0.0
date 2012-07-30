/*******************************************************************************
 * Copyright (c) 2012 IDIRA project.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Aggelos Biboudis - initial API and implementation
 ******************************************************************************/
package adapter;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Configuration {
	private static volatile Configuration instance = null;

	private Properties configProp = new Properties();

	private Configuration() {
	}

	public static Configuration getInstance() {
		if (instance == null) {
			synchronized (Configuration.class) {
				if (instance == null) {
					instance = new Configuration();
					instance.loadProps();
				}
			}
		}
		return instance;
	}

	private void loadProps() {
		InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(
				"config/config.properties");
		try {
			configProp.load(in);
		} catch (IOException e) {
			System.out.println(e.toString());
		}
	}

	public String getValue(String key) {
		return configProp.getProperty(key);
	}
}
