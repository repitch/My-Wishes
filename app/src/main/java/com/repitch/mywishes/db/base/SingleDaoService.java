package com.repitch.mywishes.db.base;


import com.repitch.mywishes.db.helper.DbHelper;

/**
 * Created by repitch on 23.07.17.
 */
public abstract class SingleDaoService<T, ID> extends DaoService {
    private final Class<T> entityClass;

    public SingleDaoService(Class<T> entityClass, DaoManager daoManager) {
        super(daoManager);
        this.entityClass = entityClass;
    }

    public SingleDaoService(Class<T> entityClass) {
        this(entityClass, new DaoManager(DbHelper.getInstance()));
    }

    protected Dao<T, ID> getDao() {
        return getDaoManager().getDao(entityClass);
    }
}
