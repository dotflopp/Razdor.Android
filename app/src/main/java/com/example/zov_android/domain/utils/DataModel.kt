package com.example.zov_android.domain.utils

enum class DataModelType{
    //аудиовызов, видеовызов, запрос, ответ, кандидаты, конец вызова
    StartAudioCall, StartVideoCall, Offer, Answer, IceCandidates, EndCall
}

data class DataModel (
    val sender: String? = null, //отправитель
    val target: String? = null, //получатель
    val type: DataModelType,
    val data: String? = null,
    val timeStamp: Long = System.currentTimeMillis() // метка текущего времени
)

fun DataModel.isValid(): Boolean{
    return System.currentTimeMillis() - this.timeStamp < 60000
}