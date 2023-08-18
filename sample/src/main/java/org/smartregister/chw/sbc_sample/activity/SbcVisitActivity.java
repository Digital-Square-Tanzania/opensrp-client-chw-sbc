package org.smartregister.chw.sbc_sample.activity;

import org.smartregister.chw.sbc.activity.BaseSbcVisitActivity;
import org.smartregister.chw.sbc.domain.MemberObject;

public class SbcVisitActivity extends BaseSbcVisitActivity {
    @Override
    protected MemberObject getMemberObject(String baseEntityId) {
        return EntryActivity.getSampleMember();
    }
}
