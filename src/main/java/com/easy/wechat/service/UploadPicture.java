package com.easy.wechat.service;

import java.io.File;

import org.apache.http.HttpResponse;

import com.alibaba.fastjson.JSONObject;
import com.easy.wechat.auth.Author;
import com.easy.wechat.httphander.DefaultResponseHandle;
import com.easy.wechat.httphander.ResultHandle;
import com.easy.wechat.utils.FilsUtils;
import com.easy.wechat.utils.HttpSender;
import com.easy.wechat.utils.SecurityUtils;

/**
 * 
 * 
 * @title: UploadPicture
 * @package: com.minpay.wechat.service
 * @description: 图片上传
 * @author: linNaibin
 * @date: 2020-05-09 16:44:49
 * @version V1.0
 */
public class UploadPicture implements BaseService {

	private String method;

	private String url;

	private String httpUrl;

	// 图片内容
	private byte[] picture;

	// 图片名称
	private String pictureName;

	public UploadPicture(String pictureName, byte[] picture) {
		this.method = "POST";
		this.url = "/v3/merchant/media/upload";
		this.httpUrl = "https://api.mch.weixin.qq.com/v3/merchant/media/upload";
		this.picture = picture;
		this.pictureName = pictureName;

	}

	@Override
	public void executor(ResultHandle h) {

		// get token

		JSONObject signBody = new JSONObject();
		String sha256 = SecurityUtils.getSHA256String(picture);
		signBody.put("filename", pictureName);
		signBody.put("sha256", sha256);

		Author au = new Author(method, url);

		String token = au.getToken(signBody.toString());

		// Base64.getEncoder().encodeToString(picture);

		// 发送
		try {
			HttpResponse result = HttpSender.doUpload(httpUrl, token, signBody.toString(), pictureName, picture);
			String ret = h.handle(result);

			System.out.println(ret);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static void main(String[] args) throws Exception {

		// GDrPvgosHYsRFC4fP8hiux9UpyiJMoTksT4A45w0-bE-66AqHHD43BFOLazS0cUpXcxYR3SGH9jwK_FwT2WqNcurv5yWpxgqPOxKhO15nA4

		File f = new File("E:\\data\\images\\yingyezhizhao.jpg");

		UploadPicture u = new UploadPicture("yingyezhizhao.jpg", FilsUtils.file(f));

		u.executor(new DefaultResponseHandle());

	}

}
