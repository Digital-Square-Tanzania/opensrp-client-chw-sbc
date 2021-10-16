package org.smartregister.chw.barebones.interactor;

import android.support.annotation.VisibleForTesting;

import org.smartregister.chw.barebones.contract.TestProfileContract;
import org.smartregister.chw.barebones.domain.MemberObject;
import org.smartregister.chw.barebones.util.AppExecutors;
import org.smartregister.chw.barebones.util.TestUtil;
import org.smartregister.domain.AlertStatus;

import java.util.Date;

public class BaseTestProfileInteractor implements TestProfileContract.Interactor {
    protected AppExecutors appExecutors;

    @VisibleForTesting
    BaseTestProfileInteractor(AppExecutors appExecutors) {
        this.appExecutors = appExecutors;
    }

    public BaseTestProfileInteractor() {
        this(new AppExecutors());
    }

    @Override
    public void refreshProfileInfo(MemberObject memberObject, TestProfileContract.InteractorCallBack callback) {
        Runnable runnable = () -> appExecutors.mainThread().execute(() -> {
            callback.refreshFamilyStatus(AlertStatus.normal);
            callback.refreshMedicalHistory(true);
            callback.refreshUpComingServicesStatus("Malaria Visit", AlertStatus.normal, new Date());
        });
        appExecutors.diskIO().execute(runnable);
    }

    @Override
    public void saveRegistration(final String jsonString, final TestProfileContract.InteractorCallBack callback) {

        Runnable runnable = () -> {
            try {
                TestUtil.saveFormEvent(jsonString);
            } catch (Exception e) {
                e.printStackTrace();
            }

        };
        appExecutors.diskIO().execute(runnable);
    }
}
