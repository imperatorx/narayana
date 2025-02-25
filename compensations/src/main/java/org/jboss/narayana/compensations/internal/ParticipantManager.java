/*
   Copyright The Narayana Authors
   SPDX short identifier: Apache-2.0
 */
package org.jboss.narayana.compensations.internal;


import javax.xml.namespace.QName;

/**
 * @author paul.robinson@redhat.com 19/04/2014
 */
public interface ParticipantManager {

    public void exit() throws Exception;


    public void completed() throws Exception;


    public void cannotComplete() throws Exception;


    public void fail(final QName exceptionIdentifier) throws Exception;
}
