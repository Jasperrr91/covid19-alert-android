package com.immotef.authorization

import com.immotef.featureflag.FeatureFlag
import com.immotef.featureflag.FeatureFlagManager
import com.immotef.preferences.PreferencesFacade
import com.immotef.testutils.MainCoroutineScopeRule
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import io.kotlintest.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 *
 */


@ExperimentalCoroutinesApi
class AuthorizationTest {

    @get:Rule
    val coroutineScope = MainCoroutineScopeRule()

    private lateinit var preferencesFacade: PreferencesFacade

    private lateinit var authorizationManager: AuthorizationManager

    private lateinit var featureFlagManager: FeatureFlagManager
    private val emailKey = "tralala key"
    private val onboarding = "onboardingKey "
    private val fcmKey = "fcmKey "
    @Before
    fun beforeTest() {
        preferencesFacade = mock()
        featureFlagManager = mock()
        authorizationManager =
            AuthorizationManager(
                preferencesFacade = preferencesFacade,
                featureFlag = featureFlagManager,
                userDataStoreKey = emailKey,
                onBoadringKey = onboarding,
                fcmTokenKey = fcmKey
            )
        runBlocking {
            whenever(preferencesFacade.retrieveBoolean(onboarding)).thenReturn(false)
        }

    }

    @Test
    fun `saving authorization data triggers saving email to preferences facade`() {
        runBlockingTest {
            //given
            val data = AuthorizationData("token", "email", loggedSince = 123L)

            //when
            authorizationManager.saveAuthData(data)

            //then
            verify(preferencesFacade).putObject(emailKey, UserData(data.name, data.surname, data.birthDate, data.loggedSince))
        }
    }

    @Test
    fun `when email is saved provide it from method provideLoginData`() {
        runBlockingTest {
            //given
            val loginEmail = "login email"

            whenever(preferencesFacade.getObject(emailKey, UserData::class.java)).thenReturn(UserData(loginEmail))

            //when then

            authorizationManager.provideLoginData() shouldBe UserData(loginEmail)
        }
    }

    @Test
    fun `when data is already provided do not call preferences twice but just returning previous saved data`() {
        runBlockingTest {
            //given
            val loginEmail = "login email"

            whenever(preferencesFacade.getObject<UserData>(emailKey, UserData::class.java)).thenReturn(UserData(loginEmail))

            //when
            authorizationManager.provideLoginData()
            authorizationManager.provideLoginData()

            //then
            verify(preferencesFacade, times(1)).getObject(emailKey, UserData::class.java)
        }
    }

    @Test
    fun `is  logged is NotLoggedState returned when no token is saved`() {
        runBlockingTest {
            //given
            whenever(preferencesFacade.retrieveBoolean(onboarding)).thenReturn(true)
            //when then
            authorizationManager.isLogged() shouldBe NotLoggedState
        }
    }

    @Test
    fun `is logged is Logged State when token is there`() {
        runBlockingTest {
            //given
            val data = AuthorizationData("token","emai")

            //when
            authorizationManager.saveAuthData(data)

            //then
            authorizationManager.isLogged() shouldBe LoggedState
        }
    }

    @Test
    fun `should return NeedOnboarding from is logged if token is blank and no onboarding data previous and feature flag to true`() {
        runBlockingTest {
            //given
            val data = AuthorizationData("  ","emai")
            whenever(featureFlagManager.getFeatureFlag(FeatureFlag.ShowOnboarding)).thenReturn(true)
            //when
            authorizationManager.saveAuthData(data)

            //then
            authorizationManager.isLogged() shouldBe NeedOnBoarding
        }
    }

    @Test
    fun `should return Need onboarding from is logged if token is empty and on boarding string empty`() {
        runBlockingTest {
            //given
            val data = AuthorizationData("","emai")
            whenever(preferencesFacade.retrieveBoolean(onboarding)).thenReturn(false)
            whenever(featureFlagManager.getFeatureFlag(FeatureFlag.ShowOnboarding)).thenReturn(true)
            //when
            authorizationManager.saveAuthData(data)

            //then
            authorizationManager.isLogged() shouldBe NeedOnBoarding
        }
    }

    @Test
    fun `should return Not logged from is logged if token is empty and on boarding string empty but feature flag is false`() {
        runBlockingTest {
            //given
            val data = AuthorizationData("", "emai")
            whenever(preferencesFacade.retrieveBoolean(onboarding)).thenReturn(false)
            whenever(featureFlagManager.getFeatureFlag(FeatureFlag.ShowOnboarding)).thenReturn(false)
            //when
            authorizationManager.saveAuthData(data)

            //then
            authorizationManager.isLogged() shouldBe NotLoggedState
        }
    }

    @Test
    fun `should return  from is Notlogged if token is empty and need onoarding is not null`() {
        runBlockingTest {
            //given
            val data = AuthorizationData("", "emai")

            whenever(preferencesFacade.retrieveBoolean(onboarding)).thenReturn(true)
            //when
            authorizationManager.saveAuthData(data)

            //then
            authorizationManager.isLogged() shouldBe NotLoggedState
        }
    }

}