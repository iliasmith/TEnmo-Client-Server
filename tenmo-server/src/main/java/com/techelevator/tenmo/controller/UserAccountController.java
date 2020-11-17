package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.AccountDAO;
import com.techelevator.tenmo.dao.TransferDAO;
import com.techelevator.tenmo.dao.UserDAO;
import com.techelevator.tenmo.exceptions.AccountNotFound;
import com.techelevator.tenmo.exceptions.OverdrawnException;
import com.techelevator.tenmo.model.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@PreAuthorize("isAuthenticated()")
@RestController
public class UserAccountController {
    private final UserDAO userDAO;
    private final AccountDAO accountDAO;
    private final TransferDAO transferDAO;


    public UserAccountController(UserDAO userDAO, AccountDAO accountDAO, TransferDAO transferDAO) {
        this.userDAO = userDAO;
        this.accountDAO = accountDAO;
        this.transferDAO = transferDAO;
    }

    @GetMapping("/accounts")
    public BigDecimal getAccountBalance(Principal principal) throws AccountNotFound {
        User currentUser = userDAO.findByUsername(principal.getName());
        return accountDAO.getAccount(currentUser.getId()).getBalance();
    }

    @GetMapping("/users")
    public Map<Long, String> listUsers() {
        Map<Long, String> results = new HashMap<>();
        List<User> info = userDAO.findAll();
        for (User u : info) {
            results.put(u.getId(), u.getUsername());
        }
        return results;
    }


    @PostMapping("/transfers")
    public void sendBucks(Principal principal, @RequestBody @Valid TransferDTO transferDTO) throws AccountNotFound, OverdrawnException {
        if (transferDTO.getPayerUsername().equals(principal.getName())) {
            Long payerId = userDAO.findIdByUsername(principal.getName());
            transferDAO.createSendTransfer(payerId, transferDTO.getPayeeId(), transferDTO.getAmount(), principal);
        } else {
            throw new AccountNotFound("PayerId must match Id of authenticated user.");
        }
    }

    @GetMapping("/transfers")
    public List<TransferHistoryDTO> getTransferHistory(Principal principal) throws AccountNotFound {
        return transferDAO.getAllTransfers(principal);
    }

    @GetMapping("/transfers/{id}")
    public TransferDetailsDTO getTransferDetails(@PathVariable long id, Principal principal) {
        return transferDAO.getTransferById(id, principal);
    }
}




