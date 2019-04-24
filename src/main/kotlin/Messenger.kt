import com.sun.org.apache.xml.internal.serializer.utils.Utils.messages
import java.awt.BorderLayout
import java.awt.Dimension
import javax.swing.*
import io.grpc.ManagedChannelBuilder
import io.grpc.ManagedChannel
import io.grpc.stub.StreamObserver


class Messenger {
    val channel = ManagedChannelBuilder.forAddress("localhost", 9090).usePlaintext(true).build()
    val chatService = ChatServiceGrpc.newStub(channel)

    val chat = chatService.chat(object : StreamObserver<Chat.ChatMessageFromServer> {
        override fun onNext(value: Chat.ChatMessageFromServer) {

        }

        override fun onError(t: Throwable) {
            t.printStackTrace()
            println("Disconnected1")
        }

        override fun onCompleted() {
            println("Disconnected2")
        }
    })


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
            chat.onNext(Chat.ChatMessage.newBuilder().setFrom("Andrey").setMessage(textField.text).build());
            textField.text = "";
        }
        inputPanel.add(sendButton)

        return inputPanel
    }

    private fun initTextArea(): JScrollPane {
        val textArea = JTextArea()
        textArea.isEditable = false
        return JScrollPane(textArea)
    }
}

fun main() {
    Messenger().initChat()
}