package com.v246.project.model;

import com.jfinal.plugin.activerecord.Model;

/**
 * Created by aquaqu on 2014/11/8.
 */
public class ${tableConfig.tableName?cap_first}Model extends Model<${tableConfig.tableName?cap_first}Model>{
    public static final ${tableConfig.tableName?cap_first}Model dao = new ${tableConfig.tableName?cap_first}Model();
}
