import com.google.protobuf.Timestamp
import io.grpc.stub.StreamObserver
import java.util.concurrent.ConcurrentHashMap


import io.grpc.ServerBuilder


fun main(args: Array<String>) {
    val server = ServerBuilder.forPort(9090).addService(ChatServiceImpl()).build()

    server.start()
    server.awaitTermination()
}


class ChatServiceImpl : ChatServiceGrpc.ChatServiceImplBase() {

    override fun chat(responseObserver: StreamObserver<Chat.ChatMessageFromServer>): StreamObserver<Chat.ChatMessage> {
        observers.add(responseObserver)

        return object : StreamObserver<Chat.ChatMessage> {
            override fun onNext(value: Chat.ChatMessage) {
                System.out.println(value)
                val message = Chat.ChatMessageFromServer.newBuilder()
                    .setMessage(value)
                    .setTimestamp(Timestamp.newBuilder().setSeconds(System.currentTimeMillis() / 1000))
                    .build()

                for (observer in observers) {
                    observer.onNext(message)
                }
            }

            override fun onError(t: Throwable) {
                // do something;
            }

            override fun onCompleted() {
                observers.remove(responseObserver)
            }
        }
    }

    companion object {
        private val observers = ConcurrentHashMap.newKeySet<StreamObserver<Chat.ChatMessageFromServer>>()
    }
}