/*******************************************************************************
 * Copyright (c) 2008 Sonatype, Inc. All rights reserved. This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.sonatype.m2e.cxf.internal;

import org.apache.maven.plugin.MojoExecution;
import org.eclipse.m2e.core.lifecyclemapping.model.IPluginExecutionMetadata;
import org.eclipse.m2e.core.project.IMavenProjectFacade;
import org.eclipse.m2e.core.project.configurator.AbstractBuildParticipant;
import org.eclipse.m2e.jdt.AbstractSourcesGenerationProjectConfigurator;

public class CxfProjectConfigurator extends AbstractSourcesGenerationProjectConfigurator
{
    @Override
    protected String getOutputFolderParameterName()
    {
        return "sourceRoot";
    }

    @Override
    public AbstractBuildParticipant getBuildParticipant(final IMavenProjectFacade projectFacade, final MojoExecution execution, final IPluginExecutionMetadata executionMetadata)
    {
        return new CxfBuildParticipant(execution);
    }
}
