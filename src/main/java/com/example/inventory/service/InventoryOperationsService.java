package com.example.inventory.service;

import com.example.inventory.dto.OperationDtos;
import com.example.inventory.entity.TransferStatus;
import java.time.LocalDate;
import java.util.List;

public interface InventoryOperationsService {
    OperationDtos.PurchaseResponse createPurchase(OperationDtos.PurchaseRequest request);
    List<OperationDtos.PurchaseResponse> purchases(Long branchId, Long equipmentId, LocalDate from, LocalDate to);
    OperationDtos.PurchaseResponse purchase(Long id);
    OperationDtos.TransferResponse createTransfer(OperationDtos.TransferRequest request);
    List<OperationDtos.TransferResponse> transfers();
    OperationDtos.TransferResponse transfer(Long id);
    OperationDtos.TransferResponse updateTransferStatus(Long id, TransferStatus status);
    OperationDtos.AssignmentResponse createAssignment(OperationDtos.AssignmentRequest request);
    List<OperationDtos.AssignmentResponse> assignments();
    OperationDtos.AssignmentResponse assignment(Long id);
    OperationDtos.ExpenditureResponse createExpenditure(OperationDtos.ExpenditureRequest request);
    List<OperationDtos.ExpenditureResponse> expenditures();
    OperationDtos.ExpenditureResponse expenditure(Long id);
}
