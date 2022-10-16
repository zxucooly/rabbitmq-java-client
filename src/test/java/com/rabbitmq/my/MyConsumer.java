package com.rabbitmq.my;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.impl.LogTrafficListener;
import com.rabbitmq.client.impl.StandardMetricsCollector;

public class MyConsumer {
	public static void main(String[] args) throws IOException, TimeoutException {
		ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(10, 20, 10, TimeUnit.SECONDS, new ArrayBlockingQueue<>(20), new ThreadFactory() {
			private final AtomicInteger sequence = new AtomicInteger(1);

			@Override
			public Thread newThread(Runnable r) {
				Thread thread = new Thread(r);
				int seq = this.sequence.getAndIncrement();
				thread.setName("MyConsumer" + seq);
				thread.setDaemon(true);
				return thread;
			}
		});
		ConnectionFactory connectionFactory = new ConnectionFactory();
		//connectionFactory.setPassword("11");
		//connectionFactory.setAutomaticRecoveryEnabled(false);
		// 指标收集
		StandardMetricsCollector metricsCollector = new StandardMetricsCollector();
		connectionFactory.setMetricsCollector(metricsCollector);

		connectionFactory.setTrafficListener(new LogTrafficListener());

		Connection connection = connectionFactory.newConnection(threadPoolExecutor, "my____consumer");
		Channel channel = connection.createChannel();
		HashMap<String, Object> map = new HashMap<>();
		map.put("key1", "val1");
		String myqueue = "myqueue";
		channel.queueDeclare(myqueue, true, false, false, map);

		System.out.println("metricsCollector.getConnections() = " + metricsCollector.getConnections());
		channel.basicConsume(myqueue, new DefaultConsumer(channel) {
			@Override
			public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
				System.out.println("consumerTag = " + consumerTag);
				long deliveryTag = envelope.getDeliveryTag();
				System.out.println("properties = " + properties);
				System.out.println("deliveryTag = " + deliveryTag);
				System.out.println("thread:" + Thread.currentThread().getName() + ", consumer: body = " + new String(body));
				channel.basicAck(deliveryTag, false);
			}
		});
		AMQP.BasicProperties.Builder builder = new AMQP.BasicProperties.Builder();
        channel.basicPublish("", myqueue, builder.build(), "张三".getBytes(StandardCharsets.UTF_8));
		System.out.println("开始消费!");
	}
}
