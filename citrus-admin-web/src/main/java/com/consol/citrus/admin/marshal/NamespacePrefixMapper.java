/*
 * Copyright 2006-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.consol.citrus.admin.marshal;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Christoph Deppisch
 */
public class NamespacePrefixMapper extends com.sun.xml.bind.marshaller.NamespacePrefixMapper {

    /** List of known namespaces with mapping to prefix */
    private Map<String, String> namespaceMappings = new HashMap<String, String>();

    public NamespacePrefixMapper() {
        namespaceMappings.put("http://www.citrusframework.org/schema/config", "citrus");
        namespaceMappings.put("http://www.citrusframework.org/schema/jms/config", "citrus-jms");
        namespaceMappings.put("http://www.citrusframework.org/schema/http/config", "citrus-http");
        namespaceMappings.put("http://www.citrusframework.org/schema/websocket/config", "citrus-websocket");
        namespaceMappings.put("http://www.citrusframework.org/schema/ws/config", "citrus-ws");
        namespaceMappings.put("http://www.citrusframework.org/schema/ssh/config", "citrus-ssh");
        namespaceMappings.put("http://www.citrusframework.org/schema/mail/config", "citrus-mail");
        namespaceMappings.put("http://www.citrusframework.org/schema/vertx/config", "citrus-vertx");
        namespaceMappings.put("http://www.citrusframework.org/schema/camel/config", "citrus-camel");
        namespaceMappings.put("http://www.citrusframework.org/schema/arquillian/config", "citrus-arquillian");
        namespaceMappings.put("http://www.citrusframework.org/schema/cucumber/config", "citrus-cucumber");
        namespaceMappings.put("http://www.citrusframework.org/schema/ftp/config", "citrus-ftp");
        namespaceMappings.put("http://www.citrusframework.org/schema/mail/config", "citrus-mail");
        namespaceMappings.put("http://www.citrusframework.org/schema/docker/config", "citrus-docker");
        namespaceMappings.put("http://www.citrusframework.org/schema/kubernetes/config", "citrus-k8s");
        namespaceMappings.put("http://www.citrusframework.org/schema/jmx/config", "citrus-jmx");
        namespaceMappings.put("http://www.citrusframework.org/schema/zookeeper/config", "citrus-zookeeper");
        namespaceMappings.put("http://www.citrusframework.org/schema/rmi/config", "citrus-rmi");
        namespaceMappings.put("http://www.citrusframework.org/schema/selenium/config", "citrus-selenium");
        namespaceMappings.put("http://www.citrusframework.org/schema/testcase", "citrus-test");
    }

    @Override
    public String getPreferredPrefix(String namespaceUri, String suggestion, boolean requirePrefix) {
        if (namespaceMappings.containsKey(namespaceUri)) {
            return namespaceMappings.get(namespaceUri);
        }

        return suggestion;
    }

    /**
     * Gets the namespace mappings.
     * @return
     */
    public Map<String, String> getNamespaceMappings() {
        return namespaceMappings;
    }

    /**
     * Sets the namespace mappings.
     * @param namespaceMappings
     */
    public void setNamespaceMappings(Map<String, String> namespaceMappings) {
        this.namespaceMappings = namespaceMappings;
    }
}
