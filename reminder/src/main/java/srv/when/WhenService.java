// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: when_srv.proto

package srv.when;

public final class WhenService {
  private WhenService() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistryLite registry) {
  }

  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions(
        (com.google.protobuf.ExtensionRegistryLite) registry);
  }
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_main_WhenRequest_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_main_WhenRequest_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_main_WhenResponse_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_main_WhenResponse_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static  com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n\016when_srv.proto\022\004main\"\033\n\013WhenRequest\022\014\n" +
      "\004name\030\001 \001(\t\"\037\n\014WhenResponse\022\017\n\007message\030\001" +
      " \001(\t28\n\004When\0220\n\005Parse\022\021.main.WhenRequest" +
      "\032\022.main.WhenResponse\"\000B\031\n\010srv.whenB\013When" +
      "ServiceP\001b\006proto3"
    };
    com.google.protobuf.Descriptors.FileDescriptor.InternalDescriptorAssigner assigner =
        new com.google.protobuf.Descriptors.FileDescriptor.    InternalDescriptorAssigner() {
          public com.google.protobuf.ExtensionRegistry assignDescriptors(
              com.google.protobuf.Descriptors.FileDescriptor root) {
            descriptor = root;
            return null;
          }
        };
    com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
        }, assigner);
    internal_static_main_WhenRequest_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_main_WhenRequest_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_main_WhenRequest_descriptor,
        new java.lang.String[] { "Name", });
    internal_static_main_WhenResponse_descriptor =
      getDescriptor().getMessageTypes().get(1);
    internal_static_main_WhenResponse_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_main_WhenResponse_descriptor,
        new java.lang.String[] { "Message", });
  }

  // @@protoc_insertion_point(outer_class_scope)
}