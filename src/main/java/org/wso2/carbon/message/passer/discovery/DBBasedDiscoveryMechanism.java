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

package org.wso2.carbon.message.passer.discovery;

import org.wso2.carbon.message.passer.message.Action;
import org.wso2.carbon.message.passer.Node;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import javax.sql.DataSource;

public class DBBasedDiscoveryMechanism implements DiscoveryMechanism {

    private static final String GET_ALL_NODES_SQL = "SELECT * FROM TABLE";
    private DataSource dataSource;

    @Override
    public void discover(Consumer<Action> updateNodeList) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Node> discoverAll() {

        List<Node> nodes = new ArrayList<>();
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement getAllNodes = connection.prepareStatement(GET_ALL_NODES_SQL);
            try (ResultSet resultSet = getAllNodes.executeQuery()) {
                while (resultSet.next()) {
                    Node node = new Node();
                    String host = resultSet.getString("host");
                    short port = resultSet.getShort("port");
                    node.setHostName(host);
                    node.setPort(port);
                    nodes.add(node);
                }
            }
        } catch (SQLException e) {
            // TODO
        }
        return nodes;
    }

    @Override
    public boolean isDiscoverSupport() {
        return false;
    }

    @Override
    public boolean isDiscoverAllSupport() {
        return true;
    }
}
