/*
 *
 * Copyright (c) 2011 bitstrings.org - Pino Silvaggio
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse Public
 * License v1.0 which accompanies this distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.sonatype.m2e.cxf.internal;

import java.io.File;

import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.Scanner;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.m2e.core.embedder.IMaven;
import org.sonatype.plexus.build.incremental.BuildContext;

public final class BuildHelper
{
    private BuildHelper()
    {
    }

    public static String[] getModifiedFiles(final MavenProject mavenProject, final MojoExecution mojoExecution, final IMaven maven, final BuildContext buildContext, final File source, final String includesParam, final String excludesParam,
            final IProgressMonitor monitor) throws Exception
    {
        return getModifiedFiles(buildContext, source, maven.getMojoParameterValue(mavenProject, mojoExecution, includesParam, String[].class, monitor), maven.getMojoParameterValue(mavenProject, mojoExecution, excludesParam, String[].class, monitor));
    }

    public static String[] getModifiedFiles(final MavenProject mavenProject, final MojoExecution mojoExecution, final IMaven maven, final BuildContext buildContext, final String sourceParam, final String includesParam, final String excludesParam,
            final IProgressMonitor monitor) throws Exception
    {
        return getModifiedFiles(buildContext, maven.getMojoParameterValue(mavenProject, mojoExecution, sourceParam, File.class, monitor), maven.getMojoParameterValue(mavenProject, mojoExecution, includesParam, String[].class, monitor),
                maven.getMojoParameterValue(mavenProject, mojoExecution, excludesParam, String[].class, monitor));
    }

    public static String[] getModifiedFiles(final MavenProject mavenProject, final MojoExecution mojoExecution, final IMaven maven, final BuildContext buildContext, final String sourceParam, final String[] includes, final String[] excludes,
            final IProgressMonitor monitor) throws Exception
    {
        return getModifiedFiles(buildContext, maven.getMojoParameterValue(mavenProject, mojoExecution, sourceParam, File.class, monitor), includes, excludes);
    }

    public static String[] getModifiedFiles(final BuildContext buildContext, final File source, final String[] includes, final String[] excludes) throws Exception
    {
        if((source == null) || !source.exists())
        {
            return null;
        }

        final Scanner ds = buildContext.newScanner(source);

        if((includes != null) && (includes.length > 0))
        {
            ds.setIncludes(includes);
        }

        if((excludes != null) && (excludes.length > 0))
        {
            ds.setExcludes(excludes);
        }

        ds.scan();

        return ds.getIncludedFiles();
    }

    public static String[] getModifiedFiles(final BuildContext buildContext, final File source) throws Exception
    {
        return getModifiedFiles(buildContext, source, null, null);
    }

    public static String[] getModifiedFiles(final MavenProject mavenProject, final MojoExecution mojoExecution, final IMaven maven, final BuildContext buildContext, final String sourceParam, final IProgressMonitor monitor) throws Exception
    {
        return getModifiedFiles(buildContext, maven.getMojoParameterValue(mavenProject, mojoExecution, sourceParam, File.class, monitor), null, null);
    }
}