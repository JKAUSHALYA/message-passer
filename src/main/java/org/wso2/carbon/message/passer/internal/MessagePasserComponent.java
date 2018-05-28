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

package org.wso2.carbon.message.passer.internal;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.carbon.message.passer.Dispatcher;
import org.wso2.carbon.message.passer.Node;
import org.wso2.carbon.message.passer.NodeRepository;
import org.wso2.carbon.message.passer.Receiver;
import org.wso2.carbon.message.passer.announce.AnnounceMechanism;
import org.wso2.carbon.message.passer.announce.DBBasedAnnounceMechanism;
import org.wso2.carbon.message.passer.announce.TCPAnnounceMechanism;
import org.wso2.carbon.message.passer.discovery.DBBasedDiscoveryMechanism;
import org.wso2.carbon.message.passer.discovery.DiscoveryMechanism;
import org.wso2.carbon.message.passer.discovery.TCPDiscoveryMechanism;
import org.wso2.carbon.message.passer.receiver.MessageReceiver;
import org.wso2.carbon.message.passer.receiver.TCPMessageReceiver;
import org.wso2.carbon.message.passer.sender.MessageSender;
import org.wso2.carbon.message.passer.sender.TCPMessageSender;

import java.util.ArrayList;
import java.util.List;

@Component(
        name = "org.wso2.carbon.message.parser",
        immediate = true)
public class MessagePasserComponent {

    private static final Logger log = LoggerFactory.getLogger(MessagePasserComponent.class);

    private List<MessageSender> messageSenders = new ArrayList<>();
    private List<MessageReceiver> messageReceivers = new ArrayList<>();

    private List<DiscoveryMechanism> discoveryMechanisms = new ArrayList<>();
    private List<AnnounceMechanism> announceMechanisms = new ArrayList<>();

    @Activate
    public void activate(ComponentContext componentContext) {

        try {
            // TODO: We need to get my ip and port here.
            Node myNode = new Node();

            TCPMessageReceiver tcpMessageReceiver = new TCPMessageReceiver(myNode);
            messageReceivers.add(tcpMessageReceiver);

            TCPMessageSender tcpMessageSender = new TCPMessageSender();
            messageSenders.add(tcpMessageSender);

            discoveryMechanisms.add(new DBBasedDiscoveryMechanism());
            discoveryMechanisms.add(new TCPDiscoveryMechanism());

            announceMechanisms.add(new DBBasedAnnounceMechanism());
            announceMechanisms.add(new TCPAnnounceMechanism(tcpMessageSender));

            NodeRepository nodeRepository = new NodeRepository(discoveryMechanisms, announceMechanisms);
            nodeRepository.register(myNode);

            Dispatcher dispatcher = new Dispatcher(nodeRepository, messageSenders);
            Receiver receiver = new Receiver(messageReceivers);

            BundleContext bundleContext = componentContext.getBundleContext();
            bundleContext.registerService(Dispatcher.class, dispatcher, null);
            bundleContext.registerService(Receiver.class, receiver, null);

        } catch (Throwable throwable) {
            log.error("Error occurred while activating the Message Passer component.", throwable);
        }
    }

}
