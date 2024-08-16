package com.nowcoder.community;
import com.nowcoder.community.util.MailClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
@MapperScan
public class MailTests {
    @Autowired
    private MailClient mailClient;
    @Test
    public void testTextMail(){
        mailClient.sendMail("zweistler@gmail.com","TEST!","Hello!this is a test");
    }
}
