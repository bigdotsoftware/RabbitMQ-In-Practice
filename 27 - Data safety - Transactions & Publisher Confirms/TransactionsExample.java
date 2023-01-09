package rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

public class TransactionsExample {

    private final static String QUEUE_NAME = "transactqueue";

    static class SampleProducer extends Thread {

        public void run() {
            System.out.println("--> Running producer");
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost("localhost");
            try (Connection connection = factory.newConnection();
                 Channel channel = connection.createChannel())
            {
                channel.queueDeclare(QUEUE_NAME, /*durable*/false, /*exclusive*/false, /*autoDelete*/false, /*arguments*/null);
                //channel.txSelect();
                for(int i=0;i<=5;i++) {
                    String message = "Hello World " + i;
                    if( i == 5 )
                        message = "Final Message " + i;
                    channel.basicPublish(/*exchange*/"", /*routingKey*/QUEUE_NAME, null, message.getBytes(StandardCharsets.UTF_8));
                    System.out.println(" [x] Sent '" + message + "'");
                    sleep(2000);
                }
                //channel.txCommit();
            } catch (TimeoutException | IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    static class SampleConsumer extends Thread {

        public void run(){
            try {
                sleep(5000);
                System.out.println("--> Running consumer");

                ConnectionFactory factory = new ConnectionFactory();
                factory.setHost("localhost");
                Connection connection = factory.newConnection();
                Channel channel = connection.createChannel();

                channel.queueDeclare(QUEUE_NAME, /*durable*/false, /*exclusive*/false, /*autoDelete*/false, /*arguments*/null);
                System.out.println(" [*] Waiting for messages....");

                DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                    channel.txSelect();
                    String message = new String(delivery.getBody(), "UTF-8");
                    System.out.println(" [x] Received '" + message + "'");
                    channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);

                    if( message.startsWith("Final Message") ) {
                        //channel.txCommit();
                        channel.txRollback();
                        //channel.basicCancel("");
                    }
                };
                channel.basicConsume(QUEUE_NAME, /*autoAck*/false, deliverCallback, consumerTag -> { });

                sleep(Long.MAX_VALUE);

            } catch (InterruptedException | IOException | TimeoutException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] argv) throws Exception {

        SampleProducer producer = new SampleProducer();
        producer.start();

        SampleConsumer consumer = new SampleConsumer();
        consumer.start();

        producer.join();
        consumer.join();

        System.out.println("Done");



    }

}
