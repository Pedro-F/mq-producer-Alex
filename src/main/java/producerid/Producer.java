package producerid;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


@RestController
@EnableAutoConfiguration
@EnableJms
/**
 * Clase que produce una entrada en la cola JBoss EAP AMQ
 * @author pedro.alonso.garcia
 *
 */
public class Producer {
	
	
	//Variables Globales
	Connection conn = null;
	Session session = null;
//	MessageProducer producer = null;
	
	private Topic topic;
    private MessageProducer publisher;
	
	//long contador = 0;
	
	@RequestMapping("/")
	//String home(@RequestParam(value="pNumero", defaultValue="1") String pNumero) {
		String home(@RequestParam(value="id", defaultValue="SIN PARAMETRO") String id,
					@RequestParam(value="size", defaultValue="1") String size,
					@RequestParam(value="init", defaultValue="1") String init) {
		
		int inicio;
		int tamanio;
		
		try {
			if (conn == null){
				init();
			}
			try{
				tamanio = Integer.parseInt(size);
			}
			catch(Exception e){
				tamanio = 1;
			}
			try{
				inicio = Integer.parseInt(init);
			}
			catch(Exception e){
				inicio = 1;
			}
			
			if (inicio == 1 && tamanio == 1){
				sendMessage2Q(id);
			}
			else{
				tamanio = tamanio + inicio;
				while(inicio<=tamanio){
					
					sendMessage2Q("" + inicio);
					inicio++;
				}
			}
				
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally{		
		}
		
		return "<strong>Productor</strong> <br>Listo para enviar mensajes .</br>";
	}

	
	private void init() {
		// TODO Auto-generated method stub
		
		conn = ProducerConnection.getConnection();
		
		try{
			// Create a Session
			session = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);

            // Create the destination (Topic or Queue)
//            Destination destination = session.createQueue("TEST.MECCANO");
            
            
            topic = session.createTopic("VirtualTopic.PruebaAlex");
            publisher = session.createProducer(topic);
            publisher.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

            // Create a MessageProducer from the Session to the Topic or Queue
//            producer = session.createProducer(destination);
//            producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
		
		 }
	    catch (Exception e) {
        System.out.println("Init Caught: " + e);
        e.printStackTrace();
 
    }
	}


	/*******************************************
	 * MAIN                                    *
	 * @param args                             *
	 * @throws Exception  
	 * @author pedro.alonso.garcia                     *
	 ******************************************/
	public static void main(String[] args) throws Exception {
		SpringApplication.run(Producer.class, args);
	}

	
	/**
	 * Método que envía el mensaje 
	 * @param idMessage
	 * @author pedro.alonso.garcia
	 */
    private void sendMessage2Q(String idMessage) {
        try {         
            // Create a messages
            
            Evento myEvent = new Evento(idMessage);
            
            Gson gson = new GsonBuilder().create();
            String text = gson.toJson(myEvent);
            
            TextMessage message = session.createTextMessage(text);
                
            String logMessage = "@@@ "+ idMessage + " @@@ -- Sent message: "+ text;
 
            // Tell the producer to send the message
            System.out.println(logMessage);
            
//            producer.send(message);
            publisher.send(message);
             
        }
        catch (Exception e) {
            System.out.println("Caught: " + e);
            e.printStackTrace();
           
        }
    }
	    
	 
	   
}
