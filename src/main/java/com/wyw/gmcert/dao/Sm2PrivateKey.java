package com.wyw.gmcert.dao;

import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.jce.interfaces.ECPrivateKey;

/**
 * @author 王亚雯
 * 2020年9月8日 上午11:15:35
 * userfor: sm2 私钥
 */
public interface Sm2PrivateKey  extends ECPrivateKey{
	 ECPrivateKeyParameters getPrivateKeyParameters();
	 byte[] getWithId();
	 void setWithId(byte[] withId);
}
