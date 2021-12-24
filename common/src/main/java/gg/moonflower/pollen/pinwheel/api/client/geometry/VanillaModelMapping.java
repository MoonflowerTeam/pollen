package gg.moonflower.pollen.pinwheel.api.client.geometry;

import com.google.common.collect.ImmutableMap;
import net.minecraft.client.model.*;
import net.minecraft.client.model.dragon.DragonHeadModel;
import net.minecraft.client.model.geom.ModelPart;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Maps all string mojmap names to model fields.
 *
 * @author Ocelot
 * @since 1.0.0
 */
public final class VanillaModelMapping {

    private static final Map<Class<? extends Model>, Map<String, String>> MAPPING = new HashMap<>();

    static {
        add(DragonHeadModel.class, "head", "jaw");
        add(ArmorStandModel.class, "bodyStick1", "bodyStick2", "shoulderStick", "basePlate");
        add(BatModel.class, "head", "body", "rightWing", "leftWing", "rightWingTip", "leftWingTip");
        add(BeeModel.class, "bone", "body", "rightWing", "leftWing", "frontLeg", "midLeg", "backLeg", "stinger", "leftAntenna", "rightAntenna");
        add(BlazeModel.class, "head");
        add(BoatModel.class, "waterPatch");
        add(BookModel.class, "leftLid", "rightLid", "leftPages", "rightPages", "flipPage1", "flipPage2", "seam");
        // No CatModel fields
        add(ChestedHorseModel.class, "boxL", "boxR");
        add(ChickenModel.class, "head", "body", "leg0", "leg1", "wing0", "wing1", "beak", "redThing");
        add(CodModel.class, "body", "topFin", "head", "nose", "sideFin0", "sideFin1", "tailFin");
        // No ColorableAgeableListModel fields
        // No ColorableListModel fields
        // No CowModel fields
        add(CreeperModel.class, "head", "hair", "body", "leg0", "leg1", "leg2", "leg3");
        add(DolphinModel.class, "body", "tail", "tailFin");
        // No DrownedModel fields
        add(ElytraModel.class, "rightWing", "leftWing");
        // No EndermanModel fields
        // No EndermiteModel fields
        // No EntityModel fields
        add(EvokerFangsModel.class, "base", "upperJaw", "lowerJaw");
        add(FoxModel.class, "head", "earL", "earR", "nose", "body", "leg0", "leg1", "leg2", "leg3", "tail");
        // No GhastModel fields
        // No GiantZombieModel fields
        add(GuardianModel.class, "head", "eye");
        add(HoglinModel.class, "head", "rightEar", "leftEar", "body", "frontRightLeg", "frontLeftLeg", "backRightLeg", "backLeftLeg", "mane");
        add(HorseModel.class, "body", "headParts", "leg1", "leg2", "leg3", "leg4", "babyLeg1", "babyLeg2", "babyLeg3", "babyLeg4", "tail");
        add(HumanoidHeadModel.class, "hat");
        add(HumanoidModel.class, "head", "hat", "body", "rightArm", "leftArm", "rightLeg", "leftLeg");
        add(IllagerModel.class, "head", "hat", "body", "arms", "leftLeg", "rightLeg", "rightArm", "leftArm");
        add(IronGolemModel.class, "head", "body", "arm0", "arm1", "leg0", "leg1");
        add(LavaSlimeModel.class, "insideCube");
        add(LeashKnotModel.class, "knot");
        // No ListModel fields
        add(LlamaModel.class, "head", "body", "leg0", "leg1", "leg2", "leg3", "chest1", "chest2");
        add(LlamaSpitModel.class, "main");
        // No MinecartModel fields
        // No Model fields
        add(OcelotModel.class, "backLegL", "backLegR", "frontLegL", "frontLegR", "tail1", "tail2", "head", "body");
        // No PandaModel fields
        add(ParrotModel.class, "body", "tail", "wingLeft", "wingRight", "head", "head2", "beak1", "beak2", "feather", "legLeft", "legRight");
        add(PhantomModel.class, "body", "leftWingBase", "leftWingTip", "rightWingBase", "rightWingTip", "tailBase", "tailTip");
        add(PiglinModel.class, "earRight", "earLeft", "bodyDefault", "headDefault", "leftArmDefault", "rightArmDefault");
        // No PigModel fields
        add(PlayerModel.class, "leftSleeve", "rightSleeve", "leftPants", "rightPants", "jacket", "cloak", "ear");
        // No PolarBearModel fields
        add(PufferfishBigModel.class, "cube", "blueFin0", "blueFin1", "topFrontFin", "topMidFin", "topBackFin", "sideFrontFin0", "sideFrontFin1", "bottomFrontFin", "bottomBackFin", "bottomMidFin", "sideBackFin0", "sideBackFin1");
        add(PufferfishMidModel.class, "cube", "finBlue0", "finBlue1", "finTop0", "finTop1", "finSide0", "finSide1", "finSide2", "finSide3", "finBottom0", "finBottom1");
        add(PufferfishSmallModel.class, "cube", "eye0", "eye1", "fin0", "fin1", "finBack");
        add(QuadrupedModel.class, "head", "body", "leg0", "leg1", "leg2", "leg3");
        add(RabbitModel.class, "rearFootLeft", "rearFootRight", "haunchLeft", "haunchRight", "body", "frontLegLeft", "frontLegRight", "head", "earRight", "earLeft", "tail", "nose");
        add(RavagerModel.class, "head", "mouth", "body", "leg0", "leg1", "leg2", "leg3", "neck");
        add(SalmonModel.class, "bodyFront", "bodyBack", "head", "sideFin0", "sideFin1");
        // No SheepFurModel fields
        // No SheepModel fields
        add(ShieldModel.class, "plate", "handle");
        add(ShulkerBulletModel.class, "main");
        add(ShulkerModel.class, "base", "lid", "head");
        // No SilverfishModel fields
        // No SkeletonModel fields
        add(SkullModel.class, "head");
        add(SlimeModel.class, "cube", "eye0", "eye1", "mouth");
        add(SnowGolemModel.class, "piece1", "piece2", "head", "arm1", "arm2");
        add(SpiderModel.class, "head", "body0", "body1", "leg0", "leg1", "leg2", "leg3", "leg4", "leg5", "leg6", "leg7");
        add(SquidModel.class, "body");
        add(StriderModel.class, "rightLeg", "leftLeg", "body", "bristle0", "bristle1", "bristle2", "bristle3", "bristle4", "bristle5");
        add(TridentModel.class, "pole");
        add(TropicalFishModelA.class, "body", "tail", "leftFin", "rightFin", "topFin");
        add(TropicalFishModelB.class, "body", "tail", "leftFin", "rightFin", "topFin", "bottomFin");
        add(TurtleModel.class, "eggBelly");
        add(VexModel.class, "leftWing", "rightWing");
        add(VillagerModel.class, "head", "hat", "hatRim", "body", "jacket", "arms", "leg0", "leg1", "nose");
        add(WitchModel.class, "mole");
        // No WitherBossModel fields
        add(WolfModel.class, "head", "realHead", "body", "leg0", "leg1", "leg2", "leg3", "tail", "realTail", "upperBody");
        // No ZombieModel fields
        add(ZombieVillagerModel.class, "hatRim");
    }

    private VanillaModelMapping() {
    }

    private static void add(Class<? extends Model> clazz, String... mapping) {
        ImmutableMap.Builder<String, String> mappingBuilder = ImmutableMap.builder();

        Field[] fields = Arrays.stream(clazz.getDeclaredFields()).filter(field -> !field.isSynthetic() && ModelPart.class.isAssignableFrom(field.getType())).toArray(Field[]::new);
        if (fields.length != mapping.length)
            throw new IllegalStateException("Incorrect mapping configuration for " + clazz.getName() + ". Expected " + fields.length + " fields, got " + mapping.length);

        for (int i = 0; i < fields.length; i++)
            mappingBuilder.put(mapping[i], fields[i].getName());

        if (MAPPING.put(clazz, mappingBuilder.build()) != null)
            throw new AssertionError("Duplicate mappings for " + clazz.getName());
    }

    private static String getInternal(Class<? extends Model> clazz, String name) {
        if (!MAPPING.containsKey(clazz))
            return null;
        return MAPPING.get(clazz).get(name);
    }

    /**
     * Retrieves a mapped field by the specified name.
     *
     * @param clazz The class to retrieve to the field from
     * @param name  The name of the field to retrieve
     * @return The field from the class or its superclass
     */
    @Nullable
    @SuppressWarnings("unchecked")
    public static String get(Class<? extends Model> clazz, String name) {
        Class<? extends Model> parent = clazz;
        while (Model.class.isAssignableFrom(parent)) {
            String field = getInternal(parent, name);
            if (field != null)
                return field;
            if (!Model.class.isAssignableFrom(parent.getSuperclass()))
                break;
            parent = (Class<? extends Model>) parent.getSuperclass();
        }

        Map<String, String> fields = new HashMap<>();
        fields.put(name, name);
        MAPPING.put(clazz, fields);
        return name;
    }
}
