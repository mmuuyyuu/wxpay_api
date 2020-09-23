package com.easy.wechat.utils;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import com.easy.wechat.constant.Constant;

/**
 * 微信支付API v3要求商户对请求进行签名。微信支付会在收到请求后进行签名的验证。如果签名验证不通过，微信支付API v3将会拒绝处理请求，并返回401
 * Unauthorized。
 * 
 * @title: SecurityUtils
 * @package: org.wxpay.utils
 * @description: TODO
 * @author: linNaibin
 * @date: 2020-05-08 10:46:25
 * @version V1.0
 */
public class SecurityUtils {

	private static final String SYMBOLS = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

	private static final SecureRandom RANDOM = new SecureRandom();

	/**
	 * 随机生成字符串
	 * 
	 * @return
	 */
	public static String generateNonceStr() {
		char[] nonceChars = new char[32];
		for (int index = 0; index < nonceChars.length; ++index) {
			nonceChars[index] = SYMBOLS.charAt(RANDOM.nextInt(SYMBOLS.length()));
		}
		return new String(nonceChars);
	}

	/**
	 * 获取时间戳
	 * 
	 * @return
	 */
	public static long generateTimestamp() {
		return System.currentTimeMillis() / 1000;
	}

	/**
	 * 构造签名串 <br>
	 * 如果商户构造签名串的方式错误，将导致签名验证不通过。<br>
	 * 
	 * @param http     请求方式 get、post
	 * @param 请求的绝对URL
	 * @param body
	 * @return
	 */
	public static String buildSignatureString(String nonce, String method, String url, String body, long timestamp) {
		// such as
		String requestSignStr = method + "\n" + url + "\n" + generateTimestamp() + "\n" + nonce + "\n" + body + "\n";

		return requestSignStr;

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

	public static String sign(byte[] message) {

		try {

			Signature sign = Signature.getInstance("SHA256withRSA");

			PrivateKey priKey = restorePrivateKey(decode(Constant.PRIVATE_KEY));

			sign.initSign(priKey);

			sign.update(message);

			return Base64.getEncoder().encodeToString(sign.sign());
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("当前Java环境不支持SHA256withRSA", e);
		} catch (SignatureException e) {
			throw new RuntimeException("签名计算失败", e);
		} catch (InvalidKeyException e) {
			throw new RuntimeException("无效的私钥", e);
		}
	}

	public static String getToken(String merchantId, String seriNo, String method, String url, String body)
			throws IOException {
		String nonceStr = generateNonceStr();
		System.out.println("随机字符串：" + nonceStr);

		long timestamp = generateTimestamp();
		System.out.println("时间戳：" + timestamp);

		String message = buildSignatureString(nonceStr, method, url, body, timestamp);

		String signature = sign(message.getBytes("utf-8"));

		String token = "WECHATPAY2-SHA256-RSA2048 mchid=\"" + Constant.MCH_ID + "\"," + "nonce_str=\"" + nonceStr
				+ "\"," + "timestamp=\"" + timestamp + "\"," + "serial_no=\"" + Constant.SERIAL_NO + "\","
				+ "signature=\"" + signature + "\"";
		System.out.println("authorization token :");

		return token;
	}

	/**
	 * 敏感信息加密
	 * 
	 * @param message
	 * @param certificate
	 * @return
	 * @throws IllegalBlockSizeException
	 * @throws IOException
	 */
	public static String rsaEncryptOAEP(String message, String publicKey) throws Exception {
		return rsaEncryptOAEP(message, getPublicKey(publicKey));
	}

	/**
	 * 敏感信息加密
	 * 
	 * @param message
	 * @param certificate
	 * @return
	 * @throws IllegalBlockSizeException
	 * @throws IOException
	 */
	public static String rsaEncryptOAEP(String message, PublicKey publicKey)
			throws IllegalBlockSizeException, IOException {
		try {

			Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-1AndMGF1Padding");

			cipher.init(Cipher.ENCRYPT_MODE, publicKey);

			byte[] data = message.getBytes("utf-8");
			byte[] cipherdata = cipher.doFinal(data);
			return Base64.getEncoder().encodeToString(cipherdata);
		} catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
			throw new RuntimeException("当前Java环境不支持RSA v1.5/OAEP", e);
		} catch (InvalidKeyException e) {
			throw new IllegalArgumentException("无效的证书", e);
		} catch (IllegalBlockSizeException | BadPaddingException e) {
			throw new IllegalBlockSizeException("加密原串的长度不能超过214字节");
		}
	}

	public static String rsaDecryptOAEP(String ciphertext, PrivateKey privateKey)
			throws BadPaddingException, IOException {
		try {
			Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-1AndMGF1Padding");
			cipher.init(Cipher.DECRYPT_MODE, privateKey);

			byte[] data = Base64.getDecoder().decode(ciphertext);
			return new String(cipher.doFinal(data), "utf-8");
		} catch (NoSuchPaddingException | NoSuchAlgorithmException e) {
			throw new RuntimeException("当前Java环境不支持RSA v1.5/OAEP", e);
		} catch (InvalidKeyException e) {
			throw new IllegalArgumentException("无效的私钥", e);
		} catch (BadPaddingException | IllegalBlockSizeException e) {
			throw new BadPaddingException("解密失败");
		}
	}

	public static PublicKey getPublicKey(String key) throws Exception {
		byte[] keyBytes = Base64.getDecoder().decode(key);
		X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		PublicKey publicKey = keyFactory.generatePublic(keySpec);
		return publicKey;
	}

	/**
	 * byte数组转换为16进制字符串
	 *
	 * @param bts 数据源
	 * @return 16进制字符串
	 */
	public static String bytes2Hex(byte[] bts) {
		String des = "";
		String tmp = null;
		for (int i = 0; i < bts.length; i++) {
			tmp = (Integer.toHexString(bts[i] & 0xFF));
			if (tmp.length() == 1) {
				des += "0";
			}
			des += tmp;
		}
		return des;
	}

	/**
	 * 利用java原生的摘要实现SHA256加密
	 * 
	 * @param str 加密后的报文
	 * @return
	 */
	public static String getSHA256String(byte[] b) {
		MessageDigest messageDigest;
		String encodeStr = "";
		try {
			messageDigest = MessageDigest.getInstance("SHA-256");
			messageDigest.update(b);
			encodeStr = bytes2Hex(messageDigest.digest());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return encodeStr;
	}

	static final String CIPHERTEXT = "MpDhQA/1FHyJ/NQB2klNl6kAjyq3y6GT4rq/RfEc9Q/W01lwSHTFObrGdw+vprhpUQqkj2ZE37cL/ZfWcXysKdd9Z4cQslaLwnSWFn9gwrpnR83KSG6cnDwQrjlhOcyfQWWZbigN0BFsAqUCV5JUFmhho/eCvBptaqHHmTyUuJ+TDuJkgO0rbAVGlRJ7uqf66vsoPFMJKxGQ37yEA3Oi8/kLFFE8mZ4tnApYrNOJCPSh5lHfyI0XxKPQEVFrSV4/cutNmoq3lUErx7r1pgNoWwPFtU7L2G8p1xNpZrnN+lKHHohRiz+e3mfMVQHDOMIAhJWAlyxSos2Yfhx6feKbpgFpmEqH5bDT8aubqnsTO0JZjlQFZzQxbxRIHCssYJ0wcs8xR9cLRQKPZZIKEerRs0xI7+j15/6SaG1RS/Q6JYTiruOTYUGRrOGbJurQKzC+ABkLQgI+HDTnGibm0XLN1QsHcLrdI97s8THWoo+g/NntUBV4tFi4x2tffcsPaZQOhG/UyQKEwc3yA5NbpWWTIgkMdZOgtVAbeYyNysoZWzhTs42zMLKhpDGVoCdQcTch5oCRepnub31dtzlyxfz6d/HpMElA2qpwdLxy2bVhlzKwcqPejAQumx0410bzbpits0pbZlue4iH09YgDA27+8L/nOM+ph871xyw368ZIEXZDmoBNOJ/MS2z7lLxqkXPaA0FyA/JkHOSKhG+eNmNw1thIVQCWCSbS2TA1FyAuxDZrZngTFnNXsvG6URwiiZls6i2AekoLNmhiULCxkzwYeMxwkPLnyaEZU2jQz3TFJxfvsUuikwJoJXP3knOGelh8GHzG+Is/UP2sKwgOYeE2eFtO27PiEDJmEY+RocyAfmUGpWnodyRC0vYvxs2SqYBQqD9LXAnLKokLmiO4p990Cu0IEHjyu+375n+4SfD0gzyKj3l4rlL+4nZAOLngTXWhcdweNmqvvvMjCC4uNZonms/I6IUlbY8K77F6dqaQf0N7TsM9tuIUX3TCwZR6iil54qfO71e6AKhQm3ELJLHrHT4rtzgyJT5hP0JULjtwJ1xIYGToEqDbrMoJkMZKSTaa2L5IS5YmhR9B1IT7Fho0Zjx6PM0j0h8DsR5D9AxoIEHuzNqWejfbFW4kDkFoK+0kmyNixyFx5JEjrZYIOv0mH3GS1+TDix5NViniidXNPm0x/4R1BSgEeYqyJA4dvIwlM5jN7vk4ayS2SrckqvqggSDnjVAShWJnNLKmEMRC1Xchma3IWvjXAKgDWu4mXcv6Hu5rDf2a2R9dEa93XZnQ73BnlO0nykvefDWNgrN/mRL/R/0/LmWJpkqvHf4GusvcRsh/rYsKvWSMA+Ct3WMJPCZgxHm72L7BotC+H66r1i3mojyc1WevXek2iFEo+HNrxJigjMsX36n7fmuDRYQtL3cmiBoh2Z99hwetMaQUsIVfqwcysf2xmI7iLIc5iVmRs6GxBCALRfrU3WasPvAwgh9o+3hCDw3yZFDzZ3GNlBT5QA6i0YE0JqLoOYn9mLfBozR/4CnJT82EXEARfLwfVvbt4GjXCuXgc2r1ClIBHmEmWHAGKuHBITJZ9odMydQThw+XgMb9zRmzYXLPOEJIALZpMOq3OESRBKg7RAYzlIPKNLrZgI6+Jg7e+v/0e9eKQOiAy/YZKIksqjpbVYMbXDaDis+MM4DKB+nN2T23hA4IZ3LMNN28+QPebcGfgsrXLeWwSC0yDfKGMUkhfM65tZ6z8U+5TJ/PpamfB0qmkRVVn3gT373HzwiUMhJ/vwfjj+KTsMo5O2zOgV4hAWx7n58ESiK9xnRjaFIyv5lfA9v4+9jH9lyvFZTYZcvnL3GE6IQ1PttHDhS0hAASGcs6mwas1qP4er9GjLJ7D+zkKDo1KqRNousu2q6lq20AOXALIlaW";

	public static void main(String[] args) throws BadPaddingException, IOException {

		String k = "c1NTPwibH7stQXnqGUjiXT7XYH8NDKAB";

		System.out.println(k.length());
		
		PrivateKey priKey = restorePrivateKey(decode(Constant.PRIVATE_KEY));

		String re = rsaDecryptOAEP(CIPHERTEXT, priKey);
		System.out.println(re);
	}
}
