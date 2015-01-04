package com.v246.commonWebFramework;

import com.jfinal.config.*;
import com.jfinal.core.JFinal;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.cache.EhCache;
import com.jfinal.plugin.activerecord.dialect.AnsiSqlDialect;
import com.jfinal.plugin.c3p0.C3p0Plugin;
import com.jfinal.plugin.ehcache.EhCachePlugin;
import com.v246.commonWebFramework.dao.DictionaryModel;
import com.v246.commonWebFramework.dao.MenuModel;
import com.v246.commonWebFramework.dao.RoleModel;
import com.v246.commonWebFramework.dao.UserModel;
import com.v246.commonWebFramework.systemManager.SystemController;

public class MyJfinalConfig extends JFinalConfig {
    public void configConstant(Constants me) {
        me.setBaseViewPath("/WEB-INF/view/");
        me.setDevMode(true);
    }

    public void configRoute(Routes me) {
        me.add("/", MyControllerMain.class);
        me.add("/systemManager", SystemController.class);
    }

    public void configPlugin(Plugins me) {
        C3p0Plugin dsMysql = new C3p0Plugin("jdbc:mysql://hy.v246.com:3306/acyFramework?characterEncoding=utf8&zeroDateTimeBehavior=convertToNull", "root", "youpassworld", "com.mysql.jdbc.Driver");
        me.add(dsMysql);
        ActiveRecordPlugin arpMysql = new ActiveRecordPlugin("acyFramework", dsMysql);
        arpMysql.setDialect(new AnsiSqlDialect());
        me.add(arpMysql);
        arpMysql.setCache(new EhCache());
        arpMysql.addMapping("acyFramework_menu", MenuModel.class);
        arpMysql.addMapping("acyFramework_users", UserModel.class);
        arpMysql.addMapping("acyFramework_dictionary", DictionaryModel.class);
        arpMysql.addMapping("acyframework_roles", RoleModel.class);


        dsMysql = new C3p0Plugin("jdbc:mysql://hy.v246.com:3306/addas?characterEncoding=utf8&zeroDateTimeBehavior=convertToNull", "addas", "addas123", "com.mysql.jdbc.Driver");
        me.add(dsMysql);
        arpMysql = new ActiveRecordPlugin("addas", dsMysql);
        arpMysql.setDialect(new AnsiSqlDialect());
        me.add(arpMysql);

        me.add(new EhCachePlugin());


    }

    public void configInterceptor(Interceptors me) {
//        me.add(new LoginInterceptor());
    }

    public void configHandler(Handlers me) {

    }
    public static void main(String[] args) {
        JFinal.start("web", 88, "/", 5);
    }
}