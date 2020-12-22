package com.github.beastyboo.advancedjail.config;

import com.github.beastyboo.advancedjail.domain.port.ConfigPort;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.inventory.ItemFactory;
import org.bukkit.inventory.meta.ItemMeta;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import space.arim.dazzleconf.ConfigurationFactory;
import space.arim.dazzleconf.ConfigurationOptions;
import space.arim.dazzleconf.ext.snakeyaml.SnakeYamlConfigurationFactory;
import space.arim.dazzleconf.ext.snakeyaml.SnakeYamlOptions;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class YamlPortConfigurationTest {

	private ConfigurationFactory<ConfigPort> configFactory;

	@BeforeAll
	public static void setupBukkit() {
		// Bukkit's awful ItemMeta API relies on static state, namely Bukkit.getItemFactory
		// However, it is not all together untestable
		Server server = mock(Server.class);
		ItemFactory itemFactory = mock(ItemFactory.class);
		ItemMeta itemMeta = mock(ItemMeta.class);
		when(server.getName()).thenReturn("testname");
		when(server.getVersion()).thenReturn("testversion");
		when(server.getBukkitVersion()).thenReturn("testbukkitversion");
		when(server.getItemFactory()).thenReturn(itemFactory);
		when(server.getLogger()).thenReturn(Logger.getLogger("ServerLogger"));
		when(itemFactory.getItemMeta(any())).thenReturn(itemMeta);
		when(itemMeta.getDisplayName()).thenReturn("testdisplayname");
		Bukkit.setServer(server);
	}

	@BeforeEach
	public void setup() {
		configFactory = new SnakeYamlConfigurationFactory<>(ConfigPort.class, ConfigurationOptions.defaults(),
				new SnakeYamlOptions.Builder().useCommentingWriter(true).build());
	}

	@Test
	public void loadDefaults() {
		assertDoesNotThrow(configFactory::loadDefaults);
	}

	@Test
	public void saveDefaults() {
		ConfigPort defaults = configFactory.loadDefaults();
		assertDoesNotThrow(() -> configFactory.write(defaults, new ByteArrayOutputStream()));
	}

	@Test
	public void reloadWithDefaultValues() throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		configFactory.write(configFactory.loadDefaults(), baos);
		assertDoesNotThrow(() -> configFactory.load(new ByteArrayInputStream(baos.toByteArray())));
	}
}
