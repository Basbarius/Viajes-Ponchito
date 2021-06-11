import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumnModel;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.sql.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

public class PublicInterface extends JFrame{
    private JPanel mainPanel;
    private JPanel headerPnale;
    private JPanel bodyPanel;
    private JPanel footerPanel;
    private JPanel simPanel;
    private JPanel reserPanel;
    private JTextField simulationCodeText;
    private JPanel registrationPanel;
    private JTextField passwordText;
    private JTextField addressText;
    private JComboBox categoryComboBox;
    private JComboBox paymentComboBox;
    private JTextField cardNumText;
    private JLabel usernameLabel;
    private JLabel passwordLabel;
    private JLabel addressLabel;
    private JLabel categoryLabel;
    private JLabel paymentLabel;
    private JLabel cardnumLabel;
    private JButton reserveButton;
    private JLabel reservationLabel;
    private JLabel activeUserLabel;
    private JLabel activeUserText;
    private JButton backButton;
    private JLabel reserMessageLabel;
    private JLabel usernameText;
    private JButton createSimulationButton;
    private JButton addEtapaButton;
    private JLabel simcodeLabel;
    private JScrollPane tableScrollPane;
    private JTable etapaDisplayTable;
    private JPanel QueryPanel;
    private JComboBox paisCombo;
    private JComboBox ciudadCombo;
    private JScrollPane InfoConsulta;
    private JTable table1;
    private JComboBox selecpaisciudadcircComboBox;
    private JButton clearTableButton;
    private JComboBox paisEtapaCombobox;
    private JComboBox ciudadEtapaCombobox;
    private JComboBox hotelEtapaCombobox;
    private JComboBox lugarEtapaCombobox;
    private JLabel paisLabelEtapa;
    private JTextField duracionText;
    private JLabel hotelLabelEtapa;
    private JLabel ciudadLabelEtapa;
    private JLabel lugarAVisitarLabelEtapa;
    private JLabel fechaLabel;
    private JComboBox diaComboBox;
    private JComboBox mesComboBox;
    private JComboBox anoComboBox;
    private JLabel claveSimulacion;
    private JLabel numPersonasLabel;
    private JTextField nPersonasText;
    private JTextField nombreCompletoText;
    private JLabel nombreCompletoLabel;
    private JTextArea mensajeSim;
    private JLabel consultaLabel;
    private JFormattedTextField fechaTextField;
    private JScrollPane listaLugaresPane;
    private JList listaLugares;
    private JTextField loginPasswordText;
    private JLabel loginPasswordlabel;

    private String activeUser;
    private Transaction transactionManager;

    private DefaultTableModel etapaTableModel = new DefaultTableModel(){

        @Override
        public boolean isCellEditable(int row, int column) {
            //all cells false
            return false;
        }
    };
    private JTable etapaTable = new JTable(etapaTableModel);
    private TableColumnModel etapaTablecolumnModel = etapaDisplayTable.getColumnModel();
    private int rowCount;
    private ArrayList<JComboBox[]> etapaTableJComboboxes = new ArrayList<>();

    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private double descuentoAgencia;

    public PublicInterface(String activeUser, Transaction transactionManager){
        this.activeUser = activeUser;
        activeUserText.setText(this.activeUser);
        this.transactionManager = transactionManager;

        add(mainPanel);
        setTitle("Interfaz Publica");
        setSize(940, 400);
        pack();
        setLocationRelativeTo(null);
        registrationPanel.setVisible(false);

        //set up consultas
        paisCombo.setEnabled(false);
        ciudadCombo.setEnabled(false);
        llenarComboPais(paisCombo);

        //set up simulaciones
        setUpTable();
        ciudadEtapaCombobox.setEnabled(false);
        hotelEtapaCombobox.setEnabled(false);
        lugarEtapaCombobox.setEnabled(false);
        rowCount = 0;
        nombreCompletoLabel.setVisible(false);
        nombreCompletoText.setVisible(false);
        descuentoAgencia = 0.9;
        borrarSimulacionesViejas();

        backButton.addActionListener(backButtonListener);
        reserveButton.addActionListener(reserveButtonListener);
        paymentComboBox.addActionListener(paymentMethodButtonListener);
        paisCombo.addActionListener(paisComboboxListener);
        addEtapaButton.addActionListener(addEtapaListener);
        selecpaisciudadcircComboBox.addActionListener(paisCiudadCircuiSelectListener);
        ciudadCombo.addActionListener(ciudadTablaListener);
        paisEtapaCombobox.addActionListener(paisEtapaComboBoxListener);
        ciudadEtapaCombobox.addActionListener(ciudadEtapaComboBoxListener);
        clearTableButton.addActionListener(deleteEtapaListener);
        createSimulationButton.addActionListener(crearSimulacionListener);
    }

    ActionListener backButtonListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            StartMenu startMenu = null;
            try {
                startMenu = new StartMenu(activeUser, transactionManager);
            }
            catch (Exception ex){
                ex.printStackTrace();
            }
            startMenu.setVisible(true);
            startMenu.setDefaultCloseOperation(EXIT_ON_CLOSE);
            dispose();
        }
    };

    ActionListener reserveButtonListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            String simulationCode = simulationCodeText.getText();
            String simulationUser;
            ArrayList<String[]> resultSet = new ArrayList<>();
            try{
                resultSet = transactionManager.query("select * " +
                        "from simulacion where " +
                        "codigoSim = \"" + simulationCode + "\";");
            }
            catch (Exception ex){
                ex.printStackTrace();
            }

            if(resultSet.isEmpty()) {
                reserMessageLabel.setText("Clave de simulación inválida");
                return;
            }
            simulationUser = resultSet.get(0)[5];
            if(activeUser.equals("Invitado")){
                try{
                    if(transactionManager.query("select * from clienteRegistrado " +
                            "where nombreCompleto = \"" + simulationUser + "\";").isEmpty()){
                        if(registrationPanel.isVisible()){
                            registerUserAndLogIn();
                            makeReservation(resultSet, simulationUser);
                        }
                        else {
                            registrationPanel.setVisible(true);
                            cardNumText.setVisible(false);
                            cardnumLabel.setVisible(false);
                            usernameText.setText(simulationUser);
                            reserMessageLabel.setText("Llene con sus datos y reserve otra vez");
                        }
                    }
                    else{
                        reserMessageLabel.setText(simulationUser + ", por favor ingrese al sistema antes de reservar");
                    }
                }
                catch(Exception ex){
                    ex.printStackTrace();
                }
            }
            else{
                makeReservation(resultSet, simulationUser);
            }
        }
    };

    ActionListener paymentMethodButtonListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            JComboBox state = (JComboBox) e.getSource();
            String currentState = state.getSelectedItem().toString();
            if(!currentState.equals("efectivo")){
                cardnumLabel.setVisible(true);
                cardNumText.setVisible(true);
                return;
            }
            cardnumLabel.setVisible(false);
            cardNumText.setVisible(false);
        }
    };

    ActionListener addEtapaListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            rowCount = etapaTable.getRowCount();
            String duracion = duracionText.getText();
            String pais = paisEtapaCombobox.getSelectedItem().toString();
            String ciudad = ciudadEtapaCombobox.getSelectedItem().toString();
            String hotel = hotelEtapaCombobox.getSelectedItem().toString();
            String lugar = lugarEtapaCombobox.getSelectedItem().toString();
            if(duracion.equals("")){
                mensajeSim.setText("Ingrese una duracion de etapa");
                return;
            }


            etapaTableModel.insertRow(rowCount, new Object[]{rowCount + 1, duracion, pais, ciudad, hotel, lugar});
            resetEtapaSelector();
        }
    };

    ActionListener deleteEtapaListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            rowCount = etapaTable.getRowCount();
            //Remove rows one by one from the end of the table
            for (int i = rowCount-1; i >= 0; i--) {
                etapaTableModel.removeRow(i);
            }
        }
    };

    ActionListener paisEtapaComboBoxListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            llenarComboCiudad(paisEtapaCombobox, ciudadEtapaCombobox);
            ciudadEtapaCombobox.setEnabled(true);
        }
    };

    ActionListener ciudadEtapaComboBoxListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
                llenarComboHotel(ciudadEtapaCombobox, hotelEtapaCombobox);
                hotelEtapaCombobox.setEnabled(true);
                llenarComboLugarAVisitar(ciudadEtapaCombobox, lugarEtapaCombobox);
                lugarEtapaCombobox.setEnabled(true);
        }
    };

    ActionListener paisComboboxListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            llenarComboCiudad(paisCombo, ciudadCombo);
            ciudadCombo.setEnabled(true);
            drawConsultTable("país");
        }
    };

    ActionListener paisCiudadCircuiSelectListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if(selecpaisciudadcircComboBox.getSelectedItem() == "País/ciudad")
            {

                paisCombo.setEnabled(true);
                paisCombo.setSelectedItem(null);
                table1.setFillsViewportHeight(false);
            }
            if(selecpaisciudadcircComboBox.getSelectedItem() == "Circuitos Sugeridos")
            {
                paisCombo.setEnabled(false);
                paisCombo.setSelectedItem(null);
                ciudadCombo.setEnabled(false);
                ciudadCombo.setSelectedItem(null);
                drawConsultTable("circuito");

            }
        }
    };

    private void registerUserAndLogIn(){
        int year = 2021;
        String fullName = usernameText.getText();
        String password = passwordText.getText();
        String address = addressText.getText();
        String category = categoryComboBox.getSelectedItem().toString();
        String paymentMethod = paymentComboBox.getSelectedItem().toString();
        String insertQuery;
        if(cardnumLabel.isVisible()) {
            long cardNum = Long.parseLong(cardNumText.getText());
            insertQuery = "insert into clienteRegistrado values (\"" + fullName + "\", " +
                    Long.toString(cardNum) + ", " +
                    "\"" + password + "\", " +
                    "\"" + category + "\", " +
                    "" + year + ", " +
                    "\"" + address + "\", " +
                    "\"" + paymentMethod + "\");";
        }
        else{
            insertQuery = "insert into clienteRegistrado values (\"" + fullName + "\", NULL, " +
                    "\"" + password + "\", " +
                    "\"" + category + "\", " +
                    "" + year + ", " +
                    "\"" + address + "\", " +
                    "\"" + paymentMethod + "\");";
        }

        try{
            transactionManager.stmt.executeUpdate(insertQuery);
        }
        catch(Exception ex) {
            ex.printStackTrace();
        }
        activeUserText.setText(fullName);
        activeUser = fullName;
        registrationPanel.setVisible(false);
        reserMessageLabel.setText("Registro y reservacion exitosos, " + activeUser);
    }

    private void makeReservation(ArrayList<String[]> resultSet, String simulationUser){
        if(activeUser.equals(simulationUser)){
            String insertQuery = "insert into reservacion values (\"" + resultSet.get(0)[0] + "\", " +
                    "\"" + resultSet.get(0)[1] + "\", " +
                    "\"" + resultSet.get(0)[2] + "\", " +
                    "\"" + resultSet.get(0)[3] + "\", " +
                    "" + resultSet.get(0)[4] + ", " +
                    "\"" + resultSet.get(0)[5] + "\", " +
                    "\"" + resultSet.get(0)[6] + "\");";
            try {
                transactionManager.stmt.executeUpdate(insertQuery);
                //obtener reservaciones simuladas de hotel
                ArrayList<String[]> reservaciones = new ArrayList<>();
                reservaciones = transactionManager.query("select * from reservahotelsimulado " +
                        "where codigoSim = '" + resultSet.get(0)[0] + "';");
                //copiar valores a reservaciones de hotel reales
                for(String[] reservacion : reservaciones){
                    transactionManager.stmt.executeUpdate("insert into reservahotel values ('" + reservacion[0] +
                            "', '" + reservacion[1] + "', '" + reservacion[2] + "', '" + reservacion[3] + "', '" +
                            reservacion[4] + "', '" + reservacion[5] + "', " + reservacion[6] + ");");
                }
                //borrar reservaciones simuladas
                transactionManager.stmt.executeUpdate("delete from reservahotelsimulado " +
                        "where codigoSim = '" + resultSet.get(0)[0] + "';");
                //borrar simulacion
                transactionManager.stmt.executeUpdate("delete from simulacion " +
                        "where codigoSim = '" + resultSet.get(0)[0] + "';");
                transactionManager.conn.commit();
            }
            catch(Exception ex){
                ex.printStackTrace();
            }
            reserMessageLabel.setText("Reservacion exitosa, " + activeUser);
            return;
        }
        reserMessageLabel.setText("El usuario de la simulacion no corresponde a usted");
    }

    private void setUpTable(){
        etapaTableModel.setColumnCount(0);
        etapaTableModel.addColumn("Etapa");
        etapaTableModel.addColumn("Duración");
        etapaTableModel.addColumn("Pais");
        etapaTableModel.addColumn("Ciudad");
        etapaTableModel.addColumn("Hotel");
        etapaTableModel.addColumn("Lugar a Visitar");
        etapaTablecolumnModel = etapaTable.getColumnModel();
        etapaDisplayTable.setFillsViewportHeight(true);
        tableScrollPane.setViewportView(etapaTable);
        llenarComboPais(paisEtapaCombobox);

        //Size of the column
        etapaTablecolumnModel.getColumn(0).setPreferredWidth(15);
        etapaTablecolumnModel.getColumn(1).setPreferredWidth(15);


        rowCount = etapaDisplayTable.getRowCount();
        //Remove rows one by one from the end of the table
        for (int i = rowCount-1; i >= 0; i--) {
            etapaTableModel.removeRow(i);
        }
    }

    public void llenarComboHotel(JComboBox comboBoxCiudad, JComboBox comboBoxHotel){
        comboBoxHotel.removeAllItems();
        if(comboBoxCiudad.getSelectedItem() != null) {
            String ciudad = String.valueOf(comboBoxCiudad.getSelectedItem());
            ArrayList<String[]> results = null;
            try
            {
                results = transactionManager.query("select h.nombre, direccion " +
                        "from ciudad c, hotel h " +
                        "where c.nombre = h.ciudad and c.pais = h.pais and " +
                        "c.nombre = \""+ ciudad + "\";");
            } catch (Exception ex)
            {
                ex.printStackTrace();
            }
            if (results.isEmpty())
            {
                return;
            }
            for (String[] hotel : results)
            {
                comboBoxHotel.addItem(hotel[0] + "," + hotel[1]);
            }
            comboBoxHotel.setSelectedItem(null);
        }
        return;
    }

    private void llenarComboLugarAVisitar(JComboBox comboBoxCiudad, JComboBox comboBoxLugarAVisitar){
        comboBoxLugarAVisitar.removeAllItems();
        if(comboBoxCiudad.getSelectedItem() != null) {
            String ciudad = String.valueOf(comboBoxCiudad.getSelectedItem());
            ArrayList<String[]> results = null;
            try
            {
                results = transactionManager.query("select l.nombre " +
                        "from ciudad c, lugarAVisitar l " +
                        "where c.nombre = l.ciudad and c.pais = l.pais and " +
                        "c.nombre = \""+ ciudad + "\";");
            } catch (Exception ex)
            {
                ex.printStackTrace();
            }
            if (results.isEmpty())
            {
                return;
            }
            for (String[] lugarAVisitar : results)
            {
                comboBoxLugarAVisitar.addItem(lugarAVisitar[0]);
            }
            comboBoxLugarAVisitar.setSelectedItem(null);
        }
        return;
    }

    public void llenarComboPais(JComboBox comboBoxPais)
    {
        comboBoxPais.removeAllItems();
        ArrayList<String[]> results = null;
        try {
            results = transactionManager.query("select distinct pais from ciudad;");
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
        if(results.isEmpty()){
            return;
        }

        for(String[] pais : results){
            comboBoxPais.addItem(pais[0]);
        }
        comboBoxPais.setSelectedItem(null);
    }

    public void llenarComboCiudad(JComboBox comboBoxPais, JComboBox comboBoxCiudad) {
        comboBoxCiudad.removeAllItems();
        if(comboBoxPais.getSelectedItem() != null) {
            String pais = String.valueOf(comboBoxPais.getSelectedItem());
            ArrayList<String[]> results = null;
            try
            {
                results = transactionManager.query("select nombre from ciudad where pais = \""+ pais + "\";");
            } catch (Exception ex)
            {
                ex.printStackTrace();
            }
            if (results.isEmpty())
            {
                return;
            }
            for (String[] ciudad : results)
            {
                comboBoxCiudad.addItem(ciudad[0]);
            }
            comboBoxCiudad.setSelectedItem(null);
        }
        return;
    }

    private void resetEtapaSelector(){
        duracionText.setText("");
        llenarComboPais(paisEtapaCombobox);
        ciudadEtapaCombobox.setEnabled(false);
        hotelEtapaCombobox.setEnabled(false);
        lugarEtapaCombobox.setEnabled(false);
    }

    ActionListener ciudadTablaListener = new ActionListener() {
        @Override
        public void actionPerformed (ActionEvent e){
            drawConsultTable("ciudad"); }
    };

    private void drawConsultTable(String type) {
        if (type.equals("circuito")) {
            String[] columnsToUse = {"Ciudad de Salida", "Ciudad de Llegada", "Duración", "Precio"};
            ArrayList<String[]> results = null;
            try {
                results = transactionManager.query("select ciudadSalida, " +
                        "ciudadLlegada, duracion, precio from circuito;");
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
            InfoConsulta.setViewportView(table1);

        }
        if (type.equals("ciudad")) {
            if (ciudadCombo.getSelectedItem() != null) {
                String ciudad = String.valueOf(ciudadCombo.getSelectedItem());
                String[] columnsToUse = {"Hotel", "Dirección", "Precio de cuarto", "Precio de desayuno"};
                ArrayList<String[]> results = null;
                try {
                    results = transactionManager.query("select nombre, direccion, precioCuarto,precioDesayuno from hotel where ciudad = \"" + ciudad + "\";");
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
                InfoConsulta.setViewportView(table1);
            }
        }
        if (type.equals("país")) {
            if (paisCombo.getSelectedItem() != null) {
                String pais = String.valueOf(paisCombo.getSelectedItem());
                String[] columnsToUse = {"Ciudad", "Hotel", "Dirección", "Precio de cuarto", "Precio de desayuno"};
                ArrayList<String[]> results = null;
                try {
                    results = transactionManager.query("select ciudad, nombre, direccion, " +
                            "precioCuarto,precioDesayuno " +
                            "from hotel " +
                            "where pais = \"" + pais + "\";");
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
                InfoConsulta.setViewportView(table1);
            }
        }
    }

    ActionListener crearSimulacionListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            boolean noSQLErrors = true;
            mensajeSim.setText("");
            if(activeUser.equals("Invitado") && !nombreCompletoLabel.isVisible()){
                nombreCompletoLabel.setVisible(true);
                nombreCompletoText.setVisible(true);
                mensajeSim.setText("Ingrese su nombre completo y vuelva a simular");
                return;
            }
            String nombreCompleto;
            if(activeUser.equals("Invitado")){
                nombreCompleto = nombreCompletoText.getText();
            }
            else{
                nombreCompleto = activeUser;
            }

            //const de fechaCircuito
            String fechaSalida = anoComboBox.getSelectedItem().toString() +  "-"
                    + mesComboBox.getSelectedItem().toString() +  "-" +
                    diaComboBox.getSelectedItem().toString();
            if(nPersonasText.getText().equals("")){
                mensajeSim.setText("Inserte un numero de personas");
                return;
            }
            String numPersonas = nPersonasText.getText();
            String claveFecha =  Character.toString(fechaSalida.charAt(1)) + Character.toString(numPersonas.charAt(0)) +
                    "-" + Character.toString(nombreCompleto.charAt(0)) + Character.toString(nombreCompleto.charAt(1));

            //extraer tabla de etapas
            String[][] etapas = new String[etapaTableModel.getRowCount()][etapaTableModel.getColumnCount() + 1];
            for(int i=0;i<etapaTableModel.getRowCount();i++){
                for(int j=1;j<etapaTableModel.getColumnCount() + 1;j++){
                    etapas[i][j] = etapaTable.getValueAt(i,j-1).toString();
                }
                etapas[i][0] = etapas[i][3].charAt(0) + Character.toString(numPersonas.charAt(0)) + etapas[i][1].charAt(0) +
                        nombreCompleto.charAt(0) + nombreCompleto.charAt(1);
            }

            //const de circuito
            int duracionCircuito = 0;
            int precioCircuito = 0;
            String[][] fechasEtapas = new String[etapas.length][2];
            String fechaInicio = fechaSalida.toString();
            String fechaTermino = fechaInicio.toString();

            for(int i=0;i<etapaTableModel.getRowCount();i++){
                duracionCircuito += Integer.parseInt(etapas[i][2]);
                String[] hotel = etapas[i][5].split(",");
                ArrayList<String[]> resultado = new ArrayList<>();
                try{
                    resultado = transactionManager.query("select * from hotel " +
                            "where nombre = \"" + hotel[0] + "\" and direccion = \"" + hotel[1] + "\";");
                }
                catch (Exception ex){
                    ex.printStackTrace();
                    noSQLErrors = false;
                }
                precioCircuito += (Integer.parseInt(resultado.get(0)[5]) + Integer.parseInt(resultado.get(0)[6]))
                        * Integer.parseInt(etapas[i][2]) * Integer.parseInt(numPersonas);

                ArrayList<String[]> reservas = new ArrayList<>();

                try{
                    Calendar calendar = Calendar.getInstance();
                    fechaInicio = fechaTermino.toString();
                    calendar.setTime(dateFormat.parse(fechaInicio));
                    calendar.add(Calendar.DATE, Integer.parseInt(etapas[i][2]));
                    fechaTermino = dateFormat.format(calendar.getTime());
                    fechasEtapas[i][0] = fechaInicio;
                    fechasEtapas[i][1] = fechaTermino;

                    reservas = transactionManager.query("select sum(numPersonas)" +
                            "from reservaHotel " +
                            "where nombreHotel = \"" + hotel[0] +  "\" and direccion = \"" + hotel[1] + "\" and " +
                            "(fechaInicio between '" + fechaInicio + "' and '" + fechaTermino + "' or fechaTermino between " +
                            "'" + fechaInicio + "' and '" + fechaTermino +"');");
                    if(reservas.get(0)[0] != null) {
                        int numCuartos = Integer.parseInt(resultado.get(0)[4]);
                        int cuartosDisponibles = numCuartos - Integer.parseInt(reservas.get(0)[0]);
                        System.out.println(cuartosDisponibles);
                        if (cuartosDisponibles < Integer.parseInt(numPersonas)) {
                            mensajeSim.setText("No hay cupo para el hotel " + hotel[0] + ", " + hotel[1] + " en las fechas" +
                                    " " + fechaInicio + " a " + fechaTermino);
                            transactionManager.conn.rollback();
                            return;
                        }
                    }

                    //insert etapa into database
                    transactionManager.stmt.executeUpdate("insert into etapa values (\"" + etapas[i][0] + "\", " +
                            etapas[i][1] + ", '" + etapas[i][6] + "', '" + etapas[i][4] + "', '" + etapas[i][3] + "', " +
                            etapas[i][2] + ");");
                }
                catch(Exception ex){
                    ex.printStackTrace();
                    noSQLErrors = false;
                }
            }

            String identificadorCircuito = nombreCompleto.charAt(0) + Character.toString(nombreCompleto.charAt(1))
                    + etapas[0][0].charAt(0) + Character.toString(fechaSalida.charAt(fechaSalida.length()-1)) +
                    fechaSalida.charAt(3);
            String descripcionCircuito = "Viaje de " + duracionCircuito + " dias por "
                    + etapas[0][4] + " y otros";
            System.out.println(descripcionCircuito);

            try{
                //verificar descuento de agencia
                ArrayList<String[]> cliente = new ArrayList<>();
                cliente = transactionManager.query("select categoria from clienteregistrado " +
                        "where nombreCompleto = '" + nombreCompleto + "';");
                if(!cliente.isEmpty() && cliente.get(0)[0].equals("agencia")){
                    System.out.println("Descuento!");
                    precioCircuito *= descuentoAgencia;
                }

                //insertar circuito
                transactionManager.stmt.executeUpdate("insert into Circuito values ('" + identificadorCircuito + "', " +
                        "'" + descripcionCircuito + "', '" + etapas[0][4] + "', '" + etapas[0][3] + "', '" + etapas[etapas.length-1][4]
                        + "', '" + etapas[etapas.length-1][3] + "', " + duracionCircuito + ", " + precioCircuito + ");");
                //unir etapas y circuito en tabla conformado
                for(int i=0;i<etapas.length;i++){
                    transactionManager.stmt.executeUpdate("insert into conformado values ('" + identificadorCircuito +
                            "', '" + etapas[i][0] + "');");
                }
                //insertar fechacircuito
                transactionManager.stmt.executeUpdate("insert into fechaCircuito values ('" + claveFecha + "', '" +
                        fechaSalida + "', " + numPersonas + ");");
                //unir fechacircuito y circuito en tabla comienza
                transactionManager.stmt.executeUpdate("insert into comienza values ('" + identificadorCircuito + "', '" +
                       claveFecha + "');");
            }
            catch (Exception ex){
                ex.printStackTrace();
                noSQLErrors = false;
            }

            //const de simulacion
            Calendar calendar = Calendar.getInstance();
            try {
                calendar.setTime(dateFormat.parse(fechaSalida));
            }
            catch (Exception ex){
                ex.printStackTrace();
                noSQLErrors = false;
            }
            calendar.add(Calendar.DATE, duracionCircuito);
            String fechaLlegada = dateFormat.format(calendar.getTime());
            String fechaSim = dateFormat.format(Calendar.getInstance().getTime());
            String simCode = fechaLlegada.charAt(3) + Character.toString(nombreCompleto.charAt(nombreCompleto.length() - 1))
                    + Character.toString(fechaLlegada.charAt(fechaLlegada.length()-1))
                    + nombreCompleto.charAt(1) + nombreCompleto.charAt(2);
            try{
                //insertar simulacion
                transactionManager.stmt.executeUpdate("insert into simulacion values ('" + simCode + "', '"
                + fechaSim + "', '" + fechaLlegada + "', '" + fechaSalida + "', " + numPersonas + ", '"
                + nombreCompleto + "', '" + identificadorCircuito + "');");
                //unir simulacion y hotel en tabla reservahotelsimulado

                for(int i=0;i<etapas.length;i++){
                    String[] hotel = etapas[i][5].split(",");
                    transactionManager.stmt.executeUpdate("insert into reservaHotelSimulado values ('" +
                            hotel[0] + "', '" + hotel[1] + "', '" + simCode + "', '" + etapas[i][0] + "', '"
                            + fechasEtapas[i][0] + "', '" + fechasEtapas[i][1] + "', " + numPersonas + ");");
                }
            }
            catch(Exception ex){
                ex.printStackTrace();
                noSQLErrors = false;
            }

            try {
                if(noSQLErrors) {
                    transactionManager.conn.commit();
                    mensajeSim.setText("La simulacion fue validada, tome su clave de simulacion y pase a reservar");
                    claveSimulacion.setText(simCode);
                }else{
                    transactionManager.conn.rollback();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            rowCount = etapaTable.getRowCount();
            //Remove rows one by one from the end of the table
            for (int i = rowCount-1; i >= 0; i--) {
                etapaTableModel.removeRow(i);
            }
        }
    };

    private void borrarSimulacionesViejas(){
        try{
            System.out.println("Eliminando simulaciones viejas...");
            ArrayList<String[]> simulacionesABorrar = new ArrayList<>();
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DATE, -2);
            String fechaLimite = dateFormat.format(calendar.getTime());
            simulacionesABorrar = transactionManager.query("select codigoSim, identificadorCircuito" +
                    " from simulacion " +
                    "where fechaSim < '" + fechaLimite + "';");
            if(!simulacionesABorrar.isEmpty()){
                for(String[] simulacionABorrar : simulacionesABorrar) {
                    //borrar reservaciones de hotel simuladas
                    transactionManager.stmt.executeUpdate("delete from reservahotelsimulado " +
                            "where codigoSim = '" + simulacionABorrar[0] + "';");
                    //borrar etapas
                    ArrayList<String[]> etapas = new ArrayList<>();
                    etapas = transactionManager.query("Select identificador from etapa, conformado " +
                            " where identificadoreta = identificador and identificadorCircuito = '" + simulacionABorrar[1]
                            + "';");
                    transactionManager.stmt.executeUpdate("delete from conformado where identificadorCircuito = '"
                    + simulacionABorrar[1] + "';");
                    for(String[] etapa : etapas) {
                        transactionManager.stmt.executeUpdate("delete from etapa where identificador = '" +
                                etapa[0] + "';");
                    }

                    //borrar fechainicio
                    ArrayList<String[]> fecha = new ArrayList<>();
                    fecha = transactionManager.query("Select identificador from fechaCircuito, comienza " +
                            " where identificadorfecha = identificador and identificadorCircuito = '" + simulacionABorrar[1]
                            + "';");
                    transactionManager.stmt.executeUpdate("delete from comienza where identificadorCircuito = '"
                            + simulacionABorrar[1] + "';");
                    transactionManager.stmt.executeUpdate("delete from fechaCircuito where identificador = '" +
                            fecha.get(0)[0] + "';");

                    //borrar simulaciones
                    transactionManager.stmt.executeUpdate("delete from simulacion " +
                            "where codigoSim = '" + simulacionABorrar[0] + "';");

                    //borrar circuito
                    transactionManager.stmt.executeUpdate("delete from circuito where identificador = '" +
                            simulacionABorrar[1] + "';");
                }
            }
            transactionManager.conn.commit();
            System.out.println("Simulaciones viejas eliminadas!");
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
    }
}
