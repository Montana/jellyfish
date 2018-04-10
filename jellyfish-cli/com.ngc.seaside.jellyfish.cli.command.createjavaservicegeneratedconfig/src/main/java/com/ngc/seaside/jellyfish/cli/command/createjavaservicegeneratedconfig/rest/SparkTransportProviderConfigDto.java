package com.ngc.seaside.jellyfish.cli.command.createjavaservicegeneratedconfig.rest;

import com.ngc.seaside.jellyfish.api.IJellyFishCommandOptions;
import com.ngc.seaside.jellyfish.cli.command.createjavaservicegeneratedconfig.dto.GeneratedServiceConfigDto;
import com.ngc.seaside.jellyfish.cli.command.createjavaservicegeneratedconfig.dto.ITransportProviderConfigDto;
import com.ngc.seaside.jellyfish.cli.command.createjavaservicegeneratedconfig.dto.TransportProviderDto;
import com.ngc.seaside.jellyfish.service.config.api.ITransportConfigurationService;
import com.ngc.seaside.jellyfish.service.config.api.dto.RestConfiguration;
import com.ngc.seaside.systemdescriptor.model.api.model.IDataReferenceField;
import com.ngc.seaside.systemdescriptor.model.api.model.IModel;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class SparkTransportProviderConfigDto implements ITransportProviderConfigDto<SparkDto> {
   public static final String REST_TEMPLATE_SUFFIX = "rest";

   private static final String REST_TRANSPORT_PROVIDER_COMPONENT_NAME =
         "com.ngc.seaside.service.transport.impl.provider.rest.RestTransportProvider";
   private static final String REST_CONFIGURATION_CLASS_NAME_SUFFIX = "RestConfiguration";
   private final static String REST_PROVIDER_VARIABLE_NAME = "sparkProvider";
   private static final String REST_TOPIC_PACKAGE_NAME = "com.ngc.seaside.service.transport.impl.topic.spark";
   private static final String REST_TOPIC_CLASS_NAME = "SparkTopic";
   private static final String REST_TOPIC_DEPENDENCY = "com.ngc.seaside:service.transport.impl.topic.spark";
   private static final String REST_PROVIDER_DEPENDENCY = "com.ngc.seaside:service.transport.impl.provider.spark";

   private ITransportConfigurationService transportConfigurationService;

   public SparkTransportProviderConfigDto(ITransportConfigurationService transportConfigurationService) {
      this.transportConfigurationService = transportConfigurationService;
   }

   @Override
   public TransportProviderDto getTransportProviderDto(SparkDto dto) {
      return new TransportProviderDto()
            .setComponentName(REST_TRANSPORT_PROVIDER_COMPONENT_NAME)
            .setConfigurationType(dto.getModelName() + REST_CONFIGURATION_CLASS_NAME_SUFFIX)
            .setProviderName(REST_PROVIDER_VARIABLE_NAME)
            .setTopicPackage(REST_TOPIC_PACKAGE_NAME)
            .setTopicType(REST_TOPIC_CLASS_NAME);
   }

   @Override
   public Optional<SparkDto> getConfigurationDto(GeneratedServiceConfigDto serviceConfigDto,
                                                 IJellyFishCommandOptions options, IModel model, String topicsClassName,
                                                 Map<String, IDataReferenceField> topics) {
      SparkDto sparkDto = new SparkDto().setBaseDto(serviceConfigDto)
                                        .setTopicsImport(topicsClassName);
      String topicsPrefix = topicsClassName.substring(topicsClassName.lastIndexOf('.') + 1) + '.';

      for (Map.Entry<String, IDataReferenceField> entry : topics.entrySet()) {
         String topicName = entry.getKey();
         IDataReferenceField field = entry.getValue();

         Collection<RestConfiguration> configurations =
               transportConfigurationService.getRestConfiguration(options, field);
         int count = 1;
         for (RestConfiguration configuration : configurations) {
            SparkTopicDto topicDto = new SparkTopicDto().setNetworkAddress(configuration.getNetworkAddress())
                                                        .setNetworkInterface(configuration.getNetworkInterface())
                                                        .setPort(configuration.getPort())
                                                        .setHttpMethod(configuration.getHttpMethod())
                                                        .setPath(configuration.getPath())
                                                        .setContentType(configuration.getContentType())
                                                        .setVariableName(
                                                              field.getName() + (configurations.size() > 1 ? count
                                                                                                           : ""))
                                                        .setName(topicsPrefix + topicName);

            sparkDto.addTopic(topicDto);
            count++;
         }
      }

      if (sparkDto.getTopics().isEmpty()) {
         return Optional.empty();
      }

      return Optional.of(sparkDto);
   }

   @Override
   public String getTemplateSuffix() {
      return REST_TEMPLATE_SUFFIX;
   }

   @Override
   public Set<String> getDependencies(boolean distribution) {
      Set<String> dependencies = new LinkedHashSet<>();
      dependencies.add(REST_TOPIC_DEPENDENCY);
      if (distribution) {
         dependencies.add(REST_PROVIDER_DEPENDENCY);
      }
      return dependencies;
   }
}
