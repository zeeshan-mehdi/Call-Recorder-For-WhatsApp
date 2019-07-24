package com.shaikhutech.callrecorder;

import android.content.Context;

public interface CallState {
    void onCallStateChanged(boolean state, Context ctx);
}
