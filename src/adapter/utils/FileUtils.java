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
package adapter.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

public class FileUtils {
	
	public static Properties pathProperties = null;
	
	public static String readFileAsString(String filePath) throws java.io.IOException {
		
		pathProperties = new Properties();
		
		InputStream configStream = pathProperties.getClass().getResourceAsStream(filePath);
		BufferedReader configReader = new BufferedReader(new InputStreamReader(configStream, "UTF-8"));
		
		String line;
		StringBuffer result = new StringBuffer();
		while ((line = configReader.readLine()) != null) {
			result.append(line);
		}
		
		return result.toString();	
	}
}


