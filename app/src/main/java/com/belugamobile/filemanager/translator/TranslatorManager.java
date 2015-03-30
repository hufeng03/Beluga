package com.belugamobile.filemanager.translator;

/**
 * Created by Feng Hu on 15-03-24.
 * <p/>
 * TODO: Add a class header comment.
 */
public class TranslatorManager {

    public static Translator[] getAll() {
        return new Translator[] {
            new Translator("Carlo Friscia", "", "Italiano"),
            new Translator("Ahmet Burak Baraklı","","Türk"),
            new Translator("Ozan Akın", "", "Türk"),
            new Translator("赵彬言", "", "中文"),
            new Translator("Freddy Orlando Canche Aban", "", "Español")
        };
    }

}
