package org.smartregister.chw.sbc.custom_views;

import android.app.Activity;
import android.content.Context;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import android.view.View;
import android.widget.LinearLayout;

import org.smartregister.chw.sbc.R;
import org.smartregister.chw.sbc.domain.MemberObject;
import org.smartregister.chw.sbc.fragment.BaseSbcCallDialogFragment;

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
        }  else if (view.getId() == R.id.sbc_refer_to_facility_layout) {
            Activity activity = (Activity) getContext();
            BaseSbcCallDialogFragment.launchDialog(activity, MEMBER_OBJECT);
        }
    }
}