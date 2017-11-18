package com.repitch.mywishes.db.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.repitch.mywishes.db.entity.Wish;

import java.sql.SQLException;

/**
 * Created by repitch on 23.07.17.
 */
public class DbHelper extends OrmLiteSqliteOpenHelper {

    private static final String DB_NAME = "mywishes.db";
    private static final int DB_VERSION = 1;

    private static DbHelper instance;

    private Class[] models = {
            Wish.class
    };

    public DbHelper(@NonNull Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    public static synchronized DbHelper getInstance() {
        if (instance == null) {
            throw new IllegalStateException("You need to init(Context) DbHelper in App class");
        }
        return instance;
    }

    public static synchronized void releaseHelper() {
        OpenHelperManager.releaseHelper();
        instance = null;
    }

    public static synchronized void init(@NonNull Context context) {
        instance = OpenHelperManager.getHelper(context, DbHelper.class);
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {
            for (Class c : models) {
                TableUtils.createTableIfNotExists(connectionSource, c);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            for (Class c : models) {
                TableUtils.dropTable(connectionSource, c, true);
            }
            onCreate(database, connectionSource);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
