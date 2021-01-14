package com.space.controller;

import com.space.model.Ship;
import com.space.model.ShipType;
import com.space.service.ShipHttpResponse;
import com.space.service.ShipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
/*
Here we tell to Spring that this controller should handle all the requests
start with /rest.
For example: http://localhost:8080/rest/ships
 */
@RequestMapping("/rest")
public class ShipController {

    private final ShipService shipService;

    @Autowired
    public ShipController(ShipService shipService) {
        this.shipService = shipService;
    }

    /*
        Here we tell to Spring that it should call this method when
        it got a GET request started with common request mapping -
        /rest and local request mapping - /ships. Method parameters
        can get params from the request.
    */
    @RequestMapping(value = "/ships", method = RequestMethod.GET)
    public List<Ship> getShipsList(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam (value = "planet", required = false) String planet,
            @RequestParam (value = "shipType", required = false) ShipType shipType,
            @RequestParam (value = "after", required = false) Long after,
            @RequestParam (value = "before", required = false) Long before,
            @RequestParam (value = "isUsed", required = false) Boolean isUsed,
            @RequestParam (value = "minSpeed", required = false) Double minSpeed,
            @RequestParam (value = "maxSpeed", required = false) Double maxSpeed,
            @RequestParam (value = "minCrewSize", required = false) Integer minCrewSize,
            @RequestParam (value = "maxCrewSize", required = false) Integer maxCrewSize,
            @RequestParam (value = "minRating", required = false) Double minRating,
            @RequestParam (value = "maxRating", required = false) Double maxRating,
            @RequestParam (value = "order", required = false) ShipOrder order,
            @RequestParam (value = "pageNumber", required = false) Integer pageNumber,
            @RequestParam (value = "pageSize", required = false) Integer pageSize) {

        List<Ship> shipsList = shipService.getShipsList(
                name, planet, shipType,
                after, before, isUsed,
                minSpeed,maxSpeed,minCrewSize,
                maxCrewSize, minRating, maxRating);

        ShipControllerUtils.sort(order, shipsList);

        return ShipControllerUtils.getShipsListOnPage(shipsList, pageNumber, pageSize);
        //return list;
    }

    @RequestMapping(value = "/ships/count",method = RequestMethod.GET)
    public Integer getShipsCount(
            @RequestParam (value = "name", required = false) String name,
            @RequestParam (value = "planet", required = false) String planet,
            @RequestParam (value = "shipType", required = false) ShipType shipType,
            @RequestParam (value = "after", required = false) Long after,
            @RequestParam (value = "before", required = false) Long before,
            @RequestParam (value = "isUsed", required = false) Boolean isUsed,
            @RequestParam (value = "minSpeed", required = false) Double minSpeed,
            @RequestParam (value = "maxSpeed", required = false) Double maxSpeed,
            @RequestParam (value = "minCrewSize", required = false) Integer minCrewSize,
            @RequestParam (value = "maxCrewSize", required = false) Integer maxCrewSize,
            @RequestParam (value = "minRating", required = false) Double minRating,
            @RequestParam (value = "maxRating", required = false) Double maxRating) {

        List<Ship> shipsList = shipService.getShipsList(
                name, planet, shipType,
                after, before, isUsed,
                minSpeed,maxSpeed,minCrewSize,
                maxCrewSize, minRating, maxRating);

        return shipsList.size();
    }

    @RequestMapping(value = "/ships", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Ship> createShip(@RequestBody Ship ship) {

        if (ship == null || !shipService.isValid(ship)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        ShipHttpResponse response = shipService.addShip(ship);
        return new ResponseEntity<>(response.getShip(), response.getHttpStatus());
    }

    @RequestMapping(value = "/ships/{id}",method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Ship> getShip(@PathVariable (value = "id") String stringId) {

        ShipHttpResponse response = shipService.getShip(stringId);
        if (!response.isSuccess()) {
            return new ResponseEntity<>(response.getHttpStatus());
        } else {
            return new ResponseEntity<>(response.getShip(), response.getHttpStatus());
        }
    }

    @RequestMapping(value = "/ships/{id}", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Ship> updateShip(@PathVariable (value = "id") String stringId,
                                           @RequestBody Ship shipWithNewValues) {

        ShipHttpResponse getResponse = shipService.getShip(stringId);
        if (!getResponse.isSuccess())
            return new ResponseEntity<>(getResponse.getHttpStatus());

        ShipHttpResponse updateResponse = shipService.updateShip(getResponse.getShip(), shipWithNewValues);
        if (!updateResponse.isSuccess())
            return new ResponseEntity<>(updateResponse.getHttpStatus());

        return new ResponseEntity<>(updateResponse.getShip(), HttpStatus.OK);
    }

    @RequestMapping(value = "/ships/{id}",method = RequestMethod.DELETE)
    @ResponseBody
    public ResponseEntity<Ship> deleteShip(@PathVariable (value = "id") String stringId) {

        ShipHttpResponse response = shipService.getShip(stringId);
        if (!response.isSuccess())
            return new ResponseEntity<>(response.getHttpStatus());

        shipService.deleteShip(response.getShip());
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
