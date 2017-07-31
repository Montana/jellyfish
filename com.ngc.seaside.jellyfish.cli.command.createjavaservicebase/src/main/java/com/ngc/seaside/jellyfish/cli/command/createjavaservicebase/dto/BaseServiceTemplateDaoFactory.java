package com.ngc.seaside.jellyfish.cli.command.createjavaservicebase.dto;

import com.ngc.seaside.jellyfish.cli.command.createjavaservice.dto.ArgumentDto;
import com.ngc.seaside.jellyfish.cli.command.createjavaservice.dto.MethodDto;
import com.ngc.seaside.jellyfish.cli.command.createjavaservice.dto.TemplateDto;
import com.ngc.seaside.jellyfish.cli.command.createjavaservice.dto.TemplateDtoFactory;
import com.ngc.seaside.systemdescriptor.model.api.model.IModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class BaseServiceTemplateDaoFactory extends TemplateDtoFactory {

   @Override
   public TemplateDto newDto(IModel model, String packagez) {
      BaseServiceTemplateDto dto = (BaseServiceTemplateDto) super.newDto(model, packagez);
      setExportedPackages(dto, model, packagez);
      setTransportTopics(dto, model, packagez);
      setPublishMethods(dto, model, packagez);
      setReceiveMethods(dto, model, packagez);
      return dto;
   }

   @Override
   protected TemplateDto createDto() {
      return new BaseServiceTemplateDto();
   }

   private static void setExportedPackages(BaseServiceTemplateDto dto, IModel model, String packagez) {
      dto.setExportedPackages(Collections.singleton("*"));
   }

   private static void setTransportTopics(BaseServiceTemplateDto dto, IModel model, String packagez) {
      Set<String> topics = new TreeSet<>();
      for (MethodDto method : dto.getMethods()) {
         for (ArgumentDto arg : method.getArguments()) {
            topics.add(constantize(arg.getArgumentClassName()));
         }
      }
      dto.setTransportTopics(topics);
   }

   private static void setPublishMethods(BaseServiceTemplateDto dto,
                                         IModel model,
                                         String packagez) {
      // TODO TH: this is a hacky way to figure out what to publish.
      List<MethodDto> methods = new ArrayList<>(dto.getMethods().size());
      for (MethodDto methodDto : dto.getMethods()) {
         if (methodDto.isReturns()) {
            MethodDto m = new MethodDto()
                  .setOverride(false)
                  .setReturns(false)
                  .setMethodName("publish" + methodDto.getReturnArgument().getArgumentClassName())
                  .setArguments(Collections.singletonList(methodDto.getReturnArgument()));
            methods.add(m);
         }
      }
      dto.setPublishingMethods(methods);
   }

   private static void setReceiveMethods(BaseServiceTemplateDto dto,
                                  IModel model,
                                  String packagez) {
      // TODO TH: this is a hacky way to figure out what to receive.
      List<MethodDto> methods = new ArrayList<>(dto.getMethods().size());
      for (MethodDto methodDto : dto.getMethods()) {
         // TODO TH: handle scenarios with multiple inputs.
         ArgumentDto argument = methodDto.getArguments().get(0);
         ArgumentDto eventArg = new ArgumentDto()
               .setArgumentPackageName(argument.getArgumentPackageName())
               .setArgumentName("event")
               .setArgumentClassName(String.format("IEvent<%s>", argument.getArgumentClassName()));
         MethodDto m = new MethodDto()
               .setOverride(false)
               .setReturns(false)
               .setMethodName("receive" + argument.getArgumentClassName())
               .setArguments(Collections.singletonList(eventArg));
         methods.add(m);
      }
      dto.setReceivingMethods(methods);
   }

//
//   private static MethodDto getPublishMethod(IScenario scenario, String packagez) {
//      MethodDto dao;
//      if (isReceivingAndPublishing(scenario)) {
//         dao = getReceivingAndPublishingMethod(scenario, packagez);
//      } else {
//         // Should probably ignore this and not throw an exception.
//         throw new IllegalArgumentException(String.format("scenario %s.%s contains no supported verbs!",
//                                                          scenario.getParent().getFullyQualifiedName(),
//                                                          scenario.getName()));
//      }
//      return dao;
//   }

   private static String constantize(String value) {
      char[] chars = value.toCharArray();
      StringBuilder sb = new StringBuilder();
      for (int i = 0; i < chars.length; i++) {
         char c = chars[i];
         if (i >= 0 && Character.isUpperCase(c)) {
            sb.append("_");
         }
         sb.append(Character.toUpperCase(c));
      }
      return sb.toString();
   }
}
