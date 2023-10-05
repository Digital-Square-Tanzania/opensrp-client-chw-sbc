package org.smartregister.chw.sbc.dao;

import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.chw.sbc.SbcLibrary;
import org.smartregister.chw.sbc.domain.MemberObject;
import org.smartregister.chw.sbc.util.Constants;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.dao.AbstractDao;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import timber.log.Timber;

public class SbcDao extends AbstractDao {
    public static void closeSbcMemberFromRegister(String baseEntityID) {
        String sql = "update ec_malaria_confirmation set is_closed = 1 where base_entity_id = '" + baseEntityID + "'";
        updateDB(sql);
    }

    public static boolean isRegisteredForSbc(String baseEntityID) {
        String sql = "SELECT count(p.base_entity_id) count FROM " + Constants.TABLES.SBC_REGISTER + " p " + "WHERE p.base_entity_id = '" + baseEntityID + "' AND p.is_closed = 0";

        DataMap<Integer> dataMap = cursor -> getCursorIntValue(cursor, "count");

        List<Integer> res = readData(sql, dataMap);
        if (res == null || res.size() != 1) return false;

        return res.get(0) > 0;
    }

    public static MemberObject getMember(String baseEntityID) {
        String sql = "select m.base_entity_id , m.unique_id , m.relational_id , m.dob , m.first_name , m.middle_name , m.last_name , m.gender , m.phone_number , m.other_phone_number , f.first_name family_name ,f.primary_caregiver , f.family_head , f.village_town ,fh.first_name family_head_first_name , fh.middle_name family_head_middle_name , fh.last_name family_head_last_name, fh.phone_number family_head_phone_number ,  pcg.first_name pcg_first_name , pcg.last_name pcg_last_name , pcg.middle_name pcg_middle_name , pcg.phone_number  pcg_phone_number , mr.* from ec_family_member m inner join ec_family f on m.relational_id = f.base_entity_id inner join " + Constants.TABLES.SBC_REGISTER + " mr on mr.base_entity_id = m.base_entity_id left join ec_family_member fh on fh.base_entity_id = f.family_head left join ec_family_member pcg on pcg.base_entity_id = f.primary_caregiver where m.base_entity_id ='" + baseEntityID + "' ";
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());

        DataMap<MemberObject> dataMap = cursor -> {
            MemberObject memberObject = new MemberObject();

            memberObject.setFirstName(getCursorValue(cursor, "first_name", ""));
            memberObject.setMiddleName(getCursorValue(cursor, "middle_name", ""));
            memberObject.setLastName(getCursorValue(cursor, "last_name", ""));
            memberObject.setAddress(getCursorValue(cursor, "village_town"));
            memberObject.setGender(getCursorValue(cursor, "gender"));
            memberObject.setUniqueId(getCursorValue(cursor, "unique_id", ""));
            memberObject.setDob(getCursorValue(cursor, "dob"));
            memberObject.setFamilyBaseEntityId(getCursorValue(cursor, "relational_id", ""));
            memberObject.setRelationalId(getCursorValue(cursor, "relational_id", ""));
            memberObject.setPrimaryCareGiver(getCursorValue(cursor, "primary_caregiver"));
            memberObject.setFamilyName(getCursorValue(cursor, "family_name", ""));
            memberObject.setPhoneNumber(getCursorValue(cursor, "phone_number", ""));
            memberObject.setBaseEntityId(getCursorValue(cursor, "base_entity_id", ""));
            memberObject.setFamilyHead(getCursorValue(cursor, "family_head", ""));
            memberObject.setFamilyHeadPhoneNumber(getCursorValue(cursor, "pcg_phone_number", ""));
            memberObject.setFamilyHeadPhoneNumber(getCursorValue(cursor, "family_head_phone_number", ""));
            memberObject.setHivStatus(getCursorValue(cursor, "hiv_status", ""));
            memberObject.setCtcNumber(getCursorValue(cursor, "ctc_number", ""));

            String familyHeadName = getCursorValue(cursor, "family_head_first_name", "") + " " + getCursorValue(cursor, "family_head_middle_name", "");

            familyHeadName = (familyHeadName.trim() + " " + getCursorValue(cursor, "family_head_last_name", "")).trim();
            memberObject.setFamilyHeadName(familyHeadName);

            String familyPcgName = getCursorValue(cursor, "pcg_first_name", "") + " " + getCursorValue(cursor, "pcg_middle_name", "");

            familyPcgName = (familyPcgName.trim() + " " + getCursorValue(cursor, "pcg_last_name", "")).trim();
            memberObject.setPrimaryCareGiverName(familyPcgName);

            return memberObject;
        };

        List<MemberObject> res = readData(sql, dataMap);
        if (res == null || res.size() != 1) return null;

        return res.get(0);
    }

    public static void updateSbcMobilization(SbcMobilization sbcMobilization) {
        String sql = String.format("INSERT INTO " + Constants.TABLES.SBC_MOBILIZATION_SESSIONS + " (" + "id, " + "mobilization_date, " + "community_sbc_activity_provided, " + "other_interventions_iec_materials_distributed, " + "number_audio_visuals_distributed, " + "number_audio_distributed, " + "number_print_materials_distributed, " + "pmtct_iec_materials_distributed, " + "number_pmtct_audio_visuals_distributed_male, " + "number_pmtct_audio_visuals_distributed_female, " + "number_pmtct_audio_distributed_male, " + "number_pmtct_audio_distributed_female, " + "number_pmtct_print_materials_distributed_male, " + "number_pmtct_print_materials_distributed_female" + ") " + "VALUES ('%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s') ON CONFLICT (id) DO UPDATE SET " + "mobilization_date = '%s', " + "community_sbc_activity_provided = '%s', " + "other_interventions_iec_materials_distributed = '%s', " + "number_audio_visuals_distributed = '%s', " + "number_audio_distributed = '%s', " + "number_print_materials_distributed = '%s', " + "pmtct_iec_materials_distributed = '%s', " + "number_pmtct_audio_visuals_distributed_male = '%s', " + "number_pmtct_audio_visuals_distributed_female = '%s', " + "number_pmtct_audio_distributed_male = '%s', " + "number_pmtct_audio_distributed_female = '%s', " + "number_pmtct_print_materials_distributed_male = '%s', " + "number_pmtct_print_materials_distributed_female = '%s' ", sbcMobilization.getBaseEntityID(), sbcMobilization.getMobilizationDate(), sbcMobilization.getCommunitySbcActivityProvided() , sbcMobilization.getOtherInterventionsIecMaterialsDistributed(),sbcMobilization.getNumberAudioVisualsDistributed(), sbcMobilization.getNumberAudioDistributed(), sbcMobilization.getNumberPrintMaterialsDistributed(), sbcMobilization.getPmtctIecMaterialsDistributed(), sbcMobilization.getNumberPmtctAudioVisualsDistributedMale(), sbcMobilization.getNumberPmtctAudioVisualsDistributedFemale(), sbcMobilization.getNumberPmtctAudioDistributedMale(),
                sbcMobilization.getNumberPmtctAudioDistributedFemale(), sbcMobilization.getNumberPmtctPrintMaterialsDistributedMale(),
                sbcMobilization.getNumberPmtctPrintMaterialsDistributedFemale(),
                sbcMobilization.getMobilizationDate(), sbcMobilization.getCommunitySbcActivityProvided(),
                sbcMobilization.getOtherInterventionsIecMaterialsDistributed(), sbcMobilization.getNumberAudioVisualsDistributed(),
                sbcMobilization.getNumberAudioDistributed(), sbcMobilization.getNumberPrintMaterialsDistributed(), sbcMobilization.getPmtctIecMaterialsDistributed(), sbcMobilization.getNumberPmtctAudioVisualsDistributedMale(),
                sbcMobilization.getNumberPmtctAudioVisualsDistributedFemale(), sbcMobilization.getNumberPmtctAudioDistributedMale(), sbcMobilization.getNumberPmtctAudioDistributedFemale(),
                sbcMobilization.getNumberPmtctPrintMaterialsDistributedMale(), sbcMobilization.getNumberPmtctPrintMaterialsDistributedFemale());
        updateDB(sql);
    }

    public static void updateSbcSocialMediaMonthlyReport(String baseEntityID, String reportingMonth, String organizationName, String otherOrganizationName, String socialMediaHivMsgDistribution, String numberBeneficiariesReachedFacebook, String numberMessagesPublications, String numberAiredMessagesBroadcasted) {
        String sql = String.format("INSERT INTO " + Constants.TABLES.SBC_MONTHLY_SOCIAL_MEDIA_REPORT + " (" + "id, " + "reporting_month, " + "organization_name, " + "other_organization_name, " + "social_media_hiv_msg_distribution, " + "number_beneficiaries_reached_facebook, " + "number_messages_publications, " + "number_aired_messages_broadcasted " + ") " + "VALUES ('%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s') ON CONFLICT (id) DO UPDATE SET " +
                        "reporting_month = '%s', " + "organization_name = '%s', " + "other_organization_name = '%s', " + "social_media_hiv_msg_distribution = '%s', " + "number_beneficiaries_reached_facebook = '%s', " + "number_messages_publications = '%s', " + "number_aired_messages_broadcasted = '%s' ",
                baseEntityID, reportingMonth, organizationName, otherOrganizationName, socialMediaHivMsgDistribution, numberBeneficiariesReachedFacebook, numberMessagesPublications, numberAiredMessagesBroadcasted,
                reportingMonth, organizationName, otherOrganizationName, socialMediaHivMsgDistribution, numberBeneficiariesReachedFacebook, numberMessagesPublications, numberAiredMessagesBroadcasted);
        updateDB(sql);
    }

    public static Event getEventByFormSubmissionId(String formSubmissionId) {
        String sql = "select json from event where formSubmissionId = '" + formSubmissionId + "'";
        DataMap<Event> dataMap = (c) -> {
            Event event;
            try {
                event = (Event) SbcLibrary.getInstance().getEcSyncHelper().convert(new JSONObject(getCursorValue(c, "json")), Event.class);
            } catch (JSONException e) {
                Timber.e(e);
                return null;
            }

            return event;
        };
        return (Event) AbstractDao.readSingleValue(sql, dataMap);
    }

    public static class SbcMobilization {
        private String baseEntityID;

        private String mobilizationDate;

        private String communitySbcActivityProvided;

        private String otherInterventionsIecMaterialsDistributed;

        private String numberAudioVisualsDistributed;

        private String numberAudioDistributed;

        private String numberPrintMaterialsDistributed;

        private String pmtctIecMaterialsDistributed;

        private String numberPmtctAudioVisualsDistributedMale;

        private String numberPmtctAudioVisualsDistributedFemale;

        private String numberPmtctAudioDistributedMale;

        private String numberPmtctAudioDistributedFemale;

        private String numberPmtctPrintMaterialsDistributedMale;

        private String numberPmtctPrintMaterialsDistributedFemale;

        public String getBaseEntityID() {
            return baseEntityID;
        }

        public void setBaseEntityID(String baseEntityID) {
            this.baseEntityID = baseEntityID;
        }

        public String getMobilizationDate() {
            return mobilizationDate;
        }

        public void setMobilizationDate(String mobilizationDate) {
            this.mobilizationDate = mobilizationDate;
        }

        public String getCommunitySbcActivityProvided() {
            return communitySbcActivityProvided;
        }

        public void setCommunitySbcActivityProvided(String communitySbcActivityProvided) {
            this.communitySbcActivityProvided = communitySbcActivityProvided;
        }

        public String getOtherInterventionsIecMaterialsDistributed() {
            return otherInterventionsIecMaterialsDistributed;
        }

        public void setOtherInterventionsIecMaterialsDistributed(String otherInterventionsIecMaterialsDistributed) {
            this.otherInterventionsIecMaterialsDistributed = otherInterventionsIecMaterialsDistributed;
        }

        public String getNumberAudioVisualsDistributed() {
            return numberAudioVisualsDistributed;
        }

        public void setNumberAudioVisualsDistributed(String numberAudioVisualsDistributed) {
            this.numberAudioVisualsDistributed = numberAudioVisualsDistributed;
        }

        public String getNumberAudioDistributed() {
            return numberAudioDistributed;
        }

        public void setNumberAudioDistributed(String numberAudioDistributed) {
            this.numberAudioDistributed = numberAudioDistributed;
        }

        public String getNumberPrintMaterialsDistributed() {
            return numberPrintMaterialsDistributed;
        }

        public void setNumberPrintMaterialsDistributed(String numberPrintMaterialsDistributed) {
            this.numberPrintMaterialsDistributed = numberPrintMaterialsDistributed;
        }

        public String getPmtctIecMaterialsDistributed() {
            return pmtctIecMaterialsDistributed;
        }

        public void setPmtctIecMaterialsDistributed(String pmtctIecMaterialsDistributed) {
            this.pmtctIecMaterialsDistributed = pmtctIecMaterialsDistributed;
        }

        public String getNumberPmtctAudioVisualsDistributedMale() {
            return numberPmtctAudioVisualsDistributedMale;
        }

        public void setNumberPmtctAudioVisualsDistributedMale(String numberPmtctAudioVisualsDistributedMale) {
            this.numberPmtctAudioVisualsDistributedMale = numberPmtctAudioVisualsDistributedMale;
        }

        public String getNumberPmtctAudioVisualsDistributedFemale() {
            return numberPmtctAudioVisualsDistributedFemale;
        }

        public void setNumberPmtctAudioVisualsDistributedFemale(String numberPmtctAudioVisualsDistributedFemale) {
            this.numberPmtctAudioVisualsDistributedFemale = numberPmtctAudioVisualsDistributedFemale;
        }

        public String getNumberPmtctAudioDistributedMale() {
            return numberPmtctAudioDistributedMale;
        }

        public void setNumberPmtctAudioDistributedMale(String numberPmtctAudioDistributedMale) {
            this.numberPmtctAudioDistributedMale = numberPmtctAudioDistributedMale;
        }

        public String getNumberPmtctAudioDistributedFemale() {
            return numberPmtctAudioDistributedFemale;
        }

        public void setNumberPmtctAudioDistributedFemale(String numberPmtctAudioDistributedFemale) {
            this.numberPmtctAudioDistributedFemale = numberPmtctAudioDistributedFemale;
        }

        public String getNumberPmtctPrintMaterialsDistributedMale() {
            return numberPmtctPrintMaterialsDistributedMale;
        }

        public void setNumberPmtctPrintMaterialsDistributedMale(String numberPmtctPrintMaterialsDistributedMale) {
            this.numberPmtctPrintMaterialsDistributedMale = numberPmtctPrintMaterialsDistributedMale;
        }

        public String getNumberPmtctPrintMaterialsDistributedFemale() {
            return numberPmtctPrintMaterialsDistributedFemale;
        }

        public void setNumberPmtctPrintMaterialsDistributedFemale(String numberPmtctPrintMaterialsDistributedFemale) {
            this.numberPmtctPrintMaterialsDistributedFemale = numberPmtctPrintMaterialsDistributedFemale;
        }
    }
}
