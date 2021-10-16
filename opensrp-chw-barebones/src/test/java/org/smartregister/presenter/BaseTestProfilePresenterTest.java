package org.smartregister.presenter;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.smartregister.chw.barebones.contract.TestProfileContract;
import org.smartregister.chw.barebones.domain.MemberObject;
import org.smartregister.chw.barebones.presenter.BaseTestProfilePresenter;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

public class BaseTestProfilePresenterTest {

    @Mock
    private TestProfileContract.View view = Mockito.mock(TestProfileContract.View.class);

    @Mock
    private TestProfileContract.Interactor interactor = Mockito.mock(TestProfileContract.Interactor.class);

    @Mock
    private MemberObject memberObject = new MemberObject();

    private BaseTestProfilePresenter profilePresenter = new BaseTestProfilePresenter(view, interactor, memberObject);


    @Test
    public void fillProfileDataCallsSetProfileViewWithDataWhenPassedMemberObject() {
        profilePresenter.fillProfileData(memberObject);
        verify(view).setProfileViewWithData();
    }

    @Test
    public void fillProfileDataDoesntCallsSetProfileViewWithDataIfMemberObjectEmpty() {
        profilePresenter.fillProfileData(null);
        verify(view, never()).setProfileViewWithData();
    }

    @Test
    public void malariaTestDatePeriodIsLessThanSeven() {
        profilePresenter.recordMalariaButton("");
        verify(view).hideView();
    }

    @Test
    public void malariaTestDatePeriodGreaterThanTen() {
        profilePresenter.recordMalariaButton("OVERDUE");
        verify(view).setOverDueColor();
    }

    @Test
    public void malariaTestDatePeriodIsMoreThanFourteen() {
        profilePresenter.recordMalariaButton("EXPIRED");
        verify(view).hideView();
    }

    @Test
    public void refreshProfileBottom() {
        profilePresenter.refreshProfileBottom();
        verify(interactor).refreshProfileInfo(memberObject, profilePresenter.getView());
    }

    @Test
    public void saveForm() {
        profilePresenter.saveForm(null);
        verify(interactor).saveRegistration(null, view);
    }
}
