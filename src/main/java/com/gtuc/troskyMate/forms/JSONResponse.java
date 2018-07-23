package com.gtuc.troskyMate.forms;

import com.gtuc.troskyMate.models.Domains.BusStops;
import com.gtuc.troskyMate.models.Domains.Buses;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JSONResponse {

    private int status ;
    private String message;
    private ArrayList<Map<String, Object>> result = new ArrayList<Map<String, Object>>();


    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


    public List<Map<String, Object>> getResult() {
        return result;
    }

    public void setResult(ArrayList<Map<String, Object>> result) {
        this.result = result;
    }

    public void addResult(Map<String, Object> result) {
        this.result.add(result);
    }
}
