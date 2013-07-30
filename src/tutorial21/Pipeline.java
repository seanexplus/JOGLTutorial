/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tutorial21;

import org.la4j.factory.Basic1DFactory;
import org.la4j.factory.Basic2DFactory;
import org.la4j.matrix.Matrix;
import org.la4j.vector.Vector;

/**
 *
 * @author user
 */
public class Pipeline {

    public void scale(double scaleX, double scaleY, double scaleZ) {
        m_scale.set(0, scaleX);
        m_scale.set(1, scaleY);
        m_scale.set(2, scaleZ);
    }

    public void transfrom(double dX, double dY, double dZ) {
        m_worldPos.set(0, dX);
        m_worldPos.set(1, dY);
        m_worldPos.set(2, dZ);
    }

    public void rotate(double rotateX, double rotateY, double rotateZ) {
        m_rotateInfo.set(0, rotateX);
        m_rotateInfo.set(1, rotateY);
        m_rotateInfo.set(2, rotateZ);
    }

    Matrix initScaleTransform() {
        double[][] data = {
            {m_scale.get(0), 0, 0, 0},
            {0, m_scale.get(1), 0, 0},
            {0, 0, m_scale.get(2), 0},
            {0, 0, 0, 1}
        };
        return b2dFactory.createMatrix(data);
    }

    Matrix initRotateTransform() {
        final double x = Math.toRadians(m_rotateInfo.get(0));
        final double y = Math.toRadians(m_rotateInfo.get(1));
        final double z = Math.toRadians(m_rotateInfo.get(2));

        double[][] rotateX = {
            {1, 0, 0, 0},
            {0, Math.cos(x), -Math.sin(x), 0},
            {0, Math.sin(x), Math.cos(x), 0},
            {0, 0, 0, 1}
        };
        Matrix rx = b2dFactory.createMatrix(rotateX);

        double[][] rotateY = {
            {Math.cos(y), 0, -Math.sin(y), 0},
            {0, 1, 0, 0},
            {Math.sin(y), 0, Math.cos(y), 0},
            {0, 0, 0, 1}
        };
        Matrix ry = b2dFactory.createMatrix(rotateY);

        double[][] rotateZ = {
            {Math.cos(z), -Math.sin(z), 0, 0},
            {Math.sin(z), Math.cos(z), 0, 0},
            {0, 0, 1, 0},
            {0, 0, 0, 1}
        };
        Matrix rz = b2dFactory.createMatrix(rotateZ);
        return rz.multiply(ry).multiply(rx);
    }

    Matrix initTranslationTransform(double x,double y,double z) {
        double[][] trans = {
            {1, 0, 0, x},
            {0, 1, 0, y},
            {0, 0, 1, z},
            {0, 0, 0, 1}
        };
        return b2dFactory.createMatrix(trans);
    }

    Matrix initPerspectiveProj() {
        final double ar = Width / Height;
        final double zRange = zNear - zFar;
        final double tanHalfFOV = Math.tan(Math.toRadians(FOV / 2.0f));

        double[][] pers = {
            {1 / (tanHalfFOV * ar), 0, 0, 0},
            {0, 1 / tanHalfFOV, 0,0},
            {0, 0, (-zNear - zFar) / zRange, 2.0 * zFar * zNear / zRange},
            {0, 0, 1, 0}
        };

        return b2dFactory.createMatrix(pers);
    }
    
    static Vector cross(Vector v1,Vector v2) {
        double x = v1.get(1) * v2.get(2) - v1.get(2) * v2.get(1);
        double y = v1.get(2) * v2.get(0) - v1.get(0) * v2.get(2);
        double z = v1.get(0) * v2.get(1) - v1.get(1) * v2.get(0);
        return b1dFactory.createVector( new double[]{x,y,z});
    }
    
    Matrix initCameraTransform() {
        Vector n = target.normalize();
        Vector u = up.normalize();
        
        u = cross(u,n);
        Vector v = cross(n,u);
        
        double[][] result = {
            {u.get(0),u.get(1),u.get(2),0},
            {v.get(0),v.get(1),v.get(2),0},
            {n.get(0),n.get(1),n.get(2),0},
            {0,0,0,1}
        };
       
        return b2dFactory.createMatrix(result);
    }

//    double toRadian(double x) {
//        return x * Math.PI / 180;
//    }
    
    public Matrix getWorldTrans() {
        Matrix scaleTrans = initScaleTransform();
        Matrix rotateTrans = initRotateTransform();
        Matrix translationTrans = initTranslationTransform(m_worldPos.get(0),m_worldPos.get(1),m_worldPos.get(2));
        return translationTrans.multiply(rotateTrans).multiply(scaleTrans);
    }

    public Matrix getWVPTrans() {

        Matrix scaleTrans = initScaleTransform();
        Matrix rotateTrans = initRotateTransform();
        Matrix translationTrans = initTranslationTransform(m_worldPos.get(0),m_worldPos.get(1),m_worldPos.get(2));
        Matrix worldTrans = getWorldTrans();
        Matrix persProjTrans = initPerspectiveProj();
        Matrix cameraTranslationTrans = initTranslationTransform(-pos.get(0),-pos.get(1),-pos.get(2));
        Matrix cameraRotateTrans = initCameraTransform();
//        m_transformation = persProjTrans.multiply(cameraRotateTrans).multiply(cameraTranslationTrans).multiply(worldTrans);
        m_transformation = persProjTrans.multiply(cameraRotateTrans).multiply(cameraTranslationTrans).multiply(translationTrans).multiply(rotateTrans).multiply(scaleTrans);
//        m_transformation = translationTrans.multiply(rotateTrans).multiply(scaleTrans);

        return m_transformation;
    }

    public float[] toFloatArray(Matrix target) {
        float[] result = new float[16];
        result[0] = (float) target.get(0, 0);
        result[1] = (float) target.get(0, 1);
        result[2] = (float) target.get(0, 2);
        result[3] = (float) target.get(0, 3);
        result[4] = (float) target.get(1, 0);
        result[5] = (float) target.get(1, 1);
        result[6] = (float) target.get(1, 2);
        result[7] = (float) target.get(1, 3);
        result[8] = (float) target.get(2, 0);
        result[9] = (float) target.get(2, 1);
        result[10] = (float) target.get(2, 2);
        result[11] = (float) target.get(2, 3);
        result[12] = (float) target.get(3, 0);
        result[13] = (float) target.get(3, 1);
        result[14] = (float) target.get(3, 2);
        result[15] = (float) target.get(3, 3);
        return result;
    }

    public void setPerspectiveProj(double FOV, double Width, double Height, double zNear, double zFar) {
        this.FOV = FOV;
        this.Width = Width;
        this.Height = Height;
        this.zNear = zNear;
        this.zFar = zFar;
    }
    
    public void setCamera(Vector pos,Vector target,Vector up) {
        this.pos = pos;
        this.target = target;
        this.up = up;
    }
    
    double FOV;
    double Width;
    double Height;
    double zNear;
    double zFar;
    
    Vector m_scale = b1dFactory.createVector(new double[]{1, 1, 1});
    Vector m_worldPos = b1dFactory.createVector(new double[]{0, 0, 0});
    Vector m_rotateInfo = b1dFactory.createVector(new double[]{0, 0, 0});
    
    Vector pos,target,up;
    
    Matrix m_transformation;
    private static Basic2DFactory b2dFactory = new Basic2DFactory();
    public static Basic1DFactory b1dFactory = new Basic1DFactory();
}
