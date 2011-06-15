package com.adventurousapp.android.util.trackmock.util;

import android.location.Location;

public class GeoUtilities {
	/**
	 * Calculates the speed (in meters/sec) between two Location objects.  Uses
	 * {@link Location#getTime()} to calculate the speed.
	 */
	public static float calculateSpeed(Location previousLocation, Location currentLocation) {
		long prevTimeMs = previousLocation.getTime();
		long curTimeMs = currentLocation.getTime();

		float distanceInMeters = previousLocation.distanceTo( currentLocation );
		long seconds = (curTimeMs - prevTimeMs) / 1000;
		
		return distanceInMeters / seconds;
	}
}
