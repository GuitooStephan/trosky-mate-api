package com.gtuc.troskyMate.controllers;

import com.gtuc.troskyMate.forms.JSONResponse;
import org.springframework.web.bind.annotation.*;

@RestController
public class TroskyMateController {

    @CrossOrigin(origins = "http://localhost:8100")
    @RequestMapping(value = {"/getRoute"} , method = RequestMethod.GET)
    @ResponseBody
    public String getRoute(
            @RequestParam String origin,
            @RequestParam String destination
    ){
        return "You are welcome in";
    }

}
