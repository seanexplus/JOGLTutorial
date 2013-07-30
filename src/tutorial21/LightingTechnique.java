/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tutorial21;

import java.util.Formatter;
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

        lightLocation.color = gl.glGetUniformLocation(programId, "gDirectionalLight.Base.Color");
        lightLocation.ambientIntensity = gl.glGetUniformLocation(programId, "gDirectionalLight.Base.AmbientIntensity");
        lightLocation.direction = gl.glGetUniformLocation(programId, "gDirectionalLight.Direction");
        lightLocation.diffuseIntensity = gl.glGetUniformLocation(programId, "gDirectionalLight.Base.DiffuseIntensity");

        eyeWorldPosLocation = gl.glGetUniformLocation(programId, "gEyeWorldPos");
        specularIntensityLocation = gl.glGetUniformLocation(programId, "gMatSpecularIntensity");
        specularPowerLocation = gl.glGetUniformLocation(programId, "gSpecularPower");

        numPointLightsLocation = gl.glGetUniformLocation(programId, "gNumPointLights");
        numSpotLightLocation = gl.glGetUniformLocation(programId, "gNumSpotLights");


        if (lightLocation.ambientIntensity == 0xFFFFFFFF
                || WVPLocation == 0xFFFFFFFF
                || samplerLocation == 0xFFFFFFFF
                || lightLocation.color == 0xFFFFFFFF
                || lightLocation.direction == 0xFFFFFFFF
                || lightLocation.diffuseIntensity == 0xFFFFFFFF
                || eyeWorldPosLocation == 0xFFFFFFFF
                || specularIntensityLocation == 0xFFFFFFFF
                || specularPowerLocation == 0xFFFFFFFF
                || numPointLightsLocation == 0xFFFFFFFF) {
            return false;
        }
        StringBuilder sb = new StringBuilder(64);
        Formatter formatter = new Formatter(sb);
        for (int i = 0; i < pointLightLocations.length; i++) {
            formatter.format("gPointLights[%d].Base.Color", i);
            pointLightLocations[i].color = gl.glGetUniformLocation(programId, sb.toString());
            sb.delete(0, sb.length());
            formatter.format("gPointLights[%d].Base.AmbientIntensity", i);
            pointLightLocations[i].ambientIntensity = gl.glGetUniformLocation(programId, sb.toString());
            sb.delete(0, sb.length());
            formatter.format("gPointLights[%d].Position", i);
            pointLightLocations[i].location = gl.glGetUniformLocation(programId, sb.toString());
            sb.delete(0, sb.length());
            formatter.format("gPointLights[%d].Base.DiffuseIntensity", i);
            pointLightLocations[i].diffuseIntensity = gl.glGetUniformLocation(programId, sb.toString());
            sb.delete(0, sb.length());
            formatter.format("gPointLights[%d].Atten.Constant", i);
            pointLightLocations[i].atten.constant = gl.glGetUniformLocation(programId, sb.toString());
            sb.delete(0, sb.length());
            formatter.format("gPointLights[%d].Atten.Linear", i);
            pointLightLocations[i].atten.linear = gl.glGetUniformLocation(programId, sb.toString());
            sb.delete(0, sb.length());
            formatter.format("gPointLights[%d].Atten.Exp", i);
            pointLightLocations[i].atten.exp = gl.glGetUniformLocation(programId, sb.toString());
            sb.delete(0, sb.length());
            if (pointLightLocations[i].color == 0xFFFFFFFF
                    || pointLightLocations[i].ambientIntensity == 0xFFFFFFFF
                    || pointLightLocations[i].location == 0xFFFFFFFF
                    || pointLightLocations[i].diffuseIntensity == 0xFFFFFFFF
                    || pointLightLocations[i].atten.constant == 0xFFFFFFFF
                    || pointLightLocations[i].atten.exp == 0xFFFFFFFF
                    || pointLightLocations[i].atten.linear == 0xFFFFFFFF) {
                return false;
            }
        }

        for (int i = 0; i < spotLightLocations.length; i++) {
            formatter.format("gSpotLights[%d].Base.Base.Color", i);
            spotLightLocations[i].color = gl.glGetUniformLocation(programId, sb.toString());
            sb.delete(0, sb.length());
            formatter.format("gSpotLights[%d].Base.Base.AmbientIntensity", i);
            spotLightLocations[i].ambientIntensity = gl.glGetUniformLocation(programId, sb.toString());
            sb.delete(0, sb.length());
            formatter.format("gSpotLights[%d].Base.Position", i);
            spotLightLocations[i].location = gl.glGetUniformLocation(programId, sb.toString());
            sb.delete(0, sb.length());
            formatter.format("gSpotLights[%d].Base.Base.DiffuseIntensity", i);
            spotLightLocations[i].diffuseIntensity = gl.glGetUniformLocation(programId, sb.toString());
            sb.delete(0, sb.length());
            formatter.format("gSpotLights[%d].Base.Atten.Constant", i);
            spotLightLocations[i].atten.constant = gl.glGetUniformLocation(programId, sb.toString());
            sb.delete(0, sb.length());
            formatter.format("gSpotLights[%d].Base.Atten.Linear", i);
            spotLightLocations[i].atten.linear = gl.glGetUniformLocation(programId, sb.toString());
            sb.delete(0, sb.length());
            formatter.format("gSpotLights[%d].Base.Atten.Exp", i);
            spotLightLocations[i].atten.exp = gl.glGetUniformLocation(programId, sb.toString());
            sb.delete(0, sb.length());
            formatter.format("gSpotLights[%d].Direction", i);
            spotLightLocations[i].direction = gl.glGetUniformLocation(programId, sb.toString());
            sb.delete(0, sb.length());
            formatter.format("gSpotLights[%d].Cutoff", i);
            spotLightLocations[i].cutoff = gl.glGetUniformLocation(programId, sb.toString());
            sb.delete(0, sb.length());
            if (spotLightLocations[i].color == 0xFFFFFFFF
                    || spotLightLocations[i].ambientIntensity == 0xFFFFFFFF
                    || spotLightLocations[i].location == 0xFFFFFFFF
                    || spotLightLocations[i].diffuseIntensity == 0xFFFFFFFF
                    || spotLightLocations[i].atten.constant == 0xFFFFFFFF
                    || spotLightLocations[i].atten.exp == 0xFFFFFFFF
                    || spotLightLocations[i].atten.linear == 0xFFFFFFFF
                    || spotLightLocations[i].direction == 0xFFFFFFFF
                    || spotLightLocations[i].cutoff == 0xFFFFFFFF) {
                return false;
            }
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
        gl.glUniform3f(lightLocation.color, light.color[0], light.color[1], light.color[2]);
        gl.glUniform1f(lightLocation.ambientIntensity, light.ambientIntensity);
        float length = (light.direction[0] * light.direction[0]) + (light.direction[1] * light.direction[1]) + (light.direction[2] * light.direction[2]);
        length = (float) Math.sqrt(length);
        gl.glUniform3f(lightLocation.direction, light.direction[0] / length, light.direction[1] / length, light.direction[2] / length);
        gl.glUniform1f(lightLocation.diffuseIntensity, light.diffuseIntensity);
    }

    public void setEyeWorldPos(final Vector EyeWorldPos) {
        gl.glUniform3f(eyeWorldPosLocation, (float) EyeWorldPos.get(0), (float) EyeWorldPos.get(1), (float) EyeWorldPos.get(2));
    }

    public void setMatSpecularIntensity(float Intensity) {
        gl.glUniform1f(specularIntensityLocation, Intensity);
    }

    void setMatSpecularPower(float Power) {
        gl.glUniform1f(specularPowerLocation, Power);
    }

    void setPointLights(final PointLight[] pLights) {
        int numLight = pLights.length;
        gl.glUniform1i(numPointLightsLocation, numLight);

        for (int i = 0; i < numLight; i++) {
            gl.glUniform3f(pointLightLocations[i].color, pLights[i].color[0], pLights[i].color[1], pLights[i].color[2]);
            gl.glUniform1f(pointLightLocations[i].ambientIntensity, pLights[i].ambientIntensity);
            gl.glUniform1f(pointLightLocations[i].diffuseIntensity, pLights[i].diffuseIntensity);
            gl.glUniform3f(pointLightLocations[i].location, pLights[i].position[0], pLights[i].position[1], pLights[i].position[2]);
            gl.glUniform1f(pointLightLocations[i].atten.constant, pLights[i].attenuation.constant);
            gl.glUniform1f(pointLightLocations[i].atten.linear, pLights[i].attenuation.linear);
            gl.glUniform1f(pointLightLocations[i].atten.exp, pLights[i].attenuation.exp);
        }
    }
    
    void setSpotLights(final SpotLight[] sLights) {
        int numLight = sLights.length;
        gl.glUniform1i(numSpotLightLocation, numLight);

        for (int i = 0; i < numLight; i++) {
            gl.glUniform3f(spotLightLocations[i].color, sLights[i].color[0], sLights[i].color[1], sLights[i].color[2]);
            gl.glUniform1f(spotLightLocations[i].ambientIntensity, sLights[i].ambientIntensity);
            gl.glUniform1f(spotLightLocations[i].diffuseIntensity, sLights[i].diffuseIntensity);
            gl.glUniform3f(spotLightLocations[i].location, sLights[i].position[0], sLights[i].position[1], sLights[i].position[2]);
            gl.glUniform1f(spotLightLocations[i].atten.constant, sLights[i].attenuation.constant);
            gl.glUniform1f(spotLightLocations[i].atten.linear, sLights[i].attenuation.linear);
            gl.glUniform1f(spotLightLocations[i].atten.exp, sLights[i].attenuation.exp);
            gl.glUniform3f(spotLightLocations[i].direction, sLights[i].direction[0], sLights[i].direction[1], sLights[i].direction[2]);
            gl.glUniform1f(spotLightLocations[i].cutoff, (float)(Math.cos(Math.toRadians(sLights[i].cutoff))));
        }
    }
    
    private int WVPLocation;
    private int WorldMatrixLocation;
    private int samplerLocation;
//    private int dirLightColorLocation;
//    private int dirLightAmbientIntensity;
//    private int dirLightDirection;
//    private int dirLightDiffuseIntensity;
    private int eyeWorldPosLocation;
    private int specularIntensityLocation;
    private int specularPowerLocation;
    private int numPointLightsLocation;
    private int numSpotLightLocation;

    class LightLocationIDS {

        int color, ambientIntensity, diffuseIntensity, direction;
    }
    private LightLocationIDS lightLocation = new LightLocationIDS();

    class PointLightLocationIDS {

        int color, ambientIntensity, diffuseIntensity, location;

        class Atten {

            int constant, linear, exp;
        }
        Atten atten = new Atten();
    }
    private PointLightLocationIDS[] pointLightLocations = new PointLightLocationIDS[MAX_POINT_LIGHTS];

    {
        for (int i = 0; i < MAX_POINT_LIGHTS; i++) {
            pointLightLocations[i] = new PointLightLocationIDS();
        }
    }

    class SpotLightLocationIDS extends PointLightLocationIDS {

        int direction, cutoff;
    }
    private SpotLightLocationIDS[] spotLightLocations = new SpotLightLocationIDS[MAX_POINT_LIGHTS];

    {
        for (int i = 0; i < MAX_POINT_LIGHTS; i++) {
            spotLightLocations[i] = new SpotLightLocationIDS();
        }
    }
    static int MAX_POINT_LIGHTS = 2;
}

class DirectionalLight extends BaseLight {

    float[] direction = new float[3];
}

class BaseLight {

    float[] color = new float[3];
    float ambientIntensity;
    float diffuseIntensity;
}

class PointLight extends BaseLight {

    float[] position = new float[3];
    Attenuation attenuation = new Attenuation();

    PointLight() {
        position[0] = 0;
        position[1] = 0;
        position[2] = 0;
        attenuation.constant = 1;
    }
}

class SpotLight extends PointLight {

    float[] direction;
    float cutoff;

    SpotLight() {
        direction = new float[]{0f, 0f, 0f};
        cutoff = 0f;
    }
}

class Attenuation {

    float constant, linear, exp;
}
