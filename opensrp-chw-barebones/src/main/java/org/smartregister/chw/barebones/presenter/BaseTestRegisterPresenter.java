package org.smartregister.chw.barebones.presenter;

import android.util.Log;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.smartregister.chw.barebones.contract.TestRegisterContract;
import org.smartregister.barebones.R;

import java.lang.ref.WeakReference;
import java.util.List;

public class BaseTestRegisterPresenter implements TestRegisterContract.Presenter, TestRegisterContract.InteractorCallBack {

    public static final String TAG = BaseTestRegisterPresenter.class.getName();

    protected WeakReference<TestRegisterContract.View> viewReference;
    private TestRegisterContract.Interactor interactor;
    protected TestRegisterContract.Model model;

    public BaseTestRegisterPresenter(TestRegisterContract.View view, TestRegisterContract.Model model, TestRegisterContract.Interactor interactor) {
        viewReference = new WeakReference<>(view);
        this.interactor = interactor;
        this.model = model;
    }

    @Override
    public void startForm(String formName, String entityId, String metadata, String currentLocationId) throws Exception {
        if (StringUtils.isBlank(entityId)) {
            return;
        }

        JSONObject form = model.getFormAsJson(formName, entityId, currentLocationId);
        getView().startFormActivity(form);
    }

    @Override
    public void saveForm(String jsonString) {
        try {
            getView().showProgressDialog(R.string.saving_dialog_title);
            interactor.saveRegistration(jsonString, this);
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
    }

    @Override
    public void onRegistrationSaved() {
        getView().hideProgressDialog();

    }

    @Override
    public void registerViewConfigurations(List<String> list) {
//        implement
    }

    @Override
    public void unregisterViewConfiguration(List<String> list) {
//        implement
    }

    @Override
    public void onDestroy(boolean b) {
//        implement
    }

    @Override
    public void updateInitials() {
//        implement
    }

    private TestRegisterContract.View getView() {
        if (viewReference != null)
            return viewReference.get();
        else
            return null;
    }
}
