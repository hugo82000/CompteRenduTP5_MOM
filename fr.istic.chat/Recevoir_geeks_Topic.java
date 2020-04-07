package fr.istic.chat;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

public class Recevoir_geeks_Topic {
	private static final String EXCHANGE_NAME = "chat";
	private static int mot = 0;
	private static int tour = 0;

	public static void main(String[] argv) throws Exception {
		ConnectionFactory factory = new ConnectionFactory();
		factory.setUri("amqp://qbipahvo:YklkSIEITfdF1E7FrLisRNUvNeZwBJrv@stingray.rmq.cloudamqp.com/qbipahvo");
		Connection connection = factory.newConnection();
		Channel channel = connection.createChannel();

		channel.exchangeDeclare(EXCHANGE_NAME, "topic");
		String queueName = channel.queueDeclare().getQueue();
		channel.queueBind(queueName, EXCHANGE_NAME, "chat.geeks");

		System.out.println("------ Welcome to the geeks topic ------");
		System.out.println();

		DeliverCallback deliverCallback = (consumerTag, delivery) -> {
			String message = new String(delivery.getBody(), "UTF-8");

			if (mot == 1) {
				System.out.println(">> " + message);
				tour++;
			}
			if (mot == 0) {
				System.out.println(message);
				mot++;
			}

			if ((tour == 1) && (mot == 1)) {
				tour = 0;
				mot = 0;
			}
		};
		channel.basicConsume(queueName, true, deliverCallback, consumerTag -> {
		});

	}
}
