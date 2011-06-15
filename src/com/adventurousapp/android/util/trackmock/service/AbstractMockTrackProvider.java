package com.adventurousapp.android.util.trackmock.service;

import android.location.Location;
import android.location.LocationManager;

public class AbstractMockTrackProvider implements MockTrackProvider {
	private final double[][] mCoords;

	int mNextIndex = 0;

	public AbstractMockTrackProvider(double[][] coords) {
		this.mCoords = coords;
	}

	@Override
	public Location getNextLocation() {
		Location next = new Location( LocationManager.GPS_PROVIDER );
		next.setLatitude( mCoords[mNextIndex][0] );
		next.setLongitude( mCoords[mNextIndex][1] );
		return next;
	}

	@Override
	public boolean hasNext() {
		return mNextIndex < mCoords.length - 1;
	}

	@Override
	public void moveToNext() {
		if( mNextIndex + 1 >= mCoords.length ) {
			throw new IllegalStateException(
					"Already reached the end of coordinates.  Please check hasNext() before moveToNext(), or call rewind()" );
		}
		mNextIndex++;
	}

	@Override
	public void rewind() {
		mNextIndex = 0;
	}
}
