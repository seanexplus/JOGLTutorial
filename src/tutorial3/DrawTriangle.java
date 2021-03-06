/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tutorial3;

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

public class DrawTriangle implements GLEventListener {

    
    IntBuffer vbo = IntBuffer.allocate(1);

    public static void main(String[] args) {
        GLProfile glp = GLProfile.getDefault();
        GLCapabilities caps = new GLCapabilities(glp);
        GLCanvas canvas = new GLCanvas(caps);

        JFrame frame = new JFrame("Draw Triangle");
        frame.setSize(300, 300);
        frame.add(canvas);
        frame.setVisible(true);

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        canvas.addGLEventListener(new DrawTriangle());

        FPSAnimator animator = new FPSAnimator(canvas, 60);
        animator.start();
    }

    @Override
    public void display(GLAutoDrawable drawable) {
        render(drawable);
    }

    @Override
    public void dispose(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        gl.glDeleteBuffers(1, vbo);
    }

    @Override
    public void init(GLAutoDrawable drawable) {
        GL4 gl = drawable.getGL().getGL4bc();
        initVertex(gl);
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int w, int h) {
    }

    

    private void render(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        gl.glClearColor(0.5f, 0.5f, 0.5f, 1f);
        gl.glClear(GL.GL_COLOR_BUFFER_BIT);
        gl.glEnableVertexAttribArray(0);

        gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, vbo.get(0));

        gl.glVertexAttribPointer(0, 3, GL2.GL_FLOAT, false, 0, 0);
        gl.glColor3f(0.7f, 0.2f, 0.1f);
        gl.glDrawArrays(GL2.GL_TRIANGLES, 0, 3);
        gl.glDisableVertexAttribArray(0);


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
        gl.glBufferData(GL4.GL_ARRAY_BUFFER, vertexs.limit() * Buffers.SIZEOF_FLOAT, vertexs, gl.GL_STATIC_DRAW);
    }
}
