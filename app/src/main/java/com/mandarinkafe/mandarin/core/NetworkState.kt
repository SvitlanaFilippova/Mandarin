package com.mandarinkafe.mandarin.core

sealed class NetworkState {

    data object Good : NetworkState()

    data object Failed : NetworkState()

}