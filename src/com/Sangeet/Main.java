package com.Sangeet;

import com.formdev.flatlaf.FlatDarkLaf;
import com.Sangeet.ui.LoginFrame;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        FlatDarkLaf.setup();
        SwingUtilities.invokeLater(() -> {
            LoginFrame mf = new LoginFrame();
            mf.setVisible(true);
        });
    }
}
