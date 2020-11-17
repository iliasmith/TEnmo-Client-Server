package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.exceptions.AccountNotFound;
import com.techelevator.tenmo.exceptions.OverdrawnException;
import com.techelevator.tenmo.model.Account;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.security.Principal;

@Component
public class JdbcAccountDAO implements AccountDAO {

    private JdbcTemplate jdbcTemplate;
    private UserDAO userDAO;

    public JdbcAccountDAO(JdbcTemplate jdbcTemplate, UserDAO userDAO) {
        this.jdbcTemplate = jdbcTemplate;
        this.userDAO = userDAO;
    }


    @Override
    public void addToBalance(BigDecimal amount, Long userId) {
        String sql = "UPDATE accounts SET balance = (SELECT balance FROM accounts WHERE user_id = ?) + ? WHERE user_id = ?;";
        jdbcTemplate.update(sql, userId, amount, userId);
    }

    @Override
    public void subtractFromBalance(BigDecimal amount, Principal principal) throws OverdrawnException {
        //check current balance as it compares to amount to be withdrawn
        Long userId = new JdbcUserDAO(jdbcTemplate).findIdOfCurrentUser(principal);
        String sql1 = "SELECT balance FROM accounts WHERE user_id = ?;";
        BigDecimal currentBalance = jdbcTemplate.queryForObject(sql1, BigDecimal.class, userId);
        if (currentBalance.compareTo(amount) < 0) {
            throw new OverdrawnException("Amount is greater than current account balance.");
        }
        String sql = "UPDATE accounts SET balance = (? - ?) WHERE user_id = ?;";
        jdbcTemplate.update(sql, currentBalance, amount, userId);

    }

    @Override
    public Account getAccount(Long userId) throws AccountNotFound {
        String sql = "SELECT account_id, user_id, balance FROM accounts WHERE user_id = ?;";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, userId);
        if (rowSet.next()) {
            return mapRowToAccount(rowSet);
        }
        throw new AccountNotFound("Account not found.");
    }

    public Long getAccountIdOfCurrentUser(Principal principal) throws AccountNotFound {
        Account currentAccount = getAccount(userDAO.findIdOfCurrentUser(principal));
        return currentAccount.getAccountId();
    }

    private Account mapRowToAccount(SqlRowSet rowSet) {
        Account account = new Account();
        account.setAccountId(rowSet.getLong("account_id"));
        account.setUserId(rowSet.getLong("user_id"));
        account.setBalance(rowSet.getBigDecimal("balance"));
        return account;
    }
}
