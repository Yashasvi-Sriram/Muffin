package org.muffin.muffin;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.muffin.muffin.beans.Movie;
import org.muffin.muffin.responses.GenericResponse;

public class Scratch {
    public static void main(String[] args) {
        Gson gson = new GsonBuilder().create();
        String c = gson.toJson(GenericResponse.get(new Movie(1, 2, "fs", 120), GenericResponse.OBJECT_RESPONSE));
        System.out.println(c);
    }
}
