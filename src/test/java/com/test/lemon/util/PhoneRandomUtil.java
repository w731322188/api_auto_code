package com.test.lemon.util;

import java.util.Random;

/**
 * @author Mr吴
 * @date 2020/12/23  21:57
 */
public class PhoneRandomUtil {

    public static String getPhone() {
        StringBuffer mobilePrefix = new StringBuffer("185");
        Random random = new Random();
        for (int i = 0; i < 8; i++) {
            int number = random.nextInt(9);
            mobilePrefix.append(number);
        }
        return mobilePrefix.toString();
    }

    public static String getNoRegisterPhone() {
        while (true) {
            String phone = getPhone();
            Object o = JDBCUtil.querySingle("select count(*) from member where mobile_phone = " + phone);
            if (o instanceof Long && (Long) o == 1){
                System.out.println("手机号码已经被注册过了");
            }else {
                return phone;
            }
        }
    }

    public static void main(String[] args) {
        System.out.println(getNoRegisterPhone());
    }
}
