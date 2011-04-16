package graphics;

import java.awt.Component;
import java.awt.Frame;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JFileChooser;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.fixedfunc.GLLightingFunc;
import javax.media.opengl.fixedfunc.GLMatrixFunc;
import javax.media.opengl.glu.GLU;
import java.io.File;
import java.io.IOException;

import math.Quaternion;
import math.Vector3;
 
import com.jogamp.opengl.util.Animator;
 
public class ModelViewer implements GLEventListener, KeyListener {
    private static final float TURN_RATE = 0.5f;
    private static final float SPEED = 0.5f;
    
    private Quaternion cameraRotation = new Quaternion();
    private Vector3 cameraPosition = new Vector3(0.0f, 0.0f, 20.0f);
    private Model model;
    private Quaternion modelRotation = new Quaternion();
    private Vector3 modelPosition = new Vector3(0.0f, 0.0f, 0.0f);
    
    static GLU glu = new GLU();
    static GLCanvas canvas = new GLCanvas();
    static Frame frame = new Frame("Model Viewer");
    static Animator animator = new Animator(canvas);
    static Shader shader = new Shader("minimal.vert","minimal.frag");
 
    public ModelViewer(File objFile) {
        model = WavefrontObjLoader.load(objFile);
    }

    public void display(GLAutoDrawable gLDrawable) {
        final GL2 gl = gLDrawable.getGL().getGL2();
        gl.glClear(GL2.GL_COLOR_BUFFER_BIT);
        gl.glClear(GL2.GL_DEPTH_BUFFER_BIT);
        gl.glLoadIdentity();
        
        gl.glMultMatrixf(cameraRotation.inverse().toGlMatrix(), 0);
        gl.glTranslatef(-cameraPosition.x, -cameraPosition.y, -cameraPosition.z);

        gl.glMultMatrixf(modelRotation.toGlMatrix(), 0);
        gl.glTranslatef(modelPosition.x, modelPosition.y, modelPosition.z);
        
        model.render(gl);
        
        gl.glFlush();
    }
 
    public void displayChanged(GLAutoDrawable gLDrawable, boolean modeChanged, boolean deviceChanged) {
    }
 
    public void init(GLAutoDrawable gLDrawable) {
        GL2 gl = gLDrawable.getGL().getGL2();
        gl.glShadeModel(GLLightingFunc.GL_SMOOTH);
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        gl.glClearDepth(1.0f);
        gl.glEnable(GL2.GL_DEPTH_TEST);
        gl.glDepthFunc(GL2.GL_LEQUAL);
        gl.glHint(GL2.GL_PERSPECTIVE_CORRECTION_HINT, GL2.GL_NICEST);
        
        
        // Enable lighting
        gl.glEnable(GL2.GL_LIGHTING);
        gl.glEnable(GL2.GL_LIGHT0);
        gl.glEnable(GL2.GL_LIGHT1);
        float light_ambient[] = { 0.5f, 0.5f, 0.5f, 1.0f };
        float light_diffuse[] = { 1.0f, 1.0f, 1.0f, 1.0f };
        float light_specular[] = { 1.0f, 1.0f, 1.0f, 1.0f };
        
        float[] light0 = {-1.0f,-2.0f,2.0f,0.0f};
        float[] light1 = {1.0f,2.0f,-2.0f,0.0f};
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_AMBIENT, light_ambient, 0);
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_DIFFUSE, light_diffuse, 0);
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_SPECULAR, light_specular, 0);
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, light0, 0);

        gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_AMBIENT, light_ambient, 0);
        gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_DIFFUSE, light_diffuse, 0);
        gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_SPECULAR, light_specular, 0);
        gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_POSITION, light1, 0);
        
        
        ((Component) gLDrawable).addKeyListener(this);
        try {
            shader.init(gl);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        shader.enable(gl);
    }
 
    public void reshape(GLAutoDrawable gLDrawable, int x, int y, int width, int height) {
        GL2 gl = gLDrawable.getGL().getGL2();
        if (height <= 0) {
            height = 1;
        }
        float h = (float) width / (float) height;
        gl.glMatrixMode(GLMatrixFunc.GL_PROJECTION);
        gl.glLoadIdentity();
        glu.gluPerspective(50.0f, h, 1.0, 1000.0);
        gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
        gl.glLoadIdentity();
    }
 
    public void keyPressed(KeyEvent e) {
        switch(e.getKeyCode()) {
            case KeyEvent.VK_ESCAPE:
                exit();
                break;
            case KeyEvent.VK_L:
                loadModel();
                break;
            case KeyEvent.VK_UP:
                cameraRotation.timesEquals(new Quaternion(cameraRotation.pitchAxis(), TURN_RATE));
                break;
            case KeyEvent.VK_DOWN:
                cameraRotation.timesEquals(new Quaternion(cameraRotation.pitchAxis(), -TURN_RATE));
                break;
            case KeyEvent.VK_LEFT:
                cameraRotation.timesEquals(new Quaternion(cameraRotation.yawAxis(), TURN_RATE));
                break;
            case KeyEvent.VK_RIGHT:
                cameraRotation.timesEquals(new Quaternion(cameraRotation.yawAxis(), -TURN_RATE));
                break;
            case KeyEvent.VK_PAGE_UP:
                cameraRotation.timesEquals(new Quaternion(cameraRotation.rollAxis(), TURN_RATE));
                break;
            case KeyEvent.VK_PAGE_DOWN:
                cameraRotation.timesEquals(new Quaternion(cameraRotation.rollAxis(), -TURN_RATE));
                break;
            case KeyEvent.VK_A:
                cameraPosition.plusEquals(cameraRotation.rollAxis().times(SPEED));
                break;
            case KeyEvent.VK_Z:
                cameraPosition.minusEquals(cameraRotation.rollAxis().times(SPEED));
                break;
        }
    }
 
    private void loadModel() {
        model = WavefrontObjLoader.load(guiPromptForFilename());
    }

    public void keyReleased(KeyEvent e) {
    }
 
    public void keyTyped(KeyEvent e) {
    }
 
    public static void exit() {
        animator.stop();
        frame.dispose();
        System.exit(0);
    }
 
    public static void main(String[] args) {
        File objFile = guiPromptForFilename();
        
        canvas.addGLEventListener(new ModelViewer(objFile));
        frame.add(canvas);
        frame.setSize(640, 480);
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                exit();
            }
        });
        frame.setVisible(true);
        animator.start();
        canvas.requestFocus();
    }
 
    public void dispose(GLAutoDrawable gLDrawable) {
        // do nothing
    }
    
    private static File guiPromptForFilename() {
        JFileChooser jfc = new JFileChooser("assets");
        
        if (jfc.showOpenDialog(null) != JFileChooser.APPROVE_OPTION) {
            System.err.println("User did not specify an input file.");
            System.exit(1);
        }

        return jfc.getSelectedFile();
    }

}