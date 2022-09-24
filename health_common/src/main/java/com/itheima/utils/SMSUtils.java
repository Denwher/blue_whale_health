package com.itheima.utils;

import com.apistd.uni.Uni;
import com.apistd.uni.UniException;
import com.apistd.uni.UniResponse;
import com.apistd.uni.sms.UniMessage;
import com.apistd.uni.sms.UniSMS;

import java.util.HashMap;
import java.util.Map;

/**
 * 短信发送工具类
 */
public class SMSUtils {
	public static final String VALIDATE_CODE = "SMS_159620392";//发送短信验证码
	public static final String ORDER_NOTICE = "SMS_159771588";//体检预约成功通知

	public static String ACCESS_KEY_ID = "RKRt7ymqU2B2HsoFASRvbfTwioi3XnbW6zSJ783bcDNukdCaW";
	//private static String ACCESS_KEY_SECRET = "your access key secret";

	public static void sendShortMessage(String templateCode,String telephone, String validateCode){
		// 初始化
		Uni.init(ACCESS_KEY_ID); // 若使用简易验签模式仅传入第一个参数即可

		// 设置自定义参数 (变量短信)
		Map<String, String> templateData = new HashMap<String, String>();
		//验证码
		templateData.put("code", validateCode);

		// 构建信息
		UniMessage message = UniSMS.buildMessage()
				.setTo(telephone)
				.setSignature("传智健康")
				.setTemplateId(templateCode)
				.setTemplateData(templateData);

		// 发送短信
		try {
			UniResponse res = message.send();
			System.out.println(res);
		} catch (UniException e) {
			System.out.println("Error: " + e);
			System.out.println("RequestId: " + e.requestId);
		}
	}
}
