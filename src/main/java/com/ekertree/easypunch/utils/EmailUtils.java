package com.ekertree.easypunch.utils;

import cn.hutool.extra.mail.MailUtil;
import com.ekertree.easypunch.pojo.User;

import java.io.File;

/**
 * ClassName: EmailUtils
 * Description:
 * date: 2022/8/6 1:49
 *
 * @author Ekertree
 * @since JDK 1.8
 */
public class EmailUtils {
    public static void sendMessage(User user, String msg, File...files) {
        MailUtil.send(user.getQq()+"@qq.com", "EasyPunchYiBanNotification", msg, false,files);
    }
}
