package br.edu.ifspsaocarlos.comunicaifsp;

import java.io.Serializable;

/**
 * Created by MRissi on 14-Nov-17.
 */

public class Message implements Serializable{
    String message;
    String date;
    String title;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTitle() { return title; }

    public void setTitle(String title) { this.title = title; }
}
