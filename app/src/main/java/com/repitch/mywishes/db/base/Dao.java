package com.repitch.mywishes.db.base;


import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.misc.TransactionManager;
import com.repitch.mywishes.db.exception.DatabaseStateException;
import com.repitch.mywishes.db.helper.DbHelper;

import java.sql.SQLException;
import java.util.concurrent.Callable;

import timber.log.Timber;

/**
 * Created by repitch on 23.07.17.
 */
public class Dao<T, ID> extends RuntimeExceptionDao<T, ID> {

    private final Class<T> entityClass;
    private final DbHelper dbHelper;

    public Dao(DbHelper dbHelper, Class<T> entityClass) throws SQLException {
        super(dbHelper.getDao(entityClass));
        this.entityClass = entityClass;
        this.dbHelper = dbHelper;
    }

    public <T1> T1 callInTransaction(Callable<T1> operations) {
        try {
            return TransactionManager.callInTransaction(dbHelper.getConnectionSource(), operations);
        } catch (SQLException e) {
            String msg = "Error occurred while executing transaction!";
            Timber.e(e, msg);
            throw new RuntimeException(msg, e);
        } catch (IllegalStateException e) {
            throw new DatabaseStateException(e);
        }
    }
}

