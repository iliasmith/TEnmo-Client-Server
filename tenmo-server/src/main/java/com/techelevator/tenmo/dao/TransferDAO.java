package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.exceptions.AccountNotFound;
import com.techelevator.tenmo.exceptions.OverdrawnException;
import com.techelevator.tenmo.model.TransferDetailsDTO;
import com.techelevator.tenmo.model.TransferHistoryDTO;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;

public interface TransferDAO {

    void createSendTransfer(Long idOfPayer, Long idOfPayee, BigDecimal amount, Principal principal) throws AccountNotFound, OverdrawnException;

    TransferDetailsDTO getTransferById(Long transferId, Principal principal);

    List<TransferHistoryDTO> getAllTransfers(Principal principal) throws AccountNotFound;
}
