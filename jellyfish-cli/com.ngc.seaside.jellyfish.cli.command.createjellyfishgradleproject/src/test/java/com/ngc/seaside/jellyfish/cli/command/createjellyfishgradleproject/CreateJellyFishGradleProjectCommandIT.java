package com.ngc.seaside.jellyfish.cli.command.createjellyfishgradleproject;

import com.ngc.blocs.service.log.api.ILogService;
import com.ngc.seaside.jellyfish.api.DefaultParameter;
import com.ngc.seaside.jellyfish.api.DefaultParameterCollection;
import com.ngc.seaside.jellyfish.api.IJellyFishCommandOptions;
import com.ngc.seaside.jellyfish.cli.command.test.service.MockedTemplateService;
import com.ngc.seaside.jellyfish.service.buildmgmt.api.DependencyScope;
import com.ngc.seaside.jellyfish.service.buildmgmt.api.IBuildDependency;
import com.ngc.seaside.jellyfish.service.buildmgmt.api.IBuildManagementService;
import com.ngc.seaside.jellyfish.service.template.api.ITemplateService;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Collections;

import static com.ngc.seaside.jellyfish.cli.command.test.files.TestingFiles.assertFileLinesEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CreateJellyFishGradleProjectCommandIT {

   private CreateJellyFishGradleProjectCommand command;

   @Mock
   private IBuildManagementService buildManagementService;

   @Mock
   private ILogService logService;

   @Mock
   private IJellyFishCommandOptions options;

   private ITemplateService templateService;

   @Rule
   public TemporaryFolder temporaryDirectory = new TemporaryFolder();

   private File outputDirectory;

   @Before
   public void setup() throws IOException {
      outputDirectory = temporaryDirectory.newFolder();

      templateService = new MockedTemplateService()
            .useRealPropertyService()
            .useDefaultUserValues(true)
            .setTemplateDirectory(CreateJellyFishGradleProjectCommandIT.class.getPackage().getName(),
                                  Paths.get("src/main/template"));

      IBuildDependency dependency1 = newDependency("com.google.protobuf",
                                                   "protobuf-gradle-plugin",
                                                   "1.2.3",
                                                   "protobufVersion");
      IBuildDependency dependency2 = newDependency("com.google.guava",
                                                   "guava",
                                                   "1.2.3",
                                                   "guavaVersion");
      IBuildDependency dependency3 = newDependency("com.test",
                                                   "test-stuff",
                                                   "1.4.5",
                                                   "testVersion");
      when(buildManagementService.getRegisteredDependencies(options, DependencyScope.BUILDSCRIPT))
            .thenReturn(Collections.singleton(dependency1));
      when(buildManagementService.getRegisteredDependencies(options, DependencyScope.BUILD))
            .thenReturn(Collections.singleton(dependency2));
      when(buildManagementService.getRegisteredDependencies(options, DependencyScope.TEST))
            .thenReturn(Collections.singleton(dependency3));

      command = new CreateJellyFishGradleProjectCommand();
      command.setLogService(logService);
      command.setTemplateService(templateService);
      command.setBuildManagementService(buildManagementService);
      command.activate();
   }

   @Test
   public void testCommand() throws IOException {
      final String projectName = "test-project-1";
      final String version = "1.0";
      final String sdgav = "com.ngc.seaside.system1:system.descriptor:1.0-SNAPSHOT";
      final String model = "com.ngc.seaside.Model1";

      DefaultParameterCollection collection = new DefaultParameterCollection();
      collection.addParameter(new DefaultParameter<>(CreateJellyFishGradleProjectCommand.PROJECT_NAME_PROPERTY,
                                                     projectName));
      collection.addParameter(new DefaultParameter<>(CreateJellyFishGradleProjectCommand.VERSION_PROPERTY,
                                                     version));
      collection.addParameter(new DefaultParameter<>(CreateJellyFishGradleProjectCommand.SYSTEM_DESCRIPTOR_GAV_PROPERTY,
                                                     sdgav));
      collection.addParameter(new DefaultParameter<>(CreateJellyFishGradleProjectCommand.MODEL_NAME_PROPERTY,
                                                     model));
      collection.addParameter(new DefaultParameter<>(CreateJellyFishGradleProjectCommand.OUTPUT_DIR_PROPERTY,
                                                     outputDirectory.toString()));
      when(options.getParameters()).thenReturn(collection);

      command.run(options);

      assertFileLinesEquals(
            "settings.gradle not correct!",
            Paths.get("src", "test", "resources", "settings.gradle.expected"),
            outputDirectory.toPath().resolve(projectName).resolve("settings.gradle"));

      assertFileLinesEquals(
            "build.gradle not correct!",
            Paths.get("src", "test", "resources", "build.gradle.expected"),
            outputDirectory.toPath().resolve(projectName).resolve("build.gradle"));
   }

   private static IBuildDependency newDependency(String groupId,
                                                 String artifactId,
                                                 String version,
                                                 String versionProperty) {
      IBuildDependency d = mock(IBuildDependency.class);
      when(d.getGroupId()).thenReturn(groupId);
      when(d.getArtifactId()).thenReturn(artifactId);
      when(d.getVersion()).thenReturn(version);
      when(d.getVersionPropertyName()).thenReturn(versionProperty);
      return d;
   }
}
