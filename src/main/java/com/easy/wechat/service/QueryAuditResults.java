package com.easy.wechat.service;

import org.apache.http.HttpResponse;

import com.easy.wechat.auth.Author;
import com.easy.wechat.httphander.DefaultResponseHandle;
import com.easy.wechat.httphander.ResultHandle;
import com.easy.wechat.utils.HttpSender;

/**
 * 
 * 
 * @title: QueryAuditResults
 * @package: com.minpay.wechat.service
 * @description: 查看审核结果
 * @author: linNaibin
 * @date: 2020-05-09 11:17:09
 * @version V1.0
 */
public class QueryAuditResults implements BaseService {

	private String applyment;

	private String businessCode;

	private String method;

	private String url;

	private String httpUrl;

	public QueryAuditResults(String applyment, String businessCode) {

		if (applyment != null) {
			this.applyment = applyment;
			this.method = "GET";
			this.url = "/v3/apply4subject/applyment?applyment_id=" + applyment;
			this.httpUrl = "https://api.mch.weixin.qq.com/v3/apply4subject/applyment?applyment_id=" + applyment;
		} else {
			this.businessCode = businessCode;
			this.method = "GET";
			this.url = "/v3/apply4subject/applyment?business_code=" + businessCode;
			this.httpUrl = "https://api.mch.weixin.qq.com/v3/apply4subject/applyment?business_code=" + businessCode;
		}

		// https://api.mch.weixin.qq.com/v3/apply4subject/applyment?applyment_id={applyment_id}

		// https://api.mch.weixin.qq.com/v3/apply4subject/applyment?business_code={business_code}

	}

	public String getApplyment() {
		return applyment;
	}

	public void setApplyment(String queryMerchantId) {
		this.applyment = queryMerchantId;
	}

	public String getBusinessCode() {
		return businessCode;
	}

	public void setBusinessCode(String businessCode) {
		this.businessCode = businessCode;
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

		QueryAuditResults c = new QueryAuditResults(null, "287510074164");
		c.executor(new DefaultResponseHandle());

	}

}
