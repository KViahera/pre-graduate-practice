package org.example.platform.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfiguration {

    // Названия для маршрутизации
    public static final String QUEUE_NAME = "judge_queue";
    public static final String EXCHANGE_NAME = "judge_exchange";
    public static final String ROUTING_KEY = "judge_routing_key";

    public static final String RESULT_QUEUE = "result_queue";
    public static final String RESULT_ROUTING_KEY = "result_routing_key";

    // 1. Создаем саму очередь (durable = true означает, что сообщения переживут перезапуск RabbitMQ)
    @Bean
    public Queue queue() {
        return new Queue(QUEUE_NAME, true);
    }

    // 2. Создаем точку обмена (Exchange)
    @Bean
    public DirectExchange exchange() {
        return new DirectExchange(EXCHANGE_NAME);
    }

    // 3. Связываем очередь и точку обмена ключом маршрутизации
    @Bean
    public Binding binding(Queue queue, DirectExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(ROUTING_KEY);
    }

    @Bean
    public Queue resultQueue() {
        return new Queue(RESULT_QUEUE, true);
    }

    @Bean
    public Binding resultBinding(DirectExchange exchange) { // Используем тот же exchange
        return BindingBuilder.bind(resultQueue()).to(exchange).with(RESULT_ROUTING_KEY);
    }

    // 4. Учим Spring Boot автоматически превращать наши Java-объекты в JSON при отправке
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}