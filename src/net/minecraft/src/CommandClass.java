package net.minecraft.src;

import net.minecraft.client.Minecraft;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.io.*;
import java.util.*;
import java.util.List;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.minecraft.client.Minecraft;

public class CommandClass {
    public Minecraft mc;
    public EntityPlayer thePlayer;
    public World theWorld;
    public float speed;
    public int lastKey;
    public boolean godmode = false;
    public boolean fly = false;
    public boolean instamine = false;
    public boolean showHelpMessage = true;
    public static HashMap<String, double[]> waypoints;
    private final Map<String, Item> itemMap = this.initializeItemMap();
    private final Map<String, Block> blockMap = this.initializeBlockMap();
    public static final String[] direction_tags = new String[]{"south", "west", "north", "east"};

    public CommandClass(Minecraft mc, EntityPlayer thePlayer, World theWorld) {
        waypoints = new HashMap();
        this.theWorld = theWorld;
        this.mc = mc;
        this.thePlayer = thePlayer;
        this.sendModLoadedMessage();
        this.loadSettings();
        this.firstModLoad();
    }

    public void saveSettings() {
        try (FileWriter var1 = new FileWriter("Commands_options.txt")) {
            BufferedWriter var3 = new BufferedWriter(var1);
            var3.write("fly:" + this.fly);
            var3.newLine();
            var3.write("noclip:" + this.thePlayer.noClip);
            var3.newLine();
            var3.write("godmode:" + this.godmode);
            var3.newLine();
            var3.write("speed:" + this.speed);
            var3.newLine();
            var3.write("instamine:" + this.instamine);
            var3.newLine();
            var3.write("helpmsg:" + this.showHelpMessage);
            var3.newLine();
            var3.newLine();
            var3.write("Note: This file saves the states of certain commands before quitting a world.");
            var3.newLine();
            var3.write("The command states are reapplied as soon as you load back into a world.");
            var3.close();
        } catch (IOException var14) {
            var14.printStackTrace();
        }

    }

    public void loadSettings() {
        try (FileReader var1 = new FileReader("Commands_options.txt")) {
            BufferedReader var3 = new BufferedReader(var1);

            String var4;
            while((var4 = var3.readLine()) != null) {
                String[] var5 = var4.split(":");
                if (var5.length == 2) {
                    switch (var5[0]) {
                        case "fly":
                            this.fly = Boolean.parseBoolean(var5[1]);
                            break;
                        case "noclip":
                            this.thePlayer.noClip = Boolean.parseBoolean(var5[1]);
                            break;
                        case "godmode":
                            this.godmode = Boolean.parseBoolean(var5[1]);
                            break;
                        case "speed":
                            this.speed = Float.parseFloat(var5[1]);
                            break;
                        case "instamine":
                            this.instamine = Boolean.parseBoolean(var5[1]);
                            break;
                        case "helpmsg":
                            this.showHelpMessage = Boolean.parseBoolean(var5[1]);
                    }
                }
            }

            var3.close();
        } catch (FileNotFoundException var19) {
        } catch (IOException var20) {
            var20.printStackTrace();
        }

    }

    public void processCommand(String var1) {
        var1 = var1.toLowerCase();
        if (var1.startsWith("/")) {
            var1 = var1.substring(1);
            String[] var2 = var1.trim().split(" ");
            switch (var2[0].toLowerCase()) {
                case "info":
                    this.mc.ingameGUI.addChatMessage("§eMod version - v1.0");
                    this.mc.ingameGUI.addChatMessage("§eMod game version - a1.1.2_01");
                    this.mc.ingameGUI.addChatMessage("§bImplemented by ooney, code by Skellz64");
                    this.mc.ingameGUI.addChatMessage("§dInspired by Simo_415, and Mojang");
                    break;
                case "setspawn":
                case "setworldspawn":
                case "sws":
                    int var7 = this.mc.theWorld.spawnX;
                    int var4 = this.mc.theWorld.spawnY;
                    int var8 = this.mc.theWorld.spawnZ;
                    if (var2.length == 1) {
                        var7 = (int)this.thePlayer.posX;
                        var4 = (int)this.thePlayer.posY;
                        var8 = (int)this.thePlayer.posZ;
                    } else {
                        if (var2.length != 4) {
                            return;
                        }

                        try {
                            var7 = (int)Double.parseDouble(var2[1]);
                            var4 = (int)Double.parseDouble(var2[2]);
                            var8 = (int)Double.parseDouble(var2[3]);
                        } catch (Exception var46) {
                            return;
                        }
                    }

                    this.mc.theWorld.spawnX = var7;
                    this.mc.theWorld.spawnY = var4;
                    this.mc.theWorld.spawnZ = var8;
                    this.mc.ingameGUI.addChatMessage("Spawn set to x: " + var7 + " y: " + var4 + " z: " + var8);
                    break;
                case "set":
                    if (var2.length < 2) {
                        return;
                    }

                    String var56 = var1.substring(4).trim();
                    waypoints.put(var56, new double[]{this.thePlayer.posX, this.thePlayer.posY, this.thePlayer.posZ});
                    this.mc.ingameGUI.addChatMessage("Waypoint \"" + var56 + "\" set at: " + this.getPositionAsString());
                    break;
                case "goto":
                    if (var2.length < 2) {
                        return;
                    }

                    String var9 = var1.substring(5).trim();
                    if (waypoints.containsKey(var9)) {
                        double[] var57 = (double[])waypoints.get(var9);
                        this.thePlayer.setPosition(var57[0], var57[1], var57[2]);
                        this.mc.ingameGUI.addChatMessage("Moved Player to waypoint \"" + var9 + "\" §7" + this.getPositionAsString());
                    } else {
                        this.mc.ingameGUI.addChatMessage("Invalid waypoint name");
                    }
                    break;
                case "rem":
                    if (var2.length < 2) {
                        return;
                    }

                    String var10 = var1.substring(4).trim();
                    if (waypoints.containsKey(var10)) {
                        waypoints.remove(var10);
                        this.mc.ingameGUI.addChatMessage("Waypoint \"" + var10 + "\" removed");
                    } else {
                        this.mc.ingameGUI.addChatMessage("Invaid waypoint name");
                    }
                    break;
                case "home":
                    this.thePlayer.setLocationAndAngles((double)this.mc.theWorld.spawnX, (double)(this.mc.theWorld.spawnY - 1), (double)this.mc.theWorld.spawnZ, 0.0F, 0.0F);
                    this.mc.ingameGUI.addChatMessage("Sent Player to world spawn");
                    break;
                case "pos":
                    String var11 = this.getPositionAsString();
                    int var12 = MathHelper.floor_double((double)(this.thePlayer.rotationYaw * 4.0F / 360.0F) + (double)0.5F) & 3;
                    this.mc.ingameGUI.addChatMessage("Current position " + var11 + " §7(facing: " + direction_tags[var12] + ")");
                    break;
                case "kill":
                    this.thePlayer.health = 0;
                    float var13 = 1.0F;
                    float var14 = 1.0F;
                    this.mc.theWorld.playSoundAtEntity(this.thePlayer, "random.hurt", var13, var14);
                    break;
                case "clear":
                    for(int var52 = 0; var52 < 50; ++var52) {
                        this.mc.ingameGUI.addChatMessage("");
                    }
                    break;
                case "heal":
                    if (var2.length < 2) {
                        return;
                    }

                    try {
                        this.thePlayer.heal(Integer.parseInt(var2[1]));
                        this.mc.ingameGUI.addChatMessage("Healed " + var2[1] + " health points");
                        break;
                    } catch (Exception var45) {
                        this.mc.ingameGUI.addChatMessage("Invalid heal value");
                        return;
                    }
                case "health":
                    if (var2.length < 2) {
                        return;
                    }

                    String var15 = var2[1];

                    try {
                        int var58 = Integer.parseInt(var15);
                        this.thePlayer.health = var58;
                        this.mc.ingameGUI.addChatMessage("Health set to " + var58);
                    } catch (NumberFormatException var48) {
                        if (var15.equalsIgnoreCase("max")) {
                            this.thePlayer.health = 20;
                            this.mc.ingameGUI.addChatMessage("Health set to maximum");
                        } else if (var15.equalsIgnoreCase("min")) {
                            this.thePlayer.health = 1;
                            this.mc.ingameGUI.addChatMessage("Health set to minimum");
                        } else if (var15.equalsIgnoreCase("infinite")) {
                            this.thePlayer.health = 32767;
                            this.mc.ingameGUI.addChatMessage("Health set to infinite");
                        } else {
                            this.mc.ingameGUI.addChatMessage("Invalid health value or term");
                        }
                    }
                    break;
                case "listwaypoints":
                case "lwp":
                    int var3 = waypoints.size();
                    if (var3 == 0) {
                        this.mc.ingameGUI.addChatMessage("Create a waypoint first");
                        return;
                    }

                    Iterator var16 = waypoints.keySet().iterator();
                    String[] var17 = new String[5];
                    int var18 = 0;

                    while(var16.hasNext()) {
                        if (var17[var18] == null) {
                            var17[var18] = "";
                        }

                        String var60 = (String)var16.next();
                        if ((var60 + var17[var18]).length() > 98) {
                            ++var18;
                            if (var18 >= var17.length) {
                                break;
                            }

                            var17[var18] = var60;
                        } else {
                            if (!var17[var18].isEmpty()) {
                                var17[var18] = var17[var18] + ", ";
                            }

                            var17[var18] = var17[var18] + var60;
                        }
                    }

                    for(int var61 = 0; var61 < var17.length && var17[var61] != null; ++var61) {
                        this.mc.ingameGUI.addChatMessage("Waypoints (" + var3 + "): " + var17[var61]);
                    }
                    break;
                case "setspeed":
                case "speed":
                case "sp":
                    this.speed = 1.0F;
                    if (var2.length < 2) {
                        return;
                    }

                    if (var2[1].equalsIgnoreCase("reset")) {
                        this.speed = 1.0F;
                        this.mc.ingameGUI.addChatMessage("Speed set to " + this.speed);
                        this.saveSettings();
                    } else {
                        try {
                            Float var59 = Float.parseFloat(var2[1]);
                            this.speed = var59 > 1.0F ? var59 : 1.0F;
                            this.mc.ingameGUI.addChatMessage("Speed set to " + this.speed);
                            if ((double)this.speed == (double)100.0F) {
                                this.mc.ingameGUI.addChatMessage("§7Warning: extreme speed value");
                            }

                            this.saveSettings();
                        } catch (Exception var44) {
                            this.mc.ingameGUI.addChatMessage("Invalid speed value");
                            return;
                        }
                    }
                    break;
                case "fly":
                    if (this.thePlayer.noClip) {
                        this.mc.ingameGUI.addChatMessage("§7Turn off noclip first");
                    } else {
                        this.fly = !this.fly;
                        if (this.fly) {
                            this.mc.ingameGUI.addChatMessage("Flying enabled");
                            if (this.speed <= 1.0F) {
                                this.mc.ingameGUI.addChatMessage("§7Use /speed to fly faster");
                            }

                            this.saveSettings();
                        } else {
                            this.mc.ingameGUI.addChatMessage("Flying disabled");
                            this.saveSettings();
                        }
                    }
                    break;
                case "noclip":
                case "nc":
                    this.thePlayer.noClip = !this.thePlayer.noClip;
                    if (this.thePlayer.noClip) {
                        this.fly = true;
                        this.mc.ingameGUI.addChatMessage("Noclip enabled");
                        this.saveSettings();
                    } else {
                        this.fly = false;
                        this.mc.ingameGUI.addChatMessage("Noclip disabled");
                        this.saveSettings();
                    }
                    break;
                case "godmode":
                case "gm":
                    this.godmode = !this.godmode;
                    if (this.godmode) {
                        this.mc.ingameGUI.addChatMessage("Godmode enabled");
                        this.saveSettings();
                    } else {
                        this.mc.ingameGUI.addChatMessage("Godmode disabled");
                        this.saveSettings();
                    }
                    break;
                case "damage":
                    this.godmode = !this.godmode;
                    if (this.godmode) {
                        this.mc.ingameGUI.addChatMessage("Godmode enabled");
                    } else {
                        this.mc.ingameGUI.addChatMessage("Godmode disabled");
                    }

                    this.saveSettings();
                    break;
                case "instamine":
                case "im":
                    this.instamine = !this.instamine;
                    if (this.instamine) {
                        this.mc.ingameGUI.addChatMessage("Instant mining enabled");
                    } else {
                        this.mc.ingameGUI.addChatMessage("Instant mining disabled");
                    }

                    this.saveSettings();
                    break;
                case "teleport":
                case "tp":
                case "tele":
                    if (var2.length < 4) {
                        return;
                    }

                    double var19;
                    double var21;
                    double var23;
                    try {
                        var19 = Double.parseDouble(var2[1]);
                        var21 = Double.parseDouble(var2[2]);
                        var23 = Double.parseDouble(var2[3]);
                    } catch (NumberFormatException var43) {
                        this.mc.ingameGUI.addChatMessage("Invalid format/pos");
                        return;
                    }

                    this.thePlayer.setPosition(var19, var21, var23);
                    String var25 = this.getPositionAsString();
                    this.mc.ingameGUI.addChatMessage("Teleported Player to " + var25);
                    if (var19 >= (double)1.25005E7F || var19 <= (double)-1.25005E7F || var23 >= (double)1.25005E7F || var23 <= (double)-1.25005E7F) {
                        this.mc.ingameGUI.addChatMessage("§7Warning: falling block entities may crash the game");
                    }
                    break;
                case "time":
                    if (var2.length == 3 && var2[1].equalsIgnoreCase("set")) {
                        if (var2[2].equalsIgnoreCase("day")) {
                            this.mc.theWorld.worldTime = 1000L;
                        } else if (var2[2].equalsIgnoreCase("noon")) {
                            this.mc.theWorld.worldTime = 6000L;
                        } else if (var2[2].equalsIgnoreCase("night")) {
                            this.mc.theWorld.worldTime = 13000L;
                        } else if (var2[2].equalsIgnoreCase("midnight")) {
                            this.mc.theWorld.worldTime = 18000L;
                        } else {
                            try {
                                long var65 = Long.parseLong(var2[2]);
                                this.mc.theWorld.worldTime = var65;
                            } catch (NumberFormatException var42) {
                                this.mc.ingameGUI.addChatMessage("Invalid time value");
                                return;
                            }
                        }

                        this.mc.ingameGUI.addChatMessage("Set the time to " + this.mc.theWorld.worldTime);
                    } else if (var2.length == 2) {
                        if (var2[1].equalsIgnoreCase("day")) {
                            this.mc.theWorld.worldTime = 1000L;
                        } else if (var2[1].equalsIgnoreCase("noon")) {
                            this.mc.theWorld.worldTime = 6000L;
                        } else if (var2[1].equalsIgnoreCase("night")) {
                            this.mc.theWorld.worldTime = 13000L;
                        } else if (var2[1].equalsIgnoreCase("midnight")) {
                            this.mc.theWorld.worldTime = 18000L;
                        } else {
                            try {
                                long var64 = Long.parseLong(var2[1]);
                                this.mc.theWorld.worldTime = var64;
                            } catch (NumberFormatException var41) {
                                this.mc.ingameGUI.addChatMessage("Invalid time value");
                                return;
                            }
                        }

                        this.mc.ingameGUI.addChatMessage("Set the time to " + this.mc.theWorld.worldTime);
                    }
                    break;
                case "seed":
                    if (var2.length > 1 && "copy".equalsIgnoreCase(var2[1])) {
                        long var63 = this.theWorld.randomSeed;
                        String var67 = String.valueOf(var63);
                        StringSelection var70 = new StringSelection(var67);
                        Clipboard var75 = Toolkit.getDefaultToolkit().getSystemClipboard();
                        var75.setContents(var70, (ClipboardOwner)null);
                        this.mc.ingameGUI.addChatMessage("§7Seed copied to clipboard");
                    } else {
                        long var62 = this.theWorld.randomSeed;
                        this.mc.ingameGUI.addChatMessage("Seed: §a" + var62);
                    }
                    break;
                case "give":
                case "item":
                    if (var2.length < 2) {
                        this.mc.ingameGUI.addChatMessage("Enter a block/item name or valid ID");
                        return;
                    }

                    int var27 = 1;
                    if (var2.length > 2) {
                        try {
                            var27 = Integer.parseInt(var2[2]);
                            if (var27 >= 999) {
                                this.mc.ingameGUI.addChatMessage("§7Warning: lots of items");
                            }
                        } catch (NumberFormatException var40) {
                            this.mc.ingameGUI.addChatMessage("Invalid quantity");
                            return;
                        }
                    }

                    try {
                        int var26 = Integer.parseInt(var2[1]);
                        if (var26 >= 0 && var26 < Block.blocksList.length && Block.blocksList[var26] != null) {
                            Block var66 = Block.blocksList[var26];
                            String var69 = null;

                            for(Map.Entry var80 : this.blockMap.entrySet()) {
                                if (var80.getValue() == var66) {
                                    var69 = (String)var80.getKey();
                                    break;
                                }
                            }

                            ItemStack var74 = new ItemStack(var66, var27);
                            this.mc.ingameGUI.addChatMessage("Gave " + var27 + " [" + var69 + "] to Player");
                            boolean var81 = this.thePlayer.inventory.addItemStackToInventory(var74);
                            if (!var81) {
                                this.thePlayer.dropPlayerItem(var74);
                                this.mc.ingameGUI.addChatMessage("Inventory full, dropped item");
                            } else {
                                float var85 = 0.5F;
                                float var90 = ((this.thePlayer.rand.nextFloat() - this.thePlayer.rand.nextFloat()) * 0.7F + 1.0F) * 2.0F;
                                this.mc.theWorld.playSoundAtEntity(this.thePlayer, "random.pop", var85, var90);
                            }
                            break;
                        }

                        throw new NumberFormatException();
                    } catch (NumberFormatException var49) {
                        StringBuilder var68 = new StringBuilder();

                        for(int var72 = 1; var72 < var2.length; ++var72) {
                            try {
                                var27 = Integer.parseInt(var2[var72]);
                                break;
                            } catch (NumberFormatException var47) {
                                if (var68.length() > 0) {
                                    var68.append(" ");
                                }

                                var68.append(var2[var72]);
                            }
                        }

                        String var79 = var68.toString().trim().toLowerCase();
                        Item var84 = (Item)this.itemMap.get(var79);
                        Block var89 = (Block)this.blockMap.get(var79);
                        if (var84 == null && var89 == null) {
                            this.mc.ingameGUI.addChatMessage("Invalid item/block name");
                            return;
                        }

                        ItemStack var95;
                        String var99;
                        if (var84 != null) {
                            var95 = new ItemStack(var84, var27);
                            var99 = var79;
                        } else {
                            var95 = new ItemStack(var89, var27);
                            var99 = var79;
                        }

                        this.mc.ingameGUI.addChatMessage("Gave " + var27 + " [" + var99 + "] to Player");
                        boolean var105 = this.thePlayer.inventory.addItemStackToInventory(var95);
                        if (!var105) {
                            this.thePlayer.dropPlayerItem(var95);
                            this.mc.ingameGUI.addChatMessage("Inventory full, dropped item");
                        } else {
                            float var106 = 0.5F;
                            float var109 = ((this.thePlayer.rand.nextFloat() - this.thePlayer.rand.nextFloat()) * 0.7F + 1.0F) * 2.0F;
                            this.mc.theWorld.playSoundAtEntity(this.thePlayer, "random.pop", var106, var109);
                        }
                        break;
                    }
                case "search":
                    if (var2.length < 2) {
                        this.mc.ingameGUI.addChatMessage("Please enter a keyword to search");
                        return;
                    }

                    String var28 = String.join(" ", (CharSequence[]) Arrays.copyOfRange(var2, 1, var2.length)).toLowerCase();
                    ArrayList var29 = new ArrayList();

                    for(String var76 : this.itemMap.keySet()) {
                        if (var76.toLowerCase().contains(var28)) {
                            var29.add(var76);
                        }
                    }

                    ArrayList var71 = new ArrayList();

                    for(String var82 : this.blockMap.keySet()) {
                        if (var82.toLowerCase().contains(var28)) {
                            var71.add(var82);
                        }
                    }

                    if (!var29.isEmpty() || !var71.isEmpty()) {
                        byte var78 = 75;
                        StringBuilder var83 = new StringBuilder();
                        int var88 = 0;
                        this.mc.ingameGUI.addChatMessage("§7-- §dsearch results for keyword: §b\"" + var28 + "\"§7 --");

                        for(Object var97 : var29) {
                            String var103 = "§e" + var97 + " §7| ";
                            if (var88 + var103.length() > var78) {
                                this.mc.ingameGUI.addChatMessage(var83.toString());
                                var83 = new StringBuilder();
                                var88 = 0;
                            }

                            var83.append(var103);
                            var88 += var103.length();
                        }

                        for(Object var98 : var71) {
                            String var104 = "§e" + var98 + " §7| ";
                            if (var88 + var104.length() > var78) {
                                this.mc.ingameGUI.addChatMessage(var83.toString());
                                var83 = new StringBuilder();
                                var88 = 0;
                            }

                            var83.append(var104);
                            var88 += var104.length();
                        }

                        if (var88 > 0) {
                            if (var83.length() > 3) {
                                var83.setLength(var83.length() - 3);
                            }

                            this.mc.ingameGUI.addChatMessage(var83.toString());
                        }
                    }
                    break;
                case "killmob":
                case "killnpc":
                    List var31 = this.mc.theWorld.getEntitiesWithinAABBExcludingEntity(this.thePlayer, AxisAlignedBB.getBoundingBox(this.thePlayer.posX - (double)50.0F, this.thePlayer.posY - (double)50.0F, this.thePlayer.posZ - (double)50.0F, this.thePlayer.posX + (double)50.0F, this.thePlayer.posY + (double)50.0F, this.thePlayer.posZ + (double)50.0F));
                    int var32 = 0;
                    if (var2.length < 2) {
                        for(Object var91 : var31) {
                            if (var91 instanceof EntityLiving) {
                                ((EntityLiving)var91).health -= 32767;
                                ++var32;
                            }
                        }

                        if (var32 > 0) {
                            this.mc.ingameGUI.addChatMessage("Killed all nearby entities");
                        } else {
                            this.mc.ingameGUI.addChatMessage("No nearby entities found");
                        }
                    } else {
                        String var87 = var2[1].toLowerCase();

                        for(Object var96 : var31) {
                            if (var96 instanceof EntityLiving) {
                                boolean var101 = false;
                                switch (var87) {
                                    case "chicken":
                                        var101 = var96 instanceof EntityChicken;
                                        break;
                                    case "cow":
                                        var101 = var96 instanceof EntityCow;
                                        break;
                                    case "creeper":
                                        var101 = var96 instanceof EntityCreeper;
                                        break;
                                    case "pig":
                                        var101 = var96 instanceof EntityPig;
                                        break;
                                    case "sheep":
                                        var101 = var96 instanceof EntitySheep;
                                        break;
                                    case "skeleton":
                                        var101 = var96 instanceof EntitySkeleton;
                                        break;
                                    case "slime":
                                        var101 = var96 instanceof EntitySlime;
                                        break;
                                    case "spider":
                                        var101 = var96 instanceof EntitySpider;
                                        break;
                                    case "zombie":
                                        var101 = var96 instanceof EntityZombie;
                                        break;
                                    case "giant":
                                        var101 = var96 instanceof EntityGiantZombie;
                                        break;
                                    default:
                                        this.mc.ingameGUI.addChatMessage("Unknown entity: " + var87);
                                        return;
                                }

                                if (var101) {
                                    ((EntityLiving)var96).health -= 32767;
                                    ++var32;
                                }
                            }
                        }

                        if (var32 > 0) {
                            this.mc.ingameGUI.addChatMessage("Killed all nearby " + var87 + " entities");
                        } else {
                            this.mc.ingameGUI.addChatMessage("No nearby " + var87 + " entities found");
                        }
                    }
                    break;
                case "spawn":
                case "summon":
                case "su":
                    if (var2.length < 2) {
                        return;
                    }

                    Object var33 = null;
                    int var34 = 1;
                    Integer var35 = this.mc.theWorld.difficultySetting;

                    do {
                        String var100 = var2[var34];
                        Object var37 = null;
                        switch (var100.toLowerCase()) {
                            case "chicken":
                                var37 = new EntityChicken(this.mc.theWorld);
                                break;
                            case "cow":
                                var37 = new EntityCow(this.mc.theWorld);
                                break;
                            case "creeper":
                                var37 = new EntityCreeper(this.mc.theWorld);
                                if (var35 == 0) {
                                    this.mc.ingameGUI.addChatMessage("§7Difficulty is currently set to peaceful");
                                }
                                break;
                            case "pig":
                                var37 = new EntityPig(this.mc.theWorld);
                                break;
                            case "sheep":
                                var37 = new EntitySheep(this.mc.theWorld);
                                break;
                            case "skeleton":
                                var37 = new EntitySkeleton(this.mc.theWorld);
                                if (var35 == 0) {
                                    this.mc.ingameGUI.addChatMessage("§7Difficulty is currently set to peaceful");
                                }
                                break;
                            case "slime":
                                var37 = new EntitySlime(this.mc.theWorld);
                                break;
                            case "spider":
                                var37 = new EntitySpider(this.mc.theWorld);
                                if (var35 == 0) {
                                    this.mc.ingameGUI.addChatMessage("§7Difficulty is currently set to peaceful");
                                }
                                break;
                            case "zombie":
                                var37 = new EntityZombie(this.mc.theWorld);
                                if (var35 == 0) {
                                    this.mc.ingameGUI.addChatMessage("§7Difficulty is currently set to peaceful");
                                }
                                break;
                            case "giant":
                                var37 = new EntityZombie(this.mc.theWorld);
                                if (var35 == 0) {
                                    this.mc.ingameGUI.addChatMessage("§7Difficulty is currently set to peaceful");
                                }
                                break;
                            case "monster":
                                var37 = new EntityMob(this.mc.theWorld);
                                if (var35 == 0) {
                                    this.mc.ingameGUI.addChatMessage("§7Difficulty is currently set to peaceful");
                                }
                            case "mob":
                                var37 = new EntityLiving(this.mc.theWorld);
                            case "herobrine":
                                this.mc.ingameGUI.addChatMessage("§cHerobrine joined the game");
                                break;
                            case "player":
                                var37 = new EntityPlayer(this.mc.theWorld);
                                this.mc.ingameGUI.addChatMessage("§ePlayer joined the game");
                                this.mc.ingameGUI.addChatMessage("§7Warning: killing player will throw NullPointerException");
                                break;
                            default:
                                this.mc.ingameGUI.addChatMessage("Unknown entity: " + var100);
                        }

                        if (var37 != null) {
                            ((EntityLiving)var37).setLocationAndAngles(this.thePlayer.posX, this.thePlayer.posY, this.thePlayer.posZ, this.thePlayer.rotationYaw, 0.0F);
                            this.mc.theWorld.spawnEntityInWorld((Entity)var37);
                            if (var33 != null) {
                                ((EntityLiving)var37).mountEntity((Entity)var33);
                            }

                            var33 = var37;
                        } else {
                            throw new NullPointerException("Spawning entity threw NullPointerException. CommandClass:783");
                        }

                        ++var34;
                    } while(var34 < var2.length);

                    return;
                case "help":
                    if (var2.length > 1) {
                        switch (var2[1].toLowerCase()) {
                            case "clear":
                                this.helpMessage("Clears the chat console", "clear", "/clear", "clear");
                                return;
                            case "give":
                            case "item":
                                this.helpMessage("Gives player item, if quantity isn’t specified 1 of that item", "give/item <ITEMCODE|ITEMNAME> <QUANTITY>", "/give 1 64, /item diamond_sword", "give");
                                this.mc.ingameGUI.addChatMessage("§7(Item names use underscores w/modern naming conventions, ");
                                this.mc.ingameGUI.addChatMessage("§7use §e/search §7if you are having trouble finding an item)");
                                return;
                            case "search":
                                this.mc.ingameGUI.addChatMessage("§7-- search --");
                                this.mc.ingameGUI.addChatMessage("§3Description:");
                                this.mc.ingameGUI.addChatMessage("Search for an item name using a keyword");
                                this.mc.ingameGUI.addChatMessage("§3Syntax:");
                                this.mc.ingameGUI.addChatMessage("search");
                                this.mc.ingameGUI.addChatMessage("§3Example:");
                                this.mc.ingameGUI.addChatMessage("/search diamond");
                                return;
                            case "godmode":
                            case "damage":
                            case "gm":
                                this.mc.ingameGUI.addChatMessage("§7-- godmode --");
                                this.mc.ingameGUI.addChatMessage("§3Description:");
                                this.mc.ingameGUI.addChatMessage("Disables all forms of damage");
                                this.mc.ingameGUI.addChatMessage("§3Syntax:");
                                this.mc.ingameGUI.addChatMessage("godmode/damage/gm");
                                this.mc.ingameGUI.addChatMessage("§3Example:");
                                this.mc.ingameGUI.addChatMessage("/godmode §7(retype /godmode to disable)");
                                return;
                            case "goto":
                                this.helpMessage("Warps the player to a set waypoint", "goto <NAME>", "/goto ...", "goto");
                                return;
                            case "heal":
                                this.helpMessage("Heals the player by a specified number of health points", "heal <VALUE>", "/heal 5", "heal");
                                return;
                            case "health":
                                this.helpMessage("Sets the health of the player using a value or query", "health <min|max|infinite|VALUE>", "/health max, /health 20", "health");
                                return;
                            case "help":
                                this.helpMessage("Brings up a help message", "help <COMMANDNAME>", "/help give", "help");
                                return;
                            case "home":
                                this.helpMessage("Teleport to the world spawn point", "home", "/home", "home");
                                return;
                            case "info":
                                this.mc.ingameGUI.addChatMessage("§7-- info --");
                                this.mc.ingameGUI.addChatMessage("§3Description:");
                                this.mc.ingameGUI.addChatMessage("Contains general information about the mod");
                                this.mc.ingameGUI.addChatMessage("§7(version, game version, and credits)");
                                this.mc.ingameGUI.addChatMessage("§3Syntax:");
                                this.mc.ingameGUI.addChatMessage("info");
                                this.mc.ingameGUI.addChatMessage("§3Example:");
                                this.mc.ingameGUI.addChatMessage("/info");
                                return;
                            case "kill":
                                this.helpMessage("Kills the current player", "kill", "/kill", "kill");
                                return;
                            case "killmob":
                            case "killnpc":
                                this.helpMessage("Kills all mobs around the player, or specify a mob type to kill", "killmob/killnpc", "/killmob, /killmob pig", "killmob");
                                return;
                            case "listwaypoints":
                            case "lwp":
                                this.helpMessage("Lists all the waypoints currently configured", "listwaypoints/lwp", "/listwaypoints", "listwaypoints");
                                return;
                            case "pos":
                                this.helpMessage("Gives the current player position, and direction facing", "pos", "/pos", "pos");
                                return;
                            case "rem":
                                this.helpMessage("Removes a previously set waypoint", "rem <NAME>", "/rem ...", "rem");
                                return;
                            case "set":
                                this.helpMessage("Sets a named waypoint in the world using current position", "set <NAME>", "/set ...", "set");
                                return;
                            case "fly":
                                this.mc.ingameGUI.addChatMessage("§7-- fly --");
                                this.mc.ingameGUI.addChatMessage("§3Description:");
                                this.mc.ingameGUI.addChatMessage("Allows the player to fly §7(up = space, down = r_shift)");
                                this.mc.ingameGUI.addChatMessage("§3Syntax:");
                                this.mc.ingameGUI.addChatMessage("fly");
                                this.mc.ingameGUI.addChatMessage("§3Example:");
                                this.mc.ingameGUI.addChatMessage("/fly §7(retype /fly to disable)");
                                return;
                            case "noclip":
                            case "nc":
                                this.mc.ingameGUI.addChatMessage("§7-- noclip --");
                                this.mc.ingameGUI.addChatMessage("§3Description:");
                                this.mc.ingameGUI.addChatMessage("Disables block collision and suffocation");
                                this.mc.ingameGUI.addChatMessage("§7(fly set to on automatically)");
                                this.mc.ingameGUI.addChatMessage("§3Syntax:");
                                this.mc.ingameGUI.addChatMessage("noclip/nc");
                                this.mc.ingameGUI.addChatMessage("§3Example:");
                                this.mc.ingameGUI.addChatMessage("/noclip §7(retype /noclip to disable)");
                                return;
                            case "instamine":
                            case "instantmine":
                            case "im":
                                this.mc.ingameGUI.addChatMessage("§7-- instamine --");
                                this.mc.ingameGUI.addChatMessage("§3Description:");
                                this.mc.ingameGUI.addChatMessage("Instantly break blocks");
                                this.mc.ingameGUI.addChatMessage("§3Syntax:");
                                this.mc.ingameGUI.addChatMessage("instamine/instantmine/im");
                                this.mc.ingameGUI.addChatMessage("§3Example:");
                                this.mc.ingameGUI.addChatMessage("/instamine §7(retype /instamine to disable)");
                                return;
                            case "setspawn":
                            case "setworldspawn":
                            case "sws":
                                this.helpMessage("Sets the current position as the spawn point, or specify XYZ   to set a specific spawn location", "setspawn/setworldspawn/sws <X> <Y> <Z>", "/setspawn 0 66 0", "setspawn");
                                return;
                            case "setspeed":
                            case "speed":
                            case "sp":
                                this.helpMessage("Sets the players movement speed", "setspeed/speed/sp <VALUE|reset>", "/speed 5", "speed");
                                return;
                            case "seed":
                                this.helpMessage("Gives the current seed, or use \"copy\" to copy the seed to     the clipboard", "seed <§7null§f|copy>", "/seed, /seed copy", "seed");
                                return;
                            case "teleport":
                            case "tp":
                            case "tele":
                                this.helpMessage("Teleport to a location using XYZ coordinates", "teleport/tele/tp <X> <Y> <Z>", "/teleport 0 70 0", "teleport");
                                return;
                            case "time":
                            case "time set":
                                this.helpMessage("Sets the world time using a value or query", "time/time set <day|noon|night|midnight|VALUE>", "/time day, /time set 1000", "time");
                                return;
                            case "spawn":
                            case "summon":
                            case "su":
                                this.mc.ingameGUI.addChatMessage("§7-- spawn --");
                                this.mc.ingameGUI.addChatMessage("§3Description:");
                                this.mc.ingameGUI.addChatMessage("Spawns the specified creature");
                                this.mc.ingameGUI.addChatMessage("§7(chicken, cow, creeper, pig, sheep, skeleton,");
                                this.mc.ingameGUI.addChatMessage("§7slime, spider, zombie, giant, player)");
                                this.mc.ingameGUI.addChatMessage("§3Syntax:");
                                this.mc.ingameGUI.addChatMessage("spawn/summon/su <CREATURENAME>");
                                this.mc.ingameGUI.addChatMessage("§3Example:");
                                this.mc.ingameGUI.addChatMessage("/spawn zombie");
                                return;
                            default:
                                this.mc.ingameGUI.addChatMessage("Unknown command, type /help for a list of commands.");
                        }
                    } else {
                        this.mc.ingameGUI.addChatMessage(" ");
                        this.mc.ingameGUI.addChatMessage("§eCommands: §7/<COMMANDNAME>");
                        this.mc.ingameGUI.addChatMessage("clear, fly, give, godmode, goto, heal, health, help, home,");
                        this.mc.ingameGUI.addChatMessage("info, instamine, kill, killmob, listwaypoints, noclip, pos, rem,");
                        this.mc.ingameGUI.addChatMessage("search, seed, set, setspawn, speed, spawn, teleport, time");
                        this.mc.ingameGUI.addChatMessage(" ");
                        this.mc.ingameGUI.addChatMessage("§7Use \"/help <COMMANDNAME>\" for more information about a   ");
                        this.mc.ingameGUI.addChatMessage("§7command");
                        this.mc.ingameGUI.addChatMessage(" ");
                    }
                    break;
                default:
                    this.mc.ingameGUI.addChatMessage("Unknown command name");
            }
        } else {
            this.mc.ingameGUI.addChatMessage("Must use \"/\" for commands");
        }

    }

    public void addErrorMessage(String var1) {
        this.mc.ingameGUI.addChatMessage("§4" + var1);
    }

    public void sendModLoadedMessage() {
        if (this.mc != null && this.mc.ingameGUI != null) {
            this.mc.ingameGUI.addChatMessage("§aOld-MC-Commands §7-- §bgithub.com/Skellz64");
        }

    }

    public void firstModLoad() {
        if (this.showHelpMessage) {
            this.mc.ingameGUI.addChatMessage("§7Press T and type /help to begin");
            this.showHelpMessage = false;
            this.saveSettings();
        }

    }

    private String getPositionAsString() {
        int var1 = (int)Math.floor(this.thePlayer.posX);
        int var2 = (int)Math.floor(this.thePlayer.posY);
        int var3 = (int)Math.floor(this.thePlayer.posZ);
        return "" + var1 + ", " + var2 + ", " + var3;
    }

    public void helpMessage(String var1, String var2, String var3, String var4) {
        this.mc.ingameGUI.addChatMessage("§7-- " + var4 + " --");
        this.mc.ingameGUI.addChatMessage("§3Description:");
        this.mc.ingameGUI.addChatMessage("\t" + var1);
        this.mc.ingameGUI.addChatMessage("§3Syntax:");
        this.mc.ingameGUI.addChatMessage("\t" + var2);
        this.mc.ingameGUI.addChatMessage("§3Example:");
        this.mc.ingameGUI.addChatMessage("\t" + var3);
    }

    public void readWaypointsFromNBT(File var1) {
        File var2 = new File(var1, "waypoints.dat");
        if (!var2.exists()) {
            waypoints.clear();
        } else {
            NBTTagCompound var3;
            try {
                var3 = CompressedStreamTools.readCompressed(new FileInputStream(var2));
            } catch (Exception var14) {
                return;
            }

            NBTTagList var4 = var3.getTagList("waypoints");

            for(int var5 = 0; var5 < var4.tagCount(); ++var5) {
                NBTTagCompound var6 = (NBTTagCompound)var4.tagAt(var5);
                String var7 = var6.getString("Name");
                double var8 = var6.getDouble("X");
                double var10 = var6.getDouble("Y");
                double var12 = var6.getDouble("Z");
                waypoints.put(var7, new double[]{var8, var10, var12});
            }

        }
    }

    public void saveWaypointsToNBT(File var1) {
        if (waypoints.size() != 0) {
            NBTTagList var2 = new NBTTagList();
            Iterator var3 = waypoints.keySet().iterator();

            while(var3.hasNext()) {
                NBTTagCompound var4 = new NBTTagCompound();
                String var5 = (String)var3.next();
                var4.setString("Name", var5);
                var4.setDouble("X", ((double[])waypoints.get(var5))[0]);
                var4.setDouble("Y", ((double[])waypoints.get(var5))[1]);
                var4.setDouble("Z", ((double[])waypoints.get(var5))[2]);
                var2.setTag(var4);
            }

            NBTTagCompound var10 = new NBTTagCompound();
            var10.setTag("waypoints", var2);
            File var11 = new File(var1, "waypoints.dat_new");
            File var6 = new File(var1, "waypoints.dat_old");
            File var7 = new File(var1, "waypoints.dat");

            try {
                CompressedStreamTools.writeCompressed(var10, new FileOutputStream(var11));
                if (var6.exists()) {
                    var6.delete();
                }

                var7.renameTo(var6);
                if (var7.exists()) {
                    var7.delete();
                }

                var11.renameTo(var7);
                if (var11.exists()) {
                    var11.delete();
                }
            } catch (Exception var9) {
            }
        }

    }


    private Map<String, Item> initializeItemMap() {
        HashMap var1 = new HashMap();
        var1.put("iron_shovel", Item.shovel);
        var1.put("iron_pickaxe", Item.pickaxeSteel);
        var1.put("iron_axe", Item.axeSteel);
        var1.put("flint_and_steel", Item.striker);
        var1.put("apple", Item.appleRed);
        var1.put("bow", Item.bow);
        var1.put("arrow", Item.arrow);
        var1.put("coal", Item.coal);
        var1.put("diamond", Item.diamond);
        var1.put("iron_ingot", Item.ingotIron);
        var1.put("gold_ingot", Item.ingotGold);
        var1.put("iron_sword", Item.swordSteel);
        var1.put("wooden_sword", Item.swordWood);
        var1.put("wooden_shovel", Item.shovelWood);
        var1.put("wooden_pickaxe", Item.pickaxeWood);
        var1.put("wooden_axe", Item.axeWood);
        var1.put("stone_sword", Item.swordStone);
        var1.put("stone_shovel", Item.shovelStone);
        var1.put("stone_pickaxe", Item.pickaxeStone);
        var1.put("stone_axe", Item.axeStone);
        var1.put("diamond_sword", Item.swordDiamond);
        var1.put("diamond_shovel", Item.shovelDiamond);
        var1.put("diamond_pickaxe", Item.pickaxeDiamond);
        var1.put("diamond_axe", Item.axeDiamond);
        var1.put("stick", Item.stick);
        var1.put("bowl", Item.bowlEmpty);
        var1.put("mushroom_stew", Item.bowlSoup);
        var1.put("golden_sword", Item.swordGold);
        var1.put("golden_shovel", Item.shovelGold);
        var1.put("golden_pickaxe", Item.pickaxeGold);
        var1.put("golden_axe", Item.axeGold);
        var1.put("oak_sign", Item.sign);
        var1.put("string", Item.silk);
        var1.put("feather", Item.feather);
        var1.put("gunpowder", Item.gunpowder);
        var1.put("wooden_hoe", Item.hoeWood);
        var1.put("stone_hoe", Item.hoeStone);
        var1.put("iron_hoe", Item.hoeSteel);
        var1.put("diamond_hoe", Item.hoeDiamond);
        var1.put("golden_hoe", Item.hoeGold);
        var1.put("wheat_seeds", Item.seeds);
        var1.put("wheat", Item.wheat);
        var1.put("bread", Item.bread);
        var1.put("leather_helmet", Item.helmetLeather);
        var1.put("leather_chestplate", Item.plateLeather);
        var1.put("leather_leggings", Item.legsLeather);
        var1.put("leather_boots", Item.bootsLeather);
        var1.put("chainmail_helmet", Item.helmetChain);
        var1.put("chainmail_chestplate", Item.plateChain);
        var1.put("chainmail_leggings", Item.legsChain);
        var1.put("chainmail_boots", Item.bootsChain);
        var1.put("iron_helmet", Item.helmetSteel);
        var1.put("iron_chestplate", Item.plateSteel);
        var1.put("iron_leggings", Item.legsSteel);
        var1.put("iron_boots", Item.bootsSteel);
        var1.put("diamond_helmet", Item.helmetDiamond);
        var1.put("diamond_chestplate", Item.plateDiamond);
        var1.put("diamond_leggings", Item.legsDiamond);
        var1.put("diamond_boots", Item.bootsDiamond);
        var1.put("golden_helmet", Item.helmetGold);
        var1.put("golden_chestplate", Item.plateGold);
        var1.put("golden_leggings", Item.legsGold);
        var1.put("golden_boots", Item.bootsGold);
        var1.put("flint", Item.flint);
        var1.put("raw_porkchop", Item.porkRaw);
        var1.put("cooked_porkchop", Item.porkCooked);
        var1.put("painting", Item.painting);
        var1.put("golden_apple", Item.appleGold);
        var1.put("oak_door", Item.doorWood);
        var1.put("bucket", Item.bucketEmpty);
        var1.put("water_bucket", Item.bucketWater);
        var1.put("lava_bucket", Item.bucketLava);
        var1.put("minecart", Item.minecartEmpty);
        var1.put("saddle", Item.saddle);
        var1.put("iron_door", Item.doorSteel);
        var1.put("redstone", Item.redstone);
        var1.put("snowball", Item.snowball);
        var1.put("oak_boat", Item.boat);
        var1.put("leather", Item.leather);
        var1.put("milk_bucket", Item.bucketMilk);
        var1.put("brick", Item.brick);
        var1.put("clay_ball", Item.clay);
        var1.put("sugar_cane", Item.reed);
        var1.put("paper", Item.paper);
        var1.put("book", Item.book);
        var1.put("slimeball", Item.slimeBall);
        var1.put("minecart_crate", Item.minecartBox);
        var1.put("minecart_powered", Item.minecartEngine);
        var1.put("egg", Item.egg);
        var1.put("compass", Item.compass);
        var1.put("fishing_rod", Item.fishingRod);
        var1.put("disc_13", Item.record13);
        var1.put("disc_cat", Item.recordCat);
        return var1;
    }

    private Map<String, Block> initializeBlockMap() {
        HashMap var1 = new HashMap();
        var1.put("stone", Block.stone);
        var1.put("grass", Block.grass);
        var1.put("dirt", Block.dirt);
        var1.put("cobblestone", Block.cobblestone);
        var1.put("oak_planks", Block.planks);
        var1.put("oak_sapling", Block.sapling);
        var1.put("bedrock", Block.bedrock);
        var1.put("water", Block.waterStill);
        var1.put("water_moving", Block.waterMoving);
        var1.put("lava", Block.lavaStill);
        var1.put("lava_moving", Block.lavaMoving);
        var1.put("sand", Block.sand);
        var1.put("gravel", Block.gravel);
        var1.put("gold_ore", Block.oreGold);
        var1.put("iron_ore", Block.oreIron);
        var1.put("coal_ore", Block.oreCoal);
        var1.put("oak_logs", Block.wood);
        var1.put("oak_leaves", Block.leaves);
        var1.put("sponge", Block.sponge);
        var1.put("glass", Block.glass);
        var1.put("white_wool", Block.cloth);
        var1.put("dandelion", Block.plantYellow);
        var1.put("rose", Block.plantRed);
        var1.put("brown_mushroom", Block.mushroomBrown);
        var1.put("red_mushroom", Block.mushroomRed);
        var1.put("block_of_gold", Block.blockGold);
        var1.put("block_of_iron", Block.blockSteel);
        var1.put("smooth_stone_slab_block", Block.stairDouble);
        var1.put("smooth_stone_slab", Block.stairSingle);
        var1.put("bricks", Block.brick);
        var1.put("tnt", Block.tnt);
        var1.put("bookshelf", Block.bookshelf);
        var1.put("mossy_cobblestone", Block.cobblestoneMossy);
        var1.put("obsidian", Block.obsidian);
        var1.put("torch", Block.torch);
        var1.put("fire", Block.fire);
        var1.put("monster_spawner", Block.mobSpawner);
        var1.put("oak_stairs", Block.stairCompactWood);
        var1.put("chest", Block.chest);
        var1.put("redstone_dust", Block.redstoneWire);
        var1.put("diamond_ore", Block.oreDiamond);
        var1.put("block_of_diamond", Block.blockDiamond);
        var1.put("crafting_table", Block.workbench);
        var1.put("crops", Block.crops);
        var1.put("farmland", Block.tilledField);
        var1.put("furnace", Block.stoneOvenIdle);
        var1.put("furnace_active", Block.stoneOvenActive);
        var1.put("oak_sign_block", Block.signStanding);
        var1.put("oak_door_half", Block.doorWood);
        var1.put("ladder", Block.ladder);
        var1.put("rail", Block.minecartTrack);
        var1.put("cobblestone_stairs", Block.stairCompactStone);
        var1.put("oak_sign_wall", Block.signWall);
        var1.put("lever", Block.lever);
        var1.put("stone_pressure_plate", Block.pressurePlateStone);
        var1.put("iron_door_half", Block.doorSteel);
        var1.put("oak_pressure_plate", Block.pressurePlateWood);
        var1.put("redstone_ore", Block.oreRedstone);
        var1.put("redstone_ore_glowing", Block.oreRedstoneGlowing);
        var1.put("redstone_torch", Block.torchRedstoneIdle);
        var1.put("redstone_torch_active", Block.torchRedstoneActive);
        var1.put("stone_button", Block.button);
        var1.put("snow", Block.snow);
        var1.put("ice", Block.ice);
        var1.put("snow_block", Block.blockSnow);
        var1.put("cactus", Block.cactus);
        var1.put("clay", Block.blockClay);
        var1.put("sugar_cane_block", Block.reed);
        var1.put("jukebox", Block.jukebox);
        var1.put("oak_fence", Block.fence);
        return var1;
    }
}