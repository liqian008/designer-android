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
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.bruce.designer.R;
import com.bruce.designer.adapter.ViewPagerAdapter;
import com.bruce.designer.listener.OnSingleClickListener;
import com.bruce.designer.util.UniversalImageUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 浏览图片的activity
 * @author liqian
 *
 */
public class Activity_ImageBrowser extends BaseActivity implements OnPageChangeListener{
	
	private ViewPager viewPager;
	
	private View titlebarView;
	private TextView titleView;
	
	private static final String KEY_BROWSE_INDEX = "index";
	private static final String kEY_BROWSE_IMAGE_LIST = "test";
	
	public static void show(Context context, int index, ArrayList<String> imageUrlList){
		Intent intent = new Intent(context, Activity_ImageBrowser.class);
		intent.putExtra(KEY_BROWSE_INDEX, index);
		intent.putStringArrayListExtra(kEY_BROWSE_IMAGE_LIST, imageUrlList);
		context.startActivity(intent);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image_browser);
		
		// init view
		titlebarView = findViewById(R.id.titlebar_return);
		titlebarView.setOnClickListener(listener);
		titleView = (TextView) findViewById(R.id.titlebar_title);
		titleView.setText("详情展示");
		
		Intent intent = getIntent();
		int index = intent.getIntExtra(KEY_BROWSE_INDEX, 0);
		List<String> imageUrlList = intent.getStringArrayListExtra(kEY_BROWSE_IMAGE_LIST);
		viewPager = (ViewPager) findViewById(R.id.viewPager);
		if(imageUrlList!=null&&imageUrlList.size()>0){
			//非法index视为0
			if(index<0 || index>=imageUrlList.size()){
				index = 0;
			}
			
			
			List<View> views = new ArrayList<View>();
			for(String imageUrl: imageUrlList){
				View imageViewItem = inflater.inflate(R.layout.pager_image_view, null);
				views.add(imageViewItem);
				ImageView imageView = (ImageView) imageViewItem.findViewById(R.id.pager_imgview);
				ImageLoader.getInstance().displayImage(imageUrl, imageView, UniversalImageUtil.DEFAULT_DISPLAY_OPTION);
			}
			ViewPagerAdapter pagerAdapter = new ViewPagerAdapter(this, views);
			viewPager.setAdapter(pagerAdapter);
			
			viewPager.setCurrentItem(index);
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
	
	
	private OnClickListener listener = new OnSingleClickListener() {
		@Override
		public void onSingleClick(View view) {
			switch (view.getId()) {
			case R.id.titlebar_return:
				finish();
				break;
			default:
				break;
			}
		}
	};
}
