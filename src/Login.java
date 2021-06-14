import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Login {
    private JButton loginButton;
    private JPanel Login;
    private JPasswordField passwordData;
    private JButton resetButton;
    private JTextField usernameData;
    private JButton registerButton;
    private JFrame frame;
    private Socket clientSocket;
    private DataInputStream clientDataInputStream;
    private DataOutputStream clientDataOutputStream;

    public Login() {
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                String username = usernameData.getText();
                String password =  new String(passwordData.getPassword());
                try {
                    clientDataOutputStream.writeUTF("login");
                    clientDataOutputStream.flush();
                    clientDataOutputStream.writeUTF(username);
                    clientDataOutputStream.flush();
                    String response = clientDataInputStream.readUTF();
                    if(response.equals("valid")){
                        clientDataOutputStream.writeUTF(password);
                        clientDataOutputStream.flush();
                        response = clientDataInputStream.readUTF();
                        if(response.equals("valid")){
                            JOptionPane.showMessageDialog(Login, "Login Successful");
                            Home home = new Home();
                            home.setSocketData(clientSocket,clientDataInputStream,clientDataOutputStream);
                            home.setUserName(username);
                            home.readStorage(username);
                            frame.dispose();
                            frame.setContentPane(home.getHome());
                            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                            frame.pack();
                            frame.setVisible(true);
                            home.setFrame(frame);
                        }
                        else{
                            JOptionPane.showMessageDialog(Login, "Invalid Login");
                        }
                    }
                    else{
                        JOptionPane.showMessageDialog(Login, "Invalid UserName");
                    }

                } catch (IOException e) {
                    JOptionPane.showMessageDialog(Login,"Connection Failure!");
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
        });
        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                usernameData.setText("");
                passwordData.setText("");
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
    public JPanel getLogin() {
        return Login;
    }
    public void setSocketData(Socket soc, DataInputStream dis, DataOutputStream dos){
        this.clientSocket = soc;
        this.clientDataInputStream = dis;
        this.clientDataOutputStream = dos;
    }

}
