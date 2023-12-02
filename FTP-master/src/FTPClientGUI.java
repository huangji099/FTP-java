import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

class FTPClientGUI extends JFrame implements ActionListener {
        private JTextField commandField;
        private JTextArea outputArea;
        public FTPClientGUI() {
            setTitle("FTP Client");
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setSize(600, 400);
            // 创建界面组件
            JLabel commandLabel = new JLabel("需要执行的命令:");
            commandField = new JTextField(20);
            JButton submitButton = new JButton("确认");
            outputArea = new JTextArea(10, 30);
            // 设置布局
            setLayout(new BorderLayout());
            JPanel topPanel = new JPanel();
            topPanel.add(commandLabel);
            topPanel.add(commandField);
            topPanel.add(submitButton);
            add(topPanel, BorderLayout.NORTH);
            add(new JScrollPane(outputArea), BorderLayout.CENTER);

            // 添加事件监听器
            submitButton.addActionListener(this);
            commandField.addActionListener(this);
        }

        public void actionPerformed(ActionEvent e) {
            String userCommand = commandField.getText().trim();
            String userArg = "";

            // 检查用户命令和参数
            int spaceIndex = userCommand.indexOf(' ');
            if (spaceIndex != -1) {
                userArg = userCommand.substring(spaceIndex + 1).trim();
                userCommand = userCommand.substring(0, spaceIndex).trim();
            }

            // 根据用户命令执行操作
            switch (userCommand) {
                case "put":
                    if (do_put(userArg)) {
                        outputArea.append("Put completed successfully.\n");
                    } else {
                        outputArea.append("Error occurred during put.\n");
                    }
                    break;
                case "get":
                    if (do_get(userArg)) {
                        outputArea.append("Get completed successfully.\n");
                    } else {
                        outputArea.append("Error occurred during get.\n");
                    }
                    break;
                case "list":
                    do_list();
                    break;
                case "quit":
                    do_quit();
                    outputArea.append("Disconnected from server.\n");
                    break;
                case "delete":
                    do_list();
                    do_delete(userArg);
                    break;
                default:
                    outputArea.append("Invalid command.\n");
            }

            // 清空输入框
            commandField.setText("");
        }

        private boolean do_put(String arg) {
            // 实现put操作的逻辑
            return true;
        }

        private boolean do_get(String arg) {
            // 实现get操作的逻辑
            return true;
        }

        private void do_list() {
            // 实现list操作的逻辑
        }

        private void do_quit() {
            // 实现quit操作的逻辑
        }

        private void do_delete(String arg) {
            // 实现delete操作的逻辑
        }

        public static void main(String[] args) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    FTPClientGUI ftpClientGUI = new FTPClientGUI();
                    ftpClientGUI.setVisible(true);
                }
            });
        }
    }