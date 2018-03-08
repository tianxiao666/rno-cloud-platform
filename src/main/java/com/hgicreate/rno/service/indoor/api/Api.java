package com.hgicreate.rno.service.indoor.api;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * @author chao.xj
 */
public class Api {

    /**
     * 线程安全
     */
    private static Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    protected String createRenderTemplate(int isSuccess, String content, String message) {
        content = gson.toJson(content);
        message = gson.toJson(message);
        String jsonStr = "{\"success\":" + isSuccess + ",\"content\":" + content + ",\"message\":" + message + "}";
        return jsonStr;
    }

    public String renderSuccessJson(Object content,boolean flag) {
        String rederData = "";
        if (flag){
            String json = gson.toJson(content);
            rederData = createRenderTemplate(1, json, "");
        }else {
            rederData = createRenderTemplate(1, content.toString(), "");
        }
        return rederData;
    }

    public String renderErrorJson(String message) {
        String rederData = createRenderTemplate(0, "", message);
        return rederData;
    }

    public String objectToJson(Object object){
        return gson.toJson(object);
    }

    public static void main(String[] arg){

    }
}
