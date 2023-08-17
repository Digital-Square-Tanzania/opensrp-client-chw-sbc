package org.smartregister.chw.sbc.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.widget.FrameLayout;

import org.json.JSONObject;

import timber.log.Timber;

public class BaseHomeVisitFragment extends DialogFragment {

    protected JSONObject jsonObject;

    public JSONObject getJsonObject() {
        return jsonObject;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Holo_Light_NoActionBar);
    }

    @Override
    public void onStart() {
        super.onStart();
        new Handler().post(() -> {
            if (getDialog() != null && getDialog().getWindow() != null) {
                getDialog().getWindow().setLayout(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
            }
        });
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        try {
            removeIfExist(manager);
            FragmentTransaction ft = manager.beginTransaction();
            ft.add(this, tag).addToBackStack(null);
            ft.commitAllowingStateLoss();
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    private void removeIfExist(FragmentManager manager) {
        try {
            manager.beginTransaction().remove(this).commit();
        } catch (Exception e) {
            Timber.v(e);
        }
    }
}