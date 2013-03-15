/*
 * Created on Feb 14, 2004
 */

package rubiks.cube;

/**
 * Implements one of the 27 cubelets that the cube is made of.
 * 
 * @author Michael Schubart (michael@schubart.net)
 */
class RCCubelet {

    // names for the faces
    public final static int FACE_NONE = -1; // none

    public final static int FACE_XV = 0; // orthogonal to x-axis and visible

    public final static int FACE_XH = 1; // orthogonal to x-axis and hidden

    public final static int FACE_YV = 2; // and so on

    public final static int FACE_YH = 3;

    public final static int FACE_ZV = 4;

    public final static int FACE_ZH = 5;

    // names for the axes
    public final static int AXIS_X = 0;

    public final static int AXIS_Y = 1;

    public final static int AXIS_Z = 2;

    // stores the color of each of the 6 faces
    protected byte[] colors = new byte[6];

    /**
     * Creates a cubelet. Each face has a different color.
     */
    public RCCubelet() {
        for (byte i = 0; i < 6; i++) {
            colors[i] = i;
        }
    }
public RCCubelet(RCCubelet r){
        System.arraycopy(r.colors, 0, colors, 0, 6);
}
public boolean equals(Object obj){
    if (obj instanceof RCCubelet){
        RCCubelet rc=(RCCubelet)obj;
        for (int i=0; i<6; i++){
            if (rc.colors[i]!=colors[i]){
                return false;
            }
        }
        return true;
    }return false;
}
    /**
     * Returns the color of a face.
     * 
     * @param face the index of the face.
     * @return the color.
     */
    public int getColor(int face) {
        return colors[face];
    }

    //=========================================================================
    // Rotate the cubelet.
    // axis: the axis around which to rotate
    // direction: the direction in which to rotate
    //=========================================================================
    public void rotate(int axis, int direction) {
        byte buffer;

        switch (axis) {
        case AXIS_X:
            if (direction == 1) {
                buffer = colors[FACE_YV];
                colors[FACE_YV] = colors[FACE_ZH];
                colors[FACE_ZH] = colors[FACE_YH];
                colors[FACE_YH] = colors[FACE_ZV];
                colors[FACE_ZV] = buffer;

            } else if (direction == -1) {
                buffer = colors[FACE_YV];
                colors[FACE_YV] = colors[FACE_ZV];
                colors[FACE_ZV] = colors[FACE_YH];
                colors[FACE_YH] = colors[FACE_ZH];
                colors[FACE_ZH] = buffer;
            }
            break;

        case AXIS_Y:
            if (direction == 1) {
                buffer = colors[FACE_XV];
                colors[FACE_XV] = colors[FACE_ZV];
                colors[FACE_ZV] = colors[FACE_XH];
                colors[FACE_XH] = colors[FACE_ZH];
                colors[FACE_ZH] = buffer;

            } else if (direction == -1) {
                buffer = colors[FACE_XV];
                colors[FACE_XV] = colors[FACE_ZH];
                colors[FACE_ZH] = colors[FACE_XH];
                colors[FACE_XH] = colors[FACE_ZV];
                colors[FACE_ZV] = buffer;
            }
            break;

        case AXIS_Z:
            if (direction == 1) {
                buffer = colors[FACE_XV];
                colors[FACE_XV] = colors[FACE_YH];
                colors[FACE_YH] = colors[FACE_XH];
                colors[FACE_XH] = colors[FACE_YV];
                colors[FACE_YV] = buffer;

            } else if (direction == -1) {
                buffer = colors[FACE_XV];
                colors[FACE_XV] = colors[FACE_YV];
                colors[FACE_YV] = colors[FACE_XH];
                colors[FACE_XH] = colors[FACE_YH];
                colors[FACE_YH] = buffer;
            }
            break;
        }
    }
}
