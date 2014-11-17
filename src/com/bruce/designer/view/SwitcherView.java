package com.bruce.designer.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bruce.designer.R;

public class SwitcherView extends RelativeLayout implements OnClickListener,
		AnimationListener {

	private ImageView bgOn, bgOff, slide;
	private boolean ischg, ison;
	private OnChangedListener listener;

	public SwitcherView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.setOnClickListener(this);
	}

	public void setCheckState(boolean checkState) {
		ison = checkState;
		requestLayout();
	}

	public void OnChangedListener(OnChangedListener listener) {
		this.listener = listener;
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		bgOn = (ImageView) this.findViewById(R.id.switcher_bg_on);
		bgOff = (ImageView) this.findViewById(R.id.switcher_bg_off);
		slide = (ImageView) this.findViewById(R.id.switcher_slide);
	}

	private int offside;

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
		int left = bgOn.getLeft();
		int top = bgOn.getTop();
		int right = bgOn.getRight();
		int bottom = bgOn.getBottom();
		offside = (right - left) / 2;
		if (ison) {
			slide.layout(left + offside, top, right, bottom);
		} else {
			slide.layout(left, top, left + offside, bottom);
		}

		if (ison) {
			bgOff.setVisibility(INVISIBLE);
		} else {
			bgOff.setVisibility(VISIBLE);
		}
	}

	@Override
	public void onClick(View v) {
		if (ischg) {
			return;
		}
		ison = !ison;
		setAnimation();
	}

	private final static long ANIMATIONTIME = 200;

	private void setAnimation() {
		TranslateAnimation translat = null;
		AlphaAnimation alpha = null;
		if (ison) {
			translat = new TranslateAnimation(0, offside, 0, 0);
			alpha = new AlphaAnimation(1, 0);
		} else {
			translat = new TranslateAnimation(0, -offside, 0, 0);
			alpha = new AlphaAnimation(0, 1);
		}
		translat.setDuration(ANIMATIONTIME);
		alpha.setDuration(ANIMATIONTIME);
		translat.setAnimationListener(this);
		slide.startAnimation(translat);
		bgOff.startAnimation(alpha);

	}

	@Override
	public void onAnimationEnd(Animation animation) {
		slide.clearAnimation();
		bgOff.clearAnimation();
		requestLayout();
		ischg = false;
		if (listener != null) {
			listener.OnChanged(ison);
		}
	}

	@Override
	public void onAnimationRepeat(Animation animation) {

	}

	@Override
	public void onAnimationStart(Animation animation) {
		ischg = true;
	}

	static void lg(String str) {
		Log.v("switcher", str);
	}

	public interface OnChangedListener {
		abstract void OnChanged(boolean CheckState);
	}
}
