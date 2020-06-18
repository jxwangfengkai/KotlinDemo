package com.example.launchdemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.orhanobut.logger.Logger
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.coroutines.CoroutineContext
import kotlin.system.measureTimeMillis

class MainActivity : AppCompatActivity(){
    //private val mainScope = MainScope()
    @Volatile
    var counter = 0
    val mutex = Mutex()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        runBlocking {
            withContext(Dispatchers.Default) {
                massiveRun {
                    mutex.withLock {
                        counter++
                    }
                }
            }
            Logger.d("Counter = $counter")
        }


    }
    suspend fun massiveRun(action: suspend () -> Unit) {
        val n = 100  // number of coroutines to launch
        val k = 1000 // times an action is repeated by each coroutine
        val time = measureTimeMillis {
            coroutineScope { // scope for coroutines
                repeat(n) {
                    launch {
                        repeat(k) { action() }
                    }
                }
            }
        }
        Logger.d("Completed ${n * k} actions in $time ms")
    }

//    fun foo(): Flow<Int> = flow {
//        for (i in 1..3) {
//            Logger.d("${Thread.currentThread()}")
//            Thread.sleep(100) // 假装我们以消耗 CPU 的方式进行计算 log("Emitting $i")
//            emit(i) // 发射下一个值
//        }
//    }.flowOn(Dispatchers.Default)


//    override val coroutineContext: CoroutineContext
//        get() = Dispatchers.Main
//    fun main() = runBlocking<Unit> {
//        // launch a coroutine to process some kind of incoming request
//        val request = launch {
//            repeat(3) { i -> // launch a few children jobs
//                launch  {
//                    delay((i + 1) * 200L) // variable delay 200ms, 400ms, 600ms
//                    Logger.d("Coroutine $i is done")
//                }
//            }
//            Logger.d("request: I'm done and I don't explicitly join my children that are still active")
//        }
//        request.join() // wait for completion of the request, including all its children
//        Logger.d("Now processing of the request is complete")
//    }
//    fun main() = runBlocking {
//        val request = launch {
//            GlobalScope.launch {
//                Logger.d("job1: I run in GlobalScope and execute independently!")
//                delay(1000)
//                Logger.d("job1: I am not affected by cancellation of the request")
//            }
//            // and the other inherits the parent context
//            launch {
//                delay(100)
//                Logger.d("job2: I am a child of the request coroutine")
//                delay(1000)
//                Logger.d("job2: I will not execute this line if my parent request is cancelled")
//            }
//        }
//        delay(500)
//        request.cancel() // cancel processing of the request
//        delay(1000) // delay a second to see what happens
//        Logger.d("main: Who has survived request cancellation?")
//    }

//    fun main() = runBlocking<Unit> {
//        Logger.d("My job is ${coroutineContext[Job]}")
//    }
//
//    fun main() = runBlocking<Unit> {
//        launch { // context of the parent, main runBlocking coroutine
//            Logger.d("main runBlocking      : I'm working in thread ${Thread.currentThread().name}")
//        }
//        launch(Dispatchers.Unconfined) { // not confined -- will work with main thread
//            Logger.d("Unconfined            : I'm working in thread ${Thread.currentThread().name}")
//        }
//        launch(Dispatchers.Default) { // will get dispatched to DefaultDispatcher
//            Logger.d("Default               : I'm working in thread ${Thread.currentThread().name}")
//        }
//        launch(newSingleThreadContext("MyOwnThread")) { // will get its own new thread
//            Logger.d("newSingleThreadContext: I'm working in thread ${Thread.currentThread().name}")
//        }
////        launch(Dispatchers.Main) { // will get dispatched to DefaultDispatcher
////            Logger.d("Main               : I'm working in thread ${Thread.currentThread().name}")
////        }
//        launch(Dispatchers.IO) { // will get dispatched to DefaultDispatcher
//            Logger.d("IO               : I'm working in thread ${Thread.currentThread().name}")
//        }
//    }

//    fun main() = runBlocking<Unit> {
//        val time = measureTimeMillis {
//            val one = doOne()
//            val two = doSecond()
//            Logger.d("The answer is ${one + two}")
//        }
//        Logger.d("Completed in $time ms")
//    }
//
//    private suspend fun doOne():Int{
//        delay(1000L)
//        return 10
//    }
//    private suspend fun doSecond():Int{
//        delay(8000L)
//        return 12
//    }

}
