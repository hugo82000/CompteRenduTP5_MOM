# Compte Rendu TP5 MOM

*Simon Le Guevel* - 
*Hugo Mathieux*

## PARTIE 1 : Création du serveur et communication
**CloudAMQP**

Pour réaliser ce TP, nous avons choisi d’utiliser le site [CloudAMQP](https://www.cloudamqp.com/) pour créer notre propre serveur. Après avoir créé une instance, nous avons copié l’URL AMQP que nous avons utilisé dans le code pour l'envoi et pour la réception.

```jsx
ConnectionFactory factory = new ConnectionFactory();
factory.setUri("amqp://bkimztxi:kfDaEm6D0v51CY45fcsUgNB7vmMRM3a0@stingray.rmq.cloudamqp.com/bkimztxi");
Connection connection = factory.newConnection();
```
Cette étape permet de créer la connexion pour communiquer avec notre serveur.

```jsx
Channel channel = connection.createChannel();
```
Cette étape permet de créer le canal permettant d'échanger les messages.

**RabbitMq Manager**

Ensuite on utilise [RabbitMQ Manager](https://www.rabbitmq.com/) pour afficher les messages MQTT qui transitent sur le serveur.

## PARTIE 2 : Envoi d'une date

On veut maintenant envoyer une date sur le serveur et observer sa réception.

On crée la méthode `getDate()` côté producteur.
```jsx
private static String getDate()
    {
		return (new Date()).toString();
    }
```

Dans le main nous avons créé une boucle while pour arrêter d'envoyer après l'envoi de 10 dates.
```jsx
while (nbDate <= 10) 
    {
    nbDate++;
    channel.basicPublish(EXCHANGE_NAME, "",nullgetDate().getBytes("UTF-8"));
    System.out.println(" [x] Sent '" + getDate() + "'");
    Thread.sleep(1000);
    }
```
La commande `Thread.sleep(1000)` permet de suspendre le thread en cours pendant la période donnée en milliseconde. On s'en sert ici pour attendre une seconde avant d'envoyer une nouvelle fois la date.

En lançant le programme on constate que le programme génère une activité dans la console d'administration.
Au niveau du consommateur on bserve bien la réception de la date toutes les secondes.
