package org.sensors2.osc.sensors;

import java.util.Hashtable;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by thomas on 12.02.15.
 */
public class SensorDimensions {
	public static Map<Integer, String> GetOscSuffixes(int dimensions) {
		switch (dimensions) {
			case 1:
				return Create1D();
			case 3:
				return Create3D();
			case 4:
				return Create4D();
			case 6:
				return Create6D();
		}
		throw new IllegalArgumentException("Wrong number of dimensions");
	}

	private static Map<Integer, String> Create6D() {
		Hashtable<Integer, String> table = new Hashtable<Integer, String>();
		table.put(0, "X");
		table.put(1, "Y");
		table.put(2, "Z");
		table.put(3, "dX");
		table.put(4, "dY");
		table.put(5, "dZ");
		return new TreeMap<Integer, String>(table);
	}

	private static Map<Integer, String> Create4D() {
		Hashtable<Integer, String> table = new Hashtable<Integer, String>();
		table.put(0, "X");
		table.put(1, "Y");
		table.put(2, "Z");
		table.put(3, "cos");
		return new TreeMap<Integer, String>(table);
	}

	private static Map<Integer, String> Create3D() {
		Hashtable<Integer, String> table = new Hashtable<Integer, String>();
		table.put(0, "X");
		table.put(1, "Y");
		table.put(2, "Z");
		return new TreeMap<Integer, String>(table);
	}

	private static Map<Integer, String> Create1D() {
		Hashtable<Integer, String> table = new Hashtable<Integer, String>();
		table.put(0, "");
		return new TreeMap<Integer, String>(table);
	}
}
