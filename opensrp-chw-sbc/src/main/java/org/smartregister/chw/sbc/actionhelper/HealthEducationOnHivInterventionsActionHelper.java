package org.smartregister.chw.sbc.actionhelper;

import android.content.Context;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.smartregister.chw.sbc.domain.MemberObject;
import org.smartregister.chw.sbc.model.BaseSbcVisitAction;
import org.smartregister.chw.sbc.util.JsonFormUtils;

import timber.log.Timber;

/**
 * SBC Activity Action Helper
 */
public class HealthEducationOnHivInterventionsActionHelper extends SbcVisitActionHelper {
    protected Context context;
    protected MemberObject memberObject;
    protected String sbcHealthEducationOnHivIntervention;

    public HealthEducationOnHivInterventionsActionHelper(Context context, MemberObject memberObject) {
        this.context = context;
        this.memberObject = memberObject;
    }

    /**
     * set preprocessed status to be inert
     *
     * @return null
     */
    @Override
    public String getPreProcessed() {
        return null;
    }

    @Override
    public void onPayloadReceived(String jsonPayload) {
        try {
            JSONObject jsonObject = new JSONObject(jsonPayload);
            sbcHealthEducationOnHivIntervention = JsonFormUtils.getValue(jsonObject, "sbc_hiv_intervention");
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    @Override
    public String evaluateSubTitle() {
        return null;
    }

    @Override
    public BaseSbcVisitAction.Status evaluateStatusOnPayload() {
        if (StringUtils.isNotBlank(sbcHealthEducationOnHivIntervention)) {
            return BaseSbcVisitAction.Status.COMPLETED;
        } else {
            return BaseSbcVisitAction.Status.PENDING;
        }
    }
}