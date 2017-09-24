package com.gtuc.troskyMate.forms;

import java.util.ArrayList;
import java.util.List;

public class JSONResponse {

    private int status ;
    private String message;
    private List<Object> result = new ArrayList<Object>();


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

    public List<Object> getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result.add(result);
    }
}
