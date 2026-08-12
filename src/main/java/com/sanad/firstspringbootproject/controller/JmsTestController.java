package com.sanad.firstspringbootproject.controller;

import com.sanad.firstspringbootproject.JMS.queue.StatementProducer;
import com.sanad.firstspringbootproject.JMS.topic.TopicProducer;

import org.springframework.jms.core.JmsTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test/jms")
public class JmsTestController {
    private final TopicProducer topicProducer;
    private final StatementProducer statementProducer;
    public  JmsTestController(TopicProducer topicProducer, StatementProducer statementProducer) {
        this.topicProducer = topicProducer;
        this.statementProducer = statementProducer;
    }

    @GetMapping("/queue")
    public String queue(@RequestParam Long bankId, @RequestParam String accountNumber) {
        statementProducer.requestStatement(bankId, accountNumber);
        return "Statement request sent for account: " + accountNumber;
    }

    @GetMapping("/topic")
    public String topic(@RequestParam String message){
        topicProducer.sendMessage(message);
        return "Message sent to topic" + message;
    }
}
