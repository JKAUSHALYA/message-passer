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

package org.wso2.carbon.message.passer.announce;

import org.wso2.carbon.message.passer.message.Action;
import org.wso2.carbon.message.passer.Node;
import org.wso2.carbon.message.passer.exception.MessagePasserException;
import org.wso2.carbon.message.passer.message.ActionMessage;
import org.wso2.carbon.message.passer.sender.TCPMessageSender;

public class TCPAnnounceMechanism implements AnnounceMechanism {

    private TCPMessageSender tcpMessageSender;

    public TCPAnnounceMechanism(TCPMessageSender tcpMessageSender) {
        this.tcpMessageSender = tcpMessageSender;
    }

    @Override
    public void announce(Node node) throws MessagePasserException {

        Action action = new Action();
        action.setActionType(Action.ActionType.ADD);
        action.setNode(node);

        ActionMessage message = new ActionMessage();
        message.setPayLoad(action);

        tcpMessageSender.send(message, node);
    }
}
