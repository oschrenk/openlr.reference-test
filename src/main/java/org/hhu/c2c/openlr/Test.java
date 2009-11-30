package org.hhu.c2c.openlr;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import openlr.encoder.Location;
import openlr.encoder.LocationReference;
import openlr.encoder.OpenLREncoder;
import openlr.encoder.properties.OpenLRPropertiesException;
import openlr.map.MapDatabase;
import openlr.map.teleatlas.sqlite.impl.MapDatabaseImpl;

import org.apache.log4j.PropertyConfigurator;

public class Test {

	private static final String pathToDB = "/Users/Oliver/Downloads/openlr/map.db3";

	private static final File encPropFile = new File("/Users/Oliver/Downloads/openlr/encoder.xml");

	private static final String pathToLogProp = "";

	public static void main(String[] args) { 
	//setup logging
	//PropertyConfigurator.configure(pathToLogProp);
	
	//instantiate map database
	MapDatabase mdb = null;
	try {
		mdb = new MapDatabaseImpl(pathToDB);
	} catch (Exception e) {
		e.printStackTrace();
		System.exit(1);
	}
	
	//load location path with n lines	
	List path = new ArrayList();
	path.add(mdb.getLine(15280001229524l));
	path.add(mdb.getLine(15280001349498l));
	path.add(mdb.getLine(-15280001349500l));	


	//instantiate Location object without offsets
	Location location = new Location("Location-1", path);
	
	//prepare encoding result object
	LocationReference locRef = null;
	
	try {
		//encode the location
		locRef = OpenLREncoder.encodeLocation(new FileInputStream(encPropFile), location, mdb);
	} catch (FileNotFoundException e) {
		//encoder properties file not found
		e.printStackTrace();
		System.exit(2);
	} catch (OpenLRPropertiesException e) {
		//accessing encoder properties failed
		e.printStackTrace();
		System.exit(3);
	}
	
	//check validity of the location reference
	if (!locRef.isValid()) {
		//location reference is not valid, print out error code
		//System.out.println("error code: " + locRef.getError().getErrorType());
	} else {
		//location reference is valid, print out the size in bytes
		System.out.println("size [bytes]: " + locRef.getBinary().length);
	}
	List lrps = locRef.getLRPs();	
}
}
