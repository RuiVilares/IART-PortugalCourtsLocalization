package gui;

import com.teamdev.jxbrowser.chromium.Browser;
import com.teamdev.jxbrowser.chromium.events.FinishLoadingEvent;
import com.teamdev.jxbrowser.chromium.events.LoadAdapter;
import com.teamdev.jxbrowser.chromium.swing.BrowserView;

import algorithms.GeneticAlgorithm;
import algorithms.SimulatedAnnealing;
import location.Place;
import parser.cityParser;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Vector;

/**
 * Created by Rui on 23-May-16.
 */
public class GuiMain extends JFrame{
    private JPanel panel;
    private JButton loadFromFileButton;
    private JButton loadFromWebButton;
    private JButton exitButton;
    private JButton saveCitiesButton;
    private JList list1;
    private JButton geneticAlgorithmButton;
    private JButton simulatedAnnealingButton;
    private JScrollBar scrollBar1;
    private JLabel label;

    public GuiMain(cityParser parser) {
        super("Courts in Portugal");
        setContentPane(panel);
        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        MouseListener mouseListener = new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && parser.getCities().size() > 0) {
                    int selectedItem = list1.getSelectedIndex();
                    String text = "Name: " + parser.getCities().get(selectedItem).getName() +
                            "\nLongitude: " + parser.getCities().get(selectedItem).getCoord_x() +
                            "\nLatitude: " + parser.getCities().get(selectedItem).getCoord_y() +
                            "\nPrice: " + parser.getCities().get(selectedItem).getPrice() +
                            "\nPopulation: " + parser.getCities().get(selectedItem).getCitizens();
                    JOptionPane.showMessageDialog(GuiMain.this, text, "City", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        };

        loadFromFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                parser.getCitiesByFile();
                list1.setListData(parser.getCities());
                list1.addMouseListener(mouseListener);
                JOptionPane.showMessageDialog(GuiMain.this, "Cities successful loaded from file", "Success", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        setVisible(true);
        loadFromWebButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    parser.getCitiesByWeb();
                    list1.setListData(parser.getCities());
                    list1.addMouseListener(mouseListener);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                list1.setListData(parser.getCities());
                list1.addMouseListener(mouseListener);
                JOptionPane.showMessageDialog(GuiMain.this, "Cities successful loaded from web", "Success", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        saveCitiesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (parser.getCities().size() > 0) {
                    parser.saveCities();
                    JOptionPane.showMessageDialog(GuiMain.this, "Cities successful saved", "Success", JOptionPane.INFORMATION_MESSAGE);
                }
                else {
                    JOptionPane.showMessageDialog(GuiMain.this, "You need load cities", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        geneticAlgorithmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if (parser.getCities().size() > 0) {
                    JFrame main = new JFrame("Genetic Algorithm");
                    main.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                    JTextField nrCourts = new JTextField("50", 2);
                    JTextField bestToPass = new JTextField("5", 2);
                    JTextField generationSize = new JTextField("20", 2);
                    JTextField iterations = new JTextField("500", 2);
                    JTextField dist = new JTextField("0.5", 2);
                    JTextField pbMutation = new JTextField("25", 2);
                    JTextField pbMarriage = new JTextField("75", 2);
                    JTextField iterationsBeforeStop = new JTextField("0.5", 2);


                    JPanel gui = new JPanel(new BorderLayout(3,3));
                    gui.setBorder(new EmptyBorder(5,5,5,5));
                    main.setContentPane(gui);

                    JPanel labels = new JPanel(new GridLayout(0,1));
                    JPanel controls = new JPanel(new GridLayout(0,1));
                    gui.add(labels, BorderLayout.WEST);
                    gui.add(controls, BorderLayout.CENTER);

                    labels.add(new JLabel("Nr courts: "));
                    controls.add(nrCourts);
                    labels.add(new JLabel("Best to pass: "));
                    controls.add(bestToPass);
                    labels.add(new JLabel("Generation size: "));
                    controls.add(generationSize);
                    labels.add(new JLabel("Iterations: "));
                    controls.add(iterations);
                    labels.add(new JLabel("Dist: "));
                    controls.add(dist);
                    labels.add(new JLabel("Pb mutation: "));
                    controls.add(pbMutation);
                    labels.add(new JLabel("Pb marriage: "));
                    controls.add(pbMarriage);
                    labels.add(new JLabel("Iterations before stop: "));
                    controls.add(iterationsBeforeStop);

                    JButton submit = new JButton("Submit");

                    gui.add(submit, BorderLayout.SOUTH);
                    main.pack();
                    main.setVisible(true);

                    GuiMain.super.setVisible(false);

                    submit.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {

                            try {
                                int nc = Integer.parseInt(nrCourts.getText());
                                int bp = Integer.parseInt(bestToPass.getText());
                                int gs = Integer.parseInt(generationSize.getText());
                                int i = Integer.parseInt(iterations.getText());
                                double d = Double.parseDouble(dist.getText());
                                int pm = Integer.parseInt(pbMutation.getText());
                                int pma = Integer.parseInt(pbMarriage.getText());
                                double ibs = Double.parseDouble(iterationsBeforeStop.getText());

                                Vector<Place> places = parser.getCities();

                                GeneticAlgorithm ga = new GeneticAlgorithm(places, nc, bp, gs, i, d, pm, pma, ibs);
                                ga.compute();

                                int best = ga.getBestScore();
                                places = ga.getBestChoice();

                                parser.javascriptFileConstructor(places, "geneticAlgorithm.html");

                                GuiMain.super.setVisible(true);

                                try {
                                    showWindow("Genetic Algorithm", "geneticAlgorithm.html");
                                } catch (IOException e1) {
                                    e1.printStackTrace();
                                }
                                main.setVisible(false);
                                JOptionPane.showMessageDialog(GuiMain.this, "Score: " + best, "Done", JOptionPane.INFORMATION_MESSAGE);
                            }
                            catch (NumberFormatException nfe){
                                JOptionPane.showMessageDialog(GuiMain.this, "Enter valid values", "Error", JOptionPane.ERROR_MESSAGE);
                            }
                        }
                    });
                }
                else {
                    JOptionPane.showMessageDialog(GuiMain.this, "You need load cities", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        simulatedAnnealingButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (parser.getCities().size() > 0) {
                    JFrame main = new JFrame("Simulated Annealing");

                    main.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                    JTextField nrCourts = new JTextField("50", 2);
                    JTextField initialTemperature = new JTextField("50", 2);
                    JTextField delta = new JTextField("0.01", 2);
                    JTextField dist = new JTextField("1", 2);
                    JTextField iterationsBeforeStop = new JTextField("0.5", 2);


                    JPanel gui = new JPanel(new BorderLayout(3,3));
                    gui.setBorder(new EmptyBorder(5,5,5,5));
                    main.setContentPane(gui);

                    JPanel labels = new JPanel(new GridLayout(0,1));
                    JPanel controls = new JPanel(new GridLayout(0,1));
                    gui.add(labels, BorderLayout.WEST);
                    gui.add(controls, BorderLayout.CENTER);

                    labels.add(new JLabel("Nr courts: "));
                    controls.add(nrCourts);
                    labels.add(new JLabel("Initial temperature: "));
                    controls.add(initialTemperature);
                    labels.add(new JLabel("Delta: "));
                    controls.add(delta);
                    labels.add(new JLabel("Dist: "));
                    controls.add(dist);
                    labels.add(new JLabel("Iterations before stop: "));
                    controls.add(iterationsBeforeStop);

                    JButton submit = new JButton("Submit");

                    gui.add(submit, BorderLayout.SOUTH);
                    main.pack();
                    main.setVisible(true);

                    GuiMain.super.setVisible(false);

                    submit.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            try {
                                int nc = Integer.parseInt(nrCourts.getText());
                                double it = Double.parseDouble(initialTemperature.getText());
                                double del = Double.parseDouble(delta.getText());
                                double d = Double.parseDouble(dist.getText());
                                double ibs = Double.parseDouble(iterationsBeforeStop.getText());

                                Vector<Place> places = parser.getCities();

                                SimulatedAnnealing sa = new SimulatedAnnealing(places, nc, it, del, d, ibs);
                                sa.compute();

                                int best = sa.getBestScore();
                                places = sa.getBestChoice();

                                parser.javascriptFileConstructor(places, "simulatedAnnealing.html");

                                GuiMain.super.setVisible(true);

                                try {
                                    showWindow("Simulated Annealing", "simulatedAnnealing.html");
                                } catch (IOException e1) {
                                    e1.printStackTrace();
                                }
                                main.setVisible(false);
                                JOptionPane.showMessageDialog(GuiMain.this, "Score: " + best, "Done", JOptionPane.INFORMATION_MESSAGE);
                            }
                            catch (NumberFormatException nfe){
                                JOptionPane.showMessageDialog(GuiMain.this, "Enter valid values", "Error", JOptionPane.ERROR_MESSAGE);
                            }
                        }
                    });
                }
                else {
                    JOptionPane.showMessageDialog(GuiMain.this, "You need load cities", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    public void showWindow(String title, String filename) throws IOException {
        Browser browser = new Browser();
        BrowserView browserView = new BrowserView(browser);

        JFrame frame = new JFrame(title);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.add(browserView, BorderLayout.CENTER);
        frame.setSize(700, 500);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        browser.addLoadListener(new LoadAdapter() {
            @Override
            public void onFinishLoadingFrame(FinishLoadingEvent event) {
                if (event.isMainFrame()) {
                    System.out.println("HTML is loaded.");
                }
            }
        });

        String HTML = new String(Files.readAllBytes(Paths.get(filename)), StandardCharsets.UTF_8);

        browser.loadHTML(HTML);
    }
}
