package com.china.ds.appanalysis.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.china.ds.appanalysis.MyApplication;
import com.china.ds.appanalysis.data.AppUseRecord;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;


/**
 * Created by Administrator on 2016/8/5.
 */
public class DBHelper extends OrmLiteSqliteOpenHelper {

    private static byte[] lock = new byte[0];
    private static DBHelper dbHelper;
    private static final String DATABASE_NAME = "db_file_search";
    private static final int DATABASE_VERSION = 1;

    public static DBHelper getInstance()
    {
        synchronized (lock) {
            if(dbHelper == null){
                dbHelper = new DBHelper(MyApplication.getInstance());
            }
        }
        return dbHelper;
    }

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, AppUseRecord.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource, int i, int i1) {

    }

    public <T> Dao<T, Integer> createDao(Class<T> clazz) throws SQLException {
        return getDao(clazz);
    }
}
