package com.takipsan.levinson.Entities.Retrofit

data class Resource<out T>(val status: Status, val data: T?, val message: String?) {
    companion object {
        fun <T> success(data: T?): Resource<T> {
            return Resource(Status.SUCCESS, data, null)
        }

        fun <T> nocontent(data: T?): Resource<T> {
            return Resource(Status.NOCONTENT, null, null)
        }

        fun <T> forbidden(data: T?): Resource<T> {
            return Resource(Status.FORBIDDEN, null, null)
        }
        fun <T> error(message: String, data: T?): Resource<T> {
            return Resource(Status.ERROR, data, message)
        }
        fun <T> noInternet(data: T?): Resource<T> {
            return Resource(Status.NOINTERNET, null, null)
        }
        fun <T> loading(data: T?): Resource<T> {
            return Resource(Status.LOADING, data, null)
        }
    }
}
enum class Status {
    SUCCESS,
    NOCONTENT,
    FORBIDDEN,
    ERROR,
    LOADING,
    NOINTERNET
}