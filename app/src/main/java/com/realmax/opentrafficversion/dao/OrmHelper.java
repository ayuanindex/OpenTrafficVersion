package com.realmax.opentrafficversion.dao;

import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.realmax.opentrafficversion.App;
import com.realmax.opentrafficversion.bean.ViolateBean;

import java.sql.SQLException;

public class OrmHelper extends OrmLiteSqliteOpenHelper {

    private static OrmHelper ormHelper;

    public OrmHelper() {
        super(App.getContext(), "violatedatabase", null, 2);
    }

    public static OrmHelper getInstance() {
        if (ormHelper == null) {
            synchronized (OrmHelper.class) {
                if (ormHelper == null) {
                    ormHelper = new OrmHelper();
                }
            }
        }
        return ormHelper;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource) {
        try {
            TableUtils.createTableIfNotExists(connectionSource, ViolateBean.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource, int i, int i1) {
        onCreate(sqLiteDatabase, connectionSource);
    }
}
