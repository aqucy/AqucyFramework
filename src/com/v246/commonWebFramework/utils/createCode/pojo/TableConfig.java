package com.v246.commonWebFramework.utils.createCode.pojo;

import com.v246.commonWebFramework.dao.DictionaryModel;

import java.util.List;
import java.util.Map;

/**
 * Created by aquaqu on 2015/1/9.
 */
public class TableConfig {
    private String tableName;
    private String primaryKey;
    private List<ColumnPojo> columnPojos = null;
    private List<ColumnPojo> allDisplayPojos = null;
    private List<ColumnPojo> allAddPojos = null;
    private List<ColumnPojo> allEditPojos = null;
    private List<ColumnPojo> allQueryPojos = null;
    private Map<String,List<DictionaryModel>> dicts = null;
    private int maxDisplayColumnLen = 0;
    private int maxQueryColumnLen = 0;
    private String tableAliasName;
    private long subViewObjectId;

    public long getSubViewObjectId() {
        return subViewObjectId;
    }

    public void setSubViewObjectId(long subViewObjectId) {
        this.subViewObjectId = subViewObjectId;
    }

    public List<ColumnPojo> getAllQueryPojos() {
        return allQueryPojos;
    }

    public void setAllQueryPojos(List<ColumnPojo> allQueryPojos) {
        this.allQueryPojos = allQueryPojos;
    }

    public List<ColumnPojo> getAllDisplayPojos() {
        return allDisplayPojos;
    }

    public void setAllDisplayPojos(List<ColumnPojo> allDisplayPojos) {
        this.allDisplayPojos = allDisplayPojos;
    }

    public List<ColumnPojo> getAllAddPojos() {
        return allAddPojos;
    }

    public void setAllAddPojos(List<ColumnPojo> allAddPojos) {
        this.allAddPojos = allAddPojos;
    }

    public List<ColumnPojo> getAllEditPojos() {
        return allEditPojos;
    }

    public void setAllEditPojos(List<ColumnPojo> allEditPojos) {
        this.allEditPojos = allEditPojos;
    }




    public String getTableAliasName() {
        return tableAliasName;
    }

    public void setTableAliasName(String tableAliasName) {
        this.tableAliasName = tableAliasName;
    }

    public int getMaxQueryColumnLen() {
        return maxQueryColumnLen;
    }

    public void setMaxQueryColumnLen(int maxQueryColumnLen) {
        this.maxQueryColumnLen = maxQueryColumnLen;
    }

    public int getMaxDisplayColumnLen() {
        return maxDisplayColumnLen;
    }

    public void setMaxDisplayColumnLen(int maxDisplayColumnLen) {
        this.maxDisplayColumnLen = maxDisplayColumnLen;
    }

    public Map<String, List<DictionaryModel>> getDicts() {
        return dicts;
    }

    public void setDicts(Map<String, List<DictionaryModel>> dicts) {
        this.dicts = dicts;
    }

    public List<ColumnPojo> getColumnPojos() {
        return columnPojos;
    }

    public void setColumnPojos(List<ColumnPojo> columnPojos) {
        this.columnPojos = columnPojos;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(String primaryKey) {
        this.primaryKey = primaryKey;
    }
}
