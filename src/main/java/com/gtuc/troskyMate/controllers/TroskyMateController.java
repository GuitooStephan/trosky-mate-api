package com.gtuc.troskyMate.controllers;

import com.gtuc.troskyMate.forms.JSONResponse;
import com.gtuc.troskyMate.logic.AvailableRoutes;
import com.gtuc.troskyMate.logic.ClosestStopsLogic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class TroskyMateController {

    @Autowired
    private AvailableRoutes availableRoutes;
    @Autowired
    private ClosestStopsLogic closestStopsLogic;

    @CrossOrigin(origins = "*")
    @RequestMapping(value = {"/getRoute"} , method = RequestMethod.GET)
    @ResponseBody
    public JSONResponse getRoute(
            @RequestParam String origin,
            @RequestParam String destination
    ){
        return availableRoutes.getRoutes(origin, destination);
    }

    @CrossOrigin(origins = "*")
    @RequestMapping(value = {"/getClosestStops"} , method = RequestMethod.GET)
    @ResponseBody
    public JSONResponse getClosestStops(
            @RequestParam String location
    ){
        return closestStopsLogic.closestStopsRequest(location);
    }

}
