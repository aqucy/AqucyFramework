package com.v246.commonWebFramework.systemManager;

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
import com.v246.commonWebFramework.dao.MenuModel;
import com.v246.commonWebFramework.dao.RoleModel;
import com.v246.commonWebFramework.dao.UserModel;
import com.v246.commonWebFramework.dao.UserRoleModel;
import com.v246.commonWebFramework.utils.createCode.Extjs2CreateCode;
import com.v246.commonWebFramework.utils.createCode.ICreateCode;

import java.sql.*;
import java.util.*;
import java.util.Date;
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
    public  void userManager()  throws Exception{
        UserModel model = getModel(UserModel.class,"user");
        String op = getPara("op");
        Map jo = new ConcurrentHashMap();
        jo.put("success", true);
        if ("add".equalsIgnoreCase(op)) {
            try {
                String username = model.getStr("username").toLowerCase();
                if(username.indexOf("admin")!=-1||username.indexOf("user")!=-1||username.indexOf("programmer")!=-1){
                    jo.put("errcode", 1111);
                    jo.put("errmsg","用户名非法,请更换后重试!");
                }else if(UserModel.dao.findFirst("SELECT * FROM acyframework_users where username=?",model.getStr("username"))!=null){
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
                    //为用户与角色建立关联
                    UserRoleModel urm = new UserRoleModel();
                    urm.set("user_id", model.getInt("id"));
                    urm.set("role_id", rm.getInt("id"));
                    urm.set("create_time", new Timestamp(new Date().getTime()));
                    urm.save();
                    jo.put("errcode", 0);
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
                //查询老用户
                UserModel um = UserModel.dao.findById(model.getInt("id"));
                if(um==null){
                    jo.put("errcode", 1112);
                    jo.put("errmsg", "查无此人,无法编辑");
                }else {
                    String username = model.getStr("username").toLowerCase();
                    if(username.indexOf("admin")!=-1||username.indexOf("user")!=-1||username.indexOf("programmer")!=-1){
                        jo.put("errcode", 1111);
                        jo.put("errmsg","用户名非法,请更换后重试!");
                    }else if (UserModel.dao.findFirst("SELECT * FROM acyframework_users where username=? and id<>?", model.getStr("username"),model.getInt("id")) != null) {
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
        }else if ("delete".equalsIgnoreCase(op)) {
            String ids = getPara("ids");
            if(StrKit.notBlank(ids)){
                //解除用户与角色关联
                Db.update("delete from acyframework_user_roles where user_id in(" + ids + ")");
                //删除用户的私有角色
                Db.batch("delete from acyframework_roles where role_name=?", "username", Db.find("SELECT username FROM acyframework_users where id in(" + ids + ")"), 1000);
                //删除用户
                Db.update("delete from acyframework_users where id in(" + ids + ")");

            }
        }else {
            render("userManager.html");
        }
    }
    public void createCodeManager(){
        String op = getPara("op");
        Map jo = new ConcurrentHashMap();
        jo.put("success", true);
        if(StrKit.isBlank(op)){
            render("createCodeManager.html");
        }else if("getColumn".equalsIgnoreCase(op)){
            String targetName = getPara("targetName");
            String sql = "SELECT * FROM "+targetName+" limit 0,1";
            Connection connection = null;
            Statement stm = null;
            ResultSet rs = null;
            List list = new ArrayList();
            try {
                connection =  DbKit.getConfig().getConnection();
                stm = connection.createStatement();
                rs = stm.executeQuery(sql);
                ResultSetMetaData md = rs.getMetaData();
                ResultSet metars = connection.getMetaData().getColumns(connection.getCatalog(),"%",targetName,"%");
//                while(metars.next()){
//                    ResultSetMetaData mdd = metars.getMetaData();
//                    for(int i=1;i<=mdd.getColumnCount();i++) {
//                        System.out.println(mdd.getColumnName(i)+"=="+metars.getString(i));
//                    }
//                    System.out.println("\n\n\n\n");
//                }
//                int c = md.getColumnCount();
//                for(int i=1;i<=c;i++) {
//                    Map m = new HashMap();
//                    String columnName = md.getColumnName(i);
//                    int columnType = md.getColumnType(i);
//                    String columnLabel = md.getColumnLabel(i);
//                    String columnTypeName = md.getColumnTypeName(i);
//                    int columnDisplaySize = md.getColumnDisplaySize(i);
//                    m.put("columnName", columnName);
//                    m.put("columnLabel", columnLabel);
//                    m.put("columnTypeName", columnTypeName);
//                    m.put("columnDisplaySize", columnDisplaySize);
//                    list.add(m);
//                }
                while(metars.next()){
                    Map m = new HashMap();
                    String columnName = metars.getString("COLUMN_NAME");
                    if("id".equalsIgnoreCase(columnName)){
                        continue;
                    }
                    m.put("columnName", columnName);
                    m.put("displayName", metars.getString("REMARKS"));
                    m.put("columnTypeName", metars.getString("TYPE_NAME"));
                    m.put("columnDisplaySize", metars.getString("COLUMN_SIZE"));
                    m.put("required",true);
                    m.put("allowQuery",false);
                    m.put("formType","textInput");
                    int dataType = metars.getInt("DATA_TYPE");
                    if(dataType==Types.INTEGER) {
                        m.put("reg", "^[0-9]*$");
                        m.put("regErrMsg", "只能为数字!");
                    }else if(dataType==Types.VARCHAR||dataType==Types.NVARCHAR) {
                        m.put("maxLength", metars.getInt("COLUMN_SIZE"));
                        m.put("minLength", 1);
                    }else if(dataType==Types.DECIMAL||dataType==Types.NUMERIC){
                        m.put("reg", "^[0-9]+(\\.[0-9]+){0,1}$");
                        m.put("regErrMsg", "只能为数值型数据!");
                    }else if(dataType==Types.TIMESTAMP){
                        m.put("formType","dateTimePicker");
                    }
                    if(metars.getBoolean("IS_NULLABLE")){
                        m.put("required",false);
                    }else{
                        m.put("allowQuery",true);
                    }
                    list.add(m);
                }
                jo.put("datas",list);
                jo.put("errcode", 0);
                renderJson(jo);
            }catch(Exception e){
                e.printStackTrace();
                jo.put("errcode", 1234);
            }

        }else if("createCode".equalsIgnoreCase(op)){
            ICreateCode createCode = new Extjs2CreateCode();
            createCode.createCode(getPara("json"));
            jo.put("errcode", 0);
            renderJson(jo);
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
