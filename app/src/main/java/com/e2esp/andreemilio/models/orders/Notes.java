package com.e2esp.andreemilio.models.orders;

import com.e2esp.andreemilio.models.orders.Note;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Zain on 2/17/2017.
 */
public class Notes {

    @SerializedName("order_notes")
    private List<Note> notes;

    public List<Note> getNotes() {
        return notes;
    }

    public void setNotes(List<Note> notes) {
        this.notes = notes;
    }
}
