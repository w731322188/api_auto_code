package com.test.lemon.data;

/**
 * @author Mr吴
 * @date 2020/12/25  21:02
 */
public class Constants {

    //接口自动化读取的excel文件地址
    public static final String EXCEL_PATH = "src/test/resources/api_testcases_futureloan_v4.xls";

    public static final String MYSQL_CLIENT_URL = "jdbc:mysql://8.129.91.152:3306/futureloan?useUnicode=true&characterEncoding=utf-8";

    public static final String MYSQL_USER_NAME = "future";

    public static final String MYSQL_USER_PWD = "123456";

    public static final String BASE_URL = "http://api.lemonban.com/futureloan";

    //日志开关 true输出到控制台 false输出到文件
    public static final boolean IS_DEBUG = false;

}
