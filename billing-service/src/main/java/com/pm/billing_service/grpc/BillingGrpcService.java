package com.pm.billing_service.grpc;

import billing.BillingResponse;
import billing.BillingServiceGrpc;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@GrpcService
public class BillingGrpcService extends BillingServiceGrpc.BillingServiceImplBase { // BillingServiceImplBase -> protobuf tarafından üretilen target-generated-sources/protobuf/grpc-java/billing içerisind
    private static final Logger log = LoggerFactory.getLogger(BillingGrpcService.class);

    // we already create this file in proto    || search StreamObserver!
    @Override
    public void createBillingAccount(billing.BillingRequest billingRequest,
                                     StreamObserver<billing.BillingResponse> responseObserver) {

        log.info("createBillingAccount request received{}", billingRequest.toString());

        // Business Logic
        BillingResponse response = BillingResponse.newBuilder()
                .setAccountId("1235")
                .setStatus("ACTIVE")
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
