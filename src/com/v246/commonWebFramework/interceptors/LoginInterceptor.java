package com.v246.commonWebFramework.interceptors;

import com.jfinal.aop.Interceptor;
import com.jfinal.core.ActionInvocation;
import com.jfinal.kit.PathKit;
import com.v246.commonWebFramework.dao.MenuModel;
import com.v246.commonWebFramework.pojo.User;
import freemarker.cache.FileTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;

/**
 * Created by aquaqu on 2014/11/14.
 */
public class LoginInterceptor implements Interceptor {
    @Override
    public void intercept(ActionInvocation ai) {
//        System.out.println("action==="+ai.getActionKey());
        if(ai.getActionKey().equalsIgnoreCase("/login")||ai.getActionKey().equalsIgnoreCase("/loginCheck")){
//            System.out.println("放行==="+ai.getActionKey());
            ai.invoke();

            return;
        }
        Subject currentUser = SecurityUtils.getSubject();
        Session session = currentUser.getSession();
        Object o = session.getAttribute("user");
        if (o==null){
            ai.getController().redirect("/login");
        }else{
            try {
                User user = (User)o;
                if(user.getId()<0L){
                    ai.getController().redirect("/login");
                }else{
                    ai.invoke();
                }
            } catch (Exception e) {
                e.printStackTrace();
                ai.getController().redirect("/login");
            }

        }

    }

}
