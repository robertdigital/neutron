/*
 * Copyright (c) 2016 Brocade Communications Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.neutron.northbound.api;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import org.opendaylight.neutron.spi.NeutronSFCPortPair;

@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
@SuppressFBWarnings("URF_UNREAD_FIELD")
public final class NeutronSFCPortPairRequest extends NeutronRequest<NeutronSFCPortPair> {
    // See OpenStack Networking SFC (networking-sfc) Port Pair API v1.0 Reference
    // for description of annotated attributes

    @XmlElement(name = "portpair")
    NeutronSFCPortPair singleton;

    @XmlElement(name = "portpairs")
    List<NeutronSFCPortPair> bulkRequest;

    NeutronSFCPortPairRequest() {
    }

    NeutronSFCPortPairRequest(List<NeutronSFCPortPair> bulkRequest) {
        this.bulkRequest = bulkRequest;
    }

    NeutronSFCPortPairRequest(NeutronSFCPortPair sfcPortPair) {
        this.singleton = sfcPortPair;
    }
}
