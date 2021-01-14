package com.space.controller;

import com.space.model.Ship;

import java.util.Comparator;
import java.util.List;

public class ShipControllerUtils {

    public static void sort(ShipOrder shipOrder, List<Ship> shipList) {
        if (shipOrder == null) return;

        Comparator<Ship> comparator = null;
        switch (shipOrder) {
            case ID: comparator =Comparator.comparing(Ship::getId);
                break;
            case SPEED: comparator = Comparator.comparing(Ship::getSpeed);
                break;
            case DATE: comparator = Comparator.comparing(Ship::getProdDate);
                break;
            case RATING: comparator = Comparator.comparing(Ship::getRating);
                break;
        }
        if (comparator != null)
            shipList.sort(comparator);
    }

    public static List<Ship> getShipsListOnPage(List<Ship> shipList, Integer pageNumber, Integer pageSize) {
        if (pageNumber == null) pageNumber = 0;
        if (pageSize == null) pageSize = 3;

        int fromShipNumber = pageNumber * pageSize;
        int toShipNumber = Math.min((pageNumber + 1) * pageSize, shipList.size());

        return shipList.subList(fromShipNumber, toShipNumber);
    }
}
