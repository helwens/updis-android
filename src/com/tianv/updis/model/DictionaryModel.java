
package com.tianv.updis.model;

import com.uucun.android.data.annotation.Column;
import com.uucun.android.data.annotation.DBName;
import com.uucun.android.data.annotation.DBVersion;
import com.uucun.android.data.annotation.Table;
import com.uucun.android.data.sqlhelper.Model;

/**
 * @author Melvin
 * @version V1.0
 * @ClassName: DictionaryModel.java
 * @Description: TODO
 * @Date 2013-4-11 下午2:21:48
 */
@DBName(dbName = "updis.db")
@DBVersion(version = 1)
@Table(name = "dictionary")
public class DictionaryModel extends Model {

    @Column(name = "dictKey")
    public String dictKey;

    @Column(name = "dictValue")
    public String dictValue;

    public DictionaryModel() {
        super();
    }

}
