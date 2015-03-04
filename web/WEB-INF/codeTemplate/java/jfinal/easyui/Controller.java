package com.v246.project.controller;

import com.jfinal.core.Controller;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.DbKit;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.v246.commonWebFramework.utils.AqucyTools;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import com.v246.project.model.${tableConfig.tableName?cap_first}Model;
        <#if tableConfig.subViewObjectId gt 0>
import com.v246.project.model.${tableConfig.subTableConfig.tableName?cap_first}Model;
</#if>

/**
 * Created by aquaqu on 2014/12/21.
 */
public class ${tableConfig.tableAliasName?cap_first}Controller extends Controller {
    public  void index()  throws Exception{
        ${tableConfig.tableName?cap_first}Model model = getModel(${tableConfig.tableName?cap_first}Model.class,"${tableConfig.tableAliasName}");
        String op = getPara("op");
        Map jo = new ConcurrentHashMap();
        jo.put("success", true);
        if ("add".equalsIgnoreCase(op)) {
        try {
            if(AqucyTools.isPermission("${tableConfig.tableAliasName}_add")){
                model.save();
                jo.put("errcode", 0);
            }else{
                jo.put("errcode",1);
                jo.put("errmsg","对不起,权限不足,无法执行该操作");
            }

        } catch (Exception e) {
            DbKit.getConfig().getConnection().rollback();
            jo.put("errcode", 1104);
            jo.put("errmsg", "系统后台异常,请联系管理员");
            e.printStackTrace();
        }
            renderJson(jo);
        } else if ("edit".equalsIgnoreCase(op)) {
            try {
                if(AqucyTools.isPermission("${tableConfig.tableAliasName}_edit")){
                    ${tableConfig.tableName?cap_first}Model um = ${tableConfig.tableName?cap_first}Model.dao.findById(model.getLong("${tableConfig.primaryKey}"));
                    if(um==null){
                        jo.put("errcode", 1112);
                        jo.put("errmsg", "查无此数据,无法编辑");
                    }else {
                        model.update();
                        jo.put("errcode", 0);
                    }
                }else{
                    jo.put("errcode",1);
                    jo.put("errmsg","对不起,权限不足,无法执行该操作");
                }
            } catch (Exception e) {
                jo.put("errcode", 1104);
                jo.put("errmsg", "系统后台异常,请联系管理员");
                e.printStackTrace();
            }
            renderJson(jo);
        } else if ("view".equalsIgnoreCase(op)) {
            if(AqucyTools.isPermission("${tableConfig.tableAliasName}_view")==false){
                jo.put("errcode",1);
                jo.put("errmsg","对不起,权限不足,无法执行该操作");
                renderJson(jo);
                return;
            }
            int start = getParaToInt("page",1);
            int limit = getParaToInt("rows",10);
            start = (start-1)*limit;
            StringBuffer sb = new StringBuffer();
            sb.append("FROM ${tableConfig.tableName}");
            model.removeNullValueAttrs();
            String[] keys = model.getAttrNames();
            Page<${tableConfig.tableName?cap_first}Model>  page = null;
            if(keys.length>0){
                sb.append(" where 1=1");
                for (String key : keys) {
                    sb.append(" and ").append(key);
                    if(model.get(key) instanceof  String){
                        sb.append(" like ?");
                        String v = model.get(key);
                        v =v .replace("'","");
                        v = "%"+v+"%";
                        model.put(key,v);
                    }else{
                        sb.append(" = ?");
                    }
                }
                String sort = getPara("sort");
                if(StrKit.notBlank(sort)){
                    sb.append(" order by ").append(sort).append(" ").append(getPara("order"));
                }
                page = ${tableConfig.tableName?cap_first}Model.dao.paginate((start/limit)+1,limit,"SELECT *",sb.toString(),model.getAttrValues());

            }else{
                String sort = getPara("sort");
                if(StrKit.notBlank(sort)){
                    sb.append(" order by ").append(sort).append(" ").append(getPara("order"));
                }
                page = ${tableConfig.tableName?cap_first}Model.dao.paginate((start/limit)+1,limit,"SELECT *",sb.toString());
            }
            jo.put("total", page.getTotalRow());
            jo.put("errcode", 0);
            jo.put("rows", page.getList());
            renderJson(jo);
        }else if ("delete".equalsIgnoreCase(op)) {
            if(AqucyTools.isPermission("${tableConfig.tableAliasName}_delete")){
                String ids = getPara("ids");
                if(StrKit.notBlank(ids)){
                    Db.update("delete from ${tableConfig.tableName} where ${tableConfig.primaryKey} in(" + ids + ")");
                }
                jo.put("errcode", 0);
            }else{
                jo.put("errcode",1);
                jo.put("errmsg","对不起,权限不足,无法执行该操作");
            }
            renderJson(jo);
        }else if("popGrid".equalsIgnoreCase(op)){
            AqucyTools tools = new AqucyTools();
            tools.doPopGrid(this);
        }else {
            if(AqucyTools.isPermission("${tableConfig.tableAliasName}_view")){
                if(AqucyTools.isPermission("${tableConfig.tableAliasName}_add")){
                    setAttr("add",true);
                }else{
                    setAttr("add",false);
                }

                if(AqucyTools.isPermission("${tableConfig.tableAliasName}_delete")){
                    setAttr("delete",true);
                }else{
                    setAttr("delete",false);
                }

                if(AqucyTools.isPermission("${tableConfig.tableAliasName}_edit")){
                    setAttr("edit",true);
                }else{
                    setAttr("edit",false);
                }

                if(AqucyTools.isPermission("${tableConfig.tableAliasName}_query")){
                    setAttr("query",true);
                }else{
                    setAttr("query",false);
                }
                render("/WEB-INF/view/project/${tableConfig.tableAliasName}.html");
            }else{
                renderText("对不起,权限不足,无法执行该操作");
            }

        }
    }
<#if tableConfig.subViewObjectId gt 0>
	public  void subTableManager()  throws Exception{
        ${tableConfig.subTableConfig.tableName?cap_first}Model model = getModel(${tableConfig.subTableConfig.tableName?cap_first}Model.class,"${tableConfig.subTableConfig.tableAliasName}");
        String op = getPara("op");
        Map jo = new ConcurrentHashMap();
        jo.put("success", true);
        if ("add".equalsIgnoreCase(op)) {
            try {
                model.save();
                jo.put("errcode", 0);
            } catch (Exception e) {
                DbKit.getConfig().getConnection().rollback();
                jo.put("errcode", 1104);
                jo.put("errmsg", "系统后台异常,请联系管理员");
                e.printStackTrace();
            }
            renderJson(jo);
        } else if ("edit".equalsIgnoreCase(op)) {
            try {
                ${tableConfig.subTableConfig.tableName?cap_first}Model um = ${tableConfig.subTableConfig.tableName?cap_first}Model.dao.findById(model.getLong("${tableConfig.subTableConfig.primaryKey}"));
                if(um==null){
                    jo.put("errcode", 1112);
                    jo.put("errmsg", "查无此数据,无法编辑");
                }else {
                     model.update();
                    jo.put("errcode", 0);
                }
            } catch (Exception e) {
                jo.put("errcode", 1104);
                jo.put("errmsg", "系统后台异常,请联系管理员");
                e.printStackTrace();
            }
            renderJson(jo);
        } else if ("view".equalsIgnoreCase(op)) {
            int start = getParaToInt("page",1);
            int limit = getParaToInt("rows",10);
            start = (start-1)*limit;
            StringBuffer sb = new StringBuffer();
            sb.append("FROM ${tableConfig.subTableConfig.tableName}");
            model.removeNullValueAttrs();
            String[] keys = model.getAttrNames();
            Page<${tableConfig.tableName?cap_first}Model>  page = null;
            if(keys.length>0){
                sb.append(" where 1=1");
                for (String key : keys) {
                    sb.append(" and ").append(key);
                    if(model.get(key) instanceof  String){
                        sb.append(" like ?");
                        String v = model.get(key);
                        v =v .replace("'","");
                        v = "%"+v+"%";
                        model.put(key,v);
                    }else{
                        sb.append(" = ?");
                    }
                }
                String sort = getPara("sort");
                if(StrKit.notBlank(sort)){
                    sb.append(" order by ").append(sort).append(" ").append(getPara("order"));
                }
                page = ${tableConfig.tableName?cap_first}Model.dao.paginate((start/limit)+1,limit,"SELECT *",sb.toString(),model.getAttrValues());

            }else{
                String sort = getPara("sort");
                if(StrKit.notBlank(sort)){
                    sb.append(" order by ").append(sort).append(" ").append(getPara("order"));
                }
                page = ${tableConfig.tableName?cap_first}Model.dao.paginate((start/limit)+1,limit,"SELECT *",sb.toString());
            }
            jo.put("total", page.getTotalRow());
            jo.put("errcode", 0);
            jo.put("rows", page.getList());
            renderJson(jo);
        }else if ("delete".equalsIgnoreCase(op)) {
            String ids = getPara("ids");
            if(StrKit.notBlank(ids)){
                Db.update("delete from ${tableConfig.subTableConfig.tableName} where ${tableConfig.subTableConfig.primaryKey} in(" + ids + ")");
            }
            jo.put("errcode", 0);
            renderJson(jo);
        }else if("popGrid".equalsIgnoreCase(op)){
            AqucyTools tools = new AqucyTools();
            tools.doPopGrid(this);
        }else {
            renderText("error");
        }
    }
        </#if>

}
