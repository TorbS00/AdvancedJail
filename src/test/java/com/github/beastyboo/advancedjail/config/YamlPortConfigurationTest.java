package com.github.beastyboo.advancedjail.config;

import com.github.beastyboo.advancedjail.BukkitClearStaticStateExtension;
import com.github.beastyboo.advancedjail.domain.port.ConfigPort;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.inventory.ItemFactory;
import org.bukkit.inventory.meta.ItemMeta;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import space.arim.dazzleconf.ConfigurationFactory;
import space.arim.dazzleconf.ConfigurationOptions;
import space.arim.dazzleconf.ext.snakeyaml.SnakeYamlConfigurationFactory;
import space.arim.dazzleconf.ext.snakeyaml.SnakeYamlOptions;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith({MockitoExtension.class, BukkitClearStaticStateExtension.class})
public class YamlPortConfigurationTest {

	private ConfigurationFactory<ConfigPort> configFactory;

	@BeforeEach
	public void setup() {
		configFactory = new SnakeYamlConfigurationFactory<>(ConfigPort.class, ConfigurationOptions.defaults(),
				new SnakeYamlOptions.Builder().useCommentingWriter(true).build());
	}

	private void setServer(boolean stubItemMetaDisplayName) {
		// Bukkit's awful ItemMeta API relies on static state, namely Bukkit.getItemFactory
		// However, it is not all together untestable
		Server server = mock(Server.class);
		ItemFactory itemFactory = mock(ItemFactory.class);
		ItemMeta itemMeta = mock(ItemMeta.class);
		when(server.getName()).thenReturn("ServerName");
		when(server.getVersion()).thenReturn("ServerVersion");
		when(server.getBukkitVersion()).thenReturn("ServerBukkitVersion");
		when(server.getItemFactory()).thenReturn(itemFactory);
		when(server.getLogger()).thenReturn(Logger.getLogger("ServerLogger"));
		when(itemFactory.getItemMeta(any())).thenReturn(itemMeta);
		if (stubItemMetaDisplayName) {
			when(itemMeta.getDisplayName()).thenReturn("ItemMetaDisplayName");
		}
		Bukkit.setServer(server);
	}

	@Test
	public void loadDefaults() {
		setServer(false);
		assertDoesNotThrow(configFactory::loadDefaults);
	}

	@Test
	public void saveDefaults() {
		setServer(true);
		ConfigPort defaults = configFactory.loadDefaults();
		assertDoesNotThrow(() -> configFactory.write(defaults, new ByteArrayOutputStream()));
	}

	@Test
	public void reloadWithDefaultValues() throws IOException {
		setServer(true);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		configFactory.write(configFactory.loadDefaults(), baos);
		assertDoesNotThrow(() -> configFactory.load(new ByteArrayInputStream(baos.toByteArray())));
	}
}
