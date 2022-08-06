package com.ekertree.easypunch.utils;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.setting.Setting;
import lombok.Value;

/**
 * ClassName: PasswordUtils
 * Description:
 * date: 2022/8/3 18:09
 *
 * @author Ekertree
 * @since JDK 1.8
 */
public class EncryptUtils{

    private static String KEY;

    static {
        Setting setting = new Setting("encrypt.setting");
        KEY = setting.get("encryptKey");
    }

    public static String encode(String str) {
        return SecureUtil.aes(KEY.getBytes()).encryptHex(str);
    }

    public static String decode(String str) {
        return SecureUtil.aes(KEY.getBytes()).decryptStr(str);
    }

}
