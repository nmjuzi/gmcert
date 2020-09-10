package com.wyw.gmcert.controller;

import java.io.File;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.alibaba.fastjson.JSONObject;
import com.wyw.gmcert.common.CAConstant;
import com.wyw.gmcert.config.AppConfig;
import com.wyw.gmcert.config.CaConfig;
import com.wyw.gmcert.domain.CertResult;
import com.wyw.gmcert.domain.KeyPairResult;
import com.wyw.gmcert.utils.CertUtil;
import com.wyw.gmcert.utils.KeyPairUtil;

import cn.hutool.core.util.ZipUtil;

/**
* @author 王亚雯
* @Date 2020年9月10日 上午11:59:57
* @Desc
*/
@Controller
public class TestController {
	@Autowired
    AppConfig appConfig;
	
	@RequestMapping(value = "/downCertZip", method = RequestMethod.GET)
	public void downCertZip(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		CAConstant.KeyType keyType = CAConstant.KeyType.SM2;
		Integer keySize = 1024;
		CaConfig caConfig = appConfig.getByKeyType(2);
		System.out.println(caConfig);
		// 后期还需配合数据库校验唯一性
		// BigInteger serialNumber = snAllocator.nextSerialNumber();
		BigInteger serialNumber = BigInteger.valueOf(System.currentTimeMillis());
		Date notBefore = new Date();
		Date notAfter = new Date(notBefore.getTime() + 20L * 365 * 24 * 60 * 60 * 1000);
		
		CertResult certResult = new CertResult();
		String signAlg = caConfig.getSignAlg();
		KeyPairResult keyPairResult = KeyPairUtil.gen(keyType, keySize);
		PublicKey pub = keyPairResult.getPub();
		PrivateKey pri = keyPairResult.getPri();
		X509Certificate userCert = CertUtil.makeUserCert(pub, caConfig.getPub(), caConfig.getPri(),
				caConfig.getIssuerDN(), buildUserCADN("alipay").toString(), notBefore, notAfter,
				serialNumber, signAlg);
		certResult.setPri(pri);
		certResult.setPub(pub);
		certResult.setCert(userCert);
		String certDir = caConfig.getClientCertBasePath() + "/" + serialNumber;
        String pri_pem_path = certDir + "/client_pri.key";
        String pub_pem_path = certDir + "/client_pub.key";
        String cert_path = certDir + "/client_cert.crt";
        String p12_path = certDir + "/client.p12";
        String userAlias = "shineyue-client";
        String password = "123456";
        FileUtils.forceMkdir(new File(certDir));
        CertUtil.savePrivateKeyPem(pri, pri_pem_path);
        CertUtil.savePublicKeyPem(pub, pub_pem_path);
        CertUtil.saveX509CertBase64(userCert, cert_path);
        CertUtil.savePKCS12(userCert, pri, userAlias, password, p12_path);
        JSONObject result = new JSONObject();
        result.put("cert_path", cert_path);
        result.put("userAlias", userAlias);
        result.put("p12_path", p12_path);
        result.put("password", password);
        result.put("pri", FileUtils.readFileToString(new File(pri_pem_path)));
        result.put("pub", FileUtils.readFileToString(new File(pub_pem_path)));
        result.put("serialNumber", serialNumber);
        result.put("catype", keyType);
        System.out.println(result);
		String destPath = caConfig.getClientCertBasePath() + "/" + UUID.randomUUID().toString() + ".zip";
		String readmeDoc = certDir + "/readme.txt";
		String readmeTxt = "证书类型为["+keyType.name+"]p12 alias:shineyue-client\r\n p12 密码:123456";
		FileUtils.writeStringToFile(new File(readmeDoc), readmeTxt, "UTF-8");
        ZipUtil.zip(certDir, destPath);
        // 下载
        File destZipFile = new File(destPath);
        response.setContentType(request.getServletContext().getMimeType(destZipFile.getName()));
        response.setHeader("Content-type", "application/octet-stream");
        response.setHeader("Content-Disposition",
                "attachment;fileName=" + URLEncoder.encode(keyType.name+"_cert.zip","UTF-8"));
        response.getOutputStream().write(FileUtils.readFileToByteArray(destZipFile));
	}

	
	public X500Name buildUserCADN(String userName) {
        X500NameBuilder builder = new X500NameBuilder(BCStyle.INSTANCE);
        builder.addRDN(BCStyle.C, "CN");
        builder.addRDN(BCStyle.ST, "HeBei");
        builder.addRDN(BCStyle.L, "ShiJiaZhuang");
        builder.addRDN(BCStyle.O, "ShineYue");
        builder.addRDN(BCStyle.CN, userName);
        return builder.build();
    }
}
