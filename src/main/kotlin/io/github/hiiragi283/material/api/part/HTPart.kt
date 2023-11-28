package io.github.hiiragi283.material.api.part

import io.github.hiiragi283.material.api.material.HTMaterial
import io.github.hiiragi283.material.api.shape.HTShape
import io.github.hiiragi283.material.common.HTMaterialsCommon
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.tag.TagKey
import net.minecraft.text.Text
import net.minecraft.text.TranslatableText
import net.minecraft.util.Identifier

data class HTPart(val material: HTMaterial, val shape: HTShape) {

    fun getTranslatedName(): String = shape.getTranslatedName(material)

    fun getTranslatedText(): TranslatableText = shape.getTranslatedText(material)

    fun getIdentifier(namespace: String = HTMaterialsCommon.MOD_ID): Identifier =
        shape.getIdentifier(material, namespace)

    fun getCommonId(): Identifier = shape.getCommonId(material)

    fun getCommonTag(): TagKey<Item> = shape.getCommonTag(material)

    fun appendTooltip(stack: ItemStack, lines: MutableList<Text>) {
        //Title
        lines.add(TranslatableText("tooltip.ht_materials.material.title"))
        //Name
        lines.add(TranslatableText("tooltip.ht_materials.material.name", getTranslatedName()))
        //Formula
        material.asFormula().takeIf(String::isNotEmpty)?.let { formula: String ->
            lines.add(TranslatableText("tooltip.ht_materials.material.formula", formula))
        }
        //Fluid Amount
        /*val fluidAmount: Long = material.getFluidAmountPerIngot() * stack.count
        lines.add(TranslatableText("tooltip.ht_materials.material.fluid_amount", fluidAmount))*/
        //Molar Mass
        material.asMolarMass().takeIf { it > 0 }?.let { molar: Int ->
            lines.add(TranslatableText("tooltip.ht_materials.material.molar", molar))
        }
        //Tooltip from Properties
        material.getProperties().forEach { it.appendTooltip(this, stack, lines) }
    }

}