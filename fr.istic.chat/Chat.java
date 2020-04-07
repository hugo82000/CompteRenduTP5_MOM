package fr.istic.chat;

import java.util.Scanner;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

public class Chat {

	private static final String EXCHANGE_NAME = "chat";

	public static void main(String[] argv) throws Exception {
		ConnectionFactory factory = new ConnectionFactory();
		factory.setUri("amqp://qbipahvo:YklkSIEITfdF1E7FrLisRNUvNeZwBJrv@stingray.rmq.cloudamqp.com/qbipahvo");
		try (Connection connection = factory.newConnection(); Channel channel = connection.createChannel()) {
			channel.exchangeDeclare(EXCHANGE_NAME, "topic");

			while (true) {

				Scanner sc = new Scanner(System.in);
				System.out.println("Identify yourself:");
				String nom = sc.nextLine();
				System.out.println("Choose a topic:");
				String topicChoisi = sc.nextLine();
				System.out.println("Write a message:");
				String messageChoisi = sc.nextLine();

				if (topicChoisi.equals("geeks")) {
					String.join(" ", argv);

					channel.basicPublish(EXCHANGE_NAME, "chat.geeks", null, nom.getBytes("UTF-8"));
					String queueName1 = channel.queueDeclare().getQueue();
					channel.queueBind(queueName1, EXCHANGE_NAME, "chat.geeks");

					channel.basicPublish(EXCHANGE_NAME, "chat.geeks", null, messageChoisi.getBytes("UTF-8"));
					String queueName = channel.queueDeclare().getQueue();
					channel.queueBind(queueName, EXCHANGE_NAME, "chat.geeks");
				}

				else if (topicChoisi.equals("trollers")) {
					String.join(" ", argv);

					channel.basicPublish(EXCHANGE_NAME, "chat.trollers", null, nom.getBytes("UTF-8"));
					String queueName1 = channel.queueDeclare().getQueue();
					channel.queueBind(queueName1, EXCHANGE_NAME, "chat.trollers");

					channel.basicPublish(EXCHANGE_NAME, "chat.trollers", null, messageChoisi.getBytes("UTF-8"));
					String queueName = channel.queueDeclare().getQueue();
					channel.queueBind(queueName, EXCHANGE_NAME, "chat.trollers");
				}

				else if (topicChoisi.equals("")) {
					String.join(" ", argv);
					topicChoisi = ("mri");

					channel.basicPublish(EXCHANGE_NAME, "chat.mri", null, nom.getBytes("UTF-8"));
					String queueName1 = channel.queueDeclare().getQueue();
					channel.queueBind(queueName1, EXCHANGE_NAME, "chat.mri");

					channel.basicPublish(EXCHANGE_NAME, "chat.mri", null, messageChoisi.getBytes("UTF-8"));
					String queueName = channel.queueDeclare().getQueue();
					channel.queueBind(queueName, EXCHANGE_NAME, "chat.mri");
				}

				System.out.println();
				System.out.println(topicChoisi + "#" + nom + ">" + messageChoisi);
				System.out.println();
			}
		}
	}

}
