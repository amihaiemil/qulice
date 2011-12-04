/**
 * Copyright (c) 2011, Qulice.com
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met: 1) Redistributions of source code must retain the above
 * copyright notice, this list of conditions and the following
 * disclaimer. 2) Redistributions in binary form must reproduce the above
 * copyright notice, this list of conditions and the following
 * disclaimer in the documentation and/or other materials provided
 * with the distribution. 3) Neither the name of the Qulice.com nor
 * the names of its contributors may be used to endorse or promote
 * products derived from this software without specific prior written
 * permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT
 * NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL
 * THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.qulice.maven;

import com.qulice.spi.Environment;
import com.qulice.spi.EnvironmentMocker;
import com.qulice.spi.ValidationException;
import com.qulice.spi.Validator;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.model.Build;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.dependency.analyzer.ProjectDependencyAnalysis;
import org.apache.maven.shared.dependency.analyzer.ProjectDependencyAnalyzer;
import org.codehaus.plexus.PlexusConstants;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.context.Context;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import org.slf4j.impl.StaticLoggerBinder;

/**
 * Mocker of {@link MavenProject}.
 * @author Yegor Bugayenko (yegor@qulice.com)
 * @version $Id$
 */
public final class MavenEnvironmentMocker {

    /**
     * Env mocker.
     */
    private final EnvironmentMocker envMocker = new EnvironmentMocker();

    /**
     * Project.
     */
    private final MavenProjectMocker projectMocker = new MavenProjectMocker();

    /**
     * Plexus container, mock.
     */
    private final PlexusContainer container =
        Mockito.mock(PlexusContainer.class);

    /**
     * Inject this object into plexus container.
     * @throws Exception If something wrong happens inside
     */
    public MavenEnvironmentMocker inPlexus(final String role, final String hint,
        final Object object) throws Exception {
        Mockito.doReturn(object).when(this.container).lookup(role, hint);
        return this;
    }

    /**
     * With this project mocker.
     * @param mocker The project mocker
     * @return This object
     */
    public MavenEnvironmentMocker with(final MavenProjectMocker mocker) {
        this.projectMocker = mocker;
        return this;
    }

    /**
     * With this file on board.
     * @param name File name related to basedir
     * @param content File content to write
     * @return This object
     * @throws IOException If some IO problem
     */
    public MavenEnvironmentMocker withFile(final String name,
        final String content) throws IOException {
        this.envMocker.withFile(name, content);
        return this;
    }

    /**
     * With this file on board.
     * @param name File name related to basedir
     * @param bytes File content to write
     * @return This object
     * @throws IOException If some IO problem
     */
    public MavenEnvironmentMocker withFile(final String name,
        final byte[] bytes) throws IOException {
        this.envMocker.withFile(name, bytes);
        return this;
    }

    /**
     * Mock it.
     * @throws Exception If something wrong happens inside
     */
    public MavenEnvironment mock() throws Exception {
        this.projectMocker.inBasedir(this.envMocker.getBasedir());
        final MavenProject project = this.projectMocker.mock();
        final Environment parent = this.envMocker.mock();
        final Environment env = Mockito.mock(MavenEnvironment.class);
        Mockito.doReturn(project).when(env).project();
        final Context context = Mockito.mock(Context.class);
        Mockito.doReturn(context).when(env).context();
        Mockito.doReturn(this.container).when(context).get(Mockito.anyString());
        return env;
    }

}
