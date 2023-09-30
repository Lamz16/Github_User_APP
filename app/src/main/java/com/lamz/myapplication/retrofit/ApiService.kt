package com.lamz.myapplication.retrofit


import com.lamz.myapplication.response.DetailUserResponse
import com.lamz.myapplication.response.GithubResponse
import com.lamz.myapplication.response.ItemsItem
import retrofit2.Call
import retrofit2.http.*


interface ApiService {

    @GET("search/users")
    fun getListUsers(@Query("q") q: String, @Query("per_page") per_page: String): Call<GithubResponse>

    @GET("users/{username}")
    fun getDetailUsers(@Path("username") login : String) : Call<DetailUserResponse>

    @GET("users/{username}/followers")
    fun getFollower(@Path("username") followersUrl : String,@Query("per_page") per_page: String) : Call<List<ItemsItem>>

    @GET("users/{username}/following")
    fun getFollowing(@Path("username")followingUrl : String, @Query("per_page") per_page: String) : Call<List<ItemsItem>>

}