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

import org.wso2.carbon.message.passer.message.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TCPMessageReceiver implements MessageReceiver {

    private List<MessageReceiveEventHandler> messageReceiveEventHandlerList = new ArrayList<>();
    private ConcurrentLinkedQueue<Message> receivedMessages = new ConcurrentLinkedQueue<>();

    public TCPMessageReceiver(Node node) {

        short port = node.getPort();
        String host = node.getHostName();

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            Thread receiverThread = new Thread(() -> {
                while (true) {
                    try (Socket socket = serverSocket.accept()) {
                        receiveMessage(socket);
                    } catch (IOException e) {
                        // TODO: Handle this.
                        break;
                    }
                }
            });
            receiverThread.setName("TCP-Receiver");
            receiverThread.start();
        } catch (IOException e) {
            // TODO: Handle this.
        }

        ExecutorService executorService = Executors.newFixedThreadPool(4, new MessageProcessorThreadFactory());
        executorService.submit(() -> {
           while (true) {
               Message message = receivedMessages.poll();
               if (message == null) {
                   continue;
               }
               processMessage(message);
           }
        });
    }

    private void processMessage(Message message) {

    }

    private void receiveMessage(Socket socket) {

        try (ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream())) {
            Object receivedObject = objectInputStream.readObject();
            if (!(receivedObject instanceof Message)) {
                return;
            }
            Message message = (Message) receivedObject;
            receivedMessages.add(message);
        } catch (IOException | ClassNotFoundException e) {
            if (!socket.isClosed()) {
                try (OutputStream outputStream = socket.getOutputStream()) {
                    // TODO: Write proper error.
                    outputStream.write(500);
                } catch (IOException ignored) {
                }
            }
        }
    }

    @Override
    public void register(MessageReceiveEventHandler messageReceiveEventHandler) {
        messageReceiveEventHandlerList.add(messageReceiveEventHandler);
    }

    @Override
    public void unregister(MessageReceiveEventHandler messageReceiveEventHandler) {
        messageReceiveEventHandlerList.remove(messageReceiveEventHandler);
    }
}
