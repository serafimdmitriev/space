package com.space.service;

import com.space.model.Ship;
import org.springframework.http.HttpStatus;

public class ShipHttpResponse {
    private HttpStatus httpStatus = HttpStatus.OK;
    private Ship ship = null;
    private boolean success = false;

    private ShipHttpResponse() {

    }

    public static ShipHttpResponse newResponse(HttpStatus httpStatus) {
        ShipHttpResponse shipHttpResponse = new ShipHttpResponse();
        shipHttpResponse.setHttpStatus(httpStatus);
        shipHttpResponse.setSuccess(false);
        return shipHttpResponse;
    }

    public static ShipHttpResponse newResponse(Ship ship) {
        ShipHttpResponse shipHttpResponse = new ShipHttpResponse();
        shipHttpResponse.setShip(ship);
        shipHttpResponse.setSuccess(true);
        return shipHttpResponse;
    }

    public boolean isSuccess() {
        return success;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public Ship getShip() {
        return ship;
    }

    private void setSuccess(boolean success) {
        this.success = success;
    }

    private void setHttpStatus(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    private void setShip(Ship ship) {
        this.ship = ship;
    }
}
