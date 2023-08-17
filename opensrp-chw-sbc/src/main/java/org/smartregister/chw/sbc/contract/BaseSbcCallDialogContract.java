package org.smartregister.chw.sbc.contract;

import android.content.Context;

public interface BaseSbcCallDialogContract {

    interface View {
        void setPendingCallRequest(Dialer dialer);
        Context getCurrentContext();
    }

    interface Dialer {
        void callMe();
    }
}
