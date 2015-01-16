package com.v246.project.controller;

import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.kit.StrKit;
import com.jfinal.kit.StringKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.DbKit;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfinal.plugin.activerecord.tx.TxByActionKeys;
import com.v246.project.model.*;
import com.v246.project.controller.*;

import java.sql.*;
import java.util.*;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by aquaqu on 2014/12/21.
 */
public class AddasShopController extends Controller {
    @Before(Tx.class)
    public  void index()  throws Exception{
        AddasShopModel model = getModel(AddasShopModel.class,"addasShop");
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
                //查询老用户
                AddasShopModel um = AddasShopModel.dao.findById(model.getLong("id"));
                if(um==null){
                    jo.put("errcode", 1112);
                    jo.put("errmsg", "查无此人,无法编辑");
                }else {
                    model.update();
                }
                jo.put("errcode", 0);
            } catch (Exception e) {
                jo.put("errcode", 1104);
                jo.put("errmsg", "系统后台异常,请联系管理员");
                e.printStackTrace();
            }
            renderJson(jo);
        } else if ("view".equalsIgnoreCase(op)) {
            int start = getParaToInt("start",0);
            int limit = getParaToInt("limit",3);
            StringBuffer sb = new StringBuffer();
            sb.append("FROM addasShop");
            model.removeNullValueAttrs();
            String[] keys = model.getAttrNames();
            Page<AddasShopModel>  page = null;
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
                page = AddasShopModel.dao.paginate((start/limit)+1,limit,"SELECT *",sb.toString(),model.getAttrValues());


            }else{
                page = AddasShopModel.dao.paginate((start/limit)+1,limit,"SELECT *",sb.toString());
            }
            jo.put("totalCount", page.getTotalRow());
            jo.put("errcode", 0);
            jo.put("datas", page.getList());
            renderJson(jo);
        }else if ("delete".equalsIgnoreCase(op)) {
            String ids = getPara("ids");
            if(StrKit.notBlank(ids)){
                Db.update("delete from addasShop where id in(" + ids + ")");
            }
        }else {
            render("/WEB-INF/view/project/addasShop.html");
        }
    }
}
