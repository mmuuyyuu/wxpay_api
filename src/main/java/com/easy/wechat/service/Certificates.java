package com.easy.wechat.service;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.http.HttpResponse;

import com.easy.wechat.auth.Author;
import com.easy.wechat.httphander.DefaultResponseHandle;
import com.easy.wechat.httphander.ResultHandle;
import com.easy.wechat.utils.HttpSender;

/**
 * 
 * 
 * @title: Certificates
 * @package: org.wxpay.service
 * @description: 获取微信平台证书
 * @author: linNaibin
 * @date: 2020-05-07 16:36:47
 * @version V1.0
 */
public class Certificates implements BaseService {

	private String method;

	private String url;

	private String httpUrl;

	public Certificates() {
		this.method = "GET";
		this.url = "/v3/certificates";
		this.httpUrl = "https://api.mch.weixin.qq.com/v3/certificates";
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

	@Override
	public void executor(ResultHandle h) {

		// get token
		Author au = new Author(method, url);
		String token = au.getToken("");


		// 发送
		try {
			HttpResponse result = HttpSender.doGetJson(httpUrl, token);
			String ret = h.handle(result);

			System.out.println(ret);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws Exception {
		Certificates c = new Certificates();
		c.executor(new DefaultResponseHandle());

	}

}
