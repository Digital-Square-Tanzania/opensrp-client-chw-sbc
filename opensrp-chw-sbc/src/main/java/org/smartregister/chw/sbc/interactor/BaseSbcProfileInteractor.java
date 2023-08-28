package org.smartregister.chw.sbc.interactor;

import androidx.annotation.VisibleForTesting;

import org.smartregister.chw.sbc.SbcLibrary;
import org.smartregister.chw.sbc.contract.SbcProfileContract;
import org.smartregister.chw.sbc.domain.MemberObject;
import org.smartregister.chw.sbc.domain.Visit;
import org.smartregister.chw.sbc.util.AppExecutors;
import org.smartregister.chw.sbc.util.Constants;
import org.smartregister.chw.sbc.util.SbcUtil;

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
            callback.refreshMedicalHistory(getVisit(Constants.EVENT_TYPE.SBC_FOLLOW_UP_VISIT, memberObject) != null);
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

    private Visit getVisit(String eventType, MemberObject memberObject) {
        try {
            return SbcLibrary.getInstance().visitRepository().getLatestVisit(memberObject.getBaseEntityId(), eventType);
        } catch (Exception e) {
            return null;
        }
    }
}
