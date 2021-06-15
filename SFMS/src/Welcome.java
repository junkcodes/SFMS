import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.InetAddress;
import java.rmi.ConnectException;
import java.rmi.UnexpectedException;

import static java.lang.Thread.sleep;

public class Welcome {
    private JPanel Welcome;
    private JButton loginButton;
    private JButton registerButton;
    private JTextField serverIp;
    private JButton connectButton;
    private JLabel serverTextFeild;
    private Boolean login = false, reg = false;
    private Socket clientSocket = null;
    private DataInputStream clientDataInputStream;
    private DataOutputStream clientDataOutputStream;
    private InetAddress ip = null;
    private JFrame frame;


    public Welcome() {
        connectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                try {
                    InetAddress ip = InetAddress.getByName(serverIp.getText());
                    if(ip != null){
                        clientSocket = new Socket();
                        clientSocket.connect(new InetSocketAddress(ip, 5056),1000);
                        if(clientSocket != null){
                            clientDataInputStream = new DataInputStream(clientSocket.getInputStream());
                            clientDataOutputStream = new DataOutputStream(clientSocket.getOutputStream());
                            JOptionPane.showMessageDialog(Welcome, "Connected Successfully");
                            loginButton.setVisible(true);
                            registerButton.setVisible(true);
                            serverIp.setVisible(false);
                            connectButton.setVisible(false);
                            serverTextFeild.setVisible(false);
                        }
                    }

                } catch (IOException e) {
                    JOptionPane.showMessageDialog(Welcome, "Connection Failed");
                }
            }
        });
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                Login login = new Login();
                login.setSocketData(clientSocket,clientDataInputStream,clientDataOutputStream);
                frame.dispose();
                frame.setContentPane(login.getLogin());
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.pack();
                frame.setVisible(true);
                login.setFrame(frame);

            }
        });
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                Registration registration = new Registration();
                registration.setSocketData(clientSocket,clientDataInputStream,clientDataOutputStream);
                frame.dispose();
                frame.setContentPane(registration.Registration);
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.pack();
                frame.setVisible(true);
                registration.setFrame(frame);
            }
        });
    }
    public void setFrame(JFrame x){
        this.frame = x;
    }
    public JPanel getWelcome() {
        return Welcome;
    }

    public static void main(String[] args) throws InterruptedException, IOException {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        JFrame frame = new JFrame("Welcome");
        Welcome welcome = new Welcome();
        frame.setContentPane(welcome.Welcome);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        welcome.setFrame(frame);

    }

}
