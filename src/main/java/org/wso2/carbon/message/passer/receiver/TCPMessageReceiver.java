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

package org.wso2.carbon.message.passer.receiver;

import org.wso2.carbon.message.passer.Node;
import org.wso2.carbon.message.passer.exception.MessagePasserException;
import org.wso2.carbon.message.passer.exception.ReceiverException;
import org.wso2.carbon.message.passer.message.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.function.Consumer;

public class TCPMessageReceiver extends AbstractMessageReceiver {

    private static final String RECEIVER_THREAD_NAME = "TCP-Receiver";
    private Node myNode;

    public TCPMessageReceiver(Node myNode) throws MessagePasserException {
        this.myNode = myNode;
    }

    @Override
    public void receiveMessage(Consumer<Message> addToQueue) throws MessagePasserException {

        try (ServerSocket serverSocket = new ServerSocket(myNode.getPort())) {
            Thread receiverThread = new Thread(() -> {
                while (true) {
                    try (Socket socket = serverSocket.accept()) {
                        try (ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream())) {
                            Object receivedObject = objectInputStream.readObject();
                            if (!(receivedObject instanceof Message)) {
                                continue;
                            }
                            Message message = (Message) receivedObject;
                            addToQueue.accept(message);
                        } catch (IOException | ClassNotFoundException e) {
                            if (!socket.isClosed()) {
                                try (OutputStream outputStream = socket.getOutputStream()) {
                                    ReceiverException receiverException = new ReceiverException(e);
                                    ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
                                    objectOutputStream.writeObject(receiverException);
                                    objectOutputStream.flush();
                                }
                            }
                        }
                    } catch (IOException e) {
                        throw new ReceiverException("Error occurred while getting the client socket.", e);
                    }
                }
            });
            receiverThread.setName(RECEIVER_THREAD_NAME);
            receiverThread.start();
        } catch (IOException e) {
            throw new MessagePasserException("Error occurred while starting the receiver thread.", e);
        }
    }
}
