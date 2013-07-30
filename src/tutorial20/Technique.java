/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tutorial20;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.media.opengl.GL4;

/**
 *
 * @author user
 */
public abstract class Technique {

    public Technique(GL4 gl) {
        this.gl = gl;
        programId = 0;
    }

    boolean init() {
        programId = gl.glCreateProgram();

        if (programId == 0) {
            System.err.println("Error creating shader program");
            return false;
        }

        return true;
    }

    void enable() {
        gl.glUseProgram(programId);
    }

    @Override
    protected void finalize() {
        try {
            super.finalize();
        } catch (Throwable ex) {
            Logger.getLogger(Technique.class.getName()).log(Level.SEVERE, null, ex);
        }

        for (Integer shaderId : shaderObjList) {
            gl.glDeleteShader(programId);
        }

        if (programId != 0) {
            gl.glDeleteProgram(programId);
        }
    }
    
    protected String[] readShader(String path) {

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
            Logger.getLogger(Point_Light.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                is.close();
            } catch (IOException ex) {
                Logger.getLogger(Point_Light.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return result;

    }

    boolean addShader(int shaderType, String[] shaderText) {
        int ShaderObj = gl.glCreateShader(shaderType);

        if (ShaderObj == 0) {
            System.err.printf("Error creating shader type %d%n", shaderType);
            return false;
        }

        shaderObjList.add(ShaderObj);
        gl.glShaderSource(ShaderObj, 1, shaderText, null);
        gl.glCompileShader(ShaderObj);
        IntBuffer succes = IntBuffer.allocate(1);
        gl.glGetShaderiv(ShaderObj, GL4.GL_COMPILE_STATUS, succes);
        if (succes.get(0) != 1) {
            ByteBuffer InfoLog = ByteBuffer.allocate(1024);
            gl.glGetShaderInfoLog(ShaderObj, 1024, null, InfoLog);
            String info = new String(InfoLog.array());
            System.err.printf("Error compiling shader type %d: '%s'%n", shaderType, info);
            return false;
        }
        gl.glAttachShader(programId, ShaderObj);
        return true;
    }

    boolean link() {
        IntBuffer Success = IntBuffer.allocate(1);
        ByteBuffer errorlog = ByteBuffer.allocate(1024);
        gl.glLinkProgram(programId);
        gl.glGetProgramiv(programId, GL4.GL_LINK_STATUS, Success);
        if (Success.get(0) == 0) {
            gl.glGetProgramInfoLog(programId, 1024, null, errorlog);
            System.err.printf("Error linking shader program: '%s'\n", new String(errorlog.array()));
            return false;
        }

        gl.glValidateProgram(programId);
        gl.glGetProgramiv(programId, GL4.GL_VALIDATE_STATUS, Success);
        if (Success.get(0) == 0) {
            gl.glGetProgramInfoLog(programId, 1024, null, errorlog);
            System.err.printf("Invalid shader program: '%s'\n", new String(errorlog.array()));
            return false;
        }
        // Delete the intermediate shader objects that have been added to the program
        for ( Integer shaderId : shaderObjList) {
            gl.glDeleteShader(shaderId);
        }
        shaderObjList.clear();
        return true;
    }

    int getUniformLocation(String uniformName) {
        int location = gl.glGetUniformLocation(programId, uniformName);
        if (location == 0xFFFFFFFF) {
            System.err.printf("Warning! Unable to get the location of uniform '%s'%n", uniformName);
        }

        return location;
    }
    
    protected GL4 gl = null;
    protected int programId;
    private List<Integer> shaderObjList = new ArrayList<>();
}
