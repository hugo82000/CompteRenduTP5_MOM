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

Dans le main nous avons créé une boucle while pour arrêter d'envoyer après l'envoi de 11 dates (de 0 à 10).
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

<img src="https://user-images.githubusercontent.com/48157631/74085976-f67ddd80-4a7e-11ea-894c-f0f1f11ea6c3.PNG" height="150">

**Diffusion**

Pour l'instant les consommateurs utilisent chacun une file temporaire distincte grâce au code suivant. De cette manière, l'exchange distribue à chaque file et chaque consommateur reçoit le même message.

```jsx
String queueName = channel.queueDeclare().getQueue();
```

En lançant un envoi et en observant la reception sur deux consommatteurs on observe donc bien qu'ils reçoivent les mêmes messages.

<img src="https://user-images.githubusercontent.com/48157631/74086317-06e38780-4a82-11ea-965b-605d5ed7d1fa.PNG" height="200">

**Partage d'une même file**

Nous voulons maintenant que les messages ne soient plus dupliqués mais distribués entre les consommateurs via une file 'file_date'

Pour celà on crée la file de la manière suivante :

```jsx
String queueName = "file_date";
channel.queueDeclare(queueName, false, false, false, null);
```
<img src="https://user-images.githubusercontent.com/48157631/74086503-b4a36600-4a83-11ea-80cf-b965fdb3c2dc.PNG" height="200">

Ici on voit que nous envoyons 11 dates et que les deux consommateurs se partage les messages. En effet, dans la première console de réception on observe 6 messages espacés de 2 secondes et dans l'autre, les 5 autres messages espacés de 2 secondes également.

**Routage simple**
