package eu.midnightdust.midnightcontrols.client.touch;

import eu.midnightdust.midnightcontrols.client.MidnightControlsConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;

import static eu.midnightdust.midnightcontrols.client.MidnightReacharound.getPlayerRange;

public class TouchUtils {
    private static final MinecraftClient client = MinecraftClient.getInstance();
    public static final Matrix4f lastWorldSpaceMatrix = new Matrix4f();
    public static final Matrix4f lastProjMat = new Matrix4f();
    public static final Matrix4f lastModMat = new Matrix4f();
    public static HitResult getTargettedObject(double mouseX, double mouseY) {
        if (MidnightControlsConfig.touchMode == TouchMode.CROSSHAIR) {
            return client.crosshairTarget;
        }
        Vec3d near = screenSpaceToWorldSpace(mouseX, mouseY, 0);
        Vec3d far = screenSpaceToWorldSpace(mouseX, mouseY, 1);
        EntityHitResult entityCast = ProjectileUtil.raycast(client.player, near, far, Box.from(client.player.getPos()).expand(getPlayerRange(client)), entity -> (!entity.isSpectator() && entity.isAttackable()), getPlayerRange(client) * getPlayerRange(client));

        if (entityCast != null && entityCast.getType() == HitResult.Type.ENTITY) return entityCast;

        BlockHitResult result = client.world.raycast(new RaycastContext(near, far, RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.ANY, client.player));

        if (client.player.getPos().distanceTo(result.getPos()) > getPlayerRange(client)) return null;
        return result;
    }

    /* Taken from https://github.com/0x3C50/Renderer/blob/master/src/main/java/me/x150/renderer/util/RendererUtils.java#L270
     * Credits to 0x3C50 */
    public static Vec3d screenSpaceToWorldSpace(double x, double y, double d) {
        Camera camera = client.getEntityRenderDispatcher().camera;
        int displayHeight = client.getWindow().getScaledHeight();
        int displayWidth = client.getWindow().getScaledWidth();
        int[] viewport = new int[4];
        GL11.glGetIntegerv(GL11.GL_VIEWPORT, viewport);
        Vector3f target = new Vector3f();

        Matrix4f matrixProj = new Matrix4f(lastProjMat);
        Matrix4f matrixModel = new Matrix4f(lastModMat);

        matrixProj.mul(matrixModel)
                .mul(lastWorldSpaceMatrix)
                .unproject((float) x / displayWidth * viewport[2],
                        (float) (displayHeight - y) / displayHeight * viewport[3], (float) d, viewport, target);

        return new Vec3d(target.x, target.y, target.z).add(camera.getPos());
    }
}
