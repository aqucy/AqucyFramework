package com.v246.commonWebFramework.utils.createCode;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jfinal.kit.PathKit;
import com.jfinal.kit.StrKit;
import com.v246.commonWebFramework.dao.DictionaryModel;
import com.v246.commonWebFramework.dao.MenuModel;
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
public class Extjs2CreateCode implements  ICreateCode{
    @Override
    public boolean createCode(String jsonString){
        try {
            Map root = new HashMap();
            Map data = new HashMap();
            StringBuffer columnString = new StringBuffer(100);
            StringBuffer displayColumnString = new StringBuffer(100);
            ObjectMapper om = new ObjectMapper();
            JsonNode jo = om.readTree(jsonString.trim());
            List<ColumnPojo> list = new ArrayList<ColumnPojo>();
            TableConfig tableConfig = new TableConfig();
            Map<String, List<DictionaryModel>> dicts = new HashMap<String, List<DictionaryModel>>();
            if (jo.has("datas")) {
                int maxDisplayLen = 0;
                int maxQueryLen = 0;
                JsonNode arr = jo.path("datas");
                for (int i = 0; i < arr.size(); i++) {
                    ColumnPojo columnPojo = new ColumnPojo();
                    JsonNode myjo = arr.path(i);
                    if (myjo.has("displayName")) {
                        String displayName = myjo.path("displayName").asText();
                        columnPojo.setDisplayName(displayName);
                        if(StrKit.notBlank(displayName)){
                            if(displayName.length()>maxDisplayLen){
                                maxDisplayLen = displayName.length();
                            }
                        }
                    }
                    if (myjo.has("columnName")) {
                        columnPojo.setColumnName(myjo.path("columnName").asText());
                        columnString.append("'").append(columnPojo.getColumnName()).append("',");
                    }
                    if (myjo.has("dictCode")) {
                        String dictCode = myjo.path("dictCode").asText();
                        if (StrKit.notBlank(dictCode)) {
                            dicts.put(dictCode, DictionaryModel.dao.find("SELECT k label,v value FROM acyFramework_dictionary where code=?", dictCode));
                            data.put(dictCode, columnPojo.getColumnName());
                        }
                        columnPojo.setDictCode(dictCode);
                    }
                    if (myjo.has("reg")) {
                        columnPojo.setRegex(myjo.path("reg").asText());
                    }
                    if (myjo.has("required")) {
                        columnPojo.setRequired(myjo.path("required").asBoolean());
                    }
                    if (myjo.has("formType")) {
                        columnPojo.setFormType(myjo.path("formType").asText());
                    }
                    if (myjo.has("regErrMsg")) {
                        columnPojo.setRegexErrorMsg(myjo.path("regErrMsg").asText());
                    }
                    if (myjo.has("allowQuery")) {
                        boolean allowQuery = myjo.path("allowQuery").asBoolean();
                        if(StrKit.isBlank(columnPojo.getDisplayName())){
                            allowQuery = false;
                        }
                        if(allowQuery&&columnPojo.getDisplayName().length()>maxQueryLen){
                            maxQueryLen = columnPojo.getDisplayName().length();
                        }
                        columnPojo.setAllowQuery(allowQuery);
                    }
                    list.add(columnPojo);
                }
                tableConfig.setColumnPojos(list);
                tableConfig.setDicts(dicts);
                tableConfig.setMaxDisplayColumnLen(++maxDisplayLen);
                tableConfig.setMaxQueryColumnLen(++maxQueryLen);
            }
            if (jo.has("tableName")) {
                tableConfig.setTableName(jo.path("tableName").asText());
            }
            columnString.deleteCharAt(columnString.length()-1);
            data.put("columnStr", columnString);
            root.put("acy", data);
            root.put("tableConfig",tableConfig);
            createView(root);
            createJava(root);
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    private boolean createView(Map root){
        StringWriter writer = new StringWriter();
        boolean re = false;
        try {
            String tableName = ((TableConfig)root.get("tableConfig")).getTableName();
            Configuration config = new Configuration();
            String path = "/WEB-INF/codeTemplate/view/extjs2/view.ftl";
            FileTemplateLoader fileLoader = new FileTemplateLoader(new File(PathKit.getWebRootPath()));
            config.setTemplateLoader(fileLoader);
            Template template = config.getTemplate(path, "UTF-8");
            template.process(root, writer);
            String str = writer.toString();
            File file = new File(PathKit.getWebRootPath()+"/WEB-INF/view/project/"+tableName+".html");
            FileOutputStream out = new FileOutputStream(file);
            out.write(str.getBytes());
            out.close();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try {
                writer.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return re;
    }
    private boolean createJava(Map root){
        StringWriter writer = new StringWriter();
        boolean re = false;
        try {
            String tableName = ((TableConfig)root.get("tableConfig")).getTableName();
            String srcPath = PathKit.getWebRootPath().substring(0, PathKit.getWebRootPath().length() - 4);
            Configuration config = new Configuration();
            String path = "/WEB-INF/codeTemplate/java/jfinal/Controller.java";
            FileTemplateLoader fileLoader = new FileTemplateLoader(new File(PathKit.getWebRootPath()));
            config.setTemplateLoader(fileLoader);
            Template template = config.getTemplate(path, "UTF-8");
            template.process(root, writer);
            String str = writer.toString();
            File file = new File(srcPath+"/src/com/v246/project/controller/"+StrKit.firstCharToUpperCase(tableName)+"Controller.java");
            FileOutputStream out = new FileOutputStream(file);
            out.write(str.getBytes());
            out.close();

            path = "/WEB-INF/codeTemplate/java/jfinal/Model.java";
            writer = new StringWriter();
           fileLoader = new FileTemplateLoader(new File(PathKit.getWebRootPath()));
            config.setTemplateLoader(fileLoader);
            template = config.getTemplate(path, "UTF-8");
            template.process(root, writer);
            str = writer.toString();
            file = new File(srcPath+"/src/com/v246/project/model/"+StrKit.firstCharToUpperCase(tableName)+"Model.java");
            out = new FileOutputStream(file);
            out.write(str.getBytes());
            out.close();

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
            str = sb.toString();
            if(str.indexOf(modelClass)==-1){
                modelfullStr = modelfullStr.replace("{1}",tableName).replace("{2}",modelClass);
                controllerStr = controllerStr.replace("{1}",tableName).replace("{2}",StrKit.firstCharToUpperCase(tableName));
                str = str.replace("//{route}",controllerStr+"\n//{route}");
                str = str.replace("//{model}", modelfullStr + "\n//{model}");
                file = new File(jfinalConfigPath);
                out = new FileOutputStream(file);
                out.write(str.getBytes());
                out.close();
                //添加菜单
                MenuModel menu = new MenuModel();
                menu.set("url", "/project/"+tableName);
                menu.set("text", tableName);
                menu.set("parentId", 0);
                menu.save();
            }

        }catch (Exception e){
            e.printStackTrace();
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
