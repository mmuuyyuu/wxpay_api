package com.easy.wechat.auth;

import java.io.IOException;

import com.easy.wechat.constant.Constant;
import com.easy.wechat.utils.SecurityUtils;


/**
 * 
 * 
 * @title: Author
 * @package: com.minpay.wechat.auth
 * @description: 授权
 * @author: linNaibin
 * @date: 2020-05-08 16:35:34
 * @version V1.0
 */
public class Author {

	private String merchantId;

	private String seriNo;

	private String method;

	private String url;

	private String token;

	public Author(String method, String url) {
		this.merchantId = Constant.MCH_ID;
		this.seriNo = Constant.SERIAL_NO;
		this.method = method;
		this.url = url;
	}

	public String getMerchantId() {
		return merchantId;
	}

	public void setMerchantId(String merchantId) {
		this.merchantId = merchantId;
	}

	public String getSeriNo() {
		return seriNo;
	}

	public void setSeriNo(String seriNo) {
		this.seriNo = seriNo;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getToken(String body) {
		// 获取
		try {
			setToken(SecurityUtils.getToken(merchantId, seriNo, method, url, body));
		} catch (IOException e) {
			e.printStackTrace();
		}

		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

}
