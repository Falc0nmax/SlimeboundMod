package slimebound.cards;



import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import slimebound.SlimeboundMod;
import slimebound.actions.SlimeSpawnAction;
import slimebound.patches.AbstractCardEnum;
import slimebound.powers.HexSlimePerTurnPower;


public class HexSlime extends AbstractSlimeboundCard {
    public static final String ID = "HexSlime";
    public static final String NAME;
    public static final String DESCRIPTION;
    public static String UPGRADED_DESCRIPTION;
    public static final String IMG_PATH = "cards/splithex.png";

    private static final CardType TYPE = CardType.SKILL;
    private static final CardRarity RARITY = CardRarity.SPECIAL;
    private static final CardTarget TARGET = CardTarget.SELF;
    private static final CardStrings cardStrings;

    private static final int COST = 0;
    private static final int BLOCK = 5;
    private static final int UPGRADE_BONUS = 3;

    public HexSlime() {
        super(ID, NAME, SlimeboundMod.getResourcePath(IMG_PATH), COST, DESCRIPTION, TYPE, CardColor.COLORLESS, RARITY, TARGET);


        this.exhaust = true;
        this.isEthereal = true;
        this.magicNumber = this.baseMagicNumber = 6;

    }

    public void use(AbstractPlayer p, AbstractMonster m) {
        int slotGain = 0;
        slotGain = 6 - p.maxOrbs;
        if (slotGain > 0) {
            CardCrawlGame.sound.play("GHOST_ORB_IGNITE_1", 0.3F);
            com.megacrit.cardcrawl.dungeons.AbstractDungeon.actionManager.addToBottom(new com.megacrit.cardcrawl.actions.defect.IncreaseMaxOrbAction(slotGain));
        }

        AbstractDungeon.actionManager.addToBottom(new SlimeSpawnAction(new slimebound.orbs.HexSlime(), false, true));
        if (this.upgraded) {
            AbstractDungeon.actionManager.addToBottom(new SlimeSpawnAction(new slimebound.orbs.HexSlime(), false, true));
        }

    }


    static {
        cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
        NAME = cardStrings.NAME;
        DESCRIPTION = cardStrings.DESCRIPTION;
        UPGRADED_DESCRIPTION = cardStrings.UPGRADE_DESCRIPTION;
    }

    public AbstractCard makeCopy() {
        return new HexSlime();
    }

    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            this.rawDescription = UPGRADED_DESCRIPTION;
            this.initializeDescription();


        }
    }
}

