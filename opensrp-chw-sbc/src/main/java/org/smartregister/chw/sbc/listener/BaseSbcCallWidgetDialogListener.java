package org.smartregister.chw.sbc.listener;


import android.view.View;

import org.smartregister.chw.sbc.fragment.BaseSbcCallDialogFragment;
import org.smartregister.chw.sbc.util.SbcUtil;
import org.smartregister.barebones.R;

import timber.log.Timber;

public class BaseSbcCallWidgetDialogListener implements View.OnClickListener {

    private BaseSbcCallDialogFragment callDialogFragment;

    public BaseSbcCallWidgetDialogListener(BaseSbcCallDialogFragment dialogFragment) {
        callDialogFragment = dialogFragment;
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.sbc_call_close) {
            callDialogFragment.dismiss();
        } else if (i == R.id.sbc_call_head_phone) {
            try {
                String phoneNumber = (String) v.getTag();
                SbcUtil.launchDialer(callDialogFragment.getActivity(), callDialogFragment, phoneNumber);
                callDialogFragment.dismiss();
            } catch (Exception e) {
                Timber.e(e);
            }
        } else if (i == R.id.call_sbc_client_phone) {
            try {
                String phoneNumber = (String) v.getTag();
                SbcUtil.launchDialer(callDialogFragment.getActivity(), callDialogFragment, phoneNumber);
                callDialogFragment.dismiss();
            } catch (Exception e) {
                Timber.e(e);
            }
        }
    }
}
