package unq.dda.grupoh

import org.springframework.boot.actuate.info.InfoContributor
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean

@SpringBootApplication
class GrupohApplication {

	@Bean
	fun infoContributor() = InfoContributor { builder ->
		builder.withDetail(
			"app", mapOf(
				"name" to "grupoh-football",
				"version" to "1.0.0",
				"description" to "Backend del Grupo H"
			)
		)
	}
}

fun main(args: Array<String>) {
	runApplication<GrupohApplication>(*args)
}
