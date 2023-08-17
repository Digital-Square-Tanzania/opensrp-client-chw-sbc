package org.smartregister.chw.sbc.presenter;

import android.util.Log;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.smartregister.chw.sbc.contract.SbcRegisterContract;
import org.smartregister.barebones.R;

import java.lang.ref.WeakReference;
import java.util.List;

public class BaseSbcRegisterPresenter implements SbcRegisterContract.Presenter, SbcRegisterContract.InteractorCallBack {

    public static final String TAG = BaseSbcRegisterPresenter.class.getName();

    protected WeakReference<SbcRegisterContract.View> viewReference;
    private SbcRegisterContract.Interactor interactor;
    protected SbcRegisterContract.Model model;

    public BaseSbcRegisterPresenter(SbcRegisterContract.View view, SbcRegisterContract.Model model, SbcRegisterContract.Interactor interactor) {
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

    private SbcRegisterContract.View getView() {
        if (viewReference != null)
            return viewReference.get();
        else
            return null;
    }
}
