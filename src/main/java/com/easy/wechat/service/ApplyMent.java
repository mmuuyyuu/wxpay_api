package com.easy.wechat.service;

import java.security.PublicKey;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpResponse;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.easy.wechat.auth.Author;
import com.easy.wechat.constant.Constant;
import com.easy.wechat.httphander.DefaultResponseHandle;
import com.easy.wechat.httphander.ResultHandle;
import com.easy.wechat.utils.CertificateUtils;
import com.easy.wechat.utils.HttpSender;
import com.easy.wechat.utils.SecurityUtils;

/**
 * 
 * 
 * @title: ApplyMent
 * @package: com.minpay.wechat.service
 * @description: 提交申请
 * @author: linNaibin
 * @date: 2020-05-09 15:41:54
 * @version V1.0
 */
public class ApplyMent implements BaseService {

	private String method;

	private String url;

	private String httpUrl;

	private String jsonParams;

	private String serial;

	private Map<String, Object> params;

	public ApplyMent(Map<String, Object> params, String serial, String jsonParams) {

		this.method = "POST";

		this.url = "/v3/apply4subject/applyment";

		this.httpUrl = "https://api.mch.weixin.qq.com/v3/apply4subject/applyment";

		this.params = params;
		this.jsonParams = jsonParams;
		this.serial = serial;

	}

	@Override
	public void executor(ResultHandle h) {

		if (params == null) {
			return;
		}

		// /
		// get token
		Author au = new Author(method, url);
		String token = au.getToken(jsonParams);

		// 发送
		try {

			HttpResponse result = HttpSender.doPostJson(httpUrl, token, serial, jsonParams);
			String ret = h.handle(result);

			System.out.println(ret);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	
	// 请注意 PUBLIK_KEY、与 WECHATPAY_SERIAL 要不定时获取；
	// https://api.mch.weixin.qq.com/v3/certificates
	// AesUtil
 	static final String  CERTIFICATE = "w.pem";
 //	static final String PUBLIK_KEY = "MIID8TCCAtmgAwIBAgIUfa0Qt77MyNVBxSpDBY5ukWydJ+gwDQYJKoZIhvcNAQELBQAwXjELMAkGA1UEBhMCQ04xEzARBgNVBAoTClRlbnBheS5jb20xHTAbBgNVBAsTFFRlbnBheS5jb20gQ0EgQ2VudGVyMRswGQYDVQQDExJUZW5wYXkuY29tIFJvb3QgQ0EwHhcNMTkwNDAyMDgyMjQ5WhcNMjQwMzMxMDgyMjQ5WjCBgjEYMBYGA1UEAwwPVGVucGF5LmNvbSBzaWduMRMwEQYDVQQKDApUZW5wYXkuY29tMR0wGwYDVQQLDBRUZW5wYXkuY29tIENBIENlbnRlcjELMAkGA1UEBgwCQ04xEjAQBgNVBAgMCUd1YW5nRG9uZzERMA8GA1UEBwwIU2hlblpoZW4wggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQD3xyFQwTQb3FnQRkqLO6Y6yI/RHEGcLNLdZbvnuiuvdBhQMjxT6JBVoZ5+EquNHgUQkUHPg480JBlWu6WW5yDqIZCmnL+ZjtTca0FTD2YiRRyGAof9klC9Xa1zeH6cuMyBp0EYTI9ZKBDpH0ZWpQBZpiAU/PpZQd64oVhxmSQvsz8nuS3bv9AyKj9psfAd+kWuKAVNfiduqwD6yB7q2Eady1bPsv4LLTQZD0mHbDFi8eE7kpHlTGlsa3OeaVWteNoGdsLfIZsfTgZp7lHFqfcv13KbCic03VTS3YZU+7Aye22Qgw1tO9fd1aosePV0NOlacVFadlhvWbMagYQ8OFRhAgMBAAGjgYEwfzAJBgNVHRMEAjAAMAsGA1UdDwQEAwIE8DBlBgNVHR8EXjBcMFqgWKBWhlRodHRwOi8vZXZjYS5pdHJ1cy5jb20uY24vcHVibGljL2l0cnVzY3JsP0NBPTFCRDQyMjBFNTBEQkMwNEIwNkFEMzk3NTQ5ODQ2QzAxQzNFOEVCRDIwDQYJKoZIhvcNAQELBQADggEBAKTa+N3lZfwrtyLbST8Cs+l1ptI0HgzUolb3ENDJuowagrAJYJofBPUewNI4y6PAbelw4puOB2H0+1xweNxGbGdVeLEKY/ExOUhSY103p1OIFbD4afAPQHKxhw7VTVYxJG1c9PuC7doI/tZRcKMa5haBHrpX3fsj5cfD9PxIOMxAm+zN6IVbsoJM0P2NrV4tZoYwPIQegeWQ7paV92Tpq6l75PaddAadpGYyY/tMaF/sjXwlnw1xKA7kOL1DW8SXX7oOiKfNzOI0COgAyYgDy4LoOM4EAdw34y5vSw18levz4MAp6DgHGsAiWKSCzrty1lXl0fZ2QibW6TzcVd912hI=";
	static final String WECHATPAY_SERIAL = "7DAD10B7BECCC8D541C52A43058E6E916C9D27E8";
 

	public static void main(String[] args) throws Exception {
		
		PublicKey pubKey = CertificateUtils.getCertificate(CERTIFICATE).getPublicKey() ;

		Map<String, Object> params = new HashMap<String, Object>();

		JSONObject sendMessage = new JSONObject();

		sendMessage.put("channel_id", "332197988"); // 商户号

		sendMessage.put("business_code", "332197988minpayment"); // 服务商自定义的唯一编号，每个编号对应一个申请单。

		/****/
		// 联系人信息，联系人是商户的超级管理员
		JSONObject contactInfo = new JSONObject();

		// 联系人
		// 加密
		String name = SecurityUtils.rsaEncryptOAEP("张三",pubKey );
		contactInfo.put("name", name); // 姓名
		String mobile = SecurityUtils.rsaEncryptOAEP("13312345678", pubKey);
		contactInfo.put("mobile", mobile);// 手机号码

		String id_card_number = SecurityUtils.rsaEncryptOAEP("430422199001236704", pubKey);
		contactInfo.put("id_card_number", id_card_number);// 身份证号码

		sendMessage.put("contact_info", contactInfo);
		/****/

		/****/
		// 主体信息
		JSONObject subjectInfo = new JSONObject();

		///////////////////////////

		/*********** 请根据类型选择行上送参数，否则可能会出现‘存有不在API的参数’ **************/
		//////////////////////////
		// 主题类型
		// SUBJECT_TYPE_ENTERPRISE：企业
		//
		// SUBJECT_TYPE_INSTITUTIONS_CLONED：事业单位
		// SUBJECT_TYPE_INDIVIDUAL：个体工商户
		// SUBJECT_TYPE_OTHERS：其他组织
		// SUBJECT_TYPE_MICRO：小微商户
		subjectInfo.put("subject_type", "SUBJECT_TYPE_ENTERPRISE");

		/** 营业执照 start **/
		// 主体类型为企业或个体户时，营业执照信息必填。

		JSONObject business_licence_info = new JSONObject();
		// 1、请填写营业执照上的注册号。
		// 2、若主体类型为个体工商户或企业，注册号格式须为15位数字或18位数字|大写字母。
		business_licence_info.put("licence_number", "914201123033363296");
		// 营业执照照片 请填写通过《图片上传API》预先上传图片生成好的MediaID
		business_licence_info.put("licence_copy",
				"GDrPvgosHYsRFC4fP8hiux9UpyiJMoTksT4A45w0-bE-66AqHHD43BFOLazS0cUpXcxYR3SGH9jwK_FwT2WqNcurv5yWpxgqPOxKhO15nA4");
		// 1、个体工商户，不能以“公司”结尾。
		// 2、个体工商户，若营业执照上商户名称为空或为“无”，请填写"个体户+经营者姓名"，如“个体户张三”。
		business_licence_info.put("merchant_name", "李四网络有限公司");

		// 法人名字
		business_licence_info.put("legal_person", "李四");
		// 注册地址
		business_licence_info.put("company_address", "广东省深圳市南山区xx路xx号");
		// 营业执照有效日期
		business_licence_info.put("licence_valid_date", "[\"2000-01-01\",\"forever\"]");

		/** 营业执照 end **/

		/****/

		/** 登记证书信息 start 主体类型为事业单位或其他组织时，登记证书信息必填 **/
		JSONObject certificate_info = new JSONObject();

		// CERTIFICATE_TYPE_2388：事业单位法人证书
		// CERTIFICATE_TYPE_2389：统一社会信用代码证书
		// CERTIFICATE_TYPE_2390：有偿服务许可证（军队医院适用)
		// CERTIFICATE_TYPE_2391：医疗机构执业许可证（军队医院适用)
		// CERTIFICATE_TYPE_2392：企业营业执照（挂靠企业的党组织适用)
		// CERTIFICATE_TYPE_2393：组织机构代码证（政府机关适用)
		// CERTIFICATE_TYPE_2394：社会团体法人登记证书
		// CERTIFICATE_TYPE_2395：民办非企业单位登记证书
		// CERTIFICATE_TYPE_2396：基金会法人登记证书
		// CERTIFICATE_TYPE_2397：慈善组织公开募捐资格证书
		// CERTIFICATE_TYPE_2398：农民专业合作社法人营业执照
		// CERTIFICATE_TYPE_2399：宗教活动场所登记证
		// CERTIFICATE_TYPE_2400：其他证书/批文/证明
		//
		certificate_info.put("cert_type", "CERTIFICATE_TYPE_2400");

		// 证书编号
		certificate_info.put("cert_number", "111111111");
		// 证书照片
		certificate_info.put("cert_copy",
				"GDrPvgosHYsRFC4fP8hiux9UpyiJMoTksT4A45w0-bE-66AqHHD43BFOLazS0cUpXcxYR3SGH9jwK_FwT2WqNcurv5yWpxgqPOxKhO15nA4");

		// 商户名称
		certificate_info.put("merchant_name", "李四公益团");
		// 法人
		certificate_info.put("legal_person", "李四");
		// 注册地址
		certificate_info.put("company_address", "广东省深圳市南山区xx路xx号");
		// 证书有效日期
		certificate_info.put("cert_valid_date", "[\"2000-01-01\",\"forever\"]");
		// 单位证明函照片
		certificate_info.put("company_prove_copy",
				"GDrPvgosHYsRFC4fP8hiux9UpyiJMoTksT4A45w0-bE-66AqHHD43BFOLazS0cUpXcxYR3SGH9jwK_FwT2WqNcurv5yWpxgqPOxKhO15nA4");

//	subjectInfo.put("certificate_info", certificate_info);

		/** 登记证书信息 end **/

		/** 辅助证明材料信息 start 主体类型为小微商户时，辅助证明材料信息必填。 **/
		JSONObject assist_prove_info = new JSONObject();

		// 小微经营类型

		// MICRO_TYPE_STORE：门店场所
		// MICRO_TYPE_MOBILE：流动经营/便民服务
		// MICRO_TYPE_ONLINE：线上商品/服务交易
		assist_prove_info.put("micro_biz_type", "MICRO_TYPE_STORE");
		// 门店名称
		assist_prove_info.put("store_name", "李四公益团");
		// 门店省市编码
		// 参照《省市区编号对照表.xlsx》
		assist_prove_info.put("store_address_code", "450500");
		// 门店地址
		assist_prove_info.put("store_address", "广东省深圳市南山区xx路xx号");

		// 门店门头照片
		assist_prove_info.put("store_header_copy",
				"GDrPvgosHYsRFC4fP8hiux9UpyiJMoTksT4A45w0-bE-66AqHHD43BFOLazS0cUpXcxYR3SGH9jwK_FwT2WqNcurv5yWpxgqPOxKhO15nA4");
		// 店内环境照片
		assist_prove_info.put("store_indoor_copy",
				"GDrPvgosHYsRFC4fP8hiux9UpyiJMoTksT4A45w0-bE-66AqHHD43BFOLazS0cUpXcxYR3SGH9jwK_FwT2WqNcurv5yWpxgqPOxKhO15nA4");

		// subjectInfo.put("assist_prove_info", assist_prove_info);

		/** 辅助证明材料信息 end **/

		/** 经营许可证信息 start 特殊行业的经营许可证信息。 **/
		JSONArray special_operation_info = new JSONArray();

		JSONObject s1 = new JSONObject();

		// 特殊行业ID
		// 参照
		// https://pay.weixin.qq.com/wiki/doc/apiv3/wxpay/applysubject/chapter6_1.shtml
		s1.put("category_id", 052);

		JSONArray arr1 = new JSONArray();

		// 店内环境照片
		arr1.add(
				"GDrPvgosHYsRFC4fP8hiux9UpyiJMoTksT4A45w0-bE-66AqHHD43BFOLazS0cUpXcxYR3SGH9jwK_FwT2WqNcurv5yWpxgqPOxKhO15nA4");

		s1.put("store_indoor_copy", arr1);

		special_operation_info.add(s1);

		// subjectInfo.put("special_operation_info", special_operation_info);

		/** 经营许可证信息 end **/

		/** 法人信息 start **/
		JSONObject identification_info = new JSONObject();
		// IDENTIFICATION_TYPE_IDCARD：身份证（限中国大陆居民)
		// IDENTIFICATION_TYPE_OVERSEA_PASSPORT：护照（限境外人士)
		// IDENTIFICATION_TYPE_HONGKONG_PASSPORT ：中国香港居民-来往内地通行证
		// IDENTIFICATION_TYPE_MACAO_PASSPORT：中国澳门居民-来往内地通行证
		// IDENTIFICATION_TYPE_TAIWAN_PASSPORT：中国台湾居民-来往大陆通行证
		identification_info.put("identification_type", "IDENTIFICATION_TYPE_IDCARD");

		// 请填写法人证件上的姓名，2~30个中文字符、英文字符、符号

		String identification_name = SecurityUtils.rsaEncryptOAEP("李四", pubKey);

		identification_info.put("identification_name", identification_name);
		// 证件号码
		// 加密
		String identification_number = SecurityUtils.rsaEncryptOAEP("430422199001236705", pubKey);
		identification_info.put("identification_number", identification_number);
		// 证件有效日期
		identification_info.put("identification_valid_date", "[\"2000-01-01\",\"forever\"]");
		// 证件正面照片
		identification_info.put("identification_front_copy",
				"GDrPvgosHYsRFC4fP8hiux9UpyiJMoTksT4A45w0-bE-66AqHHD43BFOLazS0cUpXcxYR3SGH9jwK_FwT2WqNcurv5yWpxgqPOxKhO15nA4");
		// 证件反面照片
		identification_info.put("identification_back_copy",
				"GDrPvgosHYsRFC4fP8hiux9UpyiJMoTksT4A45w0-bE-66AqHHD43BFOLazS0cUpXcxYR3SGH9jwK_FwT2WqNcurv5yWpxgqPOxKhO15nA4");

		sendMessage.put("subject_info", subjectInfo);
		sendMessage.put("identification_info", identification_info);

		/** 法人信息 end **/

		System.out.println("====================");
		System.out.println(sendMessage.toString());

		ApplyMent post = new ApplyMent(params, WECHATPAY_SERIAL, sendMessage.toString());
		post.executor(new DefaultResponseHandle());
	}

}
