package com.ihome.adapter;

import com.ihome.fragment.FirstFragment;
import com.ihome.fragment.SecondFragment;
import com.ihone.easylauncher.MainActivity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class FragementAdapter extends FragmentPagerAdapter {

	public final static int PAGE_COUNT = 2;
	public FragementAdapter(FragmentManager fragmentManager) {
		super(fragmentManager);
	}

	@Override
	public Fragment getItem(int position) {
		switch (position) {
		case MainActivity.PAGE_ONE:
			//第一个页面
			FirstFragment ff=new FirstFragment();
			return ff;
        case MainActivity.PAGE_TWO:
			//第二个页面
        	SecondFragment sf=new SecondFragment();
        	return sf;
		default:
			break;
		}
		return null;
	}

	@Override
	public int getCount() {
		return PAGE_COUNT;
	}

}
