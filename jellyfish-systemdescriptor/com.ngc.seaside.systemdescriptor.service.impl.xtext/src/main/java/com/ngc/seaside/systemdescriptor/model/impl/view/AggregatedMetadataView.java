package com.ngc.seaside.systemdescriptor.model.impl.view;

import com.google.common.base.Preconditions;

import com.ngc.seaside.systemdescriptor.model.api.data.IData;
import com.ngc.seaside.systemdescriptor.model.api.metadata.IMetadata;
import com.ngc.seaside.systemdescriptor.model.api.model.IModel;
import com.ngc.seaside.systemdescriptor.model.api.model.link.IModelLink;
import com.ngc.seaside.systemdescriptor.model.impl.basic.metadata.Metadata;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;
import javax.json.spi.JsonProvider;

/**
 * Serves as a factory for aggregating metadata for data and model objects.
 */
public class AggregatedMetadataView {

   private AggregatedMetadataView() {
   }

   /**
    * Gets an instance of {@code IMetadata} that contains all the metadata from the given object and its extended
    * types.
    */
   public static IMetadata getAggregatedMetadata(IData wrapped) {
      return getAggregatedMetadata(wrapped, IData::getExtendedDataType, IData::getMetadata);
   }

   /**
    * Gets an instance of {@code IMetadata} that contains all the metadata from the given model and its refined models.
    */
   public static IMetadata getAggregatedMetadata(IModel wrapped) {
      return getAggregatedMetadata(wrapped, IModel::getRefinedModel, IModel::getMetadata);
   }

   /**
    * Gets an instance of {@code IMetadata} that contains all the metadata from the given link and its refined links.
    */
   public static IMetadata getAggregatedMetadata(IModelLink<?> wrapped) {
      return getAggregatedMetadata(wrapped, IModelLink::getRefinedLink, IModelLink::getMetadata);
   }

   private static <T> IMetadata getAggregatedMetadata(T wrapped,
                                                      Function<T, Optional<T>> parentFunction,
                                                      Function<T, IMetadata> metadataFunction) {
      Preconditions.checkNotNull(wrapped, "wrapped may not be null!");
      // Contains the names of the keys that have already been aggregated.
      Set<String> keysAggregated = new HashSet<>();
      JsonObjectBuilder aggregatedJson = JsonProvider.provider().createObjectBuilder();
      T next = wrapped;

      while (next != null) {
         JsonObject json = metadataFunction.apply(next).getJson();
         for (Map.Entry<String, JsonValue> entry : json.entrySet()) {
            // Do not overwrite a value already aggregated.  This allows values closer to the root object to take
            // precedence over values inherited from extended types.
            if (keysAggregated.add(entry.getKey())) {
               aggregatedJson.add(entry.getKey(), entry.getValue());
            }
         }

         next = parentFunction.apply(next).orElse(null);
      }

      return new Metadata().setJson(aggregatedJson.build());
   }
}
