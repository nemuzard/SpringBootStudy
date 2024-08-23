package com.nowcoder.community.util;

import io.micrometer.common.util.StringUtils;
import org.apache.catalina.util.StringUtil;
import org.springframework.util.DigestUtils;

import java.util.UUID;

public class CommunityUtil {
    // 激活码 - 随机字符 - 会经常用
    public static String generateUUID(){
        // 不想要有 “-”，都替换成空字符串
        return UUID.randomUUID().toString().replaceAll("-","");
    }

    //MD5加密：只能加密不能解密
    public static String md5(String key){
        if(StringUtils.isBlank(key)){
            return null; //如果空 不处理
        }
        return DigestUtils.md5DigestAsHex(key.getBytes());
    }
}
