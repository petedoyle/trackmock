package com.adventurousapp.android.util.trackmock.service;

import android.location.Location;

public interface MockTrackProvider {
	public Location getNextLocation();
	
	public boolean hasNext();
	public void rewind();
	public void moveToNext();
}
