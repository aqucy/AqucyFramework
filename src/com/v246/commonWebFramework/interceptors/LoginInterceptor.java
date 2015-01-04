package com.v246.commonWebFramework.interceptors;

import com.jfinal.aop.Interceptor;
import com.jfinal.core.ActionInvocation;
import com.jfinal.kit.PathKit;
import com.v246.commonWebFramework.dao.MenuModel;
import freemarker.cache.FileTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;

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
        if(ai.getActionKey().equalsIgnoreCase("/aquaqu/login")||ai.getActionKey().equalsIgnoreCase("/aquaqu/loginCheck")){
//            System.out.println("放行==="+ai.getActionKey());
            ai.invoke();

            return;
        }
        Object o = ai.getController().getSessionAttr("user");
        if (o==null){
            ai.getController().render("/aquaqu/login.html");
        }else{
            try {
                MenuModel model = (MenuModel)o;
                if(model.get("id")==null){
                    ai.getController().render("/aquaqu/login.html");
                }else{
                    ai.getController().setAttr("menu",getLeftMenu());
                    ai.invoke();
                }
            } catch (Exception e) {
                e.printStackTrace();
                ai.getController().render("/aquaqu/login.html");
            }

        }

    }
    protected  String getLeftMenu(){
        StringWriter writer = new StringWriter();
        String re = null;
        try {
            Configuration config = new Configuration();
            String path = "/aquaqu/leftMenu.html";
            FileTemplateLoader fileLoader = new FileTemplateLoader(new File(PathKit.getWebRootPath()));
            config.setTemplateLoader(fileLoader);
            Template template = config.getTemplate(path,"UTF-8");
            template.process(null,writer);
            re = writer.toString();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try {
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return  re;
    }
}
