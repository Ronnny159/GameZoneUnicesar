package com.gameZone.persistence;

import com.gameZone.model.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class WarrantyRepository {
    
    private static final String FILE_PATH = "data/warranties.dat";
    private List<Warranty> warranties;
    
    
      private void persist(){
    
        File directory = new File("data");
            if(!directory.exists()){
                directory.mkdirs();
            }
            
            try(ObjectOutputStream oos = new ObjectOutputStream( new FileOutputStream(FILE_PATH))){
            
                oos.writeObject(warranties);
            
            }catch(IOException e){
            
                System.err.println("Error Saving Warranties: " + e.getMessage());
            }
    }
    
    public WarrantyRepository() {
        this.warranties = new ArrayList<>();
        loadAll();
    }
    
    public void saveWarranty(Warranty warranty){
        warranties.add(warranty); 
        persist();
    }
    
    private void loadAll() {
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            warranties = new ArrayList<>();
            return;
        }
        
        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(FILE_PATH))) {
                warranties = (List<Warranty>) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                System.err.println("Error loading warranties: " + e.getMessage());
                warranties = new ArrayList<>();
        }
    }
}
