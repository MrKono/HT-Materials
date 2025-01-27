package io.github.hiiragi283.material.api.material.property

import io.github.hiiragi283.material.api.fluid.HTMaterialFluid
import io.github.hiiragi283.material.api.material.HTMaterial
import io.github.hiiragi283.material.api.part.HTPart
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributeHandler
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributes
import net.minecraft.fluid.Fluid
import net.minecraft.item.ItemStack
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import net.minecraft.text.Text
import net.minecraft.text.TranslatableText
import net.minecraft.world.World
import java.util.*

@Suppress("UnstableApiUsage")
class HTFluidProperty : HTMaterialProperty<HTFluidProperty> {

    lateinit var fluid: Fluid

    var temperature: Int = FluidConstants.WATER_TEMPERATURE
    var viscosity: Int = FluidConstants.WATER_VISCOSITY
    var isGas: Boolean = false

    override val key: HTPropertyKey<HTFluidProperty> = HTPropertyKey.FLUID

    override fun verify(material: HTMaterial) {

    }

    private val fluidVariant: FluidVariant by lazy { FluidVariant.of(fluid) }

    private lateinit var handlerCache: FluidVariantAttributeHandler

    override fun appendTooltip(part: HTPart, stack: ItemStack, lines: MutableList<Text>) {
        if (!::handlerCache.isInitialized) {
            handlerCache = FluidVariantAttributes.getHandlerOrDefault(fluid)
        }
        //Luminance
        handlerCache.getLuminance(fluidVariant).run {
            lines.add(TranslatableText("tooltip.ht_materials.material.luminance", this))
        }
        //Temperature
        handlerCache.getTemperature(fluidVariant).run {
            lines.add(TranslatableText("tooltip.ht_materials.material.temperature", this))
        }
        //Viscosity
        handlerCache.getViscosity(fluidVariant, null).run {
            lines.add(TranslatableText("tooltip.ht_materials.material.viscosity", this))
        }
        //Is gas
        val type: String = if (handlerCache.isLighterThanAir(fluidVariant)) "gas" else "fluid"
        val key = "tooltip.ht_materials.material.state.$type"
        lines.add(TranslatableText("tooltip.ht_materials.material.state", TranslatableText(key)))
    }

    internal fun init(material: HTMaterial) {
        if (this::fluid.isInitialized) return
        HTMaterialFluid.Flowing(material)
        HTMaterialFluid.Still(material).run {
            HTMaterialFluid.Bucket(this)
            HTMaterialFluid.Block(this)
            FluidVariantAttributes.register(this, object : FluidVariantAttributeHandler {

                override fun getFillSound(variant: FluidVariant): Optional<SoundEvent> =
                    Optional.of(SoundEvents.ITEM_BUCKET_FILL_LAVA)
                        .filter { temperature > FluidConstants.WATER_TEMPERATURE }

                override fun getEmptySound(variant: FluidVariant): Optional<SoundEvent> =
                    Optional.of(SoundEvents.ITEM_BUCKET_EMPTY_LAVA)
                        .filter { temperature > FluidConstants.WATER_TEMPERATURE }

                override fun getTemperature(variant: FluidVariant): Int = temperature

                override fun getViscosity(variant: FluidVariant, world: World?): Int = viscosity

                override fun isLighterThanAir(variant: FluidVariant): Boolean = isGas

            })
            fluid = this
        }
    }

}