import com.formdev.flatlaf.FlatDarkLaf;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

/**
 * La classe Form1, sottoclasse di JFrame, gestisce l'inserimento, l'organizzazione e la visualizzazione dei dati di un dizionario italiano-inglese.
 * I dati sono memorizzati in un ArrayList (dizionario) di oggetti Voce e vengono salvati su due file con organizzazione ISAM (Indexed Sequential Access Method).
 * E' possibile ordinare il dizionario per italiano o per inglese e in ordine crescente o decrescente.
 * @author Andrea
 * @version 1.0
 */
public class Form1 extends JFrame{
    private JPanel panel1;
    private JList<Voce> lstVoci;
    private JTabbedPane tabbedPane1;
    private JButton btnAggiungi;
    private JTextField txtInglese;
    private JTextField txtItaliano;
    private JComboBox<String> cmbOrdTipo;
    private JComboBox<String> cmbOrd;
    private JButton btnSalva;

    /**
     * ArrayList di tipo Voci
     */
    private final ArrayList<Voce> dizionario;

    /**
     * File sequenziale (di testo) contenente le voci del dizionario con i relativi indici
     */
    private final File fileIndice;

    /**
     * File binario contenente le voci del dizionario
     */
    private final File fileRecord;

    /**
     * L'indice corrente del dizionario (inizio da 0)
     */
    private static int indice = 0;

    /**
     * Costruttore della classe Form1
     */
    public Form1()
    {
        dizionario = new ArrayList<>();
        fileRecord = new File(Objects.requireNonNull(getClass().getResource("dizionario")).getFile());
        fileIndice = new File(Objects.requireNonNull(getClass().getResource("indice")).getFile());

        caricaDizionario();
        ordinamento("inglese", true);

        cmbOrdTipo.addItem("inglese");
        cmbOrdTipo.addItem("italiano");

        cmbOrd.addItem("crescente");
        cmbOrd.addItem("decrescente");


        btnAggiungi.addActionListener(new ActionListener() {
            /**
             * Invoked when an action occurs.
             * Aggiunge una nuova voce al dizionario
             * @param e the event to be processed
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                // Aggiungo la voce al dizionario
                try
                {
                    if (!(txtInglese.getText().isEmpty() || txtItaliano.getText().isEmpty()))
                    {
                        // Controllo se la voce esiste già
                        if (dizionario.stream().anyMatch(x -> x.getItaliano().equals(txtItaliano.getText()) || x.getInglese().equals(txtInglese.getText())))
                            throw new Exception("La voce esiste già");

                        Voce voce = new Voce(txtItaliano.getText(), txtInglese.getText());

                        dizionario.add(voce);
                        aggiungiVoce(voce);
                        aggiungiRecord(voce);

                        // Aggiorno la lista
                        lstVoci.setListData(dizionario.toArray(new Voce[0]));
                    }else
                        throw new Exception("I campi non possono essere vuoti");

                }catch (Exception ex)
                {
                    JOptionPane.showMessageDialog(null, ex.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    cmbOrdTipo.addActionListener(
        new ActionListener() {
          /**
           * Invoked when an action occurs.
           * Ordina il dizionario.
           * @param e the event to be processed
           */
          @Override
          public void actionPerformed(ActionEvent e) {
              try
              {
                  // Ordino il dizionario
                  ordinamento((String) Objects.requireNonNull(cmbOrdTipo.getSelectedItem()), Objects.equals(cmbOrd.getSelectedItem(), "crescente"));
              }catch (Exception ex)
              {
                  JOptionPane.showMessageDialog(null, ex.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
              }
          }
        });
        cmbOrd.addActionListener(new ActionListener() {
            /**
             * Invoked when an action occurs.
             * Ordina il dizionario
             * @param e the event to be processed
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                try
                {
                    // Ordino il dizionario
                    ordinamento((String) Objects.requireNonNull(cmbOrdTipo.getSelectedItem()), Objects.equals(cmbOrd.getSelectedItem(), "crescente"));
                }catch (Exception ex)
                {
                    JOptionPane.showMessageDialog(null, ex.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        btnSalva.addActionListener(new ActionListener() {
            /**
             * Invoked when an action occurs.
             * Salvo il dizionario ordinato sui file
             * @param e the event to be processed
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                try
                {
                    if(dizionario.isEmpty())
                        throw new Exception("Il dizionario è vuoto");

                    salvaDizionario();

                }catch (Exception ex)
                {
                    JOptionPane.showMessageDialog(null, ex.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        lstVoci.addMouseListener(new MouseAdapter() {
            /**
             * {@inheritDoc}
             * Elimina la voce selezionata dal dizionario
             * @param e
             */
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    // Elimino la voce dal dizionario
                    try {
                        if (lstVoci.getSelectedIndex() != -1) {
                            dizionario.remove(lstVoci.getSelectedIndex());
                            indice--;
                            // Aggiorno la lista
                            lstVoci.setListData(dizionario.toArray(new Voce[0]));
                        }

                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(null, ex.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
    }

    /**
     * Salva il dizionario su file
     * @throws IOException Eccezione generata in caso di errore di I/O
     */
    private void salvaDizionario() throws Exception
    {
        if(fileIndice.length() == 0)
            throw new Exception("I file sono vuoti");

        //Elimino il file contenente l'indice
        if(!fileIndice.delete()) throw new Exception("Impossibile eliminare il file di testo contenente le voci con l'indice: " + fileIndice.getName());

        // Ricreo il file
        if(!fileIndice.createNewFile()) throw new Exception("Impossibile creare il file di testo contenente le voci con l'indice: " + fileIndice.getName());

        indice = 0;

        // Scrivo il dizionario su file
        for(Voce voce : dizionario)
        {
            aggiungiVoce(voce);
        }
    }

    /**
     * Carica il dizionario da file e lo aggiunge alla lista
     */
    private void caricaDizionario()
    {

        try
        {
            if (fileIndice.length() != 0) {
                BufferedReader bufferedReader = new BufferedReader(new FileReader(fileIndice));

                // Aggiungo le voci al dizionario
                for(String linea : bufferedReader.lines().toList())
                {
                    String[] campi = linea.split(Voce.DELIMITATORE);
                    dizionario.add(new Voce(campi[0], campi[1]));
                }

                bufferedReader.close();

                // Aggiorno la lista
                lstVoci.setListData(dizionario.toArray(new Voce[0]));
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Aggiunge una voce dal dizionario sul file sequenziale in append
     * @param voce Voce da aggiungere
     * @throws Exception Eccezione generata in caso di errore di I/O
     */
    private void aggiungiVoce(Voce voce) throws Exception
    {
        // Aggiungo la voce al dizionario

        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(fileIndice, true));

        bufferedWriter.write(voce.getItaliano() + Voce.DELIMITATORE + voce.getInglese() + Voce.DELIMITATORE + indice + "\n");
        indice++;

        bufferedWriter.close();
    }

    /**
     * Ordina il dizionario in base al tipo di ordinamento e alla direzione
     * @param tipo Tipo di ordinamento
     * @param crescente Direzione dell'ordinamento
     */
    private void ordinamento(String tipo, boolean crescente)
    {
        dizionario.sort(Comparator.comparing(tipo.equals("italiano")?Voce::getItaliano:Voce::getInglese));

        if (!crescente)
            Collections.reverse(dizionario);

        lstVoci.setListData(dizionario.toArray(new Voce[0]));
    }

    /**
     * Aggiunge un record al file binario in append
     * @param voce Voce da aggiungere
     * @throws Exception Eccezione generata in caso di errore di I/O
     */
    private void aggiungiRecord(Voce voce) throws Exception
    {
        // Aggiungo il record all'indice

        RandomAccessFile randomAccessFile = new RandomAccessFile(fileRecord, "rw");

        randomAccessFile.seek(randomAccessFile.length());

        randomAccessFile.writeUTF(voce.toString());

        randomAccessFile.close();
    }

    public static void main(String[] args)
    {
        FlatDarkLaf.setup();
        JFrame frame = new Form1();
        frame.setTitle("Dizionario");
        frame.setContentPane(new Form1().panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setSize(new Dimension(720,640));
        frame.setResizable(false);
        frame.setVisible(true);
    }
}
