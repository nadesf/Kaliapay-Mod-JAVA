# **Module de paiement JAVA.**

Ce module de paiement a été créé afin de faciliter l'intégration de Kaliapay
dans les applications développées en JAVA.

## **Prérequis**
- Avoir un compte kaliapay. Si vous ne l'avez pas, vous pouvez en créer 1 sur :(https://kaliapay.com/back-office/signup/)
- Configurer l'url de notification, l'url de retour et l'url d'annulation dans votre espace personnel dans la section développeur sur la plateforme de kaliapay. Si vous ne l'avez pas fait connectez-vous et dans la section développeur, cliquez sur "Mettre à jour Service".
- Avoir installé le module `okhttp3` et `gson`. Si vous ne l'avez pas fait vous pouvez utilisé le gestionnaire de package maven. Dans votre fichier pom.xml collez les dépendances comme suit : 
```xml
<dependencies>
    ...
    <dependency>
      <groupId>com.squareup.okhttp3</groupId>
      <artifactId>okhttp</artifactId>
      <version>4.10.0</version>
    </dependency>
    <dependency>
      <groupId>com.google.code.gson</groupId>
      <artifactId>gson</artifactId>
      <version>2.8.9</version>
    </dependency>
</dependencies>
```

## **1. Installation**
Il vous suffit de télécharger le fichier `CollectPayment.java` et de le
placer dans votre projet.
```Ex : 
\project
   Main.java
   CollectPayment.java
```

## **2. Configuration**
Pour toutes collecte de paiement vous devez fournis les informations suivantes :
1. **apikey** : C'est votre clé d'api. Chaque clé d'api est unique par marchand.
2. **tokenid** : Un jeton d'authentification qu'utilise kaliapay pour s'assurer que la demande est légitime.
3. **service** : Correspond à l'identifiant du service de collecte.
 
Il vous faut toutes ces données si vous voulez utiliser le module correctement pour effectuer des collectes.

Suivez la procédure ci-dessous afin de configurer correctement le module pour votre usage.
### **Etape 1 :** Récupérer la clé de service.
Rendez-vous dans votre espace personnel sur la plateforme de Kaliapay. Dans la section "Développeur", vous trouverez un tableau contenant les services auxquels vous êtes abonnés. Copiez le contenu de la colonne "Service" et conservez-le. Cette information sera nécessaire pour configurer correctement le module de paiement.

### **Etape 2 :** Création du fichier de configuration.

Pour créer le fichier de configuration, vous devez exécuter la méthode `get_config_file`  du module Kaliapay que vous venez de télécharger, comme suit :
```java
import com.google.gson.JsonObject;

public class App 
{
    public static void main( String[] args )
    {
        try {
            JsonObject result = CollectPayment.getConfigFile("demo@example.com", "password");
            System.out.println(result);
        }catch (IOException e){
            e.printStackTrace();
        }
    }

}
```
Si vos identifiants sont corrects, cela créera un fichier `config.json` situé dans le même répertoire que le fichier Kaliapay.py, avec le contenu suivant :

```
{
    "tokenid": "votre_token",
    "apikey": "votre_api_key",
    "service": "collez_votre_cle_de_service_ici"
}
```

Une fois que vous avez un fichier `config.json` comme celui là alors que vous êtes prêt.
**NB** : Une fois que vous avez le fichier de `config.json` vous pourrez l'utiliser à tout moment et quelque soit le langage (Python, PHP, JAVA).


## **3. Collecter des paiements** 
Pour initialiser une collecte de paiement, vous pouvez appeler la méthode `intialize(montant, id_transaction)`.
comme vous pouvez le constater, cette méthode prend deux paramètres : le `montant` et `l'identifiant de la transaction.` Voici un exemple de code :

```java
import java.io.IOException;
import com.google.gson.JsonObject;

public class App 
{
    public static void main( String[] args )
    {
        try {
            JsonObject result = CollectPayment.initialize(100, "demo");
            System.out.println(result);
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
```
**La réponse** 
```
{
  "result": {
    "created": "29/06/2023 - 08:22:30", 
    "reference": "83f02ac8740d3b7eec1fc8d8cfea7689e2a1fcd0",
    "apikey": "c6sd51c6dsc1ds6c1dsc1csc",
    "service": "040523114619461734",
    "amount": 100,
    "extra": "TXNDDBJDBSQJDBSJQ",
    "target": "mobpay",
    "qrcode_url": "https://paas-qrcode.com/display/108253c02461988dbced9f50cbc9c16991768203/",
    "qrcode_image": "https://paas-qrcode.com/media/barcodes/29062023082229821642.svg",
    "shortened_url": "https://c4p.me/p180p82229934139",
    "description": ""
  },
  "state": "success",
  "message": "Action effectuée avec Succès"
}
```

Explication du resulat : 
- **created** : Date de création de la demande de collecte.
- **reference** : Référence de la transaction. Vous aurez besoin de cette référence lorsque vous voudrez connaître le statut de la transaction.
- **apikey** : C'est votre apikey et celle utilisée pour effectuer cette de demande de collecte.
- **service** :  L'identifiant du service de collecte de paiement auquel vous êtes abonné.
- **amount** : Montant de la transaction.
- **extra** :  ID de la transaction que vous avez fourni lors de l'initialisation de la transaction.
- **target** : Service de collecte interne à kaliapay.
- **qrcode_url** : Lien vers un QR Code qui permet à l'utilisateur de scanner et de procéder au paiement.
- **qrcode_image** : Image du QR Code. Cette image peut être téléchargée par toute personne qui va scanner le QR Code pour effectuer le paiement.
- **shortened_url** : Cette URL redirige vers le guichet de paiement pour procéder à la transaction.

## **4. Obtenir le statut d'une transaction** 
Pour obtenir le statut d'une transaction, vous devez appeler la méthode `get_transaction_status()` en lui passant la référence de la transaction comme paramètre. Voici un exemple de code :
```java 
import java.io.IOException;
import com.google.gson.JsonObject;

public class App 
{
    public static void main( String[] args )
    {
        try {
            JsonObject result = CollectPayment.getTransactionStatus("TXN_REFERENCE");
            System.out.println(result);
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}

```
**Réponse 1 :**
```
{
"result": {
        "reference": "",
        "status": "TRANSACTION NOT FOUND",
        "payment_infos": {}
    },
    "state": "success",
    "message": "Action effectuée avec Succès"
}
```
`"status = TRANSACTION NOT FOUND"` : Ce qui signifie que le système n'a pas encore pris en compte cette référence dans le traitement d'une opération de paiement. Une fois que le client est redirigé vers la page de paiement et effectue le scan du code QR ou effectue une redirection manuelle, le système prend automatiquement en compte la référence, à condition qu'elle existe préalablement.

**Réponse 2 :** 
```
{
    "result": {
        "reference": "2eacf6b407a217fad781da55570e521238d410ab",
        "status": "NOT INITIATED",
        "payment_infos": {}
    },
    "state": "success",
    "message": "Action effectuée avec Succès"
}
```
Ici, le client a été redirigé vers la page de paiement. Dans le bloc `"result"`, nous avons comme statut `"NOT INITIATED"`, ce qui signifie que le client est toujours sur la page, à l'étape du choix du canal de paiement. À ce moment-là, les différents canaux de paiement lui sont proposés. Il doit sélectionner un canal, fournir les informations nécessaires et procéder au paiement.

**Réponse 3 :**
```
{
"result": {
    "reference": "2eacf6b407a217fad781da55570e521238d410ab",
    "status": "INITIATED",
    "payment_infos": {
    "name": "ORANGE",
    "txnreference": "op4e391681-ecb4-4034-88c5-c005ea7e5dad07062022181013",
    "shortreference": "6KEC841599",
    "external_reference": "b947db90-ffd1-41f4-9bbd-aee9042a23b9",
    "related_qrcode": "",
    "related_qrcode_url": "",
    "express_data": "2eacf6b407a217fad781da55570e521238d410ab",
    "created_at": "07/06/2022 - 18:10:13",
    "htime": "07/06/2022 - 18:10:13",
    "amount": 500,
    "status": "pending",
    "extra_data": "test_custom",
    "apikey": "c6sd51c6dsc1ds6c1dsc1csc",
    "service": "020322171826010677",
    "notif_number": "0709430661",
    "comments": "",
    }
},
"state": "success",
"message": "Action effectuée avec Succès"
}
```

Ici, le statut est `"INITIATED"`. Le client a sélectionné son canal de paiement préféré, dans l'exemple `"Orange Money"`. Nous constatons que l'opération n'est pas encore terminée. Dans le bloc `"payment_infos"`, nous avons tous les détails de la transaction. lorsque `status = pending` cela signifie que la transaction est en cours. `status = success` cela signifie que la transaction a reussie et enfin `success = failed` Cela signifie que la transaction a echoué.

**Réponse 4 :**
```
{
"result": {
    "reference": "2eacf6b407a217fad781da55570e521238d410ab",
    "status": "INITIATED",
    "payment_infos": {
    "name": "ORANGE",
    "txnreference": "op4e391681-ecb4-4034-88c5-c005ea7e5dad07062022181013",
    "shortreference": "6KEC841599",
    "external_reference": "b947db90-ffd1-41f4-9bbd-aee9042a23b9",
    "related_qrcode": "",
    "related_qrcode_url": "",
    "express_data": "2eacf6b407a217fad781da55570e521238d410ab",
    "created_at": "07/06/2022 - 18:10:13",
    "htime": "07/06/2022 - 18:10:13",
    "amount": 500,
    "status": "success", #Le status dans payment info est à "success"
    "extra_data": "test_custom",
    "apikey": "c6sd51c6dsc1ds6c1dsc1csc",
    "service": "88878785454646464",
    "notif_number": "0709430661",
    "comments": "",
    }
},
"state": "success",
"message": "Action effectuée avec Succès"
}
```

Ici, le `status` dans payment_infos est à `success`, ce qui signifie que la transaction est terminée et s'est déroulée avec succès !