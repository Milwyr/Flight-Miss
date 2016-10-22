package com.flight.miss.chatbotAPI;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Me on 10/22/2016.
 */
public class Chatbot {

    public static final String BASE_URL = "https://directline.botframework.com";

    public IChatbot api;

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();


    public Chatbot() {
        api = retrofit.create(IChatbot.class);
    }
}
