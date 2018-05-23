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

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class AbstractMessageReceiver implements MessageReceiver  {

    private static final int MESSAGE_PROCESSOR_THREAD_POOL_SIZE = ConfigurationFacade
            .getMessageProcessorThreadPoolSize();

    protected ConcurrentLinkedQueue<Message> receivedMessages = new ConcurrentLinkedQueue<>();

    public AbstractMessageReceiver() {

        ExecutorService executorService = Executors.newFixedThreadPool(MESSAGE_PROCESSOR_THREAD_POOL_SIZE,
                new MessageProcessorThreadFactory());
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

    public abstract void processMessage(Message message);

}
