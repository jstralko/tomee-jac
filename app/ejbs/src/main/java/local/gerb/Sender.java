package local.gerb;

import javax.annotation.PostConstruct;
import javax.ejb.EJBException;
import javax.ejb.Stateless;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Properties;

@Stateless
public class Sender {

    private ConnectionFactory connectionFactory;

    private Queue myQueue;

    private String userName;
    private String password;


    @PostConstruct
    public void onPostConstruct() {
        String jndiParameters = System.getProperty("JNDI_PARAMETERS");
        if (jndiParameters == null) {
            throw new EJBException("No JNDI_PARAMETERS found!");
        }

        try {
            Properties props = org.jboss.resource.adapter.jms.inflow.JmsActivation.convertStringToProperties(jndiParameters);
            InitialContext ic = new InitialContext(props);

            connectionFactory = (ConnectionFactory)ic.lookup("SBCF");
            myQueue = (Queue)ic.lookup("QUEUE/MyQueue");

        } catch (NamingException ne) {
            throw new EJBException(ne);
        }

        userName = System.getProperty("USER_NAME");
        password = System.getProperty("PASSWORD");

        if (userName == null || password == null) {
            throw new EJBException("Username and/or password not found!");
        }

    }

    public void sendJMS(String text) throws JMSException {

        Connection connection = null;
        Session session = null;

        try {
            connection = connectionFactory.createConnection(userName, password);
            //Connection is get from ConnectionFactory instance and it is started.
            connection.start(); 

            //Creates a session to created connection.
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE); 

            //Creates a MessageProducer from Session to the Queue.
            MessageProducer producer = session.createProducer(myQueue);
            producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT); 

            TextMessage message = session.createTextMessage(text);

            //Tells the producer to send the message
            producer.send(message); 
        }
        finally {
            if (session != null) session.close(); 
            if (connection != null) connection.close();
        }
    }
}
