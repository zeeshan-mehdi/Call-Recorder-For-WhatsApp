package com.shaikhutech.callrecorder.base;

import android.view.MenuItem;
public abstract class BaseActivityUpEnable extends BaseActivity2 {
    private final int actionBarTitleId;

    public BaseActivityUpEnable(final int actionBarTitleId) {
        this.actionBarTitleId = actionBarTitleId;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
	
    @Override
    protected void onStart() {
        super.onStart();
   }
}
