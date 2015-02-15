package com.v246.commonWebFramework.utils.createCode;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jfinal.kit.JsonKit;
import com.jfinal.kit.PathKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Table;
import com.sun.org.apache.bcel.internal.generic.POP;
import com.v246.commonWebFramework.dao.*;
import com.v246.commonWebFramework.utils.AqucyTools;
import com.v246.commonWebFramework.utils.createCode.pojo.ColumnPojo;
import com.v246.commonWebFramework.utils.createCode.pojo.TableConfig;
import freemarker.cache.FileTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by aquaqu on 2015/1/9.
 */
public class EasyUICreateCode implements  ICreateCode{
    @Override
    public String createCode(String jsonString){
        String re = null;
        try {
            AqucyTools tools = new AqucyTools();
            Map<String,Object> root = tools.resolveToRootMap(jsonString);
            if(root.containsKey("errmsg")){
                return root.get("errmsg").toString();
            }
            TableConfig tableConfig = (TableConfig)root.get("tableConfig");
            tools.saveConfigToDb(tableConfig);
            if(tableConfig.getSubViewObjectId()>0){
                List<ViewObjectColumnConfigModel> list = ViewObjectColumnConfigModel.dao.find("SELECT * FROM acyframework_view_object_columns_config where viewObjectId=?", tableConfig.getSubViewObjectId());
                Map mp = new HashMap();
                mp.put("datas",list);
                ViewObjectModel viewModel = ViewObjectModel.dao.findById(tableConfig.getSubViewObjectId());
                mp.put("tableName",viewModel.get("tableName"));
                mp.put("alias",viewModel.get("tableAliasName"));
                mp.put("title",viewModel.get("title"));
                mp.put("add",viewModel.get("allowAdd"));
                mp.put("delete",viewModel.get("allowDelete"));
                mp.put("edit",viewModel.get("allowEdit"));
                String json = null;
                json = JsonKit.toJson(mp);
                json = json.replace("true", "\"是\"");
                json = json.replace("false","\"\"");
                json = json.replace("\"null\"","\"\"");
                json = json.replace(":null",":\"\"");
                tableConfig.setSubTableConfig((TableConfig)(tools.resolveToRootMap(json).get("tableConfig")));
            }
            createView(root);
            createJava(root);
            //官吏入数据库

        }catch (Exception e){
            re = "出错刡常 "+e.getMessage();
            e.printStackTrace();
        }
        return re;
    }

    private boolean createView(Map root) throws Exception{
        StringWriter writer = new StringWriter();
        boolean re = false;
        try {
            String tableName = ((TableConfig)root.get("tableConfig")).getTableName();
            String tableAliasName = ((TableConfig)root.get("tableConfig")).getTableAliasName();
            Configuration config = new Configuration();
            String path = "/WEB-INF/codeTemplate/view/easyui/view.ftl";
            FileTemplateLoader fileLoader = new FileTemplateLoader(new File(PathKit.getWebRootPath()));
            config.setTemplateLoader(fileLoader);
            Template template = config.getTemplate(path, "UTF-8");
            template.process(root, writer);
            String str = writer.toString();
            File file = new File(PathKit.getWebRootPath()+"/WEB-INF/view/project/"+tableAliasName+".html");
            FileOutputStream out = new FileOutputStream(file);
            out.write(str.getBytes());
            out.close();
        }catch (Exception e){
            throw (e);
        }finally {
            try {
                writer.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return re;
    }
    private boolean createJava(Map root) throws Exception{
        StringWriter writer = new StringWriter();
        boolean re = false;
        try {
            TableConfig tableConfig = (TableConfig)root.get("tableConfig");
            String tableName = tableConfig.getTableName();
            String tableAliasName = tableConfig.getTableAliasName();
            String srcPath = PathKit.getWebRootPath().substring(0, PathKit.getWebRootPath().length() - 4);
            Configuration config = new Configuration();
            String path = "/WEB-INF/codeTemplate/java/jfinal/easyui/Controller.java";
            FileTemplateLoader fileLoader = new FileTemplateLoader(new File(PathKit.getWebRootPath()));
            config.setTemplateLoader(fileLoader);
            Template template = config.getTemplate(path, "UTF-8");
            template.process(root, writer);
            String str = writer.toString();
            File file = new File(srcPath+"/src/com/v246/project/controller/"+StrKit.firstCharToUpperCase(tableAliasName)+"Controller.java");
            FileOutputStream out = new FileOutputStream(file);
            out.write(str.getBytes());
            out.close();

            path = "/WEB-INF/codeTemplate/java/jfinal/easyui/Model.java";
            writer = new StringWriter();
           fileLoader = new FileTemplateLoader(new File(PathKit.getWebRootPath()));
            config.setTemplateLoader(fileLoader);
            template = config.getTemplate(path, "UTF-8");
            template.process(root, writer);
            str = writer.toString();
            file = new File(srcPath+"/src/com/v246/project/model/"+StrKit.firstCharToUpperCase(tableAliasName)+"Model.java");
            out = new FileOutputStream(file);
            out.write(str.getBytes());
            out.close();
            //子表model
            if(tableConfig.getSubTableConfig()!=null){
                path = "/WEB-INF/codeTemplate/java/jfinal/easyui/SubModel.java";
                writer = new StringWriter();
                fileLoader = new FileTemplateLoader(new File(PathKit.getWebRootPath()));
                config.setTemplateLoader(fileLoader);
                template = config.getTemplate(path, "UTF-8");
                template.process(root, writer);
                str = writer.toString();
                file = new File(srcPath+"/src/com/v246/project/model/"+StrKit.firstCharToUpperCase(tableConfig.getSubTableConfig().getTableName())+"Model.java");
                out = new FileOutputStream(file);
                out.write(str.getBytes());
                out.close();
            }



            //向jfinal的配置文件中添加router和model
            String jfinalConfigPath = srcPath+"/src/com/v246/commonWebFramework/MyJfinalConfig.java";
            BufferedReader br = new BufferedReader(new FileReader(jfinalConfigPath));
            StringBuffer sb = new StringBuffer(500);
            String data = br.readLine();//一次读入一行，直到读入null为文件结束
            while( data!=null){
                sb.append(data).append("\n");
                data = br.readLine(); //接着读下一行
            }
            String modelClass = StrKit.firstCharToUpperCase(tableName)+"Model.class";
            String modelfullStr = "arpMysql.addMapping(\"{1}\", {2});";
            String  controllerStr = "me.add(\"/project/{1}\", {2}Controller.class);";

            String subModelClass = null;
            String subModelfullStr = "arpMysql.addMapping(\"{1}\", {2});";



            str = sb.toString();
            if(str.indexOf(modelClass)==-1){
                modelfullStr = modelfullStr.replace("{1}",tableName).replace("{2}",modelClass);
                controllerStr = controllerStr.replace("{1}",tableAliasName).replace("{2}",StrKit.firstCharToUpperCase(tableAliasName));
                str = str.replace("//{route}",controllerStr+"\n//{route}");
                str = str.replace("//{model}", modelfullStr + "\n//{model}");
                if(tableConfig.getSubTableConfig()!=null){
                    subModelClass = StrKit.firstCharToUpperCase(tableConfig.getSubTableConfig().getTableName())+"Model.class";
                    if(str.indexOf(subModelClass)!=-1){
                        subModelfullStr = subModelfullStr.replace("{1}",tableConfig.getSubTableConfig().getTableName()).replace("{2}", subModelClass);
                        str = str.replace("//{model}", subModelfullStr + "\n//{model}");
                    }
                }
                file = new File(jfinalConfigPath);
                out = new FileOutputStream(file);
                out.write(str.getBytes());
                out.close();
            }
            //添加菜单
            MenuModel menu =  menu = MenuModel.dao.findFirst("SELECT * FROM acyframework_menu where url=?","/project/"+tableAliasName);
            if(menu==null||menu.getLong("id")<=0){
                menu = new MenuModel();
                menu.set("url", "/project/"+tableAliasName);
                menu.set("text", tableConfig.getTitle());
                menu.set("parentId", 0);
                menu.save();
            }

        }catch (Exception e){
            throw(e);
        }finally {
            try {
                writer.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return re;
    }
}
