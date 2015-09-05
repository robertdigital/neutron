/*
 * Copyright (C) 2015 IBM, Inc.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.neutron.e2etest;

import java.io.OutputStreamWriter;

import java.lang.Thread;

import java.net.HttpURLConnection;
import java.net.URL;

import org.junit.Assert;

public class NeutronFirewallRuleTests {
    String base;

    public NeutronFirewallRuleTests(String base) {
        this.base = base;
    }

    public void fw_rule_collection_get_test() {
        String url = base + "/fw/firewall_rules";
        ITNeutronE2E.test_fetch(url, "Firewall Rule Collection GET failed");
    }

    public void singleton_fw_rule_create_test() {
        String url = base + "/fw/firewall_rules";
        String content = "{ \"firewall_rule\": { \"action\": \"allow\"," +
            "\"description\": \"\", \"destination_ip_address\": null," +
            "\"destination_port\": \"80\", \"enabled\": true," +
            "\"firewall_policy_id\": null," +
            "\"id\": \"8722e0e0-9cc9-4490-9660-8c9a5732fbb0\"," +
            "\"ip_version\": 4, \"name\": \"ALLOW_HTTP\"," +
            "\"position\": null, \"protocol\": \"tcp\"," +
            "\"shared\": false, \"source_ip_address\": null," +
            "\"source_port\": null," +
            "\"tenant_id\": \"45977fa2dbd7482098dd68d0d8970117\" } }";
        ITNeutronE2E.test_create(url, content, "Firewall Rule Singleton Post Failed");
    }

    public void fw_rule_modify_test() {
        String url = base + "/fw/firewall_rules/8722e0e0-9cc9-4490-9660-8c9a5732fbb0";
        String content = "{ \"firewall_rule\": { \"action\": \"allow\"," +
            "\"description\": \"\", \"destination_ip_address\": null," +
            "\"destination_port\": \"80\", \"enabled\": true," +
            "\"firewall_policy_id\": null," +
            "\"id\": \"8722e0e0-9cc9-4490-9660-8c9a5732fbb0\"," +
            "\"ip_version\": 4, \"name\": \"ALLOW_HTTP\"," +
            "\"position\": null, \"protocol\": \"tcp\"," +
            "\"shared\": true, \"source_ip_address\": null," +
            "\"source_port\": null," +
            "\"tenant_id\": \"45977fa2dbd7482098dd68d0d8970117\" } }";
        ITNeutronE2E.test_modify(url, content, "Firewall Rule Singleton Post Failed");
    }

    public void fw_rule_element_get_test() {
        String url = base + "/fw/firewall_rules/8722e0e0-9cc9-4490-9660-8c9a5732fbb0";
        ITNeutronE2E.test_fetch(url, true, "Firewall Rule Element Get Failed");
    }

    public void fw_rule_delete_test() {
        String url = base + "/fw/firewall_rules/8722e0e0-9cc9-4490-9660-8c9a5732fbb0";
        ITNeutronE2E.test_delete(url, "Firewall Rule Delete Failed");
    }

    public void fw_rule_element_negative_get_test() {
        String url = base + "/fw/firewall_rules/8722e0e0-9cc9-4490-9660-8c9a5732fbb0";
        ITNeutronE2E.test_fetch(url, false, "Firewall Rule Element Negative Get Failed");
    }

    public static void runTests(String base) {
        NeutronFirewallRuleTests fw_rule_tester = new NeutronFirewallRuleTests(base);
        fw_rule_tester.fw_rule_collection_get_test();
        fw_rule_tester.singleton_fw_rule_create_test();
        fw_rule_tester.fw_rule_element_get_test();
        fw_rule_tester.fw_rule_modify_test();
        fw_rule_tester.fw_rule_delete_test();
        fw_rule_tester.fw_rule_element_negative_get_test();
    }
}
