package rabbitmq;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;
import java.util.stream.Stream;

public class PriorityQueueExample2 {

    private static final String QUEUE_NAME = "q.test-priority";
    private static final int MESSAGE_COUNT = 300_000;
    private static final int MAX_PRIORITY = 255;

    public static void produceMessages(int cnt) {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel())
        {
            Map<String, Object> args = new HashMap<String, Object>();
            args.put("x-max-priority", MAX_PRIORITY);

            channel.queueDeclare(QUEUE_NAME, /*durable*/true, /*exclusive*/false, /*autoDelete*/false, /*arguments*/args);
            channel.queuePurge(QUEUE_NAME);

            ByteBuffer buffer = ByteBuffer.allocate(1000);

            for(int i=0;i<MESSAGE_COUNT;i++) {
                try {
                    AMQP.BasicProperties props = new AMQP.BasicProperties
                            .Builder()
                            //.priority(1)
                            .priority(i%MAX_PRIORITY)
                            .deliveryMode(2 /*PERSISTENT*/)
                            .build();

                    channel.basicPublish("", QUEUE_NAME, props, buffer.array());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }


        } catch (TimeoutException | IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] argv) throws Exception {

        long start = System.nanoTime();
        produceMessages(MESSAGE_COUNT);
        long end = System.nanoTime();
        System.out.format("Published %,d messages in %,d ms%n", MESSAGE_COUNT, Duration.ofNanos(end - start).toMillis());

    }
}
