package com.test.day02;

import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;

/**
 * @author Mr吴
 * @date 2020/12/16  22:36
 */
public class CookieTest {
    Map<String, String> cookieMap = new HashMap<>();

    @Test
    public void testAuthenticationWithSession(){
        Response res =
                given().
                        header("Content-Type","application/x-www-form-urlencoded; charset=UTF-8").
                        header("X-Lemonban-Media-Type","lemonban.v2").
                        formParam("loginame","admin").formParam("password","e10adc3949ba59abbe56e057f20f883e").
                when().
                        post("http://erp.lemfix.com/user/login").
                then().
                        log().all().extract().response();
        cookieMap = res.getCookies();
    }

    @Test
    public void testGetUserSession(){

        //getUserSession接口请求必须要携带cookie里边保存的sessionId
        given().
                cookies(cookieMap).
        when().
                get("http://erp.lemfix.com/user/getUserSession").
        then().
                log().all();

    }
}
