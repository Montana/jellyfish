/**
 * UNCLASSIFIED
 * Northrop Grumman Proprietary
 * ____________________________
 *
 * Copyright (C) 2018, Northrop Grumman Systems Corporation
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains the property of
 * Northrop Grumman Systems Corporation. The intellectual and technical concepts
 * contained herein are proprietary to Northrop Grumman Systems Corporation and
 * may be covered by U.S. and Foreign Patents or patents in process, and are
 * protected by trade secret or copyright law. Dissemination of this information
 * or reproduction of this material is strictly forbidden unless prior written
 * permission is obtained from Northrop Grumman.
 */
package com.ngc.seaside.jellyfish.api;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum CommonParameters implements IParameter<String> {
   ARTIFACT_ID("artifactId", "The project's artifact ID"),
   CLASSNAME("classname", "The name of the class that will be generated"),
   CLEAN("clean", "If true, recursively deletes the project (if it already exists) before generating it again"),
   DEPLOYMENT_MODEL("deploymentModel", "The fully qualified name of a system descriptor deployment model"),
   GROUP_ID("groupId", "The project's group ID"),
   HEADER_FILE("headerFile", "A file whose contents will be injected into the header of all source code files that"
            + " Jellyfish generates.  This file should be a plain text file.  Jellyfish includes a"
            + " default header with an NGC license and UNCLASSIFIED markings.  Use a custom header for"
            + " a different license or to change the markings of generated files."),
   INPUT_DIRECTORY("inputDirectory", "Base directory of the system descriptor project"),
   GROUP_ARTIFACT_VERSION("gav",
            "The identifier of a system descriptor project in the form <groupId>:<artifactId>:<version>; "
                     + INPUT_DIRECTORY.getName() + " can be optionally used instead of this parameter"),
   MODEL("model", "The fully qualified name of a system descriptor model"),
   OUTPUT_DIRECTORY("outputDirectory", "Base directory in which to output the project"),
   PACKAGE("package", "The project's default package"),
   UPDATE_GRADLE_SETTING("updateGradleSettings", "If false, the generated project will not be added to any existing"
                                                 + " settings.gradle file"),
   PHASE("phase",
         "Indicates which phase of the command should be executed.  This command supports the following phases: "),
   PROJECT_NAME("projectName", "The name of the project"),
   STEREOTYPES("stereotypes",
               "A comma separated listed of stereotypes used to select models in the project.  This limits the scope of"
               + " the command to these models."),
   SYSTEM("system",
            "If true, indicates that the parameter " + MODEL.getName() + " should be treated as a system model"),
   VERSION("version", "The project's version");

   private static final Pattern GAV_REGEX = Pattern.compile("([^:@\\s]+):([^:@\\s]+):([^:@\\s]+)");
   private final String description;
   private final String name;

   CommonParameters(String name, String description) {
      this.description = description;
      this.name = name;
   }

   @Override
   public String getDescription() {
      return description;
   }

   @Override
   public String getName() {
      return name;
   }

   @Override
   public String getValue() {
      throw new UnsupportedOperationException("not implemented");
   }

   @Override
   public String getStringValue() {
      throw new UnsupportedOperationException("not implemented");
   }

   @Override
   public ParameterCategory getParameterCategory() {
      return ParameterCategory.ADVANCED;
   }

   /**
    * Returns a copy of the current parameter whose parameter category is required.
    *
    * @return a required version of the current parameter
    */
   public IParameter<String> required() {
      DefaultParameter<String> parameter = new DefaultParameter<>(name);
      parameter.setDescription(description);
      parameter.setParameterCategory(ParameterCategory.REQUIRED);
      return parameter;
   }
   
   /**
    * Returns a copy of the current parameter whose parameter category is optional.
    *
    * @return an optional version of the current parameter
    */
   public IParameter<String> optional() {
      DefaultParameter<String> parameter = new DefaultParameter<>(name);
      parameter.setDescription(description);
      parameter.setParameterCategory(ParameterCategory.OPTIONAL);
      return parameter;
   }
   
   /**
    * Returns a copy of the current parameter whose parameter category is advanced.
    *
    * @return an advanced version of the current parameter
    */
   public IParameter<String> advanced() {
      DefaultParameter<String> parameter = new DefaultParameter<>(name);
      parameter.setDescription(description);
      parameter.setParameterCategory(ParameterCategory.ADVANCED);
      return parameter;
   }
   
   /**
    * Returns the boolean value of the given parameter if it was set, false otherwise.
    *
    * @param parameters command parameters
    * @param parameter  name of parameter
    * @return the boolean value of the parameter
    * @throws CommandException if the value is neither 'true' nor 'false'
    */
   public static boolean evaluateBooleanParameter(IParameterCollection parameters, String parameter) {
      return evaluateBooleanParameter(parameters, parameter, false);
   }

   /**
    * Returns the boolean value of the given parameter if it was set, the given default otherwise.
    *
    * @param parameters command parameters
    * @param parameter  name of parameter
    * @return the boolean value of the parameter
    * @throws CommandException if the value is neither 'true' nor 'false'
    */
   public static boolean evaluateBooleanParameter(IParameterCollection parameters,
                                                  String parameter,
                                                  boolean defaultValue) {
      if (parameters.containsParameter(parameter)) {
         String value = parameters.getParameter(parameter).getStringValue().toLowerCase();
         if (!value.equals("true") && !value.equals("false")) {
            throw new CommandException(
                  "Invalid value for " + parameter + ": " + value + ". Expected either true or false.");
         } else {
            return Boolean.valueOf(value);
         }
      } else {
         return defaultValue;
      }
   }

   /**
    * Parses the given group/artifact/version identifier.  The GAV should be in the format
    * <pre>
    *    groupId:artifactId:version
    * </pre>
    *
    * @param gav the GAV to parse
    * @return an array that contains the parsed group ID, artifact ID, and version in that order
    * @throws IllegalArgumentException if {@code gav} is {@code null}, or does not match the format above
    */
   public static String[] parseGav(String gav) {
      if (gav == null || gav.trim().isEmpty()) {
         throw new IllegalArgumentException("GAV may not be null or empty!");
      }
      Matcher matcher = GAV_REGEX.matcher(gav);
      if (!matcher.matches()) {
         throw new IllegalArgumentException("GAV string must be of the format group:artifact:version, got "
                                            + gav);
      }
      return new String[]{
            matcher.group(1),
            matcher.group(2),
            matcher.group(3)
      };
   }
}
