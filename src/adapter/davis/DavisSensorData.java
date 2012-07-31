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
package adapter.davis;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import adapter.Configuration;

public class DavisSensorData {
	
	public double TemperatureOut;
	public double TemperatureIn;
	public byte HumidityOut;
	public byte HumidityIn;
	public byte WindSpeed;
	public short WindDirection;
	public double RainRate;
	public String LastUpdated;
	
	final DecimalFormat decimalFormat;
	final SimpleDateFormat simpleDateFormat;
	final String insertObsTemplate;
	
	public DavisSensorData(String template) {
		this.insertObsTemplate = template.replaceAll("\\{OWNER\\}", Configuration.getInstance().getValue("sos.service.owner"))
										 .replaceAll("\\{UNIQUE_ID\\}", Configuration.getInstance().getValue("sos.service.uniqueId"));
		
		DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.ENGLISH);
		otherSymbols.setDecimalSeparator('.');
		
		decimalFormat = new DecimalFormat("#.##", otherSymbols);
		simpleDateFormat = new SimpleDateFormat(Configuration.getInstance().getValue("sos.format.insertdate"));
	}
	
	public synchronized boolean checkTimestamps(String LastUpdatedCheck){
		if ((LastUpdated != null && LastUpdatedCheck==null) || 
			 LastUpdatedCheck!=null && LastUpdated.compareTo(LastUpdatedCheck)!=0)
			return true;
		else
			return false;
	}
	
	/**
	 * Unwraps a byte buffer array, in respect to the DAVIS Specification.
	 * @param wrapped The byte buffer to unwrap.
	 */
	public synchronized void unwrapValues(ByteBuffer wrapped){
		 
		wrapped.order(ByteOrder.LITTLE_ENDIAN);

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Configuration.getInstance().getValue("sos.format.insertdate"));
		Date dateTimeNow = new Date();

		this.TemperatureOut = (double) (0.555 * (wrapped.getShort(12) / 10 - 32));
		this.TemperatureIn = (double) (0.555 * (wrapped.getShort(9) / 10 - 32));
		this.HumidityIn = wrapped.get(11);
		this.HumidityOut = wrapped.get(33);
		this.WindSpeed = wrapped.get(14);
		this.WindDirection = wrapped.getShort(16);
		this.RainRate = (double) (wrapped.getShort(41) * 0.2);
		this.LastUpdated = simpleDateFormat.format(dateTimeNow);
	}
	
	/**
	 * Returns the OGC representation of a DAVIS data packet to insert observation XML - string representation.
	 * @return The XML - string representation.
	 */
	public String toInsertObservationFormat()
	{
		String insertObsTemplateCopy = new String(insertObsTemplate);
		
		Date dateTimeNow = new Date();

		insertObsTemplateCopy = insertObsTemplateCopy
				.replaceAll("\\{TIMESTAMP\\}",	simpleDateFormat.format(dateTimeNow))
				.replaceAll("\\{COUNT\\}",	"1")
				.replaceAll("\\{VALUES\\}", String.format(Configuration.getInstance().getValue("sos.format.value"),
						this.LastUpdated, 
						decimalFormat.format(this.TemperatureOut), 
						this.HumidityOut,
						this.WindSpeed,
						this.WindDirection, 
						decimalFormat.format(this.RainRate)));
		
		return insertObsTemplateCopy;
	}

	@Override
	public String toString() {
		return "DavisSensorData [TemperatureOut=" + TemperatureOut
				+ ", TemperatureIn=" + TemperatureIn + ", HumidityOut="
				+ HumidityOut + ", HumidityIn=" + HumidityIn + ", WindSpeed="
				+ WindSpeed + ", WindDirection=" + WindDirection
				+ ", RainRate=" + RainRate + ", LastUpdated=" + LastUpdated
				+ "]";
	}
	
	
}
