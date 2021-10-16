package org.smartregister.chw.barebones.listener;


import android.view.View;

import org.smartregister.chw.barebones.fragment.BaseTestCallDialogFragment;
import org.smartregister.chw.barebones.util.TestUtil;
import org.smartregister.barebones.R;

import timber.log.Timber;

public class BaseTestCallWidgetDialogListener implements View.OnClickListener {

    private BaseTestCallDialogFragment callDialogFragment;

    public BaseTestCallWidgetDialogListener(BaseTestCallDialogFragment dialogFragment) {
        callDialogFragment = dialogFragment;
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.malaria_call_close) {
            callDialogFragment.dismiss();
        } else if (i == R.id.malaria_call_head_phone) {
            try {
                String phoneNumber = (String) v.getTag();
                TestUtil.launchDialer(callDialogFragment.getActivity(), callDialogFragment, phoneNumber);
                callDialogFragment.dismiss();
            } catch (Exception e) {
                Timber.e(e);
            }
        } else if (i == R.id.call_malaria_client_phone) {
            try {
                String phoneNumber = (String) v.getTag();
                TestUtil.launchDialer(callDialogFragment.getActivity(), callDialogFragment, phoneNumber);
                callDialogFragment.dismiss();
            } catch (Exception e) {
                Timber.e(e);
            }
        }
    }
}
