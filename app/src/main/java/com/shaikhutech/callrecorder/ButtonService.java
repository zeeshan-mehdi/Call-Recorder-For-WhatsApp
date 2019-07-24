package com.shaikhutech.callrecorder;


import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.shaikhutech.callrecorder.service.CallDetectionService;
import com.skyfishjy.library.RippleBackground;

public class ButtonService extends Service {
    private WindowManager mWindowManager;
    private View mFloatingView;
    CallState callState;
    public static Context context;

    boolean state = false;

    public ButtonService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {

        try {
            Log.e("service", "started");

            super.onCreate();
            //Inflate the floating view layout we created

            LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            callState = new CallDetectionService();

            mFloatingView = layoutInflater.inflate(R.layout.button_layout, null);
            int LAYOUT_FLAG;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
            } else {
                LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_PHONE;
            }
            //Add the view to the window.
            final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    LAYOUT_FLAG,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT);

            //Specify the view position
            params.gravity = Gravity.CENTER | Gravity.LEFT;        //Initially view will be added to top-left corner
            params.x = -100;
            params.y = 100;
            Log.e("run", "done");
            //Add the view to the window
            mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
            mWindowManager.addView(mFloatingView, params);


            //record button and ripple animation
            final RippleBackground rippleBackground=(RippleBackground)mFloatingView.findViewById(R.id.content);
            final ImageView imageView=(ImageView)mFloatingView.findViewById(R.id.centerImage);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    state = !state;
                    callState.onCallStateChanged(state,context);
                    if(state) {
                        rippleBackground.startRippleAnimation();
                        imageView.setImageResource(R.drawable.recording);
                    }
                    else {
                        rippleBackground.stopRippleAnimation();
                        imageView.setImageResource(R.drawable.recording_ico);
                    }
                }
            });

        }catch (Exception e){
            e.printStackTrace();

        }



    }

    /**
     * Detect if the floating view is collapsed or expanded.
     *
     * @return true if the floating view is collapsed.
     */


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mFloatingView != null) mWindowManager.removeView(mFloatingView);
    }
}





