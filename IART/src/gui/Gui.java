package gui;

import parser.cityParser;

import java.io.IOException;

/**
 * Created by Rui on 23-May-16.
 */
public class Gui {
    public static void main(String[] args) throws IOException {
        cityParser parser = new cityParser();
        GuiMain gui = new GuiMain(parser);
    }
}
