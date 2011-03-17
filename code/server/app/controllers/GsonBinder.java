package controllers;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import play.data.binding.Global;
import play.data.binding.TypeBinder;

import java.lang.annotation.Annotation;

@Global
public class GsonBinder implements TypeBinder<JsonArray> {

    public Object bind(String name, Annotation[] antns, String value, Class type) throws Exception {
        return new JsonParser().parse(value);
    }
}
