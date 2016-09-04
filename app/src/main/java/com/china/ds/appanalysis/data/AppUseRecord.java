package com.china.ds.appanalysis.data;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p/>
 * TODO: Replace all uses of this class before publishing your app.
 */
@DatabaseTable(tableName = "tb_app_use_record")
public class AppUseRecord implements Serializable{

    @DatabaseField(generatedId = true)
    public int id;

    @DatabaseField(columnName = "packageName")
    public String packageName;

    @DatabaseField(columnName = "startTime")
    public long startTime;

    @DatabaseField(columnName = "endTime")
    public long endTime;

    public AppUseRecord() {
    }

}
