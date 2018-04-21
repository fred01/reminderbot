package srv.when;

import static io.grpc.MethodDescriptor.generateFullMethodName;
import static io.grpc.stub.ClientCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ClientCalls.asyncClientStreamingCall;
import static io.grpc.stub.ClientCalls.asyncServerStreamingCall;
import static io.grpc.stub.ClientCalls.asyncUnaryCall;
import static io.grpc.stub.ClientCalls.blockingServerStreamingCall;
import static io.grpc.stub.ClientCalls.blockingUnaryCall;
import static io.grpc.stub.ClientCalls.futureUnaryCall;
import static io.grpc.stub.ServerCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ServerCalls.asyncClientStreamingCall;
import static io.grpc.stub.ServerCalls.asyncServerStreamingCall;
import static io.grpc.stub.ServerCalls.asyncUnaryCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedStreamingCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall;

/**
 * <pre>
 * The greeting service definition.
 * </pre>
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.11.0)",
    comments = "Source: when_srv.proto")
public final class WhenGrpc {

  private WhenGrpc() {}

  public static final String SERVICE_NAME = "main.When";

  // Static method descriptors that strictly reflect the proto.
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  @java.lang.Deprecated // Use {@link #getParseMethod()} instead. 
  public static final io.grpc.MethodDescriptor<srv.when.WhenRequest,
      srv.when.WhenResponse> METHOD_PARSE = getParseMethodHelper();

  private static volatile io.grpc.MethodDescriptor<srv.when.WhenRequest,
      srv.when.WhenResponse> getParseMethod;

  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static io.grpc.MethodDescriptor<srv.when.WhenRequest,
      srv.when.WhenResponse> getParseMethod() {
    return getParseMethodHelper();
  }

  private static io.grpc.MethodDescriptor<srv.when.WhenRequest,
      srv.when.WhenResponse> getParseMethodHelper() {
    io.grpc.MethodDescriptor<srv.when.WhenRequest, srv.when.WhenResponse> getParseMethod;
    if ((getParseMethod = WhenGrpc.getParseMethod) == null) {
      synchronized (WhenGrpc.class) {
        if ((getParseMethod = WhenGrpc.getParseMethod) == null) {
          WhenGrpc.getParseMethod = getParseMethod = 
              io.grpc.MethodDescriptor.<srv.when.WhenRequest, srv.when.WhenResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "main.When", "Parse"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  srv.when.WhenRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  srv.when.WhenResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new WhenMethodDescriptorSupplier("Parse"))
                  .build();
          }
        }
     }
     return getParseMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static WhenStub newStub(io.grpc.Channel channel) {
    return new WhenStub(channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static WhenBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    return new WhenBlockingStub(channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static WhenFutureStub newFutureStub(
      io.grpc.Channel channel) {
    return new WhenFutureStub(channel);
  }

  /**
   * <pre>
   * The greeting service definition.
   * </pre>
   */
  public static abstract class WhenImplBase implements io.grpc.BindableService {

    /**
     * <pre>
     * Sends a greeting
     * </pre>
     */
    public void parse(srv.when.WhenRequest request,
        io.grpc.stub.StreamObserver<srv.when.WhenResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getParseMethodHelper(), responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getParseMethodHelper(),
            asyncUnaryCall(
              new MethodHandlers<
                srv.when.WhenRequest,
                srv.when.WhenResponse>(
                  this, METHODID_PARSE)))
          .build();
    }
  }

  /**
   * <pre>
   * The greeting service definition.
   * </pre>
   */
  public static final class WhenStub extends io.grpc.stub.AbstractStub<WhenStub> {
    private WhenStub(io.grpc.Channel channel) {
      super(channel);
    }

    private WhenStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected WhenStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new WhenStub(channel, callOptions);
    }

    /**
     * <pre>
     * Sends a greeting
     * </pre>
     */
    public void parse(srv.when.WhenRequest request,
        io.grpc.stub.StreamObserver<srv.when.WhenResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getParseMethodHelper(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   * <pre>
   * The greeting service definition.
   * </pre>
   */
  public static final class WhenBlockingStub extends io.grpc.stub.AbstractStub<WhenBlockingStub> {
    private WhenBlockingStub(io.grpc.Channel channel) {
      super(channel);
    }

    private WhenBlockingStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected WhenBlockingStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new WhenBlockingStub(channel, callOptions);
    }

    /**
     * <pre>
     * Sends a greeting
     * </pre>
     */
    public srv.when.WhenResponse parse(srv.when.WhenRequest request) {
      return blockingUnaryCall(
          getChannel(), getParseMethodHelper(), getCallOptions(), request);
    }
  }

  /**
   * <pre>
   * The greeting service definition.
   * </pre>
   */
  public static final class WhenFutureStub extends io.grpc.stub.AbstractStub<WhenFutureStub> {
    private WhenFutureStub(io.grpc.Channel channel) {
      super(channel);
    }

    private WhenFutureStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected WhenFutureStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new WhenFutureStub(channel, callOptions);
    }

    /**
     * <pre>
     * Sends a greeting
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<srv.when.WhenResponse> parse(
        srv.when.WhenRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getParseMethodHelper(), getCallOptions()), request);
    }
  }

  private static final int METHODID_PARSE = 0;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final WhenImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(WhenImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_PARSE:
          serviceImpl.parse((srv.when.WhenRequest) request,
              (io.grpc.stub.StreamObserver<srv.when.WhenResponse>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }
  }

  private static abstract class WhenBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    WhenBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return srv.when.WhenService.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("When");
    }
  }

  private static final class WhenFileDescriptorSupplier
      extends WhenBaseDescriptorSupplier {
    WhenFileDescriptorSupplier() {}
  }

  private static final class WhenMethodDescriptorSupplier
      extends WhenBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    WhenMethodDescriptorSupplier(String methodName) {
      this.methodName = methodName;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
      return getServiceDescriptor().findMethodByName(methodName);
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (WhenGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new WhenFileDescriptorSupplier())
              .addMethod(getParseMethodHelper())
              .build();
        }
      }
    }
    return result;
  }
}
