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
 * The sensor entity represented by a specific sensor info class, operates in a way complying
 * with the basic API that the vendor provides. This enumeration identifies the operation
 * modes.
 */
public enum OperationMode {
	/**
	 *  The sensor entity acts as a TCP server. Thus it has a TCP endpoint and the external sensor
	 *  pushes values in that.
	 */
	AsTCPServer,
	/**
	 * The sensor entity acts as a TCP client. Thus it has a TCP endpoint of the external sensor that
	 * is going to query and retrieves information from there.
	 */
	AsTCPClient,
	/**
	 * The sensor entity uses the API of the sensors. The library user, is responsible 
	 * for implementing the necessary interfaces, implementing the 
	 * logic that the sensor vendor requires. Possible workflows are to query the sensor
	 * actively, or just define callbacks that are going to be called by some internal vendor
	 * specific library.
	 */
	APICall
}
