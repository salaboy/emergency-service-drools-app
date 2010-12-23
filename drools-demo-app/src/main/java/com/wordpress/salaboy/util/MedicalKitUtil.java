/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.wordpress.salaboy.util;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import com.wordpress.salaboy.model.Medic.MedicSpeciality;
import com.wordpress.salaboy.model.MedicalKit;

/**
 *
 * @author esteban
 */
public class MedicalKitUtil {

    private static final String bonesMedicalKitName="Bones Medical Kit";
    private static final String fireMedicalKitName="Fire Medical Kit";
    private static final String heartAttackMedicalKitName="Heart Attack Medical Kit";
    
    public static MedicalKit createNewMEdicalKit(MedicSpeciality type){
        String stringType = "";
        
        switch (type){
            case BONES:
                stringType = bonesMedicalKitName;
                break;
            case BURNS:
                stringType = fireMedicalKitName;
                break;
            case REANIMATION:
                stringType = heartAttackMedicalKitName;
                break;
        }
        
        return new MedicalKit(stringType,type);
    }
    
    public static ImageIcon getMedicalKitImage(MedicalKit kit){
        
        String imageName = "";
        if (kit.getName().equals(bonesMedicalKitName)){
            imageName = "cuello.png";
        }else if (kit.getName().equals(fireMedicalKitName)){
            imageName = "crema.png";
        }else if (kit.getName().equals(heartAttackMedicalKitName)){
            imageName = "eletrochock.png";
        }
        
        
        return new ImageIcon("/data/png/"+imageName);
    }
    
}
