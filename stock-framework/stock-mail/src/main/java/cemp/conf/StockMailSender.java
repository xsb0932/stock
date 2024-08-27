package cemp.conf;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class StockMailSender {

    @Autowired
    JavaMailSender javaMailSender;

    public void send(String from ,String to ,String msg){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setCc(from);
        message.setTo(to);
        message.setText(msg);
        javaMailSender.send(message);
    }
}
