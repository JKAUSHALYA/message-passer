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

import org.wso2.carbon.message.passer.ConfigurationFacade;
import org.wso2.carbon.message.passer.event.MessageReceiveEventHandler;
import org.wso2.carbon.message.passer.exception.MessagePasserException;
import org.wso2.carbon.message.passer.message.Message;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class AbstractMessageReceiver implements MessageReceiver {

    private static final int MESSAGE_PROCESSOR_THREAD_POOL_SIZE = ConfigurationFacade
            .getMessageProcessorThreadPoolSize();

    private final ConcurrentLinkedQueue<Message> receivedMessages = new ConcurrentLinkedQueue<>();
    private final List<MessageReceiveEventHandler> messageReceiveEventHandlerList = new ArrayList<>();

    protected AbstractMessageReceiver() throws MessagePasserException {

        ExecutorService executorService = Executors.newFixedThreadPool(MESSAGE_PROCESSOR_THREAD_POOL_SIZE,
                new MessageProcessorThreadFactory());
        executorService.submit(() -> {
            while (true) {
                Message message = receivedMessages.poll();
                if (message == null) {
                    continue;
                }

                for (MessageReceiveEventHandler messageReceiveEventHandler : messageReceiveEventHandlerList) {
                    if (messageReceiveEventHandler.canHandle(message)) {
                        messageReceiveEventHandler.process(message);
                    }
                }
            }
        });

        receiveMessage(receivedMessages::add);
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
