package kr.codesolutions.gms;

import java.util.Locale;

import kr.codesolutions.gms.gcm.GCM;
import kr.codesolutions.gms.gcm.GCMConstants;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class MainActivity extends FragmentActivity {
	//############ Start of GCM관련 코드 ##############################################################
    GCM gcm;
	//############ End of GCM관련 코드 ################################################################

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a
	 * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
	 * will keep every loaded fragment in memory. If this becomes too memory
	 * intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the app.
		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		//############ Start of GCM관련 코드 ##############################################################
		gcm = new GCM(this);
		if(!gcm.start("max3", "김개똥")){
			this.finish();
		}
		registerReceiver(mHandleMessageReceiver, new IntentFilter(GCMConstants.DISPLAY_MESSAGE_ACTION));
		registerReceiver(mHandleErrorReceiver, new IntentFilter(GCMConstants.ERROR_MESSAGE_ACTION));
		//############ End of GCM관련 코드 ################################################################
	}

	//############ Start of GCM관련 코드 ##############################################################
	@Override
	protected void onDestroy(){
		gcm.onDestroy();
		try{
			unregisterReceiver(mHandleMessageReceiver);
			unregisterReceiver(mHandleErrorReceiver);
		}catch(Exception ex){
			Log.d(GCMConstants.TAG, "Receiver not registered");
		}
		super.onDestroy();
	}
	//############ End of GCM관련 코드 ################################################################

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			// getItem is called to instantiate the fragment for the given page.
			// Return a DummySectionFragment (defined as a static inner class
			// below) with the page number as its lone argument.
			Fragment fragment = new DummySectionFragment();
			Bundle args = new Bundle();
			args.putInt(DummySectionFragment.ARG_SECTION_NUMBER, position + 1);
			fragment.setArguments(args);
			return fragment;
		}

		@Override
		public int getCount() {
			// Show 3 total pages.
			return 3;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
			case 0:
				return getString(R.string.title_section1).toUpperCase(l);
			case 1:
				return getString(R.string.title_section2).toUpperCase(l);
			case 2:
				return getString(R.string.title_section3).toUpperCase(l);
			}
			return null;
		}
	}

	/**
	 * A dummy fragment representing a section of the app, but that simply
	 * displays dummy text.
	 */
	public static class DummySectionFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		public static final String ARG_SECTION_NUMBER = "section_number";

		public DummySectionFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main_dummy,
					container, false);
			TextView dummyTextView = (TextView) rootView
					.findViewById(R.id.section_label);
			dummyTextView.setText(Integer.toString(getArguments().getInt(
					ARG_SECTION_NUMBER)));
			return rootView;
		}
	}

	
	//############ Start of GCM관련 코드 ##############################################################
	/**
	 * GMS 메시지 처리
	 */
	private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			Bundle bundle = intent.getExtras();
			String content = bundle.getString(GCMConstants.GMS_MESSAGE_CONTENT);
			Log.i(GCMConstants.TAG, "Message received : " + content);
		}
	};
	
	/**
	 * GMS 오류 처리
	 */
	private final BroadcastReceiver mHandleErrorReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			Bundle bundle = intent.getExtras();
			String errorcode = bundle.getString(GCMConstants.GMS_ERRORCODE);
			String error = bundle.getString(GCMConstants.GMS_ERROR);
			Log.i(GCMConstants.TAG, "Error occured on GMS : " + errorcode + "," + error);
			// registrationId 등록에 실패하면 Push메시지 수신이 불가하므로 프로그램을 종료한다.
			if(errorcode.equals(GCMConstants.GMS_ERROR_REGISTOR_FAILED)){
				finish();
			}
		}
	};
	//############ End of GCM관련 코드 ################################################################
}
