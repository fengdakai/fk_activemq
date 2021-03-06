package com.fk.service;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

/**
 * 消息生产者（发送者）
 * Created by kai on 2017/9/9.
 */
public class JMQProduct {

    //默认连接用户名
    private static final String USERNAME = ActiveMQConnection.DEFAULT_USER;
    //默认连接密码
    private static final String PASSWORD = ActiveMQConnection.DEFAULT_PASSWORD;
    //默认连接地址
    private static final String BROKEURL = ActiveMQConnection.DEFAULT_BROKER_URL;
    //发送的消息数量
    private static final int SENDNUM = 10;

    public static void main(String[] args) {
        //连接工厂
        ConnectionFactory connectionFactory;
        //连接
        Connection connection = null;
        //会话 发送或接收消息的线程
        Session session;
        //消息目的地  其实就是连接到哪个队列，如果是点对点，那么它的实现是Queue，如果是订阅模式，那它的实现是Topic
        Destination destination;
        //消息生产者
        MessageProducer messageProducer = null;
        //实例化连接工厂
        connectionFactory = new ActiveMQConnectionFactory(USERNAME, PASSWORD, BROKEURL);

        try {
            //通过连接工厂获取连接
            connection = connectionFactory.createConnection();

            //启动连接
            connection.start();

            //创建session
            /**
             * 第一个参数:是否支持事务，如果为true，则会忽略第二个参数，被jms服务器设置为SESSION_TRANSACTED
             * 第二个参数为false时，paramB的值可为Session.AUTO_ACKNOWLEDGE，Session.CLIENT_ACKNOWLEDGE，DUPS_OK_ACKNOWLEDGE其中一个。
             * Session.AUTO_ACKNOWLEDGE为自动确认，客户端发送和接收消息不需要做额外的工作。哪怕是接收端发生异常，也会被当作正常发送成功。
             * Session.CLIENT_ACKNOWLEDGE为客户端确认。客户端接收到消息后，必须调用javax.jms.Message的acknowledge方法。jms服务器才会当作发送成功，并删除消息。
             * Session.DUPS_OK_ACKNOWLEDGE允许副本的确认模式。一旦接收方应用程序的方法调用从处理消息处返回，会话对象就会确认消息的接收；而且允许重复确认。
             */
            session = connection.createSession(true, Session.AUTO_ACKNOWLEDGE);

            //创建一个名为queueOne的消息队列 如果这个队列不存在，将会被创建
            destination = session.createQueue("queueOne");

            //创建消息生产者
            messageProducer = session.createProducer(destination);

            //设置生产者的模式
            /**
             * DeliveryMode.PERSISTENT 当activemq关闭的时候，队列数据将会被保存
             * DeliveryMode.NON_PERSISTENT 当activemq关闭的时候，队列里面的数据将会被清空
             */
            messageProducer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

            //设置该消息的超时时间
            messageProducer.setTimeToLive(1000);

            //发送消息
            sendMessage(session, messageProducer);

            session.commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //关闭连接
            try {
                if (null != messageProducer) {
                    messageProducer.close();
                }
                if (null != connection) {
                    connection.close();
                }
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 发送消息
     *
     * @param session
     * @param messageProducer 消息生产者
     * @throws Exception
     */
    public static void sendMessage(Session session, MessageProducer messageProducer) throws Exception {
        for (int i = 0; i < SENDNUM; i++) {
            TextMessage textMessage = session.createTextMessage("ActiveMQ 发送消息" + i);
            System.out.println("发送消息：Activemq 发送消息" + i);
            //通过消息生产者发送消息
            messageProducer.send(textMessage);
        }
    }
}
