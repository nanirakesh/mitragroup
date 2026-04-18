package com.mitra.controller;

import com.mitra.model.Payment;
import com.mitra.model.ServiceRequest;
import com.mitra.model.User;
import com.mitra.service.PaymentService;
import com.mitra.service.RazorpayService;
import com.mitra.service.ServiceRequestService;
import com.mitra.service.UserService;
import com.razorpay.Order;
import com.razorpay.RazorpayException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/payment")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private ServiceRequestService serviceRequestService;

    @Autowired
    private UserService userService;

    @Autowired
    private RazorpayService razorpayService;

    @Value("${razorpay.key.id}")
    private String razorpayKeyId;

    private Optional<ServiceRequest> getServiceRequestIfValid(Long requestId, Authentication auth) {
        User user = userService.findByEmail(auth.getName()).orElse(null);
        Optional<ServiceRequest> requestOpt = serviceRequestService.findById(requestId);

        if (requestOpt.isEmpty() || !requestOpt.get().getUser().equals(user)) {
            return Optional.empty();
        }
        return requestOpt;
    }

    @GetMapping("/request/{requestId}")
    public String showPaymentPage(@PathVariable Long requestId, Model model, Authentication auth) {
        Optional<ServiceRequest> requestOpt = getServiceRequestIfValid(requestId, auth);
        if (requestOpt.isEmpty()) {
            return "redirect:/user/dashboard";
        }
        ServiceRequest serviceRequest = requestOpt.get();

        // Check if request is completed
        if (serviceRequest.getStatus() != ServiceRequest.Status.COMPLETED) {
            return "redirect:/user/request/" + requestId;
        }

        // Check if already paid
        if (serviceRequest.getPaymentStatus() == ServiceRequest.PaymentStatus.PAID) {
            return "redirect:/user/request/" + requestId;
        }

        // Calculate cost if not set
        Double amount = serviceRequest.getActualCost();
        if (amount == null) {
            amount = paymentService.calculateServiceCost(serviceRequest);
            serviceRequest.setActualCost(amount);
            serviceRequestService.updateRequest(serviceRequest);
        }

        // Create Razorpay order
        try {
            Order order = razorpayService.createOrder(amount, "INR", "receipt_" + requestId);
            model.addAttribute("orderId", order.get("id"));
            model.addAttribute("razorpayKeyId", razorpayKeyId);
        } catch (RazorpayException e) {
            model.addAttribute("error", "Payment gateway error. Please try again.");
        }

        model.addAttribute("serviceRequest", serviceRequest);
        model.addAttribute("amount", amount);
        model.addAttribute("user", userService.findByEmail(auth.getName()).orElse(null));

        return "payment/razorpay-payment";
    }

    @GetMapping("/upi/{requestId}")
    public String showUPIPayment(@PathVariable Long requestId, Model model, Authentication auth) {
        Optional<ServiceRequest> requestOpt = getServiceRequestIfValid(requestId, auth);
        if (requestOpt.isEmpty()) {
            return "redirect:/user/dashboard";
        }
        ServiceRequest serviceRequest = requestOpt.get();
        Double amount = serviceRequest.getActualCost();

        if (amount == null) {
            amount = paymentService.calculateServiceCost(serviceRequest);
        }

        model.addAttribute("serviceRequest", serviceRequest);
        model.addAttribute("amount", amount);

        return "payment/upi-payment";
    }

    @PostMapping("/process/{requestId}")
    public String processPayment(@PathVariable Long requestId,
                               @RequestParam Payment.PaymentMethod paymentMethod,
                               Authentication auth,
                               RedirectAttributes redirectAttributes) {
        try {
            Optional<ServiceRequest> requestOpt = getServiceRequestIfValid(requestId, auth);
            if (requestOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Invalid request");
                return "redirect:/user/dashboard";
            }

            ServiceRequest serviceRequest = requestOpt.get();
            Double amount = serviceRequest.getActualCost();

            if (amount == null) {
                amount = paymentService.calculateServiceCost(serviceRequest);
            }

            // Create payment
            Payment payment = paymentService.createPayment(requestId, amount, paymentMethod);

            // For UPI, show confirmation page first
            if (paymentMethod == Payment.PaymentMethod.UPI) {
                return "redirect:/payment/upi/" + requestId;
            }

            // Process payment for other methods
            Payment processedPayment = paymentService.processPayment(payment.getId());

            if (processedPayment.getStatus() == Payment.PaymentStatus.COMPLETED) {
                redirectAttributes.addFlashAttribute("success",
                    "Payment successful! Transaction ID: " + processedPayment.getTransactionId());
            } else {
                redirectAttributes.addFlashAttribute("error",
                    "Payment failed. Please try again.");
            }

            return "redirect:/user/request/" + requestId;

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Payment processing error: " + e.getMessage());
            return "redirect:/payment/request/" + requestId;
        }
    }

    @GetMapping("/success/{transactionId}")
    public String paymentSuccess(@PathVariable String transactionId, Model model) {
        Optional<Payment> paymentOpt = paymentService.getPaymentByTransactionId(transactionId);

        if (paymentOpt.isPresent()) {
            model.addAttribute("payment", paymentOpt.get());
            return "payment/success";
        }

        return "redirect:/user/dashboard";
    }

    @PostMapping("/verify")
    public String verifyPayment(@RequestParam String razorpay_payment_id,
                              @RequestParam String razorpay_order_id,
                              @RequestParam String razorpay_signature,
                              @RequestParam("request_id") Long requestId,
                              Authentication auth,
                              RedirectAttributes redirectAttributes) {
        try {
            // Verify payment signature
            boolean isValid = razorpayService.verifyPayment(razorpay_order_id, razorpay_payment_id, razorpay_signature);

            if (isValid) {
                // Payment verified - update database
                Optional<ServiceRequest> requestOpt = getServiceRequestIfValid(requestId, auth);
                if (requestOpt.isPresent()) {
                    ServiceRequest serviceRequest = requestOpt.get();

                    // Create payment record
                    Payment payment = paymentService.createPayment(requestId, serviceRequest.getActualCost(), Payment.PaymentMethod.UPI);
                    payment.setStatus(Payment.PaymentStatus.COMPLETED);
                    payment.setTransactionId(razorpay_payment_id);
                    payment.setPaymentGatewayResponse("Razorpay payment successful");

                    // Update service request
                    serviceRequest.setPaymentStatus(ServiceRequest.PaymentStatus.PAID);
                    serviceRequestService.updateRequest(serviceRequest);

                    redirectAttributes.addFlashAttribute("success",
                        "Payment successful! Transaction ID: " + razorpay_payment_id);
                } else {
                    redirectAttributes.addFlashAttribute("error", "Invalid service request");
                }
            } else {
                redirectAttributes.addFlashAttribute("error", "Payment verification failed");
            }

            return "redirect:/user/request/" + requestId;

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Payment verification error: " + e.getMessage());
            return "redirect:/user/dashboard";
        }
    }
}
