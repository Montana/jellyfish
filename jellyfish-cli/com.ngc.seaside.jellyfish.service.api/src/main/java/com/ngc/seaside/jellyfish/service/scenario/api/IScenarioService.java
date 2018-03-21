package com.ngc.seaside.jellyfish.service.scenario.api;

import com.ngc.seaside.jellyfish.api.IJellyFishCommandOptions;
import com.ngc.seaside.systemdescriptor.model.api.model.scenario.IScenario;

import java.util.Collection;
import java.util.Optional;

/**
 * The {@code IScenarioService} is responsible for interpreting {@link IScenario}s and aiding with handling scenarios.
 */
public interface IScenarioService {

   /**
    * Gets the messaging paradigms that are applicable to the given scenario. A single scenario may declare multiple
    * paradigms.
    *
    * @param options  the options the current command is being executed with
    * @param scenario the scenario to get the messaging paradigms for
    * @return the messaging paradigms that are applicable to the given scenario
    */
   Collection<MessagingParadigm> getMessagingParadigms(IJellyFishCommandOptions options, IScenario scenario);

   /**
    * Gets the publish/subscribe messaging flow that the given scenario declares or {@link Optional#empty()} if the
    * scenario does not deal with publishing or subscribing. The flow is either a {@link
    * IPublishSubscribeMessagingFlow.FlowType#PATH path}, a {@link IPublishSubscribeMessagingFlow.FlowType#PATH source},
    * or {@link IPublishSubscribeMessagingFlow.FlowType#PATH sink}.
    *
    * @param options  the options the current command is being executed with
    * @param scenario the scenario to get the messaging flows for
    * @return the publish/subscribe messaging flow that the given scenario declares
    */
   Optional<IPublishSubscribeMessagingFlow> getPubSubMessagingFlow(IJellyFishCommandOptions options,
                                                                   IScenario scenario);

   /**
    * Gets the request/response messaging flow that the given scenario declares or {@link Optional#empty()} if the
    * scenario does not detail with request or response. Each flow is either a {@link
    * IRequestResponseMessagingFlow.FlowType#CLIENT client flow} or a {@link IRequestResponseMessagingFlow.FlowType#SERVER
    * server flow}.
    *
    * @param options  the options the current command is being executed with
    * @param scenario the scenario to get the messaging flows for
    * @return a collection of request/response messaging flows that the given scenario declares
    */
   Optional<IRequestResponseMessagingFlow> getRequestResponseMessagingFlows(IJellyFishCommandOptions options,
                                                                            IScenario scenario);

   /**
    * Gets a collection of timing constraints that have been applied to the given scenario.
    *
    * @param options  the options the current command is being executed with
    * @param scenario the scenario to get the timing constraints for
    * @return a collection of timing constraints that have been applied to the given scenario
    */
   Collection<ITimingConstraint> getTimingConstraints(IJellyFishCommandOptions options, IScenario scenario);

}
