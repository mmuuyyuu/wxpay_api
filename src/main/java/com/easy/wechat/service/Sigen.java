package com.easy.wechat.service;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

import com.easy.wechat.utils.HttpSender;


public class Sigen {

	public static void main(String[] args) throws UnsupportedEncodingException, Exception {
		Sigen s = new Sigen();
		s.getToken("", "");
		
		
		
	}

	private static String PRIVETE_KEY = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQC4lBJQDt5sISfbSVO7QGTN6PX6G9YKeh4vpvr0vjtv9XCRTBoQi/f8cf40HMt5wXjkBR3sz5JxaGcG0oT8Fs4gVqYeRd/1l89h9xD9he9iKHOqDLfC5aPEhdHFyKqhD83b+vVV6hS+/J/O2ZYi6PV2rz/MBXqVrGCjgB88WUhKQlnHNrQQI6PhiVgtsBLpocGGgMEA/pr6+8FGSxcJ03eQ77CLt4HpgvpeZPz7X0Jx5o8K4McYjR0MjCa0EPd1mpXVspBaILnsr4Fn7Q9JQonobMdd95PR905aLGQF/htwRIel/VkDaaM6dNNyHhB6896iRAQpc9TX2DxhyakO4hm/AgMBAAECggEAf56ZntL5BKhUB3GGvESErj4xvLF8Z5gRwP7iK1BQhlOcdkm5B9HPL7OdgGicY13a+UV0cGUAqvr1qSjrm+UktgVTLEFB3WwOjnymObReVG8FsgDDGbVvaxTferIJD/1+Z2f4M2P63iaLVBjrjs2l0l7PbIApRs19r+6JKk/NBNH24WA8nQb0Sr/Ta4Uy5zc7MTz9w9haQ3UwX+vcaspakID6eqrkestFGihNAoeyp4J57hc3gelUZ+k3C5rlU/hlEhV9rcWeDSdSbGProPsvlrmNocacjRWvVQHLEJuz+LOdxGU/Vek5EI9FEQKciI43gSjd5CI/KiJ7W6b77d5geQKBgQD0MF6gA8d+nQAqZGaYsBSNF/mhbyFPczq7J/FrNiYK67d7/0I8TqZbpvo12CjRukCbsrkCU6ZdlhSLRozxmz2fN1kMhx3Duzff97+geC7oPk/K9y6Uk/jnSK9yHf+qLKpljcJQmfVxkVve+U5Td5MlWwncw114IrURkY3GiFvNGwKBgQDBgZV/d9iMPSazKO/Q5gWHDHplD4n5ZzoLdmL1DCy2nzjmd7dh8e9K38UV7vG8DEF8rVbNwzf8vKSYWBkzY7NeNO+3YvkKwN5qgkKHQeT6xqpb/aIwKeh61oXGXM/V7o+jpTjE1hTC3hsQeyQT+feOZUw/z4fTriDTVZil3GnkLQKBgGrBqtzySgmEwknGU0R11sZIsQ5u83U0v3yLXfStW7xyiWlgk6iiCgEP41wfANmQQCbR4CiwQY+wyZKKaZxHbo1pOTlM6Splc2wreE/Du141v9TQdEDo4GzdTjbp/ph7ppN3gniSlsfr8OpuvFupb19picpGb1rSca8Yn48olln5AoGBALbkOV5u8ZcqvQccq/1vSs4ntw8JrsixDiWDUWh2h1RcDTWW9RZ3ykmkUey3GHc5xjOGycJUvvf7fD4ZFkiB9SgXNHLJ8sR9IjrcvvCEEV4Mozuq775B/d8MHwgq9K0oe06afJteE+VSc5YSLTlMSvSlPXoaNjCWJaRZBJvPRa5JAoGAIoFWCn8Ne5YkSS/dUjj9E2aBedlIQ/SWEtgOjQKW5bztDwMJkLXZYoH6a6VkAt8eHE06ZuTfTvQB+bzAC9cwyozbNxUTSZ17VBLbKwLQmaOooYihBh3cQUcdW2TElsGixeVdTdUlRlRYLndY62BmiCGLROW+ung7sTAm79xIkSA=";
 
	String schema = "WECHATPAY2-SHA256-RSA2048";

	String url = "https://api.mch.weixin.qq.com/v3/apply4subject/applyment/merchants/2088600655503730/state";

	String getToken(String method, String body) throws UnsupportedEncodingException, Exception {

		// 请求随机串
		String nonceStr = buildNonceStr();

		String message = buildGetMessage(method, url, nonceStr, body);

		String signature = sign(message.getBytes("utf-8"));

		StringBuffer authorization = new StringBuffer();
		authorization.append("WECHATPAY2-SHA256-RSA2048 ");
		authorization.append("mchid=1534196751,").append("nonce_str=").append(nonceStr).append(",");
		authorization.append("signature=").append(signature).append(",");

		authorization.append("timestamp=").append(System.currentTimeMillis()).append(",");
		authorization.append("serial_no=1DDE55AD98ED71D6EDD4A4A16996DE7B47773A8C");

	//	String ret = HttpSender.getWithJson(url, authorization.toString());

	//	System.out.println("结果:" + ret);
		return "";
	}

	private String md5(String merchantId) throws Exception {
		// 这个信息不记录在数据库
		// 要求在 64 位以内
		// 872450586415093
		//
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			// 计算md5函数
			md.update(merchantId.getBytes());
			return new BigInteger(1, md.digest()).toString(16).toUpperCase();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			throw new Exception("生成外部商户号失败！");
		}
	}

	String buildNonceStr() throws Exception {

		String nonce_str = "ibuaiVcKdpRxkhJAyuwy";
		// df788f58a6f83ec92e21a70f6fe56442
		String params = "appid=wx2421b1c4370ec43b&mch_id=1534196751&nonce_str=" + nonce_str;

		// 注：key为商户平台设置的密钥key
		String signTemp = params + "&key=df788f58a6f83ec92e21a70f6fe56442";

		// sign=MD5(stringSignTemp).toUpperCase()="9A0A8659F005D6984697E2CA0A9CF3B7"
		// //注：MD5签名方式
		// sign=hash_hmac("sha256",stringSignTemp,key).toUpperCase()="6A9AE1657590FD6257D693A078E1C3E4BB6BA4DC30B23E0EE2496E54170DACD6"
		// //注：HMAC-SHA256签名方式，部分语言的hmac方法生成结果二进制结果，需要调对应函数转化为十六进制字符串。
		return md5(signTemp);

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

	String sign(byte[] message) throws Exception {

		Signature sign = Signature.getInstance("SHA256withRSA");
		PrivateKey key = restorePrivateKey(decode(PRIVETE_KEY));

		sign.initSign(key);
		sign.update(message);

		return Base64.getEncoder().encodeToString(sign.sign());
	}

	String buildGetMessage(String method, String url, String nonceStr, String body) {
		long timestamp = System.currentTimeMillis() / 1000;

		return buildMessage("GET", url, timestamp, nonceStr, "");
	}

	String buildMessage(String method, String url, long timestamp, String nonceStr, String body) {
		// String canonicalUrl = url.encodedPath();
		// if (url.encodedQuery() != null) {
		// canonicalUrl += "?" + url.encodedQuery();
		// }

		return method + "\n" + url + "\n" + timestamp + "\n" + nonceStr + "\n" + body + "\n";
	}

}
