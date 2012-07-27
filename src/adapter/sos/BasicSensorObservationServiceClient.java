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
package adapter.sos;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import adapter.davis.DavisSensor;
import adapter.sos.exceptions.SOSException;
import adapter.utils.FileUtils;

/**
 * @author Aggelos Biboudis
 * Basic class for communication with an OGC SOS service.
 * It can throw SOS exceptions.
 */

public class BasicSensorObservationServiceClient {
	private HttpClient httpclient;
	Logger logger = LoggerFactory.getLogger(DavisSensor.class);
	
	public BasicSensorObservationServiceClient() {
		this.httpclient = new DefaultHttpClient();
	}

	public void postXml(String strXML, String serviceURL) throws SOSException {

		HttpPost httppost = new HttpPost(serviceURL);
		HttpEntity entity = null;
		HttpResponse response = null;
		try {
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
			
			nameValuePairs.add(new BasicNameValuePair("request", strXML));

			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			response = httpclient.execute(httppost);
			
			entity = response.getEntity();
			
			int code = response.getStatusLine().getStatusCode();
			
			if (code != 200) {
				throw new SOSException("Server replied with http error code: " + code);
			}
			
			examineReponse(entity);
		} catch (IOException e) {
			try {
				EntityUtils.consume(entity);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			throw new SOSException("Cannot execute post: " + e.getCause());
		}
	}

	private void examineReponse(HttpEntity entity) throws SOSException {
		try {
			if (entity != null) {
				String xmlString = EntityUtils.toString(entity);

				DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
				DocumentBuilder db = factory.newDocumentBuilder();
				InputSource inStream = new InputSource();
				inStream.setCharacterStream(new StringReader(xmlString));
				Document doc = db.parse(inStream);

				String value;
				
				NodeList nlExc = doc.getElementsByTagName("ows:ExceptionText");
				for (int i = 0; i < nlExc.getLength(); i++) {
					if (nlExc.item(i).getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
						org.w3c.dom.Element nameElement = (org.w3c.dom.Element) nlExc.item(i);
						value = nameElement.getFirstChild().getNodeValue().trim();
						throw new SOSException(value, "Error in response from SOS service.");
					}
				}
				
				NodeList nl;
				nl = doc.getElementsByTagName("sos:AssignedObservationId");
				for (int i = 0; i < nl.getLength(); i++) {
					if (nl.item(i).getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
						org.w3c.dom.Element nameElement = (org.w3c.dom.Element) nl.item(i);
						value = nameElement.getFirstChild().getNodeValue().trim();
						logger.info("AssignedObservationId [" + value + "]");
					}
				}
			}
		} catch (ParseException | DOMException | ParserConfigurationException | IOException
				| SAXException e) {
			e.printStackTrace();
		}
	}

	public void postFromXml(String pathToXMLFile, String serviceURL)
			throws IOException, SOSException {
		String xmlToPost = FileUtils.readFileAsString(pathToXMLFile);

		postXml(xmlToPost, serviceURL);
	}
}
