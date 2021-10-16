package org.smartregister.chw.barebones.model;

import org.json.JSONObject;
import org.smartregister.chw.barebones.contract.TestRegisterContract;
import org.smartregister.chw.barebones.util.TestJsonFormUtils;

public class BaseTestRegisterModel implements TestRegisterContract.Model {

    @Override
    public JSONObject getFormAsJson(String formName, String entityId, String currentLocationId) throws Exception {
        JSONObject jsonObject = TestJsonFormUtils.getFormAsJson(formName);
        TestJsonFormUtils.getRegistrationForm(jsonObject, entityId, currentLocationId);

        return jsonObject;
    }

}
