package org.smartregister.chw.sbc.interactor;

import androidx.annotation.VisibleForTesting;

import org.smartregister.chw.sbc.contract.SbcRegisterContract;
import org.smartregister.chw.sbc.util.SbcUtil;
import org.smartregister.chw.sbc.util.AppExecutors;

public class BaseSbcRegisterInteractor implements SbcRegisterContract.Interactor {

    private AppExecutors appExecutors;

    @VisibleForTesting
    BaseSbcRegisterInteractor(AppExecutors appExecutors) {
        this.appExecutors = appExecutors;
    }

    public BaseSbcRegisterInteractor() {
        this(new AppExecutors());
    }

    @Override
    public void saveRegistration(final String jsonString, final SbcRegisterContract.InteractorCallBack callBack) {

        Runnable runnable = () -> {
            try {
                SbcUtil.saveFormEvent(jsonString);
            } catch (Exception e) {
                e.printStackTrace();
            }

            appExecutors.mainThread().execute(() -> callBack.onRegistrationSaved());
        };
        appExecutors.diskIO().execute(runnable);
    }
}
