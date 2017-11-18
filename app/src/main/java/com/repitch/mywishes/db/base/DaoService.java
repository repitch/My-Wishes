package com.repitch.mywishes.db.base;

/**
 * Created by repitch on 23.07.17.
 */
public abstract class DaoService {
    private final DaoManager daoManager;

    public DaoService(DaoManager daoManager) {
        this.daoManager = daoManager;
    }

    protected DaoManager getDaoManager() {
        return daoManager;
    }

    protected <T> Dao<T, Long> getDao(Class<T> entityClass) {
        return getDaoManager().getDao(entityClass);
    }
}
