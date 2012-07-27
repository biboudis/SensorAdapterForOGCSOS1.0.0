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
package adapter.utils;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;
import com.vividsolutions.jts.io.WKTWriter;

public class GeospatialUtils {
	
	public static Geometry WKTToGeometry(String srid) {
		Geometry geom = null;
		
		try {
			 geom = new WKTReader().read(srid);
		} catch(ParseException e) {
			e.printStackTrace();
		}
		
		return geom;
	}
	
	public static String geometryToWKT(Geometry geom) {
		String strWKT = null;
		
		strWKT = new WKTWriter().write(geom);
		
		return strWKT;
	}
	
	// ATTENTION: (longitude, latitude) 
	public static String latLonToPointWKT(double longitude, double latitude) {
		Coordinate pCoords = new Coordinate(longitude, latitude); 
		Geometry geom = new GeometryFactory().createPoint(pCoords);
		
		return geometryToWKT(geom);
	}
	
	public static Geometry latLonToPointGeometry(double longitude, double latitude) {
		Coordinate pCoords = new Coordinate(longitude, latitude); 
		Geometry geom = new GeometryFactory().createPoint(pCoords);
		
		return geom;
	}

}
