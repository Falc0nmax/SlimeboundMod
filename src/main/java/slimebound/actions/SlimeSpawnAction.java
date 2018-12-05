package slimebound.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.LoseHPAction;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.orbs.AbstractOrb;
import slimebound.orbs.HexSlime;
import slimebound.vfx.SlimeDripsEffect;


public class SlimeSpawnAction extends AbstractGameAction {
    private AbstractOrb orbType;
    private boolean SelfDamage = true;
    private boolean autoEvoke = false;
    private int currentAmount;


    public SlimeSpawnAction(AbstractOrb newOrbType, boolean selfDamage) {

        this(newOrbType, true, selfDamage);

    }


    public SlimeSpawnAction(AbstractOrb newOrbType, boolean autoEvoke, boolean SelfDamage) {

        this.duration = Settings.ACTION_DUR_FAST;

        this.orbType = newOrbType;

        this.autoEvoke = autoEvoke;
        this.SelfDamage = SelfDamage;
        this.currentAmount = currentAmount;

    }


    public void update() {

        if (SelfDamage) {
            if (AbstractDungeon.player.hasPower("SplitForLessPower")) {
                currentAmount = 3 - AbstractDungeon.player.getPower("SplitForLessPower").amount;
            } else {
                currentAmount = 3;
            }
            //AbstractDungeon.actionManager.addToBottom(new LoseHPAction(AbstractDungeon.player, AbstractDungeon.player, currentAmount, AttackEffect.NONE));


        }
        AbstractDungeon.effectsQueue.add(new SlimeDripsEffect(AbstractDungeon.player.hb.cX, AbstractDungeon.player.hb.cY, 0));

        AbstractDungeon.player.channelOrb(this.orbType);

        if (this.orbType instanceof HexSlime)  AbstractDungeon.actionManager.addToTop(new CheckForSixHexAction(AbstractDungeon.player));


        tickDuration();

        this.isDone = true;

    }
}


