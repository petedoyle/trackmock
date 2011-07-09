package com.adventurousapp.android.util.trackmock.service;

import java.util.Random;

import com.adventurousapp.android.util.trackmock.MainActivity;
import com.adventurousapp.android.util.trackmock.R;
import com.adventurousapp.android.util.trackmock.util.GeoUtilities;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

public class DefaultMockLocationService extends Service {
	private static String TAG = DefaultMockLocationService.class.getSimpleName();

	private static final String PROVIDER_ID = LocationManager.GPS_PROVIDER;
	private static final double MAX_DISTANCE_METERS = 2.0;
	private static final int NOTIFICATION_ID = 0x111;
	
	private MockTrackProvider mMockTrackProvider;
	private PositionProvider mUpdateThread = null;
	private LocationManager mLocationManager;
	
	private boolean mIsActive = false;

	public IBinder onBind(Intent intent) {
		Log.e( TAG, "onBind() intent: " + intent );
		return binder;
	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart( intent, startId );
		
		mLocationManager = (LocationManager) this.getSystemService( LOCATION_SERVICE );
	}
	
	private final MockLocationService.Stub binder = new MockLocationService.Stub() {
		
		@Override
		public void stopPlayback() throws RemoteException {
			if( mUpdateThread != null ) {
				mUpdateThread.mStop = true;
				mUpdateThread.interrupt();
			}
			
			try {
				mLocationManager.setTestProviderEnabled( PROVIDER_ID, false );
				mLocationManager.removeTestProvider( PROVIDER_ID );
			} catch( SecurityException e ) {
				Log.e( TAG, "Ignoring SecurityException during onDestroy()", e);
			}
	
			stopForeground( true );
			mIsActive = false;
		}
		
		@Override
		public void startPlayback(String trackName) throws RemoteException {
			mMockTrackProvider = MockTrackProviderFactory.getInstance( trackName );
			
			try {
				addFakeGpsTestProvider( mLocationManager );
				// okay, now we're ready to start sending updates, make sure we're enabled
				mLocationManager.setTestProviderEnabled( PROVIDER_ID, true );  // It looks like it calls addTestProvider() for us
				mUpdateThread = new PositionProvider( mLocationManager );
				
				Log.i( TAG, "Starting playback as foreground service" );
				startForeground( NOTIFICATION_ID, getNotification() );
				
				Log.i( TAG, "Starting position sender thread." );
				mUpdateThread.start();
			} catch( SecurityException e ) {
				Log.e( TAG, "ACCESS_MOCK_LOCATION is not enabled.  Shutting down " + getClass().getSimpleName(), e );
				stopSelf();
			}
			
			mIsActive = true;
		}
		
		@Override
		public boolean isActive() throws RemoteException {
			return mIsActive;
		}
	};

	private void addFakeGpsTestProvider(LocationManager lm) {
		boolean requiresNetwork = false;
		boolean requiresSatellite = true;
		boolean requiresCell = false;
		boolean hasMonetaryCost = true;
		boolean supportsAltitude = true;
		boolean supportsSpeed = true;
		boolean supportsBearing = true;
		int powerRequirement = 0;
		int accuracy = 5;
		
		if( android.os.Build.VERSION.SDK_INT >= 4) { // 1.6+ requires us to addTestProvider(), but it breaks 1.5 with "provider "gps" already exists"
			lm.addTestProvider( PROVIDER_ID, requiresNetwork, requiresSatellite, requiresCell, hasMonetaryCost, supportsAltitude, supportsSpeed, supportsBearing, powerRequirement, accuracy );	
		}
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.i( TAG, "Location provider service being killed" );
	}

	private class PositionProvider extends Thread {
		private static final long TIME_BETWEEN_UPDATES = 1000;
		
		private LocationManager mLocationService = null;
		private Location mPreviousLocation = null;
		
		private boolean mStop = false;

		public PositionProvider(LocationManager lm) {
			super();
			mLocationService = lm;
		}

		public void run() {
			boolean loop = false;
			
			Location nextLocation = null;
			Location intermediateLocation = new Location( LocationManager.GPS_PROVIDER );
			float distanceToNext;
			
			while( !mStop && mMockTrackProvider.hasNext()) {
				nextLocation = mMockTrackProvider.getNextLocation();
				
				if( mPreviousLocation == null ) {
					postLocationChange( nextLocation );
					
					continue;
				}
				
				distanceToNext = mPreviousLocation.distanceTo( nextLocation );
				
				if( distanceToNext > MAX_DISTANCE_METERS ) {
					double percentageToMove = MAX_DISTANCE_METERS / distanceToNext;
					
					double prevLat = mPreviousLocation.getLatitude();
					double nextLat = nextLocation.getLatitude();
					if( prevLat > nextLat ) { 
						intermediateLocation.setLatitude( prevLat - ((prevLat - nextLat) * percentageToMove) );
					} else {
						intermediateLocation.setLatitude( prevLat + ((nextLat - prevLat) * percentageToMove) );
					}
					
					double prevLng = mPreviousLocation.getLongitude();
					double nextLng = nextLocation.getLongitude();
					if( prevLng > nextLng ) {
						intermediateLocation.setLongitude( prevLng - ((prevLng - nextLng)) * percentageToMove );
					} else {
						intermediateLocation.setLongitude( prevLng + ((nextLng - prevLng)) * percentageToMove );
					}
					
					postLocationChange( intermediateLocation );
					
				} else {
					postLocationChange( nextLocation );
					mMockTrackProvider.moveToNext();
				}
				
				if( !mMockTrackProvider.hasNext() && loop ) {
					mMockTrackProvider.rewind();
				}
				
				try {
					Thread.sleep( TIME_BETWEEN_UPDATES );
				} catch( InterruptedException e ) {
					// we don't really care if our sleep was interrupted.
				}
			}
		}

		private void postLocationChange(Location location) {
			// set the time in the location. If the time on this location matches
			// the time on the one in the previous set call, it will be ignored
			location.setTime( System.currentTimeMillis() );
			
			if( null != mPreviousLocation ) {
				location.setSpeed( GeoUtilities.calculateSpeed( mPreviousLocation, location ) );
			}
			Random r = new Random();
			location.setAltitude( r.nextInt( 100 ) );
			location.setAccuracy( 5 + r.nextInt( 5 ) ); // set accuracy between 5 and 10 meters
			
			// send the new location to the location service
			mLocationService.setTestProviderLocation( PROVIDER_ID, location );
			mPreviousLocation = location;
		}
	}
	
	private Notification getNotification() {
		Notification notification = new Notification( R.drawable.adventurous_icon,
				getText( R.string.notify_playback_text ), 
				System.currentTimeMillis() );

		PendingIntent mainActivityIntent = PendingIntent.getActivity( this,
																	0, 
																	new Intent(this, MainActivity.class),
																	Notification.FLAG_ONGOING_EVENT );

		notification.setLatestEventInfo( getApplicationContext(),
											getText( R.string.notify_playback_title ),
											getText( R.string.notify_playback_text ),
											mainActivityIntent );

		return notification;
	}
}