/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tutorial17;

import javax.media.opengl.GL4;

/**
 *
 * @author user
 */
public class LightingTechnique extends Technique {

    public LightingTechnique(GL4 gl) {
        super(gl);
    }

    public boolean init() {
        if (!super.init()) {
            return false;
        }
        if (!addShader(GL4.GL_VERTEX_SHADER, readShader("vs.txt"))) {
            return false;
        }
        if (!addShader(GL4.GL_FRAGMENT_SHADER, readShader("fs.txt"))) {
            return false;
        }
        if (!link()) {
            return false;
        }

        WVPLocation = gl.glGetUniformLocation(programId, "gWVP");
        samplerLocation = gl.glGetUniformLocation(programId, "gSampler");
        dirLightColorLocation = gl.glGetUniformLocation(programId, "gDirectionalLight.Color");
        dirLightAmbientIntensity = gl.glGetUniformLocation(programId, "gDirectionalLight.AmbientIntensity");

        if (dirLightAmbientIntensity == 0xFFFFFFFF
                || WVPLocation == 0xFFFFFFFF
                || samplerLocation == 0xFFFFFFFF
                || dirLightColorLocation == 0xFFFFFFFF) {
            return false;
        }
        return true;
    }

    public void setWVP(final float[] WVP) {
        gl.glUniformMatrix4fv(WVPLocation, 1, true, WVP, 0);
    }
    
    public void setTextureUnit(final int textureUnit) {
        gl.glUniform1i(samplerLocation,textureUnit);
    }
    
    public void setDirectionLight(final DirectionalLight light) {
        gl.glUniform3f(dirLightColorLocation,light.color[0],light.color[1],light.color[2]);
        gl.glUniform1f(dirLightAmbientIntensity, light.ambientIntensity);
    }
    
    private int WVPLocation;
    private int samplerLocation;
    private int dirLightColorLocation;
    private int dirLightAmbientIntensity;
}

class DirectionalLight {

    float[] color = new float[3];
    float ambientIntensity;
}
