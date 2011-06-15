package com.adventurousapp.android.util.trackmock.service;

public class MockTrackProviderFactory {
	private MockTrackProviderFactory() {
	}
	
	public static MockTrackProvider getInstance(String type) {
		if( "Cape Alava".equals( type ) ) {
			return new MockTrackProviderCapeAlava();
		} else if( "Hao".equals( type ) ) {
			return new MockTrackProviderHao();
		} else if( "San Francisco".equals( type ) ) {
			return new MockTrackProviderSanFran();
		} else if( "Shine Quarry".equals( type ) ) {
			return new MockTrackProviderShineQuarry();
		} else if( "Tahina Expedition".equals( type )) {
			return new MockTrackProviderTahina();
		} else {
			return new MockTrackProviderHao();
		}
	}
}