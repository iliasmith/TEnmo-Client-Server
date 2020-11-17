package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.exceptions.AccountNotFound;
import com.techelevator.tenmo.exceptions.OverdrawnException;
import com.techelevator.tenmo.model.Account;

import java.math.BigDecimal;
import java.security.Principal;

public interface AccountDAO {

    void addToBalance(BigDecimal amount, Long userId);

    void subtractFromBalance(BigDecimal amount, Principal principal) throws OverdrawnException;

    Account getAccount(Long userId) throws AccountNotFound;

    Long getAccountIdOfCurrentUser(Principal principal) throws AccountNotFound;

}
