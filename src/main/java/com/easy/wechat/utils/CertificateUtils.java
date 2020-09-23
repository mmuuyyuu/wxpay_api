package com.easy.wechat.utils;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateFactory;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class CertificateUtils {

	public static void main(String[] args) throws Exception {
		X509Certificate cert = getCertificate("w.pem");

		System.out.println(cert.getPublicKey().toString());

		System.out.println(cert.getSerialNumber());

		// PrivateKey priveteKey =
		// getPrivateKey("E:\\minpay\\merchant-entry\\wxpay\\apiclient_key.pem");
		// PublicKey key =
		// getPublickKey("E:\\minpay\\merchant-entry\\wxpay\\apiclient_cert.pem");

		// String strName = SecurityUtils.rsaEncryptOAEP("张三", key);

		// System.out.println(strName);

		// String str = Base64.getEncoder().encodeToString(key.getEncoded());

		// System.out.println(str);

	}

	/**
	 * 获取公钥。
	 *
	 * @param filename 私钥文件路径 (required)
	 * @return 私钥对象
	 */
	public static PublicKey getPublickKey(String cerFile) throws IOException {

		// 读取证书文件
		try {
			CertificateFactory cf = CertificateFactory.getInstance("X.509");
			FileInputStream in = new FileInputStream(cerFile);

			// 生成一个证书对象并使用从输入流 inStream 中读取的数据对它进行初始化。
			Certificate c = cf.generateCertificate(in);
			PublicKey publicKey = c.getPublicKey();
			// key = Base64.encode(publicKey.getEncoded());
			return publicKey;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	/**
	 * 获取私钥。
	 *
	 * @param filename 私钥文件路径 (required)
	 * @return 私钥对象
	 */
	public static PrivateKey getPrivateKey(String filename) throws IOException {

		String content = new String(Files.readAllBytes(Paths.get(filename)), "utf-8");
		try {
			String privateKey = content.replace("-----BEGIN PRIVATE KEY-----", "")
					.replace("-----END PRIVATE KEY-----", "").replaceAll("\\s+", "");
			System.out.println(privateKey);

			KeyFactory kf = KeyFactory.getInstance("RSA");
			return kf.generatePrivate(new PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKey)));
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("当前Java环境不支持RSA", e);
		} catch (InvalidKeySpecException e) {
			throw new RuntimeException("无效的密钥格式");
		}
	}

	/**
	 * 获取证书。
	 *
	 * @param filename 证书文件路径 (required)
	 * @return X509证书
	 */
	public static X509Certificate getCertificate(String filename) throws IOException {
		InputStream fis = new FileInputStream(filename);
		BufferedInputStream bis = new BufferedInputStream(fis);

		try {
			CertificateFactory cf = CertificateFactory.getInstance("X509");
			X509Certificate cert = (X509Certificate) cf.generateCertificate(bis);
			cert.checkValidity();
			return cert;
		} catch (CertificateExpiredException e) {
			throw new RuntimeException("证书已过期", e);
		} catch (CertificateNotYetValidException e) {
			throw new RuntimeException("证书尚未生效", e);
		} catch (CertificateException e) {
			throw new RuntimeException("无效的证书文件", e);
		} finally {
			bis.close();
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
}
