package br.edu.ifspsaocarlos.comunicaifsp.model.entity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

import java.io.Serializable;
import java.util.Map;

import br.edu.ifspsaocarlos.comunicaifsp.model.callback.TopicPresenter;

/**
 * Created by MRissi on 26-Sep-17.
 */

public class Topic implements Serializable{
    String idTopic;
    String name;
    String description;
    String course;
    String password;
    String creatorId;
    TopicPresenter presenter;

    public Topic() {}

    public Topic(TopicPresenter presenter) {
        this.presenter = presenter;
    }

    public String getIdTopic() {
        return idTopic;
    }

    public void setIdTopic(String idTopic) {
        this.idTopic = idTopic;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    private void setDescriptionInMap(Map<String, Object> map) {
        if (getDescription() != null) {
            map.put("description", getDescription());
        }
    }
    public void setDescriptionIfNull(String description) {
        if (this.description == null) {
            this.description = description;
        }
    }

    public String getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    private void setCourseInMap(Map<String, Object> map) {
        if (getCourse() != null) {
            map.put("course", getCourse());
        }
    }
    public void setCourseIfNull(String course) {
        if (this.course == null) {
            this.course = course;
        }
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void saveDB(DatabaseReference.CompletionListener... completionListener) {
        DatabaseReference firebaseReference = LibraryClass.getFirebase().child("generalTopic").push();
        String topicKey = firebaseReference.getKey();
        DatabaseReference firebase = LibraryClass.getFirebase().child("topics").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(topicKey);

        this.setIdTopic(topicKey);

        firebase.setValue(this, completionListener[0]);
        firebaseReference.setValue(this, completionListener[0]);
    }
}
