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

package org.wso2.carbon.message.passer;

import org.wso2.carbon.message.passer.message.ActionMessage;
import org.wso2.carbon.message.passer.message.Message;

import java.util.List;

public class TCPDiscoveryMechanism implements DiscoveryMechanism, MessageReceiveEventHandler {

    private List<Node> nodeList;

    @Override
    public void discover(List<Node> registerNode) {
        nodeList = registerNode;
    }

    @Override
    public List<Node> discoverAll() {
        return null;
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

        // TODO: Implement this.
        ActionMessage actionMessage = (ActionMessage) message;
        switch (actionMessage.getPayLoad().getType()) {
            case ADD: {
                Node node = actionMessage.getPayLoad().getNode();
                nodeList.add(node);
                break;
            }
            case REMOVE: {
                Node node = actionMessage.getPayLoad().getNode();
                nodeList.add(node);
                break;
            }
            case UPDATE: {
                // TODO: Check the logic
                Node node = actionMessage.getPayLoad().getNode();
                nodeList.add(node);
                break;
            }
        }
    }
}
