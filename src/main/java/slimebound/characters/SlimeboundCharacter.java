package slimebound.characters;

import basemod.abstracts.CustomPlayer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.esotericsoftware.spine.AnimationState.TrackEntry;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.EnergyManager;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.cutscenes.CutscenePanel;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.*;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.LizardTail;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.screens.CharSelectInfo;
import com.megacrit.cardcrawl.screens.DeathScreen;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import com.megacrit.cardcrawl.vfx.BorderFlashEffect;
import com.megacrit.cardcrawl.vfx.combat.BlockedWordEffect;
import com.megacrit.cardcrawl.vfx.combat.HbBlockBrokenEffect;
import com.megacrit.cardcrawl.vfx.combat.StrikeEffect;
import slimebound.cards.CorrosiveSpit;
import slimebound.cards.Defend_Slimebound;
import slimebound.cards.Split;
import slimebound.cards.Strike_Slimebound;
import slimebound.patches.AbstractCardEnum;
import slimebound.patches.SlimeboundEnum;
import slimebound.relics.AbsorbEndCombat;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class SlimeboundCharacter extends CustomPlayer {
    public static Color cardRenderColor = new Color(0.0F, 0.1F, 0.0F, 1.0F);
    public float renderscale = 1.0F;
    public float hatX;
    public float hatY;
    public boolean moved = false;
    public boolean foughtSlimeBoss;
    public float leftScale = 0.15F;
    public float xStartOffset = (float) Settings.WIDTH * 0.23F;
    private static float xSpaceBetweenSlots = 90 * Settings.scale;
    private static float xSpaceBottomAlternatingOffset = 0;

    private static float yStartOffset = AbstractDungeon.floorY + (100 * Settings.scale);

    private static float ySpaceAlternatingOffset = -60 * Settings.scale;


    public float[] orbPositionsX = {0,0,0,0,0,0,0,0,0,0};

    public float[] orbPositionsY = {0,0,0,0,0,0,0,0,0,0};


    public static final String[] orbTextures = {"SlimeboundImages/char/orb/layer1.png", "SlimeboundImages/char/orb/layer2.png", "SlimeboundImages/char/orb/layer3.png", "SlimeboundImages/char/orb/layer4.png", "SlimeboundImages/char/orb/layer5.png", "SlimeboundImages/char/orb/layer6.png", "SlimeboundImages/char/orb/layer1d.png", "SlimeboundImages/char/orb/layer2d.png", "SlimeboundImages/char/orb/layer3d.png", "SlimeboundImages/char/orb/layer4d.png", "SlimeboundImages/char/orb/layer5d.png"};

    public void setRenderscale(float renderscale) {
        this.renderscale = renderscale;
        reloadAnimation();


    }

    public SlimeboundCharacter(String name, PlayerClass setClass) {
        super(name, setClass, orbTextures, "SlimeboundImages/char/orb/vfx.png", (String) null, (String) null);


        this.initializeClass((String) null, "SlimeboundImages/char/shoulder2.png", "SlimeboundImages/char/shoulder.png", "SlimeboundImages/char/corpse.png", this.getLoadout(), 0.0F, 0.0F, 300.0F, 180.0F, new EnergyManager(3));
        this.reloadAnimation();


        this.dialogX = -200 * Settings.scale;
        this.dialogY = -200 * Settings.scale;
        initializeSlotPositions();

    }

    @Override
    public Texture getCutsceneBg() {
        return ImageMaster.loadImage("images/scenes/greenBg.jpg");

    }


    @Override
    public List<CutscenePanel> getCutscenePanels() {
        List<CutscenePanel> panels = new ArrayList();
        panels.add(new CutscenePanel("SlimeboundImages/scenes/slimebound1.png", "VO_SLIMEBOSS_1A"));
        panels.add(new CutscenePanel("SlimeboundImages/scenes/slimebound2.png"));
        panels.add(new CutscenePanel("SlimeboundImages/scenes/slimebound3.png"));
        return panels;
    }




    public void reloadAnimation() {

        this.loadAnimation("SlimeboundImages/char/skeleton.atlas", "SlimeboundImages/char/skeleton.json", renderscale);
        TrackEntry e = this.state.setAnimation(0, "Idle", true);
        e.setTime(e.getEndTime() * MathUtils.random());
        this.state.addListener(new SlimeAnimListener());

    }


    public ArrayList<String> getStartingDeck() {
        ArrayList<String> retVal = new ArrayList();
        retVal.add(Strike_Slimebound.ID);
        retVal.add(Strike_Slimebound.ID);
        retVal.add(Strike_Slimebound.ID);
        retVal.add(Strike_Slimebound.ID);
        retVal.add(Defend_Slimebound.ID);
        retVal.add(Defend_Slimebound.ID);
        retVal.add(Defend_Slimebound.ID);
        retVal.add(Defend_Slimebound.ID);
        retVal.add(Split.ID);
        retVal.add(CorrosiveSpit.ID);
        return retVal;
    }

    public ArrayList<String> getStartingRelics() {
        ArrayList<String> retVal = new ArrayList();
        retVal.add(AbsorbEndCombat.ID);
        UnlockTracker.markRelicAsSeen(AbsorbEndCombat.ID);
        return retVal;
    }


    public void initializeSlotPositions() {
        orbPositionsX[0] = xStartOffset + (xSpaceBetweenSlots * 1);
        orbPositionsX[1] = xStartOffset + (xSpaceBetweenSlots * 1) + xSpaceBottomAlternatingOffset;
        orbPositionsX[2] = xStartOffset + (xSpaceBetweenSlots * 2);
        orbPositionsX[3] = xStartOffset + (xSpaceBetweenSlots * 2) + xSpaceBottomAlternatingOffset;
        orbPositionsX[4] = xStartOffset + (xSpaceBetweenSlots * 3);
        orbPositionsX[5] = xStartOffset + (xSpaceBetweenSlots * 3) + xSpaceBottomAlternatingOffset;
        orbPositionsX[6] = xStartOffset + (xSpaceBetweenSlots * 4);
        orbPositionsX[7] = xStartOffset + (xSpaceBetweenSlots * 4) + xSpaceBottomAlternatingOffset;
        orbPositionsX[8] = xStartOffset + (xSpaceBetweenSlots * 5);
        orbPositionsX[9] = xStartOffset + (xSpaceBetweenSlots * 5) + xSpaceBottomAlternatingOffset;

        orbPositionsY[0] = yStartOffset;
        orbPositionsY[1] = yStartOffset + -100 * Settings.scale;
        orbPositionsY[2] = yStartOffset + ySpaceAlternatingOffset;
        orbPositionsY[3] = yStartOffset + -100 * Settings.scale + ySpaceAlternatingOffset;
        orbPositionsY[4] = yStartOffset;
        orbPositionsY[5] = yStartOffset + -100 * Settings.scale;
        orbPositionsY[6] = yStartOffset + ySpaceAlternatingOffset;
        orbPositionsY[7] = yStartOffset + -100 * Settings.scale + ySpaceAlternatingOffset;
        orbPositionsY[8] = yStartOffset;
        orbPositionsY[9] = yStartOffset + -100 * Settings.scale;
    }

    public CharSelectInfo getLoadout() {
        return new CharSelectInfo("The Slimebound", "A rogue minion of the Spire, driven to conquer it.", 60, 60, 4, 99, 5, this,

                getStartingRelics(), getStartingDeck(), false);
    }

    public String getTitle(PlayerClass playerClass) {
        return "The Slimebound";
    }

    public AbstractCard.CardColor getCardColor() {
        return AbstractCardEnum.SLIMEBOUND;
    }

    public Color getCardRenderColor() {

        return cardRenderColor;
    }


    public AbstractCard getStartCardForEvent() {
        return new Strike_Slimebound();
    }

    public Color getCardTrailColor() {
        return cardRenderColor.cpy();
    }

    public int getAscensionMaxHPLoss() {
        return 10;
    }

    public BitmapFont getEnergyNumFont() {
        return FontHelper.energyNumFontGreen;
    }

    public void doCharSelectScreenSelectEffect() {
        CardCrawlGame.sound.playA("SLIME_SPLIT", MathUtils.random(-0.2F, 0.2F));
        CardCrawlGame.screenShake.shake(ScreenShake.ShakeIntensity.MED, ScreenShake.ShakeDur.SHORT, true);
    }

    public String getCustomModeCharacterButtonSoundKey() {
        return "SLIME_SPLIT";
    }

    public String getLocalizedCharacterName() {
        return "The Slimebound";
    }

    public AbstractPlayer newInstance() {
        return new SlimeboundCharacter("The Slimebound", SlimeboundEnum.SLIMEBOUND);
    }

    public String getSpireHeartText() {
        return "Must... absorb... the Heart...";
    }

    public Color getSlashAttackColor() {
        return Color.GREEN;
    }

    public AbstractGameAction.AttackEffect[] getSpireHeartSlashEffect() {
        return new AbstractGameAction.AttackEffect[]{AbstractGameAction.AttackEffect.BLUNT_HEAVY, AbstractGameAction.AttackEffect.SMASH, AbstractGameAction.AttackEffect.BLUNT_HEAVY, AbstractGameAction.AttackEffect.BLUNT_HEAVY, AbstractGameAction.AttackEffect.SMASH, AbstractGameAction.AttackEffect.BLUNT_HEAVY};
    }


    public String getVampireText() {
        return com.megacrit.cardcrawl.events.city.Vampires.DESCRIPTIONS[5];
    }

    @Override
    public void applyStartOfTurnCards() {
        super.applyStartOfTurnCards();
    }

    @Override
    public void render(SpriteBatch sb) {
        super.render(sb);
        if (!this.moved) this.movePosition((float)Settings.WIDTH * this.leftScale, AbstractDungeon.floorY); this.moved = true;


        this.hatX = this.skeleton.findBone("eyeback1").getX();
        this.hatY = this.skeleton.findBone("eyeback1").getY();

    }


    public void renderPowerIcons(SpriteBatch sb, float x, float y) {
        float offset = 10.0F * Settings.scale;
        int powersIterated = 0;
        float YOffset = 0;
        Iterator var5;
        AbstractPower p;
        for (var5 = this.powers.iterator(); var5.hasNext(); offset += 48.0F * Settings.scale) {
            p = (AbstractPower) var5.next();
            p.renderIcons(sb, x + offset, (y - 48.0F + YOffset) * Settings.scale, Color.WHITE);
            powersIterated++;
            if (powersIterated == 9 || powersIterated == 18) {
                YOffset += -42F * Settings.scale;
                offset = -38.0F * Settings.scale;
            }
        }

        offset = 0.0F;
        powersIterated = 0;
        YOffset = 0.0F;

        for (var5 = this.powers.iterator(); var5.hasNext(); offset += 48.0F * Settings.scale) {
            p = (AbstractPower) var5.next();
            p.renderAmount(sb, x + offset + 32.0F * Settings.scale, (y - 66.0F + YOffset) * Settings.scale, Color.WHITE);
            powersIterated++;
            if (powersIterated == 9 || powersIterated == 18) {
                YOffset += -42F * Settings.scale;
                offset = -48.0F * Settings.scale;
            }
        }
    }



}


