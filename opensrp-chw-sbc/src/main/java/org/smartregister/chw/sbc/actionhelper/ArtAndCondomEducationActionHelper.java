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
public class ArtAndCondomEducationActionHelper extends SbcVisitActionHelper {
    protected Context context;
    protected MemberObject memberObject;
    protected String sbcArtAdherence;

    public ArtAndCondomEducationActionHelper(Context context, MemberObject memberObject) {
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
            sbcArtAdherence = JsonFormUtils.getValue(jsonObject, "sbc_art_adherence");
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
        if (StringUtils.isNotBlank(sbcArtAdherence)) {
            return BaseSbcVisitAction.Status.COMPLETED;
        } else {
            return BaseSbcVisitAction.Status.PENDING;
        }
    }
}