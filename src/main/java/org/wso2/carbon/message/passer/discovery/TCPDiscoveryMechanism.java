/*
 * Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.message.passer.discovery;

import org.wso2.carbon.message.passer.message.Action;
import org.wso2.carbon.message.passer.event.MessageReceiveEventHandler;
import org.wso2.carbon.message.passer.Node;
import org.wso2.carbon.message.passer.message.ActionMessage;
import org.wso2.carbon.message.passer.message.Message;

import java.util.List;
import java.util.function.Consumer;

public class TCPDiscoveryMechanism implements DiscoveryMechanism, MessageReceiveEventHandler {

    private Consumer<Action> updateNodeList;

    @Override
    public void discover(Consumer<Action> updateNodeList) {
        this.updateNodeList = updateNodeList;
    }

    @Override
    public List<Node> discoverAll() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isDiscoverSupport() {
        return true;
    }

    @Override
    public boolean isDiscoverAllSupport() {
        return false;
    }

    @Override
    public boolean canHandle(Message message) {
        return message instanceof ActionMessage;
    }

    @Override
    public void process(Message message) {

        ActionMessage actionMessage = (ActionMessage) message;
        Action action = actionMessage.getPayLoad();

        updateNodeList.accept(action);
    }
}
