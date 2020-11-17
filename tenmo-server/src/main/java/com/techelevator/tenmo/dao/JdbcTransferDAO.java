package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.exceptions.AccountNotFound;
import com.techelevator.tenmo.exceptions.OverdrawnException;
import com.techelevator.tenmo.model.TransferDetailsDTO;
import com.techelevator.tenmo.model.TransferHistoryDTO;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcTransferDAO implements TransferDAO {

    private static final int TRANSFER_TYPE_SEND = 2;
    private static final int TRANSFER_TYPE_REQUEST = 1;
    private static final int TRANSFER_STATUS_INITIAL = 2;

    private JdbcTemplate jdbcTemplate;
    private AccountDAO accountDAO;

    public JdbcTransferDAO(JdbcTemplate jdbcTemplate, AccountDAO accountDAO) {
        this.jdbcTemplate = jdbcTemplate;
        this.accountDAO = accountDAO;
    }


    @Override
    public void createSendTransfer(Long idOfPayer, Long idOfPayee, BigDecimal amount, Principal principal) throws AccountNotFound, OverdrawnException {
        accountDAO.subtractFromBalance(amount, principal);
        accountDAO.addToBalance(amount, idOfPayee);
        Long accountOfPayer = accountDAO.getAccount(idOfPayer).getAccountId();
        Long accountOfPayee = accountDAO.getAccount(idOfPayee).getAccountId();
        String sql = "INSERT INTO transfers (transfer_type_id, transfer_status_id, " +
                "account_from, account_to, amount) VALUES (?, ?, ?, ?, ?);";
        jdbcTemplate.update(sql, TRANSFER_TYPE_SEND, TRANSFER_STATUS_INITIAL, accountOfPayer, accountOfPayee, amount);

    }

    @Override
    public TransferDetailsDTO getTransferById(Long transferId, Principal principal) {
        String sql = "SELECT transfer_id, transfer_type_id, amount, transfer_status_id, users.username AS sender_name, you.username AS receiver_name " +
                "FROM transfers " +
                "JOIN accounts ON transfers.account_from = accounts.account_id " +
                "JOIN accounts AS me ON transfers.account_to = me.account_id " +
                "JOIN users ON accounts.user_id = users.user_id " +
                "JOIN users AS you ON me.user_id = you.user_id " +
                "WHERE transfer_id = ?;";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, transferId);
        rowSet.next();
        TransferDetailsDTO transferDetailsDTO = new TransferDetailsDTO();
        transferDetailsDTO.setAmount(rowSet.getBigDecimal("amount"));
        transferDetailsDTO.setReceiverName(rowSet.getString("receiver_name"));
        transferDetailsDTO.setSenderName(rowSet.getString("sender_name"));
        transferDetailsDTO.setTransferId(rowSet.getLong("transfer_id"));
        transferDetailsDTO.setTransferStatusId(rowSet.getInt("transfer_status_id"));
        transferDetailsDTO.setTransferTypeId(rowSet.getInt("transfer_type_id"));

        boolean requestValid = principal.getName().equals(transferDetailsDTO.getReceiverName()) | principal.getName().equals(transferDetailsDTO.getSenderName());
        if (requestValid) {
            return transferDetailsDTO;
        }
        throw new ResourceAccessException("Not authorized to view the transfer at that Id.");
    }

    @Override
    public List<TransferHistoryDTO> getAllTransfers(Principal principal) throws AccountNotFound {
        List<TransferHistoryDTO> result = new ArrayList<>();
        String sql = "SELECT transfer_id, amount, transfer_type_id, users.username AS sender_name, you.username AS receiver_name " +
                "FROM transfers " +
                "JOIN accounts ON transfers.account_from = accounts.account_id " +
                "JOIN accounts AS me ON transfers.account_to = me.account_id " +
                "JOIN users ON accounts.user_id = users.user_id " +
                "JOIN users AS you ON me.user_id = you.user_id " +
                "WHERE accounts.account_id = ? OR me.account_id = ?;";
        Long account = accountDAO.getAccountIdOfCurrentUser(principal);
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, account, account);
        while (rowSet.next()) {
            result.add(mapRowToTransferHistoryDTO(rowSet, principal));
        }
        return result;
    }

    private TransferHistoryDTO mapRowToTransferHistoryDTO(SqlRowSet rowSet, Principal principal) {
        TransferHistoryDTO transferHistoryDTO = new TransferHistoryDTO();
        transferHistoryDTO.setAmount(rowSet.getBigDecimal("amount"));
        transferHistoryDTO.setTransferId(rowSet.getLong("transfer_id"));
        transferHistoryDTO.setTransferType(rowSet.getInt("transfer_type_id"));
        transferHistoryDTO.setUsernameFrom(rowSet.getString("sender_name"));
        transferHistoryDTO.setUsernameTo(rowSet.getString("receiver_name"));
        transferHistoryDTO.setUsernameOfCurrentUser(principal.getName());
        return transferHistoryDTO;
    }

}


