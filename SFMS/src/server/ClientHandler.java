package server;

import java.io.*;
import java.net.Socket;
import java.util.List;

class ClientHandler extends Thread {
    private final List<String> username;
    private final List<String> password;
    private final String mainPath;
    final DataInputStream dis;
    final DataOutputStream dos;
    final Socket s;

    public ClientHandler(Socket s, DataInputStream dis, DataOutputStream dos, List<String> usernamelist, List<String> passwordlist, String path) {
        this.s = s;
        this.dis = dis;
        this.dos = dos;
        this.username = usernamelist;
        this.password = passwordlist;
        this.mainPath = path;
    }

    @Override
    public void run() {
        String received;
        while (true) {
            try {
                received = dis.readUTF();
                if(received.equals("registration")){
                    received = dis.readUTF();
                    if(username.contains(received)){
                        dos.writeUTF("duplicate");
                        dos.flush();
                    }
                    else {
                        dos.writeUTF("valid");
                        dos.flush();
                        PrintWriter printWriter = new PrintWriter(new FileOutputStream(new File(mainPath+"/login/username.txt"), true));
                        printWriter.println(received);
                        printWriter.flush();
                        printWriter.close();
                        File newDir = new File(mainPath+"/storage/"+received);
                        username.add(received);
                        received = dis.readUTF();
                        printWriter = new PrintWriter(new FileOutputStream(new File(mainPath+"/login/password.txt"), true));
                        printWriter.println(received);
                        printWriter.flush();
                        printWriter.close();
                        password.add(received);
                        if(!newDir.exists()){
                            newDir.mkdir();
                        }
                    }
                }
                else if(received.equals("login")){
                    received = dis.readUTF();
                    int index = -1;
                    if(username.contains(received)){
                        dos.writeUTF("valid");
                        dos.flush();
                        index = username.indexOf(received);
                        received = dis.readUTF();
                        if(password.contains(received) && password.indexOf(received) == index){
                            dos.writeUTF("valid");
                            dos.flush();
                        }
                        else{
                            dos.writeUTF("invalid");
                            dos.flush();
                        }
                    }
                    else{
                        dos.writeUTF("invalid");
                        dos.flush();
                    }
                }
                else if(received.equals("read")){
                    received = dis.readUTF();
                    File[] details = new File(mainPath+"/storage/"+received).listFiles();
                    for(int i=0; i< details.length;i++){
                        if(details[i].isDirectory()){
                            dos.writeUTF("folder");
                            dos.flush();
                            dos.writeUTF(details[i].getName());
                            dos.flush();
                        }
                        else if(details[i].isFile()){
                            dos.writeUTF("file");
                            dos.flush();
                            dos.writeUTF(details[i].getName());
                            dos.flush();
                        }
                    }
                    dos.writeUTF("thatsall");
                    dos.flush();

                }
                else if(received.equals("create")){
                    received = dis.readUTF();
                    File newDir = new File(mainPath+"/storage/"+received);
                    if(!newDir.exists()){
                        newDir.mkdir();
                        dos.writeUTF("created");
                        dos.flush();
                    }
                    else{
                        dos.writeUTF("exists");
                        dos.flush();
                    }
                }
                else if(received.equals("delete")){
                    received = dis.readUTF();
                    File name = new File(mainPath+"/storage/"+received);
                    if(deleteDirOrFile(name)){
                        dos.writeUTF("deleted");
                        dos.flush();
                    }
                    else{
                        dos.writeUTF("notexists");
                        dos.flush();
                    }
                }
                else if(received.equals("upload")){
                    received = dis.readUTF();
                    File newFile = new File(mainPath+"/storage/"+received);
                    if(!newFile.exists()) {
                        dos.writeUTF("notexists");
                        dos.flush();
                        OutputStream outputFile = new FileOutputStream(newFile);
                        int size = (int) dis.readLong();
                        byte[] buffer = new byte[size];
                        int read, totalByteRead = 0;
                        while ((read = dis.read(buffer)) != -1) {
                            outputFile.write(buffer, 0, read);
                            totalByteRead += read;
                            if (totalByteRead == size) break;
                        }
                        outputFile.close();
                    }
                    else {
                        dos.writeUTF("exists");
                        dos.flush();
                    }
                }
                else if(received.equals("download")){
                    received = dis.readUTF();
                    File inputFile = new File(mainPath+"/storage/"+received);
                    if(inputFile.exists()){
                        dos.writeUTF("exists");
                        dos.flush();
                        byte[] byteArray = new byte[(int) inputFile.length()];
                        FileInputStream fis = new FileInputStream(inputFile);
                        BufferedInputStream bis = new BufferedInputStream(fis);
                        DataInputStream tdis = new DataInputStream(bis);
                        dos.writeLong(inputFile.length());
                        dos.flush();
                        int read;
                        while((read = tdis.read(byteArray)) != -1 ){
                            dos.write(byteArray, 0, read);
                        }
                        dos.flush();
                        tdis.close();
                        bis.close();
                        fis.close();

                    }
                    else{
                        dos.writeUTF("notexists");
                        dos.flush();
                    }
                }

            } catch (IOException e) {
                break;
            }
        }
        try {
            this.dis.close();
            this.dos.close();
            this.s.close();

        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public boolean deleteDirOrFile(File name){
        File[] files = name.listFiles();
        if(files != null){
            for(int i=0; i<files.length; i++){
                deleteDirOrFile(files[i]);
            }
        }
        String path = "";
        try {
            path = name.getCanonicalPath();
        } catch (IOException e) {
            e.printStackTrace();
        }
        File absPath = new File(path);
        return absPath.delete();
    }
    public void disconnectClient(){
        try {
            this.dis.close();
            this.dos.close();
            this.s.close();

        }catch(IOException e){
            e.printStackTrace();
        }
    }
}

