/*******************************************************************************
 * Copyright (c) 2012 Aggelos Biboudis.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Aggelos Biboudis - initial API and implementation
 ******************************************************************************/
package adapter.bootstrapper;

import adapter.SensorManager;

/**
 * @author Aggelos Biboudis
 * Bootstrapper is the entry point of the Fusion Sensor Adapter
 *
 */
public class Bootstrapper {
	
	public static void main(String[] args) {
		
		SensorManager sensorManager = new SensorManager();
		
		sensorManager.DiscoverSensors();
		
		sensorManager.StartSensors();
		
		sensorManager.StartReporting();
	}
}
