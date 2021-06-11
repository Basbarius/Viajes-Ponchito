import javax.swing.*;
import javax.swing.tree.TreeNode;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.ArrayList;

public class StartMenu extends JFrame{
    private JPanel mainPanel;
    private JPanel headerPanel;
    private JPanel bodyPanel;
    private JPanel footerPanel;
    private JButton publicInterfaceButton;
    private JButton clientInterfaceButton;
    private JButton agencyInterfaceButton;
    private JPanel loginPanel;
    private JTextField usernameText;
    private JTextField passwordText;
    private JLabel usernameLabel;
    private JLabel passwordLabel;
    private JButton loginButton;
    private JButton closeButton;
    private JButton logoutButton;
    private JLabel activeUserLabel;
    private JLabel activeUserText;
    private JLabel loginMessage;

    private String activeUser;
    private JFrame startMenu;
    private ArrayList<String[]> resultSet;
    private Transaction transactionManager;

    public StartMenu(String activeUser, Transaction transactionManager) throws SQLException, Exception {
        add(mainPanel);
        setTitle("Viajes Ponchito");
        setSize(940, 400);
        pack();
        setLocationRelativeTo(null);

        //crear o manejar el administrador de transferencias
        if(transactionManager != null){
            this.transactionManager = transactionManager;
        }
        else{
            this.transactionManager = new Transaction();
        }
        this.activeUser = activeUser;
        logoutButton.setVisible(false);
        if(!this.activeUser.equals("Invitado")) setLogInVisibility(false);
        activeUserText.setText(activeUser);
        handleUserOptions();
        startMenu = this;

        closeButton.addActionListener(closeButtonListener);
        loginButton.addActionListener(logInButtonListener);
        logoutButton.addActionListener(logOutListener);
        publicInterfaceButton.addActionListener(publicInterfaceButtonListener);
        clientInterfaceButton.addActionListener(clientInterfaceButtonListener);
        agencyInterfaceButton.addActionListener(agentInterfaceButtonListener);
    }

    ActionListener closeButtonListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            mainPanel.setVisible(false);
            dispose();
        }
    };

    ActionListener publicInterfaceButtonListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            PublicInterface publicInterface = new PublicInterface(activeUser, transactionManager);
            publicInterface.setVisible(true);
            publicInterface.setDefaultCloseOperation(EXIT_ON_CLOSE);
            dispose();
        }
    };

    ActionListener clientInterfaceButtonListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            ClientInterface clientInterface = new ClientInterface(activeUser, transactionManager);
            clientInterface.setVisible(true);
            clientInterface.setDefaultCloseOperation(EXIT_ON_CLOSE);
            dispose();
        }
    };

    ActionListener agentInterfaceButtonListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            AgentInterface agentInterface = new AgentInterface(activeUser, transactionManager);
            agentInterface.setVisible(true);
            agentInterface.setDefaultCloseOperation(EXIT_ON_CLOSE);
            dispose();
        }
    };

    ActionListener logInButtonListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            String username = usernameText.getText();
            String password = passwordText.getText();
            try {
                resultSet = transactionManager.query("select contraseña " +
                        "from clienteRegistrado " +
                        "where nombreCompleto = \"" + username + "\"");
            }
            catch (Exception ex){
                ex.printStackTrace();
            }
            if(resultSet.isEmpty() || !resultSet.get(0)[0].equals(password)){
                System.out.println("No esta registrado o no es la contraseña correcta");
                loginMessage.setText("No esta registrado o contraseña incorrecta");
            }
            else{
                activeUser = username;
                activeUserText.setText(activeUser);
                loginMessage.setText("Inicio de sesión exitoso!");
                usernameText.setText("");
                passwordText.setText("");

                setLogInVisibility(false);
            }
            handleUserOptions();
        }
    };

    ActionListener logOutListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            activeUserText.setText("Invitado");
            activeUser = activeUserText.getText();
            loginMessage.setText("");
            handleUserOptions();
            setLogInVisibility(true);
        }
    };

    private void handleUserOptions(){
        if(activeUser.equals("Invitado")){
            clientInterfaceButton.setVisible(false);
            agencyInterfaceButton.setVisible(false);
        }
        else{
            clientInterfaceButton.setVisible(true);
            try {
                resultSet = transactionManager.query("select categoria from clienteRegistrado where nombrecompleto = " +
                        "\"" + activeUser + "\";");
            }
            catch (Exception ex){
                ex.printStackTrace();
            }
            agencyInterfaceButton.setVisible(false);
            if(resultSet.get(0)[0].equals("agencia")) agencyInterfaceButton.setVisible(true);
        }
    }

    private void setLogInVisibility(boolean state){
        logoutButton.setVisible(!state);
        usernameText.setVisible(state);
        usernameLabel.setVisible(state);
        passwordText.setVisible(state);
        passwordLabel.setVisible(state);
        loginButton.setVisible(state);
    }

    public String getActiveUser(){
        return activeUser;
    }

}
