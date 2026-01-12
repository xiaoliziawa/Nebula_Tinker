package top.nebula.nebula_tinker.datagen.language.locale;

import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.LanguageProvider;
import top.nebula.nebula_tinker.NebulaTinker;
import top.nebula.nebula_tinker.datagen.language.LanguageGenerate;

import java.util.List;

public class English extends LanguageProvider {
	public English(PackOutput output) {
		super(output, NebulaTinker.MODID, "en_us");
	}

	@Override
	protected void addTranslations() {
		for (List<String> item : LanguageGenerate.TRANSLATION_LIST) {
			add(item.get(0), item.get(1));
		}
	}
}