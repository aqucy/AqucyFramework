package com.v246.commonWebFramework.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jfinal.core.Controller;
import com.jfinal.kit.PathKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.v246.commonWebFramework.dao.DictionaryModel;
import com.v246.commonWebFramework.dao.PopGridSqlModel;
import com.v246.commonWebFramework.dao.ViewObjectColumnConfigModel;
import com.v246.commonWebFramework.dao.ViewObjectModel;
import com.v246.commonWebFramework.utils.createCode.pojo.ColumnPojo;
import com.v246.commonWebFramework.utils.createCode.pojo.TableConfig;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Alias;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.statement.select.*;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;

/**
 * Created by aquaqu on 2015/1/22.
 */
public class AqucyTools {
    final static CCJSqlParserManager parserManager = new CCJSqlParserManager();
//    public void doPopGrid(Controller c) {
//        String code = c.getPara("popGridCode");
//        String searchStr = c.getPara("searchStr");
//        int start = c.getParaToInt("page", 1);
//        int limit = c.getParaToInt("rows", 20);
//        if (StrKit.notBlank(searchStr)) {
//            searchStr = searchStr.replace("'", "");
//        }
//        PopGridSqlModel model = PopGridSqlModel.dao.findFirst("SELECT * FROM acyframework_popgridsql where code=?", code);
//        String sql = model.getStr("sqlStr");
//        StringBuffer sb = new StringBuffer(150);
//        sb.append("from (").append(sql).append(") _acy ");
//        Connection connection = null;
//        Statement stm = null;
//        ResultSet rs = null;
//        try {
//            connection = DbKit.getConfig().getConnection();
//            stm = connection.createStatement();
//            rs = stm.executeQuery("SELECT * "+sb.toString()+" where 1>2");
//            ResultSetMetaData mdd = rs.getMetaData();
//            List<Object> columnList = new ArrayList<Object>();
//            List<Object> dataList = new ArrayList<Object>();
//            Map<Object,Object> reMap = new HashMap<Object, Object>();
//            Map<Object,Object> jo = new HashMap<Object, Object>();
//            for(int i=1;i<=mdd.getColumnCount();i++) {
//                String columnName = mdd.getColumnName(i);
//                String label = mdd.getColumnLabel(i);
//                if (StrKit.notBlank(searchStr)) {
//                    if(i==1) {
//                        sb.append("where ");
//                    }else{
//                        sb.append(" or ");
//                    }
//                    sb.append(columnName).append(" like '%").append(searchStr).append("%' ");
//                }
//                Map<Object, Object> m = new HashMap<Object, Object>();
//                m.put("field",columnName);
//                m.put("title", label);
//                m.put("width", 120);
//                if("id".equalsIgnoreCase(columnName)) {
//                    m.put("hidden", true);
//                }
//                columnList.add(m);
//            }
//            String sort = c.getPara("sort");
//            if(StrKit.notBlank(sort)){
//                sb.append(" order by ").append(sort).append(" ").append(c.getPara("order")).append(" ");
//            }
//            Page<Record> page = Db.paginate((start/limit)+1,limit,"SELECT *",sb.toString());
//            jo.put("total", page.getTotalRow());
//            List<Record> list = page.getList();
////            rs.close();
////            stm.close();
////            stm = connection.createStatement();
////            rs = stm.executeQuery(sb.toString());
////            while(rs.next()){
////                Map<Object, Object> m = new HashMap<Object, Object>();
////                for(int i=1;i<=mdd.getColumnCount();i++) {
////                    String columnName = mdd.getColumnName(i);
////                    m.put(columnName,rs.getObject(i));
////                }
////                dataList.add(m);
////            }
//            jo.put("rows",list);
//            reMap.put("columns",columnList);
//            reMap.put("datas", jo);
//            reMap.put("rows", "");
//            c.renderJson(reMap);
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            try {
//                if(rs!=null){
//                    rs.close();;
//                }
//                if(stm!=null){
//                    stm.close();
//                }
//                if (connection != null) {
////                    connection.close();
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }
    public void doPopGrid(Controller c) {
        String code = c.getPara("popGridCode");
        String searchStr = c.getPara("searchStr");
        int start = c.getParaToInt("page", 1);
        int limit = c.getParaToInt("rows", 20);
        if (StrKit.notBlank(searchStr)) {
            searchStr = searchStr.replace("'", "");
        }
        PopGridSqlModel model = PopGridSqlModel.dao.findFirst("SELECT * FROM acyframework_popgridsql where code=?", code);
        String sql = model.getStr("sqlStr");
        String findSql = model.getStr("findSqlstr");
       //分析SQL

        StringBuffer sb = new StringBuffer(150);
        sb.append("from (").append(sql).append(") _acy ");
        Connection connection = null;
        Statement stm = null;
        ResultSet rs = null;
        try {
            List<Object> columnList = new ArrayList<Object>();
            List<Object> dataList = new ArrayList<Object>();
            Map<Object,Object> reMap = new HashMap<Object, Object>();
            Map<Object,Object> jo = new HashMap<Object, Object>();
            Map<String,String> cm = getColumns(sql);
            Iterator<String> it = cm.keySet().iterator();
            int i = 1;
            while(it.hasNext()) {
                String columnName = it.next();
                String label = cm.get(columnName);
                if (StrKit.notBlank(searchStr)) {
                    if(i==1) {
                        sb.append("where ");
                    }else{
                        sb.append(" or ");
                    }
                    sb.append(label).append(" like '%").append(searchStr).append("%' ");
                }
                Map<Object, Object> m = new HashMap<Object, Object>();
                m.put("field",columnName);
                m.put("title", label);
                m.put("width", 120);
                if("id".equalsIgnoreCase(columnName)) {
                    m.put("hidden", true);
                }
                columnList.add(m);
                i++;
            }
            String sort = c.getPara("sort");
            if(StrKit.notBlank(sort)){
                sb.append(" order by ").append(sort).append(" ").append(c.getPara("order")).append(" ");
            }
            System.out.println(sb.toString());
            Page<Record> page = Db.paginate((start/limit)+1,limit,"SELECT *",sb.toString());
            jo.put("total", page.getTotalRow());
            List<Record> list = page.getList();
            for(Record rc:list){
                it = cm.keySet().iterator();
                while(it.hasNext()){
                    String columnName = it.next();
                    String label = cm.get(columnName);
                    rc.set(columnName,rc.get(label));
                    rc.remove(label);
                }
            }
            jo.put("rows",list);
            reMap.put("columns",columnList);
            reMap.put("datas", jo);
            reMap.put("rows", "");
            c.renderJson(reMap);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if(rs!=null){
                    rs.close();;
                }
                if(stm!=null){
                    stm.close();
                }
                if (connection != null) {
//                    connection.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    public Map<String,String> getColumns(String sql) throws Exception{
        Map<String, String> columnMap = new HashMap<String, String>();
        try {
            Select select = (Select) parserManager.parse(new StringReader(sql));
            PlainSelect plainSelect = (PlainSelect) select.getSelectBody();
            List<SelectItem> selectItems = plainSelect.getSelectItems();
            for(int i=0;i<selectItems.size();i++){
                Object o = selectItems.get(i);
                if(o instanceof SelectExpressionItem){
                    SelectExpressionItem si = (SelectExpressionItem)o ;
                    Expression expression = si.getExpression();
                    Alias  alias = si.getAlias();
                    String columnName = expression.toString();
                    String aliasName = alias==null?expression.toString():alias.getName();
                    if(columnName.indexOf(".")!=-1){
                        columnName = columnName.split(".")[1];
                    }
                    columnMap.put(columnName,aliasName);
                }else if(o instanceof AllTableColumns){
                    AllTableColumns atc = (AllTableColumns)o;
                }else if(o instanceof AllColumns){
                    AllColumns ac = (AllColumns)o;
                }
            }
        } catch (JSQLParserException e) {
            throw (e);
        }
        return columnMap;
    }
    public Map<String,Object> resolveToRootMap(String jsonString) throws Exception {
        Map root = new HashMap();
        Map data = new HashMap();
        StringBuffer columnString = new StringBuffer(100);
        StringBuffer displayColumnString = new StringBuffer(100);
        ObjectMapper om = new ObjectMapper();
        JsonNode jo = om.readTree(jsonString.trim());
        List<ColumnPojo> list = new ArrayList<ColumnPojo>();
        List<ColumnPojo> allowDisplayList = new ArrayList<ColumnPojo>();
        List<ColumnPojo> allowAddList = new ArrayList<ColumnPojo>();
        List<ColumnPojo> allowEditList = new ArrayList<ColumnPojo>();
        List<ColumnPojo> allowQueryList = new ArrayList<ColumnPojo>();
        TableConfig tableConfig = new TableConfig();
        Map<String, List<DictionaryModel>> dicts = new HashMap<String, List<DictionaryModel>>();
        boolean reCreate = false;
        if(jo.has("title")){
            tableConfig.setTitle(jo.path("title").asText());
        }else{
            root.put("errmsg","菜单名称不能为空");
            return root;
        }
        if (jo.has("tableName")) {
            tableConfig.setTableName(jo.path("tableName").asText());
        }else{
            root.put("errmsg","表名不能为空");
            return root;
        }
        if (jo.has("alias")) {
            tableConfig.setTableAliasName(jo.path("alias").asText());
        }else{
            root.put("errmsg","别名不能为空");
            return root;
        }
        if (jo.has("id")) {
            long id = Long.parseLong(jo.path("id").asText());
            if(id>0){
                tableConfig.setId(id);
            }
        }
        if (jo.has("reCreate")&&"yes".equalsIgnoreCase(jo.path("reCreate").asText())) {
            reCreate = true;
        }
        if (jo.has("subVoId")) {
            long subVoId = jo.path("subVoId").asLong();
            tableConfig.setSubViewObjectId(subVoId);
        }
        if (jo.has("mainColumnName")) {
            String mainColumnName = jo.path("mainColumnName").asText();
            tableConfig.setSelfColumnForSubViewLink(mainColumnName);
        }
        if (jo.has("subColumnName")) {
            String subColumnName = jo.path("subColumnName").asText();
            tableConfig.setSubViewLinkColumn(subColumnName);
        }
        if (jo.has("importColumn")) {
            String importColumn = jo.path("importColumn").asText();
            tableConfig.setSelfColumnsToSubViewUpdate(importColumn);
        }
        if (jo.has("subAdd")&&StrKit.notBlank(jo.path("subAdd").asText())) {
            tableConfig.setSubAdd(true);
        }
        if (jo.has("subDelete")&&StrKit.notBlank(jo.path("subDelete").asText())) {
            tableConfig.setSubDelete(true);
        }
        if (jo.has("subEdit")&&StrKit.notBlank(jo.path("subEdit").asText())) {
            tableConfig.setSubEdit(true);
        }
        if (jo.has("add")&&StrKit.notBlank(jo.path("add").asText())) {
            tableConfig.setAdd(true);
        }
        if (jo.has("delete")&&StrKit.notBlank(jo.path("delete").asText())) {
            tableConfig.setDelete(true);
        }
        if (jo.has("edit")&&StrKit.notBlank(jo.path("edit").asText())) {
            tableConfig.setEdit(true);
        }
        if(tableConfig.getId()<=0){
            File file = new File(PathKit.getWebRootPath() + "/WEB-INF/view/project/" + tableConfig.getTableAliasName() + ".html");
            if (file.exists()) {
                root.put("errmsg","别名重复");
                return root;
            }
        }else{
            ///////////////////////////////////////////////////////////////////
            /////////如果重新指定了别名,需要将原别名已生成的相关数据删除//////////////
            ///////////////////////////////////////////////////////////////////
        }
        if (jo.has("datas")) {
            int maxDisplayLen = 0;
            int maxQueryLen = 0;
            JsonNode arr = jo.path("datas");
            for (int i = 0; i < arr.size(); i++) {
                ColumnPojo columnPojo = new ColumnPojo();
                JsonNode myjo = arr.path(i);
                if (myjo.has("displayName")) {
                    String displayName = myjo.path("displayName").asText();
                    columnPojo.setDisplayName(displayName);
                    if(StrKit.notBlank(displayName)){
                        if(displayName.length()>maxDisplayLen){
                            maxDisplayLen = displayName.length();
                        }
                    }
                }
                if (myjo.has("columnName")) {
                    columnPojo.setColumnName(myjo.path("columnName").asText());
                    columnString.append("'").append(columnPojo.getColumnName()).append("',");
                }
                if (myjo.has("id")) {
                    long id = Long.parseLong(myjo.path("id").asText());
                    if(id>0){
                        columnPojo.setId(id);
                    }
                }
                if (myjo.has("minLength")) {
                    String minLen = myjo.path("minLength").asText();
                    if(StrKit.notBlank(minLen)){
                        columnPojo.setMinLength(Integer.parseInt(minLen));
                    }
                }
                if (myjo.has("maxLength")) {
                    String maxLength = myjo.path("maxLength").asText();
                    if(StrKit.notBlank(maxLength)){
                        columnPojo.setMaxLength(Integer.parseInt(maxLength));
                    }
                }
                if (myjo.has("dictCode")) {
                    String dictCode = myjo.path("dictCode").asText();
                    if (StrKit.notBlank(dictCode)) {
                        dicts.put(dictCode, DictionaryModel.dao.find("SELECT k label,v value FROM acyFramework_dictionary where code=?", dictCode));
                        data.put(dictCode, columnPojo.getColumnName());
                        columnPojo.setDictCode(dictCode);
                    }
                }
                if (myjo.has("reg")&&StrKit.notBlank(myjo.path("reg").asText())) {
                    columnPojo.setRegex(myjo.path("reg").asText());
                }
                if (myjo.has("required")) {
                    columnPojo.setRequired(myjo.path("required").asText().equalsIgnoreCase("是"));
                }
                if (myjo.has("allowDisplay")) {
                    columnPojo.setAllowDisplay(myjo.path("allowDisplay").asText().equalsIgnoreCase("是"));
                    if(columnPojo.isAllowDisplay()&&StrKit.isBlank(columnPojo.getDisplayName())){
                        root.put("errmsg","列:"+columnPojo.getColumnName()+" 您设置为可显示,但显示内容为空!");
                        return root;
                    }
                }
                if (myjo.has("allowAdd")) {
                    columnPojo.setAllowAdd(myjo.path("allowAdd").asText().equalsIgnoreCase("是"));
                }
                if (myjo.has("allowEdit")) {
                    columnPojo.setAllowEdit(myjo.path("allowEdit").asText().equalsIgnoreCase("是"));
                }
                if (myjo.has("inputType")&&StrKit.notBlank(myjo.path("inputType").asText())) {
                    String inputType = myjo.path("inputType").asText();
                    columnPojo.setInputType(inputType);
                    if("selectInput".equalsIgnoreCase(inputType)){
                        tableConfig.setHasSelectInput(true);
                        String selectInputConfig = myjo.path("selectInputConfig").asText();
                        String inputSql = myjo.path("inputSql").asText();
                        String sqlCode  = tableConfig.getTableAliasName()+"_"+columnPojo.getColumnName();
                        Map<Object, Object> mp = new HashMap<Object, Object>();
                        columnPojo.setSelectInputConfigStr(selectInputConfig);
                        columnPojo.setInputSql(inputSql);
                        String[] myarr = selectInputConfig.split(",");
                        for(int j=0;j<myarr.length;j++){
                            String[] myarrtmp = myarr[j].split(":");
                            mp.put(myarrtmp[0],myarrtmp[1]);
                        }
                        columnPojo.setSelectInputConfig(mp);
                        PopGridSqlModel ml = PopGridSqlModel.dao.findFirst("SELECT * FROM acyframework_popgridsql where code=?", sqlCode);
                        if(ml!=null){
                            ml.set("sqlStr",inputSql);
                            ml.update();
                        }else{
                            ml = new PopGridSqlModel();
                            ml.set("sqlStr",inputSql);
                            ml.set("code",sqlCode);
                            ml.save();
                        }
                    }
                }
                if (myjo.has("validateType")&&StrKit.notBlank(myjo.path("validateType").asText())) {
                    columnPojo.setValidateType(myjo.path("validateType").asText());
                }
                if (myjo.has("regErrMsg")) {
                    columnPojo.setRegexErrorMsg(myjo.path("regErrMsg").asText());
                }
                if (myjo.has("allowQuery")) {
                    boolean allowQuery = "是".equalsIgnoreCase(myjo.path("allowQuery").asText());
//                        if(StrKit.isBlank(columnPojo.getDisplayName())){
//                            allowQuery = false;
//                        }
                    if(allowQuery&&columnPojo.getDisplayName().length()>maxQueryLen){
                        maxQueryLen = columnPojo.getDisplayName().length();
                    }
                    columnPojo.setAllowQuery(allowQuery);
                }
                list.add(columnPojo);
                if(columnPojo.isAllowAdd()){
                    allowAddList.add(columnPojo);
                }
                if(columnPojo.isAllowDisplay()){
                    allowDisplayList.add(columnPojo);
                }
                if(columnPojo.isAllowEdit()){
                    allowEditList.add(columnPojo);
                }
                if(columnPojo.isAllowQuery()){
                    allowQueryList.add(columnPojo);
                }
            }
            tableConfig.setColumnPojos(list);
            tableConfig.setAllAddPojos(allowAddList);
            tableConfig.setAllDisplayPojos(allowDisplayList);
            tableConfig.setAllEditPojos(allowEditList);
            tableConfig.setAllQueryPojos(allowQueryList);
            tableConfig.setDicts(dicts);
            tableConfig.setMaxDisplayColumnLen(++maxDisplayLen);
            tableConfig.setMaxQueryColumnLen(++maxQueryLen);
        }

        columnString.deleteCharAt(columnString.length()-1);
        data.put("columnStr", columnString);
        root.put("acy", data);
        root.put("tableConfig",tableConfig);
        return root;
    }
    public void saveConfigToDb(TableConfig tableConfig) throws Exception{
        try{
            ViewObjectModel vo = null;
            if(tableConfig.getId()>0){
                vo = ViewObjectModel.dao.findById(tableConfig.getId());
            }else{
                vo = new ViewObjectModel();
            }
            vo.set("title",tableConfig.getTitle());
            vo.set("tableAliasName", tableConfig.getTableAliasName());
            vo.set("tableName", tableConfig.getTableName());
            vo.set("allowSubViewObjectAdd",tableConfig.isSubAdd());
            vo.set("allowSubViewObjectDelete",tableConfig.isSubDelete());
            vo.set("allowSubViewObjectEdit",tableConfig.isSubEdit());
            vo.set("allowAdd",tableConfig.isAdd());
            vo.set("allowEdit",tableConfig.isEdit());
            vo.set("allowDelete",tableConfig.isDelete());
            vo.set("subViewObjectId",tableConfig.getSubViewObjectId());
            vo.set("subViewLinkColumn",tableConfig.getSubViewLinkColumn());
            vo.set("selfColumnForSubViewLink",tableConfig.getSelfColumnForSubViewLink());
            vo.set("selfColumnsToSubViewUpdate",tableConfig.getSelfColumnsToSubViewUpdate());
//            vo.set("status", status);
            if(tableConfig.getId()>0){
                vo.update();
            }else{
                vo.save();
            }
            List<ColumnPojo> list = tableConfig.getColumnPojos();
            for(ColumnPojo pojo:list){
                ViewObjectColumnConfigModel vocc;
                if(pojo.getId()>0){
                    vocc = ViewObjectColumnConfigModel.dao.findById(pojo.getId());
                }else{
                    vocc = new ViewObjectColumnConfigModel();
                }
                vocc.set("columnName",pojo.getColumnName());
                vocc.set("displayName",pojo.getDisplayName());
                vocc.set("allowAdd",pojo.isAllowAdd());
                vocc.set("allowEdit",pojo.isAllowEdit());
                vocc.set("allowDisplay",pojo.isAllowDisplay());
                vocc.set("allowQuery",pojo.isAllowQuery());
                vocc.set("viewObjectId",vo.getLong("id"));
                vocc.set("dictCode",pojo.getDictCode());
                vocc.set("validateType",pojo.getValidateType());
                vocc.set("inputType",pojo.getInputType());
                vocc.set("required",pojo.isRequired());
                vocc.set("regex",pojo.getRegex());
                vocc.set("regexErrorMsg",pojo.getRegexErrorMsg());
                vocc.set("selectInputConfig",pojo.getSelectInputConfigStr());
                vocc.set("inputSql",pojo.getInputSql());
                vocc.set("maxLength",pojo.getMaxLength());
                vocc.set("minLength",pojo.getMinLength());
                if(pojo.getId()>0){
                    vocc.update();
                }else{
                    vocc.save();
                }
            }
        }catch (Exception e){
            throw e;
        }
    }
}
