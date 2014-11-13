/*******************************************************************************
 * Copyright (c) 2008 Sonatype, Inc. All rights reserved. This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.sonatype.m2e.cxf.internal;

import java.io.File;
import java.util.Set;

import org.apache.maven.plugin.MojoExecution;
import org.codehaus.plexus.util.Scanner;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.m2e.core.embedder.IMaven;
import org.eclipse.m2e.core.project.configurator.MojoExecutionBuildParticipant;
import org.sonatype.plexus.build.incremental.BuildContext;

public class CxfBuildParticipant extends MojoExecutionBuildParticipant
{

    public CxfBuildParticipant(final MojoExecution execution)
    {
        super(execution, true);
    }

    @Override
    public Set<IProject> build(final int kind, final IProgressMonitor monitor) throws Exception
    {
        final IMaven maven = MavenPlugin.getMaven();
        final BuildContext buildContext = getBuildContext();

        //
        // check if any of the grammar files changed
        //
        final File source = maven.getMojoParameterValue(getSession().getCurrentProject(), getMojoExecution(), "sourceDirectory", File.class, monitor);
        final Scanner ds = buildContext.newScanner(source); // delta or full scanner
        ds.scan();
        final String[] includedFiles = ds.getIncludedFiles();
        if((includedFiles == null) || (includedFiles.length <= 0))
        {
            return null;
        }

        //
        // execute mojo
        //
        final Set<IProject> result = super.build(kind, monitor);

        //
        // tell m2e builder to refresh generated files
        //
        final File generated = maven.getMojoParameterValue(getSession().getCurrentProject(), getMojoExecution(), "outputDirectory", File.class, monitor);
        if(generated != null)
        {
            buildContext.refresh(generated);
        }

        return result;
    }
}
