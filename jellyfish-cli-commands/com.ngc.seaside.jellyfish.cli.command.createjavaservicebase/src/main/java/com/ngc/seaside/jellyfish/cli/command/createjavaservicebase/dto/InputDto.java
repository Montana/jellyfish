package com.ngc.seaside.jellyfish.cli.command.createjavaservicebase.dto;

public class InputDto {
   private String type;
   private String correlationMethod;
   private String fieldName;
   private String fullyQualifiedName;

   /**
    * Returns the name of the generated java event type for this input.
    */
   public String getType() {
      return type;
   }

   public InputDto setType(String type) {
      this.type = type;
      return this;
   }
   
   public String getFieldName() {
      return fieldName;
   }

   public InputDto setFieldName(String fieldName) {
      this.fieldName = fieldName;
      return this;
   }

   /**
    * Returns the method name that should be called when the correlation involving this input is completed.
    */
   public String getCorrelationMethod() {
      return correlationMethod;
   }

   public InputDto setCorrelationMethod(String correlationMethod) {
      this.correlationMethod = correlationMethod;
      return this;
   }

   public InputDto setFullyQualifiedName(String fullyQualifiedName) {
      this.fullyQualifiedName = fullyQualifiedName;
      return this;
   }

   public  String getFullyQualifiedName() {
      return fullyQualifiedName;
   }
}
