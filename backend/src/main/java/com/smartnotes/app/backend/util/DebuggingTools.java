package com.smartnotes.app.backend.util;


import org.springframework.stereotype.Component;

@Component
public class DebuggingTools {

    public void SlowDatabaseCalls(){
        try {
            Thread.sleep(5000);
        } catch (Exception ex){

        }
    }


}
