package com.mitra.service;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class RazorpayService {

    private static final Logger logger = LoggerFactory.getLogger(RazorpayService.class);

    @Value("${razorpay.key.id:rzp_test_YOUR_KEY_ID}")
    private String keyId;

    @Value("${razorpay.key.secret:YOUR_KEY_SECRET}")
    private String keySecret;

    private RazorpayClient razorpayClient;

    public RazorpayService(@Value("${razorpay.key.id:rzp_test_YOUR_KEY_ID}") String keyId,
                          @Value("${razorpay.key.secret:YOUR_KEY_SECRET}") String keySecret) {
        this.keyId = keyId;
        this.keySecret = keySecret;
        try {
            this.razorpayClient = new RazorpayClient(keyId, keySecret);
        } catch (RazorpayException e) {
            logger.error("Error initializing Razorpay client", e);
            throw new RuntimeException(e);
        }
    }

    public Order createOrder(Double amount, String currency, String receipt) throws RazorpayException {
        JSONObject orderRequest = new JSONObject();
        orderRequest.put("amount", amount * 100); // Amount in paise
        orderRequest.put("currency", currency);
        orderRequest.put("receipt", receipt);

        return razorpayClient.orders.create(orderRequest);
    }

    public boolean verifyPayment(String orderId, String paymentId, String signature) {
        try {
            JSONObject options = new JSONObject();
            options.put("razorpay_order_id", orderId);
            options.put("razorpay_payment_id", paymentId);
            options.put("razorpay_signature", signature);

            return Utils.verifyPaymentSignature(options, keySecret);
        } catch (RazorpayException e) {
            logger.error("Error verifying payment signature", e);
            return false;
        }
    }
}