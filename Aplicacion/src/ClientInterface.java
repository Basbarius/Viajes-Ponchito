import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class ClientInterface extends JFrame
{
    private JPanel mainPanel;
    private JPanel headerPanel;
    private JPanel footerPanel;
    private JPanel bodyPanel;
    private JLabel userLabel;
    private JLabel userText;
    private JButton regresarButton;
    private JTable table1;
    private JScrollPane scroll;
    private String activeUser;
    private Transaction transactionManager;

    public ClientInterface (String activeUser, Transaction transactionManager)
    {
        this.activeUser = activeUser;
        userText.setText(this.activeUser);
        this.transactionManager = transactionManager;

        add(mainPanel);
        setTitle("Interfaz de Cliente");
        setSize(940, 400);
        pack();
        setLocationRelativeTo(null);
        drawReservationTable(activeUser);
        regresarButton.addActionListener(backButtonListener);
    }
    ActionListener backButtonListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            StartMenu startMenu = null;
            try {
                startMenu = new StartMenu(activeUser, transactionManager);
                transactionManager.conn.rollback();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            startMenu.setVisible(true);
            startMenu.setDefaultCloseOperation(EXIT_ON_CLOSE);
            dispose();
        }
    };
    private void drawReservationTable(String activeUser) {
        String[] columnsToUse = {"Código", "Fecha de salida", "Fecha de Llegada", "Número de Personas", "Ciudad de Salida", "Ciudad de Llegada", "Precio"};
        ArrayList<String[]> results = null;
        try {
            results = transactionManager.query("select RESERVACIoN.codigoReserv, RESERVACIoN.fechaSalida, RESERVACIoN.fechaLlegada, RESERVACIoN.numPersonas, CIRCUITO.ciudadSalida, CIRCUITO.ciudadLlegada, CIRCUITO.precio " +
                    "from reservacion, circuito where CIRCUITO.identificador = RESERVACIoN.identificadorCircuito and RESERVACIoN.NombreCompleto = \"" + activeUser + "\";" );
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        if (results.isEmpty()) {
            return;
        }
        String[][] contenido = new String[results.size()][columnsToUse.length];
        for (int i = 0; i < results.size(); i++) {
            contenido[i] = results.get(i);
        }
        table1 = new JTable(contenido, columnsToUse);
        table1.setFillsViewportHeight(true);
        scroll.setViewportView(table1);
    }
}
