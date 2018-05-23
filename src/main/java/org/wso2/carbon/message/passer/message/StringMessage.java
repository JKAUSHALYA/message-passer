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

package org.wso2.carbon.message.passer.message;

import org.wso2.carbon.message.passer.MessageMetadata;

public class StringMessage extends AbstractMessage<String> {

    private static final long serialVersionUID = -1786856500132228118L;

    private String payLoad;
    private MessageMetadata messageMetadata;

    public StringMessage(MessageMetadata messageMetadata) {
        this.messageMetadata = messageMetadata;
    }

    @Override
    public MessageMetadata getMetadata() {
        return messageMetadata;
    }

    @Override
    public void setMetadata(MessageMetadata metadata) {
        this.messageMetadata = metadata;
    }

    @Override
    public String getPayLoad() {
        return this.payLoad;
    }

    @Override
    public void setPayLoad(String payLoad) {
        this.payLoad = payLoad;
    }
}
