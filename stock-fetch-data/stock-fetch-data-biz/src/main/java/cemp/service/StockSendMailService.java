package cemp.service;

import cemp.conf.StockMailSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class StockSendMailService {

    @Autowired
    StockMailSender stockMailSender;

    @Async("sendMailPool")
    public void sendMail(String from ,String to,String msg){
        stockMailSender.send(from,to,msg);
    }
}
