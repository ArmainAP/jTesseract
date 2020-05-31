package org.jmonkeyengine.simple_jme_android.gamelogic;

import com.jme3.app.SimpleApplication;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Spatial;

import java.util.logging.Logger;

public class Main extends SimpleApplication {
    private static final Logger logger = Logger.getLogger(Main.class.getName());

    public void simpleInitApp() {
        Spatial cone = assetManager.loadModel("Models/Cone/Cone.obj");
        cone.setLocalTranslation(-5, 0, 0);
        rootNode.attachChild(cone);
        Material coneMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        coneMat.setColor("Color", ColorRGBA.Blue);
        cone.setMaterial(coneMat);

        Spatial cylinder = assetManager.loadModel("Models/Cylinder/Cylinder.obj");
        cylinder.setLocalTranslation(5, 0, 0);
        rootNode.attachChild(cylinder);
        Material cylinderMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        cylinderMat.setColor("Color", ColorRGBA.Green);
        cylinder.setMaterial(cylinderMat);

        Spatial torus = assetManager.loadModel("Models/Torus/Torus.obj");
        torus.setLocalTranslation(0, -5, 0);
        rootNode.attachChild(torus);
        Material torusMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        torusMat.setColor("Color", ColorRGBA.Red);
        torus.setMaterial(torusMat);
    }
}
