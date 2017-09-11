package com.gtuc.troskyMate.controllers;

import com.gtuc.troskyMate.forms.JSONResponse;
import com.gtuc.troskyMate.logic.RouteSelectionLogic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class TroskyMateController {

    @Autowired
    RouteSelectionLogic routeSelectionLogic;

    @CrossOrigin(origins = "http://localhost:8100")
    @RequestMapping(value = {"/getRoute"} , method = RequestMethod.GET)
    @ResponseBody
    public String getRoute(
            @RequestParam String origin,
            @RequestParam String destination
    ){
        return routeSelectionLogic.routeRequest(origin, destination);
    }

}
