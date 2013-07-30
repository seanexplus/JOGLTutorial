/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tutorial9;

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
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Interpolation implements GLEventListener {

    IntBuffer vbo = IntBuffer.allocate(1);
    int gWorldLocation;
    float Scale = 0f;

    public static void main(String[] args) {
        GLProfile glp = GLProfile.getDefault();
        GLCapabilities caps = new GLCapabilities(glp);
        GLCanvas canvas = new GLCanvas(caps);

        JFrame frame = new JFrame("Interpolation");
        frame.setSize(300, 300);
        frame.add(canvas);
        frame.setVisible(true);

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        canvas.addGLEventListener(new Interpolation());

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
    }

    @Override
    public void init(GLAutoDrawable drawable) {
        GL4 gl = drawable.getGL().getGL4();
        initVertex(gl);
        compileShader(gl);
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int w, int h) {
    }

    private void render(GLAutoDrawable drawable) {
        GL4 gl = drawable.getGL().getGL4();
        gl.glClearColor(0.5f, 0.5f, 0.5f, 1f);
        gl.glClear(GL4.GL_COLOR_BUFFER_BIT);

        Scale += 0.001f;

//        FloatBuffer World = FloatBuffer.allocate(9);
        float[] World = {(float)Math.sin(Scale), 0f, 0f, 0,
            0f, (float)Math.sin(Scale), 0f, 0f,
            0f, 0f, (float)Math.sin(Scale), 0f,
            0f, 0f, 0f, 1f};



        gl.glUniformMatrix4fv(gWorldLocation, 1, true, World, 0);



        gl.glEnableVertexAttribArray(0);
        gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, vbo.get(0));
        gl.glVertexAttribPointer(0, 3, GL2.GL_FLOAT, false, 0, 0);
        gl.glDrawArrays(GL2.GL_TRIANGLES, 0, 3);
        gl.glDisableVertexAttribArray(0);
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
            Logger.getLogger(Interpolation.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                is.close();
            } catch (IOException ex) {
                Logger.getLogger(Interpolation.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return result;

    }

    private void initVertex(GL4 gl) {
        float[] data = {
            -1.0f, -1.0f, 0.0f,
            1.0f, -1.0f, 0.0f,
            0.0f, 1.0f, 0.0f
        };


        FloatBuffer vertexs = FloatBuffer.wrap(data);

        gl.glGenBuffers(1, vbo);
        gl.glBindBuffer(GL4.GL_ARRAY_BUFFER, vbo.get(0));
        gl.glBufferData(GL4.GL_ARRAY_BUFFER, vertexs.limit() * Buffers.SIZEOF_FLOAT, vertexs, GL4.GL_STATIC_DRAW);
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
        int ShaderProgram = gl.glCreateProgram();

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
        gWorldLocation = gl.glGetUniformLocation(ShaderProgram, "gWorld");
        assert (gWorldLocation != 0xFFFFFFFF);
    }
}
