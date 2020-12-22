package com.github.beastyboo.advancedjail;

import org.bukkit.Bukkit;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.lang.reflect.Field;

public class BukkitClearStaticStateExtension implements AfterEachCallback {

	@Override
	public void afterEach(ExtensionContext context) throws Exception {
		Field field = Bukkit.class.getDeclaredField("server");
		field.setAccessible(true);
		field.set(null, null);
	}

}
