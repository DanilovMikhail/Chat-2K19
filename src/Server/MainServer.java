package Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.Vector;

public class MainServer {
    private Vector<ClientHandler> clients;

    public MainServer() throws SQLException {
        clients = new Vector<>();
        ServerSocket server = null;
        Socket socket = null;

        try {
            AuthService.connection();

            server = new ServerSocket(8189);
            System.out.println("Сервер запущен");

            while (true) {
                socket = server.accept();
                System.out.println("Клиент подключился");
                new ClientHandler(socket, this);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                server.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            AuthService.disconnect();
        }
    }

    public void addClient(ClientHandler clientHandler)
    {
        clients.add(clientHandler);
    }

    public void removeClient(ClientHandler clientHandler)
    {
        clients.remove(clientHandler);
    }

    public void broadCastMsg(String nick, String msg) {

        boolean sendUser = false;
        String userNick = "";
        String newMSG;

        if (msg.startsWith("/w "))
        {
            sendUser = true;
            String NickAndMSG = msg.substring(3);
            userNick = NickAndMSG.substring(0, NickAndMSG.indexOf(" "));
            newMSG = NickAndMSG.substring(NickAndMSG.indexOf(" ") + 1);
        }
        else
        {
            newMSG = msg;
        }

        for (ClientHandler o : clients) {
            if (sendUser && (!o.getNick().equals(userNick) && !o.getNick().equals(nick)))
            {
                continue;
            }
            o.sendMsg(nick + ": " + newMSG);
        }
    }
}
