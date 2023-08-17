package org.smartregister.chw.sbc.dao;

import net.sqlcipher.database.SQLiteDatabase;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.smartregister.chw.sbc.dao.SbcDao;
import org.smartregister.repository.Repository;

@RunWith(MockitoJUnitRunner.class)
public class SbcDaoTest extends SbcDao {

    @Mock
    private Repository repository;

    @Mock
    private SQLiteDatabase database;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        setRepository(repository);
    }

    @Test
    public void testGetMalariaTestDate() {
        Mockito.doReturn(database).when(repository).getReadableDatabase();
        SbcDao.getMalariaTestDate("123456");
        Mockito.verify(database).rawQuery(Mockito.anyString(), Mockito.any());
    }

    @Test
    public void testIsRegisteredForMalaria() {
        Mockito.doReturn(database).when(repository).getReadableDatabase();
        boolean registered = SbcDao.isRegisteredForMalaria("12345");
        Mockito.verify(database).rawQuery(Mockito.anyString(), Mockito.any());
        Assert.assertFalse(registered);
    }
}

