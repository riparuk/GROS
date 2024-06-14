package com.bukitvista.gros.retrofit

import com.bukitvista.gros.response.ImageURLsItem
import com.bukitvista.gros.response.RequestImagesResponse
import com.bukitvista.gros.response.RequestsResponse
import com.bukitvista.gros.response.RequestsResponseItem
import com.bukitvista.gros.response.StaffResponse
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
//    @GET("search/users")
//    fun searchUser(
//        @Query("q") username: String
//    ): Call<SearchUserResponse>
//
//    @GET("users/{username}")
//    fun detailUser(
//        @Path("username") username: String
//    ): Call<UserDetailResponse>

    @GET("staffs/{id}")
    fun getStaffById(
        @Path("id") id: String
    ): Call<StaffResponse>

    @Multipart
    @PUT("staffs/{id}/photo")
    fun uploadProfilePicture(
        @Path("id") id: String,
        @Part file: MultipartBody.Part
    ): Call<StaffResponse>

    @PUT("/requests/{request_id}/assignto/{staff_id}")
    fun assignRequest(
        @Path("request_id") requestId: Int,
        @Path("staff_id") staffId: Int
    ): Call<RequestsResponseItem>

    @Multipart
    @POST("/requests/{request_id}/images")
    fun uploadRequestImage(
        @Path("request_id") requestId: Int,
        @Part image: MultipartBody.Part
    ): Call<List<ImageURLsItem?>?>

    @PUT("/requests/{request_id}/update-step/{step}")
    fun updateRequestStep(
        @Path("request_id") requestId: Int,
        @Path("step") step: Int
    ): Call<RequestsResponseItem>

    @GET("requests/")
    fun getRequests(
        @Query("start_date") startDate: String? = null,
        @Query("end_date") endDate: String? = null,
        @Query("guest_id") guestId: Int? = null,
        @Query("property_id") propertyId: String? = null,
        @Query("priority") priority: Int? = null,
        @Query("request_id") requestId: Int? = null,
        @Query("assignTo") assignTo: String? = null
    ): Call<List<RequestsResponseItem>>
}