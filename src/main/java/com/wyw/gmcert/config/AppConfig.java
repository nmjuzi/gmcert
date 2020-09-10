package com.wyw.gmcert.config;

import java.io.File;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import com.wyw.gmcert.common.CAConstant;
import com.wyw.gmcert.exception.CertException;
import com.wyw.gmcert.utils.CertUtil;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@ConfigurationProperties(prefix = "app.config")
@Configuration
@Data
@Slf4j
public class AppConfig implements ApplicationRunner{
	/**
	 * caCert ca证书地址
	 * caPrivateKey ca私钥地址 
	 * clientCertBasePath 生成客户端证书存放地址
	 * signAlg  签名算法
	 */
	private String certBaseDir;
	private  CaConfig rsa;
	private  CaConfig sm2;
	private  CaConfig dsa;
	private  CaConfig ecdsa;
	
	private Map<CAConstant.KeyType,CaConfig> allCaConfig=new HashMap<>();
	public void run(ApplicationArguments applicationArguments) throws Exception {
			try{
				// ca系统初始化,暂时不开放其他证书
				// allCaConfig.put(CAConstant.KeyType.RSA,readToConfig(rsa, CAConstant.KeyType.RSA));
				allCaConfig.put(CAConstant.KeyType.SM2,readToConfig(sm2, CAConstant.KeyType.SM2));
				// allCaConfig.put(CAConstant.KeyType.DSA,readToConfig(dsa, CAConstant.KeyType.DSA));
				// allCaConfig.put(CAConstant.KeyType.ECDSA,readToConfig(ecdsa, CAConstant.KeyType.ECDSA));
			}catch (Exception e){
				throw new RuntimeException("初始化系统失败",e);
			}
	}
	public CaConfig getByKeyType(Integer keyType){
		return  allCaConfig.get(CAConstant.KeyType.forValue(keyType));
	}
	
	public CaConfig readToConfig(CaConfig caConfig, CAConstant.KeyType keyType){
		System.err.println("--------1---------------"+caConfig);
		try {
			//初始化证书配置
			if(Objects.nonNull(caConfig)){
				System.err.println("-----------2------------"+caConfig);
				caConfig.setCertPath(forceBuildPath(caConfig.getCertPath()));
				System.err.println("-----------3------------"+caConfig);
				caConfig.setPriPath(forceBuildPath(caConfig.getPriPath()));
				caConfig.setClientCertBasePath(forceBuildPath(caConfig.getClientCertBasePath()));
				X509Certificate caCertX509 = CertUtil.readX509Cert(caConfig.getCertPath());
				caConfig.setCert(caCertX509);
				caConfig.setIssuerDN(caCertX509.getIssuerDN().getName());
				caConfig.setPub(caCertX509.getPublicKey());
				caConfig.setPri(CertUtil.readPrivateKeyPem(caConfig.getPriPath()));
				log.info("{}初始化完毕,配置信息:{}",keyType.name,caConfig);
				return caConfig;
			}
		} catch (CertException e) {
			throw new RuntimeException(e);
		}
		return null;
	}
	
	private String forceBuildPath(String oppositePath){
		System.out.println(oppositePath);
		File baseFileDir=new File(certBaseDir);
		System.out.println(certBaseDir);
		File absFile=new File(baseFileDir,oppositePath);
		if(!baseFileDir.exists()) {
			baseFileDir.mkdirs();
		}
		if(absFile.isDirectory()){
			if(!absFile.exists()){
				absFile.mkdirs();
			}
		}else{
			if(!absFile.getParentFile().exists()){
				absFile.getParentFile().mkdirs();
			}
		}
		return  absFile.getAbsolutePath();
	}
}
