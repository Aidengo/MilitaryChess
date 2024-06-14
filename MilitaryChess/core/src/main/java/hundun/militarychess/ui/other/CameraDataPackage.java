package hundun.militarychess.ui.other;

import lombok.Getter;

public class CameraDataPackage {
    public static float DEFAULT_CAMERA_ZOOM_WEIGHT = 3.7f;
    @Getter
    private float currentCameraX;
    @Getter
    private float currentCameraY;
    @Getter
    private float currentCameraZoomWeight;
    @Getter
    private boolean currentCameraZoomDirty;

    public static float cameraZoomWeightToZoomValue(float weight){
        //return weight <= 0 ? (float)Math.pow(2, weight) : (float)Math.log(weight + 2);
        return weight;
    }

    public void modifyCurrentCamera(Float deltaX, Float deltaY) {
        if (deltaX != null) {
            currentCameraX += deltaX;
        }
        if (deltaY != null) {
            currentCameraY += deltaY;
        }
    }

    public void modifyCurrentCameraZoomWeight(Float delta) {
        currentCameraZoomWeight += delta;
        currentCameraZoomWeight = Math.max(0.1f, currentCameraZoomWeight);
        currentCameraZoomDirty = true;
    }

    public boolean getAndClearCameraZoomDirty () {
        boolean result = currentCameraZoomDirty;
        currentCameraZoomDirty = false;
        return result;
    }

    public void forceSet(Float currentCameraX, Float currentCameraY, Float currentCameraZoomWeight) {
        if (currentCameraX != null) {
            this.currentCameraX = currentCameraX;
        }
        if (currentCameraY != null) {
            this.currentCameraY = currentCameraY;
        }
        if (currentCameraZoomWeight != null) {
            this.currentCameraZoomWeight = currentCameraZoomWeight;
            this.currentCameraZoomDirty = true;
        }
    }
}
