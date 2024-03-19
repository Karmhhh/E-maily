#### E-Maily Docs

E-maily e' un microservizio springBoot che utilizza SpringMail e jakarta.mail per la configurazione di end point che permettono di instaurare connessione con un server di posta e permette di effettuare il "send" e "retrive" delle email dal server e successivamente memorizzarle in Redis.

##### Configurazione Redis, Redis Commander, Redis Connection

###### Redis

```
$redis-server --port 6969
```

##### Redis-Commander

```
$redis-commander
```

##### Redis Connection

```
@Configuration
public class ClientConfig {

  @Value("${connection.variable}")
    private String connectionValueString;

    @Bean
    public StatefulRedisConnection redisConnection() {
        RedisClient redisClient = RedisClient.create(connectionValueString);
        StatefulRedisConnection<String, String> connection = redisClient.connect();
        return connection;
    }
}
```

In questo Modo instauro una connessine con Redis alla porta specificata e precedentemente aperta 6969.

###### Email Configuration ( Session, Store, Folder )

La configurazione tramite Bean mi permette di avere la stessa e singola instanza disponibile poi in tutto il progetto ( Singleton )

###### Session Configuration

```
@Configuration
public class MailSessionConfiguration {
    

    @Bean
    public Session mailSession() {
        Properties properties = new Properties();
        properties.setProperty("mail.pop3.host", "pop3.mailtrap.io");
        properties.setProperty("mail.pop3.port", "1100"); // Or "9950" if SSL is required
        properties.setProperty("mail.pop3.starttls.enable", "true");
        properties.setProperty("mail.pop3.auth", "true");

        return Session.getDefaultInstance(properties);
    }
}

```

###### Store Configuration

```
@Configuration
public class MailStoreConfiguration {

    @Autowired
    private Session mailSession;

    @Bean
    public Store mailStore() throws MessagingException {
        Store store = mailSession.getStore("pop3");
        store.connect("pop3.mailtrap.io", "b734cbf3569f73", "c003eedf13a28e");
        return store;
    }
}
```

###### Folder Configuration

```
@Configuration
public class MailFolderConfiguration {
 
    @Autowired
    private Store mailStore;

    @Bean
    public Folder mailFolder() throws MessagingException {
        Folder folder = mailStore.getFolder("INBOX");
        folder.open(Folder.READ_ONLY);
        return folder;
    }
}
```

###### Email ServiceImpl ( Componente Template per l'invio delle email )

```
@Component
public class EmailServiceImpl {


    @Autowired
    private JavaMailSender emailSender;

    public void sendSimpleMessage(EmailObj obj) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(obj.getMittente());
        message.setTo(obj.getDestinatario());
        message.setSubject(obj.getEmailObject());
        message.setText(obj.getEmailBody());
        for (String cc : obj.getCCs()) {
            message.setCc(cc);  
        }
      
        emailSender.send(message);
    }
}
```

###### Email Service ( Salva in redis, invia email, retrive delle email dal server usando pop3s)

```

@Service
public class EmailService {

    @Autowired
    private StatefulRedisConnection<String, String> connection;

    @Autowired
    private EmailServiceImpl emailService;

    @Autowired
    private Folder inbox;

    @Autowired
    private Store store;

    public String inboxMessage() throws Exception {

        // Recupera i messaggi
        Message[] messages = inbox.getMessages();
        List<String> cc = new ArrayList<>();
        EmailObj obj = new EmailObj();
        // Converti i messaggi in una lista di oggetti JSON
        List<Map<String, Object>> emails = new ArrayList<>();
        
        for (Message message : messages) {

            Map<String, Object> emailData = new HashMap<>();
            emailData.put("from", Arrays.toString(message.getFrom()));
            emailData.put("subject", message.getSubject());
            emailData.put("content", message.getContent().toString());
            emailData.put("to", Arrays.toString(message.getAllRecipients()));
            emailData.put("sentDate", message.getSentDate());
            Address[] ccRecipients = message.getRecipients(Message.RecipientType.CC);

            if (ccRecipients != null && ccRecipients.length > 0) {
                for (Address address : ccRecipients) {
                    cc.add(address.toString());
                }
                emailData.put("cc", Arrays.toString(ccRecipients));
            } else {
                emailData.put("cc", ""); // If no CC recipients
            }
            emails.add(emailData);

            obj.setEmailId(null);
            obj.setDestinatario(emailData.get("to").toString());
            obj.setMittente(emailData.get("from").toString());
            obj.setEmailObject(emailData.get("subject").toString());
            obj.setEmailBody(emailData.get("content").toString());
            obj.setDateSend(emailData.get("sentDate").toString());
            obj.setState(false);
            obj.setCCs(cc);
            RedisCommands<String, String> syncCommands = connection.sync();

            syncCommands.hset("Email_" + obj.getEmailId(), "Email_UUUID", obj.getEmailId().toString());
            syncCommands.hset("Email_" + obj.getEmailId(), "Mittente", obj.getMittente());
            syncCommands.hset("Email_" + obj.getEmailId(), "Destinatario", obj.getDestinatario());
            syncCommands.hset("Email_" + obj.getEmailId(), "CCs", obj.getCCs().toString());
            syncCommands.hset("Email_" + obj.getEmailId(), "Email_Object", obj.getEmailObject());
            syncCommands.hset("Email_" + obj.getEmailId(), "Email_Body", obj.getEmailBody());
            syncCommands.hset("Email_" + obj.getEmailId(), "Date_Send", obj.getDateSend());
            syncCommands.hset("Email_" + obj.getEmailId(), "State", obj.isState().toString());

        }

        // Converti la lista di email in JSON
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(emails);

        // Chiudi la cartella "inbox" e lo store
        inbox.close(false);
        store.close();
        return json;
    }

    public String saveEmail(EmailObj obj) {

        // Converti l'oggetto EmailObj in formato JSON utilizzando Jackson
        ObjectMapper mapper = new ObjectMapper();
        String jsonRecord;
        try {
            jsonRecord = mapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            // Gestione dell'eccezione, ad esempio logger.error("Errore durante la
            // conversione in JSON", e);
            jsonRecord = "{}";
        }

        emailService.sendSimpleMessage(obj);

        return jsonRecord;
    }

}
```

###### Email Controller

Successivamente nel controller Email Controller creo gli endPoint utilizzando i servizi definiti nel Service.

```
@RestController
@RequestMapping("/api/email")

public class EmailControl {

    @Autowired
    EmailService emailService;

    @Autowired
    Store store;

    @PostMapping("/newEmail")
    public String saveEmail(@RequestBody EmailObj obj) throws JsonProcessingException {
        return emailService.saveEmail(obj);
    }

    @GetMapping("/retriveMails")
    public String retriveMails() {

        try {
            return emailService.inboxMessage();
        } catch (Exception e) {
            e.printStackTrace();
            return "No Response";
        }
        
    }

}

```

###### EmailObj

l'email obj alla quale si fa riverimento e' il seguente

```
public class EmailObj {

    @Id
    private UUID emailId;
    private String mittente;
    private String destinatario;
    private String emailObject;
    private String emailBody;
    private List<String> ccs = new ArrayList<>();
    private LocalDateTime dateSend;
    private boolean state;

    public UUID getEmailId() {
        return emailId;
    }
    
    // Genera UUID casuale se non specificato direttamente nel setEmailId quando richiamato
    public void setEmailId(UUID emailId) {
        if (emailId == null) {
            this.emailId = UUID.randomUUID();
        } else {
            this.emailId = emailId;
        }

    }

    public String getMittente() {
        return mittente;
    }

    public void setMittente(String mittente) {
        this.mittente = mittente;
    }

    public String getDestinatario() {
        return destinatario;
    }

    public void setDestinatario(String destinatario) {
        this.destinatario = destinatario;
    }

    public String getEmailObject() {
        return emailObject;
    }

    public void setEmailObject(String emailObject) {
        this.emailObject = emailObject;
    }

    public String getEmailBody() {
        return emailBody;
    }

    public void setEmailBody(String emailBody) {
        this.emailBody = emailBody;
    }

    public List<String> getCCs() {
        return ccs;
    }

    public void setCCs(List<String> ccs) {
        this.ccs = ccs;
    }

    public String getDateSend() {
        return dateSend.toString();
    }

    public void setDateSend(LocalDateTime dateSend) {
        this.dateSend = LocalDateTime.now();
    }

    public Integer isState() {
        return state ? 1 : 0;
    }

    public void setState(boolean state) {
        this.state = state;
    }

    @Override
    public String toString() {
        return "EmailObj {emailId:" + emailId + ", mittente:" + mittente + ", destinatario:" + destinatario
                + ", emailObject:" + emailObject + ", emailBody:" + emailBody + ", ccs:" + ccs + ", dateSend:"
                + dateSend + ", state:" + state + "}";
    }

}

```

#### Flusso di dati: 
POP3 è unidirezionale, scaricando e-mail dal server al dispositivo. Post Office Protocol – Versione 3.
SMTP è utilizzato per inviare email. Simple Mail Transfer Protocol.
IMAP è bidirezionale e consente di gestire le tue email su più dispositivi in modo sincronizzato. Internet Message Access Protocol.

#### Memorizzazione delle email: 
POP3 rimuove le email dal server e le memorizza sul dispositivo locale.

In conclusione, la scelta tra POP3, SMTP e IMAP dipende dalle tue esigenze specifiche. 
Se hai bisogno di accedere alle tue email da più dispositivi in modo sincronizzato, IMAP è la scelta migliore. 
Se desideri scaricare le tue mail localmente, POP3 potrebbe essere la soluzione ideale. 
Infine, per un invio affidabile delle tue comunicazioni, il protocollo SMTP è sicuramente il più efficiente.