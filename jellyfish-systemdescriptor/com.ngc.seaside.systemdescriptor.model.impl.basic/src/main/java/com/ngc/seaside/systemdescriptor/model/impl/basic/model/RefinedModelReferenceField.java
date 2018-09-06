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
package com.ngc.seaside.systemdescriptor.model.impl.basic.model;

import com.google.common.base.Preconditions;

import com.ngc.seaside.systemdescriptor.model.api.metadata.IMetadata;
import com.ngc.seaside.systemdescriptor.model.api.model.IModel;
import com.ngc.seaside.systemdescriptor.model.api.model.IModelReferenceField;
import com.ngc.seaside.systemdescriptor.model.api.model.IReferenceField;
import com.ngc.seaside.systemdescriptor.model.api.model.properties.IProperties;

import java.util.Objects;
import java.util.Optional;

/**
 * Implements the IModelReferenceField interface for a refined reference.
 */
public class RefinedModelReferenceField implements IModelReferenceField {

   protected final String name;
   protected IMetadata metadata;
   protected IModelReferenceField refinedField;
   protected IModel parent;
   protected IProperties properties;

   /**
    * Creates a new field.
    */
   public RefinedModelReferenceField(String name, IModelReferenceField refinedField) {
      Preconditions.checkNotNull(name, "name may not be null!");
      Preconditions.checkArgument(!name.trim().isEmpty(), "name may not be empty!");
      Preconditions.checkNotNull(refinedField, "refined field may not be empty!");
      this.name = name;
      this.refinedField = refinedField;
   }

   @Override
   public IMetadata getMetadata() {
      return metadata;
   }

   @Override
   public IReferenceField setMetadata(IMetadata metadata) {
      this.metadata = metadata;
      return this;
   }

   @Override
   public IProperties getProperties() {
      return properties;
   }

   @Override
   public IReferenceField setProperties(IProperties properties) {
      this.properties = properties;
      return this;
   }

   @Override
   public String getName() {
      return name;
   }

   @Override
   public IModel getParent() {
      return parent;
   }

   @Override
   public IModel getType() {
      return refinedField.getType();
   }

   @Override
   public IModelReferenceField setType(IModel model) {
      throw new UnsupportedOperationException("The type of refined field cannot be changed");
   }

   public void setParent(Model model) {
      parent = model;

   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      }
      if (!(o instanceof RefinedModelReferenceField)) {
         return false;
      }
      RefinedModelReferenceField that = (RefinedModelReferenceField) o;
      return Objects.equals(name, that.name)
             && Objects.equals(metadata, that.metadata)
             && Objects.equals(refinedField, that.refinedField)
             && parent == that.parent;
   }

   @Override
   public int hashCode() {
      return Objects.hash(name, metadata, System.identityHashCode(parent));
   }

   @Override
   public String toString() {
      return "RefinedModelReferenceField["
             + "name='" + name + '\''
             + ", metadata=" + metadata
             + ", parent=" + (parent == null ? "null" : parent.getName())
             + ']';
   }

   @Override
   public Optional<IModelReferenceField> getRefinedField() {
      return Optional.of(refinedField);
   }
}
