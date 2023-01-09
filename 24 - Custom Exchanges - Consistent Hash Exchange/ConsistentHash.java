package rabbitmq;


import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.TimeoutException;

public class ConsistentHash {

        private static String CONSISTENT_HASH_EXCHANGE_TYPE = "x-consistent-hash";

        public static void main(String[] argv) throws IOException, TimeoutException, InterruptedException {
            ConnectionFactory cf = new ConnectionFactory();
            cf.setHost("127.0.0.1");
            Connection conn = cf.newConnection();
            Channel ch = conn.createChannel();

            for (String q : Arrays.asList("q.messages1", "q.messages2"/*, "q.messages3", "q.messages4"*/)) {
                ch.queueDeclare(q, true, false, false, null);
                ch.queuePurge(q);
            }

            ch.exchangeDeclare("ex.hash", CONSISTENT_HASH_EXCHANGE_TYPE, true, false, null);

            for (String q : Arrays.asList("q.messages1"/*, "q.messages2"*/)) {
                ch.queueBind(q, "ex.hash", "1");
            }

            for (String q : Arrays.asList("q.messages2"/*, "q.messages4"*/)) {
                ch.queueBind(q, "ex.hash", "1");
            }

            ch.confirmSelect();

            AMQP.BasicProperties.Builder bldr = new AMQP.BasicProperties.Builder();
            for (int i = 0; i < 100000; i++) {
                ch.basicPublish("ex.hash", String.valueOf(i), bldr.build(), "".getBytes("UTF-8"));
            }

            ch.waitForConfirmsOrDie(10000);

            System.out.println("Done publishing!");
            System.out.println("Evaluating results...");
            // wait for one stats emission interval so that queue counters
            // are up-to-date in the management UI
            Thread.sleep(5);

            System.out.println("Done.");
            conn.close();
        }
}
