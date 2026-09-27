package com.gameZone.persistence;
import com.gameZone.model.Promotion;
import com.gameZone.model.Sale;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class PromotionRepository {
    private static final String FILE_PATH = "data/promotions.dat";
    private List<Promotion> promotions;
    
    
      private void persist(){
    
        File directory = new File("data");
            if(!directory.exists()){
                directory.mkdirs();
            }
            
            try(ObjectOutputStream oos = new ObjectOutputStream( new FileOutputStream(FILE_PATH))){
            
                oos.writeObject(promotions);
            
            }catch(IOException e){
            
                System.err.println("Error Saving Promotions: " + e.getMessage());
            }
    }
    
    public PromotionRepository() {
        this.promotions = new ArrayList<>();
        loadAll();
    }
    
    public void savePromotion(Promotion promotion){
        promotions.add(promotion); 
        persist();
    }
    
    private void loadAll() {
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            promotions = new ArrayList<>();
            return;
        }
        
        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(FILE_PATH))) {
                promotions = (List<Promotion>) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                System.err.println("Error loading promotions: " + e.getMessage());
                promotions = new ArrayList<>();
        }
    }
}
