package io.github.hiiragi283.material.api.part

import com.google.common.collect.HashBasedTable
import com.google.common.collect.ImmutableTable
import com.google.common.collect.Table
import io.github.hiiragi283.material.api.material.HTMaterial
import io.github.hiiragi283.material.api.material.materials.HTElementMaterials
import io.github.hiiragi283.material.api.material.materials.HTVanillaMaterials
import io.github.hiiragi283.material.api.shape.HTShape
import io.github.hiiragi283.material.api.shape.HTShapes
import io.github.hiiragi283.material.common.HTMaterialsCommon
import io.github.hiiragi283.material.common.util.computeIfAbsent
import io.github.hiiragi283.material.common.util.getEntries
import io.github.hiiragi283.material.common.util.isAir
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents
import net.minecraft.item.Item
import net.minecraft.item.ItemConvertible
import net.minecraft.item.Items
import net.minecraft.util.registry.Registry

object HTPartManager {

    //    Item -> HTPart    //

    private val itemToPart: MutableMap<Item, HTPart> = mutableMapOf()

    @JvmField
    val ITEM_TO_PART: Map<Item, HTPart> = itemToPart

    @JvmStatic
    fun getPart(itemConvertible: ItemConvertible): HTPart? = itemToPart[itemConvertible.asItem()]

    @JvmStatic
    fun hasPart(itemConvertible: ItemConvertible): Boolean = itemConvertible.asItem() in itemToPart

    //    HTMaterial, HTShape -> Item    //

    private val partToItem: Table<HTMaterial, HTShape, Item> = HashBasedTable.create()

    @JvmStatic
    fun getDefaultItemTable(): ImmutableTable<HTMaterial, HTShape, Item> = ImmutableTable.copyOf(partToItem)

    @JvmStatic
    fun getDefaultItem(material: HTMaterial, shape: HTShape): Item? = partToItem.get(material, shape)

    @JvmStatic
    fun hasDefaultItem(material: HTMaterial, shape: HTShape): Boolean = partToItem.contains(material, shape)

    //    HTMaterial, HTShape -> Collection<Item>    //

    private val partToItems: Table<HTMaterial, HTShape, MutableSet<Item>> = HashBasedTable.create()

    @JvmStatic
    fun getPartToItemTable(): ImmutableTable<HTMaterial, HTShape, Collection<Item>> =
        ImmutableTable.copyOf(partToItems)

    @JvmStatic
    fun getItems(material: HTMaterial, shape: HTShape): Collection<Item> = partToItems.get(material, shape) ?: setOf()

    //    Initialization    //

    init {
        //Amethyst
        forceRegister(HTVanillaMaterials.AMETHYST, HTShapes.BLOCK, Items.AMETHYST_BLOCK)
        forceRegister(HTVanillaMaterials.AMETHYST, HTShapes.GEM, Items.AMETHYST_SHARD)
        //Andesite
        forceRegister(HTVanillaMaterials.ANDESITE, HTShapes.BLOCK, Items.ANDESITE)
        forceRegister(HTVanillaMaterials.ANDESITE, HTShapes.BLOCK, Items.POLISHED_ANDESITE)
        //Basalt
        forceRegister(HTVanillaMaterials.BASALT, HTShapes.BLOCK, Items.BASALT)
        forceRegister(HTVanillaMaterials.BASALT, HTShapes.BLOCK, Items.POLISHED_BASALT)
        //Brick
        forceRegister(HTVanillaMaterials.BRICK, HTShapes.BLOCK, Items.BRICKS)
        forceRegister(HTVanillaMaterials.BRICK, HTShapes.GEM, Items.BRICK)
        //Calcite
        forceRegister(HTVanillaMaterials.CALCITE, HTShapes.BLOCK, Items.CALCITE)
        //Charcoal
        forceRegister(HTVanillaMaterials.CHARCOAL, HTShapes.GEM, Items.CHARCOAL)
        //Clay
        forceRegister(HTVanillaMaterials.CLAY, HTShapes.BLOCK, Items.CLAY)
        forceRegister(HTVanillaMaterials.CLAY, HTShapes.GEM, Items.CLAY_BALL)
        //Coal
        forceRegister(HTVanillaMaterials.COAL, HTShapes.GEM, Items.COAL)
        forceRegister(HTVanillaMaterials.COAL, HTShapes.BLOCK, Items.COAL_BLOCK)
        //Copper
        forceRegister(HTElementMaterials.COPPER, HTShapes.BLOCK, Items.COPPER_BLOCK)
        forceRegister(HTElementMaterials.COPPER, HTShapes.INGOT, Items.COPPER_INGOT)
        forceRegister(HTElementMaterials.COPPER, HTShapes.ORE, Items.COPPER_ORE)
        forceRegister(HTElementMaterials.COPPER, HTShapes.RAW_BLOCK, Items.RAW_COPPER_BLOCK)
        forceRegister(HTElementMaterials.COPPER, HTShapes.RAW_ORE, Items.RAW_COPPER)
        //Deepslate
        forceRegister(HTVanillaMaterials.DEEPSLATE, HTShapes.BLOCK, Items.DEEPSLATE)
        //Diamond
        forceRegister(HTVanillaMaterials.DIAMOND, HTShapes.BLOCK, Items.DIAMOND_BLOCK)
        forceRegister(HTVanillaMaterials.DIAMOND, HTShapes.GEM, Items.DIAMOND)
        forceRegister(HTVanillaMaterials.DIAMOND, HTShapes.ORE, Items.DIAMOND_ORE)
        //Diorite
        forceRegister(HTVanillaMaterials.DIORITE, HTShapes.BLOCK, Items.DIORITE)
        forceRegister(HTVanillaMaterials.DIORITE, HTShapes.BLOCK, Items.POLISHED_DIORITE)
        //Dripstone
        forceRegister(HTVanillaMaterials.DRIPSTONE, HTShapes.BLOCK, Items.DRIPSTONE_BLOCK)
        //Emerald
        forceRegister(HTVanillaMaterials.EMERALD, HTShapes.BLOCK, Items.EMERALD_BLOCK)
        forceRegister(HTVanillaMaterials.EMERALD, HTShapes.GEM, Items.EMERALD)
        forceRegister(HTVanillaMaterials.EMERALD, HTShapes.ORE, Items.EMERALD_ORE)
        //End Stone
        forceRegister(HTVanillaMaterials.END_STONE, HTShapes.BLOCK, Items.END_STONE)
        //Ender Pearl
        forceRegister(HTVanillaMaterials.ENDER_PEARL, HTShapes.GEM, Items.ENDER_PEARL)
        //Flint
        forceRegister(HTVanillaMaterials.FLINT, HTShapes.GEM, Items.FLINT)
        //Iron
        forceRegister(HTElementMaterials.IRON, HTShapes.BLOCK, Items.IRON_BLOCK)
        forceRegister(HTElementMaterials.IRON, HTShapes.INGOT, Items.IRON_INGOT)
        forceRegister(HTElementMaterials.IRON, HTShapes.NUGGET, Items.IRON_NUGGET)
        forceRegister(HTElementMaterials.IRON, HTShapes.ORE, Items.IRON_ORE)
        forceRegister(HTElementMaterials.IRON, HTShapes.RAW_BLOCK, Items.RAW_IRON_BLOCK)
        forceRegister(HTElementMaterials.IRON, HTShapes.RAW_ORE, Items.RAW_IRON)
        //Glass
        forceRegister(HTVanillaMaterials.GLASS, HTShapes.BLOCK, Items.GLASS)
        //Glowstone
        forceRegister(HTVanillaMaterials.GLOWSTONE, HTShapes.BLOCK, Items.GLOWSTONE)
        forceRegister(HTVanillaMaterials.GLOWSTONE, HTShapes.DUST, Items.GLOWSTONE_DUST)
        //Gold
        forceRegister(HTElementMaterials.GOLD, HTShapes.BLOCK, Items.GOLD_BLOCK)
        forceRegister(HTElementMaterials.GOLD, HTShapes.INGOT, Items.GOLD_INGOT)
        forceRegister(HTElementMaterials.GOLD, HTShapes.NUGGET, Items.GOLD_NUGGET)
        forceRegister(HTElementMaterials.GOLD, HTShapes.ORE, Items.GOLD_ORE)
        forceRegister(HTElementMaterials.GOLD, HTShapes.RAW_BLOCK, Items.RAW_GOLD_BLOCK)
        forceRegister(HTElementMaterials.GOLD, HTShapes.RAW_ORE, Items.RAW_GOLD)
        //Granite
        forceRegister(HTVanillaMaterials.GRANITE, HTShapes.BLOCK, Items.GRANITE)
        forceRegister(HTVanillaMaterials.GRANITE, HTShapes.BLOCK, Items.POLISHED_GRANITE)
        //Lapis
        forceRegister(HTVanillaMaterials.LAPIS, HTShapes.BLOCK, Items.LAPIS_BLOCK)
        forceRegister(HTVanillaMaterials.LAPIS, HTShapes.GEM, Items.LAPIS_LAZULI)
        forceRegister(HTVanillaMaterials.LAPIS, HTShapes.ORE, Items.LAPIS_ORE)
        //Nether Brick
        forceRegister(HTVanillaMaterials.NETHER_BRICK, HTShapes.BLOCK, Items.NETHER_BRICKS)
        forceRegister(HTVanillaMaterials.NETHER_BRICK, HTShapes.GEM, Items.NETHER_BRICK)
        //Netherite
        forceRegister(HTVanillaMaterials.NETHERITE, HTShapes.BLOCK, Items.NETHERITE_BLOCK)
        forceRegister(HTVanillaMaterials.NETHERITE, HTShapes.INGOT, Items.NETHERITE_INGOT)
        //Netherrack
        forceRegister(HTVanillaMaterials.NETHERRACK, HTShapes.BLOCK, Items.NETHERRACK)
        //Obsidian
        forceRegister(HTVanillaMaterials.OBSIDIAN, HTShapes.BLOCK, Items.OBSIDIAN)
        //Prismarine
        forceRegister(HTVanillaMaterials.PRISMARINE, HTShapes.DUST, Items.PRISMARINE_CRYSTALS)
        forceRegister(HTVanillaMaterials.PRISMARINE, HTShapes.GEM, Items.PRISMARINE_SHARD)
        //Quartz
        forceRegister(HTVanillaMaterials.QUARTZ, HTShapes.BLOCK, Items.QUARTZ_BLOCK)
        forceRegister(HTVanillaMaterials.QUARTZ, HTShapes.GEM, Items.QUARTZ)
        forceRegister(HTVanillaMaterials.QUARTZ, HTShapes.ORE, Items.NETHER_QUARTZ_ORE)
        //Redstone
        forceRegister(HTVanillaMaterials.REDSTONE, HTShapes.BLOCK, Items.REDSTONE_BLOCK)
        forceRegister(HTVanillaMaterials.REDSTONE, HTShapes.DUST, Items.REDSTONE)
        forceRegister(HTVanillaMaterials.REDSTONE, HTShapes.ORE, Items.REDSTONE_ORE)
        //Stone
        forceRegister(HTVanillaMaterials.STONE, HTShapes.BLOCK, Items.STONE)
        //Tuff
        forceRegister(HTVanillaMaterials.TUFF, HTShapes.BLOCK, Items.TUFF)
        //Wood
        forceRegister(HTVanillaMaterials.WOOD, HTShapes.BLOCK, Items.OAK_PLANKS)
        forceRegister(HTVanillaMaterials.WOOD, HTShapes.BLOCK, Items.BIRCH_PLANKS)
        forceRegister(HTVanillaMaterials.WOOD, HTShapes.BLOCK, Items.SPRUCE_PLANKS)
        forceRegister(HTVanillaMaterials.WOOD, HTShapes.BLOCK, Items.JUNGLE_PLANKS)
        forceRegister(HTVanillaMaterials.WOOD, HTShapes.BLOCK, Items.ACACIA_PLANKS)
        forceRegister(HTVanillaMaterials.WOOD, HTShapes.BLOCK, Items.DARK_OAK_PLANKS)
        forceRegister(HTVanillaMaterials.WOOD, HTShapes.BLOCK, Items.CRIMSON_HYPHAE)
        forceRegister(HTVanillaMaterials.WOOD, HTShapes.BLOCK, Items.WARPED_HYPHAE)

        //Event
        ServerWorldEvents.LOAD.register { _, _ ->

            itemToPart.clear()
            partToItems.clear()

            HTMaterial.REGISTRY.forEach { material ->
                HTShapes.REGISTRY.forEach { shape ->
                    shape.getCommonTag(material)
                        .getEntries(Registry.ITEM)
                        .forEach { item -> register(material, shape, item) }
                }
            }

        }
    }

    //    Registration    //

    private fun checkItemNotAir(itemConvertible: ItemConvertible): Item = itemConvertible.asItem().also { item ->
        check(!item.isAir()) { "The Entry: $itemConvertible has no valid Item!" }
    }

    @JvmStatic
    @JvmSynthetic
    fun register(material: HTMaterial, shape: HTShape, itemConvertible: ItemConvertible) {
        //Check if the itemConvertible has non-air item
        val item: Item = checkItemNotAir(itemConvertible)
        //ItemConvertible -> HTPart
        itemToPart.putIfAbsent(item, HTPart(material, shape))
        //HTMaterial, HTShape -> ItemConvertible
        if (!partToItem.contains(material, shape)) {
            partToItem.put(material, shape, item)
            HTMaterialsCommon.LOGGER.info("The Item: ${Registry.ITEM.getId(item)} registered as Default Item for Material: $material and Shape: $shape!!")
        }
        //HTMaterial, HTShape -> Collection<ItemConvertible>
        partToItems.computeIfAbsent(material, shape) { _, _ -> mutableSetOf() }.add(item)
        //print info
        HTMaterialsCommon.LOGGER.info("The Item: ${Registry.ITEM.getId(item)} linked to Material: $material and Shape: $shape!")
    }

    @JvmSynthetic
    internal fun forceRegister(material: HTMaterial, shape: HTShape, itemConvertible: ItemConvertible) {
        //Check if the itemConvertible has non-air item
        val item: Item = checkItemNotAir(itemConvertible)
        //ItemConvertible -> HTPart
        itemToPart.putIfAbsent(item, HTPart(material, shape))
        //HTMaterial, HTShape -> ItemConvertible
        partToItem.put(material, shape, item)
        HTMaterialsCommon.LOGGER.info("The Item: ${Registry.ITEM.getId(item)} registered as Default Item for Material: $material and Shape: $shape!!")
        //HTMaterial, HTShape -> Collection<ItemConvertible>
        partToItems.computeIfAbsent(material, shape) { _, _ -> mutableSetOf() }.add(item)
        //print info
        HTMaterialsCommon.LOGGER.info("The Item: ${Registry.ITEM.getId(item)} linked to Material: $material and Shape: $shape!")
    }

}