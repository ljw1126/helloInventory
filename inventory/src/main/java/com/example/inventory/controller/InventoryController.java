package com.example.inventory.controller;

import com.example.inventory.common.controller.ApiResponse;
import com.example.inventory.controller.consts.ErrorCodes;
import com.example.inventory.controller.dto.DecreaseQuantityRequest;
import com.example.inventory.controller.dto.InventoryResponse;
import com.example.inventory.controller.exception.CommonInventoryHttpException;
import com.example.inventory.service.InventoryService;
import com.example.inventory.service.domain.Inventory;
import com.example.inventory.service.exception.InsufficientStockException;
import com.example.inventory.service.exception.InvalidDecreaseQuantityException;
import com.example.inventory.service.exception.ItemNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/v1/inventory")
@RestController
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @GetMapping("/{itemId}")
    ApiResponse<InventoryResponse> findByItemId(@PathVariable("itemId") String itemId) {
        Inventory inventory = inventoryService.findByItemId(itemId);
        if (inventory == null) {
            throw new CommonInventoryHttpException(ErrorCodes.ITEM_NOT_FOUND, HttpStatus.NOT_FOUND);
        }

        return ApiResponse.ok(InventoryResponse.from(inventory));
    }

    @PostMapping("/{itemId}/decrease")
    ApiResponse<InventoryResponse> decreaseByItemId(@PathVariable("itemId") String itemId,
                                                    @RequestBody DecreaseQuantityRequest request) {
        try {
            Inventory inventory = inventoryService.decreaseByItemId(itemId, request.quantity());
            return ApiResponse.ok(InventoryResponse.from(inventory));
        } catch (ItemNotFoundException e) {
            throw new CommonInventoryHttpException(ErrorCodes.ITEM_NOT_FOUND, HttpStatus.NOT_FOUND);
        } catch (InsufficientStockException e) {
            throw new CommonInventoryHttpException(ErrorCodes.INSUFFICIENT_STOCK, HttpStatus.BAD_REQUEST);
        } catch (InvalidDecreaseQuantityException e) {
            throw new CommonInventoryHttpException(ErrorCodes.INVALID_DECREASE_QUANTITY, HttpStatus.BAD_REQUEST);
        }
    }
}
