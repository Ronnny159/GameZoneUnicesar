package com.gameZone.persistence;

import com.gameZone.model.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
public class ReturnRepository {
    private static final String FILE_PATH = "data/returns.dat";
    private List<Return> returns;
    
    
      private void persist(){
    
        File directory = new File("data");
            if(!directory.exists()){
                directory.mkdirs();
            }
            
            try(ObjectOutputStream oos = new ObjectOutputStream( new FileOutputStream(FILE_PATH))){
            
                oos.writeObject(returns);
            
            }catch(IOException e){
            
                System.err.println("Error Saving Returns: " + e.getMessage());
            }
    }
    
    public ReturnRepository() {
        this.returns = new ArrayList<>();
        loadAll();
    }
    
    public void saveAll(Return returnItem){
        returns.add(returnItem); 
        persist();
    }
    
    private void loadAll() {
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            returns = new ArrayList<>();
            return;
        }
        
        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(FILE_PATH))) {
                returns = (List<Return>) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                System.err.println("Error loading returns: " + e.getMessage());
                returns = new ArrayList<>();
        }
    }
}
