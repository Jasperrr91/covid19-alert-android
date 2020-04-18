package com.immotef.authorization

import java.util.*

/**
 *
 */


data class AuthorizationData(val token: String,
                             val name: String? = null,
                             val surname: String? = null,
                             val birthDate: String? = null,
                             val loggedSince: Long = Date().time)

data class UserData(val name: String? = null,
                    val surname: String? = null,
                    val birthDate: String? = null,
                    val loggedSince: Long = -1)