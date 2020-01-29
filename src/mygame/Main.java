package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;

import com.jme3.input.KeyInput;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.ActionListener;

public class Main extends SimpleApplication {

    Tesseract t = new Tesseract();
    Tesseract.RotationalPlane planeRotation = Tesseract.RotationalPlane.None;

    public static void main(String[] args) {
        Main app = new Main();
        app.start();
    }

    @Override
    public void simpleInitApp() {
        Geometry tesseractGeom = new Geometry("Tesseract", t);
        Material matVC = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        matVC.setBoolean("VertexColor", true);
        tesseractGeom.setMaterial(matVC);
        rootNode.attachChild(tesseractGeom);

        initInputKeys();
    }

    private void initInputKeys() {
        inputManager.addMapping("ToggleProjection", new KeyTrigger(KeyInput.KEY_0));
        inputManager.addMapping("XY", new KeyTrigger(KeyInput.KEY_1));
        inputManager.addMapping("XZ", new KeyTrigger(KeyInput.KEY_2));
        inputManager.addMapping("YZ", new KeyTrigger(KeyInput.KEY_3));
        inputManager.addMapping("XW", new KeyTrigger(KeyInput.KEY_4));
        inputManager.addMapping("YW", new KeyTrigger(KeyInput.KEY_5));
        inputManager.addMapping("ZW", new KeyTrigger(KeyInput.KEY_6));
        inputManager.addMapping("XYZW", new KeyTrigger(KeyInput.KEY_7));
        inputManager.addMapping("XZYW", new KeyTrigger(KeyInput.KEY_8));
        inputManager.addMapping("YZXW", new KeyTrigger(KeyInput.KEY_9));

        inputManager.addListener(actionListener, "ToggleProjection");
        inputManager.addListener(actionListener, "XY");
        inputManager.addListener(actionListener, "XZ");
        inputManager.addListener(actionListener, "YZ");
        inputManager.addListener(actionListener, "XW");
        inputManager.addListener(actionListener, "YW");
        inputManager.addListener(actionListener, "ZW");
        inputManager.addListener(actionListener, "XYZW");
        inputManager.addListener(actionListener, "XZYW");
        inputManager.addListener(actionListener, "YZXW");
    }

    private final ActionListener actionListener = new ActionListener() {
        @Override
        public void onAction(String name, boolean keyPressed, float tpf) {
            if (keyPressed) {
                switch (name) {
                    case "ToggleProjection": {
                        t.ToggleProjection();
                        break;
                    }

                    case "XY": {
                        planeRotation = Tesseract.RotationalPlane.XY;
                        break;
                    }

                    case "XZ": {
                        planeRotation = Tesseract.RotationalPlane.XZ;
                        break;
                    }

                    case "YZ": {
                        planeRotation = Tesseract.RotationalPlane.YZ;
                        break;
                    }

                    case "XW": {
                        planeRotation = Tesseract.RotationalPlane.XW;
                        break;
                    }

                    case "YW": {
                        planeRotation = Tesseract.RotationalPlane.YW;
                        break;
                    }

                    case "ZW": {
                        planeRotation = Tesseract.RotationalPlane.ZW;
                        break;
                    }

                    case "XYZW": {
                        planeRotation = Tesseract.RotationalPlane.XYZW;
                        break;
                    }

                    case "XZYW": {
                        planeRotation = Tesseract.RotationalPlane.XZYW;
                        break;
                    }

                    case "YZXW": {
                        planeRotation = Tesseract.RotationalPlane.YZXW;
                        break;
                    }
                }
            }
        }
    };

    @Override
    public void simpleUpdate(float tpf) {
        //Rotating with 15 degrees per second on the desired plane
        t.Rotate(30 * tpf, planeRotation);
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }
}
