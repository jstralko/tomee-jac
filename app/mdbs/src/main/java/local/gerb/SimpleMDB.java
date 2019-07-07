package local.gerb;

import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.ejb.ActivationConfigProperty;
import java.util.logging.Logger;

@MessageDriven(
    mappedName = "QUEUE/MyQueue",
    activationConfig = { 
        @ActivationConfigProperty(propertyName = "user", propertyValue="RootManageSharedAccessKey"),
        @ActivationConfigProperty(propertyName ="password", propertyValue="YhAJGyq4HsqADXFdgNm8S01xEXKvdk5QClpTqV7wIa8="),
        @ActivationConfigProperty(propertyName = "jndiParameters", propertyValue="java.naming.factory.initial=org.apache.qpid.jms.jndi.JmsInitialContextFactory;connectionfactory.SBCF=amqps://gerb-tomee.servicebus.windows.net;queue.QUEUE=MyQueue"),
        @ActivationConfigProperty(propertyName = "connectionFactory", propertyValue = "SBCF"),
        @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue") 
    }
)
public class SimpleMDB implements MessageListener {

    private static final Logger logger = Logger.getLogger(SimpleMDB.class.getName());

    public void onMessage(Message message) {
        try {
            logger.info("onMessage: " + message.getJMSMessageID());
        } catch (JMSException e) {
            throw new IllegalStateException(e);
        }
    }
}