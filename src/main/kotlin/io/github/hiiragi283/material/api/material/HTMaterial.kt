package io.github.hiiragi283.material.api.material

import io.github.hiiragi283.material.api.material.flag.HTMaterialFlag
import io.github.hiiragi283.material.api.material.flag.HTMaterialFlags
import io.github.hiiragi283.material.api.material.formula.FormulaConvertible
import io.github.hiiragi283.material.api.material.materials.HTElementMaterials
import io.github.hiiragi283.material.api.material.materials.HTVanillaMaterials
import io.github.hiiragi283.material.api.material.property.HTMaterialProperties
import io.github.hiiragi283.material.api.material.property.HTMaterialProperty
import io.github.hiiragi283.material.api.material.property.HTPropertyKey
import io.github.hiiragi283.material.api.shape.HTShape
import io.github.hiiragi283.material.common.HTLoadState
import io.github.hiiragi283.material.common.HTMaterialsCommon
import io.github.hiiragi283.material.common.util.commonId
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder
import net.fabricmc.fabric.api.event.registry.RegistryAttribute
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants
import net.minecraft.block.Block
import net.minecraft.block.MapColor
import net.minecraft.client.resource.language.I18n
import net.minecraft.text.TranslatableText
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry
import net.minecraft.util.registry.SimpleRegistry
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.awt.Color
import java.util.*

@Suppress("unused", "UnstableApiUsage")
class HTMaterial private constructor(
    private val info: Info,
    private val properties: HTMaterialProperties,
    private val flags: HTMaterialFlags
) : FormulaConvertible {

    companion object {

        private val logger: Logger = LogManager.getLogger("HTMaterial")

        @JvmField
        val REGISTRY: SimpleRegistry<HTMaterial> = FabricRegistryBuilder.createSimple(
            HTMaterial::class.java,
            HTMaterialsCommon.id("material")
        ).attribute(RegistryAttribute.SYNCED).buildAndRegister()

        @JvmStatic
        internal fun createMaterial(
            name: String,
            preInit: HTMaterial.() -> Unit = {},
            init: HTMaterial.() -> Unit = {},
        ): HTMaterial = HTMaterial(Info(name), HTMaterialProperties(), HTMaterialFlags())
            .also {
                check(HTMaterialsCommon.getLoadState() <= HTLoadState.PRE_INIT) {
                    "Cannot register material after Initialization!!"
                }
            }
            .apply(preInit)
            .apply(init)
            .also { mat ->
                Registry.register(REGISTRY, commonId(name), mat)
                logger.info("The Material: $mat registered!")
            }

        @JvmStatic
        fun getMaterial(name: String): HTMaterial? = REGISTRY.get(commonId(name))

        init {
            HTElementMaterials
            HTVanillaMaterials
        }

    }

    fun verify() {
        properties.verify(this)
        flags.verify(this)
    }

    //    Properties    //

    fun getProperties(): Collection<HTMaterialProperty<*>> = properties.values

    fun <T : HTMaterialProperty<T>> getProperty(key: HTPropertyKey<T>): T? = properties[key]

    fun <T : HTMaterialProperty<T>> getPropertyOptional(key: HTPropertyKey<T>): Optional<T> =
        Optional.ofNullable(getProperty(key))

    fun <T : HTMaterialProperty<T>> hasProperty(key: HTPropertyKey<T>): Boolean = key in properties

    fun modifyProperties(init: HTMaterialProperties.() -> Unit) {
        check(HTMaterialsCommon.getLoadState() <= HTLoadState.PRE_INIT) {
            "Cannot modify material properties after Initialization!!"
        }
        properties.init()
    }

    fun getDefaultShape(): HTShape? = when {
        hasProperty(HTPropertyKey.METAL) -> HTShape.INGOT
        hasProperty(HTPropertyKey.GEM) -> HTShape.GEM
        else -> null
    }

    //    Flags    //

    fun hasFlag(flag: HTMaterialFlag): Boolean = flag in flags

    fun modifyFlags(init: HTMaterialFlags.() -> Unit) {
        check(HTMaterialsCommon.getLoadState() <= HTLoadState.PRE_INIT) {
            "Cannot modify material flags after Initialization!!"
        }
        flags.init()
    }

    //    Info    //

    fun getInfo(): Info = info.copy()

    fun getName(): String = info.name

    fun getIdentifier(namespace: String = HTMaterialsCommon.MOD_ID): Identifier = Identifier(namespace, getName())

    fun getCommonId(): Identifier = commonId(getName())

    fun getColor(): Int = info.color

    private lateinit var formulaCache: String

    override fun asFormula(): String {
        check(HTMaterialsCommon.getLoadState() > HTLoadState.PRE_INIT) { "Cannot call #asFormula before Initialization!!" }
        if (!this::formulaCache.isInitialized) {
            formulaCache = info.formula.asFormula()
        }
        return formulaCache
    }

    fun getIngotCountPerBlock(): Int = info.ingotPerBlock

    fun getFluidAmountPerIngot(): Long = FluidConstants.BLOCK / getIngotCountPerBlock()

    fun getTranslatedName(): String = I18n.translate(info.translationKey)

    fun getTranslatedText(): TranslatableText = TranslatableText(info.translationKey)

    fun modifyInfo(init: Info.() -> Unit) {
        check(HTMaterialsCommon.getLoadState() <= HTLoadState.PRE_INIT) {
            "Cannot modify material infos after Initialization!!"
        }
        info.init()
    }

    data class Info(
        val name: String,
        var color: Int = -1,
        var formula: FormulaConvertible = FormulaConvertible.EMPTY,
        var ingotPerBlock: Int = 9,
        var translationKey: String = "ht_material.$name"
    ) {

        fun setColor(color: Color) {
            this.color = color.rgb
        }

        fun setColor(block: Block) {
            setColor(block.defaultMapColor)
        }

        fun setColor(mapColor: MapColor) {
            this.color = mapColor.color
        }

    }

    //    Any    //

    override fun equals(other: Any?): Boolean = when (other) {
        null -> false
        !is HTMaterial -> false
        else -> other.getName() == this.getName()
    }

    override fun hashCode(): Int = getName().hashCode()

    override fun toString(): String = getName()

}