/*
 * Copyright (c) 2016 Brocade Communications Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.neutron.transcriber;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.apache.aries.blueprint.annotation.service.Service;
import org.opendaylight.controller.md.sal.binding.api.DataBroker;
import org.opendaylight.neutron.spi.INeutronSFCPortChainCRUD;
import org.opendaylight.neutron.spi.NeutronSFCPortChain;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.yang.types.rev130715.Uuid;
import org.opendaylight.yang.gen.v1.urn.opendaylight.neutron.sfc.rev160511.port.chain.attributes.ChainParameters;
import org.opendaylight.yang.gen.v1.urn.opendaylight.neutron.sfc.rev160511.port.chain.attributes.ChainParametersBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.neutron.sfc.rev160511.port.chain.attributes.ChainParametersKey;
import org.opendaylight.yang.gen.v1.urn.opendaylight.neutron.sfc.rev160511.sfc.attributes.PortChains;
import org.opendaylight.yang.gen.v1.urn.opendaylight.neutron.sfc.rev160511.sfc.attributes.port.chains.PortChain;
import org.opendaylight.yang.gen.v1.urn.opendaylight.neutron.sfc.rev160511.sfc.attributes.port.chains.PortChainBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.neutron.sfc.rev160511.sfc.attributes.port.chains.PortChainKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Anil Vishnoi (avishnoi@Brocade.com).
 */
@Singleton
@Service(classes = INeutronSFCPortChainCRUD.class)
public final class NeutronSFCPortChainInterface
        extends AbstractNeutronInterface<PortChain, PortChains, PortChainKey, NeutronSFCPortChain>
        implements INeutronSFCPortChainCRUD {

    private static final Logger LOG = LoggerFactory.getLogger(NeutronSFCPortChainInterface.class);

    @Inject
    public NeutronSFCPortChainInterface(DataBroker db) {
        super(PortChainBuilder.class, db);
    }

    @Override
    protected List<PortChain> getDataObjectList(PortChains dataObjects) {
        return dataObjects.getPortChain();
    }

    @Override
    protected PortChain toMd(NeutronSFCPortChain neutronPortChain) {

        LOG.trace("toMd: REST SFC Port Chain data : {}", neutronPortChain);

        PortChainBuilder result = new PortChainBuilder();
        toMdBaseAttributes(neutronPortChain, result);
        if (neutronPortChain.getPortPairGroupsUUID() != null) {
            List<Uuid> portPairGroups = new ArrayList<>();
            for (String uuid : neutronPortChain.getPortPairGroupsUUID()) {
                portPairGroups.add(new Uuid(uuid));
            }
            result.setPortPairGroups(portPairGroups);
        }
        if (neutronPortChain.getFlowClassifiersUUID() != null) {
            List<Uuid> flowClassifiers = new ArrayList<>();
            for (String uuid : neutronPortChain.getFlowClassifiersUUID()) {
                flowClassifiers.add(new Uuid(uuid));
            }
            result.setFlowClassifiers(flowClassifiers);
        }
        if (neutronPortChain.getChainParameters() != null) {
            List<ChainParameters> chainParams = new ArrayList<>();
            for (String paramKey : neutronPortChain.getChainParameters().keySet()) {
                ChainParametersBuilder param = new ChainParametersBuilder();
                param.withKey(new ChainParametersKey(paramKey));
                param.setChainParameter(paramKey);
                param.setChainParameterValue(neutronPortChain.getChainParameters().get(paramKey));
                chainParams.add(param.build());
            }
            result.setChainParameters(chainParams);
        }
        LOG.trace("toMd: Yang SFC Port Chain data : {}", result);
        return result.build();
    }

    @Override
    protected NeutronSFCPortChain fromMd(PortChain mdPortChain) {
        LOG.trace("fromMd: Yang SFC Port Chain data : {}", mdPortChain);
        NeutronSFCPortChain result = new NeutronSFCPortChain();
        fromMdBaseAttributes(mdPortChain, result);
        if (mdPortChain.getPortPairGroups() != null) {
            List<String> portPairGroups = new ArrayList<>();
            for (Uuid uuid : mdPortChain.getPortPairGroups()) {
                portPairGroups.add(uuid.getValue());
            }
            result.setPortPairGroupsUUID(portPairGroups);
        }
        if (mdPortChain.getFlowClassifiers() != null) {
            List<String> flowClassifiers = new ArrayList<>();
            for (Uuid uuid : mdPortChain.getFlowClassifiers()) {
                flowClassifiers.add(uuid.getValue());
            }
            result.setFlowClassifiersUUID(flowClassifiers);
        }
        if (mdPortChain.getChainParameters() != null) {
            HashMap<String, String> chainParams = new HashMap<>();
            for (ChainParameters param : mdPortChain.getChainParameters()) {
                chainParams.put(param.getChainParameter(), param.getChainParameterValue());
            }
            result.setChainParameters(chainParams);
        }
        LOG.trace("fromMd: REST SFC Port Chain data : {}", result);
        return result;
    }
}
