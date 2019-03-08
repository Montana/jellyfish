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
package com.ngc.seaside.systemdescriptor.model.api.model.properties;

import com.google.common.base.Preconditions;

import com.ngc.seaside.systemdescriptor.model.api.FieldCardinality;
import com.ngc.seaside.systemdescriptor.model.api.INamedChild;
import com.ngc.seaside.systemdescriptor.model.api.data.DataTypes;
import com.ngc.seaside.systemdescriptor.model.api.data.IData;
import com.ngc.seaside.systemdescriptor.model.api.data.IEnumeration;

/**
 * A property is a combination of a <i>declaration</i> and an optional value. Properties can be declared on a variety of
 * elements, including models, fields, and links. Properties be declared to be of any of the defined {@link DataTypes
 * primitive data types}. Properties may also use a custom user defined data type or enumeration. Values of properties
 * can be investigated to determine the type of the property.
 */
public interface IProperty extends INamedChild<IProperties> {

   /**
    * Gets the type of this property. If the type is {@link DataTypes#DATA}, {@link #getData()} or {@link #getDatas()}
    * can be used obtain the values of the property. If the type is {@link DataTypes#ENUM}, {@link #getEnumerations()}
    * can be used obtain the values of the property. Finally, {@link #getPrimitives()} can be used for all other cases.
    *
    * @return the type of this property
    */
   DataTypes getType();

   /**
    * Gets the data type this property is referencing.
    *
    * @return the data type this property is referencing
    * @throws IllegalStateException if the {@link #getType() type} is not {@link DataTypes#DATA}
    */
   IData getReferencedDataType();

   /**
    * Gets the enumeration type this property is referencing.
    *
    * @return the enumeration type this property is referencing
    * @throws IllegalStateException if the {@link #getType() type} is not {@link DataTypes#ENUM}
    */
   IEnumeration getReferencedEnumeration();

   /**
    * Gets the cardinality of this property. This indicates if the property has at most a single value or many values.
    *
    * @return the cardinality of this property
    */
   FieldCardinality getCardinality();

   /**
    * Gets the value of this property.
    *
    * @return the value of this property
    * @throws IllegalStateException if the cardinality is not {@link FieldCardinality#SINGLE}
    */
   default IPropertyValue getValue() {
      Preconditions.checkState(getCardinality() == FieldCardinality.SINGLE,
                               "can only invoke getValue() on a property with a cardinality of single!");
      switch (getType()) {
         case DATA:
            return getData();
         case ENUM:
            return getEnumeration();
         default:
            return getPrimitive();
      }
   }

   /**
    * Gets the data value of this property.
    *
    * @return the data value of this property
    * @throws IllegalStateException if the type of this property is not {@link DataTypes#DATA} or the cardinality is not
    *                               {@link FieldCardinality#SINGLE}
    */
   IPropertyDataValue getData();

   /**
    * Gets the enumeration value of this property.
    *
    * @return the enumeration value of this property
    * @throws IllegalStateException if the type of this property is not {@link DataTypes#ENUM} or the cardinality is not
    *                               {@link FieldCardinality#SINGLE}
    */
   IPropertyEnumerationValue getEnumeration();

   /**
    * Gets the primitive value of this property.
    *
    * @return the primitive value of this property
    * @throws IllegalStateException if the type of this property is not a primitive or the cardinality is not {@link
    *                               FieldCardinality#SINGLE}
    */
   IPropertyPrimitiveValue getPrimitive();

   /**
    * Gets the values of this property.
    *
    * @return the value of this property
    * @throws IllegalStateException if the cardinality is not {@link FieldCardinality#MANY}
    */
   default IPropertyValues<? extends IPropertyValue> getValues() {
      Preconditions.checkState(getCardinality() == FieldCardinality.MANY,
                               "can only invoke getValues() on a property with a cardinality of many!");
      switch (getType()) {
         case DATA:
            return getDatas();
         case ENUM:
            return getEnumerations();
         default:
            return getPrimitives();
      }
   }

   /**
    * Gets the data values of this property.
    *
    * @return the data value of this property
    * @throws IllegalStateException if the type of this property is not {@link DataTypes#DATA} or the cardinality is not
    *                               {@link FieldCardinality#MANY}
    */
   IPropertyValues<IPropertyDataValue> getDatas();

   /**
    * Gets the enumeration values of this property.  Note the collection of values may not be set.
    *
    * @return the enumeration value of this property
    * @throws IllegalStateException if the type of this property is not {@link DataTypes#ENUM} or the cardinality is not
    *                               {@link FieldCardinality#MANY}
    */
   IPropertyValues<IPropertyEnumerationValue> getEnumerations();

   /**
    * Gets the data values of this property.
    *
    * @return the data value of this property
    * @throws IllegalStateException if the type of this property is not a primitive type or the cardinality is not
    *                               {@link FieldCardinality#MANY}
    */
   IPropertyValues<IPropertyPrimitiveValue> getPrimitives();

}
