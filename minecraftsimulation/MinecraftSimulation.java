/***********************************
*   file: MinecraftSimulation.java
*   author: Justen Minamitani, Matthew Haddad, Kathleen Phan, Junda Lou, Robbie Blanco
*   class: CS445.01 - Computer Graphics
*   assignment: Final Project - Minecraft Simulation
*   date last modified: 5/6/2018
*   purpose: Simulate a scene in the Minecraft
***********************************/
package minecraftsimulation;

import java.nio.FloatBuffer;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import static org.lwjgl.opengl.GL11.GL_AMBIENT;
import static org.lwjgl.opengl.GL11.GL_COLOR_ARRAY;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_DIFFUSE;
import static org.lwjgl.opengl.GL11.GL_LIGHT0;
import static org.lwjgl.opengl.GL11.GL_LIGHTING;
import static org.lwjgl.opengl.GL11.GL_MODELVIEW;
import static org.lwjgl.opengl.GL11.GL_NICEST;
import static org.lwjgl.opengl.GL11.GL_PERSPECTIVE_CORRECTION_HINT;
import static org.lwjgl.opengl.GL11.GL_POSITION;
import static org.lwjgl.opengl.GL11.GL_PROJECTION;
import static org.lwjgl.opengl.GL11.GL_SPECULAR;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_COORD_ARRAY;
import static org.lwjgl.opengl.GL11.GL_VERTEX_ARRAY;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glEnableClientState;
import static org.lwjgl.opengl.GL11.glHint;
import static org.lwjgl.opengl.GL11.glLight;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.opengl.GL11.glMatrixMode;
import org.lwjgl.util.glu.GLU;


public class MinecraftSimulation {
    
    public static void initGL(DisplayMode displayMode) {
        glClearColor(0.529f, 0.808f, 0.922f, 1f);
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        GLU.gluPerspective(100f, (float)displayMode.getWidth() / (float)displayMode.getHeight(), 0.1f, 300f);
        
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_TEXTURE_2D);
        
        glEnableClientState(GL_VERTEX_ARRAY);
        glEnableClientState(GL_COLOR_ARRAY);
        glEnableClientState(GL_TEXTURE_COORD_ARRAY);
        
        FloatBuffer lightPosition, whiteLight;
        lightPosition = BufferUtils.createFloatBuffer(4);
        lightPosition.put(0.0f).put(0.0f).put(0.0f).put(1.0f).flip();
        whiteLight = BufferUtils.createFloatBuffer(4);
        whiteLight.put(1.0f).put(1.0f).put(1.0f).put(0.0f).flip();
        glLight(GL_LIGHT0, GL_POSITION, lightPosition); 
        glLight(GL_LIGHT0, GL_SPECULAR, whiteLight);
        glLight(GL_LIGHT0, GL_DIFFUSE, whiteLight);
        glLight(GL_LIGHT0, GL_AMBIENT, whiteLight);
        glEnable(GL_LIGHTING);
        glEnable(GL_LIGHT0);
        
        glMatrixMode(GL_MODELVIEW);
        glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);
    }

    public static DisplayMode createWindow() throws Exception {
        Display.setFullscreen(false);
        DisplayMode[] dMode = Display.getAvailableDisplayModes();
        DisplayMode displayMode = null;
        for(int i = 0; i < dMode.length; i++) {
            if(dMode[i].getWidth() == 640 && dMode[i].getHeight() == 480 && dMode[i].getBitsPerPixel() == 32) {
                displayMode = dMode[i];
                break;
            }
        }
        Display.setDisplayMode(displayMode);
        Display.setTitle("Final Project");
        Display.create();
        return displayMode;
    }
    
    public static void main(String[] args) {
        FPCameraController fp;
        DisplayMode displayMode = null;
        try {
            displayMode = createWindow();
            initGL(displayMode);
            fp = new FPCameraController(0f, 0f, 0f);
            fp.gameLoop();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
}
