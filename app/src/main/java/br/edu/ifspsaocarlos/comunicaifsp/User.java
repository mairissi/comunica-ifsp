package br.edu.ifspsaocarlos.comunicaifsp;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by MRissi on 13-Sep-17.
 */

public class User implements Serializable{

    private String idUser;
    private String name;
    private String cpf;
    private String ra;
    private String email;
    private String password;
    private HashMap<String, Topic> signedTopicsList;


    public User() {}

    public HashMap<String, Topic> getSignedTopicsList() {
        return signedTopicsList;
    }

    public void setSignedTopicsList(HashMap<String, Topic> signedTopicsList) {
        this.signedTopicsList = signedTopicsList;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private void setNameInMap(Map<String, Object> map) {
        if (getName() != null) {
            map.put("name", getName());
        }
    }
    public void setNameIfNull(String name) {
        if (this.name == null) {
            this.name = name;
        }
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    private void setCpfInMap(Map<String, Object> map) {
        if (getCpf() != null) {
            map.put("cpf", getCpf());
        }
    }
    public void setCpfIfNull(String cpf) {
        if (this.cpf == null) {
            this.cpf = cpf;
        }
    }

    public String getRa() {
        return ra;
    }

    public void setRa(String ra) {
        this.ra = ra;
    }

    private void setRaInMap(Map<String, Object> map) {
        if (getRa() != null) {
            map.put("ra", getRa());
        }
    }
    public void setRaIfNull(String ra) {
        if (this.ra == null) {
            this.ra = ra;
        }
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    private void setEmailInMap(Map<String, Object> map) {
        if (getEmail() != null) {
            map.put("email", getEmail());
        }
    }
    public void setEmailIfNull(String email) {
        if (this.email == null) {
            this.email = email;
        }
    }

    @Exclude
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void saveDB(DatabaseReference.CompletionListener... completionListener) {
        DatabaseReference firebase = LibraryClass.getFirebase().child("users").child(getIdUser().toString());
        if (completionListener.length == 0) {
            firebase.setValue(this);
        } else {
            firebase.setValue(this, completionListener[0]);
        }
    }
}
