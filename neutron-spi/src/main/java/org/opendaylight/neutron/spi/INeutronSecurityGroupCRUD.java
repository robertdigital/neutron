/*
 * Copyright (C) 2014 Red Hat, Inc.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.neutron.spi;

import java.util.List;

/**
 * This interface defines the methods for CRUD of NB OpenStack Security Group objects
 */

public interface INeutronSecurityGroupCRUD
    extends INeutronCRUD<NeutronSecurityGroup> {
    // Nothing Here.
    // This class is defined to use reflection.
}
