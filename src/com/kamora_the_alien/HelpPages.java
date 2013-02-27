package com.kamora_the_alien;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import com.kamora_the_alien.R;

/** http://mobile.tutsplus.com/tutorials/android/android-user-interface-design-horizontal-view-paging/ */

/** This is the help pages class, extending ActionBarClass, so that the action bar appears.  There are 5 help slides, each
 * 	in their own layout.  The class MyPagerAdapter has been taken from the above link and it has allowed me to implement a
 * 	sliding set of help pages.
 * */
public class HelpPages extends ActionBarClass {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);


		setContentView(R.layout.main);
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		
		MyPagerAdapter adapter = new MyPagerAdapter();
		ViewPager myPager = (ViewPager) findViewById(R.id.myfivepanelpager);
		myPager.setAdapter(adapter);
		myPager.setCurrentItem(0);

	}



	/** The MyPagerAdapter class is what creates the page slider. */
	private class MyPagerAdapter extends PagerAdapter {

		public int getCount() {
			return 5;
		}

		public Object instantiateItem(View collection, int position) {

			LayoutInflater inflater = (LayoutInflater) collection.getContext()
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			int resId = 0;
			switch (position) {
			case 0:
				resId = R.layout.help1;
				break;
			case 1:
				resId = R.layout.help2;
				break;
			case 2:
				resId = R.layout.help3;
				break;
			case 3:
				resId = R.layout.help4;
				break;
			case 4:
				resId = R.layout.help5;
				break;
			}

			View view = inflater.inflate(resId, null);
			((ViewPager) collection).addView(view, 0);

			return view;
		}

		@Override
		public void destroyItem(View arg0, int arg1, Object arg2) {
			((ViewPager) arg0).removeView((View) arg2);

		}

		@Override
		public void finishUpdate(View arg0) {
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == ((View) arg1);

		}

		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1) {
		}

		@Override
		public Parcelable saveState() {
			return null;
		}

		@Override
		public void startUpdate(View arg0) {

		}
	}
}





