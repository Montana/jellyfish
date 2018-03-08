package com.ngc.seaside.systemdescriptor.model.impl.basic.model.properties;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Objects;

import com.google.common.base.Preconditions;
import com.ngc.seaside.systemdescriptor.model.api.data.DataTypes;
import com.ngc.seaside.systemdescriptor.model.api.model.properties.IPropertyPrimitiveValue;

public class PropertyPrimitiveValue extends PropertyValue implements IPropertyPrimitiveValue {

   private final Object value;
   
   /**
    * Constructs an unset PropertyPrimitiveValue of the given type.
    * @param type the value's type
    */
   public PropertyPrimitiveValue(DataTypes type) {
      super(type, false);
      this.value = null;
   }
   
   public PropertyPrimitiveValue(BigInteger value) {
      super(DataTypes.INT, true);
      Preconditions.checkNotNull(value, "value may not be null!");
      this.value = value;
   }
   
   public PropertyPrimitiveValue(BigDecimal value) {
      super(DataTypes.FLOAT, true);
      Preconditions.checkNotNull(value, "value may not be null!");
      this.value = value;
   }
   
   public PropertyPrimitiveValue(String value) {
      super(DataTypes.STRING, true);
      Preconditions.checkNotNull(value, "value may not be null!");
      this.value = value;
   }
   
   public PropertyPrimitiveValue(boolean value) {
      super(DataTypes.BOOLEAN, true);
      Preconditions.checkNotNull(value, "value may not be null!");
      this.value = value;
   }
   
   @Override
   public BigInteger getInteger() {
      checkType(DataTypes.INT);
      return (BigInteger) value;
   }

   @Override
   public BigDecimal getDecimal() {
      checkType(DataTypes.FLOAT);
      return (BigDecimal) value;
   }

   @Override
   public boolean getBoolean() {
      checkType(DataTypes.BOOLEAN);
      return (Boolean) value;
   }

   @Override
   public String getString() {
      checkType(DataTypes.STRING);
      return (String) value;
   }
   
   private void checkType(DataTypes expectedType) {
      super.checkIsSet();
      if (getType() != expectedType) {
         throw new IllegalStateException("Cannot get a value of type " + expectedType + ": actual type is " + getType());
      }
   }
   
   @Override
   public int hashCode() {
      return Objects.hash(value);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      }
      if (!(obj instanceof PropertyDataValue)) {
         return false;
      }
      PropertyPrimitiveValue that = (PropertyPrimitiveValue) obj;
      return Objects.equals(value, that.value);
   }

   @Override
   public String toString() {
      return "PropertyPrimitiveValue[type=" + getType() + ", value=" + value + "]";
   }
   
}
