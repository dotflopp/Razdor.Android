package com.example.zov_android.data.firebaseClient

import com.example.zov_android.domain.utils.DataModel
import com.example.zov_android.domain.utils.FirebaseFieldNames.LATEST_EVENT
import com.example.zov_android.domain.utils.FirebaseFieldNames.PASSWORD
import com.example.zov_android.domain.utils.FirebaseFieldNames.STATUS
import com.example.zov_android.domain.utils.UserStatus
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.values
import com.google.gson.Gson
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseClient @Inject constructor(
    private val dbRef: DatabaseReference, //ссылка на нашу бд
    private val gson: Gson
) {
    private var currentUserName:String?=null //введённое пользователем имя
    private fun setUserName(username: String){
        this.currentUserName = username
    }


    fun login(username: String, password: String, done: (Boolean, String?) -> Unit) {
        /* при каждом входе в систему мы проверяем,
          если ли такое имя пользователя, если да -> чекаем пароль */
        dbRef.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                //проверка существования пользователя
                //snapshot - бд
                if(snapshot.hasChild(username)){
                    //пользователь существует
                    val dbPassword = snapshot.child(username).child(PASSWORD).value
                    // Проверяем статус пользователя
                    val dbUserStatus = snapshot.child(username).child(STATUS).value

                    if (dbUserStatus == "ONLINE") {
                        // пользователь уже в сети
                        done(false, "Пользователь $username уже в сети")
                        return
                    }

                    if(password == dbPassword){
                        //вход
                        dbRef.child(username).child(STATUS).setValue(UserStatus.ONLINE)
                            .addOnCompleteListener {
                                setUserName(username)
                                //слушатель завершения
                                done(true,null)
                            }.addOnFailureListener{
                                //слушатель ошибки
                                done(false,"${it.message}")
                            }
                    }
                    else{
                        //сообщение об ошибке
                        done(false,"Неверный пароль")
                    }
                }
                else{
                    done(false,"Пользователя $username не существует")
                }
            }
            override fun onCancelled(error: DatabaseError) {} //обработчик ошибок для логирования
        })
    }

    fun reg(username: String, password: String, done: (Boolean, String?) -> Unit){
        dbRef.addListenerForSingleValueEvent(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.hasChild(username)){
                    done(false, "Такой пользователь уже существует")
                }
                else{
                    //регаем нового пользователя, если имени пользователя нет
                    dbRef.child(username).child(PASSWORD).setValue(password)
                        .addOnCompleteListener{//если всё успешно, обновляем статус user-а
                            dbRef.child(username).child(STATUS).setValue(UserStatus.ONLINE)
                                .addOnCompleteListener {
                                    setUserName(username)
                                    done(true,null)
                                }.addOnFailureListener {
                                    done(false,"${it.message}")
                                }
                        }.addOnFailureListener{
                            done(false,"${it.message}")
                        }
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    fun observeUsersStatus(status: (List<Pair<String, String>>) -> Unit) {
        // при каждом изменении данных в бд получаем список пользователей с их статусами (исключая текущего пользователя)
        dbRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = snapshot.children.filter { it.key != currentUserName }.map{
                    it.key!! to it.child(STATUS).value.toString()
                }
                status(list) //вызов колбека
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    interface Listener{
        fun onLatestEventReceived(event: DataModel)
    }

    // слежение за ласт ивентом
    fun subscribeForLatestEvent(listener: Listener){
        try{
            dbRef.child(currentUserName!!).child(LATEST_EVENT).addValueEventListener(
                object : ValueEventListener{ //следим за полем ивента
                    override fun onDataChange(snapshot: DataSnapshot) {
                        //onDataChange(snapshot)
                        val event = try{
                            //преобразуем json в dataModel
                            gson.fromJson(snapshot.value.toString(), DataModel::class.java)
                        }
                        catch (e:Exception){
                            e.printStackTrace()
                            null
                        }
                        event?.let{ // если событие произошло, то выполняем
                            listener.onLatestEventReceived(it) // в MainService
                        }

                    }

                    override fun onCancelled(error: DatabaseError) {}
                }
            )
        }
        catch (e:Exception){
            e.printStackTrace()
        }
    }

    fun sendMessageToOtherClient(message: DataModel, success:(Boolean) -> Unit){
        //конвертим сообщение в json
        val convertedMessage = gson.toJson(message.copy(sender = currentUserName))
        //устанавливаем значение в бд
        message.target?.let { dbRef.child(it).child(LATEST_EVENT).setValue(convertedMessage)
            .addOnCompleteListener {
                success(true)
            }
            .addOnFailureListener {
                success(false)
            }
        }
    }

    fun changeMyStatus(status: UserStatus) {
        dbRef.child(currentUserName!!).child(STATUS).setValue(status.name)
    }

    fun clearLatestEvent() {
        dbRef.child((currentUserName!!)).child(LATEST_EVENT).setValue(null)
    }

    fun logOff(function: () -> Unit) {
        dbRef.child(currentUserName!!).child(STATUS).setValue(UserStatus.OFFLINE)
            .addOnFailureListener{
                function()
            }
    }

}