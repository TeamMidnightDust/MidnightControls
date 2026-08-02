package eu.midnightdust.midnightcontrols.client.touch;

import com.mojang.blaze3d.systems.RenderSystem;
import eu.midnightdust.lib.util.PlatformFunctions;
import eu.midnightdust.midnightcontrols.client.MidnightControlsConfig;
import eu.midnightdust.midnightcontrols.client.enums.TouchMode;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;

import java.util.Arrays;

import static eu.midnightdust.midnightcontrols.client.MidnightReacharound.getPlayerRange;

public class TouchUtils {
    private static final Minecraft client = Minecraft.getInstance();
    public static final Matrix4f lastWorldSpaceMatrix = new Matrix4f();
    public static final Matrix4f lastProjMat = new Matrix4f();
    public static final Matrix4f lastModMat = new Matrix4f();

    public static HitResult getTargetedObject(double mouseX, double mouseY) {
        if (client.player == null || client.level == null || MidnightControlsConfig.touchMode == TouchMode.CROSSHAIR || PlatformFunctions.isModLoaded("vulkanmod")) {
            return client.hitResult;
        }
        Vec3 near = screenSpaceToWorldSpace(mouseX, mouseY, 0);
        Vec3 far = screenSpaceToWorldSpace(mouseX, mouseY, 1);

        float playerRange = getPlayerRange(client);
        EntityHitResult entityCast = ProjectileUtil.getEntityHitResult(client.player, near, far, AABB.unitCubeFromLowerCorner(client.player.position()).inflate(playerRange), entity -> (!entity.isSpectator() && entity.isAttackable()), playerRange * playerRange);

        if (entityCast != null && entityCast.getType() == HitResult.Type.ENTITY) return entityCast;

        BlockHitResult result = client.level.clip(new ClipContext(near, far, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, client.player));

        if (client.player.position().distanceTo(result.getLocation()) > playerRange) return null;
        return result;
    }

    /* Taken from https://github.com/0x3C50/Renderer/blob/master/src/main/java/me/x150/renderer/util/RendererUtils.java#L270
     * Credits to 0x3C50 */
    public static Vec3 screenSpaceToWorldSpace(double x, double y, double d) {
        Camera camera = client.getEntityRenderDispatcher().camera;
        int displayHeight = client.getWindow().getGuiScaledHeight();
        int displayWidth = client.getWindow().getGuiScaledWidth();
        int[] viewport = new int[4];
        viewport[0] = 0;
        viewport[1] = 0;
        viewport[2] = 128;
        viewport[3] = 128;
        Vector3f target = new Vector3f();

        Matrix4f matrixProj = new Matrix4f(lastProjMat);
        Matrix4f matrixModel = new Matrix4f(lastModMat);

        matrixProj.mul(matrixModel)
                .mul(lastWorldSpaceMatrix)
                .unproject((float) x / displayWidth * viewport[2],
                        (float) (displayHeight - y) / displayHeight * viewport[3], (float) d, viewport, target);

        return new Vec3(target.x, target.y, target.z).add(camera.position());
    }

    public static boolean hasInWorldUseAction(ItemStack stack) {
        ItemUseAnimation action = stack.getUseAnimation();
        return action == ItemUseAnimation.BOW || action == ItemUseAnimation.BRUSH || action == ItemUseAnimation.SPEAR;
    }
}
