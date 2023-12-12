package org.microg.gms.people.sync;

import io.grpc.CallOptions;
import io.grpc.Channel;
import io.grpc.ClientCall;
import io.grpc.ClientInterceptor;
import io.grpc.ForwardingClientCall;
import io.grpc.Metadata;
import io.grpc.MethodDescriptor;

public class HeaderClientInterceptor implements ClientInterceptor {

    private static final Metadata.Key<String> AUTHORIZATION_METADATA_KEY = Metadata.Key.of("Authorization", Metadata.ASCII_STRING_MARSHALLER);
//    private static final Metadata.Key<String> SPATULA_METADATA_KEY = Metadata.Key.of("X-Goog-Spatula", Metadata.ASCII_STRING_MARSHALLER);
    private String token;
    private String Spatula;

    public HeaderClientInterceptor(String token, String spatula) {
        this.token = token;
        this.Spatula = spatula;
    }

    @Override
    public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(
            MethodDescriptor<ReqT, RespT> method, CallOptions callOptions, Channel next) {
        return new ForwardingClientCall.SimpleForwardingClientCall<ReqT, RespT>(next.newCall(method, callOptions)) {
            @Override
            public void start(Listener<RespT> responseListener, Metadata headers) {
//                headers.put(SPATULA_METADATA_KEY,Spatula);
                headers.put(AUTHORIZATION_METADATA_KEY, token);
                super.start(responseListener, headers);
            }
        };
    }
}
