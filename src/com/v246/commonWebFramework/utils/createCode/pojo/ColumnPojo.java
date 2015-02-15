package com.v246.commonWebFramework.utils.createCode.pojo;

import com.jfinal.kit.StrKit;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by aquaqu on 2015/1/9.
 */
public class ColumnPojo {
    private String columnName;
    private String displayName;
    private String dictCode;
    private String regex;
    private boolean isRequired;
    private String inputType;
    private String regexErrorMsg;
    private boolean isAllowQuery;
    private boolean isAllowAdd;
    private boolean isAllowEdit;
    private boolean isAllowDisplay;
    private String validateType;
    private int minLength;
    private int maxLength;
    private long id;
    private String selectInputConfigStr;
    private String inputSql;
    private Map<Object, Object> selectInputConfig;

    public Map<Object, Object> getSelectInputConfig() {
        return selectInputConfig;
    }

    public void setSelectInputConfig(Map<Object, Object> selectInputConfig) {
        this.selectInputConfig = selectInputConfig;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getSelectInputConfigStr() {
        return selectInputConfigStr;
    }

    public void setSelectInputConfigStr(String selectInputConfigStr) {
        this.selectInputConfigStr = selectInputConfigStr;
    }

    public String getInputSql() {
        return inputSql;
    }

    public void setInputSql(String inputSql) {
        this.inputSql = inputSql;
    }

    public int getMinLength() {
        return minLength;
    }

    public void setMinLength(int minLength) {
        this.minLength = minLength;
    }

    public int getMaxLength() {
        return maxLength;
    }

    public void setMaxLength(int maxLength) {
        this.maxLength = maxLength;
    }

    public boolean isAllowDisplay() {
        return isAllowDisplay;
    }

    public void setAllowDisplay(boolean isAllowDisplay) {
        this.isAllowDisplay = isAllowDisplay;
    }

    public String getValidateType() {
        return validateType;
    }

    public void setValidateType(String validateType) {
        this.validateType = validateType;
    }
    public String getInputType() {
        return inputType;
    }

    public void setInputType(String inputType) {
        this.inputType = inputType;
    }

    public boolean isAllowAdd() {
        return isAllowAdd;
    }

    public void setAllowAdd(boolean isAllowAdd) {
        this.isAllowAdd = isAllowAdd;
    }

    public boolean isAllowEdit() {
        return isAllowEdit;
    }

    public void setAllowEdit(boolean isAllowEdit) {
        this.isAllowEdit = isAllowEdit;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        if(StrKit.notBlank(columnName))
        this.columnName = columnName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        if(StrKit.notBlank(displayName))
        this.displayName = displayName;
    }

    public String getDictCode() {
        return dictCode;
    }

    public void setDictCode(String dictCode) {
        if(StrKit.notBlank(dictCode))
        this.dictCode = dictCode;
    }

    public String getRegex() {
        return regex;
    }

    public void setRegex(String regex) {
        if(StrKit.notBlank(regex))
        this.regex = regex;
    }

    public boolean isRequired() {
        return isRequired;
    }

    public void setRequired(boolean isRequired) {

        this.isRequired = isRequired;
    }



    public String getRegexErrorMsg() {
        return regexErrorMsg;
    }

    public void setRegexErrorMsg(String regexErrorMsg) {
        if(StrKit.notBlank(regexErrorMsg))
        this.regexErrorMsg = regexErrorMsg;
    }

    public boolean isAllowQuery() {
        return isAllowQuery;
    }

    public void setAllowQuery(boolean isAllowQuery) {
        this.isAllowQuery = isAllowQuery;
    }


}
