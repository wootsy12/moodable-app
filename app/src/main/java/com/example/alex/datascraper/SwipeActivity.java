package com.example.alex.datascraper;
import android.content.Context;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;

/**
 * Created by Bella on 2/25/18.
 */

public class SwipeActivity implements OnTouchListener{

    private final GestureDetector gestureDetector;
    private static Button nextScreenButton;


    public SwipeActivity(Context context){
        gestureDetector = new GestureDetector(context, new GestureListener());
    }

    public void onSwipeLeft(){

    }
    public void onSwipeRight(){

    }

    public boolean onTouch(View v, MotionEvent event){
        return gestureDetector.onTouchEvent(event);
    }

    private final class GestureListener extends SimpleOnGestureListener{
        private static final int SWIPE_DISTANCE_THRESHOLD = 100;
        private static final int SWIPE_VELOCITY_THRESHOLD = 100;

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            float distanceX = e2.getX() - e1.getX();
            float distanceY = e2.getY() - e1.getY();
            if (Math.abs(distanceX) > Math.abs(distanceY)
                    && Math.abs(distanceX) > SWIPE_DISTANCE_THRESHOLD
                    && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD){
                if (distanceX > 0)
                    onSwipeRight();

                else
                    onSwipeLeft();
                return true;
            }
            return false;
        }
    }
}

