package com.bukitvista.gros.retrofit

import com.bukitvista.gros.response.RequestsResponse
import com.bukitvista.gros.response.RequestsResponseItem
import com.bukitvista.gros.response.StaffResponse
import retrofit2.Call
import retrofit2.http.GET
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