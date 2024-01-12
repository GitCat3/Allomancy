package com.legobmw99.allomancy.test;

import com.legobmw99.allomancy.Allomancy;
import com.legobmw99.allomancy.api.enums.Metal;
import com.legobmw99.allomancy.modules.powers.data.AllomancerAttachment;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

@PrefixGameTestTemplate(false)
@GameTestHolder(Allomancy.MODID)
public class DataGameTests {
    @GameTest(template = "empty_1x1")
    public static void emptyDataTest(GameTestHelper helper) {
        var player = helper.makeMockPlayer();
        var data = player.getData(AllomancerAttachment.ALLOMANCY_DATA);

        helper.assertTrue(data.isUninvested(), "Default data is invested");
        helper.succeed();
    }

    @GameTest(template = "empty_1x1")
    public static void aluminiumDrainsInstantly(GameTestHelper helper) {
        var player = helper.makeMockServerPlayerInLevel();
        var data = player.getData(AllomancerAttachment.ALLOMANCY_DATA);
        data.setMistborn();
        data.setAmount(Metal.PEWTER, 10);
        data.setBurning(Metal.PEWTER, true);
        data.setAmount(Metal.ALUMINUM, 1);
        data.setBurning(Metal.ALUMINUM, true);

        helper.succeedOnTickWhen(1, () -> {
            helper.assertFalse(data.isBurning(Metal.ALUMINUM), "Aluminum still burning after a tick");
            helper.assertTrue(data.getAmount(Metal.PEWTER) == 0, "Pewter was not drained by burning Aluminum");
            helper.assertFalse(data.isBurning(Metal.PEWTER), "Pewter still burning after empty");
        });
    }

    // TODO more tests once testframework is available to modders
    // https://github.com/FrozenBlock/WilderWild/blob/6eb0f8313efd2ca68a96ddb4900cbc154f12c68f/src/main/java/net/frozenblock/wilderwild/gametest/WWGameTest.java#L28

    // https://github.com/neoforged/NeoForge/blob/1.20.x/tests/src/main/java/net/neoforged/neoforge/debug/chat/CommandTests.java#L58
    //    @GameTest(template = "empty_1x1")
    //    public static void allomancyPowerWorks(GameTestHelper helper) {
    //        var player = helper.makeMockServerPlayerInLevel();
    //        helper
    //                .startSequence()
    //                // add random power
    //                .thenExecute(() -> helper.getLevel().getServer().getCommands().performPrefixedCommand(player.createCommandSourceStack(), "/allomancy add random"))
    //                .thenIdle(1)
    //                .thenExecute(() -> helper.assertFalse(player.getData(AllomancerAttachment.ALLOMANCY_DATA).isUninvested(), "Player is still uninvested"))
    //                // remove it
    //                .thenExecute(() -> helper.getLevel().getServer().getCommands().performPrefixedCommand(player.createCommandSourceStack(), "/allomancy remove random"))
    //                .thenIdle(1)
    //                .thenExecute(() -> helper.assertTrue(player.getData(AllomancerAttachment.ALLOMANCY_DATA).isUninvested(), "Player is still invested"))
    //                .thenSucceed();
    //    }

    //    @GameTest(template = "empty_1x1")
    //    public static void mistbornStaysOnDeath(GameTestHelper helper) {
    //        var player = helper.makeMockServerPlayerInLevel();
    //        var data = player.getData(AllomancerAttachment.ALLOMANCY_DATA);
    //        data.setMistborn();
    //        data.setAmount(Metal.STEEL, 10);
    //        player.setRespawnPosition(Level.OVERWORLD, helper.relativePos(BlockPos.ZERO), 0.0f, true, true);
    //        helper.startSequence().thenExecute(player::kill).thenIdle(10).thenExecute(player::respawn).thenExecute(() -> {
    //            helper.assertTrue(player.getData(AllomancerAttachment.ALLOMANCY_DATA).isMistborn(), "Player lost investment on death");
    //            helper.assertTrue(player.getData(AllomancerAttachment.ALLOMANCY_DATA).getAmount(Metal.STEEL) == 0, "Player kept inventory on death");
    //        }).thenSucceed();
    //    }
}