/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2006, Red Hat Middleware LLC, and individual contributors
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
package org.jboss.resource.adapter.jms.inflow;

import org.jboss.logging.Logger;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ServerSession;
import javax.jms.Session;
import javax.jms.XAConnection;
import javax.jms.XASession;
import javax.resource.spi.endpoint.MessageEndpoint;
import javax.resource.spi.endpoint.MessageEndpointFactory;
import javax.resource.spi.work.Work;
import javax.resource.spi.work.WorkEvent;
import javax.resource.spi.work.WorkException;
import javax.resource.spi.work.WorkListener;
import javax.resource.spi.work.WorkManager;
import javax.transaction.TransactionManager;
import javax.transaction.xa.XAResource;

/**
 * A generic jms session pool.
 *
 * @author <a href="adrian@jboss.com">Adrian Brock</a>
 * @author <a href="mailto:weston.price@jboss.com>Weston Price</a>
 * @author <a href="mailto:jbertram@rehat.com>Justin Bertram</a>
 */
public class JmsServerSession implements ServerSession, MessageListener, Work, WorkListener {
    /**
     * The log
     */
    private static final Logger log = Logger.getLogger(JmsServerSession.class);

    /**
     * The session pool
     */
    JmsServerSessionPool pool;

    /**
     * The session
     */
    Session session;

    /**
     * Any XA session
     */
    XASession xaSession;

    /**
     * The endpoint
     */
    MessageEndpoint endpoint;

    TransactionManager tm;

    /**
     * Create a new JmsServerSession
     *
     * @param pool the server session pool
     */
    public JmsServerSession(JmsServerSessionPool pool) {
        this.pool = pool;
    }

    /**
     * Setup the session
     */
    public void setup() throws Exception {
        JmsActivation activation = pool.getActivation();
        JmsActivationSpec spec = activation.getActivationSpec();
        Connection connection = activation.getConnection();
        XAResource xaResource = null;
        tm = activation.getTransactionManager();

        // Get the endpoint
        MessageEndpointFactory endpointFactory = activation.getMessageEndpointFactory();

        // Create the session
        if (activation.isDeliveryTransacted) {
            if (connection instanceof XAConnection) {
                log.debug("Delivery is transacted, and client JMS implementation properly implements javax.jms.XAConnection.");
                xaSession = ((XAConnection) connection).createXASession();
                session = xaSession.getSession();
                xaResource = xaSession.getXAResource();
            } else {
                throw new Exception("Delivery is transacted, but client JMS implementation does not properly implement the necessary interfaces as described in section 8 of the JMS 1.1 specification.");
            }
        } else {
            session = connection.createSession(false, spec.getAcknowledgeModeInt());
        }

        endpoint = endpointFactory.createEndpoint(xaResource);

        // Set the message listener
        session.setMessageListener(this);
    }

    /**
     * Stop the session
     */
    public void teardown() {
        try {
            if (endpoint != null) {
                endpoint.release();
            }
        } catch (Throwable t) {
            log.debug("Error releasing endpoint " + endpoint, t);
        }

        try {
            if (xaSession != null) {
                xaSession.close();
            }
        } catch (Throwable t) {
            log.debug("Error closing xaSession " + xaSession, t);
        }

        try {
            if (session != null) {
                session.close();
            }
        } catch (Throwable t) {
            log.debug("Error closing session " + session, t);
        }
    }

    public void onMessage(Message message) {
        try {
            final int timeout = pool.getActivation().getActivationSpec().getTransactionTimeout();

            if (timeout > 0) {
                log.trace("Setting transactionTimeout for JMSSessionPool to " + timeout);
                tm.setTransactionTimeout(timeout);
            }

            endpoint.beforeDelivery(JmsActivation.ONMESSAGE);

            try {
                MessageListener listener = (MessageListener) endpoint;
                listener.onMessage(message);
            } finally {
                endpoint.afterDelivery();
            }
        } catch (Throwable t) {
            log.error("Unexpected error delivering message " + message, t);
        }
    }

    public Session getSession() throws JMSException {
        return session;
    }

    public void start() throws JMSException {
        JmsActivation activation = pool.getActivation();
        WorkManager workManager = activation.getWorkManager();
        try {
            workManager.scheduleWork(this, 0, null, this);
        } catch (WorkException e) {
            log.error("Unable to schedule work", e);
            throw new JMSException("Unable to schedule work: " + e.toString());
        }
    }

    public void run() {
        session.run();
    }

    public void release() {
    }

    public void workAccepted(WorkEvent e) {
    }

    public void workCompleted(WorkEvent e) {
        pool.returnServerSession(this);
    }

    public void workRejected(WorkEvent e) {
        pool.returnServerSession(this);
    }

    public void workStarted(WorkEvent e) {
    }
}