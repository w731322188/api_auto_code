package com.test.day01;

import org.testng.annotations.Test;
import org.testng.collections.Maps;

import java.util.Map;

import static io.restassured.RestAssured.given;

/**
 * @author Mr吴
 * @date 2020/12/16  16:13
 */
public class FutureLoanTokenTest {


    @Test
    public void testLogin(){
        Map<String, String> map = Maps.newHashMap();
        map.put("mobile_phone","18515669601");
        map.put("pwd","lemon666");
        given().
                header("X-Lemonban-Media-Type","lemonban.v2").
                body(map).contentType("application/json;charset=utf-8").
        when().
                post("http://api.lemonban.com/futureloan/member/login").
        then().
                log().body();
    }

}
