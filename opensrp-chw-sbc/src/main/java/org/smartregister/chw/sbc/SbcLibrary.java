package org.smartregister.chw.sbc;

import org.smartregister.Context;
import org.smartregister.CoreLibrary;
import org.smartregister.chw.sbc.repository.VisitDetailsRepository;
import org.smartregister.chw.sbc.repository.VisitRepository;
import org.smartregister.repository.Repository;
import org.smartregister.sync.ClientProcessorForJava;
import org.smartregister.sync.helper.ECSyncHelper;

import id.zelory.compressor.Compressor;

public class SbcLibrary {
    private static SbcLibrary instance;
    private VisitRepository visitRepository;
    private VisitDetailsRepository visitDetailsRepository;
    private boolean submitOnSave = false;

    private final Context context;
    private final Repository repository;

    private final int applicationVersion;
    private final int databaseVersion;
    private ECSyncHelper syncHelper;

    private ClientProcessorForJava clientProcessorForJava;
    private Compressor compressor;

    public static void init(Context context, Repository repository, int applicationVersion, int databaseVersion) {
        if (instance == null) {
            instance = new SbcLibrary(context, repository, applicationVersion, databaseVersion);
        }
    }

    public static SbcLibrary getInstance() {
        if (instance == null) {
            throw new IllegalStateException(" Instance does not exist!!! Call "
                    + CoreLibrary.class.getName()
                    + ".init method in the onCreate method of "
                    + "your Application class ");
        }
        return instance;
    }

    private SbcLibrary(Context contextArg, Repository repositoryArg, int applicationVersion, int databaseVersion) {
        this.context = contextArg;
        this.repository = repositoryArg;
        this.applicationVersion = applicationVersion;
        this.databaseVersion = databaseVersion;
    }

    public Context context() {
        return context;
    }

    public Repository getRepository() {
        return repository;
    }

    public int getApplicationVersion() {
        return applicationVersion;
    }

    public int getDatabaseVersion() {
        return databaseVersion;
    }

    public ECSyncHelper getEcSyncHelper() {
        if (syncHelper == null) {
            syncHelper = ECSyncHelper.getInstance(context().applicationContext());
        }
        return syncHelper;
    }

    public ClientProcessorForJava getClientProcessorForJava() {
        if (clientProcessorForJava == null) {
            clientProcessorForJava = ClientProcessorForJava.getInstance(context().applicationContext());
        }
        return clientProcessorForJava;
    }

    public void setClientProcessorForJava(ClientProcessorForJava clientProcessorForJava) {
        this.clientProcessorForJava = clientProcessorForJava;
    }

    public VisitRepository visitRepository() {
        if (visitRepository == null) {
            visitRepository = new VisitRepository();
        }
        return visitRepository;
    }

    public VisitDetailsRepository visitDetailsRepository() {
        if (visitDetailsRepository == null) {
            visitDetailsRepository = new VisitDetailsRepository();
        }
        return visitDetailsRepository;
    }

    public String getSourceDateFormat() {
        return "dd-MM-yyyy";
    }

    public String getSaveDateFormat() {
        return "yyyy-MM-dd";
    }

    public boolean isSubmitOnSave() {
        return submitOnSave;
    }

    public void setSubmitOnSave(boolean submitOnSave) {
        this.submitOnSave = submitOnSave;
    }

}
