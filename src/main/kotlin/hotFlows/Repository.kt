package hotFlows

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.shareIn
import java.util.concurrent.Executors

object Repository {

    private val dispatcher = Executors.newCachedThreadPool().asCoroutineDispatcher()
    private val scope = CoroutineScope(dispatcher)

//    val timer = MutableSharedFlow<Int>().apply {
//        scope.launch {
//            getTimerFlow().collect {
//                emit(it)
//            }
//        }
//    }.asSharedFlow()


    val timer = getTimerFlow().shareIn(
        scope,
        SharingStarted.WhileSubscribed()
    )

    private fun getTimerFlow(): Flow<Int> {
        return flow {
            var seconds = 0
            while (seconds < 10) {
                println("Emitted: $seconds")
                emit(seconds++)
                delay(1000)
            }
        }
    }
}