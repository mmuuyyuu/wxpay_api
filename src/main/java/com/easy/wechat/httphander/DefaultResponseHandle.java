package com.easy.wechat.httphander;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.util.EntityUtils;

import com.alibaba.fastjson.JSONObject;

/**
 * 
 * 
 * @title: GetResponeHandle
 * @package: com.minpay.wechat.service
 * @description: 微信get请求处理
 * @author: linNaibin
 * @date: 2020-05-08 17:10:09
 * @version V1.0
 */
public class DefaultResponseHandle implements ResultHandle {

	@Override
	public String handle(HttpResponse response) {

		Header[] requestId = response.getHeaders("Request-ID");

		final StatusLine statusLine = response.getStatusLine();

		final HttpEntity entity = response.getEntity();

		JSONObject ret = null;

		try {

			String retStr = EntityUtils.toString(entity);

			if (retStr == null || retStr.equals("")) {
				ret = new JSONObject();
			} else {
				ret = JSONObject.parseObject(retStr);

			}

			ret.put("request_id", requestId[0].getValue());

			// entity.
		} catch (Exception e) {

			ret = new JSONObject();
			try {
				ret.put("request_id", requestId[0].getValue());
			} catch (Exception e1) {

			}
		}

		if (statusLine.getStatusCode() >= 300) {

			ret.put("httpcode", statusLine.getStatusCode());
			ret.put("httpmsg", statusLine.getReasonPhrase());

		} else {
			ret.put("httpcode", statusLine.getStatusCode());
		}

		return ret.toString();
	}

}
