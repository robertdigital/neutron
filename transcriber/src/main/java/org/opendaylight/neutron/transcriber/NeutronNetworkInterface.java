/*
 * Copyright (c) 2013, 2015 IBM Corporation and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.neutron.transcriber;

import com.google.common.collect.ImmutableBiMap;

import java.util.ArrayList;
import java.util.List;

import org.opendaylight.controller.md.sal.binding.api.DataBroker;
import org.opendaylight.neutron.spi.INeutronNetworkCRUD;
import org.opendaylight.neutron.spi.NeutronNetwork;
import org.opendaylight.neutron.spi.NeutronNetwork_Segment;
import org.opendaylight.yang.gen.v1.urn.opendaylight.neutron.l3.ext.rev150712.NetworkL3Extension;
import org.opendaylight.yang.gen.v1.urn.opendaylight.neutron.l3.ext.rev150712.NetworkL3ExtensionBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.neutron.networks.rev150712.networks.attributes.Networks;
import org.opendaylight.yang.gen.v1.urn.opendaylight.neutron.networks.rev150712.networks.attributes.networks.Network;
import org.opendaylight.yang.gen.v1.urn.opendaylight.neutron.networks.rev150712.networks.attributes.networks.NetworkBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.neutron.provider.ext.rev150712.neutron.networks.network.Segments;
import org.opendaylight.yang.gen.v1.urn.opendaylight.neutron.provider.ext.rev150712.neutron.networks.network.SegmentsBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.neutron.networks.rev150712.NetworkTypeBase;
import org.opendaylight.yang.gen.v1.urn.opendaylight.neutron.networks.rev150712.NetworkTypeFlat;
import org.opendaylight.yang.gen.v1.urn.opendaylight.neutron.networks.rev150712.NetworkTypeGre;
import org.opendaylight.yang.gen.v1.urn.opendaylight.neutron.networks.rev150712.NetworkTypeVlan;
import org.opendaylight.yang.gen.v1.urn.opendaylight.neutron.networks.rev150712.NetworkTypeVxlan;
import org.opendaylight.yang.gen.v1.urn.opendaylight.neutron.provider.ext.rev150712.NetworkProviderExtension;
import org.opendaylight.yang.gen.v1.urn.opendaylight.neutron.provider.ext.rev150712.NetworkProviderExtensionBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.neutron.rev150712.Neutron;
import org.opendaylight.yangtools.yang.binding.InstanceIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NeutronNetworkInterface extends AbstractNeutronInterface<Network, Networks, NeutronNetwork> implements INeutronNetworkCRUD {
    private static final Logger LOGGER = LoggerFactory.getLogger(NeutronNetworkInterface.class);

    private static final ImmutableBiMap<Class<? extends NetworkTypeBase>,String> NETWORK_MAP
            = new ImmutableBiMap.Builder<Class<? extends NetworkTypeBase>,String>()
            .put(NetworkTypeFlat.class,"flat")
            .put(NetworkTypeGre.class,"gre")
            .put(NetworkTypeVlan.class,"vlan")
            .put(NetworkTypeVxlan.class,"vxlan")
            .build();

    NeutronNetworkInterface(DataBroker db) {
        super(db);
    }

    // IfNBNetworkCRUD methods
    @Override
    protected List<Network> getDataObjectList(Networks networks) {
        return networks.getNetwork();
    }

    protected NeutronNetwork fromMd(Network network) {
        final NeutronNetwork result = new NeutronNetwork();
        result.initDefaults();
        result.setAdminStateUp(network.isAdminStateUp());
        result.setNetworkName(network.getName());
        result.setShared(network.isShared());
        result.setStatus(network.getStatus());
        result.setTenantID(network.getTenantId());
        result.setID(network.getUuid().getValue());

        final NetworkL3Extension l3Extension = network.getAugmentation(NetworkL3Extension.class);
        result.setRouterExternal(l3Extension.isExternal());

        final NetworkProviderExtension providerExtension = network.getAugmentation(NetworkProviderExtension.class);
        result.setProviderPhysicalNetwork(providerExtension.getPhysicalNetwork());
        result.setProviderSegmentationID(providerExtension.getSegmentationId());
        result.setProviderNetworkType(NETWORK_MAP.get(providerExtension.getNetworkType()));
        final List<NeutronNetwork_Segment> segments = new ArrayList<NeutronNetwork_Segment>();
        if (providerExtension.getSegments() != null) {
            for (final Segments segment: providerExtension.getSegments()) {
                final NeutronNetwork_Segment neutronSegment = new NeutronNetwork_Segment();
                neutronSegment.setProviderPhysicalNetwork(segment.getPhysicalNetwork());
                neutronSegment.setProviderSegmentationID(segment.getSegmentationId());
                neutronSegment.setProviderNetworkType(NETWORK_MAP.get(segment.getNetworkType()));
                segments.add(neutronSegment);
            }
        }
        result.setSegments(segments);
        return result;
    }

    private void fillExtensions(NetworkBuilder networkBuilder,
                                NeutronNetwork network) {
        final NetworkL3ExtensionBuilder l3ExtensionBuilder = new NetworkL3ExtensionBuilder();
        if (network.getRouterExternal() != null) {
            l3ExtensionBuilder.setExternal(network.getRouterExternal());
        }

        final NetworkProviderExtensionBuilder providerExtensionBuilder = new NetworkProviderExtensionBuilder();
        if (network.getProviderPhysicalNetwork() != null) {
            providerExtensionBuilder.setPhysicalNetwork(network.getProviderPhysicalNetwork());
        }
        if (network.getProviderSegmentationID() != null) {
            providerExtensionBuilder.setSegmentationId(network.getProviderSegmentationID());
        }
        if (network.getProviderNetworkType() != null) {
            final ImmutableBiMap<String, Class<? extends NetworkTypeBase>> mapper =
                NETWORK_MAP.inverse();
            providerExtensionBuilder.setNetworkType((Class<? extends NetworkTypeBase>) mapper.get(network.getProviderNetworkType()));
        }
        if (network.getSegments() != null) {
            final List<Segments> segments = new ArrayList<Segments>();
            long count = 0;
            for( final NeutronNetwork_Segment segment : network.getSegments()) {
                count++;
                final SegmentsBuilder segmentsBuilder = new SegmentsBuilder();
                if (segment.getProviderPhysicalNetwork() != null) {
                    segmentsBuilder.setPhysicalNetwork(segment.getProviderPhysicalNetwork());
                }
                if (segment.getProviderSegmentationID() != null) {
                    segmentsBuilder.setSegmentationId(segment.getProviderSegmentationID());
                }
                if (segment.getProviderNetworkType() != null) {
                    final ImmutableBiMap<String, Class<? extends NetworkTypeBase>> mapper =
                        NETWORK_MAP.inverse();
                    segmentsBuilder.setNetworkType((Class<? extends NetworkTypeBase>) mapper.get(segment.getProviderNetworkType()));
                }
                segmentsBuilder.setSegmentationIndex(Long.valueOf(count));
                segments.add(segmentsBuilder.build());
            }
            providerExtensionBuilder.setSegments(segments);
        }
        if (network.getProviderSegmentationID() != null) {
            providerExtensionBuilder.setSegmentationId(network.getProviderSegmentationID());
        }

        networkBuilder.addAugmentation(NetworkL3Extension.class,
                                       l3ExtensionBuilder.build());
        networkBuilder.addAugmentation(NetworkProviderExtension.class,
                                       providerExtensionBuilder.build());
    }

    protected Network toMd(NeutronNetwork network) {
        final NetworkBuilder networkBuilder = new NetworkBuilder();
        fillExtensions(networkBuilder, network);

        networkBuilder.setAdminStateUp(network.getAdminStateUp());
        if (network.getNetworkName() != null) {
            networkBuilder.setName(network.getNetworkName());
        }
        if (network.getShared() != null) {
            networkBuilder.setShared(network.getShared());
        }
        if (network.getStatus() != null) {
            networkBuilder.setStatus(network.getStatus());
        }
        if (network.getTenantID() != null) {
            networkBuilder.setTenantId(toUuid(network.getTenantID()));
        }
        if (network.getID() != null) {
            networkBuilder.setUuid(toUuid(network.getID()));
        } else {
            LOGGER.warn("Attempting to write neutron network without UUID");
        }
        return networkBuilder.build();
    }

    protected Network toMd(String uuid) {
        final NetworkBuilder networkBuilder = new NetworkBuilder();
        networkBuilder.setUuid(toUuid(uuid));
        return networkBuilder.build();
    }

    @Override
    protected InstanceIdentifier<Network> createInstanceIdentifier(Network network) {
        return InstanceIdentifier.create(Neutron.class)
                .child(Networks.class)
                .child(Network.class,network.getKey());
    }

    @Override
    protected InstanceIdentifier<Networks> createInstanceIdentifier() {
        return InstanceIdentifier.create(Neutron.class)
                .child(Networks.class);
    }
}
