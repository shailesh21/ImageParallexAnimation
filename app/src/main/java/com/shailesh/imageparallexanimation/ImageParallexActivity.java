package com.shailesh.imageparallexanimation;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;

public class ImageParallexActivity extends AppCompatActivity implements View.OnTouchListener {

    private static final String TAG = ImageParallexActivity.class.getSimpleName();
    private int imageViewHeight;
    private RelativeLayout mainLayout;
    private CustomImageView imageView;

    private float previousTapPosition;
    private int imageActualHeight;
    private int imageActualWidth;
    private int effectiveWidth;
    private int effectiveHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_parallex);
        mainLayout = (RelativeLayout) findViewById(R.id.main_layout);
        imageView = (CustomImageView) findViewById(R.id.image_view);

        mainLayout.setOnTouchListener(this);
        ViewTreeObserver viewTreeObserver = imageView.getViewTreeObserver();
        if (viewTreeObserver.isAlive()) {
            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    imageView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    imageActualHeight = imageView.getHeight();
                    imageActualWidth = imageView.getWidth();
                }
            });
        }

        /*imageView.postDelayed(new Runnable() {
            @Override
            public void run() {
                imageView.invalidate();
                Log.i(ImageParallexActivity.class.getSimpleName(), "height " + imageView
                        .getHeight());
            }
        }, 1);

        startAnimation();*/
    }

    private void startAnimation() {
        ViewConfiguration viewConfiguration = ViewConfiguration.get(this);
        int touchSlop = viewConfiguration.getScaledTouchSlop();
        Log.i(TAG, "Touch slop : " + touchSlop);
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        int action = (motionEvent.getAction() & MotionEvent.ACTION_MASK);
        switch (action) {
            case MotionEvent.ACTION_POINTER_DOWN:
                imageViewHeight++;
                break;
            case MotionEvent.ACTION_POINTER_UP:
                imageViewHeight--;
                break;
            case MotionEvent.ACTION_DOWN:
                imageViewHeight++;
                previousTapPosition = motionEvent.getY();
                break;
            case MotionEvent.ACTION_UP:
                imageViewHeight--;
                restoreImagePosition(imageView.getWidth(), imageView.getHeight());
                break;
            case MotionEvent.ACTION_MOVE:
                Log.i(TAG, "image move : " + imageViewHeight);
                //Log.i(TAG, "x dir " + motionEvent.getX() + " y dir " + motionEvent.getY());
                setImageDimen(motionEvent);
                break;
        }
        return true;
    }

    private void setImageDimen(MotionEvent motionEvent) {
        float diff = motionEvent.getY() - previousTapPosition;
        if (diff > 0) {
            setAspectRatio(diff + imageActualHeight);
            Log.i(TAG, "previous tap position " + previousTapPosition);
        }
    }

    private void restoreImagePosition(float width, float height) {

        for (int i = (int) height; i >= imageActualHeight; i--) {
            float imageWidth = (imageActualWidth * i) / imageActualHeight;
            imageView.getLayoutParams().height = (int) i;
            imageView.getLayoutParams().width = (int) imageWidth;
            imageView.requestLayout();
        }
        //setImageBackAnimation(width, height);
    }

    private void setAspectRatio(float height) {
        float imageWidth = (imageActualWidth * height) / imageActualHeight;
        imageView.getLayoutParams().height = (int) height;
        imageView.getLayoutParams().width = (int) imageWidth;
        imageView.requestLayout();
    }

    private void setImageBackAnimation(float fromX, float fromY) {

        if (fromY > imageActualHeight) {
            float ratioX = imageActualWidth / fromX;
            float ratioY = imageActualHeight / fromY;

            Log.i(TAG, "actualWidth " + imageActualWidth + " height " + fromY +
                    " actualHeight " + imageActualHeight + " ratioX " + ratioX + " ratioY " +
                    ratioY);
            /*imageView.getLayoutParams().height = imageActualHeight;
            imageView.getLayoutParams().width = imageActualWidth;
            imageView.requestLayout();*/

            ObjectAnimator scaleImage = ObjectAnimator.ofPropertyValuesHolder(imageView,
                    PropertyValuesHolder.ofFloat(View.X, fromX, imageActualWidth),
                    PropertyValuesHolder.ofFloat(View.Y, fromY, imageActualHeight));
            scaleImage.setDuration(500);
            scaleImage.start();
        }
    }
}
