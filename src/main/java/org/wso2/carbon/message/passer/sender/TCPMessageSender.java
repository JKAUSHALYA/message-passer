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

package org.wso2.carbon.message.passer.sender;

import org.wso2.carbon.message.passer.event.MessageSendEventHandler;
import org.wso2.carbon.message.passer.Node;
import org.wso2.carbon.message.passer.exception.MessagePasserException;
import org.wso2.carbon.message.passer.message.Message;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class TCPMessageSender implements MessageSender {

    private List<MessageSendEventHandler> messageSendEventHandlers = new ArrayList<>();

    @Override
    public void send(Message message, Node node) throws MessagePasserException {

        String host = node.getHostName();
        short port = node.getPort();

        if (message.getMetadata().getRecipient() == null) {
            message.getMetadata().setRecipient(node);
        }

        try (Socket clientSocket = new Socket(host, port);
             InputStream inputStream = clientSocket.getInputStream();
             OutputStream outputStream = clientSocket.getOutputStream()) {

            ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
            objectOutputStream.writeObject(message);
            objectOutputStream.flush();

            ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
            Object object = objectInputStream.readObject();
            if (object instanceof Exception) {
                throw (Exception) object;
            }
        } catch (Exception e) {
            throw new MessagePasserException("Error occurred while sending the message.", e);
        }

        for (MessageSendEventHandler messageSendEventHandler : messageSendEventHandlers) {
            if (messageSendEventHandler.canHandle(message)) {
                messageSendEventHandler.process(message);
            }
        }
    }

    @Override
    public void register(MessageSendEventHandler messageSendEventHandler) {
        messageSendEventHandlers.add(messageSendEventHandler);
    }

    @Override
    public void unregister(MessageSendEventHandler messageSendEventHandler) {
        messageSendEventHandlers.remove(messageSendEventHandler);
    }
}
