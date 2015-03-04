package com.v246.commonWebFramework.systemManager;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.kit.JsonKit;
import com.jfinal.kit.PathKit;
import com.jfinal.kit.StrKit;
import com.jfinal.kit.StringKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.DbKit;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfinal.plugin.activerecord.tx.TxByActionKeys;
import com.v246.commonWebFramework.dao.*;
import com.v246.commonWebFramework.pojo.User;
import com.v246.commonWebFramework.utils.AqucyTools;
import com.v246.commonWebFramework.utils.createCode.EasyUICreateCode;
import com.v246.commonWebFramework.utils.createCode.Extjs2CreateCode;
import com.v246.commonWebFramework.utils.createCode.ICreateCode;
import com.v246.commonWebFramework.utils.createCode.pojo.TableConfig;
import net.sf.json.JSON;
import org.apache.shiro.crypto.hash.Md5Hash;

import java.awt.event.MouseAdapter;
import java.io.*;
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
            String permissionGroupName = getPara("permissionGroupName");
            Map<Object, Object> jo = new ConcurrentHashMap<Object, Object>();
            jo.put("success", true);
            if(StrKit.notBlank(permissionGroupName)) {
                PermissionsModel model = PermissionsModel.dao.findFirst("SELECT id FROM acyframework_permissions where groupName=?", permissionGroupName);
                if(model==null){
                    jo.put("errcode",1103);
                    jo.put("errmsg", "对不起,未找到该组权限,请先添加权限!");
                    renderJson(jo);
                    return;
                }
            }
            if ("subAdd".equalsIgnoreCase(op)) {
                if(id==5||id==23||id==6){
                    jo.put("errcode",1103);
                    jo.put("errmsg", "系统内置菜单不可操作");
                }else{
                    //添加子类
                    try {
                        MenuModel menu = new MenuModel();
                        menu.set("url", url);
                        menu.set("icon", icon);
                        menu.set("text", menuName);
                        menu.set("parentId", id);
                        menu.set("permissionGroupName", permissionGroupName);
                        menu.save();
                        jo.put("errcode",0);
                    }catch(Exception e){
                        jo.put("errcode",1102);
                        jo.put("errmsg", "新增菜单时发生错误,请与管理员联系");
                        e.printStackTrace();
                    }
                }
                renderJson(jo);
            }else if ("edit".equalsIgnoreCase(op)) {
                if(id==5||id==23||id==6){
                    jo.put("errcode",1103);
                    jo.put("errmsg", "系统内置菜单不可编辑");
                }else{
                    MenuModel menu = MenuModel.dao.findById(id);
                    if(menu!=null){
                        menu.set("url", url);
                        menu.set("icon", icon);
                        menu.set("text", menuName);
                        menu.set("permissionGroupName", permissionGroupName);
                        menu.update();
                        jo.put("errcode",0);
                    }else{
                        jo.put("errcode",1103);
                        jo.put("errmsg", "未找到您所要修改的菜单");
                    }
                }

                renderJson(jo);
            }else if ("drag".equalsIgnoreCase(op)) {
                if(id==5||id==23||id==6){
                    jo.put("errcode",1103);
                    jo.put("errmsg", "系统内置菜单不可移动");
                }else{
                    long parentId = getParaToLong("parentId");
                    MenuModel menu = MenuModel.dao.findById(id);
                    if(menu!=null){
                        menu.set("parentId", parentId);
                        menu.update();
                        jo.put("errcode",0);
                    }else{
                        jo.put("errcode", 1103);
                        jo.put("errmsg", "未找到您所要修改的菜单");
                    }
                }
                renderJson(jo);
            }else if ("delete".equalsIgnoreCase(op)) {
                if(id==5||id==23||id==6){
                    jo.put("errcode",1103);
                    jo.put("errmsg", "系统内置菜单不可删除");
                }else{
                    MenuModel model = MenuModel.dao.findById(id);
                    if(model.getBoolean("readOnly")!=null&&model.getBoolean("readOnly")){
                        jo.put("errcode",1103);
                        jo.put("errmsg", "只读状态的菜单无法删除!");
                    }else{
                        deleteMenu(model);
                        jo.put("errcode",0);
                    }
                }
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
                    username = model.get("username");
                    String password = model.get("password");
                    model.set("password",new Md5Hash(password,username,3).toHex());
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
                        username = model.get("username");
                        String password = model.get("password");
                        if(StrKit.notBlank(password)){
                            model.set("password",new Md5Hash(password,username,3).toHex());
                        }
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
                            jo.put("errcode", 0);
                        }
                    }
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
                String sort = getPara("sort");
                if(StrKit.notBlank(sort)){
                    sb.append(" order by ").append(sort).append(" ").append(getPara("order"));
                }
                page = UserModel.dao.paginate((start/limit)+1,limit,"SELECT id,username,status,userRealName,sex,mobile,phone,address,mail,QQ,age",sb.toString(),model.getAttrValues());

            }else{
                String sort = getPara("sort");
                if(StrKit.notBlank(sort)){
                    sb.append(" order by ").append(sort).append(" ").append(getPara("order"));
                }
                page = UserModel.dao.paginate((start/limit)+1,limit,"SELECT id,username,status,userRealName,sex,mobile,phone,address,mail,QQ,age",sb.toString());
            }
            jo.put("total", page.getTotalRow());
            jo.put("errcode", 0);
            jo.put("rows", page.getList());
            renderJson(jo);
        }else if ("delete".equalsIgnoreCase(op)) {
            String ids = getPara("ids");
            if(StrKit.notBlank(ids)){
//                //删除用户权限
                Db.batch("delete from acyframework_roles_permissions where role_id=?","role_id",Db.find("SELECT role_id FROM acyframework_user_roles where user_id in(" + ids + ")"), 1000);
//                //删除用户菜单
                Db.batch("delete from acyframework_roles_menus where roleId=?","role_id",Db.find("SELECT role_id FROM acyframework_user_roles where user_id in (" + ids + ")"), 1000);
                //删除用户的私有角色
                Db.batch("delete from acyframework_roles where role_name=?", "username", Db.find("SELECT username FROM acyframework_users where id in(" + ids + ")"), 1000);
                //删除用户
                Db.update("delete from acyframework_users where id in(" + ids + ")");
                //解除用户与角色关联
                Db.update("delete from acyframework_user_roles where user_id in(" + ids + ")");
            }
            jo.put("errcode", 0);
            renderJson(jo);
        }else if("modifyPassword".equalsIgnoreCase(op)){
            String oldPassword = getPara("oldPassword","");
            String password = getPara("password","");
            String passwordAgain = getPara("passwordAgain","");
            String errmsg = null;
            if(StrKit.isBlank(oldPassword)){
                errmsg = "原密码不能为空!";
            }else if(StrKit.isBlank(password)){
                errmsg = "新密码不能为空";
            }else if(password.length()<6){
                errmsg = "新密码不能小于6位!";
            }else if(password.equalsIgnoreCase(passwordAgain)==false){
                errmsg = "两次输入的密码不一至";
            }
            if(errmsg!=null){
                jo.put("errcode",1);
                jo.put("errmsg",errmsg);
            }else{
                //取出
                User user  = AqucyTools.getSessionUser();
                String username = user.getUserName();
                oldPassword = new Md5Hash(oldPassword,username,3).toHex();
                //校验原密码是否正确
                UserModel m = UserModel.dao.findFirst("SELECT * FROM acyframework_users where username=? and password=?",username,oldPassword);
                if(m!=null){
                    password = new Md5Hash(password,username,3).toHex();
                    m.set("password",password);
                    m.update();
                    jo.put("errcode",0);
                }else{
                    jo.put("errcode",1);
                    jo.put("errmsg","原密码错误");
                }
            }
            renderJson(jo);
        }else if("popGrid".equalsIgnoreCase(op)){
            AqucyTools tools = new AqucyTools();
            tools.doPopGrid(this);
        }else {
            render("userManager.html");
        }
    }
    public void createCodeManager(){
        String op = getPara("op");
        long id = getParaToLong("id",0L);
        setAttr("void",id);
        Map jo = new ConcurrentHashMap();
        jo.put("success", true);
        ViewObjectModel vo = null;
        if(StrKit.isBlank(op)){
            if(id>0){
                vo = ViewObjectModel.dao.findById(id);


            }else{
                vo = new ViewObjectModel();
                vo.set("allowSubViewObjectAdd",true);
                vo.set("allowSubViewObjectDelete",true);
                vo.set("allowSubViewObjectEdit",true);
                vo.set("allowAdd",true);
                vo.set("allowEdit",true);
                vo.set("allowDelete",true);
            }
            setAttr("vo",vo);
            render("createCodeManager.html");
        }else if("getColumn".equalsIgnoreCase(op)){
            String targetName = getPara("tableName");
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
                String primaryKey = "";
                ResultSet metars = connection.getMetaData().getColumns(connection.getCatalog(),"%",targetName,"%");
                ResultSet primaryrs = connection.getMetaData().getPrimaryKeys(connection.getCatalog(),null,targetName);
                if(primaryrs.next()){
                    primaryKey = primaryrs.getString("COLUMN_NAME");
                }
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
                    m.put("required","是");
                    m.put("primaryKey", primaryKey);
                    m.put("allowQuery","");
                    m.put("formType","textInput");
                    int dataType = metars.getInt("DATA_TYPE");
                    if(dataType==Types.INTEGER||dataType==Types.TINYINT||dataType==Types.BIGINT) {
                        m.put("validateType", "integer");
                    }else if(dataType==Types.VARCHAR||dataType==Types.NVARCHAR) {
                        m.put("maxLength", metars.getInt("COLUMN_SIZE"));
                        m.put("minLength", 1);
                    }else if(dataType==Types.DECIMAL||dataType==Types.NUMERIC||dataType==Types.FLOAT||dataType==Types.DOUBLE){
                        m.put("validateType", "intOrFloat");
                    }else if(dataType==Types.TIMESTAMP){
                        m.put("inputType","datetimebox");
                    }else if(dataType==Types.DATE){
                        m.put("inputType","datebox");
                    }
                    if(metars.getBoolean("IS_NULLABLE")){
                        m.put("required","");
                    }else{
                        m.put("required","是");
                    }
                    list.add(m);
                }
                jo.put("rows",list);
                jo.put("errcode", 0);

            }catch(Exception e){
                e.printStackTrace();
                jo.put("errcode", 1234);
                jo.put("errmsg","查无此表或视图");
                jo.put("rows",list);
            }
            renderJson(jo);

        }else if("createCode".equalsIgnoreCase(op)){
            AqucyTools tools = new AqucyTools();
            try {
                Map<String,Object> root = tools.resolveToRootMap(getPara("json"));
                if(root.containsKey("errmsg")){
                    jo.put("errcode", 1);
                    jo.put("errmsg", root.get("errmsg"));
                }else{
                    long vid = tools.saveConfigToDb((TableConfig)root.get("tableConfig"));
                    if(vid>0l){
                        ICreateCode createCode = new EasyUICreateCode();
                        String re = createCode.createCode(getPara("json"));
                        if(re!=null){
                            jo.put("errcode", 1);
                            jo.put("errmsg",re);
                        }else{
                            jo.put("errcode", 0);
                            jo.put("vid", vid);
                        }
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            renderJson(jo);
        }else if("getVo".equalsIgnoreCase(op)){
            List<ViewObjectColumnConfigModel> list = ViewObjectColumnConfigModel.dao.find("SELECT * FROM acyframework_view_object_columns_config where viewObjectId=?", id);
            Map mp = new HashMap();
            mp.put("rows",list);
            String json = null;
            ObjectMapper om = new ObjectMapper();
            json = JsonKit.toJson(mp);
            json = json.replace("true", "\"是\"");
            json = json.replace("false","\"\"");
            json = json.replace("\"null\"","\"\"");
            json = json.replace(":null",":\"\"");
            renderJson(json);
        }else if("saveCode".equalsIgnoreCase(op)){
            AqucyTools tools = new AqucyTools();
            try {
                Map<String,Object> root = tools.resolveToRootMap(getPara("json"));
                if(root.containsKey("errmsg")){
                    jo.put("errcode", 1);
                    jo.put("errmsg", root.get("errmsg"));
                }else{
                    long vid = tools.saveConfigToDb((TableConfig)root.get("tableConfig"));
                    jo.put("errcode", 0);
                    jo.put("vid", vid);
                }

            }catch (Exception e){
                jo.put("errcode", 1);
                jo.put("errmsg", "操作异常,请联系管理员");
                e.printStackTrace();
            }
            renderJson(jo);
        }else if("getAllDict".equalsIgnoreCase(op)){
            List<Record> list = Db.find("SELECT distinct(code) FROM acyframework_dictionary");
            for(Record r:list){
                String k = r.get("code");
                r.set("label",k);
                r.set("value",k);
            }
            renderJson(list);
        }

    }
    public  void validateSelectInputSql(){
        String sql = getPara("sql");
        Map jo = new HashMap();
        List list = new ArrayList();
        jo.put("errcode", 0);
        if(StrKit.isBlank(sql)){
            jo.put("errcode", 1);
            jo.put("errmsg", "sql语句不能为空");
        }else{
            AqucyTools tools = new AqucyTools();
            try {
                Map<String,String> map = tools.getColumns(sql);
                if(map.size()>0){
                    Iterator<String> it = map.keySet().iterator();
                    while (it.hasNext()) {
                        String k = it.next();
                        Map m = new HashMap();
                        m.put("label", k);
                        m.put("value",k);
                        list.add(m);
                    }
                    jo.put("datas", list);
                }else{
                    jo.put("errcode", 1);
                    jo.put("errmsg", "sql语句有错误!");
                }
            }catch (Exception e){
                jo.put("errcode", 1);
                jo.put("errmsg", "sql语句有错误!\n"+e.getMessage());
            }
        }
        renderJson(jo);
    }
    public void getVoColumn(){
        long id = getParaToLong("id",0L);
        Map jo = new HashMap();
        if(id<=0L){
            jo.put("errcode",1);
            jo.put("errmsg","无此VO");
        }else{
            List<ViewObjectColumnConfigModel> list = ViewObjectColumnConfigModel.dao.find("SELECT columnName value,displayName label FROM acyframework_view_object_columns_config where viewObjectId=?", id);
            for(ViewObjectColumnConfigModel model:list){
                if(StrKit.isBlank(model.getStr("label"))){
                    model.put("label", model.getStr("value"));
                }
            }
            ViewObjectColumnConfigModel m = new ViewObjectColumnConfigModel();
            m.put("label","id");
            m.put("value","id");
            list.add(0,m);
            jo.put("datas", list);
            jo.put("errcode",0);
        }
        renderJson(jo);
    }
    @Before(Tx.class)
    public void viewObjectManager(){
        String op = getPara("op");
        Map<Object, Object> jo = new HashMap<Object, Object>();
        ViewObjectModel model = getModel(ViewObjectModel.class,"vo");
        if(StrKit.isBlank(op)){
            render("voManager.html");
        }
        else if("view".equalsIgnoreCase(op)) {
            int start = getParaToInt("page",1);
            int limit = getParaToInt("rows",10);
            start = (start-1)*limit;
            StringBuffer sb = new StringBuffer();
            sb.append("FROM acyframework_view_object");
            model.removeNullValueAttrs();
            String[] keys = model.getAttrNames();
            Page<ViewObjectModel>  page = null;
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
                page = ViewObjectModel.dao.paginate((start/limit)+1,limit,"SELECT *",sb.toString(),model.getAttrValues());

            }else{
                String sort = getPara("sort");
                if(StrKit.notBlank(sort)){
                    sb.append(" order by ").append(sort).append(" ").append(getPara("order"));
                }
                page = ViewObjectModel.dao.paginate((start/limit)+1,limit,"SELECT *",sb.toString());
            }
            jo.put("total", page.getTotalRow());
            jo.put("errcode", 0);
            jo.put("rows", page.getList());
            renderJson(jo);
        }else if ("delete".equalsIgnoreCase(op)) {
            String ids = getPara("ids");
            StringBuffer errmsg = new StringBuffer();
            if(StrKit.notBlank(ids)){
                String[] arr = ids.split(",");
                for(int i=0;i<arr.length;i++){

                    boolean HasHref = false;
                    long id = Long.parseLong(arr[i]);
                    //有引用子表不能删除
                    ViewObjectModel voSub = ViewObjectModel.dao.findFirst("SELECT * FROM acyframework_view_object where subViewObjectId=?",id);
                    //查看是否有其它VO引用
                    ViewObjectModel vo = ViewObjectModel.dao.findById(id);
                    String tableName = vo.get("tableName");
                    String templateName = vo.get("templateName");
                    ViewObjectModel voOther = ViewObjectModel.dao.findFirst("SELECT * FROM acyframework_view_object where tableName=? and id<>?",tableName,id);
                    //删除已生成的文件
                    //删除control
                    String srcPath = AqucyTools.getSrcPath();
                    File file = new File(srcPath+"/src/com/v246/project/controller/"+StrKit.firstCharToUpperCase(vo.getStr("tableAliasName"))+"Controller.java");
                    if(file.exists()){
                        file.delete();
                    }
                    //删除view
                    file = new File(PathKit.getWebRootPath()+"/WEB-INF/view/project/"+vo.getStr("tableAliasName")+".html");
                    if(file.exists()){
                        file.delete();
                    }
                    //删除路由
                    StringBuffer sb = new StringBuffer(500);
                    String jfinalConfigPath = null;
                    try {
                        jfinalConfigPath = srcPath+"/src/com/v246/commonWebFramework/MyJfinalConfig.java";
                        BufferedReader br = new BufferedReader(new FileReader(jfinalConfigPath));
                        String data = br.readLine();//一次读入一行，直到读入null为文件结束
                        while( data!=null){
                            sb.append(data).append("\n");
                            data = br.readLine(); //接着读下一行
                        }
                        br.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                        try {
                            DbKit.getConfig().getConnection().rollback();
                        } catch (SQLException e1) {
                            e1.printStackTrace();
                        }
                    }
                    String str = sb.toString();
                    String modelfullStr = "arpMysql.addMapping(\"{1}\",\"{3}\", {2});";
                    String  controllerStr = "me.add(\"/project/{1}\", {2}Controller.class);";
                    String modelClass = StrKit.firstCharToUpperCase(tableName)+"Model.class";
                    modelfullStr = modelfullStr.replace("{1}",tableName).replace("{2}", modelClass).replace("{3}",vo.getStr("primaryKey"));
                    controllerStr = controllerStr.replace("{1}",vo.getStr("tableAliasName")).replace("{2}",StrKit.firstCharToUpperCase(vo.getStr("tableAliasName")));
                    str = str.replace(controllerStr,"");
                    if(voOther==null&&voSub==null){
                        //删除MODEL
                        str = str.replace(modelfullStr,"");
                        file = new File(srcPath+"/src/com/v246/project/model/"+modelClass.replace(".class",".java"));
                        if(file.exists()){
                            file.delete();
                        }
                    }
                    //删除VO字段配置
                    Db.update("delete from acyframework_view_object_columns_config where viewObjectId=?",id);
                    //删除弹出选择的SQL语句
                    Db.update("delete from acyframework_popgridsql where code like '"+vo.get("tableAliasName")+"/_%' escape '/'");
                    //删除自己
                    vo.delete();
                    //删除已配置的菜单
                    Db.update("delete from acyframework_menu where voId=?",id);
                    //删除权限代码
                    Db.update("delete from acyframework_permissions where groupName=?",vo.getStr("tableAliasName"));
                    //保存jfinal配置
                    //删除MODEL
                    str = str.replace(modelfullStr,"");
                    file = new File(jfinalConfigPath);
                    FileOutputStream out = null;
                    try {
                        out = new FileOutputStream(file);
                        out.write(str.getBytes());
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }
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
    @Before(Tx.class)
    public void permissionManager(){
        String op = getPara("op");
        Map jo = new HashMap();
        if("getMenu".equalsIgnoreCase(op)){
            int roleId = getParaToInt("roleId",0);
            StringBuffer sb = new StringBuffer();
            //查找该角色所有的菜单
            List<RoleMenuModel> mlist = RoleMenuModel.dao.find("SELECT * FROM acyframework_roles_menus where roleId=? order by menuId asc", roleId);
            for(RoleMenuModel m:mlist){
                sb.append(m.getLong("menuId")).append(",");
            }
            if(sb.length()>0){
                sb.deleteCharAt(sb.length()-1);
            }
            String[] marr = sb.toString().split(",");
            //查找该角色所有的权限
            sb = new StringBuffer();
            List<RolePermissionModel> plist = RolePermissionModel.dao.find("SELECT * FROM acyframework_roles_permissions where role_id=? order by permission_id asc", roleId);
            for(RolePermissionModel m:plist){
                sb.append(m.getInt("permission_id")).append(",");
            }
            if(sb.length()>0){
                sb.deleteCharAt(sb.length()-1);
            }
            String[] parr = sb.toString().split(",");
            Arrays.sort(parr);
            Arrays.sort(marr);
            renderJson(getAllMenuForPermissionConfig(marr, parr));
        }else if("save".equalsIgnoreCase(op)){
            String menus = getPara("menuId");
            String permissions = getPara("permissionId");
            int roleId = getParaToInt("roleId",0);
            String[] marr = menus.split(",");
            String[] parr = permissions.split(",");
            try {
                //删除旧的
                Db.update("delete from acyframework_roles_menus where roleId=?", roleId);
                Db.update("delete from acyframework_roles_permissions where role_id=?", roleId);

                //写入新的
                if(StrKit.notBlank(menus)){
                    for (String v : marr) {
                        RoleMenuModel rm = new RoleMenuModel();
                        rm.set("roleId", roleId);
                        rm.set("menuId", Integer.parseInt(v));
                        rm.save();
                    }
                }
                if(StrKit.notBlank(permissions)){
                    for (String v : parr) {
                        RolePermissionModel rpm = new RolePermissionModel();
                        rpm.set("role_id", roleId);
                        rpm.set("permission_id", Integer.parseInt(v));
                        rpm.save();
                    }
                }
                jo.put("errcode", 0);
            }catch (Exception e){
                e.printStackTrace();
                try {
                    DbKit.getConfig().getConnection().rollback();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
                jo.put("errcode", 1);
                jo.put("errmsg","操作异常");
            }

            renderJson(jo);
        }else {
            String roleName = getPara("roleName");
            if(StrKit.isBlank(roleName)){
                renderText("查无此角色");
                return;
            }
            RoleModel rm = RoleModel.dao.findFirst("SELECT * FROM acyframework_roles where role_name=?", roleName);
            setAttr("roleName", roleName);
            setAttr("roleId", rm.getInt("id"));
            setAttr("roleDescription", rm.getStr("description"));
            render("permissionManager.html");
        }
    }
    private  Object  getAllMenuForPermissionConfig(String[] marr,String[] parr) {
        List<MenuModel> list = MenuModel.dao.find("SELECT * FROM acyFramework_menu where parentId=0 or parentId is null");
        for(MenuModel model:list) {
            List<MenuModel> ml = getChildMenu(model,marr,parr);
            model.put("children", ml);
            if(Arrays.binarySearch(marr,model.getLong("id").toString())>=0){
                model.put("checked",true);
            }
        }
        return list;
    }
    private List<MenuModel> getChildMenu(MenuModel model,String[] marr,String[] parr){
        List<MenuModel> list = MenuModel.dao.find("SELECT * FROM acyFramework_menu where parentId=?", model.getLong("id"));
        if(list==null||list.size()<=0) {
            model.put("leaf", true);
            //获取该菜单下的所有权限
            List<PermissionsModel> pl = PermissionsModel.dao.find("SELECT * FROM acyframework_permissions where groupName=?", model.getStr("permissionGroupName"));
            model.put("allPermission", pl);
            //获取该用户对该菜单已拥有的权限
            StringBuffer sb = new StringBuffer();
            for(PermissionsModel m:pl){
                if(Arrays.binarySearch(parr, m.getInt("id").toString())>=0){
                    sb.append(m.getInt("id")).append(",");
                }
            }
            model.put("havePermission",sb.toString());
            int index = Arrays.binarySearch(marr, model.getLong("id").toString());
            if(index>=0){
                model.put("checked",true);
            }
        }
        for(MenuModel m:list){
            List<MenuModel> ml = getChildMenu(m,marr,parr);
            m.put("children", ml);
        }
        return  list;
    }
}
