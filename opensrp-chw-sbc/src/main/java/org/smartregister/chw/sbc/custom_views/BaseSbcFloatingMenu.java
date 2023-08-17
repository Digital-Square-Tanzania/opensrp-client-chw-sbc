package org.smartregister.chw.sbc.custom_views;

import android.app.Activity;
import android.content.Context;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.widget.LinearLayout;

import org.smartregister.chw.sbc.domain.MemberObject;
import org.smartregister.chw.sbc.fragment.BaseSbcCallDialogFragment;
import org.smartregister.barebones.R;

public class BaseSbcFloatingMenu extends LinearLayout implements View.OnClickListener {
    private MemberObject MEMBER_OBJECT;

    public BaseSbcFloatingMenu(Context context, MemberObject MEMBER_OBJECT) {
        super(context);
        initUi();
        this.MEMBER_OBJECT = MEMBER_OBJECT;
    }

    protected void initUi() {
        inflate(getContext(), R.layout.view_sbc_floating_menu, this);
        FloatingActionButton fab = findViewById(R.id.sbc_fab);
        if (fab != null)
            fab.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.sbc_fab) {
            Activity activity = (Activity) getContext();
            BaseSbcCallDialogFragment.launchDialog(activity, MEMBER_OBJECT);
        }  else if (view.getId() == R.id.refer_to_facility_layout) {
            Activity activity = (Activity) getContext();
            BaseSbcCallDialogFragment.launchDialog(activity, MEMBER_OBJECT);
        }
    }
}