package org.smartregister.chw.sbc_sample.interactor;

import org.smartregister.chw.sbc.domain.MemberObject;
import org.smartregister.chw.sbc.interactor.BaseSbcVisitInteractor;
import org.smartregister.chw.sbc_sample.activity.EntryActivity;

public class SbcVisitInteractor extends BaseSbcVisitInteractor {
    @Override
    public MemberObject getMemberClient(String memberID) {
        return EntryActivity.getSampleMember();
    }
}
