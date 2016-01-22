/*
 * Copyright 2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.codehaus.gmavenplus.mojo;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.model.fileset.FileSet;
import org.codehaus.gmavenplus.model.Version;
import org.codehaus.gmavenplus.util.ClassWrangler;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.util.HashSet;


/**
 * @author Keegan Witt
 */
@RunWith(MockitoJUnitRunner.class)
public class GroovyDocMojoTest {
    private static final String INTENTIONAL_EXCEPTION_MESSAGE = "Intentionally blowing up.";

    @Spy
    private GroovyDocMojo groovydocMojo;

    @Before
    public void setup() throws Exception {
        Mockito.doReturn(new HashSet<File>()).when(groovydocMojo).getSources();
        groovydocMojo.project = Mockito.mock(MavenProject.class);
        Mockito.doReturn(Mockito.mock(File.class)).when(groovydocMojo.project).getBasedir();
        groovydocMojo.classWrangler = Mockito.mock(ClassWrangler.class);
        Mockito.doReturn(new Version(1, 5, 0)).when(groovydocMojo.classWrangler).getGroovyVersion();
    }

    @Test
    public void testCallsExpectedMethods() throws Exception {
        Mockito.doReturn(true).when(groovydocMojo).groovyVersionSupportsAction();
        Mockito.doNothing().when(groovydocMojo).doGroovydocGeneration(Mockito.any(FileSet[].class), Mockito.anyList(), Mockito.any(File.class));
        groovydocMojo.execute();
        Mockito.verify(groovydocMojo, Mockito.times(1)).doGroovydocGeneration(Mockito.any(FileSet[].class), Mockito.anyList(), Mockito.any(File.class));
        groovydocMojo.classWrangler = Mockito.mock(ClassWrangler.class);
        Mockito.doReturn(new Version(1, 5, 0)).when(groovydocMojo.classWrangler).getGroovyVersion();
    }

    @Test
    public void testGroovyVersionDoesntSupportAction() throws Exception {
        Mockito.doReturn(false).when(groovydocMojo).groovyVersionSupportsAction();
        groovydocMojo.execute();
        Mockito.verify(groovydocMojo, Mockito.never()).logPluginClasspath();
    }

    @Test (expected = MojoExecutionException.class)
    public void testClassNotFoundExceptionThrowsMojoExecutionException() throws Exception {
        Mockito.doReturn(true).when(groovydocMojo).groovyVersionSupportsAction();
        Mockito.doThrow(new ClassNotFoundException(INTENTIONAL_EXCEPTION_MESSAGE)).when(groovydocMojo).doGroovydocGeneration(Mockito.any(FileSet[].class), Mockito.anyList(), Mockito.any(File.class));
        groovydocMojo.execute();
    }

    @Test (expected = MojoExecutionException.class)
    public void testInvocationTargetExceptionThrowsMojoExecutionException() throws Exception {
        Mockito.doReturn(true).when(groovydocMojo).groovyVersionSupportsAction();
        Mockito.doThrow(new InvocationTargetException(Mockito.mock(Exception.class), INTENTIONAL_EXCEPTION_MESSAGE)).when(groovydocMojo).doGroovydocGeneration(Mockito.any(FileSet[].class), Mockito.anyList(), Mockito.any(File.class));
        groovydocMojo.execute();
    }

    @Test (expected = MojoExecutionException.class)
    public void testInstantiationExceptionThrowsMojoExecutionException() throws Exception {
        Mockito.doReturn(true).when(groovydocMojo).groovyVersionSupportsAction();
        Mockito.doThrow(new InstantiationException(INTENTIONAL_EXCEPTION_MESSAGE)).when(groovydocMojo).doGroovydocGeneration(Mockito.any(FileSet[].class), Mockito.anyList(), Mockito.any(File.class));
        groovydocMojo.execute();
    }

    @Test (expected = MojoExecutionException.class)
    public void testIllegalAccessExceptionThrowsMojoExecutionException() throws Exception {
        Mockito.doReturn(true).when(groovydocMojo).groovyVersionSupportsAction();
        Mockito.doThrow(new IllegalAccessException(INTENTIONAL_EXCEPTION_MESSAGE)).when(groovydocMojo).doGroovydocGeneration(Mockito.any(FileSet[].class), Mockito.anyList(), Mockito.any(File.class));
        groovydocMojo.execute();
    }

    @Test (expected = MojoExecutionException.class)
    @SuppressWarnings("unchecked")
    public void testMalformedURLExceptionThrowsMojoExecutionException() throws Exception {
        Mockito.doReturn(true).when(groovydocMojo).groovyVersionSupportsAction();
        Mockito.doThrow(new MalformedURLException(INTENTIONAL_EXCEPTION_MESSAGE)).when(groovydocMojo).doGroovydocGeneration(Mockito.any(FileSet[].class), Mockito.anyList(), Mockito.any(File.class));
        groovydocMojo.execute();
    }

    @Test
    public void testGroovyVersionSupportsActionTrue() {
        Mockito.doReturn(Version.parseFromString("1.5.0")).when(groovydocMojo.classWrangler).getGroovyVersion();
        Assert.assertTrue(groovydocMojo.groovyVersionSupportsAction());
    }

    @Test
    public void testGroovyVersionSupportsActionFalse() {
        Mockito.doReturn(Version.parseFromString("1.0")).when(groovydocMojo.classWrangler).getGroovyVersion();
        Assert.assertFalse(groovydocMojo.groovyVersionSupportsAction());
    }

}
