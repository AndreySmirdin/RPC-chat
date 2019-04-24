import com.sun.org.apache.xml.internal.serializer.utils.Utils.messages
import java.awt.BorderLayout
import java.awt.Dimension
import javax.swing.*
import io.grpc.ManagedChannelBuilder
import io.grpc.ManagedChannel
import io.grpc.stub.StreamObserver


import com.rabbitmq.client.AMQP
import com.rabbitmq.client.ConnectionFactory
import com.rabbitmq.client.DefaultConsumer
import com.rabbitmq.client.Envelope

class Messenger {


//    val chat = chatService.chat(object : StreamObserver<Chat.ChatMessageFromServer> {
//        override fun onNext(value: Chat.ChatMessageFromServer) {
//
//        }
//
//        override fun onError(t: Throwable) {
//            t.printStackTrace()
//            println("Disconnected1")
//        }
//
//        override fun onCompleted() {
//            println("Disconnected2")
//        }
//    })


    val factory = ConnectionFactory()
    val connection = factory.newConnection()
    val channel = connection.createChannel()

    private val QUEUE_NAME = "MySuperQ"
    private val GAP = "                  "
    val textArea = JTextArea()

    init{
        factory.host = "192.168.43.70"
        channel.queueDeclare(QUEUE_NAME, false, false, false, null)


        val consumer = object : DefaultConsumer(channel) {
            override fun handleDelivery(consumerTag: String, envelope: Envelope, properties: AMQP.BasicProperties, body: ByteArray) {
                val message = String(body, charset("UTF-8"))
                println(" [x] Received '$message'")
                textArea.append(message)
            }
        }
        channel.basicConsume(QUEUE_NAME, true, consumer)
    }

    fun initChat() {
        val frame = JFrame("Chat")
        frame.defaultCloseOperation = WindowConstants.EXIT_ON_CLOSE
        val panel = initPanel()
        frame.add(panel)
        frame.pack()
        frame.isVisible = true
    }

    private fun initPanel(): JPanel {
        val manager = BorderLayout()
        val panel = JPanel(manager)
        panel.preferredSize = Dimension(500, 500)
        val scrollPane = initTextArea()
        val inputPanel = initInputPanel()
        panel.add(scrollPane, BorderLayout.CENTER)
        panel.add(inputPanel, BorderLayout.SOUTH)
        return panel
    }

    private fun initInputPanel(): JPanel {
        val inputPanel = JPanel()
        val textField = JTextField(20)
        inputPanel.add(textField)

        val sendButton = JButton("Send")
        sendButton.addActionListener { e ->
            channel.basicPublish("", QUEUE_NAME, null, textField.text.toByteArray(charset("UTF-8")))
            textField.text = "";
        }
        inputPanel.add(sendButton)

        return inputPanel
    }

    private fun initTextArea(): JScrollPane {
        return JScrollPane(textArea)
    }
}

fun main() {
    Messenger().initChat()
}
