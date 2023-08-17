package org.smartregister.chw.sbc.presenter;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.smartregister.chw.sbc.contract.SbcRegisterFragmentContract;
import org.smartregister.chw.sbc.presenter.BaseSbcRegisterFragmentPresenter;
import org.smartregister.chw.sbc.util.Constants;
import org.smartregister.chw.sbc.util.DBConstants;
import org.smartregister.configurableviews.model.View;

import java.util.Set;
import java.util.TreeSet;

public class BaseSbcRegisterFragmentPresenterTest {
    @Mock
    protected SbcRegisterFragmentContract.View view;

    @Mock
    protected SbcRegisterFragmentContract.Model model;

    private BaseSbcRegisterFragmentPresenter baseSbcRegisterFragmentPresenter;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        baseSbcRegisterFragmentPresenter = new BaseSbcRegisterFragmentPresenter(view, model, "");
    }

    @Test
    public void assertNotNull() {
        Assert.assertNotNull(baseSbcRegisterFragmentPresenter);
    }

    @Test
    public void getMainCondition() {
        Assert.assertEquals("", baseSbcRegisterFragmentPresenter.getMainCondition());
    }

    @Test
    public void getDueFilterCondition() {
        Assert.assertEquals(" (cast( julianday(STRFTIME('%Y-%m-%d', datetime('now'))) -  julianday(IFNULL(SUBSTR(malaria_test_date,7,4)|| '-' || SUBSTR(malaria_test_date,4,2) || '-' || SUBSTR(malaria_test_date,1,2),'')) as integer) between 7 and 14) ", baseSbcRegisterFragmentPresenter.getDueFilterCondition());
    }

    @Test
    public void getDefaultSortQuery() {
        Assert.assertEquals(Constants.TABLES.SBC_REGISTER + "." + DBConstants.KEY.LAST_INTERACTED_WITH + " DESC ", baseSbcRegisterFragmentPresenter.getDefaultSortQuery());
    }

    @Test
    public void getMainTable() {
        Assert.assertEquals(Constants.TABLES.SBC_REGISTER, baseSbcRegisterFragmentPresenter.getMainTable());
    }

    @Test
    public void initializeQueries() {
        Set<View> visibleColumns = new TreeSet<>();
        baseSbcRegisterFragmentPresenter.initializeQueries(null);
        Mockito.doNothing().when(view).initializeQueryParams("ec_malaria_confirmation", null, null);
        Mockito.verify(view).initializeQueryParams("ec_malaria_confirmation", null, null);
        Mockito.verify(view).initializeAdapter(visibleColumns);
        Mockito.verify(view).countExecute();
        Mockito.verify(view).filterandSortInInitializeQueries();
    }

}