/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tutorial2;

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

public class DrawDot implements GLEventListener {

    
    IntBuffer vbo = IntBuffer.allocate(1);

    public static void main(String[] args) {
        GLProfile glp = GLProfile.getDefault();
        GLCapabilities caps = new GLCapabilities(glp);
        GLCanvas canvas = new GLCanvas(caps);

        JFrame frame = new JFrame("DrawDot");
        frame.setSize(300, 300);
        frame.add(canvas);
        frame.setVisible(true);

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        canvas.addGLEventListener(new DrawDot());

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
        GL2 gl = drawable.getGL().getGL2();

        float[] data = new float[24];
        
        for (int i = 0 ; i < 24 ;) {
            data[i++] = (float)(Math.random() * 2 - 1);
            data[i++] = (float)(Math.random() * 2 - 1);
            data[i++] = 0;
        }
        FloatBuffer vertexs = FloatBuffer.wrap(data);

        gl.glGenBuffers(1, vbo);
        gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, vbo.get(0));
        gl.glBufferData(GL2.GL_ARRAY_BUFFER, vertexs.limit() * Buffers.SIZEOF_FLOAT, vertexs, gl.GL_STATIC_DRAW);
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
        gl.glPointSize(8f);
        gl.glDrawArrays(GL2.GL_POINTS, 0, 8);
        gl.glDisableVertexAttribArray(0);
    }
}
