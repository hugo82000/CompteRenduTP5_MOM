# Compte Rendu TP5 MOM

*Simon Le Guevel* - 
*Hugo Mathieux*

## PARTIE 1 : Création du serveur et communication
### CloudAMQP

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

### Diffusion

Pour l'instant les consommateurs utilisent chacun une file temporaire distincte grâce au code suivant. De cette manière, l'exchange distribue à chaque file et chaque consommateur reçoit le même message.

```jsx
String queueName = channel.queueDeclare().getQueue();
```

En lançant un envoi et en observant la reception sur deux consommatteurs on observe donc bien qu'ils reçoivent les mêmes messages.

<img src="https://user-images.githubusercontent.com/48157631/74086317-06e38780-4a82-11ea-965b-605d5ed7d1fa.PNG" height="200">

### Partage d'une même file

Nous voulons maintenant que les messages ne soient plus dupliqués mais distribués entre les consommateurs via une file 'file_date'

Pour celà on crée la file de la manière suivante :

```jsx
String queueName = "file_date";
channel.queueDeclare(queueName, false, false, false, null);
```
<img src="https://user-images.githubusercontent.com/48157631/74086503-b4a36600-4a83-11ea-80cf-b965fdb3c2dc.PNG" height="200">

Ici on voit que nous envoyons 11 dates et que les deux consommateurs se partage les messages. En effet, dans la première console de réception on observe 6 messages espacés de 2 secondes et dans l'autre, les 5 autres messages espacés de 2 secondes également.

### Routage simple

Nous voulons maintenant utiliser le routage pour envoyer la date gmt sur une clef de routage "gmt" et la date locale sur une clef de routage "locale". Ainsi, on pourra récupérer les dates GMT et locale sur deux consommateurs différents en les envoyant avec un producteur commun.

Après avoir :
- Modifié l'exchange vers `date_route`
- modifié le type d'exchange pour `direct` au lieu de `fanout`
- ajouté une méthode `getDateGMT()` dans le producteur
- ajouté une publication pour pour la date GMT dans la boucle while du producteur
- modifié les binding dans les consommateurs

On observe les deux consoles récupérant les messages correspondants à leur clé de routage.

<img src="https://user-images.githubusercontent.com/48157631/74087463-cb01ef80-4a8c-11ea-826f-48d1fc364275.PNG" height="200">

### Routage par topic

Le routage par topic est similaire à la méthode vu précédemment.

Après avoir :
- modifié l'exchange pour `date_topic`
- modifié le type d'exchange pour `topic` au lieu de `direct`
- modifié le producteur pour envoyer la date locale sur le topic `date.locale` et la date gmt sur le topic `date.gmt`
- créé 3 consommateurs pour recevoir sur `date.locale`, `date.gmt`, `date.#` (les 2 topics de date)

*WildCard*

`#` se substitue à zéro ou plusieurs mots ( dans notre cas `date.#` prend en compte `date.locale` et `date.gmt`)

On observe donc les consoles avec l'envoi et les trois réceptions :

<img src="https://user-images.githubusercontent.com/48157631/74087861-66489400-4a90-11ea-8714-4f8a0eaa1503.PNG" height="200">
<img src="https://user-images.githubusercontent.com/48157631/74087859-65affd80-4a90-11ea-90ae-9da6dda5781a.PNG" height="200">


## PARTIE 3 : Création d'un chat

Pour commencer, j'ai modifié le fichier principal, chargé d'envoyer les messages des différents utilisateurs dans les topics en me basant sur le code du producteur de la partie topic utilisé précédemment.

### Lecture sur clavier

Pour cette partie, j'ai commencé par la demande au clavier dans une boucle `while(1)` afin de demander trois informations à l'utilisateur et de les stocker dans 3 variables différentes `nom`,`topicChoisi`,`messageChoisi`. Dans le main prncipal, avant chaque envoi, ces information seront donc demandées à l'utilisateur.

```jsx
Scanner sc = new Scanner(System.in);
System.out.println("Identify yourself:");
String nom = sc.nextLine();
System.out.println("Choose a topic:");
String topicChoisi = sc.nextLine();
System.out.println("Write a message:");
String messageChoisi = sc.nextLine();
```

### Envoi sur les différents topics

Ensuite, j'ai utilisé des boucles `if()` pour réagir différemment en fonction du topic choisi par l'utilisateur.

#### Exemple avec le choix du topic `geeks`

```jsx
if (topicChoisi.equals("geeks")) {
	String.join(" ", argv);

	channel.basicPublish(EXCHANGE_NAME, "chat.geeks", null, nom.getBytes("UTF-8"));
	String queueName1 = channel.queueDeclare().getQueue();
	channel.queueBind(queueName1, EXCHANGE_NAME, "chat.geeks");

	channel.basicPublish(EXCHANGE_NAME, "chat.geeks", null, messageChoisi.getBytes("UTF-8"));
	String queueName = channel.queueDeclare().getQueue();
	channel.queueBind(queueName, EXCHANGE_NAME, "chat.geeks");
}
```

Dans cet exemple, on utilise deux variables, une pour envoyer le nom de l'utilisateur et l'autre pour envoyer son message sur le topic geeks.

#### Exemple si l'utilisateur ne choisit pas de topic

```jsx
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
```

Ici, si l'utilisateur ne choisit pas de topic il sera automatiquement dirigé vers le topic `mri`

Ensuite j'ai ajouté un topic `trollers` selon la même structure que les autres.
Après l'envoi, j'ai affiché dans le producteur les informations suivantes :

```jsx
System.out.println(topicChoisi + "#" + nom + ">" + messageChoisi);
```

Ainsi, on peut savoir quel message a été envoyé, dans quel topic il a été envoyé et par quel utilisateur il a été envoyé.

<img src="https://user-images.githubusercontent.com/48157631/78656390-9b723680-78c7-11ea-9c8a-f9febb73a3ed.PNG" height="400">


### Réception dans les différents topics

