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

import org.wso2.carbon.message.passer.exception.MessagePasserException;

import java.io.Serializable;

public interface Message<T> extends Serializable {

    MessageMetadata getMetadata();

    void setMetadata(MessageMetadata metadata);

    T getPayLoad();

    void setPayLoad(T t);

    byte[] getMessageAsBytes() throws MessagePasserException;
}
