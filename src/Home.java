import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;

public class Home {
    private JTextField serverIpTextField;
    private JTextField deleteTextField1;
    private JButton deleteButton;
    private JTextField createTextField;
    private JButton createButton;
    private JButton selectFileButton;
    private JButton uploadFileButton;
    private JTextField usernameTextField;
    private JScrollPane StorageData;
    private JPanel Sotrage;
    private JPanel Home;
    private JButton RESETButton1;
    private JButton RESETButton;
    private JTextField downloadTextField;
    private JButton downloadFileButton, lb = null;
    private JButton RESETButton2;
    private JButton RESETButton3;
    private JButton RESTARTButton;
    private JLabel uploadFileName;
    private JLabel serverIp;
    private Boolean isAlreadyOneClick = false;
    private JFrame frame;
    private Socket clientSocket = null;
    private DataInputStream clientDataInputStream;
    private DataOutputStream clientDataOutputStream;
    private List<String> folders;
    private List<String> files;
    private List<String> cwd;
    private JFileChooser uploadFileSelect;



    public Home() {
        cwd = new ArrayList<String>();
        cwd.add(usernameTextField.getText());
        WrapLayout experimentLayout = new WrapLayout();
        Sotrage = new JPanel();
        Sotrage.setLayout(experimentLayout);
        StorageData.setViewportView(Sotrage);
        experimentLayout.setAlignment(FlowLayout.LEFT);


        createButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if(!createTextField.getText().equals("")){
                    try {
                        clientDataOutputStream.writeUTF("create");
                        clientDataOutputStream.flush();
                        clientDataOutputStream.writeUTF(String.join("/",cwd.toArray(new String[cwd.size()]))+"/"+createTextField.getText());
                        clientDataOutputStream.flush();
                        String response = clientDataInputStream.readUTF();
                        if(response.equals("created")) {
                            readStorage(String.join("/",cwd.toArray(new String[cwd.size()])));
                            JOptionPane.showMessageDialog(Home,"Directory Created");
                        }
                        else if(response.equals("exists")){
                            JOptionPane.showMessageDialog(Home,"Directory Already Exits!");
                        }

                    } catch (IOException e) {
                        JOptionPane.showMessageDialog(Home,"Connection Failure!");
                        startPage();
                    }
                }
                else{
                    JOptionPane.showMessageDialog(Home,"Enter Directory Name!");
                }
            }
        });
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if(!deleteTextField1.getText().equals("")){
                    try {
                        clientDataOutputStream.writeUTF("delete");
                        clientDataOutputStream.flush();
                        clientDataOutputStream.writeUTF(String.join("/",cwd.toArray(new String[cwd.size()]))+"/"+deleteTextField1.getText());
                        clientDataOutputStream.flush();
                        String response = clientDataInputStream.readUTF();
                        if(response.equals("deleted")) {
                            readStorage(String.join("/",cwd.toArray(new String[cwd.size()])));
                            JOptionPane.showMessageDialog(Home,"Deleted Successfully");
                        }
                        else if(response.equals("notexists")){
                            JOptionPane.showMessageDialog(Home,"Directory or File Doesn't Exit!");
                        }
                    } catch (IOException e) {
                        JOptionPane.showMessageDialog(Home,"Connection Failure!");
                        startPage();
                    }
                }
                else{
                    JOptionPane.showMessageDialog(Home,"Enter Directory or File Name!");
                }
            }
        });
        RESETButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                deleteTextField1.setText("");
            }
        });
        RESETButton1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                createTextField.setText("");
            }
        });
        RESETButton2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                downloadTextField.setText("");
            }
        });
        selectFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                uploadFileSelect = new JFileChooser();
                uploadFileSelect.showOpenDialog(frame);
                File selectedFile = uploadFileSelect.getSelectedFile();
                if(selectedFile != null) {
                    selectFileButton.setText(selectedFile.getName().substring(0, 4) + "...");
                }
                else{
                    uploadFileSelect = null;
                }
            }
        });
        RESETButton3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                uploadFileSelect = null;
                selectFileButton.setText("SELECT");
            }
        });
        uploadFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if(uploadFileSelect != null){
                    File selectedFile = uploadFileSelect.getSelectedFile();
                    try {
                        clientDataOutputStream.writeUTF("upload");
                        clientDataOutputStream.flush();
                        clientDataOutputStream.writeUTF(String.join("/",cwd.toArray(new String[cwd.size()]))+"/"+selectedFile.getName());
                        clientDataOutputStream.flush();
                        String response = clientDataInputStream.readUTF();
                        if(response.equals("notexists")) {
                            byte[] byteArray = new byte[(int) selectedFile.length()];
                            FileInputStream fis = new FileInputStream(selectedFile);
                            BufferedInputStream bis = new BufferedInputStream(fis);
                            DataInputStream dis = new DataInputStream(bis);
                            clientDataOutputStream.writeLong(selectedFile.length());
                            clientDataOutputStream.flush();
                            int read;
                            while ((read = dis.read(byteArray)) != -1) {
                                clientDataOutputStream.write(byteArray, 0, read);
                            }
                            clientDataOutputStream.flush();
                            JOptionPane.showMessageDialog(Home, "File Uploaded!");
                            readStorage(String.join("/", cwd.toArray(new String[cwd.size()])));
                            selectFileButton.setText("SELECT");
                            uploadFileSelect = null;
                        }
                        else if(response.equals("notexists")){
                            JOptionPane.showMessageDialog(Home, "Same File Name Exists!\nRename The File");
                            selectFileButton.setText("SELECT");
                            uploadFileSelect = null;
                        }
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        JOptionPane.showMessageDialog(Home,"Connection Failure!");
                        startPage();
                    }

                }
                else{
                    JOptionPane.showMessageDialog(Home,"Select File!");
                }
            }
        });
        downloadFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if(!downloadTextField.getText().equals("")){
                    try {
                        clientDataOutputStream.writeUTF("download");
                        clientDataOutputStream.flush();
                        clientDataOutputStream.writeUTF(String.join("/",cwd.toArray(new String[cwd.size()]))+"/"+downloadTextField.getText());
                        clientDataOutputStream.flush();
                        String response = clientDataInputStream.readUTF();
                        if(response.equals("exists")){
                            OutputStream outputFile = new FileOutputStream("/tmp/"+downloadTextField.getText());
                            int size = (int)clientDataInputStream.readLong();
                            byte[] buffer = new byte[size];
                            int read, totalByteRead = 0;
                            while((read = clientDataInputStream.read(buffer)) != -1){
                                outputFile.write(buffer, 0, read);
                                totalByteRead += read;
                                if(totalByteRead == size)break;
                            }
                            JOptionPane.showMessageDialog(Home,"File Downloaded at /tmp");
                        }
                        else if(response.equals("notexists")){
                            JOptionPane.showMessageDialog(Home,"File Doesn't Exist!");
                        }
                    } catch (IOException e) {
                        JOptionPane.showMessageDialog(Home,"Connection Failure!");
                        startPage();
                    }
                }
                else{
                    JOptionPane.showMessageDialog(Home,"Enter File Name!");
                }
            }
        });
        RESTARTButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                JOptionPane.showMessageDialog(Home,"Restarting Client!");
                startPage();
            }
        });
    }
    public JPanel getHome() {
        return Home;
    }
    public void setFrame(JFrame x){
        this.frame = x;
    }
    public void setSocketData(Socket soc, DataInputStream dis, DataOutputStream dos){
        this.clientSocket = soc;
        this.clientDataInputStream = dis;
        this.clientDataOutputStream = dos;

    }
    public void setUserName(String username){
        this.usernameTextField.setText(username);
        this.serverIpTextField.setText(clientSocket.getInetAddress().toString().split("/")[1]);
    }

    public void readStorage(String path){
        try {
            clientDataOutputStream.writeUTF("read");
            clientDataOutputStream.flush();
            clientDataOutputStream.writeUTF(path);
            clientDataOutputStream.flush();
            String name;
            folders = null;
            files = null;
            while (true) {
                name = clientDataInputStream.readUTF();
                if (name.equals("thatsall")) {
                    break;
                } else if (name.equals("folder")) {
                    if (folders == null) {
                        folders = new ArrayList<String>();
                    }
                    name = clientDataInputStream.readUTF();
                    folders.add(name);
                } else if (name.equals("file")) {
                    if (files == null) {
                        files = new ArrayList<String>();
                    }
                    name = clientDataInputStream.readUTF();
                    files.add(name);
                }
            }
        } catch (IOException ioException) {
            JOptionPane.showMessageDialog(Home,"Connection Failure!");
            startPage();
        }
        Sotrage.removeAll();
        Sotrage.revalidate();
        Sotrage.repaint();
        if(cwd.size() > 1){
            Sotrage.add(backButton());
        }
        if(folders != null) {
            for (int i = 0; i < folders.size(); i++) {
                JButton btn = new JButton(new ImageIcon("/home/groot/IdeaProjects/SFMS/src/bbb.png"));
                btn.setText(folders.get(i));
                btn.setVerticalTextPosition(SwingConstants.BOTTOM);
                btn.setHorizontalTextPosition(SwingConstants.CENTER);
                btn.setBackground(new Color(0, 0, 0, 0));
                btn.setFocusPainted(false);
                btn.setBorder(new EmptyBorder(5, 5, 5, 5));
                btn.addMouseListener(new MouseAdapter() {
                    private int eventCnt = 0;
                    private boolean selected = true;
                    java.util.Timer timer = new java.util.Timer("timer", false);

                    @Override
                    public void mouseClicked(MouseEvent e) {
                        btn.setBackground(new Color(0, 0, 0, 0));
                        Sotrage.revalidate();
                        Sotrage.repaint();
                        eventCnt = e.getClickCount();
                        if (e.getClickCount() == 1) {
                            timer.schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    if (eventCnt == 1) {
                                        if (lb == null) {
                                            lb = btn;
                                            btn.setBackground(Color.lightGray);
                                        } else if (lb == btn) {
                                            btn.setBackground(new Color(0, 0, 0, 0));
                                            lb = null;
                                        } else {
                                            btn.setBackground(Color.lightGray);
                                            lb.setBackground(new Color(0, 0, 0, 0));
                                            lb = btn;
                                        }
                                        deleteTextField1.setText(btn.getText());
                                    } else if (eventCnt > 1) {
                                        btn.setBackground(new Color(0, 0, 0, 0));
                                        if (lb != null) {
                                            lb.setBackground(new Color(0, 0, 0, 0));
                                            lb = null;
                                        }
                                        cwd.add(btn.getText());
                                        readStorage(String.join("/", cwd.toArray(new String[cwd.size()])));
                                    }
                                    Sotrage.revalidate();
                                    Sotrage.repaint();
                                    eventCnt = 0;
                                }
                            }, 300);
                        }
                    }
                });
                Sotrage.add(btn);
            }
        }
        if(files != null) {
            for (int i = 0; i < files.size(); i++) {
                JButton btn = new JButton(new ImageIcon("/home/groot/IdeaProjects/SFMS/src/ccc.png"));
                btn.setText(files.get(i));
                btn.setVerticalTextPosition(SwingConstants.BOTTOM);
                btn.setHorizontalTextPosition(SwingConstants.CENTER);
                btn.setBackground(new Color(0, 0, 0, 0));
                btn.setFocusPainted(false);
                btn.setBorder(new EmptyBorder(5, 5, 5, 5));
                btn.addMouseListener(new MouseAdapter() {
                    private int eventCnt = 0;
                    private boolean selected = true;
                    java.util.Timer timer = new java.util.Timer("timer", false);

                    @Override
                    public void mouseClicked(MouseEvent e) {
                        btn.setBackground(new Color(0, 0, 0, 0));
                        Sotrage.revalidate();
                        Sotrage.repaint();
                        eventCnt = e.getClickCount();
                        if (e.getClickCount() == 1) {
                            timer.schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    if (eventCnt == 1) {
                                        if (lb == null) {
                                            lb = btn;
                                            btn.setBackground(Color.lightGray);
                                        } else if (lb == btn) {
                                            btn.setBackground(new Color(0, 0, 0, 0));
                                            lb = null;
                                        } else {
                                            btn.setBackground(Color.lightGray);
                                            lb.setBackground(new Color(0, 0, 0, 0));
                                            lb = btn;
                                        }
                                        deleteTextField1.setText(btn.getText());
                                        downloadTextField.setText(btn.getText());
                                    } else if (eventCnt > 1) {
                                        btn.setBackground(new Color(0, 0, 0, 0));
                                        if (lb != null) {
                                            lb.setBackground(new Color(0, 0, 0, 0));
                                            lb = null;
                                        }
                                    }
                                    Sotrage.revalidate();
                                    Sotrage.repaint();
                                    eventCnt = 0;
                                }
                            }, 300);
                        }
                    }
                });
                Sotrage.add(btn);
            }
        }
        Sotrage.revalidate();
        Sotrage.repaint();
    }

    public void startPage(){
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

    public JButton backButton(){
        JButton btn = new JButton(new ImageIcon("/home/groot/IdeaProjects/SFMS/src/aaa.png"));
        btn.setBackground(new Color(0, 0, 0, 0));
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(5, 5, 5, 5));
        btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                cwd.remove(cwd.size()-1);
                readStorage(String.join("/", cwd.toArray(new String[cwd.size()])));
            }
        });
        return btn;
    }

}
