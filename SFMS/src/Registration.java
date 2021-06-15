import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Registration {
    private JTextField usernameReg;
    private JButton regButton;
    private JPasswordField passwordReg;
    private JButton resetButton;
    public JPanel Registration;
    private JButton loginButton;
    private JFrame frame;
    private Socket clientSocket;
    private DataInputStream clientDataInputStream;
    private DataOutputStream clientDataOutputStream;

    public Registration() {
        regButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                String username = usernameReg.getText();
                String password = new String(passwordReg.getPassword());
                if(username.equals("")){
                    JOptionPane.showMessageDialog(Registration, "Enter UserName!");
                }
                else if(password.equals("")) {
                    JOptionPane.showMessageDialog(Registration, "Enter Password!");
                }
                else if(!username.equals("") && !password.equals("")) {
                    try {
                        clientDataOutputStream.writeUTF("registration");
                        clientDataOutputStream.flush();
                        clientDataOutputStream.writeUTF(username);
                        clientDataOutputStream.flush();
                        String response = clientDataInputStream.readUTF();
                        if (response.equals("valid")) {
                            clientDataOutputStream.writeUTF(password);
                            clientDataOutputStream.flush();
                            JOptionPane.showMessageDialog(Registration, "Successfully Registered");
                        } else if (response.equals("duplicate")) {
                            JOptionPane.showMessageDialog(Registration, "UserName Already Exists!");
                        }
                    } catch (IOException e) {
                        JOptionPane.showMessageDialog(Registration, "Connection Failure!");
                        try {
                            clientSocket.close();
                            clientDataOutputStream.close();
                            clientDataOutputStream.close();
                        } catch (IOException ioException) {
                            ioException.printStackTrace();
                        }
                        Welcome welcome = new Welcome();
                        frame.setContentPane(welcome.getWelcome());
                        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                        frame.pack();
                        frame.setVisible(true);
                        welcome.setFrame(frame);
                    }
                }
            }
        });
        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {

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
    }
    public void setFrame(JFrame x){
        this.frame = x;
    }
    public void setSocketData(Socket soc, DataInputStream dis, DataOutputStream dos){
        this.clientSocket = soc;
        this.clientDataInputStream = dis;
        this.clientDataOutputStream = dos;
    }

}
