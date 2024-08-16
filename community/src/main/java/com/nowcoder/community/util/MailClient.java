package com.nowcoder.community.util;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

// 通用，需要spring管理
@Component
public class MailClient {
    private static final Logger logger = LoggerFactory.getLogger(MailClient.class);
    @Autowired
    private JavaMailSender mailSender; //构建MimeMessage方法

    //寄件人，收件人，标题，内容
    @Value("${spring.mail.username}")
    private String from;
    //能被外界调用
    public void sendMail(String to,String subject, String content){

        try{
            //空的
            MimeMessage message = mailSender.createMimeMessage();
            // 加内容
            MimeMessageHelper helper = new MimeMessageHelper(message);
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            // support html
            helper.setText(content,true);

            //send
            mailSender.send(helper.getMimeMessage());
        }catch(MessagingException e){
            logger.error("失败："+ e.getMessage()); // 记录错误日志
        }
    }
}
