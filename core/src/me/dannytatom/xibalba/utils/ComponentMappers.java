package me.dannytatom.xibalba.utils;

import com.badlogic.ashley.core.ComponentMapper;
import me.dannytatom.xibalba.components.AttributesComponent;
import me.dannytatom.xibalba.components.BodyComponent;
import me.dannytatom.xibalba.components.DecorationComponent;
import me.dannytatom.xibalba.components.EnemyComponent;
import me.dannytatom.xibalba.components.EntranceComponent;
import me.dannytatom.xibalba.components.EquipmentComponent;
import me.dannytatom.xibalba.components.ExitComponent;
import me.dannytatom.xibalba.components.InventoryComponent;
import me.dannytatom.xibalba.components.ItemComponent;
import me.dannytatom.xibalba.components.MouseMovementComponent;
import me.dannytatom.xibalba.components.PlayerComponent;
import me.dannytatom.xibalba.components.PositionComponent;
import me.dannytatom.xibalba.components.SkillsComponent;
import me.dannytatom.xibalba.components.VisualComponent;
import me.dannytatom.xibalba.components.actions.MeleeComponent;
import me.dannytatom.xibalba.components.actions.MovementComponent;
import me.dannytatom.xibalba.components.actions.RangeComponent;
import me.dannytatom.xibalba.components.ai.BrainComponent;
import me.dannytatom.xibalba.components.ai.TargetComponent;
import me.dannytatom.xibalba.components.items.AmmunitionComponent;
import me.dannytatom.xibalba.components.items.ArmorComponent;
import me.dannytatom.xibalba.components.items.ItemEffectsComponent;
import me.dannytatom.xibalba.components.items.WeaponComponent;
import me.dannytatom.xibalba.components.statuses.BleedingComponent;
import me.dannytatom.xibalba.components.statuses.CrippledComponent;
import me.dannytatom.xibalba.components.statuses.DrowningComponent;
import me.dannytatom.xibalba.components.statuses.WetComponent;

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

  public static final ComponentMapper<VisualComponent> visual =
      ComponentMapper.getFor(VisualComponent.class);

  public static final ComponentMapper<AttributesComponent> attributes =
      ComponentMapper.getFor(AttributesComponent.class);

  public static final ComponentMapper<SkillsComponent> skills =
      ComponentMapper.getFor(SkillsComponent.class);

  public static final ComponentMapper<CrippledComponent> crippled =
      ComponentMapper.getFor(CrippledComponent.class);

  public static final ComponentMapper<BleedingComponent> bleeding =
      ComponentMapper.getFor(BleedingComponent.class);

  public static final ComponentMapper<DrowningComponent> drowning =
      ComponentMapper.getFor(DrowningComponent.class);

  public static final ComponentMapper<WetComponent> wet =
      ComponentMapper.getFor(WetComponent.class);

  public static final ComponentMapper<TargetComponent> target =
      ComponentMapper.getFor(TargetComponent.class);

  public static final ComponentMapper<MeleeComponent> melee =
      ComponentMapper.getFor(MeleeComponent.class);

  public static final ComponentMapper<RangeComponent> range =
      ComponentMapper.getFor(RangeComponent.class);

  public static final ComponentMapper<InventoryComponent> inventory =
      ComponentMapper.getFor(InventoryComponent.class);

  public static final ComponentMapper<EquipmentComponent> equipment =
      ComponentMapper.getFor(EquipmentComponent.class);

  public static final ComponentMapper<ItemComponent> item =
      ComponentMapper.getFor(ItemComponent.class);

  public static final ComponentMapper<AmmunitionComponent> ammunition =
      ComponentMapper.getFor(AmmunitionComponent.class);

  public static final ComponentMapper<ArmorComponent> armor =
      ComponentMapper.getFor(ArmorComponent.class);

  public static final ComponentMapper<ItemEffectsComponent> itemEffects =
      ComponentMapper.getFor(ItemEffectsComponent.class);

  public static final ComponentMapper<WeaponComponent> weapon =
      ComponentMapper.getFor(WeaponComponent.class);

  public static final ComponentMapper<DecorationComponent> decoration =
      ComponentMapper.getFor(DecorationComponent.class);

  public static final ComponentMapper<PlayerComponent> player =
      ComponentMapper.getFor(PlayerComponent.class);

  public static final ComponentMapper<EnemyComponent> enemy =
      ComponentMapper.getFor(EnemyComponent.class);

  public static final ComponentMapper<EntranceComponent> entrance =
      ComponentMapper.getFor(EntranceComponent.class);

  public static final ComponentMapper<ExitComponent> exit =
      ComponentMapper.getFor(ExitComponent.class);
}
