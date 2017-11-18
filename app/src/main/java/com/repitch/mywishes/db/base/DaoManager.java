package com.repitch.mywishes.db.base;


import com.repitch.mywishes.db.helper.DbHelper;

import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import timber.log.Timber;

/**
 * Created by repitch on 23.07.17.
 */
public class DaoManager {
    private final Map<Class, Dao> daoCache = new ConcurrentHashMap<>();
    private final DbHelper dbHelper;

    public DaoManager(DbHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    public <T, ID> Dao<T, ID> getDao(Class<T> entityClass) {
        if (!daoCache.containsKey(entityClass)) {
            try {
                daoCache.put(entityClass, new Dao<>(dbHelper, entityClass));
            } catch (SQLException e) {
                String message = "Couldn't instantiate DAO class for " + entityClass;
                Timber.e(e, message);
                throw new RuntimeException(message, e);
            }
        }

        //noinspection unchecked
        return daoCache.get(entityClass);
    }

    public void clearCache() {
        daoCache.clear();
    }
}
