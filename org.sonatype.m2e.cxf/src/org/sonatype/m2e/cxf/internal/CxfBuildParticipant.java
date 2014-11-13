/*******************************************************************************
 * Copyright (c) 2008 Sonatype, Inc. All rights reserved. This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.sonatype.m2e.cxf.internal;

import java.io.File;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.project.MavenProject;
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
        final MojoExecution mojoExecution = getMojoExecution();

        final MavenProject mavenProject = getMavenProjectFacade().getMavenProject(monitor);

        final File sourceRoot = maven.getMojoParameterValue(mavenProject, mojoExecution, "sourceRoot", File.class, monitor);

        boolean filesModified = false;

        if((sourceRoot != null) && !sourceRoot.exists())
        {
            filesModified = true;
        }
        else
        {
            final List<?> wsdlOptions = maven.getMojoParameterValue(mavenProject, mojoExecution, "wsdlOptions", List.class, monitor);

            // getMojoParameterValue returns an instance of WsdlOption from a different classloader, so casting doesn't work.
            for(final Object obj : wsdlOptions)
            {
                final Class<?> k = obj.getClass();
                final Method getWsdl = k.getMethod("getWsdl");
                final Method getBindingFiles = k.getMethod("getBindingFiles");

                final String wsdl = getWsdl.invoke(obj).toString();

                filesModified = (!StringUtils.isEmpty(wsdl) && !ArrayUtils.isEmpty(BuildHelper.getModifiedFiles(buildContext, new File(wsdl))));

                if(!filesModified)
                {
                    @SuppressWarnings("unchecked")
                    final Set<String> bindingFiles = (Set<String>) getBindingFiles.invoke(obj);

                    for(final String bindingFile : bindingFiles)
                    {
                        filesModified = (!StringUtils.isEmpty(bindingFile) && !ArrayUtils.isEmpty(BuildHelper.getModifiedFiles(buildContext, new File(bindingFile))));

                        if(filesModified)
                        {
                            break;
                        }
                    }
                }

                if(filesModified)
                {
                    break;
                }
            }
        }

        if(!filesModified)
        {
            return null;
        }

        final Set<IProject> result = super.build(kind, monitor);

        if(sourceRoot != null)
        {
            buildContext.refresh(sourceRoot);
        }

        return result;
    }
}
