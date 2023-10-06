package hiiragi283.material.item

import hiiragi283.material.api.item.MaterialItem
import hiiragi283.material.api.material.HiiragiMaterial
import hiiragi283.material.init.HiiragiItems
import hiiragi283.material.init.HiiragiShapes
import hiiragi283.material.util.append
import hiiragi283.material.util.simpleItemModel
import net.minecraft.data.server.recipe.SmithingRecipeJsonBuilder
import net.minecraft.recipe.Ingredient
import net.minecraft.util.Identifier
import pers.solid.brrp.v1.api.RuntimeResourcePack
import pers.solid.brrp.v1.model.ModelJsonBuilder

class MaterialPlateItem(material: HiiragiMaterial) : MaterialItem(HiiragiShapes.PLATE, material) {

    override fun getModel(identifier: Identifier): ModelJsonBuilder = simpleItemModel("item/iron_ingot")

    override fun addRecipe(resourcePack: RuntimeResourcePack, identifier: Identifier) {
        //1x Ingot + 1x Hammer -> 1x Plate
        resourcePack.addRecipeAndAdvancement(
            identifier.append("_smithing"),
            SmithingRecipeJsonBuilder.create(
                Ingredient.fromTag(HiiragiShapes.INGOT.getPart(part.material).tagKey),
                Ingredient.ofItems(HiiragiItems.FORGE_HAMMER),
                this
            )
        )
    }

}