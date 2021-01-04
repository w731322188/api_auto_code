package com.test.day01;

import io.restassured.response.Response;
import org.testng.annotations.Test;
import org.testng.collections.Maps;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;

/**
 * @author Mr吴
 * @date 2020/12/16  16:19
 */
public class HomeWork {

    @Test
    public void test01(){
        //注册
        Map<String, String> registerMap = new HashMap<>();
        registerMap.put("mobile_phone","18515669621");
        registerMap.put("pwd","lemon666");
        registerMap.put("type","1");
        given().
                header("X-Lemonban-Media-Type","lemonban.v2").
                body(registerMap).contentType("application/json;charset=utf-8").
        when().
                post("http://api.lemonban.com/futureloan/member/register").
        then().
                log().body();


        //登录
        Response res =
        given().
                header("X-Lemonban-Media-Type","lemonban.v2").
                body(registerMap).contentType("application/json;charset=utf-8").
        when().
                post("http://api.lemonban.com/futureloan/member/login").
        then().
                extract().response();
        String token = res.path("data.token_info.token");
        Integer memberId = res.path("data.id");


        //充值
        Map<String, Object> payMap = new HashMap<>();
        payMap.put("member_id", memberId);
        payMap.put("amount", 200000.0);
        given().
                header("X-Lemonban-Media-Type","lemonban.v2").
                header("Authorization","Bearer " + token).
                body(payMap).contentType("application/json;charset=utf-8").
        when().
                post("http://api.lemonban.com/futureloan/member/recharge").
        then().
                log().body();


        //新增项目
        Map<String, Object> projectMap = new HashMap<>();
        projectMap.put("member_id", memberId);
        projectMap.put("title", "接口自动化测试12-15-test");
        projectMap.put("amount", 15000);
        projectMap.put("loan_rate", 1.2);
        projectMap.put("loan_term", 5);
        projectMap.put("loan_date_type", 1);
        projectMap.put("bidding_days", 6);
        given().
                header("X-Lemonban-Media-Type","lemonban.v2").
                header("Authorization","Bearer " + token).
                body(projectMap).contentType("application/json;charset=utf-8").
        when().
                post("http://api.lemonban.com/futureloan/loan/add").
        then().
                log().body();


        //投资
        Map<String, Object> investMap = new HashMap<>();
        investMap.put("member_id", memberId);
        investMap.put("loan_id", 2);
        investMap.put("amount", 20000);
        given().
                header("X-Lemonban-Media-Type","lemonban.v2").
                header("Authorization","Bearer " + token).
                body(investMap).contentType("application/json;charset=utf-8").
        when().
                post("http://api.lemonban.com/futureloan/member/invest").
        then().
                log().body();

    }

}
