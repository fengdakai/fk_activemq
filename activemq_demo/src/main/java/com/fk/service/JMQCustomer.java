package com.fk.service;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

/**
 * 消息的消费者（接收者）
 * Created by kai on 2017/9/9.
 */
public class JMQCustomer {
    //默认连接用户名
    private static final String USERNAME = ActiveMQConnection.DEFAULT_USER;
    //默认连接密码
    private static final String PASSWORD = ActiveMQConnection.DEFAULT_PASSWORD;
    //默认连接地址
    private static final String BROKEURL = ActiveMQConnection.DEFAULT_BROKER_URL;

    public static void main(String[] args) {
        ConnectionFactory connectionFactory;
        Connection connection = null;
        Session session;
        Destination destination;
        MessageConsumer messageConsumer = null;
        connectionFactory = new ActiveMQConnectionFactory(USERNAME, PASSWORD, BROKEURL);

        try {
            connection = connectionFactory.createConnection();
            connection.start();
            session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
            destination = session.createQueue("queueOne");
            messageConsumer = session.createConsumer(destination);

            //接收消息  方法一 主动接收消息
            while (true) {
                TextMessage textMessage = (TextMessage) messageConsumer.receive(1000);
                if (null != textMessage) {
                    System.out.println("消费者接收消息:" + textMessage.getText());
                    textMessage.acknowledge();
                    /*session.commit();*/
                } else {
                    continue;
                }
            }
            //接收消息 方法二 被动接收消息
            //实现一个消息的监听器  只要queueOne队列中有消息，就会被此监听器接收到
            /*messageConsumer.setMessageListener(new MessageListener() {
                public void onMessage(Message message) {
                    try {
                        String text = ((TextMessage)message).getText();
                        System.out.println(text);
                    } catch (JMSException e) {
                        e.printStackTrace();
                    }
                }
            });*/

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != messageConsumer) {
                    messageConsumer.close();
                }
                if (null != connection) {
                    connection.close();
                }
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }
    }
}
