package io.lees.boom.storage.db.core.config

import org.springframework.boot.persistence.autoconfigure.EntityScan
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.transaction.annotation.EnableTransactionManagement

@Configuration
@EnableTransactionManagement
@EntityScan(basePackages = ["io.lees.boom.storage.db.core"])
@EnableJpaRepositories(basePackages = ["io.lees.boom.storage.db.core"])
internal class CoreJpaConfig
