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

/**
 * @author Aggelos Biboudis
 * Describes the operations that a sensor can perform. This interface is 
 * relevant to the sensor values retrieval and the method followed is driven 
 * by the implementor.
 */
public interface Sensor {
	
	/**
	 * Starts the sensor.
	 */
	void start();
	
	/**
	 * Stops the sensor.
	 */
	void stop();
	
	/**
	 * @return A boolean value indicating if the sensor can retrieve values currently.
	 */
	boolean isReady();
}
