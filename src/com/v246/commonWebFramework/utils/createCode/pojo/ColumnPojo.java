package com.v246.commonWebFramework.utils.createCode.pojo;

import com.jfinal.kit.StrKit;

/**
 * Created by aquaqu on 2015/1/9.
 */
public class ColumnPojo {
    private String columnName;
    private String displayName;
    private String dictCode;
    private String regex;
    private boolean isRequired;
    private String formType;
    private String regexErrorMsg;
    private boolean isAllowQuery;

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

    public String getFormType() {
        return formType;
    }

    public void setFormType(String formType) {
        if(StrKit.notBlank(formType))
        this.formType = formType;
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
