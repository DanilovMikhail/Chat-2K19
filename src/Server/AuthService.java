package Server;

import java.sql.*;

public class AuthService {
    private static Connection connection;
    private static Statement stmt;

    public static void connection()
    {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:mainDB.db");
            stmt = connection.createStatement();

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void disconnect()
    {
        try {
            connection.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static String getNickByLoginAndPass(String login, String pass)
    {
        String sql = String.format("Select nickname From users Where login = '%s' and password = '%s'", login, pass);

        try {
            ResultSet rs = stmt.executeQuery(sql);

            if (rs.next())
            {
                return rs.getString("nickname");

            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static boolean userOnline(String nick)
    {
        String sql = String.format("Select users.id, case when users_online.id is Null then 'false' else 'true' end online " +
                "From users Left Join users_online on users.id = users_online.userID " +
                "Where users.nickname = '%s'", nick);

        try {
            ResultSet rs = stmt.executeQuery(sql);

            if (rs.next())
            {
                if (rs.getString("online").equals("true"))
                {
                    return true;
                }
                else {
                    return false;
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public static void setUserOnline(String nickName)
    {
        String sql = String.format("Insert Into users_online(userID) Select ID From users where nickname = '%s'", nickName);

        try {
            stmt.execute(sql);
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void delUserOnline(String nickName)
    {
        String sql = String.format("Delete From users_online Where userID = (Select id From users where nickname = '%s' )", nickName);

        try {
            stmt.execute(sql);
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
