package rabbitmq;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.time.Duration;
import java.util.concurrent.TimeoutException;
import java.util.stream.Stream;

public class LazyQueueExample {

    private static final String QUEUE_NAME = "q.test-queue-1";
    private static final int MESSAGE_COUNT = 1_000_000;

    public static void produceMessages(int cnt) {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel())
        {
            channel.queueDeclare(QUEUE_NAME, /*durable*/true, /*exclusive*/false, /*autoDelete*/false, /*arguments*/null);
            channel.queuePurge(QUEUE_NAME);

            ByteBuffer buffer = ByteBuffer.allocate(1000);

            Stream.iterate(0, n -> n + 1)
                    .limit(MESSAGE_COUNT)
                    .forEach(i -> {
                        try {
                            channel.basicPublish("", QUEUE_NAME, MessageProperties.PERSISTENT_TEXT_PLAIN, buffer.array());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });


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
