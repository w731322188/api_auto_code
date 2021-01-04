package com.test.day01;

import org.testng.annotations.Test;

import static io.restassured.RestAssured.*;


/**
 * @author Mr吴
 * @date 2020/12/16  15:26
 */
public class GetApiTest {

    @Test
    public void testGet01(){
        given().
        when().
                get("http://httpbin.org/get?phone=13323234545&password=123456").
        then().
                log().all();
    }


    @Test
    public void testGet02(){
//        given().
//                queryParam("name","张三").
//                queryParam("sex","男").
//        when().
//                get("http://httpbin.org/get").
//        then().
//                log().all();

        given().
                queryParam("name","张三").
                queryParam("sex","男").
                when().
                get("http://httpbin.org/get").
                then().
                log().body();
    }


}
