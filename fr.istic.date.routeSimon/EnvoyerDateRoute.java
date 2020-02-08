package fr.istic.date.routeSimon;

import java.util.Date;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class EnvoyerDateRoute {
	private static final String EXCHANGE_NAME = "date_route";
	static int nbDate = 0;

	private static String getDate() {
		return (new Date()).toString();
	}

	private static String getDateGMT() {
		return (new Date()).toGMTString();
	}

	public static void main(String[] argv) throws Exception {
		ConnectionFactory factory = new ConnectionFactory();
		factory.setUri("amqp://qbipahvo:YklkSIEITfdF1E7FrLisRNUvNeZwBJrv@stingray.rmq.cloudamqp.com/qbipahvo");
		try (Connection connection = factory.newConnection(); Channel channel = connection.createChannel()) {
			channel.exchangeDeclare(EXCHANGE_NAME, "direct");
			while (nbDate <= 10) {

				nbDate++;
				String date = getDate();
				String dategmt = getDateGMT();
				channel.basicPublish(EXCHANGE_NAME, "locale", null, getDate().getBytes("UTF-8"));
				channel.basicPublish(EXCHANGE_NAME, "gmt", null, getDateGMT().getBytes("UTF-8"));
				System.out.println(" [x] Sent '" + date + "'");
				System.out.println(" [x] Sent '" + dategmt + "'");
				Thread.sleep(1000);
			}
		}
	}

}
