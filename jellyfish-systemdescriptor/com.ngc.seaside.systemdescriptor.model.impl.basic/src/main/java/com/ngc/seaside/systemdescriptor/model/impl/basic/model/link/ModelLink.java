/**
 * UNCLASSIFIED
 * Northrop Grumman Proprietary
 * ____________________________
 *
 * Copyright (C) 2019, Northrop Grumman Systems Corporation
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
package com.ngc.seaside.systemdescriptor.model.impl.basic.model.link;

import com.ngc.seaside.systemdescriptor.model.api.metadata.IMetadata;
import com.ngc.seaside.systemdescriptor.model.api.model.IModel;
import com.ngc.seaside.systemdescriptor.model.api.model.IModelReferenceField;
import com.ngc.seaside.systemdescriptor.model.api.model.IReferenceField;
import com.ngc.seaside.systemdescriptor.model.api.model.link.IModelLink;
import com.ngc.seaside.systemdescriptor.model.api.model.properties.IProperties;
import com.ngc.seaside.systemdescriptor.model.impl.basic.model.properties.Properties;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * Implements the IModelLink interface.  Maintains the source of the link and the target of the link.
 *
 * @param <T> IReferenceField type
 */
public class ModelLink<T extends IReferenceField> implements IModelLink<T> {

   private T source;
   private T target;
   private final IModel parent;
   private String name;
   private IProperties properties;
   private IModelLink<T> refinedLink;
   private IMetadata metadata;

   /**
    * Creates a new link.
    */
   public ModelLink(IModel p) {
      parent = p;
      this.properties = new Properties();
   }

   @Override
   public IMetadata getMetadata() {
      return metadata;
   }

   @Override
   public IModelLink<T> setMetadata(IMetadata metadata) {
      this.metadata = metadata;
      return this;
   }

   @Override
   public T getSource() {
      return source;
   }

   @Override
   public IModelLink<T> setSource(T source) {
      this.source = source;
      return this;
   }

   @Override
   public void traverseLinkSourceExpression(Consumer<IModelReferenceField> linkVisitor) {
      // These methods aren't supported for the basic impl.
      throw new UnsupportedOperationException("not implemented");
   }

   @Override
   public T getTarget() {
      return target;
   }

   @Override
   public IModelLink<T> setTarget(T target) {
      this.target = target;
      return this;
   }

   @Override
   public void traverseLinkTargetExpression(Consumer<IModelReferenceField> linkVisitor) {
      // These methods aren't supported for the basic impl.
      throw new UnsupportedOperationException("not implemented");
   }

   @Override
   public Optional<String> getName() {
      return Optional.ofNullable(name);
   }

   @Override
   public IModelLink<T> setName(String name) {
      this.name = name;
      return this;
   }

   @Override
   public Optional<IModelLink<T>> getRefinedLink() {
      return Optional.ofNullable(refinedLink);
   }

   @Override
   public IModelLink<T> setRefinedLink(IModelLink<T> refinedLink) {
      this.refinedLink = refinedLink;
      return this;
   }

   @Override
   public IModel getParent() {
      return parent;
   }

   @Override
   public IProperties getProperties() {
      return properties;
   }

   @Override
   public IModelLink<T> setProperties(IProperties properties) {
      this.properties = properties;
      return this;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      }
      if (!(o instanceof ModelLink)) {
         return false;
      }
      ModelLink<?> modelLink = (ModelLink<?>) o;
      return source == modelLink.source
             && target == modelLink.target
             && parent == modelLink.parent
             && Objects.equals(name, modelLink.name)
             && Objects.equals(properties, modelLink.properties)
             && refinedLink == modelLink.refinedLink
             && Objects.equals(metadata, modelLink.metadata);
   }

   @Override
   public int hashCode() {
      return Objects.hash(System.identityHashCode(source),
                          System.identityHashCode(target),
                          System.identityHashCode(parent),
                          name,
                          properties,
                          System.identityHashCode(refinedLink),
                          metadata);
   }

   @Override
   public String toString() {
      return "ModelLink{"
             + "source=" + source
             + ", target=" + target
             + ", parent=" + parent
             + ", name='" + name + '\''
             + ", properties=" + properties
             + ", refinedLink=" + refinedLink
             + '}';
   }
}
