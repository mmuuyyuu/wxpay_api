package com.easy.wechat.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;

public class SignUtils {

	public static byte[] encode(byte[] src) {
		return Base64.getEncoder().encode(src);
	}

	public static byte[] decode(String src) {

		return Base64.getDecoder().decode(src);
	}

	public static PrivateKey restorePrivateKey(byte[] keyBytes) {
		PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(keyBytes);
		try {
			KeyFactory factory = KeyFactory.getInstance("RSA");
			return factory.generatePrivate(pkcs8EncodedKeySpec);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String sign(String priKey, String content) throws Exception {
		PrivateKey key = restorePrivateKey(decode(priKey));
		Signature signature = Signature.getInstance("SHA256withRSA");
		signature.initSign(key);
		signature.update(content.getBytes("UTF-8"));
		byte[] signed = signature.sign();
		String sign = new String(encode(signed), "UTF-8");
		return sign;
	}

	public static String urlEncoder(JSONObject requestParam) {

		StringBuffer sf = new StringBuffer("");

		if ((requestParam != null) && (requestParam.size() != 0)) {

			Iterator<Map.Entry<String, Object>> iter = requestParam.entrySet().iterator();

			while (iter.hasNext()) {
				try {
					Map.Entry<String, Object> entry = iter.next();
					if (entry.getValue() == null) {
						continue;
					}

					sf.append(entry.getKey()).append("=")
							.append(URLEncoder.encode(String.valueOf(entry.getValue()), "utf-8")).append("&");

				} catch (UnsupportedEncodingException e) {
					continue;
				}
			}

			sf.substring(0, sf.length() - 1);
		}
		return sf.toString();

	}

	public static String signData(JSONObject data) {

		List<String> keys = new ArrayList<String>(data.keySet());

		Collections.sort(keys);

		StringBuffer param = new StringBuffer();

		for (String key : keys) {
			Object v = data.get(key);
			if (v == null) {
				continue;
			}
			param.append(key).append("=").append(v).append("&");

		}

		return param.substring(0, param.length() - 1).toString();
	}

	public static PublicKey restorePublicKey(byte[] keyBytes) {
		X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(keyBytes);
		try {
			KeyFactory factory = KeyFactory.getInstance("RSA");
			return factory.generatePublic(x509EncodedKeySpec);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static boolean rsa256CheckContent(String sign, String content) throws Exception {

		try {

			PublicKey pubKey = restorePublicKey("".getBytes());

			Signature signature = Signature.getInstance("SHA256withRSA");
			signature.initVerify(pubKey);
			signature.update(content.getBytes("UTF-8"));

			boolean result = signature.verify(decode(sign));
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public static void main(String[] args) throws Exception {

		String result = "{\"ant_merchant_expand_indirect_query_response\":{\"msg\":\"非法的参数\",\"code\":\"40002\",\"sub_msg\":\"验签失败 [UP0210301]\",\"sub_code\":\"up.invalid-signature\"},\"sign\":\"bc7a3mSwbLBTv4cdoyyfOOh6ocC00POWgh1Xvk/NoYsqDAE/KcnboDjLlb3js6Y6QxMSTE+PuZ1yjbCUhnan7ODWzwdBCPBs8+ctqCrkznh9US1PkH4RUEVPw4OsP9OGaG0re8NWg+4GAGHXwlY6jPTl5NiCjggSO7gHzR/8cOaL7gOmLGsAJVtWdqJ9HA4aLBqCHSJRHVhxzCM5LWD/wyrLgFQDoABK6bPSFevK7oCZmzTJ8hNugqsaQU4exyUOmdlDDLx19p5YETzk4XX5RxxCUxoWZLRUV0ZNXYPcQJ2BWI/s98luHiwYwydNwUw4IY2kFUFLxExxUZvIdawbIg==\"}";
		JSONObject json = JSONObject.parseObject(result);

		String sign = json.getString("sign");

		String methodName = "ant.merchant.expand.indirect.query";

		methodName = methodName.replace(".", "_");
		Object value = json.get(methodName + "_response");
		String rstStr = value != null ? value.toString() : json.getString("error_response");
		boolean is = rsa256CheckContent(sign, rstStr);

		System.out.println(is);

	}
}
