package com.test.day01;
import io.restassured.http.ContentType;
import org.testng.annotations.Test;
import org.testng.collections.Maps;

import java.util.Map;

import static io.restassured.RestAssured.*;
/**
 * @author Mr吴
 * @date 2020/12/16  16:03
 */
public class FutureLoanTest {

    @Test
    public void testRegister(){
        Map<String, String> map = Maps.newHashMap();
        map.put("mobile_phone","18515669601");
        map.put("pwd","lemon666");
        given().
                header("X-Lemonban-Media-Type","lemonban.v1").
                body(map).contentType("application/json;charset=utf-8").
        when().
                post("http://api.lemonban.com/futureloan/member/register").
        then().
                log().body();
    }

    @Test
    public void testLogin(){
        Map<String, String> map = Maps.newHashMap();
        map.put("mobile_phone","18515669601");
        map.put("pwd","lemon666");
        given().
                header("X-Lemonban-Media-Type","lemonban.v1").
                body(map).contentType("application/json;charset=utf-8").
        when().
                post("http://api.lemonban.com/futureloan/member/login").
        then().
                log().body();
    }

}
