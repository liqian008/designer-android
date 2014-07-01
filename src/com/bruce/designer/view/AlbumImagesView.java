package com.bruce.designer.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import com.bruce.designer.R;

/**
 * @author xiaobo.yuan
 */
public class AlbumImagesView extends ViewGroup{

//    public static final int SQUARE = 0;
//    public static final int RELATIVELY_WIDE = 1;
//    public static final int WIDE = 2;
//    public static final int RELATIVELY_HIGH = 3;
//    public static final int HIGH = 4;

    private static final int MAX_COUNT = 4;

    private static final int MARGIN_IN_DIP = 4;
    private static final int MIN_SINGLE_IMAGE_HEIGHT_IN_DIP = 50;

    private static int margin;
    private static int minSingleImageHeight;

    private List<ImageView> list;
    private AtomicInteger size;
    private int singleImageWidth;
    private int singleImageHeight;

    private int paddingTop = 0;
    private int paddingBottom = 0;

    public AlbumImagesView(Context context) {
        this(context, null, 0);
    }

    public AlbumImagesView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AlbumImagesView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        final float density = context.getResources().getDisplayMetrics().density;
        margin = (int)(0.5f + MARGIN_IN_DIP * density);
        minSingleImageHeight = (int)(0.5f + MIN_SINGLE_IMAGE_HEIGHT_IN_DIP * density);
        list = new ArrayList<ImageView>();
        size = new AtomicInteger(1);
        resetView(context, size.get(), ImageView.ScaleType.CENTER_CROP, 0, 0);
    }

    public void setImage(int index, Bitmap bitmap){
        if(index < list.size())
            list.get(index).setImageBitmap(bitmap);
    }

    public void setImageViewOnClickListener(int index, OnClickListener listener){
        if(index < list.size())
            list.get(index).setOnClickListener(listener);
    }

    public void resetView(Context context, int size, ImageView.ScaleType scaleType, int singleImageWidth, int singleImageHeight){
        if(size == 1){
            this.singleImageWidth = singleImageWidth;
            this.singleImageHeight = singleImageHeight;
        }
        if(size>0 && size<=MAX_COUNT)
            this.size.set(size);
        for(ImageView imageView : list)
            removeView(imageView);
        list.clear();
        for(int i=0; i<size; i++){
            final ImageView imageView = new ImageView(context);
            imageView.setScaleType(scaleType);
            imageView.setBackgroundColor(getResources().getColor(R.color.white));
            list.add(imageView);
            addView(imageView);
            switch (i){
                case 0:
                    imageView.setId(R.id.test_feed_image1);
                    break;
                case 1:
                    imageView.setId(R.id.test_feed_image2);
                    break;
                case 2:
                    imageView.setId(R.id.test_feed_image3);
                    break;
                case 3:
                    imageView.setId(R.id.test_feed_image4);
                    break;
            }
        }
        paddingTop = getPaddingTop();
        paddingBottom = getPaddingBottom();
//        logger.d("PADDING TOP ="+paddingTop);
//        logger.d("PADDING BOTTOM = "+paddingBottom);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        logger.d("@MEASURE +"+size.get());
        final int width = MeasureSpec.getSize(widthMeasureSpec);
        final int height = MeasureSpec.getSize(heightMeasureSpec);
        final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
//        logger.d("width mode = "+(widthMode>>30));
//        logger.d("height mode = "+(heightMode>>30));
//        logger.d("width = "+width);
//        logger.d("height = "+height);
        switch (size.get()){
            case 1:
                measureOneChildView(width, widthMode, height, heightMode);
                break;
            case 2:
                measureTwoChildViews(width, widthMode, height, heightMode);
                break;
            case 3:
                measureThreeChildViews(width, widthMode, height, heightMode);
                break;
            case 4:
                measureFourChildViews(width, widthMode, height, heightMode);
                break;
            default:
                setMeasuredDimension(0, 0);
                break;
        }

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
//        logger.d("@LAYOUT");
        final int width = r - l;
        final int height = b - t;
//        logger.d("l = "+l);
//        logger.d("t = "+t);
//        logger.d("r = "+r);
//        logger.d("b = "+b);
        switch (size.get()){
            case 1:
                layoutOneChildView(changed, width, height);
                break;
            case 2:
                layoutTwoChildViews(changed, width, height);
                break;
            case 3:
                layoutThreeChildViews(changed, width, height);
                break;
            case 4:
                layoutFourChildViews(changed, width, height);
            default:
                break;
        }

    }

//    @Override
//    protected void dispatchDraw(Canvas canvas) {
//        final long drawTime = getDrawingTime();
//        drawChild(canvas, list.get(0), drawTime);
//    }

    private void measureOneChildView(int widthSize, int widthMode, int heightSize, int heightMode){
        int width = widthSize;
        int height = widthSize;
        if(singleImageWidth != 0 && singleImageHeight != 0){
            height = (int)(singleImageHeight * (1.0f * width/singleImageWidth));
            if(height < minSingleImageHeight)
                height = minSingleImageHeight;
            if(height > width)
                height = width;
        }
        measureChild(list.get(0), width+MeasureSpec.EXACTLY, height+MeasureSpec.EXACTLY);
        setMeasuredDimension(width, height + paddingTop + paddingBottom);
    }
    private void layoutOneChildView(boolean changed, int width, int height){
        list.get(0).layout(0, paddingTop, width, height - paddingBottom);
    }

    private void measureTwoChildViews(int widthSize, int widthMode, int heightSize, int heightMode){
        final int imageSize = (int)((widthSize - margin * 1.0f)/2);
        for(ImageView imageView : list)
            measureChild(imageView, imageSize + MeasureSpec.EXACTLY,
                    imageSize + MeasureSpec.EXACTLY);
        setMeasuredDimension(imageSize *2 + margin, imageSize + paddingTop + paddingBottom);
    }
    private void layoutTwoChildViews(boolean changed, int width, int height){
        final int imageSize = (int)((width - margin * 1.0f)/2);
        list.get(0).layout(0, paddingTop, imageSize, height - paddingBottom);
        list.get(1).layout(imageSize + margin, paddingTop, width, height - paddingBottom);
    }

    private void measureThreeChildViews(int widthSize, int widthMode, int heightSize, int heightMode){
        final int imageSize = (int)((widthSize - margin * 2.0f)/3);
        measureChild(list.get(0), imageSize * 2 + margin + MeasureSpec.EXACTLY,
                imageSize * 2 + margin + MeasureSpec.EXACTLY);
        measureChild(list.get(1), imageSize + MeasureSpec.EXACTLY,
                imageSize + MeasureSpec.EXACTLY);
        measureChild(list.get(2), imageSize + MeasureSpec.EXACTLY,
                imageSize + MeasureSpec.EXACTLY);
        setMeasuredDimension(imageSize *3 + margin *2, imageSize * 2 + margin + paddingTop + paddingBottom);
    }
    private void layoutThreeChildViews(boolean changed, int width, int height){
        final int imageSize = (int)((width - margin * 2.0f)/3);
        list.get(0).layout(0, paddingTop, imageSize *2 + margin, height - paddingBottom);
        list.get(1).layout(imageSize * 2 + margin * 2, paddingTop, width, paddingTop + imageSize);
        list.get(2).layout(imageSize * 2 + margin * 2, imageSize + margin + paddingTop, width, height - paddingBottom);
    }

    private void measureFourChildViews(int widthSize, int widthMode, int heightSize, int heightMode){
        final int imageSize = (int)((widthSize - margin * 2.0f)/3);
        final int firstHeight =   (int)((imageSize * 2 + margin) * 0.9f);
        measureChild(list.get(0), imageSize *3 + margin * 2 + MeasureSpec.EXACTLY,
                firstHeight + MeasureSpec.EXACTLY);
        measureChild(list.get(1), imageSize + MeasureSpec.EXACTLY,
                imageSize + MeasureSpec.EXACTLY);
        measureChild(list.get(2), imageSize + MeasureSpec.EXACTLY,
                imageSize + MeasureSpec.EXACTLY);
        measureChild(list.get(3), imageSize + MeasureSpec.EXACTLY,
                imageSize + MeasureSpec.EXACTLY);
        setMeasuredDimension(imageSize * 3 + margin * 2, firstHeight + imageSize + margin + paddingTop + paddingBottom);
    }
    private void layoutFourChildViews(boolean changed, int width, int height){
        final int imageSize = (int)((width - margin * 2.0f)/3);
        final int firstHeight =   (int)((imageSize * 2 + margin) * 0.9f);
        list.get(0).layout(0, paddingTop, width, paddingTop + firstHeight);
        list.get(1).layout(0, paddingTop + firstHeight + margin, imageSize, height - paddingBottom);
        list.get(2).layout(imageSize + margin, paddingTop + firstHeight + margin, imageSize *2 + margin, height - paddingBottom);
        list.get(3).layout(imageSize * 2 + margin * 2, paddingTop + firstHeight + margin, width, height - paddingBottom);
    }
}
