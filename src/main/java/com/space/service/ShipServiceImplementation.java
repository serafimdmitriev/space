package com.space.service;

import com.space.model.Ship;
import com.space.model.ShipType;
import com.space.repository.ShipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
/*
Транзакцией называется набор связанных операций, все из которых должны
быть выполнены корректно без ошибок. Если при выполнении одной из операций
возникла ошибка, все остальные должны быть отменены.

Аннотация @Transactional определяет область действия одной транзакции БД. В
данном случае - все обращения к MySQL в пределах одного метода происходят в
рамках одной транзакции.
 */
@Transactional
public class ShipServiceImplementation implements ShipService {

    private final ShipRepository shipRepository;

    @Autowired
    public ShipServiceImplementation(ShipRepository shipRepository) {
        this.shipRepository = shipRepository;
    }

    public List<Ship> getShipsList(String namePart, String planetPart, ShipType shipType, Long after,
                                   Long before, Boolean isUsed, Double minSpeed, Double maxSpeed,
                                   Integer minCrewSize, Integer maxCrewSize, Double minRating, Double maxRating) {
        Date afterDate = after != null ? new Date(after) : null;
        Date beforeDate = before != null ? new Date(before) : null;
        List<Ship> shipsList = new ArrayList<>();

        Iterable<Ship> shipsIterable = shipRepository.findAll();

        for (Ship ship : shipsIterable) {
            if (namePart != null && !ship.getName().contains(namePart)) continue;
            if (planetPart != null && !ship.getPlanet().contains(planetPart)) continue;
            if (shipType != null && ship.getShipType() != shipType) continue;
            if (isUsed != null && !ship.getUsed().equals(isUsed)) continue;

            if (!ShipServiceUtils.isBetween(ship.getProdDate(), afterDate, beforeDate)) continue;
            if (!ShipServiceUtils.isBetween(ship.getSpeed(), minSpeed, maxSpeed)) continue;
            if (!ShipServiceUtils.isBetween(ship.getCrewSize(), minCrewSize, maxCrewSize)) continue;
            if (!ShipServiceUtils.isBetween(ship.getRating(), minRating, maxRating)) continue;

            shipsList.add(ship);
        }

        return shipsList;
    }

    @Override
    public ShipHttpResponse addShip(Ship ship) {
        if (ship.getUsed() == null)
            setUsedAsDefault(ship);
        updateRating(ship);

        return ShipHttpResponse.newResponse(shipRepository.save(ship));
    }

    @Override
    public ShipHttpResponse getShip(String stringId) {

        Long id = null;
        try {
            id = Long.parseLong(stringId);
        } catch (NumberFormatException e) {
            return ShipHttpResponse.newResponse(HttpStatus.BAD_REQUEST);
        }

        if (!isIdValid(id))
            return ShipHttpResponse.newResponse(HttpStatus.BAD_REQUEST);

        Ship foundShip = shipRepository.findById(id).orElse(null);
        if (foundShip == null)
            return ShipHttpResponse.newResponse(HttpStatus.NOT_FOUND);

        return ShipHttpResponse.newResponse(foundShip);
    }

    @Override
    public ShipHttpResponse updateShip(Ship shipToUpdate, Ship shipWithNewValues) {

        boolean needForUpdateRating = false;

        if (isNameValid(shipWithNewValues)) {
            shipToUpdate.setName(shipWithNewValues.getName());
        } else if (shipWithNewValues.getName() != null) {
            return ShipHttpResponse.newResponse(HttpStatus.BAD_REQUEST);
        }

        if (isPlanetValid(shipWithNewValues)) {
            shipToUpdate.setPlanet(shipWithNewValues.getPlanet());
        } else if (shipWithNewValues.getPlanet() != null) {
            return ShipHttpResponse.newResponse(HttpStatus.BAD_REQUEST);
        }

        if (shipWithNewValues.getShipType() != null)
            shipToUpdate.setShipType(shipWithNewValues.getShipType());

        if (isProdDateValid(shipWithNewValues)) {
            shipToUpdate.setProdDate(shipWithNewValues.getProdDate());
            needForUpdateRating = true;
        } else if (shipWithNewValues.getProdDate() != null) {
            return ShipHttpResponse.newResponse(HttpStatus.BAD_REQUEST);
        }

        if (shipWithNewValues.getUsed() != null) {
            shipToUpdate.setUsed(shipWithNewValues.getUsed());
            needForUpdateRating = true;
        }

        if (isSpeedValid(shipWithNewValues)) {
            shipToUpdate.setSpeed(shipWithNewValues.getSpeed());
            needForUpdateRating = true;
        } else if (shipWithNewValues.getSpeed() != null) {
            return ShipHttpResponse.newResponse(HttpStatus.BAD_REQUEST);
        }

        if (isCrewSizeValid(shipWithNewValues)) {
            shipToUpdate.setCrewSize(shipWithNewValues.getCrewSize());
        } else if (shipWithNewValues.getCrewSize() != null) {
            return ShipHttpResponse.newResponse(HttpStatus.BAD_REQUEST);
        }

        if (needForUpdateRating)
            updateRating(shipToUpdate);

        return ShipHttpResponse.newResponse(shipRepository.save(shipToUpdate));
    }

    @Override
    public void deleteShip(Ship ship) {
        shipRepository.delete(ship);
    }

    public boolean isValid(Ship ship) {
        return (isNameValid(ship)
                && isPlanetValid(ship)
                && isProdDateValid(ship)
                && isSpeedValid(ship)
                && isCrewSizeValid(ship));
    }

    private void setUsedAsDefault(Ship ship) {
        ship.setUsed(false);
    }

    private boolean isIdValid(Long id) {
        return id != null && id > 0;
    }

    private void updateRating(Ship ship) {
        int currentYear = 3019;
        int prodYear = ShipServiceUtils.getYearFromDate(ship.getProdDate());
        double coefficient = ship.getUsed() ? 0.5 : 1;
        int scale = 2;

        ship.setRating(
                ShipServiceUtils.round(
                (80 * ship.getSpeed() * coefficient) / (currentYear - prodYear + 1), scale));
    }

    private boolean isNameValid(Ship ship) {
        int maxLength = 50;
        return ship.getName() != null
                && ShipServiceUtils.isStringValid(ship.getName(), maxLength);
    }

    private boolean isPlanetValid(Ship ship) {
        int maxLength = 50;
        return ship.getPlanet() != null
                && ShipServiceUtils.isStringValid(ship.getPlanet(), maxLength);
    }

    private boolean isProdDateValid(Ship ship) {
        int minDate = 2800;
        int maxDate = 3019;
        return ship.getProdDate() != null
                && ShipServiceUtils.isBetween(ship.getProdDate(),
                        ShipServiceUtils.getDateFromYear(minDate), ShipServiceUtils.getDateFromYear(maxDate));
    }

    private boolean isSpeedValid(Ship ship) {
        double minSpeed = 0.01;
        double maxSpeed = 0.99;
        return ship.getSpeed() != null
                && ShipServiceUtils.isBetween(ship.getSpeed(), minSpeed, maxSpeed);
    }

    private boolean isCrewSizeValid(Ship ship) {
        int minCrewSize = 1;
        int maxCrewSize = 9999;
        return ship.getCrewSize() != null
                && ShipServiceUtils.isBetween(ship.getCrewSize(), minCrewSize, maxCrewSize);
    }
}
