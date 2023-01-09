package rabbitmq;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.TimeoutException;

//video
public class ConsistentHashPublish {

        public static void main(String[] argv) throws IOException, TimeoutException, InterruptedException {
            ConnectionFactory cf = new ConnectionFactory();
            cf.setHost("127.0.0.1");
            cf.setPort(5672);
            Connection conn = cf.newConnection();

            Channel ch1 = conn.createChannel();
            Channel ch2 = conn.createChannel();

            Thread.sleep(30000L);

            AMQP.BasicProperties.Builder bldr = new AMQP.BasicProperties.Builder();
            for (int i = 0; i < 100000000; i++) {
                Channel ch = conn.createChannel();
                ch.basicPublish("ex.hash", String.valueOf(i), bldr.build(), "".getBytes("UTF-8"));
                ch.close();
            }


            System.out.println("Done publishing!");

            conn.close();
        }
}
