package com.v246.commonWebFramework;

import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.kit.StrKit;
import com.jfinal.kit.StringKit;
import com.jfinal.plugin.ehcache.CacheInterceptor;
import com.jfinal.plugin.ehcache.CacheName;
import com.v246.commonWebFramework.dao.DictionaryModel;
import com.v246.commonWebFramework.dao.MenuModel;
import com.v246.project.model.sp;
import net.sf.json.JSONObject;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
		List<MenuModel> list = MenuModel.dao.find("SELECT * FROM acyFramework_menu where parentId=0 or parentId is null");
		for(MenuModel model:list) {
			model.put("children", getParentMenu(model));
		}
		renderJson(list);
	}
	private List<MenuModel> getParentMenu(MenuModel model){
		List<MenuModel> list = MenuModel.dao.find("SELECT * FROM acyFramework_menu where parentId=?", model.getLong("id"));
		if(list==null||list.size()<=0) {
			model.put("leaf", true);
		}
		for(MenuModel m:list){
			m.put("children", getParentMenu(m));
		}
		return  list;
	}
	public  void login(){
		render("login.html");
	}
	public  void loginCheck(){
		String username = getPara("username");
		String password = getPara("password");
		Subject currentUser = SecurityUtils.getSubject();
		UsernamePasswordToken token = new UsernamePasswordToken(username, password);
		Map<Object, Object> jo = new ConcurrentHashMap<Object, Object>();
		jo.put("success", true);
		try {
			currentUser.login(token);
			jo.put("errcode", 0);
			jo.put("errmsg", "");

		} catch (Exception e) {
			//登录失败
			jo.put("errcode", 1101);
			jo.put("errmsg", "用户名或密码错误");
		}
		renderJson(jo);
	}
	public void getDict() {
		String code = getPara("code");
		List<DictionaryModel> list = null;
		list = DictionaryModel.dao.find("SELECT k label,v value FROM acyFramework_dictionary where code=?",code);
		renderJson(list);
	}
	public void sp(){
		String op = getPara("op");
		if("view".equalsIgnoreCase(op)){
			List<sp> list = sp.dao.find("SELECT * FROM shangPin");
			renderJson(list);
		}else {
			render("/WEB-INF/view/project/sp.html");
		}
	}
}
