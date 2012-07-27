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

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import adapter.AbstractSensor;
import adapter.Configuration;
import adapter.SensorObservationServiceClient;
import adapter.exceptions.CommunicationException;
import adapter.exceptions.ConfigurationException;
import adapter.exceptions.WakeUpException;
import adapter.sos.BasicSensorObservationServiceClient;
import adapter.sos.exceptions.SOSException;
import adapter.utils.FileUtils;


/**
 * @author Aggelos Biboudis 
 * 
 * Sensor class for DAVIS Vantage PRO 2 Weather Station
 * Communication is realized via serial protocol defined in [1]. Serial
 * communication is done via the RXTX library [2].
 * 
 * [1] http://www.davisnet.com/support/weather/download/VantageSerialProtocolDocs_v230.pdf 
 * [2] http://rxtx.qbang.org/
 */
public class DavisSensor extends AbstractSensor implements SensorObservationServiceClient {
	Logger logger = LoggerFactory.getLogger(DavisSensor.class);
	
	/**
	 * Data structure holding unwrapped values from DAVIS.
	 */
	private DavisSensorData davisData = null;
	private String registerMessage;
	private String insertObservationTemplate;
	/**
	 * Sanity check for not reporting values in SOS if DAVIS hasn't reported something new.
	 */
	private volatile String LastInserted = null;
	
	private volatile static boolean Ready = false;
	private volatile static boolean Woke = false;
	private volatile boolean Working = true;
	private String identifier;
	
	private SerialPort serialPort;
	private InputStream in;
	private OutputStream out;

	private BasicSensorObservationServiceClient basicSOSClient;

	public DavisSensor(String identifier) throws ConfigurationException {
		super();
		
		this.basicSOSClient = new BasicSensorObservationServiceClient();
		this.identifier = identifier;
		
		try {
			this.registerMessage = 
					FileUtils.readFileAsString(
						Configuration.getInstance().getValue("sos.service.templates") + 
						Configuration.getInstance().getValue("sos.service.templates.register"));
			
			this.insertObservationTemplate =
					FileUtils.readFileAsString(
						Configuration.getInstance().getValue("sos.service.templates") + 
						Configuration.getInstance().getValue("sos.service.templates.insert"));
					
			this.davisData = new DavisSensorData(insertObservationTemplate);	
		} catch (IOException e) {
			throw new ConfigurationException(e);
		}
	}

	@Override
	public void registerSensor() throws SOSException, ConfigurationException {
		logger.info("Registering Sensor.");
		
		basicSOSClient.postXml(registerMessage, Configuration.getInstance().getValue("sos.service.url"));
	}
	
	@Override
	public void insertObservation() throws SOSException, ConfigurationException {
		logger.info("Inserting observation.");
		
		if (isReady() && davisData.checkTimestamps(LastInserted)) {
			
			basicSOSClient.postXml(davisData.toInsertObservationFormat(),	Configuration.getInstance().getValue("sos.service.url"));

			LastInserted = davisData.LastUpdated;
		} 
		else
			logger.warn("Sensor not ready yet or hasn't changed since last reported value.");
	}

	@Override
	public void run() {
		int loopPackets = Integer.parseInt(Configuration.getInstance().getValue("davis.loopPackets"));
		
		byte[] ans;
		
		try 
		{
			this.connect(Configuration.getInstance().getValue("davis.driver.port"));
			
			Thread.sleep(2000);
		} 
		catch (Exception e) {
			logger.error("Cannot connect to Davis.", e);
			System.exit(1);
		}	
		
		while (Working) {
			
			try {
				this.wakeUp();
				
				this.send("LOOP " + loopPackets + "\n");
				
				Thread.sleep(2000);
				
				this.expectACK();
				
				for (int i = 0; i < loopPackets; i++) {
					
					Thread.sleep(2000);
					
					ans = this.expect("LOO", 99);
					
					this.consumeLoo(ans); 
				}
				
				Thread.sleep(2000);
			}
			catch (WakeUpException e)
			{
				logger.warn("Didn't woke up after 3 retries");
			}
			catch (Exception e) {
				logger.error("Exception in Davis sampling loop.", e);
			}
		}
	}
	
	@Override
	public void start() {
		new Thread(this, identifier).start();
	}

	@Override
	public void stop() {
		
		this.disconnect();
		
		Working = false;
	}

	@Override
	public boolean isReady() {
		return Ready;
	}

	private void disconnect() {
		try {
			serialPort.removeEventListener();
			serialPort.close();

			in.close();
			out.close();
		} catch (Exception e) {
			logger.error("Exception in disconnect operation.", e);
		}
	}

	private void connect(String portName) throws Exception {
		CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(portName);

		if (portIdentifier.isCurrentlyOwned()) {
			logger.error("Port is currently in use");
		} else {
			CommPort commPort = portIdentifier.open(this.getClass().getName(),2000);

			if (commPort instanceof SerialPort) {
				serialPort = (SerialPort) commPort;
				serialPort.setSerialPortParams(19200, 
						SerialPort.DATABITS_8,
						SerialPort.STOPBITS_1, 
						SerialPort.PARITY_NONE);

				in = serialPort.getInputStream();
				out = serialPort.getOutputStream();
			} 
			else 
			{
				logger.error("Only serial ports are handled by this example.");
			}
		}
	}
	
	/**
	 * Sends commands to serial port. 
	 * @param command String representation of the command which is used as a byte array in UTF-8 encoding.
	 */
	private void send(String command) {
		try {
			logger.info("Sending command " + StringUtils.chomp(command));
			
			byte[] byteArray = command.getBytes("UTF-8");
			
			this.out.write(byteArray);
			this.out.flush();
		} 
		catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Consumes a loop packet disassembling it.
	 * @param ans The byte array with the loop payload.
	 */
	private void consumeLoo(byte ans []){
		
		logger.info("Loop packet received");
		
		ByteBuffer wrapped = ByteBuffer.wrap(ans, 0, ans.length);
		
		davisData.unwrapValues(wrapped);
		
		logger.info(davisData.toString());
		
		if (!Ready)
			Ready = true;
	}
	
	/**
	 * Consumes any unused bytes from the input and performs three retries.
	 * @throws WakeUpException
	 */
	private void wakeUp() throws WakeUpException{
		int retries = 1;
		
		logger.info("Attempt to wake up: " + retries);
		
		while (retries<4) {
			try 
			{
				this.send("\n");

				Thread.sleep(3000);

				if (this.consumeInputUntilWakeUp())
				{
					setWoke(true);
					break;
				}
				else
					throw new CommunicationException("Didn't found \\n\\r");
			} 
			catch (Exception e) {
				retries++;
				logger.warn("Cannot wake up...retrying.");
				if (retries==4){
					setWoke(false);
					throw new WakeUpException();
				}
			}
		}
		logger.info("Woke up successfully.");
	}
		
	/**
	 * Expecting ACK message.
	 * @throws CommunicationException
	 * @throws InterruptedException
	 */
	private void expectACK() throws CommunicationException, InterruptedException
	{
		logger.info("Expecting ACK");
		byte ans[] = this.expect(1);
		
		ByteBuffer wrapped = ByteBuffer.wrap(ans, 0, ans.length);
		wrapped.order(ByteOrder.LITTLE_ENDIAN);

		if (wrapped.get() != DavisReturnCodes.ACK)
			throw new CommunicationException("Didn't receive ACK");
		
		logger.info("Received ACK successful.");
	}
	
	/**
	 * Used by wake up to consume data.
	 * @return
	 */
	private boolean consumeInputUntilWakeUp(){
		
		int readChar = -1 , bytesRead=0;	
		boolean foundN = false, foundNR = false;
		
		try {
			while((readChar = in.read()) > -1)
			{
				if (readChar=='\n'){
					foundN = true;
				}
				else if (readChar=='\r' && foundN)
					foundNR = true;
				else
				{
					foundN = false;
					foundNR = false;
				}
				
				bytesRead++;
			}
			
			if (bytesRead > 0)
				logger.info("Consumed " + bytesRead + " bytes.");
			
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
		
		return foundNR;
	}
	
	/**
	 * Expect message of certain length.
	 * @param length Expected message's length.
	 * @return Byte array format of answer.
	 * @throws CommunicationException
	 */
	private byte[] expect(int length) throws CommunicationException {
		byte ans[] = new byte[length];
		logger.info("Expecting message of length: " + length);
		try {
			int replyLength = -1;
			
			replyLength = in.read(ans);

			if (replyLength != length)
				throw new CommunicationException("Didn't receive " + length + " bytes but " + replyLength);

		} catch (IOException e) {
			throw new CommunicationException("Expect failed");
		}
		return ans;
	}
	
	/**
	 * Expect message of certain length and of certain preamble.
	 * @param preamble First part of message in string format.
	 * @param length Expected message's length.
	 * @return Byte array format of answer.
	 * @throws Exception
	 */
	private byte[] expect(String preamble, int length) throws CommunicationException{
		
		byte ans [] = expect(length);
		
		String preampleTest = new String(ans, 0, preamble.length());
		
		if (!preampleTest.equalsIgnoreCase(preamble)) {
			throw new CommunicationException("Preamble mismatched: received " + preampleTest);
		}
		return ans;
	}	

	public static boolean isWoke() {
		return Woke;
	}

	public static void setWoke(boolean woke) {
		Woke = woke;
	}
}
