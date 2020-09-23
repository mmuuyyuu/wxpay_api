package com.easy.wechat.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class FilsUtils {

	public static byte[] file(File file) {
		
		InputStream ins = null;
		try {

			ins = new FileInputStream(file);

			byte[] bytes = new byte[ins.available()];

			// 将文件内容写入字节数组
			ins.read(bytes);

			return bytes;

		} catch (Exception e) {

			e.printStackTrace();
			return null;
		} finally {

			if (ins != null) {
				try {
					ins.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}

	}
}
