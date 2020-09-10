package com.wyw.gmcert.bc;

import java.security.Provider;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

/**
 * @author 王亚雯
 * 2020年9月9日 上午9:47:00
 * 提供者创建工具
 */
public class ProviderInstance {
    private static Provider BCProvider;

    public static Provider getBCProvider() {
        if (null == BCProvider) {
            synchronized (ProviderInstance.class) {
                if (null == BCProvider) {
                    BCProvider = new BouncyCastleProvider();
                }
            }
        }
        return BCProvider;
    }

}
