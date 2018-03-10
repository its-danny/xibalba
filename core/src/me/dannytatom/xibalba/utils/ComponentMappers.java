package me.dannytatom.xibalba.utils;

import com.badlogic.ashley.core.ComponentMapper;
import me.dannytatom.xibalba.components.*;
import me.dannytatom.xibalba.components.actions.ExploreComponent;
import me.dannytatom.xibalba.components.actions.MeleeComponent;
import me.dannytatom.xibalba.components.actions.MovementComponent;
import me.dannytatom.xibalba.components.actions.RangeComponent;
import me.dannytatom.xibalba.components.items.AmmunitionComponent;
import me.dannytatom.xibalba.components.items.WeaponComponent;
import me.dannytatom.xibalba.components.statuses.*;
import me.dannytatom.xibalba.components.traps.SpiderWebComponent;

public final class ComponentMappers {
  public static final ComponentMapper<BrainComponent> brain =
    ComponentMapper.getFor(BrainComponent.class);

  public static final ComponentMapper<BodyComponent> body =
    ComponentMapper.getFor(BodyComponent.class);

  public static final ComponentMapper<PositionComponent> position =
    ComponentMapper.getFor(PositionComponent.class);

  public static final ComponentMapper<MovementComponent> movement =
    ComponentMapper.getFor(MovementComponent.class);

  public static final ComponentMapper<MouseMovementComponent> mouseMovement =
    ComponentMapper.getFor(MouseMovementComponent.class);

  public static final ComponentMapper<ExploreComponent> explore =
    ComponentMapper.getFor(ExploreComponent.class);

  public static final ComponentMapper<VisualComponent> visual =
    ComponentMapper.getFor(VisualComponent.class);

  public static final ComponentMapper<AttributesComponent> attributes =
    ComponentMapper.getFor(AttributesComponent.class);

  public static final ComponentMapper<SkillsComponent> skills =
    ComponentMapper.getFor(SkillsComponent.class);

  public static final ComponentMapper<AbilitiesComponent> abilities =
    ComponentMapper.getFor(AbilitiesComponent.class);

  public static final ComponentMapper<GodComponent> god =
    ComponentMapper.getFor(GodComponent.class);

  public static final ComponentMapper<EncumberedComponent> encumbered =
    ComponentMapper.getFor(EncumberedComponent.class);

  public static final ComponentMapper<CharmedComponent> charmed =
    ComponentMapper.getFor(CharmedComponent.class);

  public static final ComponentMapper<CrippledComponent> crippled =
    ComponentMapper.getFor(CrippledComponent.class);

  public static final ComponentMapper<BleedingComponent> bleeding =
    ComponentMapper.getFor(BleedingComponent.class);

  public static final ComponentMapper<DrowningComponent> drowning =
    ComponentMapper.getFor(DrowningComponent.class);

  public static final ComponentMapper<StuckComponent> stuck =
    ComponentMapper.getFor(StuckComponent.class);

  public static final ComponentMapper<PoisonedComponent> poisoned =
    ComponentMapper.getFor(PoisonedComponent.class);

  public static final ComponentMapper<SickComponent> sick =
    ComponentMapper.getFor(SickComponent.class);

  public static final ComponentMapper<WetComponent> wet =
    ComponentMapper.getFor(WetComponent.class);

  public static final ComponentMapper<MeleeComponent> melee =
    ComponentMapper.getFor(MeleeComponent.class);

  public static final ComponentMapper<RangeComponent> range =
    ComponentMapper.getFor(RangeComponent.class);

  public static final ComponentMapper<EffectsComponent> effects =
    ComponentMapper.getFor(EffectsComponent.class);

  public static final ComponentMapper<InventoryComponent> inventory =
    ComponentMapper.getFor(InventoryComponent.class);

  public static final ComponentMapper<EquipmentComponent> equipment =
    ComponentMapper.getFor(EquipmentComponent.class);

  public static final ComponentMapper<ItemComponent> item =
    ComponentMapper.getFor(ItemComponent.class);

  public static final ComponentMapper<AmmunitionComponent> ammunition =
    ComponentMapper.getFor(AmmunitionComponent.class);

  public static final ComponentMapper<WeaponComponent> weapon =
    ComponentMapper.getFor(WeaponComponent.class);

  public static final ComponentMapper<DecorationComponent> decoration =
    ComponentMapper.getFor(DecorationComponent.class);

  public static final ComponentMapper<LightComponent> light =
    ComponentMapper.getFor(LightComponent.class);

  public static final ComponentMapper<RainDropComponent> rainDrop =
    ComponentMapper.getFor(RainDropComponent.class);

  public static final ComponentMapper<PlayerComponent> player =
    ComponentMapper.getFor(PlayerComponent.class);

  public static final ComponentMapper<EnemyComponent> enemy =
    ComponentMapper.getFor(EnemyComponent.class);

  public static final ComponentMapper<CorpseComponent> corpse =
    ComponentMapper.getFor(CorpseComponent.class);

  public static final ComponentMapper<LimbComponent> limb =
    ComponentMapper.getFor(LimbComponent.class);

  public static final ComponentMapper<EntranceComponent> entrance =
    ComponentMapper.getFor(EntranceComponent.class);

  public static final ComponentMapper<ExitComponent> exit =
    ComponentMapper.getFor(ExitComponent.class);

  public static final ComponentMapper<TrapComponent> trap =
    ComponentMapper.getFor(TrapComponent.class);

  public static final ComponentMapper<SpiderWebComponent> spiderWeb =
    ComponentMapper.getFor(SpiderWebComponent.class);

  public static final ComponentMapper<DefectsComponent> defects =
    ComponentMapper.getFor(DefectsComponent.class);

  public static final ComponentMapper<TraitsComponent> traits =
    ComponentMapper.getFor(TraitsComponent.class);
}
