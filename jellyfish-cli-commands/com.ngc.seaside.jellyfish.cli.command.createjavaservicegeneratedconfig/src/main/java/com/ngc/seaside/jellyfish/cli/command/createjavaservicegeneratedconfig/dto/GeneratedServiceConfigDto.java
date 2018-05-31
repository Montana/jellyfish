package com.ngc.seaside.jellyfish.cli.command.createjavaservicegeneratedconfig.dto;

import com.ngc.seaside.jellyfish.api.IJellyFishCommandOptions;
import com.ngc.seaside.jellyfish.service.buildmgmt.api.IBuildDependency;
import com.ngc.seaside.jellyfish.service.buildmgmt.api.IBuildManagementService;
import com.ngc.seaside.systemdescriptor.model.api.model.IModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class GeneratedServiceConfigDto {

   private final IBuildManagementService buildManagementService;
   private final IJellyFishCommandOptions options;

   private IModel model;
   private String packageName;
   private String projectDirectoryName;
   private String baseProjectArtifactName;
   private String connectorClassname;
   private boolean hasTelemetry;

   private List<TransportProviderDto> transportProviders = new ArrayList<>();
   private Set<String> transportProviderDependencies = new LinkedHashSet<>();
   private Set<String> readinessSubscribers = new LinkedHashSet<>();
   private Set<String> requiredReadinessClasses = new LinkedHashSet<>();

   public GeneratedServiceConfigDto(IBuildManagementService buildManagementService,
                                    IJellyFishCommandOptions options) {
      this.buildManagementService = buildManagementService;
      this.options = options;
   }

   public IModel getModel() {
      return model;
   }

   public String getModelName() {
      return model.getName();
   }

   public GeneratedServiceConfigDto setModel(IModel model) {
      this.model = model;
      return this;
   }

   public String getPackageName() {
      return packageName;
   }

   public GeneratedServiceConfigDto setPackageName(String packageName) {
      this.packageName = packageName;
      return this;
   }

   public String getBaseProjectArtifactName() {
      return baseProjectArtifactName;
   }

   public GeneratedServiceConfigDto setBaseProjectArtifactName(String baseProjectArtifactName) {
      this.baseProjectArtifactName = baseProjectArtifactName;
      return this;
   }

   public String getProjectDirectoryName() {
      return projectDirectoryName;
   }

   public GeneratedServiceConfigDto setProjectDirectoryName(String projectDirectoryName) {
      this.projectDirectoryName = projectDirectoryName;
      return this;
   }

   public boolean hasTelemetry() {
      return hasTelemetry;
   }

   public GeneratedServiceConfigDto setTelemetry(boolean hasTelemetry) {
      this.hasTelemetry = hasTelemetry;
      return this;
   }

   public List<TransportProviderDto> getTransportProviders() {
      return this.transportProviders;
   }

   public GeneratedServiceConfigDto addTransportProvider(TransportProviderDto dto) {
      this.transportProviders.add(dto);
      return this;
   }

   public Set<String> getTransportProviderDependencies() {
      return transportProviderDependencies;
   }

   public GeneratedServiceConfigDto addTransportProviderDependencies(Collection<String> dependencies) {
      this.transportProviderDependencies.addAll(dependencies);
      return this;
   }

   public String getConnectorClassname() {
      return this.connectorClassname;
   }

   public GeneratedServiceConfigDto setConnectorClassname(String connectorClassname) {
      this.connectorClassname = connectorClassname;
      return this;
   }

   public Set<String> getReadinessSubscribers() {
      return readinessSubscribers;
   }

   public GeneratedServiceConfigDto addReadinessSubscriber(String subscriberClassname) {
      this.readinessSubscribers.add(subscriberClassname);
      return this;
   }

   public Set<String> getRequiredReadinessClasses() {
      return requiredReadinessClasses;
   }

   public GeneratedServiceConfigDto addRequiredReadinessClasses(String classname) {
      this.requiredReadinessClasses.add(classname);
      return this;
   }

   /**
    * @param groupAndArtifactId String of the group ID that you want formatted
    * @return String of formatted dependency
    */
   public String getFormattedDependency(String groupAndArtifactId) {
      IBuildDependency dependency = buildManagementService.registerDependency(options, groupAndArtifactId);
      return String.format("%s:%s:$%s",
                           dependency.getGroupId(),
                           dependency.getArtifactId(),
                           dependency.getVersionPropertyName());
   }
}
