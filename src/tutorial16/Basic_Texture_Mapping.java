/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tutorial16;

/**
 *
 * @author user
 */
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.media.opengl.*;
import javax.media.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.*;
import javax.swing.JFrame;
import java.nio.IntBuffer;
import java.nio.FloatBuffer;
import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;
import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.la4j.matrix.Matrix;
import org.la4j.vector.Vector;

public class Basic_Texture_Mapping implements GLEventListener {

    public Basic_Texture_Mapping(Camera camera) {
        this.camera = camera;
    }
    IntBuffer vbo = IntBuffer.allocate(1);
    IntBuffer ibo = IntBuffer.allocate(1);
    IntBuffer tbo = IntBuffer.allocate(1);
    int ShaderProgram;
    int gWVPLocation;
    int gSampler;
    float Scale = 0f;
    Camera camera = null;
    Texture texture;

    public static void main(String[] args) {
        GLProfile glp = GLProfile.getDefault();
        GLCapabilities caps = new GLCapabilities(glp);
        GLCanvas canvas = new GLCanvas(caps);
        Camera camera = new Camera(800, 800);
        canvas.addKeyListener(camera);
        canvas.addMouseListener(camera);
        canvas.addComponentListener(camera);

        JFrame frame = new JFrame("Basic Texture Mapping");
        frame.setSize(800, 800);
        frame.add(canvas);
        frame.setVisible(true);

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        canvas.addGLEventListener(new Basic_Texture_Mapping(camera));

        FPSAnimator animator = new FPSAnimator(canvas, 60);
//        animator.add(canvas);
        animator.start();
    }

    @Override
    public void display(GLAutoDrawable drawable) {
        render(drawable);
    }

    @Override
    public void dispose(GLAutoDrawable drawable) {
        GL4 gl = drawable.getGL().getGL4();
        gl.glDeleteVertexArrays(1, vbo);
        gl.glDeleteBuffers(1, ibo);
        gl.glDeleteProgram(ShaderProgram);
    }

    @Override
    public void init(GLAutoDrawable drawable) {
        GL4 gl = drawable.getGL().getGL4();

        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        gl.glFrontFace(GL4.GL_CW);
        gl.glCullFace(GL4.GL_BACK);
        gl.glEnable(GL4.GL_CULL_FACE);

        initVertex(gl);
        CreateIndexBuffer(gl);
        compileShader(gl);
        gl.glUniform1i(gSampler, 0);

        //        gl.glGenTextures(1, tbo);
        //        gl.glBindTexture(GL.GL_TEXTURE_2D, tbo.get(0));
        try {
            InputStream is = this.getClass().getResourceAsStream("/tutorial16/test.png");
            texture = TextureIO.newTexture(is, true, TextureIO.PNG);
        } catch (IOException ex) {
            Logger.getLogger(Basic_Texture_Mapping.class.getName()).log(Level.SEVERE, null, ex);
        } catch (GLException ex) {
            Logger.getLogger(Basic_Texture_Mapping.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int w, int h) {
    }

    private void render(GLAutoDrawable drawable) {
        GL4 gl = drawable.getGL().getGL4();
        gl.glClearColor(0.5f, 0.5f, 0.5f, 1f);
        gl.glClear(GL4.GL_COLOR_BUFFER_BIT);

        camera.onRender();

        Scale += 0.1f;

        Pipeline p = new Pipeline();

        p.rotate(0, Scale, 0);
        p.transfrom(0, 0, 3);

//        Vector cameraPos = Pipeline.b1dFactory.createVector(new double[]{0, 0, -3});
//        Vector cameraTarget = Pipeline.b1dFactory.createVector(new double[]{0.45f, 0.0f, 1.0f});
//        Vector cameraUp = Pipeline.b1dFactory.createVector(new double[]{0.0f, 1.0f, 0.0f});
        p.setCamera(camera.pos, camera.target, camera.up);
//        System.out.printf("Camera pos:%s%n",camera.pos);
//        System.out.printf("Camera target:%s%n",camera.target);
//        System.out.printf("Camera up:%s%n",camera.up);
        p.setPerspectiveProj(60f, drawable.getWidth(), drawable.getHeight(), 1, 100);
        Matrix result = p.getTrans();
        float[] World = p.toFloatArray(result);



        gl.glUniformMatrix4fv(gWVPLocation, 1, true, World, 0);



        gl.glEnableVertexAttribArray(0);
        gl.glEnableVertexAttribArray(1);
        gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, vbo.get(0));
        gl.glVertexAttribPointer(0, 3, GL2.GL_FLOAT, false, 5*Buffers.SIZEOF_FLOAT, 0);
        gl.glVertexAttribPointer(1, 2, GL2.GL_FLOAT, false, 5*Buffers.SIZEOF_FLOAT, Buffers.SIZEOF_FLOAT*3);
        gl.glBindBuffer(GL4.GL_ELEMENT_ARRAY_BUFFER, ibo.get(0));
        texture.bind(gl);
        gl.glDrawElements(GL4.GL_TRIANGLES, 12, GL4.GL_UNSIGNED_INT, 0);
        
        gl.glDisableVertexAttribArray(0);
        gl.glDisableVertexAttribArray(1);
        
    }

    private String[] readShader(String path) {

        StringBuilder sb = new StringBuilder();
        String[] result = null;
        InputStream is = null;
        is = getClass().getResourceAsStream(path);
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        try {
            String ts = null;
            while ((ts = br.readLine()) != null) {
                sb.append(ts);
                sb.append('\n');
            }
            result = new String[1];
            result[0] = sb.toString();
        } catch (IOException ex) {
            Logger.getLogger(Basic_Texture_Mapping.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                is.close();
            } catch (IOException ex) {
                Logger.getLogger(Basic_Texture_Mapping.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return result;

    }

    private void initVertex(GL4 gl) {
        float[] data = {
            -1.0f, -1.0f, 0.5773f, 0f, 0f,
            0f, -1.0f, -1.15475f, 0.5f, 0f,
            1f, -1f, 0.5773f, 1f, 0f,
            0, 1, 0, 0.5f, 1f
        };
        
//        float[] data = {
//            -1.0f, -1.0f, 0, 0, 0,
//            0f, -1.0f, 0, 0.5f, 0,
//            1f, -1f, 0, 1f, 0,
//            0, 1, 0, 0, 1f
//        };


        FloatBuffer vertexs = FloatBuffer.wrap(data);

        gl.glGenBuffers(1, vbo);
        gl.glBindBuffer(GL4.GL_ARRAY_BUFFER, vbo.get(0));
        gl.glBufferData(GL4.GL_ARRAY_BUFFER, vertexs.limit() * Buffers.SIZEOF_FLOAT, vertexs, GL4.GL_STATIC_DRAW);
    }

    private void CreateIndexBuffer(GL4 gl) {
        int Indices[] = {0, 3, 1,
            1, 3, 2,
            2, 3, 0,
            1,2,0};

        gl.glGenBuffers(1, ibo);
        gl.glBindBuffer(GL4.GL_ELEMENT_ARRAY_BUFFER, ibo.get(0));
        gl.glBufferData(GL4.GL_ELEMENT_ARRAY_BUFFER, Indices.length * Buffers.SIZEOF_INT, IntBuffer.wrap(Indices), GL4.GL_STATIC_DRAW);
    }

    void AddShader(GL4 gl, int ShaderProgram, String[] pShaderText, int ShaderType) {
        int ShaderObj = gl.glCreateShader(ShaderType);

        if (ShaderObj == 0) {
            System.err.printf("Error creating shader type %d%n", ShaderType);
            System.exit(0);
        }
        gl.glShaderSource(ShaderObj, 1, pShaderText, null);
        gl.glCompileShader(ShaderObj);
        IntBuffer succes = IntBuffer.allocate(1);
        gl.glGetShaderiv(ShaderObj, GL4.GL_COMPILE_STATUS, succes);
        if (succes.get(0) != 1) {
            ByteBuffer InfoLog = ByteBuffer.allocate(1024);
            gl.glGetShaderInfoLog(ShaderObj, 1024, null, InfoLog);
            String info = new String(InfoLog.array());
            System.err.printf("Error compiling shader type %d: '%s'%n", ShaderType, info);
            System.exit(1);
        }
        gl.glAttachShader(ShaderProgram, ShaderObj);
    }

    private void compileShader(GL4 gl) {
        ShaderProgram = gl.glCreateProgram();

        if (ShaderProgram == 0) {
            System.err.println("Error creating shader program");
            System.exit(1);
        }

        String[] pVS = readShader("vs.txt");
        String[] pFS = readShader("fs.txt");

        AddShader(gl, ShaderProgram, pVS, GL4.GL_VERTEX_SHADER);
        AddShader(gl, ShaderProgram, pFS, GL4.GL_FRAGMENT_SHADER);

        IntBuffer Success = IntBuffer.allocate(1);
        ByteBuffer errorlog = ByteBuffer.allocate(1024);
        gl.glLinkProgram(ShaderProgram);
        gl.glGetProgramiv(ShaderProgram, GL4.GL_LINK_STATUS, Success);
        if (Success.get(0) == 0) {
            gl.glGetProgramInfoLog(ShaderProgram, 1024, null, errorlog);
            System.err.printf("Error linking shader program: '%s'\n", new String(errorlog.array()));
            System.exit(1);
        }

        gl.glValidateProgram(ShaderProgram);
        gl.glGetProgramiv(ShaderProgram, GL4.GL_VALIDATE_STATUS, Success);
        if (Success.get(0) == 0) {
            gl.glGetProgramInfoLog(ShaderProgram, 1024, null, errorlog);
            System.err.printf("Invalid shader program: '%s'\n", new String(errorlog.array()));
            System.exit(1);
        }

        gl.glUseProgram(ShaderProgram);
        gWVPLocation = gl.glGetUniformLocation(ShaderProgram, "gWVP");
        assert (gWVPLocation != 0xFFFFFFFF);
        gSampler = gl.glGetUniformLocation(ShaderProgram, "gSampler");
        assert (gSampler != 0xFFFFFFFF);
    }
}

class Camera extends MouseAdapter implements KeyListener, ComponentListener {

    Vector pos, target, up;
    static final double StepScale = 0.1;
    static int Margin = 10;
    boolean onUpperEdge;
    boolean onLowerEdge;
    boolean onLeftEdge;
    boolean onRightEdge;
    double angleH;
    double angleV;
    int width;
    int height;
    Point2D.Double mouseP = new Point2D.Double();

    void onRender() {
        boolean ShouldUpdate = false;

        if (onLeftEdge) {
            angleH -= 0.1f;
            ShouldUpdate = true;
        } else if (onRightEdge) {
            angleH += 0.1f;
            ShouldUpdate = true;
        }

        if (onUpperEdge) {
            if (angleV > -90.0f) {
                angleV -= 0.1f;
                ShouldUpdate = true;
            }
        } else if (onLowerEdge) {
            if (angleV < 90.0f) {
                angleV += 0.1f;
                ShouldUpdate = true;
            }
        }

        if (ShouldUpdate) {
            update();
        }
    }

    void update() {
        final Vector Vaxis = Pipeline.b1dFactory.createVector(new double[]{0.0, 1.0, 0.0});

        // Rotate the view vector by the horizontal angle around the vertical axis
        System.out.printf("HAngle: %f%n", angleH);
        System.out.printf("VAngle: %f%n", angleV);
        Vector View = Pipeline.b1dFactory.createVector(new double[]{1.0f, 0.0f, 0.0f});

        View = rotate(angleH, View, Vaxis);
        View = View.normalize();

        // Rotate the view vector by the vertical angle around the horizontal axis
        Vector Haxis = Pipeline.cross(Vaxis, View);
        Haxis = Haxis.normalize();
        View = rotate(angleV, View, Haxis);

        target = View;
        target = target.normalize();

        up = Pipeline.cross(target, Haxis);
        up = up.normalize();
    }

    private Vector rotate(double angle, Vector target, Vector Axe) {
        final double sinHalfAngle = Math.sin(Math.toRadians(angle / 2));
        final double cosHalfAngle = Math.cos(Math.toRadians(angle / 2));

        final double rx = Axe.get(0) * sinHalfAngle;
        final double ry = Axe.get(1) * sinHalfAngle;
        final double rz = Axe.get(2) * sinHalfAngle;
        final double w = cosHalfAngle;

        Quaternion rotationQ = new Quaternion(rx, ry, rz, w);
        Quaternion conjuateQ = rotationQ.conjugate();
        Quaternion wQ = rotationQ.multipe(target);
        wQ = wQ.multipe(conjuateQ);
        return Pipeline.b1dFactory.createVector(new double[]{wQ.x, wQ.y, wQ.z});
    }

    void init() {
        Vector HTarget = Pipeline.b1dFactory.createVector(new double[]{target.get(0), 0.0, target.get(2)});
        HTarget = HTarget.normalize();

        if (HTarget.get(2) >= 0.0) {
            if (HTarget.get(0) >= 0.0) {

                angleH = 360.0f - Math.toDegrees(Math.asin(HTarget.get(2)));
            } else {
                angleH = 180.0f + Math.toDegrees(Math.asin(HTarget.get(2)));
            }
        } else {
            if (HTarget.get(0) >= 0.0) {
                angleH = Math.toDegrees(Math.asin(-HTarget.get(2)));
            } else {
                angleH = 90.0f + Math.toDegrees(Math.asin(-HTarget.get(2)));
            }
        }

        angleV = -Math.toDegrees(Math.asin(target.get(1)));

        onUpperEdge = false;
        onLowerEdge = false;
        onLeftEdge = false;
        onRightEdge = false;
        mouseP.x = width / 2.0;
        mouseP.y = height / 2.0;
        try {
            Robot robot = new Robot();
            robot.mouseMove((int) mouseP.x, (int) mouseP.y);
        } catch (AWTException ex) {
            Logger.getLogger(Camera.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    Camera(int width, int height) {
        pos = Pipeline.b1dFactory.createVector(new double[]{0, 0, 0});
        target = Pipeline.b1dFactory.createVector(new double[]{0, 0, 1});
        up = Pipeline.b1dFactory.createVector(new double[]{0, 1, 0});
        this.width = width;
        this.height = height;
        init();
    }

    Camera(int width, int height, Vector pos, Vector target, Vector up) {
        this.pos = pos;
        this.target = target.normalize();
        this.up = up.normalize();
        this.width = width;
        this.height = height;
        init();
    }

    @Override
    public void keyTyped(KeyEvent e) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void keyPressed(KeyEvent e) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        System.out.println(e.getKeyCode());
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP:
                System.out.println("Up");
                pos = pos.add(target.multiply(StepScale));
                break;
            case KeyEvent.VK_DOWN:
                System.out.println("Down");
                pos = pos.subtract(target.multiply(StepScale));
                break;
            case KeyEvent.VK_LEFT:
                System.out.println("Left");
                Vector left = Pipeline.cross(target, up);
                left = left.normalize();
                left = left.multiply(StepScale);
                pos = pos.add(left);
                break;
            case KeyEvent.VK_RIGHT:
                System.out.println("Right");
                Vector right = Pipeline.cross(up, target);
                right = right.normalize();
                right = right.multiply(StepScale);
                pos = pos.add(right);
                break;
            case KeyEvent.VK_Q:
                System.exit(0);
                break;
            default:
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void mouseClicked(MouseEvent e) {

//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        System.out.printf("x = %d,y = %d%n", e.getX(), e.getY());
        int x = e.getX();
        int y = e.getY();

        final double dx = x - mouseP.x;
        final double dy = y - mouseP.y;

        angleH += (dx / 20.0);
        angleV += (dy / 20.0);

        if (dx == 0) {
            if (x <= Margin) {
                //    m_AngleH -= 1.0f;
                onLeftEdge = true;
            } else if (x >= (width - Margin)) {
                //    m_AngleH += 1.0f;
                onRightEdge = true;
            }
        } else {
            onLeftEdge = false;
            onRightEdge = false;
        }

        if (dy == 0) {
            if (y <= Margin) {
                onUpperEdge = true;
            } else if (y >= (height - Margin)) {
                onLowerEdge = true;
            }
        } else {
            onUpperEdge = false;
            onLowerEdge = false;
        }

        update();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
//        System.out.println("move!");
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void componentResized(ComponentEvent e) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.

        width = e.getComponent().getWidth();
        height = e.getComponent().getHeight();
        mouseP.x = width / 2.0;
        mouseP.y = height / 2.0;
    }

    @Override
    public void componentMoved(ComponentEvent e) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void componentShown(ComponentEvent e) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void componentHidden(ComponentEvent e) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}

class Quaternion {

    public Quaternion(double x, double y, double z, double w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    Quaternion conjugate() {
        Quaternion ret = new Quaternion(-x, -y, -z, w);
        return ret;
    }

    Quaternion multipe(Vector v) {
        final double tw = -(x * v.get(0)) - (y * v.get(1)) - (z * v.get(2));
        final double tx = (w * v.get(0)) + (y * v.get(2)) - (z * v.get(1));
        final double ty = (w * v.get(1)) + (z * v.get(0)) - (x * v.get(2));
        final double tz = (w * v.get(2)) + (x * v.get(1)) - (y * v.get(0));

        Quaternion ret = new Quaternion(tx, ty, tz, tw);
        return ret;
    }

    public Quaternion multipe(Quaternion r) {
        final double tw = (w * r.w) - (x * r.x) - (y * r.y) - (z * r.z);
        final double tx = (x * r.w) + (w * r.x) + (y * r.z) - (z * r.y);
        final double ty = (y * r.w) + (w * r.y) + (z * r.x) - (x * r.z);
        final double tz = (z * r.w) + (w * r.z) + (x * r.y) - (y * r.x);

        Quaternion ret = new Quaternion(tx, ty, tz, tw);
        return ret;
    }
    double x, y, z, w;
}
