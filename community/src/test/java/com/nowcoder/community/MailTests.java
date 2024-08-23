package com.nowcoder.community;
import com.nowcoder.community.util.MailClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
@MapperScan
public class MailTests {
    @Autowired
    private MailClient mailClient;
    @Autowired
    private TemplateEngine templateEngine;
    @Test
    public void testTextMail(){
        mailClient.sendMail("zweistler@gmail.com","TEST!","Hello!this is a test");
    }

    // 主动调用thymleaf引擎
    @Test
    public void testHtmlMail(){
        Context context = new Context();

        //模板对象存到context
        context.setVariable("username","sunday");
        //调用模板引擎
        String content = templateEngine.process("/mail/demo",context);
        System.out.println(content);

         mailClient.sendMail("zweistler@gmail.com","HTML",content);
    }
}
