package org.smartregister.chw.barebones.interactor;

import android.support.annotation.VisibleForTesting;

import org.smartregister.chw.barebones.contract.TestRegisterContract;
import org.smartregister.chw.barebones.util.AppExecutors;
import org.smartregister.chw.barebones.util.TestUtil;

public class BaseTestRegisterInteractor implements TestRegisterContract.Interactor {

    private AppExecutors appExecutors;

    @VisibleForTesting
    BaseTestRegisterInteractor(AppExecutors appExecutors) {
        this.appExecutors = appExecutors;
    }

    public BaseTestRegisterInteractor() {
        this(new AppExecutors());
    }

    @Override
    public void saveRegistration(final String jsonString, final TestRegisterContract.InteractorCallBack callBack) {

        Runnable runnable = () -> {
            try {
                TestUtil.saveFormEvent(jsonString);
            } catch (Exception e) {
                e.printStackTrace();
            }

            appExecutors.mainThread().execute(() -> callBack.onRegistrationSaved());
        };
        appExecutors.diskIO().execute(runnable);
    }
}
