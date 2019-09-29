package com.twitter.twitterbelltest.location.model

enum class LocationRequestSource(val locationSourceType: String) {
    GPS_PROVIDER("gps"), NETWORK_PROVIDER("network"), PASSIVE_PROVIDER("passive"), ALL("all");
}