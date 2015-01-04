package com.v246.commonWebFramework.systemManager;

import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.kit.StrKit;
import com.jfinal.kit.StringKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfinal.plugin.activerecord.tx.TxByActionKeys;
import com.v246.commonWebFramework.dao.MenuModel;
import com.v246.commonWebFramework.dao.RoleModel;
import com.v246.commonWebFramework.dao.UserModel;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by aquaqu on 2014/12/21.
 */
public class SystemController extends Controller {
    public void menuManager(){
        String op = getPara("op");
        if (StrKit.notBlank(op)) {
            long id = getParaToLong("id");
            String menuName = getPara("menuName");
            String icon = getPara("icon");
            String url = getPara("url");
            Map<Object, Object> jo = new ConcurrentHashMap<Object, Object>();
            jo.put("success", true);
            if ("subAdd".equalsIgnoreCase(op)) {
                //添加子类
                try {
                    MenuModel menu = new MenuModel();
                    menu.set("url", url);
                    menu.set("icon", icon);
                    menu.set("text", menuName);
                    menu.set("parentId", id);
                    menu.save();
                    jo.put("errcode",0);
                }catch(Exception e){
                    jo.put("errcode",1102);
                    jo.put("errmsg", "新增菜单时发生错误,请与管理员联系");
                    e.printStackTrace();
                }
                renderJson(jo);
            }else if ("edit".equalsIgnoreCase(op)) {
                MenuModel menu = MenuModel.dao.findById(id);
                if(menu!=null){
                    menu.set("url", url);
                    menu.set("icon", icon);
                    menu.set("text", menuName);
                    menu.update();
                    jo.put("errcode",0);
                }else{
                    jo.put("errcode",1103);
                    jo.put("errmsg", "未找到您所要修改的菜单");
                }
                renderJson(jo);
            }else if ("drag".equalsIgnoreCase(op)) {
                long parentId = getParaToLong("parentId");
                MenuModel menu = MenuModel.dao.findById(id);
                if(menu!=null){
                    menu.set("parentId", parentId);
                    menu.update();
                    jo.put("errcode",0);
                }else{
                    jo.put("errcode",1103);
                    jo.put("errmsg", "未找到您所要修改的菜单");
                }
                renderJson(jo);
            }else if ("delete".equalsIgnoreCase(op)) {

                 deleteMenu(MenuModel.dao.findById(id));
                jo.put("errcode",0);
                renderJson(jo);
            }
        }else {
            render("menuManager.html");
        }
    }
    @Before(Tx.class)
    public  void userManager() {
        UserModel model = getModel(UserModel.class,"user");
        String op = getPara("op");
        Map jo = new ConcurrentHashMap();
        jo.put("success", true);
        if ("add".equalsIgnoreCase(op)) {
            try {
                if(UserModel.dao.findFirst("SELECT * FROM acyframework_users where username=?",model.getStr("username"))!=null){
                    jo.put("errcode", 1111);
                    jo.put("errmsg","该用户名已经存在,请换一个试试");

                }else {
                    model.set("create_time", new Timestamp(new Date().getTime()));
                    model.save();
                    //每个用户默认有一个角色,以自己的真实姓名为显示名称,以登录名为角色名
                    RoleModel rm = new RoleModel();
                    rm.set("role_name", model.getStr("username"));
                    rm.set("description", model.getStr("userRealName"));
                    rm.set("isPrivate", true);
                    rm.save();
                    jo.put("errcode", 0);
                }
            } catch (Exception e) {
                jo.put("errcode", 1104);
                e.printStackTrace();
            }
            renderJson(jo);
        } else if ("edit".equalsIgnoreCase(op)) {
            try {
                //查询老用户
                UserModel um = UserModel.dao.findById(model.getLong("id"));
                if(um==null){
                    jo.put("errcode", 1112);
                    jo.put("errmsg", "查无此人,无法编辑");
                }else {
                    if (UserModel.dao.findFirst("SELECT * FROM acyframework_users where username=? and id<>?", model.getStr("username"),model.getLong("id")) != null) {
                        jo.put("errcode", 1111);
                        jo.put("errmsg", "该用户名已经存在,请换一个试试");

                    }else {
                        model.update();
                        RoleModel rm = RoleModel.dao.findFirst("SELECT * FROM acyframework_roles where role_name=?", um.getStr("username"));
                        if (rm == null) {
                            jo.put("errcode", 1113);
                            jo.put("errmsg", "未找到要修改的角色,请联系攻城狮");
                        } else {
                            rm.set("role_name", model.getStr("username"));
                            rm.set("description", model.getStr("userRealName"));
                            rm.set("isPrivate", true);
                            rm.update();
                        }
                    }
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
            sb.append("FROM acyframework_users");
            model.removeNullValueAttrs();
            String[] keys = model.getAttrNames();
            Page<UserModel>  page = null;
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
                page = UserModel.dao.paginate((start/limit)+1,limit,"SELECT *",sb.toString(),model.getAttrValues());


            }else{
                page = UserModel.dao.paginate((start/limit)+1,limit,"SELECT *",sb.toString());
            }
            jo.put("totalCount", page.getTotalRow());
            jo.put("errcode", 0);
            jo.put("datas", page.getList());
            renderJson(jo);
        }else {
            render("userManager.html");
        }
    }
    private void test(Object... params){
        System.out.println(params.getClass());
        for(Object str:params){
            System.out.println(str);
        }
    }
    private void deleteMenu(MenuModel m) {
        long id = m.getLong("id");
        List<MenuModel> list = MenuModel.dao.find("SELECT * FROM acyframework_menu where parentId=?", id);
        for (MenuModel model : list) {
            deleteMenu(model);
        }
        m.delete();
    }
}
