package com.easy.wechat.service;

import org.apache.http.HttpResponse;

import com.easy.wechat.auth.Author;
import com.easy.wechat.httphander.DefaultResponseHandle;
import com.easy.wechat.httphander.ResultHandle;
import com.easy.wechat.utils.HttpSender;

/**
 * 
 * 
 * @title: QueryAuthorization
 * @package: com.minpay.wechat.service
 * @description: 查询商户开户意愿
 * @author: linNaibin
 * @date: 2020-05-09 10:12:00
 * @version V1.0
 */
public class QueryAuthorization implements BaseService {

	private String queryMerchantId;

	private String method;

	private String url;

	private String httpUrl;

	public QueryAuthorization(String queryMerchantId) {
		this.method = queryMerchantId;
		this.method = "GET";
		this.url = "/v3/apply4subject/applyment/merchants/" + queryMerchantId + "/state";

		this.httpUrl = "https://api.mch.weixin.qq.com/v3/apply4subject/applyment/merchants/" + queryMerchantId
				+ "/state";
	}

	public String getQueryMerchantId() {
		return queryMerchantId;
	}

	public void setQueryMerchantId(String queryMerchantId) {
		this.queryMerchantId = queryMerchantId;
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

	public String getHttpUrl() {
		return httpUrl;
	}

	public void setHttpUrl(String httpUrl) {
		this.httpUrl = httpUrl;
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
		
		QueryAuthorization c = new QueryAuthorization("332197988");
		c.executor(new DefaultResponseHandle());

	}

}
