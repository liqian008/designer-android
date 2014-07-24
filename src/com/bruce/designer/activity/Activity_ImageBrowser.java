package com.bruce.designer.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.bruce.designer.R;
import com.bruce.designer.adapter.ViewPagerAdapter;
import com.bruce.designer.util.UniversalImageUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 浏览图片的activity
 * @author liqian
 *
 */
public class Activity_ImageBrowser extends BaseActivity implements OnPageChangeListener{
	
	private ViewPager viewPager;
	
	
	private static final String key = "test";
	
	public static void show(Context context, ArrayList<String> imageUrlList){
		Intent intent = new Intent(context, Activity_ImageBrowser.class);
		intent.putStringArrayListExtra(key, imageUrlList);
		context.startActivity(intent);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image_browser);
		
		Intent intent = getIntent();
		List<String> imageUrlList = intent.getStringArrayListExtra(key);
		
		viewPager = (ViewPager) findViewById(R.id.viewPager);
		if(imageUrlList!=null&&imageUrlList.size()>0){
			List<View> views = new ArrayList<View>();
			for(String imageUrl: imageUrlList){
				View imageViewItem = LayoutInflater.from(context).inflate(R.layout.pager_image_view, null);
				views.add(imageViewItem);
				ImageView imageView = (ImageView) imageViewItem.findViewById(R.id.pager_imgview);
				ImageLoader.getInstance().displayImage(imageUrl, imageView, UniversalImageUtil.DEFAULT_DISPLAY_OPTION);
			}
			ViewPagerAdapter pagerAdapter = new ViewPagerAdapter(this, views);
			viewPager.setAdapter(pagerAdapter);
		}
	}

	
	
	
	@Override
	public void onPageScrollStateChanged(int arg0) {
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
	}

	@Override
	public void onPageSelected(int arg0) {
	}
	
	
}
