package org.smartregister.chw.sbc.util;

public interface Constants {

    int REQUEST_CODE_GET_JSON = 2244;
    String ENCOUNTER_TYPE = "encounter_type";
    String STEP_ONE = "step1";
    String STEP_TWO = "step2";

    interface JSON_FORM_EXTRA {
        String JSON = "json";
        String ENCOUNTER_TYPE = "encounter_type";

        String DELETE_EVENT_ID = "deleted_event_id";

        String DELETE_FORM_SUBMISSION_ID = "deleted_form_submission_id";
    }

    interface EVENT_TYPE {
        String SBC_REGISTRATION = "SBC Registration";
        String SBC_FOLLOW_UP_VISIT = "SBC Follow-up Visit";

        String SBC_HEALTH_EDUCATION_MOBILIZATION = "SBC Health Education Mobilization";

        String SBC_MONTHLY_SOCIAL_MEDIA_REPORT = "Monthly Social Media Report";

        String VOID_EVENT = "Void Event";

        String DELETE_EVENT = "Delete Event";
    }

    interface FORMS {
        String SBC_ENROLLMENT = "sbc_enrollment";
        String SBC_HIV_STATUS = "sbc_hiv_status";
        String SBC_ACTIVITY = "sbc_activity";
        String SBC_HEALTH_EDUCATION = "sbc_health_education";
        String SBC_HEALTH_EDUCATION_ON_HIV = "sbc_health_education_hiv_intervention";
        String HEALTH_EDUCATION_SBC_MATERIALS = "sbc_health_education_hiv_materials";
        String SBC_SERVICE_SURVEY = "sbc_service_survey";
        String SBC_ART_CONDOM_EDUCATION = "sbc_art_condom_education";
        String SBC_COMMENTS = "sbc_comments";
        String SBC_MOBILIZATION_SESSION= "sbc_health_education_mobilization";

        String SBC_MONTHLY_SOCIAL_MEDIA_REPORT= "sbc_monthly_social_media_report";
    }

    interface TABLES {
        String SBC_REGISTER = "ec_sbc_register";

        String SBC_FOLLOW_UP = "ec_sbc_follow_up_visit";

        String SBC_MOBILIZATION_SESSIONS = "ec_sbc_mobilization_session";

        String SBC_MONTHLY_SOCIAL_MEDIA_REPORT = "ec_sbc_monthly_social_media_report";

    }

    interface ACTIVITY_PAYLOAD {
        String BASE_ENTITY_ID = "BASE_ENTITY_ID";
        String FAMILY_BASE_ENTITY_ID = "FAMILY_BASE_ENTITY_ID";
        String ACTION = "ACTION";
        String SBC_FORM_NAME = "SBC_FORM_NAME";
        String EDIT_MODE = "editMode";
        String MEMBER_PROFILE_OBJECT = "MemberObject";

    }

    interface ACTIVITY_PAYLOAD_TYPE {
        String REGISTRATION = "REGISTRATION";
        String FOLLOW_UP_VISIT = "FOLLOW_UP_VISIT";
    }

    interface CONFIGURATION {
        String SBC_REGISTRATION_CONFIGURATION = "sbc_registration";
    }

}