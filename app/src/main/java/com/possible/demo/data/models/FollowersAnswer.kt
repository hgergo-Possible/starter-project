package com.possible.demo.data.models

/**
 * Represents the answers to the followers request.
 * It either contains the followers or reporst no-such-user
 */
sealed class FollowersAnswer {
    data class Followers(val followers: List<User>) : FollowersAnswer()
    object NoSuchUser : FollowersAnswer()
}