package io.github.hiiragi283.material.api.addon

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment

@JvmDefaultWithCompatibility
interface HTMaterialsAddon {

    val modId: String

    fun registerShapes() {}

    fun registerMaterials() {}

    fun modifyMaterials() {}

    fun commonSetup() {}

    @Environment(EnvType.CLIENT)
    fun clientSetup() {
    }

}