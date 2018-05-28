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

import org.wso2.carbon.message.passer.event.MessageSendEventHandler;
import org.wso2.carbon.message.passer.exception.MessagePasserException;
import org.wso2.carbon.message.passer.message.Message;
import org.wso2.carbon.message.passer.sender.MessageSender;

import java.util.List;

public class Dispatcher {

    private NodeRepository nodeRepository;
    private List<MessageSender> messageSenders;

    public Dispatcher(NodeRepository nodeRepository, List<MessageSender> messageSenders) {
        this.messageSenders = messageSenders;
        this.nodeRepository = nodeRepository;
    }

    public void dispatch(Message message) throws MessagePasserException {

        for (MessageSender messageSender : messageSenders) {
            for (Node node : nodeRepository.getAllNodes()) {
                messageSender.send(message, node);
            }
        }
    }

    public void dispacth(Message message, Node node) throws MessagePasserException {

        for (MessageSender messageSender : messageSenders) {
            messageSender.send(message, node);
        }
    }

    public void addMessageSender(MessageSender messageSender) {
        messageSenders.add(messageSender);
    }

    public void removeMessageSender(MessageSender messageSender) {
        messageSenders.remove(messageSender);
    }

    public void register(MessageSendEventHandler messageSendEventHandler) {
        for (MessageSender messageSender : messageSenders) {
            messageSender.register(messageSendEventHandler);
        }
    }

    public void unregister(MessageSendEventHandler messageSendEventHandler) {
        for (MessageSender messageSender : messageSenders) {
            messageSender.unregister(messageSendEventHandler);
        }
    }
}
