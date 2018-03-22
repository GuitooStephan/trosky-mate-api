package com.gtuc.troskyMate.controllers;

import com.gtuc.troskyMate.forms.JSONResponse;
import com.gtuc.troskyMate.forms.JSONResponseClosestStops;
import com.gtuc.troskyMate.logic.ClosestStopsLogic;
import com.gtuc.troskyMate.logic.RouteSelectionLogic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class TroskyMateController {

    @Autowired
    private RouteSelectionLogic routeSelectionLogic;
    @Autowired
    private ClosestStopsLogic closestStopsLogic;

    @CrossOrigin(origins = "http://localhost:8100")
    @RequestMapping(value = {"/getRoute"} , method = RequestMethod.GET)
    @ResponseBody
    public JSONResponse getRoute(
            @RequestParam String origin,
            @RequestParam String destination
    ){
        return routeSelectionLogic.routeRequest(origin, destination);
    }

    @CrossOrigin(origins = "http://localhost:8100")
    @RequestMapping(value = {"/getClosestStops"} , method = RequestMethod.GET)
    @ResponseBody
    public JSONResponseClosestStops getClosestStops(
            @RequestParam String location
    ){
        return closestStopsLogic.closestStopsRequest(location);
    }

}
