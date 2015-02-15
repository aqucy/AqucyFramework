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
import com.v246.project.model.ShangPinModel;
import com.v246.project.model.AddasShopModel;

/**
 * Created by aquaqu on 2014/12/21.
 */
public class ShangPinController extends Controller {
    public  void index()  throws Exception{
        ShangPinModel model = getModel(ShangPinModel.class,"shangPin");
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
                ShangPinModel um = ShangPinModel.dao.findById(model.getLong("id"));
                if(um==null){
                    jo.put("errcode", 1112);
                    jo.put("errmsg", "查无此数据,无法编辑");
                }else {
                     model.update();
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
            sb.append("FROM shangPin");
            model.removeNullValueAttrs();
            String[] keys = model.getAttrNames();
            Page<ShangPinModel>  page = null;
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
                page = ShangPinModel.dao.paginate((start/limit)+1,limit,"SELECT *",sb.toString(),model.getAttrValues());

            }else{
                String sort = getPara("sort");
                if(StrKit.notBlank(sort)){
                    sb.append(" order by ").append(sort).append(" ").append(getPara("order"));
                }
                page = ShangPinModel.dao.paginate((start/limit)+1,limit,"SELECT *",sb.toString());
            }
            jo.put("total", page.getTotalRow());
            jo.put("errcode", 0);
            jo.put("rows", page.getList());
            renderJson(jo);
        }else if ("delete".equalsIgnoreCase(op)) {
            String ids = getPara("ids");
            if(StrKit.notBlank(ids)){
                Db.update("delete from shangPin where id in(" + ids + ")");
            }
            jo.put("errcode", 0);
            renderJson(jo);
        }else if("popGrid".equalsIgnoreCase(op)){
            AqucyTools tools = new AqucyTools();
            tools.doPopGrid(this);
        }else {
            render("/WEB-INF/view/project/shangPin.html");
        }
    }
	public  void subTableManager()  throws Exception{
        AddasShopModel model = getModel(AddasShopModel.class,"addasShopOne");
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
                AddasShopModel um = AddasShopModel.dao.findById(model.getLong("id"));
                if(um==null){
                    jo.put("errcode", 1112);
                    jo.put("errmsg", "查无此数据,无法编辑");
                }else {
                     model.update();
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
            sb.append("FROM addasShop");
            model.removeNullValueAttrs();
            String[] keys = model.getAttrNames();
            Page<ShangPinModel>  page = null;
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
                page = ShangPinModel.dao.paginate((start/limit)+1,limit,"SELECT *",sb.toString(),model.getAttrValues());

            }else{
                String sort = getPara("sort");
                if(StrKit.notBlank(sort)){
                    sb.append(" order by ").append(sort).append(" ").append(getPara("order"));
                }
                page = ShangPinModel.dao.paginate((start/limit)+1,limit,"SELECT *",sb.toString());
            }
            jo.put("total", page.getTotalRow());
            jo.put("errcode", 0);
            jo.put("rows", page.getList());
            renderJson(jo);
        }else if ("delete".equalsIgnoreCase(op)) {
            String ids = getPara("ids");
            if(StrKit.notBlank(ids)){
                Db.update("delete from addasShop where id in(" + ids + ")");
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

}
