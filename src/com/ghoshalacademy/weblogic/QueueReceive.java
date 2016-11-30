
package com.ghoshalacademy.weblogic;
import java.util.Hashtable;
import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
 
public class QueueReceive implements MessageListener
{
    public final static String SERVER="t3://localhost:7001";
    public final static String JNDI_FACTORY="weblogic.jndi.WLInitialContextFactory";
    public final static String JMS_FACTORY="QCF";
    public final static String QUEUE="wsqueue";
    private QueueConnectionFactory qconFactory;
    private QueueConnection qcon;
    private QueueSession qsession;
    private QueueReceiver qreceiver;
    private Queue queue;
    boolean quit = false;
    public static String mens;
 
    
    public void onMessage(Message msg)
    {
        try {
        if (msg instanceof TextMessage)
        {
            mens = ((TextMessage)msg).getText();
        }
        else
        {
            mens = msg.toString();
        }
        System.out.println("Mensaje Recibido: "+ mens);
        
        
        }
        catch (JMSException jmse)
        {
            jmse.printStackTrace();
        }
    }
public void init(Context ctx, String queueName) throws NamingException, JMSException
{
    qconFactory = (QueueConnectionFactory) ctx.lookup(JMS_FACTORY);
    qcon = qconFactory.createQueueConnection();
    qsession = qcon.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
    queue = (Queue) ctx.lookup(queueName);
    qreceiver = qsession.createReceiver(queue);
    qreceiver.setMessageListener(this);
    qcon.start();
}
 
public void close()throws JMSException
{
    qreceiver.close();
    qsession.close();
    qcon.close();
}
 
public static void main(String args[]) throws Exception
    {
       
        InitialContext ic = getInitialContext(SERVER);
        QueueReceive qr = new QueueReceive();
        qr.init(ic, QUEUE);
        
        synchronized(qr)
        {
            while (! qr.quit)
            {
                try
                {
                    qr.wait();
                }
                catch (InterruptedException ie)
                {}
            }
        }
        
        qr.close();
    }
 
private static InitialContext getInitialContext(String url) throws NamingException
    {
        Hashtable env = new Hashtable();
        env.put(Context.INITIAL_CONTEXT_FACTORY, JNDI_FACTORY);
        env.put(Context.PROVIDER_URL, url);
        return new InitialContext(env);
    }




}
