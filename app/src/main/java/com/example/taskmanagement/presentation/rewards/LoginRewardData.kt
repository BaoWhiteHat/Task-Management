package com.example.taskmanagement.presentation.rewards

data class LoginReward(
    val day: Int,
    val coins: Int = 0,
    val tomeId: String? = null,
    val backgroundId: String? = null
)

val loginRewards = listOf(
    LoginReward(day = 1, coins = 25),
    LoginReward(day = 2, coins = 40),
    LoginReward(day = 3, tomeId = "tome_xp_1"),
    LoginReward(day = 4, coins = 70),
    LoginReward(day = 5, tomeId = "tome_gold_2"),
    LoginReward(day = 6, coins = 120),
    LoginReward(day = 7, coins = 100, backgroundId = "bg_27")
)