package com.apps.quizz.utils

interface Constants {
    companion object {

        var user_name:String=""
        var password:String=""
        val NAME_REGEX = ".{2,}"
        val PASSWORD_REGEX = ".{3,}"
        val YEAR_REGEX = ".{4,}"
        val EMPTY_REGEX = ".{1,}"
        val CARD_REGEX = ".{16,}"
        val PHONE_REGEX = ".{10,}"
        val SSN_REGEX = ".{9,}"
        val ZIP_REGEX = ".{5,}"

        val IS_NOTIFICATION = "is_notification"
        var notificationCheck = 0
    }

}