package com.plivo.voicetest.Helpers;

import android.app.Fragment;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;

/**
 * Created by Siva on 12/06/17.
 */

public abstract class BackableFragment extends Fragment implements View.OnKeyListener{
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(this);
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_UP) {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                onBackButtonPressed();
                return true;
            }
        }

        return false;
    }

    public abstract void onBackButtonPressed();
}
