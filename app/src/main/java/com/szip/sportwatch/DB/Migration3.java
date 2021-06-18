package com.szip.sportwatch.DB;

import com.raizlabs.android.dbflow.annotation.Migration;
import com.raizlabs.android.dbflow.sql.SQLiteType;
import com.raizlabs.android.dbflow.sql.migration.AlterTableMigration;
import com.szip.sportwatch.DB.dbModel.AnimalHeatData;
import com.szip.sportwatch.DB.dbModel.BloodOxygenData;

import static com.szip.sportwatch.DB.AppDatabase.VERSION;

@Migration(version = VERSION, database = AppDatabase.class)//=2的升级
public class Migration3 extends AlterTableMigration<BloodOxygenData> {
    public Migration3(Class<BloodOxygenData> table) {
        super(table);
    }

    @Override
    public void onPreMigrate() {
        addColumn(SQLiteType.get(String.class.getName()), "deviceCode");//基本数据类型
    }
}
