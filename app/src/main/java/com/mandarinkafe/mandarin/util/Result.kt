package com.mandarinkafe.mandarin.util

sealed class Result<out T> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Failure(val throwable: Throwable) : Result<Nothing>()
}

inline fun <T> Result<T>.onSuccess(block: (T) -> Unit): Result<T> {
    if (this is Result.Success) block(data)
    return this
}

inline fun <T> Result<T>.onFailure(block: (Throwable) -> Unit): Result<T> {
    if (this is Result.Failure) block(throwable)
    return this
}