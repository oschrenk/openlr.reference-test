package org.hhu.c2c.openlr;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import openlr.Location;
import openlr.LocationReference;
import openlr.LocationReferencePoint;
import openlr.OpenLRRuntimeException;
import openlr.encoder.LocationFactory;
import openlr.encoder.LocationReferenceHolder;
import openlr.encoder.OpenLREncoder;
import openlr.map.Line;
import openlr.map.MapDatabase;
import openlr.map.teleatlas.sqlite.impl.LineImpl;
import openlr.map.teleatlas.sqlite.impl.MapDatabaseImpl;

import org.ibex.nestedvm.util.Seekable.ByteArray;

public class Test {

	private static final String DATABASE = "/teleatlas_utrecht_2008_04.db3";

	private static final String ENCODER_PROPERTIES = "/encoder.xml";

	/**
	 * {@link LineImpl#getShape()}
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		new Test().run();
	}

	public void run() {
		// instantiate map database
		MapDatabase mdb = null;
		try {
			mdb = new MapDatabaseImpl(this.getClass().getResource(DATABASE)
					.getFile().toString());
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}

		// load location path with n lines
		List<Line> path = new ArrayList<Line>();
		path.add(mdb.getLine(15280001229524l));
		path.add(mdb.getLine(15280001349498l));
		path.add(mdb.getLine(-15280001349500l));

		// instantiate Location object without offsets
		// PAIN why do I have name locations?
		Location location = LocationFactory.createLineLocation("Location-1", path);

		// prepare encoding result object
		LocationReferenceHolder locRef = null;

		try {
			// PAIN what about convention over configuration?
			// PAIN throws OpenLRPropertiesException
			// PAIN throws FileNotFoundException
			// PAIN inputstream as only way to inject properties?
			// PAIN create object that can be passed arround instead of static access
			// java.util.Properties perhaps?
			locRef = OpenLREncoder.encodeLocation(new FileInputStream(this
					.getClass().getResource(ENCODER_PROPERTIES).getFile()),
					location, mdb);
		} catch (FileNotFoundException e) {
			// encoder properties file not found
			e.printStackTrace();
			System.exit(2);
		} catch (OpenLRRuntimeException e) {
			// accessing encoder properties failed
			e.printStackTrace();
			System.exit(3);
		}

		// check validity of the location reference
		// PAIN should throw an unchecked OpenLREncodingException
		if (!locRef.isValid()) {
			// location reference is not valid, print out error code

		} else {
			// location reference is valid, print out the size in bytes
			LocationReference lr = locRef.getLocationReference("binary");
			ByteArray ba = (ByteArray) lr.getLocationReferenceData();
			System.out.println("size [bytes]: " + ba.length());
		}

		// PAIN
		List<? extends LocationReferencePoint> lrps = locRef.getLRPs();

		// PAIN
		Iterator<? extends LocationReferencePoint> iter = lrps.iterator();

		while (iter.hasNext()) {
			LocationReferencePoint lrp = iter.next();
			lrp.getBearing();

			// PAIN call the method getDistanceTo
			lrp.getDistanceToNext();
		}
	}
}
