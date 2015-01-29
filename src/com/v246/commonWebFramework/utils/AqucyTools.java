package com.v246.commonWebFramework.utils;

import com.jfinal.core.Controller;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.v246.commonWebFramework.dao.PopGridSqlModel;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Alias;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.statement.select.*;

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
    private Map<String,String> getColumns(String sql){
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
            e.printStackTrace();
        }
        return columnMap;
    }
}
