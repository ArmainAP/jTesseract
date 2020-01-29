package mygame;

import java.util.*;

import com.jme3.scene.Mesh;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.math.Vector4f;

import com.jme3.scene.VertexBuffer.Type;
import com.jme3.util.BufferUtils;

import com.jme3.math.FastMath;

public class Tesseract extends Mesh {

    //In 4 dimensional space, rotations are made around a plane
    //In 4 dimensional space, double rotations are also possible
    public enum RotationalPlane {
        XY,
        XZ,
        YZ,
        XW,
        YW,
        ZW,
        XYZW,
        XZYW,
        YZXW,
        None
    }
    
    /*
        A 4 dimensional hypercube contains 16 vertices that make up 8 cubes in the same way
        a 3 dimensional hypercube contains 8 vertices that make up 6 squares.
        A 2 dimensional hypercube contains 4 vertices.
    
        For easier visualization of the 4 dimensional box, I am assigning a color to each vertex.
        This means I cannot recycle vertex points leaving us with a total of 192 vertices. (8 cubes * 6 squares * 4 vertices)
     */
    private Vector4f[] m_boxBounds = new Vector4f[16];
    private Vector4f[] m_vertices4D = new Vector4f[192];

    private Vector3f[] m_vertices = new Vector3f[192];
    private Vector2f[] m_texCoords = new Vector2f[192];
    private List<Integer> m_triangles = new ArrayList<Integer>();

    private float[] m_colors = new float[32]; //8 cubes * 4 floats for RGBA
    private float[] m_vertexColors = new float[768]; //192 vertices * 4 floats for RGBA
    
    //To keep the sense of depth from the 4 dimension, we use a stereographic projection
    //The shadow of a 4 dimensional shape is 3 dimensional similar to the 2 dimensional shadow of a 3 dimensional shape
    private boolean b_isStereographicProjection = true;
    private float m_lightOnW = 50.f;

    public Tesseract() {
        this(new Vector4f(1.f, 1.f, 1.f, 1.f));
    }

    public Tesseract(Vector4f i_boxSize) {
        InitVerts(i_boxSize);
        InitColor();

        PrepareVertices();
        PrepareTexCoords();
        PrepareTriangles();
        PrepareColors();

        int[] triangles = new int[m_triangles.size()];
        for (int i = 0; i < m_triangles.size(); i++) {
            triangles[i] = m_triangles.get(i);
        }
        this.setBuffer(Type.Index, 3, BufferUtils.createIntBuffer(triangles));
        this.setBuffer(Type.TexCoord, 2, BufferUtils.createFloatBuffer(m_texCoords));
        this.setBuffer(Type.Color, 4, m_vertexColors);

        Project();
    }

    private void InitVerts(Vector4f i_boxSize) {
        m_boxBounds[0] = new Vector4f(-i_boxSize.x, i_boxSize.y, i_boxSize.z, -i_boxSize.w);
        m_boxBounds[1] = new Vector4f(i_boxSize.x, i_boxSize.y, i_boxSize.z, -i_boxSize.w);
        m_boxBounds[2] = new Vector4f(i_boxSize.x, -i_boxSize.y, i_boxSize.z, -i_boxSize.w);
        m_boxBounds[3] = new Vector4f(-i_boxSize.x, -i_boxSize.y, i_boxSize.z, -i_boxSize.w);
        m_boxBounds[4] = new Vector4f(-i_boxSize.x, i_boxSize.y, -i_boxSize.z, -i_boxSize.w);
        m_boxBounds[5] = new Vector4f(i_boxSize.x, i_boxSize.y, -i_boxSize.z, -i_boxSize.w);
        m_boxBounds[6] = new Vector4f(i_boxSize.x, -i_boxSize.y, -i_boxSize.z, -i_boxSize.w);
        m_boxBounds[7] = new Vector4f(-i_boxSize.x, -i_boxSize.y, -i_boxSize.z, -i_boxSize.w);
        m_boxBounds[8] = new Vector4f(-i_boxSize.x, i_boxSize.y, i_boxSize.z, i_boxSize.w);
        m_boxBounds[9] = new Vector4f(i_boxSize.x, i_boxSize.y, i_boxSize.z, i_boxSize.w);
        m_boxBounds[10] = new Vector4f(i_boxSize.x, -i_boxSize.y, i_boxSize.z, i_boxSize.w);
        m_boxBounds[11] = new Vector4f(-i_boxSize.x, -i_boxSize.y, i_boxSize.z, i_boxSize.w);
        m_boxBounds[12] = new Vector4f(-i_boxSize.x, i_boxSize.y, -i_boxSize.z, i_boxSize.w);
        m_boxBounds[13] = new Vector4f(i_boxSize.x, i_boxSize.y, -i_boxSize.z, i_boxSize.w);
        m_boxBounds[14] = new Vector4f(i_boxSize.x, -i_boxSize.y, -i_boxSize.z, i_boxSize.w);
        m_boxBounds[15] = new Vector4f(-i_boxSize.x, -i_boxSize.y, -i_boxSize.z, i_boxSize.w);
    }

    private void InitColor() {
        //i represents the cube we are assigning color to
        for (int i = 0; i < 8; i++) {
            switch (i) {
                case 0: {
                    m_colors[i * 4] = 0.75f; //Red Value
                    m_colors[i * 4 + 1] = 0.75f; //Green Value
                    m_colors[i * 4 + 2] = 0.75f; //Blue Value
                    m_colors[i * 4 + 3] = 1.f; //Alpha Value
                    break;
                }

                case 1: {
                    m_colors[i * 4] = 1.f; //Red Value
                    m_colors[i * 4 + 1] = 0.f; //Green Value
                    m_colors[i * 4 + 2] = 0.f; //Blue Value
                    m_colors[i * 4 + 3] = 1.f; //Alpha Value
                    break;
                }

                case 2: {
                    m_colors[i * 4] = 0.f; //Red Value
                    m_colors[i * 4 + 1] = 1.f; //Green Value
                    m_colors[i * 4 + 2] = 0.f; //Blue Value
                    m_colors[i * 4 + 3] = 1.f; //Alpha Value
                    break;
                }

                case 3: {
                    m_colors[i * 4] = 0.f; //Red Value
                    m_colors[i * 4 + 1] = 0.f; //Green Value
                    m_colors[i * 4 + 2] = 1.f; //Blue Value
                    m_colors[i * 4 + 3] = 1.f; //Alpha Value
                    break;
                }

                case 4: {
                    m_colors[i * 4] = 1.f; //Red Value
                    m_colors[i * 4 + 1] = 1.f; //Green Value
                    m_colors[i * 4 + 2] = 0.f; //Blue Value
                    m_colors[i * 4 + 3] = 1.f; //Alpha Value
                    break;
                }

                case 5: {
                    m_colors[i * 4] = 1.f; //Red Value
                    m_colors[i * 4 + 1] = 0.f; //Green Value
                    m_colors[i * 4 + 2] = 1.f; //Blue Value
                    m_colors[i * 4 + 3] = 1.f; //Alpha Value
                    break;
                }

                case 6: {
                    m_colors[i * 4] = 0.f; //Red Value
                    m_colors[i * 4 + 1] = 1.f; //Green Value
                    m_colors[i * 4 + 2] = 1.f; //Blue Value
                    m_colors[i * 4 + 3] = 1.f; //Alpha Value
                    break;
                }

                case 7: {
                    m_colors[i * 4] = 0.25f; //Red Value
                    m_colors[i * 4 + 1] = 0.25f; //Green Value
                    m_colors[i * 4 + 2] = 0.25f; //Blue Value
                    m_colors[i * 4 + 3] = 1.f; //Alpha Value
                    break;
                }
            }
        }
    }

    private void PrepareVertices() {
        //Cube 0
        m_vertices4D[0] = m_boxBounds[0];
        m_vertices4D[1] = m_boxBounds[1];
        m_vertices4D[2] = m_boxBounds[2];
        m_vertices4D[3] = m_boxBounds[3];

        m_vertices4D[4] = m_boxBounds[4];
        m_vertices4D[5] = m_boxBounds[0];
        m_vertices4D[6] = m_boxBounds[3];
        m_vertices4D[7] = m_boxBounds[7];

        m_vertices4D[8] = m_boxBounds[5];
        m_vertices4D[9] = m_boxBounds[1];
        m_vertices4D[10] = m_boxBounds[0];
        m_vertices4D[11] = m_boxBounds[4];

        m_vertices4D[12] = m_boxBounds[6];
        m_vertices4D[13] = m_boxBounds[2];
        m_vertices4D[14] = m_boxBounds[1];
        m_vertices4D[15] = m_boxBounds[5];

        m_vertices4D[16] = m_boxBounds[7];
        m_vertices4D[17] = m_boxBounds[3];
        m_vertices4D[18] = m_boxBounds[2];
        m_vertices4D[19] = m_boxBounds[6];

        m_vertices4D[20] = m_boxBounds[7];
        m_vertices4D[21] = m_boxBounds[6];
        m_vertices4D[22] = m_boxBounds[5];
        m_vertices4D[23] = m_boxBounds[4];

        //Cube 1
        m_vertices4D[24] = m_boxBounds[8];
        m_vertices4D[25] = m_boxBounds[9];
        m_vertices4D[26] = m_boxBounds[10];
        m_vertices4D[27] = m_boxBounds[11];

        m_vertices4D[28] = m_boxBounds[12];
        m_vertices4D[29] = m_boxBounds[8];
        m_vertices4D[30] = m_boxBounds[11];
        m_vertices4D[31] = m_boxBounds[15];

        m_vertices4D[32] = m_boxBounds[13];
        m_vertices4D[33] = m_boxBounds[9];
        m_vertices4D[34] = m_boxBounds[8];
        m_vertices4D[35] = m_boxBounds[12];

        m_vertices4D[36] = m_boxBounds[14];
        m_vertices4D[37] = m_boxBounds[10];
        m_vertices4D[38] = m_boxBounds[9];
        m_vertices4D[39] = m_boxBounds[13];

        m_vertices4D[40] = m_boxBounds[15];
        m_vertices4D[41] = m_boxBounds[11];
        m_vertices4D[42] = m_boxBounds[10];
        m_vertices4D[43] = m_boxBounds[14];

        m_vertices4D[44] = m_boxBounds[15];
        m_vertices4D[45] = m_boxBounds[14];
        m_vertices4D[46] = m_boxBounds[13];
        m_vertices4D[47] = m_boxBounds[12];

        //Cube 2
        m_vertices4D[48] = m_boxBounds[0];
        m_vertices4D[49] = m_boxBounds[1];
        m_vertices4D[50] = m_boxBounds[2];
        m_vertices4D[51] = m_boxBounds[3];

        m_vertices4D[52] = m_boxBounds[8];
        m_vertices4D[53] = m_boxBounds[0];
        m_vertices4D[54] = m_boxBounds[3];
        m_vertices4D[55] = m_boxBounds[11];

        m_vertices4D[56] = m_boxBounds[9];
        m_vertices4D[57] = m_boxBounds[1];
        m_vertices4D[58] = m_boxBounds[0];
        m_vertices4D[59] = m_boxBounds[8];

        m_vertices4D[60] = m_boxBounds[10];
        m_vertices4D[61] = m_boxBounds[2];
        m_vertices4D[62] = m_boxBounds[1];
        m_vertices4D[63] = m_boxBounds[9];

        m_vertices4D[64] = m_boxBounds[11];
        m_vertices4D[65] = m_boxBounds[3];
        m_vertices4D[66] = m_boxBounds[2];
        m_vertices4D[67] = m_boxBounds[10];

        m_vertices4D[68] = m_boxBounds[11];
        m_vertices4D[69] = m_boxBounds[10];
        m_vertices4D[70] = m_boxBounds[9];
        m_vertices4D[71] = m_boxBounds[8];

        //Cube 3
        m_vertices4D[72] = m_boxBounds[12];
        m_vertices4D[73] = m_boxBounds[13];
        m_vertices4D[74] = m_boxBounds[14];
        m_vertices4D[75] = m_boxBounds[15];

        m_vertices4D[76] = m_boxBounds[4];
        m_vertices4D[77] = m_boxBounds[12];
        m_vertices4D[78] = m_boxBounds[15];
        m_vertices4D[79] = m_boxBounds[7];

        m_vertices4D[80] = m_boxBounds[5];
        m_vertices4D[81] = m_boxBounds[13];
        m_vertices4D[82] = m_boxBounds[12];
        m_vertices4D[83] = m_boxBounds[4];

        m_vertices4D[84] = m_boxBounds[6];
        m_vertices4D[85] = m_boxBounds[14];
        m_vertices4D[86] = m_boxBounds[13];
        m_vertices4D[87] = m_boxBounds[5];

        m_vertices4D[88] = m_boxBounds[7];
        m_vertices4D[89] = m_boxBounds[15];
        m_vertices4D[90] = m_boxBounds[14];
        m_vertices4D[91] = m_boxBounds[6];

        m_vertices4D[92] = m_boxBounds[7];
        m_vertices4D[93] = m_boxBounds[6];
        m_vertices4D[94] = m_boxBounds[5];
        m_vertices4D[95] = m_boxBounds[4];

        //Cube 4
        m_vertices4D[96] = m_boxBounds[0];
        m_vertices4D[97] = m_boxBounds[8];
        m_vertices4D[98] = m_boxBounds[11];
        m_vertices4D[99] = m_boxBounds[3];

        m_vertices4D[100] = m_boxBounds[4];
        m_vertices4D[101] = m_boxBounds[0];
        m_vertices4D[102] = m_boxBounds[3];
        m_vertices4D[103] = m_boxBounds[7];

        m_vertices4D[104] = m_boxBounds[12];
        m_vertices4D[105] = m_boxBounds[8];
        m_vertices4D[106] = m_boxBounds[0];
        m_vertices4D[107] = m_boxBounds[4];

        m_vertices4D[108] = m_boxBounds[15];
        m_vertices4D[109] = m_boxBounds[11];
        m_vertices4D[110] = m_boxBounds[8];
        m_vertices4D[111] = m_boxBounds[12];

        m_vertices4D[112] = m_boxBounds[7];
        m_vertices4D[113] = m_boxBounds[3];
        m_vertices4D[114] = m_boxBounds[11];
        m_vertices4D[115] = m_boxBounds[15];

        m_vertices4D[116] = m_boxBounds[7];
        m_vertices4D[117] = m_boxBounds[15];
        m_vertices4D[118] = m_boxBounds[12];
        m_vertices4D[119] = m_boxBounds[4];

        //Cube 5
        m_vertices4D[120] = m_boxBounds[0];
        m_vertices4D[121] = m_boxBounds[1];
        m_vertices4D[122] = m_boxBounds[9];
        m_vertices4D[123] = m_boxBounds[8];

        m_vertices4D[124] = m_boxBounds[4];
        m_vertices4D[125] = m_boxBounds[0];
        m_vertices4D[126] = m_boxBounds[8];
        m_vertices4D[127] = m_boxBounds[12];

        m_vertices4D[128] = m_boxBounds[5];
        m_vertices4D[129] = m_boxBounds[1];
        m_vertices4D[130] = m_boxBounds[0];
        m_vertices4D[131] = m_boxBounds[4];

        m_vertices4D[132] = m_boxBounds[13];
        m_vertices4D[133] = m_boxBounds[9];
        m_vertices4D[134] = m_boxBounds[1];
        m_vertices4D[135] = m_boxBounds[5];

        m_vertices4D[136] = m_boxBounds[12];
        m_vertices4D[137] = m_boxBounds[8];
        m_vertices4D[138] = m_boxBounds[9];
        m_vertices4D[139] = m_boxBounds[13];

        m_vertices4D[140] = m_boxBounds[12];
        m_vertices4D[141] = m_boxBounds[13];
        m_vertices4D[142] = m_boxBounds[5];
        m_vertices4D[143] = m_boxBounds[4];

        //Cube 6
        m_vertices4D[144] = m_boxBounds[9];
        m_vertices4D[145] = m_boxBounds[1];
        m_vertices4D[146] = m_boxBounds[2];
        m_vertices4D[147] = m_boxBounds[10];

        m_vertices4D[148] = m_boxBounds[13];
        m_vertices4D[149] = m_boxBounds[9];
        m_vertices4D[150] = m_boxBounds[10];
        m_vertices4D[151] = m_boxBounds[14];

        m_vertices4D[152] = m_boxBounds[5];
        m_vertices4D[153] = m_boxBounds[1];
        m_vertices4D[154] = m_boxBounds[9];
        m_vertices4D[155] = m_boxBounds[13];

        m_vertices4D[156] = m_boxBounds[6];
        m_vertices4D[157] = m_boxBounds[2];
        m_vertices4D[158] = m_boxBounds[1];
        m_vertices4D[159] = m_boxBounds[5];

        m_vertices4D[160] = m_boxBounds[14];
        m_vertices4D[161] = m_boxBounds[10];
        m_vertices4D[162] = m_boxBounds[2];
        m_vertices4D[163] = m_boxBounds[6];

        m_vertices4D[164] = m_boxBounds[14];
        m_vertices4D[165] = m_boxBounds[6];
        m_vertices4D[166] = m_boxBounds[5];
        m_vertices4D[167] = m_boxBounds[13];

        //Cube 7
        m_vertices4D[168] = m_boxBounds[11];
        m_vertices4D[169] = m_boxBounds[10];
        m_vertices4D[170] = m_boxBounds[2];
        m_vertices4D[171] = m_boxBounds[3];

        m_vertices4D[172] = m_boxBounds[15];
        m_vertices4D[173] = m_boxBounds[11];
        m_vertices4D[174] = m_boxBounds[3];
        m_vertices4D[175] = m_boxBounds[7];

        m_vertices4D[176] = m_boxBounds[14];
        m_vertices4D[177] = m_boxBounds[10];
        m_vertices4D[178] = m_boxBounds[11];
        m_vertices4D[179] = m_boxBounds[15];

        m_vertices4D[180] = m_boxBounds[6];
        m_vertices4D[181] = m_boxBounds[2];
        m_vertices4D[182] = m_boxBounds[10];
        m_vertices4D[183] = m_boxBounds[14];

        m_vertices4D[184] = m_boxBounds[7];
        m_vertices4D[185] = m_boxBounds[3];
        m_vertices4D[186] = m_boxBounds[2];
        m_vertices4D[187] = m_boxBounds[6];

        m_vertices4D[188] = m_boxBounds[7];
        m_vertices4D[189] = m_boxBounds[6];
        m_vertices4D[190] = m_boxBounds[14];
        m_vertices4D[191] = m_boxBounds[15];
    }

    private void PrepareTexCoords() {
        for (int i = 0; i < 48; i++) {
            m_texCoords[i * 4] = new Vector2f(0, 0);
            m_texCoords[i * 4 + 1] = new Vector2f(1, 0);
            m_texCoords[i * 4 + 2] = new Vector2f(0, 1);
            m_texCoords[i * 4 + 3] = new Vector2f(1, 1);
        }
    }

    private void PrepareTriangles() {
        //Cube 0
        ConvertQuadToTriangles(3, 2, 1, 0);
        ConvertQuadToTriangles(7, 6, 5, 4);
        ConvertQuadToTriangles(11, 10, 9, 8);
        ConvertQuadToTriangles(15, 14, 13, 12);
        ConvertQuadToTriangles(19, 18, 17, 16);
        ConvertQuadToTriangles(23, 22, 21, 20);

        //Cube 1
        ConvertQuadToTriangles(24, 25, 26, 27);
        ConvertQuadToTriangles(28, 29, 30, 31);
        ConvertQuadToTriangles(32, 33, 34, 35);
        ConvertQuadToTriangles(36, 37, 38, 39);
        ConvertQuadToTriangles(40, 41, 42, 43);
        ConvertQuadToTriangles(44, 45, 46, 47);

        //Cube 2
        ConvertQuadToTriangles(48, 49, 50, 51);
        ConvertQuadToTriangles(52, 53, 54, 55);
        ConvertQuadToTriangles(56, 57, 58, 59);
        ConvertQuadToTriangles(60, 61, 62, 63);
        ConvertQuadToTriangles(64, 65, 66, 67);
        ConvertQuadToTriangles(68, 69, 70, 71);

        //Cube 3
        ConvertQuadToTriangles(72, 73, 74, 75);
        ConvertQuadToTriangles(76, 77, 78, 79);
        ConvertQuadToTriangles(80, 81, 82, 83);
        ConvertQuadToTriangles(84, 85, 86, 87);
        ConvertQuadToTriangles(88, 89, 90, 91);
        ConvertQuadToTriangles(92, 93, 94, 95);

        //Cube 4
        ConvertQuadToTriangles(96, 97, 98, 99);
        ConvertQuadToTriangles(100, 101, 102, 103);
        ConvertQuadToTriangles(104, 105, 106, 107);
        ConvertQuadToTriangles(108, 109, 110, 111);
        ConvertQuadToTriangles(112, 113, 114, 115);
        ConvertQuadToTriangles(116, 117, 118, 119);

        //Cube 5
        ConvertQuadToTriangles(120, 121, 122, 123);
        ConvertQuadToTriangles(124, 125, 126, 127);
        ConvertQuadToTriangles(128, 129, 130, 131);
        ConvertQuadToTriangles(132, 133, 134, 135);
        ConvertQuadToTriangles(136, 137, 138, 139);
        ConvertQuadToTriangles(140, 141, 142, 143);

        //Cube 6
        ConvertQuadToTriangles(144, 145, 146, 147);
        ConvertQuadToTriangles(148, 149, 150, 151);
        ConvertQuadToTriangles(152, 153, 154, 155);
        ConvertQuadToTriangles(156, 157, 158, 159);
        ConvertQuadToTriangles(160, 161, 162, 163);
        ConvertQuadToTriangles(164, 165, 166, 167);

        //Cube 7
        ConvertQuadToTriangles(168, 169, 170, 171);
        ConvertQuadToTriangles(172, 173, 174, 175);
        ConvertQuadToTriangles(176, 177, 178, 179);
        ConvertQuadToTriangles(180, 181, 182, 183);
        ConvertQuadToTriangles(184, 185, 186, 187);
        ConvertQuadToTriangles(188, 189, 190, 191);
    }

    private void PrepareColors() {
        for (int i = 0; i < 8; i++) {
            for (int j = i * 24; j < i * 24 + 24; j++) {
                m_vertexColors[j * 4] = m_colors[i];
                m_vertexColors[j * 4 + 1] = m_colors[i + 1];
                m_vertexColors[j * 4 + 2] = m_colors[i + 2];
                m_vertexColors[j * 4 + 3] = m_colors[i + 3];
            }
        }
    }

    //Project the points from 4D space into 3D space and update the mesh
    private void Project() {
        for (int i = 0; i < 192; i++) {
            if (b_isStereographicProjection) {
                m_vertices[i] = new Vector3f(
                        m_vertices4D[i].x * m_lightOnW / (m_lightOnW - m_vertices4D[i].w),
                        m_vertices4D[i].y * m_lightOnW / (m_lightOnW - m_vertices4D[i].w),
                        m_vertices4D[i].z * m_lightOnW / (m_lightOnW - m_vertices4D[i].w));
            } else {
                m_vertices[i] = new Vector3f(m_vertices4D[i].x, m_vertices4D[i].y, m_vertices4D[i].z);
            }
        }

        this.setBuffer(Type.Position, 3, BufferUtils.createFloatBuffer(m_vertices));
        this.updateBound();
    }

    private void ConvertQuadToTriangles(int i_v0, int i_v1, int i_v2, int i_v3) {
        m_triangles.add(i_v0);
        m_triangles.add(i_v1);
        m_triangles.add(i_v3);

        m_triangles.add(i_v1);
        m_triangles.add(i_v2);
        m_triangles.add(i_v3);
    }
    
    public void Rotate(float i_angle, RotationalPlane i_rotationPlane) {
        if(i_rotationPlane == RotationalPlane.None) return;
        
        float x, y, z, w;
        switch (i_rotationPlane) {
            case XY: {
                for (int i = 0; i < 192; i++) {
                    x = FastMath.cos(i_angle * FastMath.DEG_TO_RAD) * m_vertices4D[i].x - FastMath.sin(i_angle * FastMath.DEG_TO_RAD) * m_vertices4D[i].y;
                    y = FastMath.sin(i_angle * FastMath.DEG_TO_RAD) * m_vertices4D[i].x + FastMath.cos(i_angle * FastMath.DEG_TO_RAD) * m_vertices4D[i].y;
                    m_vertices4D[i] = new Vector4f(x, y, m_vertices4D[i].z, m_vertices4D[i].w);
                }
                break;
            }

            case XZ: {
                for (int i = 0; i < 192; i++) {
                    x = FastMath.cos(i_angle * FastMath.DEG_TO_RAD) * m_vertices4D[i].x - FastMath.sin(i_angle * FastMath.DEG_TO_RAD) * m_vertices4D[i].z;
                    z = FastMath.sin(i_angle * FastMath.DEG_TO_RAD) * m_vertices4D[i].x + FastMath.cos(i_angle * FastMath.DEG_TO_RAD) * m_vertices4D[i].z;
                    m_vertices4D[i] = new Vector4f(x, m_vertices4D[i].y, z, m_vertices4D[i].w);
                }
                break;
            }

            case YZ: {
                for (int i = 0; i < 192; i++) {
                    y = FastMath.cos(i_angle * FastMath.DEG_TO_RAD) * m_vertices4D[i].y - FastMath.sin(i_angle * FastMath.DEG_TO_RAD) * m_vertices4D[i].z;
                    z = FastMath.sin(i_angle * FastMath.DEG_TO_RAD) * m_vertices4D[i].y + FastMath.cos(i_angle * FastMath.DEG_TO_RAD) * m_vertices4D[i].z;
                    m_vertices4D[i] = new Vector4f(m_vertices4D[i].x, y, z, m_vertices4D[i].w);
                }
                break;
            }

            case XW: {
                for (int i = 0; i < 192; i++) {
                    x = FastMath.cos(i_angle * FastMath.DEG_TO_RAD) * m_vertices4D[i].x - FastMath.sin(i_angle * FastMath.DEG_TO_RAD) * m_vertices4D[i].w;
                    w = FastMath.sin(i_angle * FastMath.DEG_TO_RAD) * m_vertices4D[i].x + FastMath.cos(i_angle * FastMath.DEG_TO_RAD) * m_vertices4D[i].w;
                    m_vertices4D[i] = new Vector4f(x, m_vertices4D[i].y, m_vertices4D[i].z, w);
                }
                break;
            }

            case YW: {
                for (int i = 0; i < 192; i++) {
                    y = FastMath.cos(i_angle * FastMath.DEG_TO_RAD) * m_vertices4D[i].y - FastMath.sin(i_angle * FastMath.DEG_TO_RAD) * m_vertices4D[i].w;
                    w = FastMath.sin(i_angle * FastMath.DEG_TO_RAD) * m_vertices4D[i].y + FastMath.cos(i_angle * FastMath.DEG_TO_RAD) * m_vertices4D[i].w;
                    m_vertices4D[i] = new Vector4f(m_vertices4D[i].x, y, m_vertices4D[i].z, w);
                }
                break;
            }

            case ZW: {
                for (int i = 0; i < 192; i++) {
                    z = FastMath.cos(i_angle * FastMath.DEG_TO_RAD) * m_vertices4D[i].z - FastMath.sin(i_angle * FastMath.DEG_TO_RAD) * m_vertices4D[i].w;
                    w = FastMath.sin(i_angle * FastMath.DEG_TO_RAD) * m_vertices4D[i].z + FastMath.cos(i_angle * FastMath.DEG_TO_RAD) * m_vertices4D[i].w;
                    m_vertices4D[i] = new Vector4f(m_vertices4D[i].x, m_vertices4D[i].y, z, w);
                }
                break;
            }

            case XYZW: {
                for (int i = 0; i < 192; i++) {
                    x = FastMath.cos(i_angle * FastMath.DEG_TO_RAD) * m_vertices4D[i].x - FastMath.sin(i_angle * FastMath.DEG_TO_RAD) * m_vertices4D[i].y;
                    y = FastMath.sin(i_angle * FastMath.DEG_TO_RAD) * m_vertices4D[i].x + FastMath.cos(i_angle * FastMath.DEG_TO_RAD) * m_vertices4D[i].y;
                    z = FastMath.cos(i_angle * FastMath.DEG_TO_RAD) * m_vertices4D[i].z - FastMath.sin(i_angle * FastMath.DEG_TO_RAD) * m_vertices4D[i].w;
                    w = FastMath.sin(i_angle * FastMath.DEG_TO_RAD) * m_vertices4D[i].z + FastMath.cos(i_angle * FastMath.DEG_TO_RAD) * m_vertices4D[i].w;
                    m_vertices4D[i] = new Vector4f(x, y, z, w);
                }
                break;
            }

            case XZYW: {
                for (int i = 0; i < 192; i++) {
                    x = FastMath.cos(i_angle * FastMath.DEG_TO_RAD) * m_vertices4D[i].x - FastMath.sin(i_angle * FastMath.DEG_TO_RAD) * m_vertices4D[i].z;
                    z = FastMath.sin(i_angle * FastMath.DEG_TO_RAD) * m_vertices4D[i].x + FastMath.cos(i_angle * FastMath.DEG_TO_RAD) * m_vertices4D[i].z;
                    y = FastMath.cos(i_angle * FastMath.DEG_TO_RAD) * m_vertices4D[i].y - FastMath.sin(i_angle * FastMath.DEG_TO_RAD) * m_vertices4D[i].w;
                    w = FastMath.sin(i_angle * FastMath.DEG_TO_RAD) * m_vertices4D[i].y + FastMath.cos(i_angle * FastMath.DEG_TO_RAD) * m_vertices4D[i].w;
                    m_vertices4D[i] = new Vector4f(x, y, z, w);
                }
                break;
            }

            case YZXW: {
                for (int i = 0; i < 192; i++) {
                    y = FastMath.cos(i_angle * FastMath.DEG_TO_RAD) * m_vertices4D[i].y - FastMath.sin(i_angle * FastMath.DEG_TO_RAD) * m_vertices4D[i].z;
                    z = FastMath.sin(i_angle * FastMath.DEG_TO_RAD) * m_vertices4D[i].y + FastMath.cos(i_angle * FastMath.DEG_TO_RAD) * m_vertices4D[i].z;
                    x = FastMath.cos(i_angle * FastMath.DEG_TO_RAD) * m_vertices4D[i].x - FastMath.sin(i_angle * FastMath.DEG_TO_RAD) * m_vertices4D[i].w;
                    w = FastMath.sin(i_angle * FastMath.DEG_TO_RAD) * m_vertices4D[i].x + FastMath.cos(i_angle * FastMath.DEG_TO_RAD) * m_vertices4D[i].w;
                    m_vertices4D[i] = new Vector4f(x, y, z, w);
                }
                break;
            }
        }

        Project();
    }
    
    public void ToggleProjection()
    {
        b_isStereographicProjection = !b_isStereographicProjection;
        Project();
    }
}
