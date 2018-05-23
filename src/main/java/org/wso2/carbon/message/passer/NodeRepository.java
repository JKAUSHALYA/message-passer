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

import java.util.ArrayList;
import java.util.List;

public class NodeRepository {

    private final List<Node> nodeList = new ArrayList<>();
    private List<DiscoveryMechanism> discoveryMechanisms = new ArrayList<>();

    public NodeRepository() {

        nodeList.addAll(getAllNodes());

        for (DiscoveryMechanism discoveryMechanism : discoveryMechanisms) {
            if (discoveryMechanism.isDiscoverSupport()) {
                discoveryMechanism.discover(nodeList);
            }
        }
    }

    public List<Node> getAllNodes() {

        for (DiscoveryMechanism discoveryMechanism : discoveryMechanisms) {
            if (discoveryMechanism.isDiscoverAllSupport()) {
                return discoveryMechanism.discoverAll();
            }
        }
        return new ArrayList<>();
    }

    public void register(Node node) {
        nodeList.add(node);
    }

    public void unRegister(Node node) {
        nodeList.remove(node);
    }

}
