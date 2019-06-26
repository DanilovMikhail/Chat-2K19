package Client;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;

import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Controller
{
    @FXML
    AnchorPane AnchorPane;

    @FXML
    TextArea textArea;

    @FXML
    ListView lwUsers;

    @FXML
    TextField textField, loginField, tfAuth;

    @FXML
    PasswordField passField;

    @FXML
    Button btAuth, btnSend;

    //menu
    @FXML
    MenuItem miCloseWindow;

    @FXML
    CheckMenuItem userListLeft;

    private boolean isAuthorised;

    Socket socket;
    DataInputStream in;
    DataOutputStream out;

    final String IP_ADRESS = "localhost";
    final int PORT = 8189;

    public void setAuthorised(boolean isAuthorised)
    {
        this.isAuthorised = isAuthorised;

        if (!isAuthorised)
        {
            loginField.setVisible(true);
            loginField.setManaged(true);
            passField.setVisible(true);
            passField.setManaged(true);
            btAuth.setVisible(true);
            btAuth.setManaged(true);
            tfAuth.setVisible(true);
            tfAuth.setManaged(true);

            textArea.setVisible(false);
            textArea.setManaged(false);
            lwUsers.setVisible(false);
            lwUsers.setManaged(false);
            textField.setVisible(false);
            textField.setManaged(false);
            btnSend.setVisible(false);
            btnSend.setManaged(false);
        }
        else
        {
            loginField.setVisible(false);
            loginField.setManaged(false);
            passField.setVisible(false);
            passField.setManaged(false);
            btAuth.setVisible(false);
            btAuth.setManaged(false);
            tfAuth.setVisible(false);
            tfAuth.setManaged(false);

            textArea.setVisible(true);
            textArea.setManaged(true);
            lwUsers.setVisible(true);
            lwUsers.setManaged(true);
            textField.setVisible(true);
            textField.setManaged(true);
            btnSend.setVisible(true);
            btnSend.setManaged(true);
        }
    }

    public void connect()
    {
        try {
            socket = new Socket(IP_ADRESS, PORT);

            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        while (true) {
                            String str = in.readUTF();
                            if (str.startsWith("/authOk"))
                            {
                                setAuthorised(true);
                                break;
                            }
                            else
                            {
                                tfAuth.appendText(str);
                            }
                        }

                        while (true) {
                            String str = in.readUTF();
                            if (str.equals("/serverClosed")) break;
                            textArea.appendText(str + "\n");
                        }
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                    finally {
                        try {
                            socket.close();
                        }
                        catch (IOException e) {
                            e.printStackTrace();
                        }
                        setAuthorised(false);
                    }
                }
            }).start();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMsg() {
        try {
            out.writeUTF(textField.getText());
            textField.clear();
            textField.requestFocus();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void tryToAuth()
    {
        if (socket == null || socket.isClosed())
        {
            connect();
        }

        try {
            out.writeUTF("/auth " + loginField.getText() + " " + passField.getText());
            loginField.clear();
            passField.clear();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void userListLeft()
    {
        if (userListLeft.isSelected())
        {

        }
        else
        {

        }
    }

    public void closeChat()
    {
        try {
            out.writeUTF("/end");
            System.exit(0);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}