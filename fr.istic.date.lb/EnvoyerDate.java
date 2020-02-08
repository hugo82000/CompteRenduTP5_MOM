package fr.istic.date.lb;

import java.util.Date;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class EnvoyerDate {

	private static final String EXCHANGE_NAME = "file_date";
	static int nbDate = 0;

	private static String getDate() {
		return (new Date()).toString();
	}

	public static void main(String[] argv) throws Exception {
		ConnectionFactory factory = new ConnectionFactory();
		factory.setUri("amqp://bkimztxi:kfDaEm6D0v51CY45fcsUgNB7vmMRM3a0@stingray.rmq.cloudamqp.com/bkimztxi");
		try (Connection connection = factory.newConnection(); Channel channel = connection.createChannel()) {
			channel.exchangeDeclare(EXCHANGE_NAME, "fanout");

			while (nbDate <= 10) {

				nbDate++;
				channel.basicPublish(EXCHANGE_NAME, "", null, getDate().getBytes("UTF-8"));
				System.out.println(" [x] Sent '" + getDate() + "'");
				Thread.sleep(1000);
			}
		}
	}

}
