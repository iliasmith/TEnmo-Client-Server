package com.techelevator.tenmo.services;

import com.techelevator.tenmo.models.*;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Map;

public class MoneyService {

    private String authToken = "";
    private final String baseUrl; //   localhost:8080/
    private final RestTemplate restTemplate = new RestTemplate();


    public MoneyService(String url) {
        baseUrl = url;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public String viewCurrentBalance() {
        String result = "";
        BigDecimal amount = new BigDecimal(0);
        try {
            amount = restTemplate.exchange(baseUrl + "accounts", HttpMethod.GET, makeAuthEntity(), BigDecimal.class).getBody();
            result = "Your current account balance is: $" + amount;
        } catch (RestClientResponseException | ResourceAccessException e) {
            return "Something went wrong. Check connection to server and making a valid request.";
        }
        return result;
    }

    public String viewTransferHistory() {
        TransferLogItem[] history = null;
        String result = "";
        try {
            history = restTemplate.exchange(baseUrl + "transfers", HttpMethod.GET, makeAuthEntity(), TransferLogItem[].class).getBody();
            result = "-------------------------------------\n" +
                    "Transfers \n" +
                    "ID         From/To            Amount \n" +
                    "-------------------------------------\n";
            for (TransferLogItem item : history) {
                Long id = item.getTransferId();
                BigDecimal amount = item.getAmount();
                String fromTo;
                if (item.getUsernameFrom().equals(item.getUsernameOfCurrentUser())) {
                    fromTo = "To: " + item.getUsernameTo();
                } else {
                    fromTo = "From: " + item.getUsernameFrom();
                }
                result = result + "\n" + id + "         " + fromTo + "          " + "$ " + amount;
            }
            result = result + "\n ---------\n";
        } catch (RestClientResponseException | ResourceAccessException e) {
            return "Something went wrong. Check connection to server and making a valid request.";
        }
        return result;
    }

    public String viewTransferDetails(long id) {
        TransferDetails details = new TransferDetails();
        String result = "";
        try {
            details = restTemplate.exchange(baseUrl + "transfers/" + id, HttpMethod.GET, makeAuthEntity(), TransferDetails.class).getBody();
            long transferId = details.getTransferId();
            String from = details.getSenderName();
            String to = details.getReceiverName();
            String status = null;
            if (details.getTransferStatusId() == 2) {
                status = "Approved";
            }
            String type = null;
            if (details.getTransferTypeId() == 2) {
                type = "Send";
            }
            BigDecimal amount = details.getAmount();

            result = "--------------------------------------------\n" +
                    "Transfer Details\n" +
                    "--------------------------------------------\n" +
                    " Id: " + transferId + "\n" +
                    " From: " + from + "\n" +
                    " To: " + to + "\n" +
                    " Type: " + type + "\n" +
                    " Status: " + status + "\n" +
                    " Amount: " + "$" + amount;
        } catch (ResourceAccessException | RestClientResponseException e) {
            return "Something went wrong. Check connection to server and making a valid request.";
        }
        return result;

    }

    public void sendBucks(Long id, BigDecimal amount, String myUsername) {
        Payment myPayment = new Payment();
        myPayment.setAmount(amount);
        myPayment.setPayeeId(id);
        myPayment.setPayerUsername(myUsername);

        try {
            restTemplate.postForObject(baseUrl + "transfers", makePaymentEntity(myPayment), Payment.class);
        } catch (Exception e) {
            System.out.println("\n**********Transfer was not completed.**********\nPlease make sure you: \n -Have enough money in your account to make the transfer\n-Chose a valid account ID as the recipient");
        }
    }

    public String listUsers() {
        String result = "";
        Map users = restTemplate.exchange(baseUrl + "users", HttpMethod.GET, makeAuthEntity(), Map.class).getBody();
        result = "-------------------------------------------\n" +
                "Users\n" +
                "ID          Name\n" +
                "-------------------------------------------\n";

        for (Object key : users.keySet()) {
            String id = (String) key;
            String name = (String) users.get(key);
            result = result + id + "          " + name + "\n";
        }
        return result + "\n ---------";
    }

    private HttpEntity<Payment> makePaymentEntity(Payment payment) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(authToken);
        return new HttpEntity<>(payment, headers);
    }

    private HttpEntity<?> makeAuthEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authToken);
        return new HttpEntity<>(headers);
    }
}
