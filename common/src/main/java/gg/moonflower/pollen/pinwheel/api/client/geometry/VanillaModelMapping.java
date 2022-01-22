package gg.moonflower.pollen.pinwheel.api.client.geometry;

import com.google.common.collect.ImmutableMap;
import net.minecraft.client.model.*;
import net.minecraft.client.model.dragon.DragonHeadModel;
import net.minecraft.client.model.geom.ModelPart;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.ApiStatus;
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
    private static final Logger LOGGER = LogManager.getLogger();

    static {
        add(DragonHeadModel.class, "head", "jaw");
        add(ArmorStandModel.class, "bodyStick1", "bodyStick2", "shoulderStick", "basePlate");
        add(BatModel.class, "root", "head", "body", "rightWing", "leftWing", "rightWingTip", "leftWingTip");
        add(BeeModel.class, "bone", "rightWing", "leftWing", "frontLeg", "midLeg", "backLeg", "stinger", "leftAntenna", "rightAntenna");
        add(BlazeModel.class, "root", "head");
        add(BoatModel.class, "leftPaddle", "rightPaddle", "waterPatch");
        add(BookModel.class, "root", "leftLid", "rightLid", "leftPages", "rightPages", "flipPage1", "flipPage2");
        // No CatModel fields
        add(ChestedHorseModel.class, "leftChest", "rightChest");
        add(ChickenModel.class, "head", "body", "rightLeg", "leftLeg", "rightWing", "leftWing", "beak", "redThing");
        add(CodModel.class, "root", "tailFin");
        // No ColorableAgeableListModel fields
        // No ColorableListModel fields
        // No CowModel fields
        add(CreeperModel.class, "root", "head", "rightHindLeg", "leftHindLeg", "rightFrontLeg", "leftFrontLeg");
        add(DolphinModel.class, "root", "body", "tail", "tailFin");
        // No DrownedModel fields
        add(ElytraModel.class, "rightWing", "leftWing");
        // No EndermanModel fields
        add(EndermiteModel.class, "root");
        // No EntityModel fields
        add(EvokerFangsModel.class, "root", "base", "upperJaw", "lowerJaw");
        add(FoxModel.class, "head", "body", "rightHindLeg", "leftHindLeg", "rightFrontLeg", "leftFrontLeg", "tail");
        add(GhastModel.class, "root");
        // No GiantZombieModel fields
        add(GuardianModel.class, "root", "head", "eye");
        add(HoglinModel.class, "head", "rightEar", "leftEar", "body", "rightFrontLeg", "leftFrontLeg", "rightHindLeg", "leftHindLeg", "mane");
        add(HorseModel.class, "body", "headParts", "rightHindLeg", "leftHindLeg", "rightFrontLeg", "leftFrontLeg", "rightHindBabyLeg", "leftHindBabyLeg", "rightFrontBabyLeg", "leftFrontBabyLeg", "tail");
        add(HumanoidModel.class, "head", "hat", "body", "rightArm", "leftArm", "rightLeg", "leftLeg");
        add(IllagerModel.class, "root", "head", "hat", "arms", "leftLeg", "rightLeg", "rightArm", "leftArm");
        add(IronGolemModel.class, "root", "head", "rightArm", "leftArm", "rightLeg", "leftLeg");
        add(LavaSlimeModel.class, "root");
        add(LeashKnotModel.class, "root", "knot");
        // No ListModel fields
        add(LlamaModel.class, "head", "body", "rightHindLeg", "leftHindLeg", "rightFrontLeg", "leftFrontLeg", "rightChest", "leftChest");
        add(LlamaSpitModel.class, "root");
        add(MinecartModel.class, "root");
        // No Model fields
        add(OcelotModel.class, "leftHindLeg", "rightHindLeg", "leftFrontLeg", "rightFrontLeg", "tail1", "tail2", "head", "body");
        // No PandaModel fields
        add(ParrotModel.class, "root", "body", "tail", "leftWing", "rightWing", "head", "feather", "legLeft", "legRight");
        add(PhantomModel.class, "root", "leftWingBase", "leftWingTip", "rightWingBase", "rightWingTip", "tailBase", "tailTip");
        add(PiglinModel.class, "rightEar", "leftEar");
        // No PigModel fields
        add(PlayerModel.class, "leftSleeve", "rightSleeve", "leftPants", "rightPants", "jacket", "cloak", "ear");
        // No PolarBearModel fields
        add(PufferfishBigModel.class, "root", "leftBlueFin", "rightBlueFin");
        add(PufferfishMidModel.class, "root", "leftBlueFin", "rightBlueFin");
        add(PufferfishSmallModel.class, "root", "leftFin", "rightFin");
        add(QuadrupedModel.class, "head", "body", "rightHindLeg", "leftHindLeg", "rightFrontLeg", "leftFrontLeg");
        add(RabbitModel.class, "leftRearFoot", "rightRearFoot", "leftHaunch", "rightHaunch", "body", "leftFrontLeg", "rightFrontLeg", "head", "rightEar", "leftEar", "tail", "nose");
        add(RavagerModel.class, "root", "head", "mouth", "rightHindLeg", "leftHindLeg", "rightFrontLeg", "leftFrontLeg", "neck");
        add(SalmonModel.class, "root", "bodyBack");
        // No SheepFurModel fields
        // No SheepModel fields
        add(ShieldModel.class, "root", "plate", "handle");
        add(ShulkerBulletModel.class, "root", "main");
        add(ShulkerModel.class, "base", "lid", "head");
        add(SilverfishModel.class, "root");
        // No SkeletonModel fields
        add(SkullModel.class, "root", "head");
        add(SlimeModel.class, "root");
        add(SnowGolemModel.class, "root", "upperBody", "head", "leftArm", "rightArm");
        add(SpiderModel.class, "root", "head", "rightHindLeg", "leftHindLeg", "rightMiddleHindLeg", "leftMiddleHindLeg", "rightMiddleFrontLeg", "leftMiddleFrontLeg", "rightFrontLeg", "leftFrontLeg");
        add(SquidModel.class, "root");
        add(StriderModel.class, "root", "rightLeg", "leftLeg", "body", "rightBottomBristle", "rightMiddleBristle", "rightTopBristle", "leftTopBristle", "leftMiddleBristle", "leftBottomBristle");
        add(TridentModel.class, "root");
        add(TropicalFishModelA.class, "root", "tail");
        add(TropicalFishModelB.class, "root", "tail");
        add(TurtleModel.class, "eggBelly");
        add(VexModel.class, "leftWing", "rightWing");
        add(VillagerModel.class, "root", "head", "hat", "hatRim", "rightLeg", "leftLeg", "nose");
        // No WitchModel fields
        add(WitherBossModel.class, "root", "centerHead", "rightHead", "leftHead", "ribcage", "tail");
        add(WolfModel.class, "head", "realHead", "body", "rightHindLeg", "leftHindLeg", "rightFrontLeg", "leftFrontLeg", "tail", "realTail", "upperBody");
        // No ZombieModel fields
        add(ZombieVillagerModel.class, "hatRim");
    }

    private VanillaModelMapping() {
    }

    @ApiStatus.Internal
    public static void load() {
    }

    private static void add(Class<? extends Model> clazz, String... mapping) {
        ImmutableMap.Builder<String, String> mappingBuilder = ImmutableMap.builder();

        Field[] fields = Arrays.stream(clazz.getDeclaredFields()).filter(field -> !field.isSynthetic() && ModelPart.class.isAssignableFrom(field.getType())).toArray(Field[]::new);
        if (fields.length != mapping.length) {
            LOGGER.error("Incorrect mapping configuration for " + clazz.getName() + ". Expected " + fields.length + " fields, got " + mapping.length);
            return;
        }

        for (int i = 0; i < fields.length; i++)
            mappingBuilder.put(mapping[i], fields[i].getName());

        if (MAPPING.put(clazz, mappingBuilder.build()) != null)
            throw new RuntimeException("Duplicate mappings for " + clazz.getName());
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
