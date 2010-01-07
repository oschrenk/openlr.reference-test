package org.hhu.c2c.openlr;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import openlr.encoder.Location;
import openlr.encoder.LocationReference;
import openlr.encoder.OpenLREncoder;
import openlr.encoder.data.LocRefPoint;
import openlr.encoder.properties.OpenLRPropertiesException;
import openlr.encoder.properties.generated.OpenLREncoderProperties;
import openlr.map.Line;
import openlr.map.MapDatabase;
import openlr.map.teleatlas.sqlite.impl.LineImpl;
import openlr.map.teleatlas.sqlite.impl.MapDatabaseImpl;

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
		Location location = new Location("Location-1", path);

		// prepare encoding result object
		LocationReference locRef = null;

		try {
			// PAIN what about convention over configuration?
			// PAIN throws OpenLRPropertiesException
			// PAIN throws FileNotFoundException
			// PAIN inputstream as only way to inject properties?
			// java.util.Properties perhaps?
			locRef = OpenLREncoder.encodeLocation(new FileInputStream(this
					.getClass().getResource(ENCODER_PROPERTIES).getFile()),
					location, mdb);
		} catch (FileNotFoundException e) {
			// encoder properties file not found
			e.printStackTrace();
			System.exit(2);
		} catch (OpenLRPropertiesException e) {
			// accessing encoder properties failed
			e.printStackTrace();
			System.exit(3);
		}

		// check validity of the location reference
		// PAIN should throw an unchecked OpenLREncodingException
		if (!locRef.isValid()) {
			// location reference is not valid, print out error code
			System.out.println("error code: "
					+ locRef.getException().getErrorType());
		} else {
			// location reference is valid, print out the size in bytes
			System.out.println("size [bytes]: " + locRef.getBinary().length);
		}

		List<LocRefPoint> lrps = locRef.getLRPs();

		Iterator<LocRefPoint> iter = lrps.iterator();

		// PAIN if there is OpenLREncoderProperties
		// why can't it be used for encodeLocation()?
		OpenLREncoderProperties op = new OpenLREncoderProperties();

		while (iter.hasNext()) {
			LocRefPoint lrp = iter.next();
			try {
				// PAIN get Bearing needs properties?
				lrp.getBearing(op);

				// PAIN call the method getDistanceTo
				// PAIN distanceToNEXT shouldn't use another LocRefPoint
				// lrp.getDistanceToNext(null)

				// PAIN throws OpenLRPropertiesException
			} catch (OpenLRPropertiesException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
