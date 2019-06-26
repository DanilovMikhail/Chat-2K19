package Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler {
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private MainServer server;
    private String nick;

    public ClientHandler(Socket socket, MainServer server) {
        try {
            this.socket = socket;
            this.server = server;
            this.in = new DataInputStream(socket.getInputStream());
            this.out = new DataOutputStream(socket.getOutputStream());

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        while (true) {
                            String str = in.readUTF();

                            if (str.startsWith("/auth")) {
                                String[] tokens = str.split(" ");
                                String newNick = AuthService.getNickByLoginAndPass(tokens[1], tokens[2]);
                                if (newNick != null)
                                {
                                    if (AuthService.userOnline(newNick))
                                    {
                                        sendMsg("Пользователь уже online");
                                    }
                                    else {
                                        sendMsg("/authOk");
                                        nick = newNick;
                                        server.addClient(ClientHandler.this);
                                        AuthService.setUserOnline(nick);
                                        server.broadCastMsg(nick, " вошел в чат");
                                        break;
                                    }
                                }
                                else
                                {
                                    sendMsg("Не верный логин или пароль");
                                }
                            }
                        }

                        while (true) {
                            String str = in.readUTF();

                            if (str.equals("/end")) {
                                out.writeUTF("/serverClosed");
                                break;
                            }
                            server.broadCastMsg(nick, str);
                        }
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                    finally {
                        try {
                            in.close();
                        }
                        catch (IOException e) {
                            e.printStackTrace();
                        }
                        try {
                            out.close();
                        }
                        catch (IOException e) {
                            e.printStackTrace();
                        }
                        try {
                            socket.close();
                        }
                        catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    server.removeClient(ClientHandler.this);
                    AuthService.delUserOnline(nick);
                }
            }).start();

        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMsg(String msg) {
        try {
            out.writeUTF(msg);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getNick()
    {
        return this.nick;
    }
}
