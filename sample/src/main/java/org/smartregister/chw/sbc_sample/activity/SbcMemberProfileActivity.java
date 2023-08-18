package org.smartregister.chw.sbc_sample.activity;

import android.app.Activity;
import android.content.Intent;

import org.smartregister.chw.sbc.activity.BaseSbcProfileActivity;
import org.smartregister.chw.sbc.domain.MemberObject;
import org.smartregister.chw.sbc.presenter.BaseSbcProfilePresenter;
import org.smartregister.chw.sbc.util.Constants;
import org.smartregister.chw.sbc_sample.interactor.SbcProfileInteractor;


public class SbcMemberProfileActivity extends BaseSbcProfileActivity {

    public static void startMe(Activity activity, String baseEntityID) {
        Intent intent = new Intent(activity, SbcMemberProfileActivity.class);
        intent.putExtra(Constants.ACTIVITY_PAYLOAD.BASE_ENTITY_ID, baseEntityID);
        activity.startActivityForResult(intent, Constants.REQUEST_CODE_GET_JSON);
    }

    protected void registerPresenter() {
        profilePresenter = new BaseSbcProfilePresenter(this, new SbcProfileInteractor(), memberObject);
    }

    @Override
    protected MemberObject getMemberObject(String baseEntityId) {
        return EntryActivity.getSampleMember();
    }
}