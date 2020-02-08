package fr.istic.date.lb;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

public class RecevoirDate {

	private static final String EXCHANGE_NAME = "file_date";

	public static void main(String[] argv) throws Exception {
		ConnectionFactory factory = new ConnectionFactory();
		factory.setUri("amqp://bkimztxi:kfDaEm6D0v51CY45fcsUgNB7vmMRM3a0@stingray.rmq.cloudamqp.com/bkimztxi");
		Connection connection = factory.newConnection();
		Channel channel = connection.createChannel();

		channel.exchangeDeclare(EXCHANGE_NAME, "fanout");

		String queueName = "file_date";
		channel.queueDeclare(queueName, false, false, false, null);

		channel.queueBind(queueName, EXCHANGE_NAME, "");

		System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

		DeliverCallback deliverCallback = (consumerTag, delivery) -> {
			String message = new String(delivery.getBody(), "UTF-8");

			System.out.println(" [x] Received '" + message + "'");
		};
		channel.basicConsume(queueName, true, deliverCallback, consumerTag -> {
		});
	}
}
