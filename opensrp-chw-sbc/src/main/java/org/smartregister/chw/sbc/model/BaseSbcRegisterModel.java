package org.smartregister.chw.sbc.model;

import org.json.JSONObject;
import org.smartregister.chw.sbc.contract.SbcRegisterContract;
import org.smartregister.chw.sbc.util.SbcJsonFormUtils;

public class BaseSbcRegisterModel implements SbcRegisterContract.Model {

    @Override
    public JSONObject getFormAsJson(String formName, String entityId, String currentLocationId) throws Exception {
        JSONObject jsonObject = SbcJsonFormUtils.getFormAsJson(formName);
        SbcJsonFormUtils.getRegistrationForm(jsonObject, entityId, currentLocationId);

        return jsonObject;
    }

}
