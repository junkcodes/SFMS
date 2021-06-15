package server;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;

import static java.lang.Thread.sleep;

public class serverGUI {
    private JPanel server;
    private JPanel Home;
    private JPanel Sotrage;
    private JTextField serverIpTextField;
    private JTextField deleteTextField1;
    private JTextField createTextField;
    private JTextField usernameTextField;
    private JScrollPane StorageData;
    private JButton deleteButton;
    private JButton RESETButton1;
    private JButton createButton;
    private JButton RESETButton;
    private JTextField downloadTextField;
    private JButton downloadFileButton, lb = null;
    private JButton RESETButton2;
    private JButton selectFileButton;
    private JButton uploadFileButton;
    private JButton RESETButton4;
    private JButton STARTButton;
    private JButton STOPButton;
    private List<String> folders;
    private List<String> files;
    private static ServerSocket serverSocket = null;
    private static Socket socket = null;
    private List<String> cwd;
    private JFileChooser uploadFileSelect;
    private static  List<String> username = null;
    private static  List<String> password = null;
    private static String mainPath = "server";

    public serverGUI() {
        cwd = new ArrayList<String>();
        cwd.add(mainPath+"/storage");
        WrapLayout experimentLayout = new WrapLayout();
        Sotrage = new JPanel();
        Sotrage.setLayout(experimentLayout);
        StorageData.setViewportView(Sotrage);
        experimentLayout.setAlignment(FlowLayout.LEFT);

        STARTButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                try {
                    serverSocket = new ServerSocket(5056);
                    STOPButton.setEnabled(true);
                    STARTButton.setEnabled(false);
                    deleteButton.setEnabled(false);
                    createButton.setEnabled(false);
                    uploadFileButton.setEnabled(false);
                    selectFileButton.setEnabled(false);
                    downloadFileButton.setEnabled(false);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        STOPButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                try {
                    serverSocket.close();
                    serverSocket = null;
                    STOPButton.setEnabled(false);
                    STARTButton.setEnabled(true);
                    deleteButton.setEnabled(true);
                    createButton.setEnabled(true);
                    uploadFileButton.setEnabled(true);
                    selectFileButton.setEnabled(true);
                    downloadFileButton.setEnabled(true);
                    readStorage(String.join("/",cwd.toArray(new String[cwd.size()])));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if(!deleteTextField1.getText().equals("")){
                    File name = new File(String.join("/",cwd.toArray(new String[cwd.size()]))+"/"+deleteTextField1.getText());
                    if(deleteDirOrFile(name)){
                        JOptionPane.showMessageDialog(Home,"Deleted Successfully");
                        readStorage(String.join("/",cwd.toArray(new String[cwd.size()])));
                        if(cwd.size() == 1){
                            deleteUser(mainPath+"/login",deleteTextField1.getText());
                            JOptionPane.showMessageDialog(Home,"User :"+deleteTextField1.getText()+"Deleted!");
                        }
                    }
                    else{
                        JOptionPane.showMessageDialog(Home,"Directory or File Doesn't Exit!");
                    }
                }
                else{
                    JOptionPane.showMessageDialog(Home,"Enter Directory or File Name!");
                }
            }
        });
        createButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if(!createTextField.getText().equals("")){
                    File newDir = new File(String.join("/",cwd.toArray(new String[cwd.size()]))+"/"+createTextField.getText());
                    if(!newDir.exists()){
                        newDir.mkdir();
                        JOptionPane.showMessageDialog(Home,"Directory Created");
                        readStorage(String.join("/",cwd.toArray(new String[cwd.size()])));
                    }
                    else{
                        JOptionPane.showMessageDialog(Home,"Directory Already Exits!");
                    }
                }
                else{
                    JOptionPane.showMessageDialog(Home,"Enter Directory Name!");
                }
            }
        });
        downloadFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if(!downloadTextField.getText().equals("")){
                    try {
                        File newFile = new File(String.join("/",cwd.toArray(new String[cwd.size()]))+"/"+downloadTextField.getName());
                        if(newFile.exists()){
                            byte[] byteArray = new byte[(int) newFile.length()];
                            FileInputStream fis = new FileInputStream(newFile);
                            BufferedInputStream bis = new BufferedInputStream(fis);
                            DataInputStream dis = new DataInputStream(bis);
                            OutputStream outputFile = new FileOutputStream("/tmp/"+downloadTextField.getName());
                            int read;
                            while((read = dis.read(byteArray)) != -1 ){
                                outputFile.write(byteArray, 0, read);
                            }
                            JOptionPane.showMessageDialog(Home,"File Downloaded to /tmp/");
                        }
                        else{
                            JOptionPane.showMessageDialog(Home,"File Doesn't Exist!");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else{
                    JOptionPane.showMessageDialog(Home,"Enter File Name!");
                }
            }
        });
        uploadFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if(uploadFileSelect != null){
                    File selectedFile = uploadFileSelect.getSelectedFile();
                    try {
                        File newFile = new File(String.join("/",cwd.toArray(new String[cwd.size()]))+"/"+selectedFile.getName());
                        if(!newFile.exists()){
                            byte[] byteArray = new byte[(int) selectedFile.length()];
                            FileInputStream fis = new FileInputStream(selectedFile);
                            BufferedInputStream bis = new BufferedInputStream(fis);
                            DataInputStream dis = new DataInputStream(bis);
                            OutputStream outputFile = new FileOutputStream(newFile);
                            int read;
                            while((read = dis.read(byteArray)) != -1 ){
                                outputFile.write(byteArray, 0, read);
                            }
                            JOptionPane.showMessageDialog(Home,"File Uploaded!");
                            readStorage(String.join("/",cwd.toArray(new String[cwd.size()])));
                            selectFileButton.setText("SELECT");
                            uploadFileSelect = null;
                        }
                        else{
                            JOptionPane.showMessageDialog(Home, "Same File Name Exists!\nRename The File");
                            selectFileButton.setText("SELECT");
                            uploadFileSelect = null;
                        }
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
                else{
                    JOptionPane.showMessageDialog(Home,"Select File!");
                }
            }
        });
        selectFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                uploadFileSelect = new JFileChooser();
                uploadFileSelect.showOpenDialog(server);
                File selectedFile = uploadFileSelect.getSelectedFile();
                if(selectedFile != null) {
                    selectFileButton.setText(selectedFile.getName().substring(0, 4) + "...");
                }
                else{
                    uploadFileSelect = null;
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
        RESETButton4.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                uploadFileSelect = null;
                selectFileButton.setText("SELECT");
            }
        });
    }

    public void readStorage(String path){
        folders = null;
        files = null;
        File[] details = new File(path).listFiles();
        for(int i = 0; i< (details != null ? details.length : 0); i++){
            if(details[i].isDirectory()){
                if (folders == null) {
                    folders = new ArrayList<String>();
                }
                folders.add(details[i].getName());
            }
            else if(details[i].isFile()){
                if (files == null) {
                    files = new ArrayList<String>();
                }
                files.add(details[i].getName());
            }
        }

        Sotrage.removeAll();
        Sotrage.revalidate();
        Sotrage.repaint();
        if(cwd.size() > 1){
            Sotrage.add(backButton());
        }
        if(folders != null) {
            for (int i = 0; i < folders.size(); i++) {
                JButton btn = new JButton(new ImageIcon("res/bbb.png"));
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
                JButton btn = new JButton(new ImageIcon("res/ccc.png"));
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

    public String[] readLines(String filename) throws IOException {
        FileReader fileReader = new FileReader(filename);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        List<String> lines = new ArrayList<String>();
        String line = null;
        while ((line = bufferedReader.readLine()) != null) {
            lines.add(line);
        }
        bufferedReader.close();
        return lines.toArray(new String[lines.size()]);
    }
    public boolean deleteDirOrFile(File name){
        File[] files = name.listFiles();
        if(files != null){
            for(int i=0; i<files.length; i++){
                deleteDirOrFile(files[i]);
            }
        }
        return name.delete();
    }
    public JButton backButton(){
        JButton btn = new JButton(new ImageIcon("res/aaa.png"));
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
    public void deleteUser(String path, String usern){
        password.remove(username.indexOf(usern));
        username.remove(usern);
        deleteDirOrFile(new File(path+"/username.txt"));
        deleteDirOrFile(new File(path+"/password.txt"));
        PrintWriter printWriter1 = null, printWriter2 = null;
        try {
            printWriter1 = new PrintWriter(new FileOutputStream(new File(path+"/username.txt"), true));
            printWriter2 = new PrintWriter(new FileOutputStream(new File(path+"/password.txt"), true));
            for(int i=0; i < username.size(); i++){
                printWriter1.println(username.get(i));
                printWriter1.flush();
                printWriter2.println(password.get(i));
                printWriter2.flush();
            }
            printWriter1.close();
            printWriter2.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }
    public static void main(String[] args) throws InterruptedException {
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
        JFrame frame = new JFrame("Server");
        File servDir = new File("server");
        if(!servDir.exists()) {
            servDir.mkdir();
            File credDir = new File("server/login");
            File storageDir = new File("server/storage");
            credDir.mkdir();
            storageDir.mkdir();
            File uFile = new File("server/login/username.txt");
            File pFile = new File("server/login/password.txt");
            try {
                uFile.createNewFile();
                pFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        serverGUI server= new serverGUI();
        server.readStorage(String.join("/", server.cwd.toArray(new String[server.cwd.size()])));
        frame.setContentPane(server.Home);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        FileArrayProvider fileArrayProvider = new FileArrayProvider();
        try {
            username = fileArrayProvider.readLines(mainPath+"/login/username.txt");
            password = fileArrayProvider.readLines(mainPath+"/login/password.txt");
        } catch (IOException e) {

        }
        List<ClientHandler> clients = new ArrayList<ClientHandler>();

        while (true) {
            socket = null;
            if(serverSocket != null) {
                try {
                    socket = serverSocket.accept();
                    System.out.println("A new client is connected : " + socket);
                    DataInputStream dis = new DataInputStream(socket.getInputStream());
                    DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
                    System.out.println("Assigning new thread for this client");
                    ClientHandler newClient = new ClientHandler(socket, dis, dos, username, password, mainPath);
                    clients.add(newClient);
                    Thread thread = newClient;
                    thread.start();
                } catch (Exception e) {
                    for(int i=0; i<clients.size();i++){
                        clients.get(i).disconnectClient();
                    }
                    clients.clear();
                }
            }
            else {
                sleep(100);
            }
        }
    }
}
