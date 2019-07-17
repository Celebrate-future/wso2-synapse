/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *   * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */

package org.apache.synapse.mediators.builtin;

import org.apache.axiom.om.impl.llom.OMTextImpl;
import org.apache.axis2.AxisFault;
import org.apache.synapse.MessageContext;
import org.apache.synapse.commons.json.JsonUtil;
import org.apache.synapse.core.axis2.Axis2MessageContext;
import org.apache.synapse.mediators.AbstractMediator;
import org.apache.synapse.mediators.Value;
import org.apache.synapse.mediators.jjparser.exceptions.ParserException;
import org.apache.synapse.mediators.jjparser.exceptions.ValidatorException;
import org.apache.synapse.mediators.jjparser.parser.JavaJsonParser;

public class TransformMediator extends AbstractMediator {
    private Value schemaKey = null;

    @Override
    public boolean mediate(MessageContext synCtx) {
        // Derive actual key from message context
        String generatedSchemaKey = schemaKey.evaluateValue(synCtx);
        Object jsonSchemaObj = null;
        jsonSchemaObj = synCtx.getEntry(generatedSchemaKey);
        String schema = ((OMTextImpl) jsonSchemaObj).getText();
        try {
            String jsonPayload;
            if (JsonUtil.hasAJsonPayload(((Axis2MessageContext) synCtx).getAxis2MessageContext())) {
                jsonPayload = JsonUtil.jsonPayloadToString(((Axis2MessageContext) synCtx).getAxis2MessageContext());
            } else {
                jsonPayload = JsonUtil.toJsonString(((Axis2MessageContext) synCtx).getAxis2MessageContext()
                        .getEnvelope()).toString();
            }
            String result;

            result = JavaJsonParser.parseJson(jsonPayload, schema);
            if (result != null) {
                JsonUtil.getNewJsonPayload(((Axis2MessageContext) synCtx).getAxis2MessageContext(),
                        result, true, true);
            }
        } catch (ValidatorException e) {
            handleException("ValidatorException : ", synCtx);
        } catch (ParserException e) {
            handleException("ParserException : ", synCtx);
        } catch (AxisFault af) {
            handleException("Axisfault : ", synCtx);
        }
        System.out.println("This is a test inside transform mediator");
        return true;
    }

    public Value getSchemaKey() {
        return schemaKey;
    }

    public void setSchemaKey(Value schemaKey) {
        this.schemaKey = schemaKey;
    }
}
