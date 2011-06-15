package com.adventurousapp.android.util.trackmock;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

import com.adventurousapp.android.util.trackmock.service.DefaultMockLocationService;
import com.adventurousapp.android.util.trackmock.service.MockLocationService;

public class MainActivity extends Activity {
	
	private static final String TAG = MainActivity.class.getSimpleName();
	
	private Intent mMockLocationServiceIntent;
	private Spinner mSpinner;
	private String mSelectedTrackName = null;
	
	private Button mStartButton;
	private Button mStopButton; 
	
	private ServiceConnection mServiceConnection;
	private MockLocationService mService;
	
	private class MyOnItemSelectedLister implements OnItemSelectedListener {

		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
			mSelectedTrackName = parent.getItemAtPosition( pos ).toString();
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {
			mSelectedTrackName = null;
		}
	}
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate( savedInstanceState );
		
		Log.e( TAG, "onCreate()" );
		setContentView( R.layout.main );

		mSpinner = (Spinner) findViewById( R.id.spinner );
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
	            this, R.array.tracks_array, android.R.layout.simple_spinner_item);
		mSpinner.setAdapter(adapter);
		mSpinner.setOnItemSelectedListener( new MyOnItemSelectedLister() );

		mStartButton = (Button) findViewById( R.id.button_start );
		mStopButton = (Button) findViewById( R.id.button_stop );
		
		// disable until service connects
		mSpinner.setEnabled( false );
		mStartButton.setEnabled( false );
		mStopButton.setEnabled( false );
		
		mStartButton.setOnClickListener( new OnClickListener() {
			@Override
			public void onClick(View v) {
				mStartButton.setEnabled( false );
				mSpinner.setEnabled( false );
				if( null == mSelectedTrackName ) {
					Toast.makeText( MainActivity.this, "Please select a track", Toast.LENGTH_LONG ).show();
				} else {
					try {
						mService.startPlayback( mSelectedTrackName );
					} catch( RemoteException e ) {
						Toast.makeText( MainActivity.this, "RemoteException", Toast.LENGTH_LONG ).show();
					}
					mStopButton.setEnabled( true );
				}
			}
		});
		
		mStopButton.setOnClickListener( new OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					mService.stopPlayback();
				} catch( RemoteException e ) {
					Toast.makeText( MainActivity.this, "RemoteException", Toast.LENGTH_LONG ).show();
				}
				
				mStopButton.setEnabled( false );
				mStartButton.setEnabled( true );
				mSpinner.setEnabled( true );
			}
		});
		
		connectService();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		disconnectService();
	}

	private void connectService() {
		Log.e( TAG, "connectService()" );
		
		mServiceConnection = new ServiceConnection() {
			@Override
			public void onServiceConnected(ComponentName name, IBinder service) {
				Log.e( TAG, "onServiceConnected()" );
				
				mService = MockLocationService.Stub.asInterface( service );
				try {
					if( mService.isActive() ) {
						mStopButton.setEnabled( true );
						mStartButton.setEnabled( false );
						mSpinner.setEnabled( false );
					} else {
						mStopButton.setEnabled( false );
						mStartButton.setEnabled( true );
						mSpinner.setEnabled( true );
					}
				} catch (RemoteException e) {
					Toast.makeText( MainActivity.this, "RemoteException", Toast.LENGTH_LONG ).show();
				}
			}

			@Override
			public void onServiceDisconnected(ComponentName name) {
				Log.e( TAG, "onServiceDisconnected()" );
				
				mService = null;
				mStopButton.setEnabled( false );
				mStartButton.setEnabled( false );
				mSpinner.setEnabled( false );
			}
		};

		ComponentName comp = new ComponentName( MainActivity.this, DefaultMockLocationService.class );
		mMockLocationServiceIntent = new Intent().setComponent( comp );
		
		startService( mMockLocationServiceIntent );
		bindService( mMockLocationServiceIntent, mServiceConnection, 0 );
	}
	
	private void disconnectService() {
		unbindService( mServiceConnection );
		mServiceConnection = null;
	}
}