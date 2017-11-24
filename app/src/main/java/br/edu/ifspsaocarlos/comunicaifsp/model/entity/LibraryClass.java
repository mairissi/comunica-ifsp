package br.edu.ifspsaocarlos.comunicaifsp.model.entity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by MRissi on 13-Sep-17.
 */

public final class LibraryClass {

    private static DatabaseReference firebase;

    private LibraryClass(){}

    public static DatabaseReference getFirebase(){
        if( firebase == null ){
            firebase = FirebaseDatabase.getInstance().getReference();
        }

        return( firebase );
    }
}
