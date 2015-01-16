package com.v246.commonWebFramework;

import com.jfinal.config.*;
import com.jfinal.core.JFinal;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.CaseInsensitiveContainerFactory;
import com.jfinal.plugin.activerecord.cache.EhCache;
import com.jfinal.plugin.activerecord.dialect.AnsiSqlDialect;
import com.jfinal.plugin.c3p0.C3p0Plugin;
import com.jfinal.plugin.ehcache.EhCachePlugin;
import com.v246.commonWebFramework.dao.*;
import com.v246.commonWebFramework.systemManager.SystemController;
import com.v246.project.controller.AddasShopController;
import com.v246.project.model.AddasShopModel;

public class MyJfinalConfig extends JFinalConfig {
    public void configConstant(Constants me) {
        me.setBaseViewPath("/WEB-INF/view/");
        me.setDevMode(true);
    }

    public void configRoute(Routes me) {
        me.add("/", MyControllerMain.class);
        me.add("/systemManager", SystemController.class);
me.add("/project/addasShop", AddasShopController.class);
//{route}
    }

    public void configPlugin(Plugins me) {
        C3p0Plugin dsMysql = new C3p0Plugin("jdbc:mysql://xxx.com:3306/acyFramework?characterEncoding=utf8&zeroDateTimeBehavior=convertToNull", "1111", "1111", "com.mysql.jdbc.Driver");
        me.add(dsMysql);
        ActiveRecordPlugin arpMysql = new ActiveRecordPlugin("acyFramework", dsMysql);
        arpMysql.setDialect(new AnsiSqlDialect());
        me.add(arpMysql);
        arpMysql.setCache(new EhCache());
        arpMysql.addMapping("acyFramework_menu", MenuModel.class);
        arpMysql.addMapping("acyFramework_users", UserModel.class);
        arpMysql.addMapping("acyFramework_dictionary", DictionaryModel.class);
        arpMysql.addMapping("acyframework_roles", RoleModel.class);
        arpMysql.addMapping("acyframework_user_roles", UserRoleModel.class);
arpMysql.addMapping("addasShop", AddasShopModel.class);
//{model}
        //不区分大小写 有问题呃model里的字段名会全变成小写 如果你的数据库是区分大小写的,可能会有问题
//        arpMysql.setContainerFactory(new CaseInsensitiveContainerFactory(true));


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
