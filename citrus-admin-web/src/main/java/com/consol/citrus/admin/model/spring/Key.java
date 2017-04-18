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

package com.consol.citrus.admin.model.spring;

import org.springframework.context.annotation.Bean;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "description",
    "beenAndRevesAndIdreves"
})
@XmlRootElement(name = "key")
public class Key {

    protected Description description;
    @XmlElementRefs({
        @XmlElementRef(name = "value", namespace = "http://www.springframework.org/schema/beans", type = Value.class),
        @XmlElementRef(name = "bean", namespace = "http://www.springframework.org/schema/beans", type = SpringBean.class),
        @XmlElementRef(name = "map", namespace = "http://www.springframework.org/schema/beans", type = Map.class),
        @XmlElementRef(name = "list", namespace = "http://www.springframework.org/schema/beans", type = List.class),
        @XmlElementRef(name = "ref", namespace = "http://www.springframework.org/schema/beans", type = Ref.class),
        @XmlElementRef(name = "props", namespace = "http://www.springframework.org/schema/beans", type = Props.class)
    })
    @XmlAnyElement(lax = true)
    protected java.util.List<Object> beenAndRevesAndIdreves;

    /**
     * Gets the value of the description property.
     * 
     * @return
     *     possible object is
     *     {@link Description }
     *     
     */
    public Description getDescription() {
        return description;
    }

    /**
     * Sets the value of the description property.
     * 
     * @param value
     *     allowed object is
     *     {@link Description }
     *     
     */
    public void setDescription(Description value) {
        this.description = value;
    }

    /**
     * Gets the value of the beenAndRevesAndIdreves property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the beenAndRevesAndIdreves property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getBeenAndRevesAndIdreves().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Value }
     * {@link Bean }
     * {@link Map }
     * {@link Object }
     * {@link Ref }
     * {@link List }
     * {@link Props }
     * 
     * 
     */
    public java.util.List<Object> getBeenAndRevesAndIdreves() {
        if (beenAndRevesAndIdreves == null) {
            beenAndRevesAndIdreves = new ArrayList<Object>();
        }
        return this.beenAndRevesAndIdreves;
    }

}
