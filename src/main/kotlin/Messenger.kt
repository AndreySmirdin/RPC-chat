import com.rabbitmq.client.*
import com.sun.org.apache.xml.internal.serializer.utils.Utils.messages
import java.awt.BorderLayout
import java.awt.Dimension
import javax.swing.*
import io.grpc.ManagedChannelBuilder
import io.grpc.ManagedChannel
import io.grpc.stub.StreamObserver
import java.util.*

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
    private val EXCHANGE_NAME = "MySuperE"
    private val GAP = "                                                  "
    val textArea = JTextArea()
    val r = Random().nextInt(1000000)

    init{
        factory.host = "localhost"
//        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.FANOUT)
        channel.queueDeclare(QUEUE_NAME, false, false, false, null)

//        val queueName = channel.queueDeclare().queue
//        channel.queueBind(queueName, EXCHANGE_NAME, "")

        val consumer = object : DefaultConsumer(channel) {
            override fun handleDelivery(consumerTag: String, envelope: Envelope,
                                        properties: AMQP.BasicProperties, body: ByteArray) {
                val message = String(body, charset("UTF-8"))
                if (message.startsWith(r.toString())) {
                    channel.basicPublish("", QUEUE_NAME, null, message.toByteArray(charset("UTF-8")))
                    return
                }

                println(" [x] Received '$message'")
                textArea.append(message.substring(message.indexOf(' ')) + "\n")
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
            val m = r.toString() + " " + textField.text
            channel.basicPublish("", QUEUE_NAME, null, m.toByteArray(charset("UTF-8")))
            textArea.append(GAP + textField.text + "\n")
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
