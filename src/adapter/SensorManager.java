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

import java.util.HashMap;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import adapter.davis.DavisSensor;
import adapter.exceptions.ConfigurationException;
import adapter.sos.exceptions.SOSException;

public class SensorManager {
	Logger logger = LoggerFactory.getLogger(DavisSensor.class);

	HashMap<String, AbstractSensor> hookedSensors = new HashMap<String, AbstractSensor>();

	int reportingRateInSeconds = Integer.parseInt(Configuration.getInstance().getValue("sos.service.reportingRateInSeconds"));

	public void StartSensor(String sensorId) {
		hookedSensors.get(sensorId).start();
	}

	public void StartSensors()  {
		for (Entry<String, AbstractSensor> entry : hookedSensors.entrySet()) {

			AbstractSensor sensor = entry.getValue();

			sensor.start();
		}
	}
	
	public void StartReporting()
	{
		DavisSensor davisSensor = (DavisSensor) hookedSensors.get("davis-drv");

		try {
			davisSensor.registerSensor();
		} catch (ConfigurationException | SOSException e) {
			logger.error("RegisterSensor:" + e.getMessage());
		}

		while (true) {
			try {
				davisSensor.insertObservation();

				Thread.sleep(reportingRateInSeconds * 1000);
			} catch (Exception e) {
				logger.error("SOS Operation:" + e.getMessage());
			}
		}
	}

	public void StopSensors() {
		for (Entry<String, AbstractSensor> entry : hookedSensors.entrySet()) {

			AbstractSensor sensor = entry.getValue();

			sensor.stop();
		}
	}

	public void DiscoverSensors() {
		try {
			hookedSensors.put("davis-drv", new DavisSensor("davis-drv"));
		} catch (ConfigurationException e) {
			logger.error("Cannot create an instance of " + e.getClass()
					+ " due to configuration error.");
		}
	}
}
