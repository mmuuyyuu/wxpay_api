package com.easy.wechat.utils;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.alibaba.fastjson.JSONObject;
import com.easy.wechat.constant.Constant;

/**
 * 
 * 
 * @title: HttpSender
 * @package: org.easy.commons.http
 * @description: TODO
 * @author: linNaibin
 * @date: 2020-04-10 17:01:20
 * @version V1.0
 */
public class HttpSender {

	private static String charset = "UTF-8";

	/**
	 * 
	 * @param 设置连接超时时间，单位毫秒。<br>
	 * @param 设置从connect         Manager获取Connection
	 *                           超时时间，单位毫秒。这个属性是新加的属性，因为目前版本是可以共享连接池的 <br>
	 * @param 请求获取数据的超时时间，单位毫秒。  如果访问一个接口，多少时间内无法返回数据，就直接放弃此次调用。<br>
	 * @return
	 */
	private static RequestConfig configBuilder(int connTimeout, int requestTimeout, int socketTimout) {
		RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(connTimeout)
				.setConnectionRequestTimeout(requestTimeout).setSocketTimeout(socketTimout).build();

		return requestConfig;
	}

	private static RequestConfig configBuilder(int connTimeout, int socketTimout) {

		return configBuilder(connTimeout, -1, socketTimout);
	}

	// 10000
	private static RequestConfig configBuilder() {

		return configBuilder(10000, -1, 10000);
	}

	/**
	 * 
	 * @param 请求地址
	 * @param 请求token
	 * @param 实时获取到的证书序列号（证书会有效期）
	 * @param 请求参数
	 * @return
	 * @throws Exception
	 */
	public static HttpResponse doPostJson(String url, String auth, String serial, String json) throws Exception {

		CloseableHttpClient httpClient = HttpClients.createDefault();

		HttpResponse response = null;

		try {

			// 第一步：创建HttpClient对象
			httpClient = HttpClients.createDefault();

			// 第二步：创建httpPost对象
			HttpPost httpPost = new HttpPost(url);

			httpPost.setHeader("Content-type", "application/json");
			httpPost.setHeader("Accept", "application/json");
			httpPost.setHeader("Authorization", auth);
			httpPost.setHeader("User-Agent", Constant.USER_AGENT);
			httpPost.setHeader("Wechatpay-Serial", serial);
			httpPost.setConfig(configBuilder());
			// 微信支付给每个接收到的请求分配了一个唯一标示。请求的唯一标示包含在应答的HTTP头Request-Id中。当需要微信支付帮助时，请提供请求的唯一标示，以便我们更快的定位到具体的请求。

			// 第三步：给httpPost设置JSON格式的参数
			StringEntity requestEntity = new StringEntity(json, charset);

			requestEntity.setContentEncoding(charset);

			httpPost.setEntity(requestEntity);

			response = httpClient.execute(httpPost);

			Header[] requestId = response.getHeaders("Request-ID");

			final StatusLine statusLine = response.getStatusLine();

			// final HttpEntity entity = response.getEntity();

			JSONObject ret = null;

			try {

				// String retStr = EntityUtils.toString(entity);

				// entity.
				// ret = JSONObject.parseObject(retStr);
			} catch (Exception e) {

				ret = new JSONObject();
				ret.put("request_id", requestId[0].getValue());
			}

			if (statusLine.getStatusCode() >= 300) {
				// EntityUtils.consume(entity);
				// throw new HttpResponseException(statusLine.getStatusCode(),
				// statusLine.getReasonPhrase());
			}
			// return entity == null ? null : handleEntity(entity);

			// 调接口获取返回值时，必须用此方法

		} catch (Exception e) {
			e.printStackTrace();

			return response;
		} finally {
			try {
				httpClient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		// 第五步：处理返回值
		return response;

	}

	// 换行符
	private static String LINE_END = "\r\n";
	// 定义数据分隔线
	private static String PREFIX = "--";

	@Deprecated
	public static String uploadBodyBuilder(String pictureSha, String picName, String picBase64) {

		StringBuffer requestBody = new StringBuffer();

		// start
		// 第一部分
		requestBody.append(PREFIX).append(Constant.BOUNDARY).append(LINE_END);
		requestBody.append("Content-Disposition: form-data; name=\"meta\";").append(LINE_END);
		requestBody.append("Content-Type: application/json; ").append(LINE_END);
		requestBody.append(LINE_END);
		//
		requestBody.append(pictureSha).append(LINE_END);

		// 第二部分
		requestBody.append(PREFIX).append(Constant.BOUNDARY).append(LINE_END);
		requestBody.append("Content-Disposition: form-data; name=\"file\"; ").append("filename=\"").append(picName)
				.append("\";").append(LINE_END);
		requestBody.append("Content-Type: image/jpeg").append(LINE_END);
		requestBody.append(LINE_END);
		//
		// 二进制内容
		requestBody.append(picBase64).append(LINE_END);
		requestBody.append(PREFIX).append(Constant.BOUNDARY).append(PREFIX).append(LINE_END);

		return requestBody.toString();
	}

	public static HttpResponse doUpload(String url, String auth, String pictureSha, String picName, byte[] pic)
			throws Exception {

		CloseableHttpClient httpClient = HttpClients.createDefault();

		HttpResponse response = null;

		try {

			// 第一步：创建HttpClient对象
			httpClient = HttpClients.createDefault();

			// 第二步：创建httpPost对象
			HttpPost httpPost = new HttpPost(url);

			httpPost.setHeader(HttpHeaders.CONTENT_TYPE, ContentType.MULTIPART_FORM_DATA.getMimeType());

			httpPost.setHeader("Accept", "application/json");
			httpPost.setHeader("Authorization", auth);
			httpPost.setHeader("User-Agent", Constant.USER_AGENT);

			httpPost.setConfig(configBuilder());

			// 设置请求Body
			// 创建MultipartEntityBuilder
			MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create()
					.setMode(HttpMultipartMode.RFC6532);
			// 设置boundary
			multipartEntityBuilder.setBoundary(Constant.BOUNDARY);
			multipartEntityBuilder.setCharset(Charset.forName("UTF-8"));
			// 设置meta内容
			multipartEntityBuilder.addTextBody("meta", pictureSha, ContentType.APPLICATION_JSON);
			// 设置图片内容

			multipartEntityBuilder.addBinaryBody("file", pic, ContentType.create("image/jpg"), picName);
			// 放入内容
			httpPost.setEntity(multipartEntityBuilder.build());

			// 第四步：发送HttpPost请求，获取返回值
			response = httpClient.execute(httpPost);

		} catch (Exception e) {
			e.printStackTrace();

			return response;
		} finally {
			try {
				httpClient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		// 第五步：处理返回值
		return response;

	}

	public static HttpResponse doGetJson(String url, String auth) throws Exception {

		CloseableHttpClient httpClient = HttpClients.createDefault();

		HttpResponse response = null;

		try {

			// 第一步：创建HttpClient对象
			httpClient = HttpClients.createDefault();

			// 第二步：创建httpPost对象
			HttpGet httpGet = new HttpGet(url);

			httpGet.setHeader("Content-type", "application/json");
			httpGet.setHeader("Accept", "application/json");
			httpGet.setHeader("Authorization", auth);
			httpGet.setHeader("User-Agent", Constant.USER_AGENT);

			httpGet.setConfig(configBuilder());
			// 微信支付给每个接收到的请求分配了一个唯一标示。请求的唯一标示包含在应答的HTTP头Request-Id中。当需要微信支付帮助时，请提供请求的唯一标示，以便我们更快的定位到具体的请求。

			// 第四步：发送HttpPost请求，获取返回值
			// returnValue = httpClient.execute(httpGet, responseHandler);

			response = httpClient.execute(httpGet);

			Header[] requestId = response.getHeaders("Request-ID");

			final StatusLine statusLine = response.getStatusLine();

			// final HttpEntity entity = response.getEntity();

			JSONObject ret = null;

			try {

				// String retStr = EntityUtils.toString(entity);

				// entity.
				// ret = JSONObject.parseObject(retStr);
			} catch (Exception e) {

				ret = new JSONObject();
				ret.put("request_id", requestId[0].getValue());
			}

			if (statusLine.getStatusCode() >= 300) {
				// EntityUtils.consume(entity);
				// throw new HttpResponseException(statusLine.getStatusCode(),
				// statusLine.getReasonPhrase());
			}
			// return entity == null ? null : handleEntity(entity);

			// 调接口获取返回值时，必须用此方法

		} catch (Exception e) {
			e.printStackTrace();

			return response;
		} finally {
			try {
				httpClient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		// 第五步：处理返回值
		return response;

	}

	public static String post(String url, int cnnTimeout, int socketTimeout, Map<String, String> paramMap)
			throws Exception {

		CloseableHttpClient httpClient = null;

		HttpPost post = new HttpPost(url);

		post.setHeader("Content-type", "application/x-www-form-urlencoded; charset=" + charset);

		post.setHeader("Accept", "text/xml;charset=" + charset);

		post.setHeader("Cache-Control", "no-cache");

		post.setConfig(configBuilder(cnnTimeout, -1, socketTimeout));

		List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();

		if (paramMap != null) {

			Iterator<Map.Entry<String, String>> iter = paramMap.entrySet().iterator();

			while (iter.hasNext()) {

				Map.Entry<String, String> entry = iter.next();

				params.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
			}

		}

		post.setEntity(new UrlEncodedFormEntity(params, charset));

		httpClient = HttpClients.createDefault();

		HttpResponse response = httpClient.execute(post);

		if (response.getStatusLine().getStatusCode() != 200) {
			throw new Exception("请求失败！");
		}
		HttpEntity resEntity = response.getEntity();
		return resEntity == null ? "" : EntityUtils.toString(resEntity, charset);

	}

	public static String post(String url, Map<String, String> paramMap) throws Exception {

		return post(url, -1, -1, paramMap);
	}

	public static void main(String[] args) throws Exception {

		HttpSender s = new HttpSender();

		String result = s.post("https://www.baidu.com/", null);

		System.out.println(result);

		// System.out.println(data[0].getValue());
	}
}
