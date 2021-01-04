package com.test.day01;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.*;

/**
 * @author Mr吴
 * @date 2020/12/16  15:37
 */
public class PostApiTest {

    /**
     * post请求form表单传参
     */
    @Test
    public void postTest01(){
        given().
                formParam("mobile_phone","13323234545").
                formParam("pwd","234545").
        when().
                post("http://httpbin.org/post").
        then().
                log().all();
    }

    /**
     * post请求json传参
     */
    @Test
    public void postTest02(){
        String jsonData = "{\"mobilephone\":\"13323234545\",\"password\":\"234545\"}";
        given().
                body(jsonData).contentType("application/json").
        when().
                post("http://httpbin.org/post").
        then().
                log().all();
    }

    /**
     * post请求xml传参
     */
    @Test
    public void postTest03(){
        String xmlStr = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                "<suite>\n" +
                "  <class>测试xml</class>\n" +
                "</suite>";
        given().
                body(xmlStr).contentType("text/xml").
        when().
                post("http://httpbin.org/post").
        then().
                log().all();
    }

    /**
     * post请求上传文件
     */
    @Test
    public void postTest04(){
        File file = new File("C:\\Users\\Mr吴\\Desktop\\新建文本文档.xml");
        given().
                multiPart(file).
        when().
                post("http://httpbin.org/post").
        then().
                log().all();
    }

    /**
     * post请求json传参2
     */
    @Test
    public void postTest05(){
        Map<String, String> map = new HashMap<>();
        map.put("mobile_phone","13323234545");
        map.put("pwd","234545");
        given().
                body(map).contentType("application/json").
        when().
                post("http://httpbin.org/post").
        then().
                log().body();
    }

    /**
     * post请求获取响应结果
     */
    @Test
    public void postTest06(){
        Map<String, String> map = new HashMap<>();
        map.put("mobile_phone","13323234545");
        map.put("pwd","234545");
       Response response =
       given().
                body(map).contentType("application/json;charset=utf-8").
        when().
                post("http://httpbin.org/post").
        then().
               extract().response();

        System.out.println("response 耗时：" + response.time());
        System.out.println("获取响应头token结果：" + response.getHeader("token"));
        String path = response.path("json.pwd");
        System.out.println("GPath获取json对象的pwd属性：" + path);
    }
}
