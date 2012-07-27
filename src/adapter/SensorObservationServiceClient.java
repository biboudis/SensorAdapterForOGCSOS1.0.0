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
package adapter;


import adapter.exceptions.ConfigurationException;
import adapter.sos.exceptions.SOSException;

/**
 * @author Aggelos Biboudis
 * Describes the operations that an OGC compliant client, can perform.
 * All methods throw an SOSException message with information retrieved via the 
 * response from the SOS service.
 */
public interface SensorObservationServiceClient {

	/**
	 * Register sensor
	 * @throws SOSException 
	 * @throws ConfigurationException 
	 */
	public abstract void registerSensor() throws SOSException, ConfigurationException;

	/**
	 * Insert observation
	 * @throws SOSException
	 * @throws ConfigurationException 
	 */
	public abstract void insertObservation() throws SOSException, ConfigurationException;

}
