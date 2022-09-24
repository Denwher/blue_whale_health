package com.itheima.test;

import com.itheima.utils.SMSUtils;
import com.itheima.utils.ValidateCodeUtils;
import org.junit.Test;

/**
 * @author Denwher
 * @version 1.0
 */
public class SMSUtilsTest {
    @Test
    public void test(){
        //生成验证码
        Integer validateCode = ValidateCodeUtils.generateValidateCode(6);
        SMSUtils.sendShortMessage(SMSUtils.VALIDATE_CODE,"13679902333",validateCode.toString());
    }
}