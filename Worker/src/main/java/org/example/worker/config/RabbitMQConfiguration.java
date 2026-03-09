package org.example.worker.config;

import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfiguration {
    public static final String RESULT_QUEUE = "result_queue";
    public static final String RESULT_ROUTING_KEY = "result_routing_key";
    public static final String EXCHANGE_NAME = "judge_exchange";

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}