/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tutorial19;

import javax.media.opengl.GL4;
import org.la4j.vector.Vector;

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
        WorldMatrixLocation = gl.glGetUniformLocation(programId, "gWorld");
        samplerLocation = gl.glGetUniformLocation(programId, "gSampler");
        dirLightColorLocation = gl.glGetUniformLocation(programId, "gDirectionalLight.Color");
        dirLightAmbientIntensity = gl.glGetUniformLocation(programId, "gDirectionalLight.AmbientIntensity");
        dirLightDirection = gl.glGetUniformLocation(programId, "gDirectionalLight.Direction");
        dirLightDiffuseIntensity = gl.glGetUniformLocation(programId, "gDirectionalLight.DiffuseIntensity");
        eyeWorldPosLocation = gl.glGetUniformLocation(programId, "gEyeWorldPos");
        specularIntensityLocation = gl.glGetUniformLocation(programId, "gMatSpecularIntensity");
        specularPowerLocation = gl.glGetUniformLocation(programId, "gSpecularPower");


        if (dirLightAmbientIntensity == 0xFFFFFFFF
                || WVPLocation == 0xFFFFFFFF
                || samplerLocation == 0xFFFFFFFF
                || dirLightColorLocation == 0xFFFFFFFF
                || dirLightDirection == 0xFFFFFFFF
                || dirLightDiffuseIntensity == 0xFFFFFFFF
                || eyeWorldPosLocation == 0xFFFFFFFF
                || specularIntensityLocation == 0xFFFFFFFF
                || specularPowerLocation == 0xFFFFFFFF) {
            return false;
        }
        return true;
    }

    public void setWVP(final float[] WVP) {
        gl.glUniformMatrix4fv(WVPLocation, 1, true, WVP, 0);
    }

    public void setWorldMatrix(final float[] WorldInverse) {
        gl.glUniformMatrix4fv(WorldMatrixLocation, 1, true, WorldInverse, 0);
    }

    public void setTextureUnit(final int textureUnit) {
        gl.glUniform1i(samplerLocation, textureUnit);
    }

    public void setDirectionLight(final DirectionalLight light) {
        gl.glUniform3f(dirLightColorLocation, light.color[0], light.color[1], light.color[2]);
        gl.glUniform1f(dirLightAmbientIntensity, light.ambientIntensity);
        float length = (light.direction[0] * light.direction[0]) + (light.direction[1] * light.direction[1]) + (light.direction[2] * light.direction[2]);
        length = (float) Math.sqrt(length);
        gl.glUniform3f(dirLightDirection, light.direction[0] / length, light.direction[1] / length, light.direction[2] / length);
        gl.glUniform1f(dirLightDiffuseIntensity, light.diffuseIntensity);
    }

    public void setEyeWorldPos(final Vector EyeWorldPos) {
        gl.glUniform3f(eyeWorldPosLocation, (float)EyeWorldPos.get(0), (float)EyeWorldPos.get(1),(float)EyeWorldPos.get(2));
    }

    public void setMatSpecularIntensity(float Intensity) {
        gl.glUniform1f(specularIntensityLocation, Intensity);
    }

    void setMatSpecularPower(float Power) {
        gl.glUniform1f(specularPowerLocation, Power);
    }
    
    private int WVPLocation;
    private int WorldMatrixLocation;
    private int samplerLocation;
    private int dirLightColorLocation;
    private int dirLightAmbientIntensity;
    private int dirLightDirection;
    private int dirLightDiffuseIntensity;
    private int eyeWorldPosLocation;
    private int specularIntensityLocation;
    private int specularPowerLocation;
}

class DirectionalLight {

    float[] color = new float[3];
    float ambientIntensity;
    float[] direction = new float[3];
    float diffuseIntensity;
}
