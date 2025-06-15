package unq.dda.grupoh.architecture

import com.tngtech.archunit.core.domain.JavaClass.Predicates.resideInAnyPackage
import com.tngtech.archunit.core.domain.JavaClasses
import com.tngtech.archunit.core.importer.ClassFileImporter
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses
import com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices
import org.junit.jupiter.api.Test

class ArchitectureTest {

    private val basePackage = "unq.dda.grupoh"
    private val importedClasses: JavaClasses = ClassFileImporter()
        .withImportOption { location ->
            !location.contains("/test/") && !location.contains("/e2e/")
        }
        .importPackages(basePackage)

    @Test
    fun controllers_should_only_access_services() {
        val rule = classes()
            .that().resideInAnyPackage("$basePackage.controller..")
            .should().onlyDependOnClassesThat(
                resideInAnyPackage(
                    "$basePackage.controller..",
                    "$basePackage.service..",
                    "$basePackage.config..",
                    "$basePackage.model..",
                    "$basePackage.dto..",
                    "org.springframework..",
                    "org.jetbrains.annotations..",
                    "java..",
                    "kotlin..",
                    "io.swagger..",
                )
            )
        rule.check(importedClasses)
    }

    @Test
    fun services_should_not_depend_on_controllers_or_config() {
        val rule = classes()
            .that().resideInAnyPackage("$basePackage.service..")
            .should().onlyDependOnClassesThat(
                resideInAnyPackage(
                    "$basePackage.service..",
                    "$basePackage.repository..",
                    "$basePackage.external..",
                    "$basePackage.model..",
                    "$basePackage.exceptions..",
                    "java..",
                    "kotlin..",
                    "kotlinx..",
                    "io.jsonwebtoken..",
                    "org.jetbrains.annotations..",
                    "org.springframework..",
                    "javax.crypto.."
                )
            )
        rule.check(importedClasses)
    }

    @Test
    fun repositories_should_only_depend_on_model() {
        val rule = classes()
            .that().resideInAnyPackage("$basePackage.repository..")
            .should().onlyDependOnClassesThat(
                resideInAnyPackage(
                    "$basePackage.repository..",
                    "$basePackage.model..",
                    "java..",
                    "org.springframework.data.jpa.repository..",
                    "kotlin..",
                    "org.jetbrains.annotations.."
                )
            )
        rule.check(importedClasses)
    }

    @Test
    fun external_should_only_be_accessed_by_services_and_self() {
        val rule = classes()
            .that().resideInAnyPackage("$basePackage.external..")
            .should().onlyBeAccessed().byClassesThat(
                resideInAnyPackage(
                    "$basePackage.service..",
                    "$basePackage.external.."
                )
            )
        rule.check(importedClasses)
    }

    @Test
    fun no_backwards_dependencies_from_repository() {
        val rule = noClasses()
            .that().resideInAnyPackage("$basePackage.repository..")
            .should().dependOnClassesThat()
            .resideInAnyPackage(
                "$basePackage.service..",
                "$basePackage.controller.."
            )
        rule.check(importedClasses)
    }

    @Test
    fun no_direct_db_access_from_controller() {
        val rule = noClasses()
            .that().resideInAnyPackage("$basePackage.controller..")
            .should().dependOnClassesThat()
            .resideInAnyPackage("$basePackage.repository..")
        rule.check(importedClasses)
    }

    @Test
    fun no_cycles_between_packages() {
        val rule = slices()
            .matching("$basePackage.(*)..")
            .should().beFreeOfCycles()

        rule.check(importedClasses)
    }
}