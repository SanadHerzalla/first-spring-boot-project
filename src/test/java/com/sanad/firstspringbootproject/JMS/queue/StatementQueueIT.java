package com.sanad.firstspringbootproject.JMS.queue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;


@SpringBootTest
public class StatementQueueIT {

//    @Autowired
//    private StatementProducer statementProducer;
//
//    @MockitoSpyBean
//    private StatementListener statementListener;
//
//    void shouldSendAccountNumberThrowQueue(){
//        String accountNumber = "1111";
//        Long bankId = 1L;
//        statementProducer.requestStatement(bankId, accountNumber);
//
//        verify(statementListener, timeout(3000)).generateStatement(bankId,accountNumber);
//    }
}
