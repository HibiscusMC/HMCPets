package com.hibiscusmc.hmcpets.storage.impl.local;

import com.hibiscusmc.hmcpets.HMCPetsPlugin;
import com.hibiscusmc.hmcpets.config.PluginConfig;
import com.hibiscusmc.hmcpets.pet.PetConfig;
import com.hibiscusmc.hmcpets.storage.impl.SQLBasedStorage;
import org.h2.jdbc.JdbcConnection;
import team.unnamed.inject.Inject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;

public class H2Storage extends SQLBasedStorage {

    private JdbcConnection connection;

    @Inject
    public H2Storage(PluginConfig pluginConfig, PetConfig petConfig) {
        super(pluginConfig, petConfig);
    }

    @Override
    public String name() {
        return "H2";
    }

    @Override
    public Connection getConnection() {
        return connection;
    }

    @Override
    public void initialize(PluginConfig.StorageConfig config) {
        try {
            connection = new JdbcConnection("jdbc:h2:./" + HMCPetsPlugin.instance().getDataFolder() + "/" + config.database(), new Properties(), null, null, false);
            connection.setAutoCommit(true);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

        String resource = getStatementPath(config.method(), "schema");
        try (InputStream in = getClass().getClassLoader().getResourceAsStream(resource)) {
            if (in == null) throw new IllegalStateException(resource + " not found in resources.");
            String sql = new String(in.readAllBytes(), StandardCharsets.UTF_8);

            for (String statementString : sql.split(";")) {
                statementString = statementString.trim()
                        .replaceAll("<prefix>", config.prefix());

                if (!statementString.isEmpty()) {
                    try (PreparedStatement stmt = connection.prepareStatement(statementString)) {
                        stmt.executeUpdate();
                    }
                }
            }
        } catch (IOException | SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void close() {
        try {
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}