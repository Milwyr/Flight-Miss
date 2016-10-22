package com.flight.miss.chatbotAPI;

import com.flight.miss.chatbotAPI.JsonObjects.Conversation;
import com.flight.miss.chatbotAPI.JsonObjects.Message;
import com.flight.miss.chatbotAPI.JsonObjects.Messages;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Me on 10/22/2016.
 */
public interface IChatbot {

    @Headers(
        "Authorization: BotConnector x7fljS-XrD8.cwA.c_Q.0yg10Od99Q999UQZUhWfAwCJG9xk1fZZy-GCAukXzL4"
    )
    @POST("/api/conversations")
    Call<Conversation> startConversation();

    @Headers({
            "Authorization: BotConnector x7fljS-XrD8.cwA.c_Q.0yg10Od99Q999UQZUhWfAwCJG9xk1fZZy-GCAukXzL4"
    })
    @POST("/api/conversations/{id}/messages")
    Call<ResponseBody> sendMessage(@Path("id") String conversationId, @Body Message message);

    @Headers({
            "Authorization: BotConnector x7fljS-XrD8.cwA.c_Q.0yg10Od99Q999UQZUhWfAwCJG9xk1fZZy-GCAukXzL4"
    })
    @GET("/api/conversations/{id}/messages")
    Call<Messages> getUnseenMessages(@Path("id") String conversationId);

    @Headers({
            "Authorization: BotConnector x7fljS-XrD8.cwA.c_Q.0yg10Od99Q999UQZUhWfAwCJG9xk1fZZy-GCAukXzL4"
    })
    @GET("/api/conversations/{id}/messages")
    Call<Messages> getUnseenMessages(@Path("id") String conversationId, @Query("watermark") String lastWatermark);
}
