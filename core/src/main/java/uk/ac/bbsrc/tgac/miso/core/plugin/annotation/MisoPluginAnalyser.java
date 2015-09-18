/*
 * Copyright (c) 2012. The Genome Analysis Centre, Norwich, UK
 * MISO project contacts: Robert Davey, Mario Caccamo @ TGAC
 * *********************************************************************
 *
 * This file is part of MISO.
 *
 * MISO is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MISO is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MISO.  If not, see <http://www.gnu.org/licenses/>.
 *
 * *********************************************************************
 */

package uk.ac.bbsrc.tgac.miso.core.plugin.annotation;

//import uk.ac.ebi.arrayexpress2.annotation.ArrayExpress2Metadata;
//import uk.ac.ebi.arrayexpress2.annotation.ArrayExpress2Operation;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

/**
 * uk.ac.bbsrc.tgac.miso.core.plugin.annotation
 * <p/>
 * Searches for MISO annotations and generates information about how
 * to invoke the tools.  This introspector can be used to examine jar files or
 * the classpath.
 * <p/>
 * This introspector can also be used to generate a client stub, which can then
 * be invoked directly from the command line using a client that binds arguments
 * to the generated stubs.
 *
 * @author Tony Burdett
 * @author Rob Davey
 * @date 20-Jul-2010
 * @since 0.0.2
 */
public class MisoPluginAnalyser {
    /**
     * Probes the set of currently loaded classes and uses the file
     * "META-INF/miso/plugins" to discover those annotated with {@link
     * MisoOperation} annotations. The
     * plugins file is a SPI-type file, that lists all the annotated
     * classes, and is generated by annotation processing at compile-time. Each
     * resource thus annotated is used to extract an {@link
     * MisoMetadata} object, and the set of all metadata returned.
     *
     * @return the metadata describing all MISO tools found
     * @throws java.io.IOException    if there was a problem reading from the
     *                                resource "META-INF/miso/plugins"
     * @throws ClassNotFoundException if a class described in one of the
     *                                META-INF/miso/plugins was not be
     *                                loaded
     */

    //  public Collection<MisoMetadata> analyse()
    //      throws IOException, ClassNotFoundException {
    //    return analyseByLoader(getClass().getClassLoader());
    //  }

    /**
     * Probes all classes in the supplied jar file and uses the file
     * "META-INF/miso/plugins" to discover those annotated with {@link
     * MisoOperation} annotations. The
     * plugins file is a SPI-type file, that lists all the annotated
     * classes, and is generated by annotation processing at compile-time. Each
     * resource thus annotated is used to extract an {@link
     * MisoMetadata} object, and the set of all metadata returned.
     *
     * @param pluginJarFile the jar file containing MISO tool(s) that we
     *                   want to analyse
     * @return the metadata describing all MISO tools found
     * @throws java.io.IOException    if there was a problem reading from the
     *                                resource "META-INF/miso/plugins"
     * @throws ClassNotFoundException if a class described in one of the
     *                                META-INF/miso/plugins was not be
     *                                loaded
     */

/*  public Collection<MisoMetadata> analyse(File pluginJarFile)
      throws IOException, ClassNotFoundException {
    // load the supplied jar file
    return analyseByLoader(new URLClassLoader(
        new URL[]{pluginJarFile.getAbsoluteFile().toURI().toURL()}));
  }

  private Collection<MisoMetadata> analyseByLoader(ClassLoader loader)
      throws IOException, ClassNotFoundException {
    // collection of metadata objects to return
    Set<MisoMetadata> metadata = new HashSet<MisoMetadata>();

    // grab services-type file: this is a list of classes annotated with @MisoPlugin
    Enumeration<URL> resources =
        loader.getResources("META-INF/miso/plugins");

    while (resources.hasMoreElements()) {
      URL resource = resources.nextElement();

      // read generator line
      BufferedReader reader =
          new BufferedReader(new InputStreamReader(resource.openStream()));

      String line;
      while ((line = reader.readLine()) != null) {
        Class m = Class.forName(line.trim());
        metadata.add(examineClass(m));
      }
    }

    return metadata;
  }

  private MisoMetadata examineClass(Class misoPluginClass) {
    MisoMetadataImpl misoMetadata = null;

    // reflect class to discover operation name
    if (misoPluginClass.isAnnotationPresent(MisoPlugin.class)) {
      misoMetadata = new MisoMetadataImpl(misoPluginClass);

      // reflect all methods on this class to discover operations
      for (Method m : misoPluginClass.getMethods()) {
        if (m.isAnnotationPresent(MisoOperation.class)) {
          examineMethods(misoMetadata, m);
        }
      }
    }

    return misoMetadata;
  }

  private void examineMethods(MisoMetadataImpl misoMetadata,
                              Method misoOperationMethod) {
    // create operation by checking annotations on the supplied method
    MisoOperation misoOp =
        misoOperationMethod.getAnnotation(MisoOperation.class);

    // read operation metadata
    final String opName = misoOp.name();
    final String opDescription = misoOp.description();
    final int parallelProcesses = misoOp.maxParallelProcesses();
    final int memRequirement = misoOp.maxMemoryRequirement();
    final Collection<MisoMetadata.Parameter> parameters =
        new HashSet<MisoMetadata.Parameter>();
    final String methodName = misoOperationMethod.getName();

    // read parameter metadata
    Annotation[][] parameterAnnotations =
        misoOperationMethod.getParameterAnnotations();
    for (int pos = 0; pos < parameterAnnotations.length; pos++) {
      // check next argument for MisoParameter annotations
      for (Annotation annotation : parameterAnnotations[pos]) {
        if (annotation instanceof MisoParameter) {
          // found annotation, create argument
          MisoParameter misoP = (MisoParameter) annotation;

          final String paramName = misoP.name();
          final String paramDescription = misoP.description();
          final int paramPosition = pos;

          MisoMetadata.Parameter parameter =
              new MisoMetadata.Parameter() {
                @Override
                public String getParameterName() {
                  return paramName;
                }

                @Override
                public String getParameterDescription() {
                  return paramDescription;
                }

                @Override
                public int getParameterPosition() {
                  return paramPosition;
                }
              };

          parameters.add(parameter);
        }
      }
    }

    // create the operation
    MisoMetadata.Operation operation =
        new MisoMetadata.Operation() {
          @Override
          public String getOperationName() {
            return opName;
          }

          @Override
          public String getOperationDescription() {
            return opDescription;
          }

          @Override
          public int getMaxAllowedParallelProcesses() {
            return parallelProcesses;
          }

          @Override
          public int getMaxMemoryRequirement() {
            return memRequirement;
          }

          @Override
          public Collection<MisoMetadata.Parameter> getParameters() {
            return parameters;
          }

          @Override public String getMethodName() {
            return methodName;
          }
        };

    misoMetadata.addOperation(operation);
  }

  private class MisoMetadataImpl implements MisoMetadata {
    private Class misoPluginClass;

    private String name;
    private String description;
    private Set<Operation> operations;

    public MisoMetadataImpl(Class mpc) {
      this.misoPluginClass = mpc;

      // examine annotations, extract mode name
      Annotation ann = mpc.getAnnotation(
          MisoPlugin.class);
      if (ann instanceof MisoPlugin) {
        MisoPlugin mp = (MisoPlugin) ann;

        this.name = mp.name();
        this.description = mp.description();

        this.operations = new HashSet<Operation>();
      }
    }

    @Override
    public String getToolName() {
      return name;
    }

    @Override public String getToolDescription() {
      return description;
    }

    @Override
    public Collection<Operation> getOperations() {
      return operations;
    }

    @Override public String getClassName() {
      return misoPluginClass.getName();
    }

    public void addOperation(Operation operation) {
      operations.add(operation);
    }
  }
  */
}
