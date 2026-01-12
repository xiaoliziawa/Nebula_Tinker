package top.nebula.nebula_tinker.datagen.language;

import top.nebula.nebula_tinker.NebulaTinker;

import java.util.ArrayList;
import java.util.List;

public class LanguageGenerate {
	public static final List<List<String>> TRANSLATION_LIST = new ArrayList<>();

	public static void register() {
		addLanguage(
				"item",
				"demonization_stone",
				"Demonization Stone",
				"魔化石"
		);
		addLanguage(
				"item",
				"divinization_stone",
				"Divinization Stone",
				"神化石"
		);
		addLanguage(
				"itemGroup",
				"tab",
				"Nebula Tinker",
				"星云工匠"
		);
		addLanguage(
				"message",
				"modifier.acupoint",
				"Deadly Acupoint activated",
				"<死穴>效果发动"
		);
		addLanguage(
				"modifier",
				"death_echo",
				"Death Echo",
				"生命回响"
		);
		addLanguage(
				"modifier",
				"death_echo.flavor",
				"Fight to the end!",
				"战斗到底!"
		);
		addLanguage(
				"modifier",
				"death_echo.description",
				"When HP is less than 35%, attacks have a 15% chance to be critical directly.",
				"当血量低于35%时攻击有15%的概率直接暴击"
		);
		addLanguage(
				"modifier",
				"acupoint",
				"Deadly Acupoint",
				"死穴"
		);
		addLanguage(
				"modifier",
				"acupoint.flavor",
				"You're in the kill zone!",
				"你已进入斩杀线！"
		);
		addLanguage(
				"modifier",
				"acupoint.description",
				"Chance to instantly kill enemies below 25% health (12.5% for BOSS)",
				"怪物血量低于25%时有概率直接斩杀 (BOSS为12.5%)"
		);
		addLanguage(
				"modifier",
				"frenzy",
				"Frenzy",
				"狂乱"
		);
		addLanguage(
				"modifier",
				"frenzy.flavor",
				"Speed is life",
				"速度即是生命"
		);
		addLanguage(
				"modifier",
				"frenzy.description",
				"Heal when attacking with speed effect",
				"拥有速度效果时，攻击会回复生命值"
		);
		addLanguage(
				"modifier",
				"swift_blade",
				"Swift Blade",
				"迅捷之刃"
		);
		addLanguage(
				"modifier",
				"swift_blade.flavor",
				"Face the wind!",
				"面对疾风吧！"
		);
		addLanguage(
				"modifier",
				"swift_blade.description",
				"Always critical hit while having speed effect\n§7Higher speed level increases critical damage",
				"拥有速度效果时，攻击必定暴击\n§7速度等级越高，暴击伤害越高"
		);
		addLanguage(
				"modifier",
				"causal_truncation",
				"Causal Truncation",
				"因果裁断"
		);
		addLanguage(
				"modifier",
				"causal_truncation.flavor",
				"Sever the chains of causality",
				"斩断因果，得证大道"
		);
		addLanguage(
				"modifier",
				"causal_truncation.description",
				"Chance to deal 33% of target's current health as damage",
				"攻击时有概率造成目标33%当前血量的伤害"
		);
		addLanguage(
				"modifier",
				"abuser",
				"Abuser",
				"虐待者"
		);
		addLanguage(
				"modifier",
				"abuser.flavor",
				"Kick them when they're down",
				"趁你病，要你命"
		);
		addLanguage(
				"modifier",
				"abuser.description",
				"Deal 50% more damage to poisoned, withered or bleeding targets",
				"如果目标身上有中毒、凋零或流血的效果，攻击时必定造成暴击"
		);
		addLanguage(
				"modifier",
				"rapid_shot",
				"Rapid Shot",
				"快速射击"
		);
		addLanguage(
				"modifier",
				"rapid_shot.flavor",
				"Arrows like rain",
				"箭如雨下"
		);
		addLanguage(
				"modifier",
				"rapid_shot.description",
				"Increase ranged weapon firing speed and accuracy",
				"提升远程武器的射速和精准度"
		);
		addLanguage(
				"modifier",
				"divinization",
				"Divinization",
				"神化"
		);
		addLanguage(
				"modifier",
				"divinization.flavor",
				"Infused with divine power",
				"灌注神圣之力"
		);
		addLanguage(
				"modifier",
				"divinization.description",
				"Grants 3 random divine attributes when applied\n§7• Weapons: Attack, speed, elemental damage\n• Tools: Efficiency, durability, tier\n• Armor: Defense, health, resistance\n§6Total 9 levels, each level enhances effects",
				"每次应用时随机赋予3种神圣属性\n§7• 武器：攻击、速度、元素伤害\n• 工具：效率、耐久、等级\n• 盔甲：防御、生命、抗性\n§6共9级，每级提升属性效果"
		);
		addLanguage(
				"modifier",
				"demonization",
				"Demonization",
				"魔化"
		);
		addLanguage(
				"modifier",
				"demonization.flavor",
				"Corrupted by demonic power",
				"被恶魔之力腐蚀"
		);
		addLanguage(
				"modifier",
				"demonization.description",
				"Grants 3 powerful attributes with 1 negative effect\n§7• Weapons: High attack boost, but weaker survival\n• Tools: Extreme efficiency, but lower durability\n• Armor: Maximum defense, but weaker offense\n§cSide effect: Periodically takes damage",
				"赋予3种强力属性，但附带1个负面效果\n§7• 武器：高额攻击加成，但削弱生存\n• 工具：超强效率，但降低耐久\n• 盔甲：极致防御，但削弱攻击\n§c副作用：周期性受到伤害"
		);
		addLanguage(
				"message",
				"divinization.generate",
				"Divine power surges through your weapon!",
				"神圣之力涌入你的武器！"
		);
		addLanguage(
				"message",
				"demonization.generate",
				"Demonic energy corrupts your weapon!",
				"恶魔能量腐蚀了你的武器！"
		);

		addLanguage(
				"modifier",
				"divinization.tooltip.title",
				"§6§lDivinization Attributes:",
				"§6§l神化属性："
		);
		addLanguage(
				"modifier",
				"demonization.tooltip.title",
				"§4§lDemonization Attributes:",
				"§4§l魔化属性："
		);
		addLanguage(
				"modifier",
				"demonization.positive",
				"§aPositive Effects:",
				"§a正面效果："
		);
		addLanguage(
				"modifier",
				"demonization.negative",
				"§cNegative Effects:",
				"§c负面效果："
		);

		addLanguage(
				"tooltip",
				"demonization.title",
				"§6§lDemonization Attributes:",
				"§6§l魔化属性:"
		);
		addLanguage(
				"tooltip",
				"divinization.title",
				"§b§lDivinization Attributes:",
				"§b§l神化属性:"
		);
		addLanguage(
				"tooltip",
				"negative.title",
				"§c§lNegative Effects:",
				"§c§l负面效果:"
		);
		addLanguage(
				"tooltip",
				"hold",
				"Hold ",
				"按住"
		);
		addLanguage(
				"tooltip",
				"view",
				" to view ",
				"查看"
		);
		addLanguage(
				"tooltip",
				"divine_demonic",
				"Divine/Demonic",
				"神魔化"
		);
		addLanguage(
				"tooltip",
				"attributes",
				" attributes",
				"属性"
		);

		addLanguage(
				"format",
				"multiplier",
				"x",
				"倍"
		);
		addLanguage(
				"format",
				"blocks",
				" blocks",
				"格"
		);
		addLanguage(
				"format",
				"seconds",
				"s",
				"秒"
		);

		addLanguage(
				"command",
				"player_only",
				"Only players can use this command",
				"只有玩家可以使用此命令"
		);
		addLanguage(
				"command",
				"no_modifier",
				"Main hand or off hand item has no Divinization or Demonization modifier",
				"主手或副手物品没有神化或魔化修饰器"
		);
		addLanguage(
				"command",
				"no_demonization",
				"Main hand or off hand item has no Demonization modifier",
				"主手或副手物品没有魔化修饰器"
		);
		addLanguage(
				"command",
				"no_divinization",
				"Main hand or off hand item has no Divinization modifier",
				"主手或副手物品没有神化修饰器"
		);

		addLanguage(
				"command",
				"debug.title",
				"=== Item Attribute Debug ===",
				"=== 物品属性调试 ==="
		);
		addLanguage(
				"command",
				"debug.main_hand",
				"Main hand item: %s",
				"主手物品: %s"
		);
		addLanguage(
				"command",
				"debug.off_hand",
				"Off hand item: %s",
				"副手物品: %s"
		);
		addLanguage(
				"command",
				"debug.main_has_demonization",
				"Main hand item has Demonization modifier",
				"主手物品有魔化修饰器"
		);
		addLanguage(
				"command",
				"debug.off_has_demonization",
				"Off hand item has Demonization modifier",
				"副手物品有魔化修饰器"
		);
		addLanguage(
				"command",
				"debug.main_has_divinization",
				"Main hand item has Divinization modifier",
				"主手物品有神化修饰器"
		);
		addLanguage(
				"command",
				"debug.off_has_divinization",
				"Off hand item has Divinization modifier",
				"副手物品有神化修饰器"
		);

		addLanguage(
				"command",
				"modifier.demonization",
				"Demonization",
				"魔化"
		);
		addLanguage(
				"command",
				"modifier.divinization",
				"Divinization",
				"神化"
		);

		addLanguage(
				"attribute.modifier",
				"attack_damage",
				"Attack Damage +%s",
				"攻击伤害 +%s"
		);
		addLanguage(
				"attribute.modifier",
				"attack_speed",
				"Attack Speed +%s",
				"攻击速度 +%s"
		);
		addLanguage(
				"attribute.modifier",
				"critical_chance",
				"Critical Chance +%s",
				"暴击几率 +%s"
		);
		addLanguage(
				"attribute.modifier",
				"mining_speed_reduction",
				"Mining Speed -%s",
				"挖掘速度 -%s"
		);
		addLanguage(
				"attribute.modifier",
				"durability_reduction",
				"Durability -%s",
				"耐久度 -%s"
		);
		addLanguage(
				"attribute.modifier",
				"harvest_level_reduction",
				"Harvest Level -%s",
				"采集等级 -%s"
		);
	}

	/**
	 * 添加翻译
	 *
	 * @param type
	 * @param key
	 * @param english
	 * @param chinese
	 */
	@SuppressWarnings("AlibabaLowerCamelCaseVariableNaming")
	public static void addLanguage(String type, String key, String english, String chinese) {
		List<String> newList = new ArrayList<>();
		if (type == null) {
			newList.add(String.format("%s.%s", NebulaTinker.MODID, key));
		} else {
			newList.add(String.format("%s.%s.%s", type, NebulaTinker.MODID, key));
		}
		newList.add(english);
		newList.add(chinese);
		TRANSLATION_LIST.add(newList);
	}
}