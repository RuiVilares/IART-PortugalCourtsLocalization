package gui;

import algorithms.GeneticAlgorithm;
import algorithms.SimulatedAnnealing;
import location.Place;
import parser.cityParser;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.util.Arrays;
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
                    JOptionPane.showMessageDialog(GuiMain.this, text, "Info", JOptionPane.INFORMATION_MESSAGE);
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
                Vector<Place> places = parser.getCities();

                GeneticAlgorithm ga = new GeneticAlgorithm(places, 50, 5, 20, 100, 0.5, 25, 75 );
                ga.compute();

                int best = ga.getBestScore();
                places = ga.getBestChoice();

                parser.javascriptFileConstructor(places, "geneticAlgorithm.html");
            }
        });
        simulatedAnnealingButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Vector<Place> places = parser.getCities();

                SimulatedAnnealing sa = new SimulatedAnnealing(places, 50, 50, 1, 1);
                sa.compute();

                int best = sa.getBestScore();
                places = sa.getBestChoice();

                parser.javascriptFileConstructor(places, "simulatedAnnealing.html");
            }
        });
    }
}
