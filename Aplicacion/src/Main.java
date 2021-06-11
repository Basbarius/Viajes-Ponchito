import javax.swing.*;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) throws SQLException, Exception {
        if( args.length != 0 ) {
            System.err.println( "Use: java TransactionMySQL" );
            System.exit( 1 );
        }

        StartMenu startMenu = new StartMenu("Invitado", null);
        startMenu.setVisible(true);
        startMenu.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }
}
