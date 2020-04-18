package com.immotef.featureflag.load

import com.google.gson.annotations.SerializedName


/**
 *
 */


data class FeatureFlagJson(
    @SerializedName("showReportButton")
    val showReportButton: Boolean,
    @SerializedName("displayShareButton")
    val showShareButton: Boolean,
    @SerializedName("onboarding")
    val showOnboarding: Boolean
)