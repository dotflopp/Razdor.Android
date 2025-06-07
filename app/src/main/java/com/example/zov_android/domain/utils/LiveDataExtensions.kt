import androidx.lifecycle.LiveData
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import androidx.lifecycle.Observer

fun <T> LiveData<T>.asFlow() = callbackFlow {
    val observer = Observer<T> { value ->
        value?.let { this.trySend(it).isSuccess }
    }
    this@asFlow.observeForever(observer)
    awaitClose {
        this@asFlow.removeObserver(observer)
    }
}