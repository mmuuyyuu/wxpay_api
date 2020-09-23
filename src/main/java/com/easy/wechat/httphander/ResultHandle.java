package com.easy.wechat.httphander;

import org.apache.http.HttpResponse;

public interface ResultHandle {

	public String handle(HttpResponse response);

}
