package com.example.executablelauncher;

import com.example.executablelauncher.entities.Disc;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Font;
import javafx.stage.Screen;

public class DiscController {
    @FXML
    private Label name;
    @FXML
    private Label number;
    private Disc disc;
    private SeasonController parentController = null;

    public void setData(Disc d) {
        name.setText(d.getName());
        number.setText(App.textBundle.getString("episode") + " " + d.getEpisodeNumber());
        disc = d;

        double screenWidth = Screen.getPrimary().getBounds().getWidth();

        if (screenWidth < 1920){
            name.setFont(new Font("System", 20));
            number.setFont(new Font("System", 14));
        }else if (screenWidth >= 2048){
            name.setFont(new Font("System", 26));
            number.setFont(new Font("System", 20));
        }
    }

    public void setParent(SeasonController col){
        parentController = col;
    }

    @FXML
    private void play(KeyEvent event) {
        if (parentController != null)
            parentController.playEpisode(disc);
    }
}