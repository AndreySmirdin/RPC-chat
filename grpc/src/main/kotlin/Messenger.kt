import java.awt.BorderLayout
import java.awt.Dimension
import javax.swing.*

class Messenger {

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
        sendButton.addActionListener { e -> println("Hello") }
        inputPanel.add(sendButton)

        return inputPanel
    }

    private fun initTextArea(): JScrollPane {
        val textArea = JTextArea()
        textArea.isEditable = false
        return JScrollPane(textArea)
    }
}