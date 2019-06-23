package com.gtuc.troskyMate;


import com.gtuc.troskyMate.Domains.AbstractTest;
import com.gtuc.troskyMate.forms.JSONResponse;
import com.gtuc.troskyMate.models.Domains.BusStops;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TroskyMateControllerIntegrationTest extends AbstractTest {

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        String findAllJson = "[{\"busStopLocation\":\"5.5957329, -0.2220479\",\"busStopName\":\"CAT Bus Stop\"," +
                "\"busStopArea\":[\"Tesano\",\"Accra\"]}" +
                ",{\"busStopLocation\":\"5.5951809, -0.2219576\",\"busStopName\":\"CAT Bus Stop\"," +
                "\"busStopArea\":[\"Tesano\",\"Accra\"]}," +
                "{\"busStopLocation\":\"5.5989245, -0.2239655\",\"busStopName\":\"Abeka Junction Bus Stop\"," +
                "\"busStopArea\":[\"Tesano\",\"Accra\"]}," +
                "{\"busStopLocation\":\"5.5985513, -0.2234973\",\"busStopName\":\"Abeka Junction Bus Stop\"," +
                "\"busStopArea\":[\"Tesano\",\"Accra\"]}," +
                "{\"busStopLocation\":\"5.5995177, -0.2247772\",\"busStopName\":\"Abeka Junction Curve Bus Stop\"," +
                "\"busStopArea\":[\"Tesano\",\"Accra\"]}," +
                "{\"busStopLocation\":\"5.5993959, -0.2245869\",\"busStopName\":\"Abeka Junction Curve Bus Stop\"," +
                "\"busStopArea\": [\"Tesano\",\"Accra\"]}]";
        BusStops[] busStops = super.mapFromJson(findAllJson, BusStops[].class);
        Mockito.when(busStopsRepository.findAll()).thenReturn(Arrays.asList(busStops));
    }

    @Test
    public void getProductsList() throws Exception {
        String uri = "/getClosestStops";
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(uri)
                .param("location", "5.5985513, -0.2234973")
                .accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        String content = mvcResult.getResponse().getContentAsString();
        assertEquals(200, status);
        JSONResponse jsonResponse = super.mapFromJson(content, JSONResponse.class);
        assertTrue(jsonResponse.getResult().size() > 0);
    }
}
