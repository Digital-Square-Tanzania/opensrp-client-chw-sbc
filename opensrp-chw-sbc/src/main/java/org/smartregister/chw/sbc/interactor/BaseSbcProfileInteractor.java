package org.smartregister.chw.sbc.interactor;

import android.support.annotation.VisibleForTesting;

import org.smartregister.chw.sbc.contract.SbcProfileContract;
import org.smartregister.chw.sbc.util.SbcUtil;
import org.smartregister.chw.sbc.domain.MemberObject;
import org.smartregister.chw.sbc.util.AppExecutors;
import org.smartregister.domain.AlertStatus;

import java.util.Date;

public class BaseSbcProfileInteractor implements SbcProfileContract.Interactor {
    protected AppExecutors appExecutors;

    @VisibleForTesting
    BaseSbcProfileInteractor(AppExecutors appExecutors) {
        this.appExecutors = appExecutors;
    }

    public BaseSbcProfileInteractor() {
        this(new AppExecutors());
    }

    @Override
    public void refreshProfileInfo(MemberObject memberObject, SbcProfileContract.InteractorCallBack callback) {
        Runnable runnable = () -> appExecutors.mainThread().execute(() -> {
            callback.refreshFamilyStatus(AlertStatus.normal);
            callback.refreshMedicalHistory(true);
            callback.refreshUpComingServicesStatus("Malaria Visit", AlertStatus.normal, new Date());
        });
        appExecutors.diskIO().execute(runnable);
    }

    @Override
    public void saveRegistration(final String jsonString, final SbcProfileContract.InteractorCallBack callback) {

        Runnable runnable = () -> {
            try {
                SbcUtil.saveFormEvent(jsonString);
            } catch (Exception e) {
                e.printStackTrace();
            }

        };
        appExecutors.diskIO().execute(runnable);
    }
}
