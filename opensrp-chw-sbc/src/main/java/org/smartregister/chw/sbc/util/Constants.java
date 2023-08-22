package org.smartregister.chw.sbc.util;

public interface Constants {

    int REQUEST_CODE_GET_JSON = 2244;
    String ENCOUNTER_TYPE = "encounter_type";
    String STEP_ONE = "step1";
    String STEP_TWO = "step2";

    interface JSON_FORM_EXTRA {
        String JSON = "json";
        String ENCOUNTER_TYPE = "encounter_type";
    }

    interface EVENT_TYPE {
        String SBC_REGISTRATION = "SBC Registration";
        String SBC_FOLLOW_UP_VISIT = "SBC Follow-up Visit";

        String VOID_EVENT = "Void Event";
    }

    interface FORMS {
        String SBC_REGISTRATION = "sbc_registration";
        String SBC_ACTIVITY = "sbc_activity";
        String SBC_HEALTH_EDUCATION = "sbc_health_education";
        String SBC_HEALTH_EDUCATION_ON_HIV = "sbc_health_education_hiv_intervention";
        String HEALTH_EDUCATION_SBC_MATERIALS = "sbc_health_education_hiv_materials";
        String SBC_SERVICE_SURVEY = "sbc_service_survey";
        String SBC_ART_CONDOM_EDUCATION = "sbc_art_condom_education";
        String SBC_COMMENTS = "sbc_comments";
    }

    interface TABLES {
        String SBC_REGISTER = "ec_sbc_register";

        String SBC_FOLLOW_UP = "ec_sbc_follow_up_visit";

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