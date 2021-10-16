package org.smartregister.presenter;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.smartregister.chw.barebones.contract.TestRegisterFragmentContract;
import org.smartregister.chw.barebones.presenter.BaseTestRegisterFragmentPresenter;
import org.smartregister.chw.barebones.util.Constants;
import org.smartregister.chw.barebones.util.DBConstants;
import org.smartregister.configurableviews.model.View;

import java.util.Set;
import java.util.TreeSet;

public class BaseTestRegisterFragmentPresenterTest {
    @Mock
    protected TestRegisterFragmentContract.View view;

    @Mock
    protected TestRegisterFragmentContract.Model model;

    private BaseTestRegisterFragmentPresenter baseTestRegisterFragmentPresenter;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        baseTestRegisterFragmentPresenter = new BaseTestRegisterFragmentPresenter(view, model, "");
    }

    @Test
    public void assertNotNull() {
        Assert.assertNotNull(baseTestRegisterFragmentPresenter);
    }

    @Test
    public void getMainCondition() {
        Assert.assertEquals("", baseTestRegisterFragmentPresenter.getMainCondition());
    }

    @Test
    public void getDueFilterCondition() {
        Assert.assertEquals(" (cast( julianday(STRFTIME('%Y-%m-%d', datetime('now'))) -  julianday(IFNULL(SUBSTR(malaria_test_date,7,4)|| '-' || SUBSTR(malaria_test_date,4,2) || '-' || SUBSTR(malaria_test_date,1,2),'')) as integer) between 7 and 14) ", baseTestRegisterFragmentPresenter.getDueFilterCondition());
    }

    @Test
    public void getDefaultSortQuery() {
        Assert.assertEquals(Constants.TABLES.MALARIA_CONFIRMATION + "." + DBConstants.KEY.LAST_INTERACTED_WITH + " DESC ", baseTestRegisterFragmentPresenter.getDefaultSortQuery());
    }

    @Test
    public void getMainTable() {
        Assert.assertEquals(Constants.TABLES.MALARIA_CONFIRMATION, baseTestRegisterFragmentPresenter.getMainTable());
    }

    @Test
    public void initializeQueries() {
        Set<View> visibleColumns = new TreeSet<>();
        baseTestRegisterFragmentPresenter.initializeQueries(null);
        Mockito.doNothing().when(view).initializeQueryParams("ec_malaria_confirmation", null, null);
        Mockito.verify(view).initializeQueryParams("ec_malaria_confirmation", null, null);
        Mockito.verify(view).initializeAdapter(visibleColumns);
        Mockito.verify(view).countExecute();
        Mockito.verify(view).filterandSortInInitializeQueries();
    }

}