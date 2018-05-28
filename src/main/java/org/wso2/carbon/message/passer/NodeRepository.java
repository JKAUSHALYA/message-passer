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

import org.wso2.carbon.message.passer.announce.AnnounceMechanism;
import org.wso2.carbon.message.passer.discovery.DiscoveryMechanism;
import org.wso2.carbon.message.passer.exception.MessagePasserException;
import org.wso2.carbon.message.passer.message.Action;

import java.util.ArrayList;
import java.util.List;

public class NodeRepository {

    private static final List<Node> nodeList = new ArrayList<>();
    private List<DiscoveryMechanism> discoveryMechanisms;
    private List<AnnounceMechanism> announceMechanisms;

    public NodeRepository(List<DiscoveryMechanism> discoveryMechanisms, List<AnnounceMechanism> announceMechanisms) {

        this.discoveryMechanisms = discoveryMechanisms;
        this.announceMechanisms = announceMechanisms;

        for (DiscoveryMechanism discoveryMechanism : this.discoveryMechanisms) {
            if (discoveryMechanism.isDiscoverAllSupport()) {
                nodeList.addAll(discoveryMechanism.discoverAll());
                break;
            }
        }

        for (DiscoveryMechanism discoveryMechanism : this.discoveryMechanisms) {
            if (discoveryMechanism.isDiscoverSupport()) {
                discoveryMechanism.discover(this::updateNodeList);
            }
        }
    }

    private void updateNodeList(Action action) {

        Node node = action.getNode();

        switch (action.getType()) {
            case ADD:
                nodeList.add(node);
                break;
            case REMOVE:
                nodeList.remove(node);
                break;
            case UPDATE:
                int index = nodeList.indexOf(node);
                if (index > -1) {
                    nodeList.set(index, node);
                }
                break;
        }
    }

    public List<Node> getAllNodes() {
        return nodeList;
    }

    public void register(Node node) throws MessagePasserException {

        for (AnnounceMechanism announceMechanism : announceMechanisms) {
            announceMechanism.announce(node);
        }

        nodeList.add(node);
    }
}
