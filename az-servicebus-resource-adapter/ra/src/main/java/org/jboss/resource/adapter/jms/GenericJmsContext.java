/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2016, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.jboss.resource.adapter.jms;

import java.io.Serializable;

import javax.jms.BytesMessage;
import javax.jms.ConnectionMetaData;
import javax.jms.Destination;
import javax.jms.ExceptionListener;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.JMSProducer;
import javax.jms.JMSRuntimeException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.QueueBrowser;
import javax.jms.Session;
import javax.jms.StreamMessage;
import javax.jms.TemporaryQueue;
import javax.jms.TemporaryTopic;
import javax.jms.TextMessage;
import javax.jms.Topic;

/**
 * @author <a href="http://jmesnil.net/">Jeff Mesnil</a> (c) 2016 Red Hat inc.
 */
public class GenericJmsContext implements JMSContext {

    private static final String ILLEGAL_METHOD = "This method is not applicable inside the application server. See the JEE spec, e.g. JEE 7 Section 6.7";

    private final JmsSessionFactory sessionFactory;
    private final JmsSession session;

    GenericJmsContext(JmsSessionFactory sessionFactory, JmsSession session) {
        this.sessionFactory = sessionFactory;
        this.session = session;
    }

    @Override
    public JMSContext createContext(int sessionMode) {
        throw new JMSRuntimeException(ILLEGAL_METHOD);
    }

    @Override
    public JMSProducer createProducer() {
        return session.getJMSContext().createProducer();
    }

    @Override
    public String getClientID() {
        return session.getJMSContext().getClientID();
    }

    @Override
    public void setClientID(String clientID) {
        throw new JMSRuntimeException(ILLEGAL_METHOD);
    }

    @Override
    public ConnectionMetaData getMetaData() {
        return session.getJMSContext().getMetaData();
    }

    @Override
    public ExceptionListener getExceptionListener() {
        return session.getJMSContext().getExceptionListener();
    }

    @Override
    public void setExceptionListener(ExceptionListener listener) {
        throw new JMSRuntimeException(ILLEGAL_METHOD);
    }

    @Override
    public void start() {
        session.getJMSContext().start();
    }

    @Override
    public void stop() {
        throw new JMSRuntimeException(ILLEGAL_METHOD);
    }

    @Override
    public void setAutoStart(boolean autoStart) {
        session.getJMSContext().setAutoStart(autoStart);
    }

    @Override
    public boolean getAutoStart() {
        return session.getJMSContext().getAutoStart();
    }

    @Override
    public void close() {
        // #17 - close the session factory to return the managed connection to the pool
        try {
            sessionFactory.close();
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    @Override
    public BytesMessage createBytesMessage() {
        return session.getJMSContext().createBytesMessage();
    }

    @Override
    public MapMessage createMapMessage() {
        return session.getJMSContext().createMapMessage();
    }

    @Override
    public Message createMessage() {
        return session.getJMSContext().createMessage();
    }

    @Override
    public ObjectMessage createObjectMessage() {
        return session.getJMSContext().createObjectMessage();
    }

    @Override
    public ObjectMessage createObjectMessage(Serializable object) {
        return session.getJMSContext().createObjectMessage(object);
    }

    @Override
    public StreamMessage createStreamMessage() {
        return session.getJMSContext().createStreamMessage();
    }

    @Override
    public TextMessage createTextMessage() {
        return session.getJMSContext().createTextMessage();
    }

    @Override
    public TextMessage createTextMessage(String text) {
        return session.getJMSContext().createTextMessage(text);
    }

    @Override
    public boolean getTransacted() {
        return session.getJMSContext().getTransacted();
    }

    @Override
    public int getSessionMode() {
        return session.getJMSContext().getSessionMode();
    }

    @Override
    public void commit() {
        session.getJMSContext().commit();
    }

    @Override
    public void rollback() {
        session.getJMSContext().rollback();

    }

    @Override
    public void recover() {
        session.getJMSContext().recover();
    }

    @Override
    public JMSConsumer createConsumer(Destination destination) {
        return session.getJMSContext().createConsumer(destination);
    }

    @Override
    public JMSConsumer createConsumer(Destination destination, String messageSelector) {
        return session.getJMSContext().createConsumer(destination, messageSelector);
    }

    @Override
    public JMSConsumer createConsumer(Destination destination, String messageSelector, boolean noLocal) {
        return session.getJMSContext().createConsumer(destination, messageSelector, noLocal);
    }

    @Override
    public Queue createQueue(String queueName) {
        return session.getJMSContext().createQueue(queueName);
    }

    @Override
    public Topic createTopic(String topicName) {
        return session.getJMSContext().createTopic(topicName);
    }

    @Override
    public JMSConsumer createDurableConsumer(Topic topic, String name) {
        return session.getJMSContext().createDurableConsumer(topic, name);
    }

    @Override
    public JMSConsumer createDurableConsumer(Topic topic, String name, String messageSelector, boolean noLocal) {
        return session.getJMSContext().createDurableConsumer(topic, name, messageSelector, noLocal);
    }

    @Override
    public JMSConsumer createSharedDurableConsumer(Topic topic, String name) {
        return session.getJMSContext().createSharedDurableConsumer(topic, name);
    }

    @Override
    public JMSConsumer createSharedDurableConsumer(Topic topic, String name, String messageSelector) {
        return session.getJMSContext().createSharedDurableConsumer(topic, name, messageSelector);
    }

    @Override
    public JMSConsumer createSharedConsumer(Topic topic, String sharedSubscriptionName) {
        return session.getJMSContext().createSharedConsumer(topic, sharedSubscriptionName);
    }

    @Override
    public JMSConsumer createSharedConsumer(Topic topic, String sharedSubscriptionName, String messageSelector) {
        return session.getJMSContext().createSharedConsumer(topic, sharedSubscriptionName, messageSelector);
    }

    @Override
    public QueueBrowser createBrowser(Queue queue) {
        return session.getJMSContext().createBrowser(queue);
    }

    @Override
    public QueueBrowser createBrowser(Queue queue, String messageSelector) {
        return session.getJMSContext().createBrowser(queue, messageSelector);
    }

    @Override
    public TemporaryQueue createTemporaryQueue() {
        return session.getJMSContext().createTemporaryQueue();
    }

    @Override
    public TemporaryTopic createTemporaryTopic() {
        return session.getJMSContext().createTemporaryTopic();
    }

    @Override
    public void unsubscribe(String name) {
        session.getJMSContext().unsubscribe(name);
    }

    @Override
    public void acknowledge() {
        session.getJMSContext().acknowledge();
    }
}
