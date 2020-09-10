package com.wyw.gmcert.dao;

import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.jce.interfaces.ECPublicKey;

/**
 * @author 王亚雯
 * 2020年9月8日 上午11:15:35
 * userfor: sm2 公钥
 */
public interface Sm2PublicKey extends ECPublicKey {
	ECPublicKeyParameters getPublicKeyParameters();
	 byte[] getWithId();
	 void setWithId(byte[] withId);
}
