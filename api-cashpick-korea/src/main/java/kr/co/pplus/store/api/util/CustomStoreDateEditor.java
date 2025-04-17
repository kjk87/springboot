package kr.co.pplus.store.api.util;

import org.springframework.beans.propertyeditors.CustomDateEditor;

import java.text.SimpleDateFormat;

public class CustomStoreDateEditor extends CustomDateEditor {

    public CustomStoreDateEditor(){
        super(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"), true) ;
    }
}
