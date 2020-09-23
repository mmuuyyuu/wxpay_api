package com.easy.wechat.utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;


import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateFactory;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;

public class AesUtil {

	static final int KEY_LENGTH_BYTE = 32;
	static final int TAG_LENGTH_BIT = 128;
	private final byte[] aesKey;

	public AesUtil(byte[] key) {
		if (key.length != KEY_LENGTH_BYTE) {
			throw new IllegalArgumentException("无效的ApiV3Key，长度必须为32个字节");
		}
		this.aesKey = key;
	}

	public String decryptToString(byte[] associatedData, byte[] nonce, String ciphertext)
			throws GeneralSecurityException, IOException {
		try {
			Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");

			SecretKeySpec key = new SecretKeySpec(aesKey, "AES");
			GCMParameterSpec spec = new GCMParameterSpec(TAG_LENGTH_BIT, nonce);

			cipher.init(Cipher.DECRYPT_MODE, key, spec);
			cipher.updateAAD(associatedData);

			return new String(cipher.doFinal(Base64.getDecoder().decode(ciphertext)), "utf-8");
		} catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
			throw new IllegalStateException(e);
		} catch (InvalidKeyException | InvalidAlgorithmParameterException e) {
			throw new IllegalArgumentException(e);
		}
	}

	public PublicKey getPubKey(String certificate) {
		try {
			return stringToX509Certificate(certificate).getPublicKey();
		} catch (Exception e) {
			return null;

		}
	}

	public X509Certificate stringToX509Certificate(String str) throws Exception {

		InputStream fis = new ByteArrayInputStream(str.getBytes());

		try {

			CertificateFactory cf = CertificateFactory.getInstance("X509");

			X509Certificate cert = (X509Certificate) cf.generateCertificate(fis);

			cert.checkValidity();

			return cert;

		} catch (CertificateExpiredException e) {
			throw new RuntimeException("证书已过期", e);
		} catch (CertificateNotYetValidException e) {
			throw new RuntimeException("证书尚未生效", e);
		} catch (CertificateException e) {
			throw new RuntimeException("无效的证书文件", e);
		} finally {
			fis.close();
		}
	}

	static final String CIPHERTEXT = "MpDhQA/1FHyJ/NQB2klNl6kAjyq3y6GT4rq/RfEc9Q/W01lwSHTFObrGdw+vprhpUQqkj2ZE37cL/ZfWcXysKdd9Z4cQslaLwnSWFn9gwrpnR83KSG6cnDwQrjlhOcyfQWWZbigN0BFsAqUCV5JUFmhho/eCvBptaqHHmTyUuJ+TDuJkgO0rbAVGlRJ7uqf66vsoPFMJKxGQ37yEA3Oi8/kLFFE8mZ4tnApYrNOJCPSh5lHfyI0XxKPQEVFrSV4/cutNmoq3lUErx7r1pgNoWwPFtU7L2G8p1xNpZrnN+lKHHohRiz+e3mfMVQHDOMIAhJWAlyxSos2Yfhx6feKbpgFpmEqH5bDT8aubqnsTO0JZjlQFZzQxbxRIHCssYJ0wcs8xR9cLRQKPZZIKEerRs0xI7+j15/6SaG1RS/Q6JYTiruOTYUGRrOGbJurQKzC+ABkLQgI+HDTnGibm0XLN1QsHcLrdI97s8THWoo+g/NntUBV4tFi4x2tffcsPaZQOhG/UyQKEwc3yA5NbpWWTIgkMdZOgtVAbeYyNysoZWzhTs42zMLKhpDGVoCdQcTch5oCRepnub31dtzlyxfz6d/HpMElA2qpwdLxy2bVhlzKwcqPejAQumx0410bzbpits0pbZlue4iH09YgDA27+8L/nOM+ph871xyw368ZIEXZDmoBNOJ/MS2z7lLxqkXPaA0FyA/JkHOSKhG+eNmNw1thIVQCWCSbS2TA1FyAuxDZrZngTFnNXsvG6URwiiZls6i2AekoLNmhiULCxkzwYeMxwkPLnyaEZU2jQz3TFJxfvsUuikwJoJXP3knOGelh8GHzG+Is/UP2sKwgOYeE2eFtO27PiEDJmEY+RocyAfmUGpWnodyRC0vYvxs2SqYBQqD9LXAnLKokLmiO4p990Cu0IEHjyu+375n+4SfD0gzyKj3l4rlL+4nZAOLngTXWhcdweNmqvvvMjCC4uNZonms/I6IUlbY8K77F6dqaQf0N7TsM9tuIUX3TCwZR6iil54qfO71e6AKhQm3ELJLHrHT4rtzgyJT5hP0JULjtwJ1xIYGToEqDbrMoJkMZKSTaa2L5IS5YmhR9B1IT7Fho0Zjx6PM0j0h8DsR5D9AxoIEHuzNqWejfbFW4kDkFoK+0kmyNixyFx5JEjrZYIOv0mH3GS1+TDix5NViniidXNPm0x/4R1BSgEeYqyJA4dvIwlM5jN7vk4ayS2SrckqvqggSDnjVAShWJnNLKmEMRC1Xchma3IWvjXAKgDWu4mXcv6Hu5rDf2a2R9dEa93XZnQ73BnlO0nykvefDWNgrN/mRL/R/0/LmWJpkqvHf4GusvcRsh/rYsKvWSMA+Ct3WMJPCZgxHm72L7BotC+H66r1i3mojyc1WevXek2iFEo+HNrxJigjMsX36n7fmuDRYQtL3cmiBoh2Z99hwetMaQUsIVfqwcysf2xmI7iLIc5iVmRs6GxBCALRfrU3WasPvAwgh9o+3hCDw3yZFDzZ3GNlBT5QA6i0YE0JqLoOYn9mLfBozR/4CnJT82EXEARfLwfVvbt4GjXCuXgc2r1ClIBHmEmWHAGKuHBITJZ9odMydQThw+XgMb9zRmzYXLPOEJIALZpMOq3OESRBKg7RAYzlIPKNLrZgI6+Jg7e+v/0e9eKQOiAy/YZKIksqjpbVYMbXDaDis+MM4DKB+nN2T23hA4IZ3LMNN28+QPebcGfgsrXLeWwSC0yDfKGMUkhfM65tZ6z8U+5TJ/PpamfB0qmkRVVn3gT373HzwiUMhJ/vwfjj+KTsMo5O2zOgV4hAWx7n58ESiK9xnRjaFIyv5lfA9v4+9jH9lyvFZTYZcvnL3GE6IQ1PttHDhS0hAASGcs6mwas1qP4er9GjLJ7D+zkKDo1KqRNousu2q6lq20AOXALIlaW";

	public static void main(String[] args) throws Exception {

		String key = "c1NTPwibH7stQXnqGUjiXT7XYH8NDKAB";

		byte[] keys = key.getBytes();

		System.out.println(keys.length);

		AesUtil aes = new AesUtil(key.getBytes());

		String res = aes.decryptToString("certificate".getBytes(), "7403698c92c9".getBytes(), CIPHERTEXT);

		// System.out.println(res);
		// res = res.replace("-----BEGIN CERTIFICATE-----", "");
		// res = res.replace("-----END CERTIFICATE-----", "");
		// res = res.replace("\n", "");
 
		X509Certificate x = aes.stringToX509Certificate(res);

		System.out.println(res);
	}

}
