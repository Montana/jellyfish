package ${dto.interface.packageName};

#set ($ignore = $dto.interface.imports.add("com.ngc.seaside.service.fault.api.ServiceFaultException"))
#foreach ($i in $dto.interface.imports)
import ${i};
#end

public interface ${dto.interface.name}#if ($dto.interface.implementedInterface) extends ${dto.interface.implementedInterface.name}#end {

#foreach ($method in $dto.basicPubSubMethods)
   ${method.output.type} ${method.serviceMethod}(${method.input.type} ${method.input.fieldName}) throws ServiceFaultException;

#end
#foreach ($method in $dto.basicSinkMethods)
   void ${method.serviceMethod}(${method.input.type} ${method.input.fieldName}) throws ServiceFaultException;

#end
#foreach ($method in $dto.correlationMethods)
   ${method.output.type} ${method.serviceMethod}(
#foreach ($input in $method.inputs)
      ${input.type} ${input.fieldName},
#end
      ILocalCorrelationEvent<${method.correlationType}> correlationEvent) throws ServiceFaultException;

#end
#foreach ($scenario in $dto.complexScenarios)
   void ${scenario.serviceMethod}(
#foreach ($input in $scenario.inputs)
#set ($lastParam = $velocityCount == $scenario.inputs.size() && $scenario.outputs.isEmpty())
      BlockingQueue<${input.type}> ${input.fieldName}Queue#if ($lastParam));#{else},
#end
#end
#foreach ($output in $scenario.outputs)
#set ($lastParam = $velocityCount == $scenario.outputs.size())
      Consumer<${output.type}> ${output.fieldName}Consumer#if ($lastParam));#{else},
#end
#end


#end
}
