package com.ngc.seaside.jellyfish.service.config.api.dto.zeromq;

/**
 * Defines the different modes that a ZeroMQ connection can use.
 */
public enum ConnectionType {

   /**
    * Indicates that the source of the link binds as a server and the target connects as a client. This means the
    * target (the client) connects to the source (the server).
    */
   SOURCE_BINDS_TARGET_CONNECTS,

   /**
    * Indicates that the source of the link connects as a client and the target binds as a server. This means the
    * source (the client) connects to the target (the server).
    */
   SOURCE_CONNECTS_TARGET_BINDS

}
