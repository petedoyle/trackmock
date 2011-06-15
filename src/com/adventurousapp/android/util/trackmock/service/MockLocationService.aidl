package com.adventurousapp.android.util.trackmock.service;

/**
 * {@hide}
 */
interface MockLocationService {

	boolean isActive();

	void startPlayback(String trackName);
	void stopPlayback();	
}