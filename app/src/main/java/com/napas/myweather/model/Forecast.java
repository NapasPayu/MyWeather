
package com.napas.myweather.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Forecast {

    @SerializedName("response")
    @Expose
    private Response response;
    @SerializedName("forecast")
    @Expose
    private Forecast_ forecast;

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }

    public Forecast_ getForecast() {
        return forecast;
    }

    public void setForecast(Forecast_ forecast) {
        this.forecast = forecast;
    }

}
