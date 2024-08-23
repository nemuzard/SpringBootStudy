package com.nowcoder.community.config;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.google.code.kaptcha.Producer;

import java.util.Properties;

@Configuration
public class KaptchaConfig {
    @Bean //  声明一个bean会被spring管理
    // producer 是kaptcha的接口类型
    public Producer kaptchaProducer(){
        Properties properties = new Properties();
        // 给prop里面set key values, 这些key都是在官网看到的
        properties.setProperty("kaptcha.image.width","100");
        properties.setProperty("kaptcha.image.height","40");
        properties.setProperty("kaptcha.textproducer.font.size","32");
        //0,0,0是黑色 三个位数代表 红 绿 蓝
        properties.setProperty("kaptcha.textproducer.font.color","0,0,0");
        // 给字符串拆成字符 去随机
        properties.setProperty("kaptcha.textproducer.char.string","0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ");
        properties.setProperty("kaptcha.textproducer.char.length","4");
        // 图片的干扰类 -- noise
        properties.setProperty("kaptcha.noise.impl","com.google.code.kaptcha.impl.NoNoise");

        // 实例化 实现类
        DefaultKaptcha kaptcha = new DefaultKaptcha(); // 需要传入参数
        // 给参数封装到config对象
        Config config = new Config(properties); // 依赖properties对象
        kaptcha.setConfig(config);
        return kaptcha;
    }
}
