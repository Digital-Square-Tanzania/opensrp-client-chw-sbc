package org.smartregister.chw.sbc.interactor;


import android.content.Context;

import androidx.annotation.VisibleForTesting;

import com.google.gson.Gson;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;
import org.smartregister.chw.sbc.R;
import org.smartregister.chw.sbc.SbcLibrary;
import org.smartregister.chw.sbc.actionhelper.ArtAdherenceCounsellingActionHelper;
import org.smartregister.chw.sbc.actionhelper.CommentsActionHelper;
import org.smartregister.chw.sbc.actionhelper.HealthEducationActionHelper;
import org.smartregister.chw.sbc.actionhelper.HealthEducationOnHivInterventionsActionHelper;
import org.smartregister.chw.sbc.actionhelper.HivHealthEducationSbcMaterialsActionHelper;
import org.smartregister.chw.sbc.actionhelper.SbcActivityActionHelper;
import org.smartregister.chw.sbc.actionhelper.SbcVisitActionHelper;
import org.smartregister.chw.sbc.actionhelper.ServicesSurveyActionHelper;
import org.smartregister.chw.sbc.contract.BaseSbcVisitContract;
import org.smartregister.chw.sbc.dao.SbcDao;
import org.smartregister.chw.sbc.domain.MemberObject;
import org.smartregister.chw.sbc.domain.Visit;
import org.smartregister.chw.sbc.domain.VisitDetail;
import org.smartregister.chw.sbc.model.BaseSbcVisitAction;
import org.smartregister.chw.sbc.repository.VisitRepository;
import org.smartregister.chw.sbc.util.AppExecutors;
import org.smartregister.chw.sbc.util.Constants;
import org.smartregister.chw.sbc.util.JsonFormUtils;
import org.smartregister.chw.sbc.util.NCUtils;
import org.smartregister.chw.sbc.util.VisitUtils;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.clientandeventmodel.Obs;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.sync.helper.ECSyncHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import timber.log.Timber;


public class BaseSbcVisitInteractor implements BaseSbcVisitContract.Interactor {

    private final SbcLibrary sbcLibrary;
    private final LinkedHashMap<String, BaseSbcVisitAction> actionList;
    protected AppExecutors appExecutors;
    private ECSyncHelper syncHelper;
    private Context mContext;
    private Map<String, List<VisitDetail>> details = null;

    private BaseSbcVisitContract.InteractorCallBack callBack;

    @VisibleForTesting
    public BaseSbcVisitInteractor(AppExecutors appExecutors, SbcLibrary SbcLibrary, ECSyncHelper syncHelper) {
        this.appExecutors = appExecutors;
        this.sbcLibrary = SbcLibrary;
        this.syncHelper = syncHelper;
        this.actionList = new LinkedHashMap<>();
    }

    public BaseSbcVisitInteractor() {
        this(new AppExecutors(), org.smartregister.chw.sbc.SbcLibrary.getInstance(), org.smartregister.chw.sbc.SbcLibrary.getInstance().getEcSyncHelper());
    }

    @Override
    public void reloadMemberDetails(String memberID, BaseSbcVisitContract.InteractorCallBack callBack) {
        MemberObject memberObject = getMemberClient(memberID);
        if (memberObject != null) {
            final Runnable runnable = () -> {
                appExecutors.mainThread().execute(() -> callBack.onMemberDetailsReloaded(memberObject));
            };
            appExecutors.diskIO().execute(runnable);
        }
    }

    /**
     * Override this method and return actual member object for the provided user
     *
     * @param memberID unique identifier for the user
     * @return MemberObject wrapper for the user's data
     */
    @Override
    public MemberObject getMemberClient(String memberID) {
        return SbcDao.getMember(memberID);
    }

    @Override
    public void saveRegistration(String jsonString, boolean isEditMode, BaseSbcVisitContract.InteractorCallBack callBack) {
        Timber.v("saveRegistration");
    }

    @Override
    public void calculateActions(final BaseSbcVisitContract.View view, MemberObject memberObject, final BaseSbcVisitContract.InteractorCallBack callBack) {
        mContext = view.getContext();
        this.callBack = callBack;
        boolean isFirstVisit;
        if (view.getEditMode()) {
            Visit lastVisit = sbcLibrary.visitRepository().getLatestVisit(memberObject.getBaseEntityId(), Constants.EVENT_TYPE.SBC_FOLLOW_UP_VISIT);
            isFirstVisit = sbcLibrary.visitRepository().getVisits(memberObject.getBaseEntityId(), Constants.EVENT_TYPE.SBC_FOLLOW_UP_VISIT).size() < 2;
            if (lastVisit != null) {
                details = VisitUtils.getVisitGroups(sbcLibrary.visitDetailsRepository().getVisits(lastVisit.getVisitId()));
            }
        } else {
            isFirstVisit = sbcLibrary.visitRepository().getLatestVisit(memberObject.getBaseEntityId(), Constants.EVENT_TYPE.SBC_FOLLOW_UP_VISIT) == null;
        }

        final Runnable runnable = () -> {
            try {
                if (!isFirstVisit && !memberObject.getHivStatus().contains("positive"))
                    evaluateHivStatus(memberObject, details);

                evaluateSbcActivity(memberObject, details);
                evaluateServicesSurvey(memberObject, details);
                evaluateHealthEducation(memberObject, details);
                evaluateHealthEducationOnHivInterventions(memberObject, details);
                evaluateHealthEducationSbcMaterials(memberObject, details);

                if (memberObject.getHivStatus().contains("positive"))
                    evaluateArtAdherenceCounselling(memberObject, details);

                evaluateComments(memberObject, details);

            } catch (BaseSbcVisitAction.ValidationException e) {
                Timber.e(e);
            }

            appExecutors.mainThread().execute(() -> callBack.preloadActions(actionList));
        };

        appExecutors.diskIO().execute(runnable);
    }

    protected void evaluateHivStatus(MemberObject memberObject, Map<String, List<VisitDetail>> details) throws BaseSbcVisitAction.ValidationException {
        SbcVisitActionHelper actionHelper = new HivStatusActionHelper(mContext, memberObject);
        String actionName = mContext.getString(R.string.sbc_visit_action_title_hiv_status);
        BaseSbcVisitAction action = getBuilder(actionName).withOptional(false).withDetails(details).withHelper(actionHelper).withFormName(Constants.FORMS.SBC_ENROLLMENT).build();
        actionList.put(actionName, action);
    }

    protected void evaluateSbcActivity(MemberObject memberObject, Map<String, List<VisitDetail>> details) throws BaseSbcVisitAction.ValidationException {
        SbcVisitActionHelper actionHelper = new SbcActivityActionHelper(mContext, memberObject);

        String actionName = mContext.getString(R.string.sbc_visit_action_title_sbc_activity);

        BaseSbcVisitAction action = getBuilder(actionName).withOptional(false).withDetails(details).withHelper(actionHelper).withFormName(Constants.FORMS.SBC_ACTIVITY).build();

        actionList.put(actionName, action);
    }

    protected void evaluateServicesSurvey(MemberObject memberObject, Map<String, List<VisitDetail>> details) throws BaseSbcVisitAction.ValidationException {
        SbcVisitActionHelper actionHelper = new ServicesSurveyActionHelper(mContext, memberObject);

        String actionName = mContext.getString(R.string.sbc_visit_action_title_services_survey);

        BaseSbcVisitAction action = getBuilder(actionName).withOptional(false).withDetails(details).withHelper(actionHelper).withFormName(Constants.FORMS.SBC_SERVICE_SURVEY).build();

        actionList.put(actionName, action);
    }

    protected void evaluateHealthEducation(MemberObject memberObject, Map<String, List<VisitDetail>> details) throws BaseSbcVisitAction.ValidationException {
        SbcVisitActionHelper actionHelper = new HealthEducationActionHelper(mContext, memberObject);

        String actionName = mContext.getString(R.string.sbc_visit_action_title_health_education);

        BaseSbcVisitAction action = getBuilder(actionName).withOptional(false).withDetails(details).withHelper(actionHelper).withFormName(Constants.FORMS.SBC_HEALTH_EDUCATION).build();

        actionList.put(actionName, action);
    }

    protected void evaluateHealthEducationOnHivInterventions(MemberObject memberObject, Map<String, List<VisitDetail>> details) throws BaseSbcVisitAction.ValidationException {
        SbcVisitActionHelper actionHelper = new HealthEducationOnHivInterventionsActionHelper(mContext, memberObject);

        String actionName = mContext.getString(R.string.sbc_visit_action_title_health_education_on_hiv_interventions);

        BaseSbcVisitAction action = getBuilder(actionName).withOptional(false).withDetails(details).withHelper(actionHelper).withFormName(Constants.FORMS.SBC_HEALTH_EDUCATION_ON_HIV).build();

        actionList.put(actionName, action);
    }

    protected void evaluateHealthEducationSbcMaterials(MemberObject memberObject, Map<String, List<VisitDetail>> details) throws BaseSbcVisitAction.ValidationException {
        SbcVisitActionHelper actionHelper = new HivHealthEducationSbcMaterialsActionHelper(mContext, memberObject);

        String actionName = mContext.getString(R.string.sbc_visit_action_title_health_education_sbc_materials);

        BaseSbcVisitAction action = getBuilder(actionName).withOptional(false).withDetails(details).withHelper(actionHelper).withFormName(Constants.FORMS.HEALTH_EDUCATION_SBC_MATERIALS).build();

        actionList.put(actionName, action);
    }

    protected void evaluateArtAdherenceCounselling(MemberObject memberObject, Map<String, List<VisitDetail>> details) throws BaseSbcVisitAction.ValidationException {
        SbcVisitActionHelper actionHelper = new ArtAdherenceCounsellingActionHelper(mContext, memberObject);

        String actionName = mContext.getString(R.string.sbc_visit_action_title_art_and_condom_education);

        BaseSbcVisitAction action = getBuilder(actionName).withOptional(false).withDetails(details).withHelper(actionHelper).withFormName(Constants.FORMS.SBC_ART_CONDOM_EDUCATION).build();

        actionList.put(actionName, action);
    }

    protected void evaluateComments(MemberObject memberObject, Map<String, List<VisitDetail>> details) throws BaseSbcVisitAction.ValidationException {
        SbcVisitActionHelper actionHelper = new CommentsActionHelper(mContext, memberObject);

        String actionName = mContext.getString(R.string.sbc_visit_action_title_comments);

        BaseSbcVisitAction action = getBuilder(actionName).withOptional(true).withDetails(details).withHelper(actionHelper).withFormName(Constants.FORMS.SBC_COMMENTS).build();

        actionList.put(actionName, action);
    }

    public BaseSbcVisitAction.Builder getBuilder(String title) {
        return new BaseSbcVisitAction.Builder(mContext, title);
    }

    @Override
    public void submitVisit(final boolean editMode, final String memberID, final Map<String, BaseSbcVisitAction> map, final BaseSbcVisitContract.InteractorCallBack callBack) {
        final Runnable runnable = () -> {
            boolean result = true;
            try {
                submitVisit(editMode, memberID, map, "");
            } catch (Exception e) {
                Timber.e(e);
                result = false;
            }

            final boolean finalResult = result;
            appExecutors.mainThread().execute(() -> callBack.onSubmitted(finalResult));
        };

        appExecutors.diskIO().execute(runnable);
    }

    protected void submitVisit(final boolean editMode, final String memberID, final Map<String, BaseSbcVisitAction> map, String parentEventType) throws Exception {
        // create a map of the different types

        Map<String, BaseSbcVisitAction> externalVisits = new HashMap<>();
        Map<String, String> combinedJsons = new HashMap<>();
        String payloadType = null;
        String payloadDetails = null;

        // aggregate forms to be processed
        for (Map.Entry<String, BaseSbcVisitAction> entry : map.entrySet()) {
            String json = entry.getValue().getJsonPayload();
            if (StringUtils.isNotBlank(json)) {
                // do not process events that are meant to be in detached mode
                // in a similar manner to the the aggregated events

                BaseSbcVisitAction action = entry.getValue();
                BaseSbcVisitAction.ProcessingMode mode = action.getProcessingMode();

                if (mode == BaseSbcVisitAction.ProcessingMode.SEPARATE && StringUtils.isBlank(parentEventType)) {
                    externalVisits.put(entry.getKey(), entry.getValue());
                } else {
                    if (action.getActionStatus() != BaseSbcVisitAction.Status.PENDING)
                        combinedJsons.put(entry.getKey(), json);
                }

                payloadType = action.getPayloadType().name();
                payloadDetails = action.getPayloadDetails();
            }
        }

        String type = StringUtils.isBlank(parentEventType) ? getEncounterType() : getEncounterType();

        // persist to database
        Visit visit = saveVisit(editMode, memberID, type, combinedJsons, parentEventType);
        if (visit != null) {
            saveVisitDetails(visit, payloadType, payloadDetails);
            processExternalVisits(visit, externalVisits, memberID);
        }

        if (sbcLibrary.isSubmitOnSave()) {
            List<Visit> visits = new ArrayList<>(1);
            visits.add(visit);
            VisitUtils.processVisits(visits, sbcLibrary.visitRepository(), sbcLibrary.visitDetailsRepository());

            Context context = sbcLibrary.getInstance().context().applicationContext();

        }
    }

    /**
     * recursively persist visits to the db
     *
     * @param visit
     * @param externalVisits
     * @param memberID
     * @throws Exception
     */
    protected void processExternalVisits(Visit visit, Map<String, BaseSbcVisitAction> externalVisits, String memberID) throws Exception {
        if (visit != null && !externalVisits.isEmpty()) {
            for (Map.Entry<String, BaseSbcVisitAction> entry : externalVisits.entrySet()) {
                Map<String, BaseSbcVisitAction> subEvent = new HashMap<>();
                subEvent.put(entry.getKey(), entry.getValue());

                String subMemberID = entry.getValue().getBaseEntityID();
                if (StringUtils.isBlank(subMemberID)) subMemberID = memberID;

                submitVisit(false, subMemberID, subEvent, visit.getVisitType());
            }
        }
    }

    protected @Nullable Visit saveVisit(boolean editMode, String memberID, String encounterType, final Map<String, String> jsonString, String parentEventType) throws Exception {

        AllSharedPreferences allSharedPreferences = sbcLibrary.getInstance().context().allSharedPreferences();

        String derivedEncounterType = StringUtils.isBlank(parentEventType) ? encounterType : "";
        Event baseEvent = JsonFormUtils.processVisitJsonForm(allSharedPreferences, memberID, derivedEncounterType, jsonString, getTableName());

        // only tag the first event with the date
        if (StringUtils.isBlank(parentEventType)) {
            prepareEvent(baseEvent);
        } else {
            prepareSubEvent(baseEvent);
        }

        if (baseEvent != null) {
            baseEvent.setFormSubmissionId(JsonFormUtils.generateRandomUUIDString());
            JsonFormUtils.tagEvent(allSharedPreferences, baseEvent);

            String visitID = (editMode) ? visitRepository().getLatestVisit(memberID, getEncounterType()).getVisitId() : JsonFormUtils.generateRandomUUIDString();

            // reset database
            if (editMode) {
                Visit visit = visitRepository().getVisitByVisitId(visitID);
                if (visit != null) baseEvent.setEventDate(visit.getDate());

                VisitUtils.deleteProcessedVisit(visitID, memberID);
                deleteOldVisit(visitID);
            }

            Visit visit = NCUtils.eventToVisit(baseEvent, visitID);
            visit.setPreProcessedJson(new Gson().toJson(baseEvent));
            visit.setParentVisitID(getParentVisitEventID(visit, parentEventType));

            visitRepository().addVisit(visit);
            return visit;
        }
        return null;
    }

    protected String getParentVisitEventID(Visit visit, String parentEventType) {
        return visitRepository().getParentVisitEventID(visit.getBaseEntityId(), parentEventType, visit.getDate());
    }

    @VisibleForTesting
    public VisitRepository visitRepository() {
        return SbcLibrary.getInstance().visitRepository();
    }

    protected void deleteOldVisit(String visitID) {
        visitRepository().deleteVisit(visitID);
        SbcLibrary.getInstance().visitDetailsRepository().deleteVisitDetails(visitID);

        List<Visit> childVisits = visitRepository().getChildEvents(visitID);
        for (Visit v : childVisits) {
            visitRepository().deleteVisit(v.getVisitId());
            SbcLibrary.getInstance().visitDetailsRepository().deleteVisitDetails(v.getVisitId());
        }
    }


    protected void saveVisitDetails(Visit visit, String payloadType, String payloadDetails) {
        if (visit.getVisitDetails() == null) return;

        for (Map.Entry<String, List<VisitDetail>> entry : visit.getVisitDetails().entrySet()) {
            if (entry.getValue() != null) {
                for (VisitDetail d : entry.getValue()) {
                    d.setPreProcessedJson(payloadDetails);
                    d.setPreProcessedType(payloadType);
                    SbcLibrary.getInstance().visitDetailsRepository().addVisitDetails(d);
                }
            }
        }
    }

    /**
     * Injects implementation specific changes to the event
     *
     * @param baseEvent
     */
    protected void prepareEvent(Event baseEvent) {
        if (baseEvent != null) {
            // add sbc date obs and last
            List<Object> list = new ArrayList<>();
            list.add(new Date());
            baseEvent.addObs(new Obs("concept", "text", "vmmc_visit_date", "", list, new ArrayList<>(), null, "vmmc_visit_date"));
        }
    }

    /**
     * injects additional meta data to the event
     *
     * @param baseEvent
     */
    protected void prepareSubEvent(Event baseEvent) {
        Timber.v("You can add information to sub events");
    }

    protected String getEncounterType() {
        return Constants.EVENT_TYPE.SBC_FOLLOW_UP_VISIT;
    }

    protected String getTableName() {
        return Constants.TABLES.SBC_REGISTER;
    }

    class HivStatusActionHelper extends SbcVisitActionHelper {
        protected Context context;
        protected MemberObject memberObject;
        protected String hivStatus;

        public HivStatusActionHelper(Context context, MemberObject memberObject) {
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
                hivStatus = JsonFormUtils.getValue(jsonObject, "hiv_status");

                if(hivStatus.contains("positive")){
                    evaluateArtAdherenceCounselling(memberObject, details);
                }else{
                    actionList.remove(mContext.getString(R.string.sbc_visit_action_title_art_and_condom_education));
                }
                appExecutors.mainThread().execute(() -> callBack.preloadActions(actionList));
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
            if (StringUtils.isNotBlank(hivStatus)) {
                return BaseSbcVisitAction.Status.COMPLETED;
            } else {
                return BaseSbcVisitAction.Status.PENDING;
            }
        }
    }

}