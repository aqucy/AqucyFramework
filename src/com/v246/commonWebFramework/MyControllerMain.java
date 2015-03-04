package com.v246.commonWebFramework;

import com.jfinal.core.Controller;
import com.v246.commonWebFramework.dao.DictionaryModel;
import com.v246.commonWebFramework.dao.MenuModel;
import com.v246.commonWebFramework.dao.RoleMenuModel;
import com.v246.commonWebFramework.dao.UserModel;
import com.v246.commonWebFramework.pojo.User;
import com.v246.commonWebFramework.utils.AqucyTools;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class MyControllerMain extends Controller {
	public  void  testTreeJson() {
		List<Map<Object, Object>> list = new ArrayList<Map<Object, Object>>();
		for (int i = 1; i < 5; i++) {
			Map<Object, Object> jd = new HashMap<Object, Object>();
			jd.put("id", "num" + i);
			jd.put("text", "节点" + i);
			jd.put("leaf", false);
			jd.put("url", "http://www.baidu.com");
			List<Map<Object, Object>> list1 = new ArrayList<Map<Object, Object>>();
			for (int j = 1; j < 3; j++) {
				Map<Object, Object> subjd = new HashMap<Object, Object>();
				subjd.put("id", "sub" + j);
				subjd.put("text", "子节点" + j);
				subjd.put("leaf", true);
				subjd.put("url", "http://news.qq.com");
				list1.add(subjd);
			}
			jd.put("children",list1);
			list.add(jd);
		}
		renderJson(list);
	}
	public void index(){
		render("index.html");
	}
	public  void  getMyMenu() {
		//得到当前登录的用户
		User user = AqucyTools.getSessionUser();
		//得到当前用户所有角色
		List<RoleMenuModel> rm = RoleMenuModel.dao.find("SELECT menuId FROM acyframework_roles_menus where roleId in (SELECT role_id FROM acyframework_user_roles where user_id=?) group by menuId", user.getId());
		StringBuffer sb = new StringBuffer();
		for(RoleMenuModel model:rm){
			sb.append(model.getLong("menuId")).append(",");
		}
		if(sb.length()>0) {
			sb.deleteCharAt(sb.length() - 1);
		}
		String[] marr = sb.toString().split(",");
		Arrays.sort(marr);

		List<MenuModel> list = MenuModel.dao.find("SELECT * FROM acyFramework_menu where parentId=0 or parentId is null");
		List<MenuModel> re = new ArrayList<MenuModel>();
		for(MenuModel model:list) {
			List<MenuModel> l = getParentMenu(model,marr);
			List<MenuModel> ok = new ArrayList<MenuModel>();
			for(MenuModel mm:l){
				if(mm.getBoolean("havaPermission")){
					model.put("havaPermission", true);
					ok.add(mm);
				}
			}
			model.put("children", ok);
			if(model.getBoolean("havaPermission")!=null&&model.getBoolean("havaPermission")){
				re.add(model);
			}
		}
		renderJson(re);
	}
	private List<MenuModel> getParentMenu(MenuModel model,String[] marr){
		List<MenuModel> list = MenuModel.dao.find("SELECT * FROM acyFramework_menu where parentId=?", model.getLong("id"));
		if(list==null||list.size()<=0) {
			model.put("leaf", true);
			if(Arrays.binarySearch(marr,model.getLong("id").toString())>=0||AqucyTools.hasRole("codeMan")){
				model.put("havaPermission",true);
			}else{
				model.put("havaPermission",false);
			}
		}
		for(MenuModel m:list){
			List<MenuModel> l = getParentMenu(m,marr);
			List<MenuModel> ok = new ArrayList<MenuModel>();
			for(MenuModel mm:l){
				if(mm.getBoolean("havaPermission")!=null&&mm.getBoolean("havaPermission")){
					m.put("havaPermission", true);
					ok.add(mm);
				}
			}
			m.put("children", ok);
		}
		return  list;
	}
	public  void login(){
		render("login.html");
	}
	public  void loginCheck(){
		String username = getPara("username");
		String password = getPara("password");
		System.out.println("加密前密码=="+password);
		password = new Md5Hash(password,username,3).toHex();
		System.out.println("加密后密码=="+password);
		Subject currentUser = SecurityUtils.getSubject();
		UsernamePasswordToken token = new UsernamePasswordToken(username, password);
		Map<Object, Object> jo = new ConcurrentHashMap<Object, Object>();
		jo.put("success", true);
		try {
			currentUser.login(token);
			if(currentUser.isAuthenticated()) {
				//查询出此用户,并保存至session
				UserModel model = UserModel.dao.findFirst("SELECT * FROM acyframework_users where userName=? and password=?", username, password);
				if(model!=null){
					User user = new User();
					user.setId(model.getInt("id"));
					user.setUserName(username);
					user.setUserRealName(model.getStr("userRealName"));
					user.setStatus(model.getInt("status"));
					Session session = currentUser.getSession();
					session.setAttribute("user",user);
					jo.put("errcode", 0);
					jo.put("errmsg", "");
				}else{
					//登录失败
					jo.put("errcode", 1101);
					jo.put("errmsg", "用户名或密码错误");
				}
			}else{
				//登录失败
				jo.put("errcode", 1101);
				jo.put("errmsg", "用户名或密码错误");
			}

		} catch (Exception e) {
			if(e instanceof IncorrectCredentialsException){

			}else{
				e.printStackTrace();
			}
			//登录失败
			jo.put("errcode", 1101);
			jo.put("errmsg", "用户名或密码错误");
		}
		renderJson(jo);
	}
	public void logout(){
		Subject currentUser = SecurityUtils.getSubject();
		Session session = currentUser.getSession();
		session.removeAttribute("user");
		redirect("/login");
	}
	public void getDict() {
		String code = getPara("code");
		List<DictionaryModel> list = null;
		list = DictionaryModel.dao.find("SELECT k label,v value FROM acyFramework_dictionary where code=?",code);
		renderJson(list);
	}

}
