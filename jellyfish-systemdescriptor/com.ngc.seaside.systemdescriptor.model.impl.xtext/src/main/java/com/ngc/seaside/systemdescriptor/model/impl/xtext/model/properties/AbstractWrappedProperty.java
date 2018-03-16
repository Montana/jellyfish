package com.ngc.seaside.systemdescriptor.model.impl.xtext.model.properties;

import com.google.common.base.Preconditions;

import com.ngc.seaside.systemdescriptor.model.api.FieldCardinality;
import com.ngc.seaside.systemdescriptor.model.api.data.IData;
import com.ngc.seaside.systemdescriptor.model.api.data.IEnumeration;
import com.ngc.seaside.systemdescriptor.model.api.model.properties.IProperties;
import com.ngc.seaside.systemdescriptor.model.api.model.properties.IProperty;
import com.ngc.seaside.systemdescriptor.model.api.model.properties.IPropertyDataValue;
import com.ngc.seaside.systemdescriptor.model.api.model.properties.IPropertyEnumerationValue;
import com.ngc.seaside.systemdescriptor.model.api.model.properties.IPropertyPrimitiveValue;
import com.ngc.seaside.systemdescriptor.model.api.model.properties.IPropertyValues;
import com.ngc.seaside.systemdescriptor.model.impl.xtext.AbstractWrappedXtext;
import com.ngc.seaside.systemdescriptor.model.impl.xtext.exception.UnrecognizedXtextTypeException;
import com.ngc.seaside.systemdescriptor.model.impl.xtext.store.IWrapperResolver;
import com.ngc.seaside.systemdescriptor.model.impl.xtext.util.ConversionUtil;
import com.ngc.seaside.systemdescriptor.systemDescriptor.DataModel;
import com.ngc.seaside.systemdescriptor.systemDescriptor.PrimitivePropertyFieldDeclaration;
import com.ngc.seaside.systemdescriptor.systemDescriptor.Properties;
import com.ngc.seaside.systemdescriptor.systemDescriptor.PropertyFieldDeclaration;
import com.ngc.seaside.systemdescriptor.systemDescriptor.ReferencedPropertyFieldDeclaration;
import com.ngc.seaside.systemdescriptor.systemDescriptor.SystemDescriptorPackage;

public abstract class AbstractWrappedProperty<T extends PropertyFieldDeclaration>
      extends AbstractWrappedXtext<T>
      implements IProperty {

   public AbstractWrappedProperty(IWrapperResolver resolver, T wrapped) {
      super(resolver, wrapped);
   }

   @Override
   public String getName() {
      return wrapped.getName();
   }

   @Override
   public IProperties getParent() {
      return resolver.getWrapperFor((Properties) wrapped.eContainer());
   }

   @Override
   public FieldCardinality getCardinality() {
      return ConversionUtil.convertCardinalityFromXtext(wrapped.getCardinality());
   }

   @Override
   public IData getReferencedDataType() {
      throw new IllegalStateException("property is not a data type");
   }

   @Override
   public IEnumeration getReferencedEnumeration() {
      throw new IllegalStateException("property is not an enumeration type");
   }


   @Override
   public IPropertyDataValue getData() {
      throw new IllegalStateException("property is not a data type");
   }

   @Override
   public IPropertyEnumerationValue getEnumeration() {
      throw new IllegalStateException("property is not an enumeration type");
   }

   @Override
   public IPropertyPrimitiveValue getPrimitive() {
      throw new IllegalStateException("property is not a primitive type");
   }

   @Override
   public IPropertyValues<IPropertyDataValue> getDatas() {
      throw new IllegalStateException("property is not a data type");
   }

   @Override
   public IPropertyValues<IPropertyEnumerationValue> getEnumerations() {
      throw new IllegalStateException("property is not an enumeration type");
   }

   @Override
   public IPropertyValues<IPropertyPrimitiveValue> getPrimitives() {
      throw new IllegalStateException("property is not a primitive type");
   }

   public static AbstractWrappedProperty<? extends PropertyFieldDeclaration> getWrappedPropertiesFieldReference(
         IWrapperResolver resolver,
         PropertyFieldDeclaration property) {
      Preconditions.checkNotNull(resolver, "resolver must not be null!");
      Preconditions.checkNotNull(property, "property must not be null!");

      AbstractWrappedProperty<? extends PropertyFieldDeclaration> wrapped;
      switch (property.eClass().getClassifierID()) {
         case SystemDescriptorPackage.PRIMITIVE_PROPERTY_FIELD_DECLARATION:
            wrapped = wrapPrimitiveProperty(resolver, (PrimitivePropertyFieldDeclaration) property);
            break;
         case SystemDescriptorPackage.REFERENCED_PROPERTY_FIELD_DECLARATION:
            wrapped = wrapReferencedProperty(resolver, (ReferencedPropertyFieldDeclaration) property);
            break;
         default:
            throw new UnrecognizedXtextTypeException(property);
      }

      return wrapped;
   }

   public static PropertyFieldDeclaration toXTextPropertyFieldDeclaration(IWrapperResolver resolver,
                                                                          IProperty property) {
      Preconditions.checkNotNull(resolver, "resolver must not be null!");
      Preconditions.checkNotNull(property, "property must not be null!");
      switch (property.getType()) {
         case DATA:
            return WrappedDataProperty.toXtextReferencedPropertyFieldDeclaration(resolver, property);
         case ENUM:
            return WrappedEnumerationProperty.toXtextReferencedPropertyFieldDeclaration(resolver, property);
         default:
            return WrappedPrimitiveProperty.toXtextPrimitivePropertyFieldDeclaration(resolver, property);
      }
   }

   private static AbstractWrappedProperty<? extends PropertyFieldDeclaration> wrapPrimitiveProperty(
         IWrapperResolver resolver,
         PrimitivePropertyFieldDeclaration property) {
      return new WrappedPrimitiveProperty(resolver, property);
   }

   private static AbstractWrappedProperty<? extends PropertyFieldDeclaration>  wrapReferencedProperty(
         IWrapperResolver resolver,
         ReferencedPropertyFieldDeclaration property) {
      AbstractWrappedProperty<? extends PropertyFieldDeclaration> wrapped;
      DataModel data = property.getDataModel();
      switch (data.eClass().getClassifierID()) {
         case SystemDescriptorPackage.DATA:
            wrapped = new WrappedDataProperty(resolver, property);
            break;
         case SystemDescriptorPackage.ENUMERATION:
            wrapped = new WrappedEnumerationProperty(resolver, property);
            break;
         default:
            throw new UnrecognizedXtextTypeException(data);
      }
      return wrapped;
   }
}
