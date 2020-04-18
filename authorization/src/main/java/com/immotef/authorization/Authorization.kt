package com.immotef.authorization

import com.immotef.featureflag.FeatureFlag
import com.immotef.featureflag.FeatureFlagManager
import com.immotef.preferences.PreferencesFacade

/**
 *
 */
private const val EMAIL_KEY = "key_to_save_email"
private const val TOKEN_KEY = "token_key"
private const val BOARDING_KEY = "onboaring_key"
private const val FCM_TOKEN_KEY = "fcm_token_key"

sealed class LoginState
object LoggedState : LoginState()
object NotLoggedState : LoginState()
object NeedOnBoarding : LoginState()

interface AuthorizationSaver {
    suspend fun saveAuthData(data: AuthorizationData)
    suspend fun saveOnBoardingFinished()
    suspend fun saveFcmToken(token: String)
    suspend fun logout()
}

interface UserDataProvider {
    suspend fun provideLoginData(): UserData?
}

interface AuthorizationProvider {
    suspend fun provideToken(): String?
    suspend fun provideFcmToken(): String?
    suspend fun isLogged(): LoginState
}

internal class AuthorizationManager(
    private val preferencesFacade: PreferencesFacade,
    private val featureFlag: FeatureFlagManager,
    private val userDataStoreKey: String = EMAIL_KEY,
    private val tokenKey: String = TOKEN_KEY,
    private val onBoadringKey: String = BOARDING_KEY,
    private val fcmTokenKey: String = FCM_TOKEN_KEY
) : AuthorizationSaver,
    UserDataProvider,
    AuthorizationProvider {

    private var token: String? = null

    private var userData: UserData? = null

    private var fcmToken: String? = null

    override suspend fun saveAuthData(data: AuthorizationData) {
        token = data.token

        preferencesFacade.saveString(text = data.token, key = tokenKey)
        val userData = UserData(data.name, data.surname, data.birthDate, data.loggedSince)
        preferencesFacade.putObject(userDataStoreKey, userData)

    }

    override suspend fun saveOnBoardingFinished() {
        preferencesFacade.saveBoolean(true, onBoadringKey)
    }

    override suspend fun saveFcmToken(token: String) {
        fcmToken = token
        preferencesFacade.saveString(token, fcmTokenKey)

    }

    override suspend fun logout() {
        preferencesFacade.remove(userDataStoreKey)
        preferencesFacade.remove(tokenKey)
        token = null
        userData = null
    }

    override suspend fun provideFcmToken(): String? {
        if (fcmToken == null || fcmToken?.isBlank() == true) {
            fcmToken = preferencesFacade.retrieveString(fcmTokenKey)
        }
        return fcmToken
    }

    override suspend fun provideLoginData(): UserData? {
        return userData ?: (preferencesFacade.getObject(userDataStoreKey, UserData::class.java)).run {
            if (this == null)
                null
            else {
                userData = this
                userData
            }
        }
    }

    override suspend fun provideToken(): String? {
        if (token == null || token?.isBlank() == true) {
            token = preferencesFacade.retrieveString(tokenKey)
        }
        return token
    }

    override suspend fun isLogged(): LoginState {
        if (token == null || token?.isBlank() == true) {
            token = preferencesFacade.retrieveString(tokenKey)
        }
        val onboaring = !preferencesFacade.retrieveBoolean(onBoadringKey)

        return when {
            token?.isNotBlank() == true -> LoggedState
            onboaring && featureFlag.getFeatureFlag(FeatureFlag.ShowOnboarding) -> NeedOnBoarding
            else -> NotLoggedState
        }
    }
}

