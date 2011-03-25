//  The contents of this file are subject to the Mozilla Public License
//  Version 1.1 (the "License"); you may not use this file except in
//  compliance with the License. You may obtain a copy of the License
//  at http://www.mozilla.org/MPL/
//
//  Software distributed under the License is distributed on an "AS IS"
//  basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See
//  the License for the specific language governing rights and
//  limitations under the License.
//
//  The Original Code is RabbitMQ.
//
//  The Initial Developer of the Original Code is VMware, Inc.
//  Copyright (c) 2007-2011 VMware, Inc.  All rights reserved.
//

package com.rabbitmq.client.test;

import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.util.Map;
import java.util.Hashtable;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.MessageProperties;

public class ClonePropertiesTest extends TestCase {

    public static TestSuite suite()
    {
        TestSuite suite = new TestSuite("cloneProperties");
        suite.addTestSuite(ClonePropertiesTest.class);
        return suite;
    }

    public void testPropertyCloneIsDistinct()
        throws CloneNotSupportedException
    {
        assertTrue(MessageProperties.MINIMAL_PERSISTENT_BASIC !=
                   MessageProperties.MINIMAL_PERSISTENT_BASIC.clone());
        
        BasicProperties bp = (BasicProperties) MessageProperties.MINIMAL_PERSISTENT_BASIC.clone();
        Map<String, Object> headers = new Hashtable<String, Object>();
        headers.put("test", "123");
        bp.setHeaders(headers);
        assertTrue(
            bp.getHeaders() != ((BasicProperties) bp.clone()).getHeaders());
    }

    public void testPropertyClonePreservesValues()
        throws CloneNotSupportedException
    {
        assertEquals(MessageProperties.MINIMAL_PERSISTENT_BASIC.getDeliveryMode(),
                     ((BasicProperties) MessageProperties.MINIMAL_PERSISTENT_BASIC.clone())
                       .getDeliveryMode());
        assertEquals(2,
                     ((BasicProperties) MessageProperties.MINIMAL_PERSISTENT_BASIC.clone())
                       .getDeliveryMode());
    }
}
