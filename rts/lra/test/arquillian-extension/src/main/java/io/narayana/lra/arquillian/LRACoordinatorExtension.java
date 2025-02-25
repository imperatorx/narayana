/*
   Copyright The Narayana Authors
   SPDX short identifier: Apache-2.0
 */


package io.narayana.lra.arquillian;

import io.narayana.lra.arquillian.deployment.scenario.LRACoordinatorScenarioGenerator;
import org.jboss.arquillian.container.test.spi.client.deployment.DeploymentScenarioGenerator;
import org.jboss.arquillian.core.spi.LoadableExtension;

/**
 * This class is the activation point to use {@link LRACoordinatorScenarioGenerator}
 */
public class LRACoordinatorExtension implements LoadableExtension {

    @Override
    public void register(ExtensionBuilder extensionBuilder) {
        extensionBuilder.service(DeploymentScenarioGenerator.class, LRACoordinatorScenarioGenerator.class);
    }
}