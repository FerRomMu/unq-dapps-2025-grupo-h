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
    fun controllersShouldOnlyAccessServices() {
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
    fun servicesShouldNotDependOnControllersOrConfig() {
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
    fun repositoriesShouldOnlyDependOnModel() {
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
    fun externalShouldOnlyBeAccessedByServicesAndSelf() {
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
    fun noBackwardsDependenciesFromRepository() {
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
    fun noDirectDbAccessFromController() {
        val rule = noClasses()
            .that().resideInAnyPackage("$basePackage.controller..")
            .should().dependOnClassesThat()
            .resideInAnyPackage("$basePackage.repository..")
        rule.check(importedClasses)
    }

    @Test
    fun noCyclesBetweenPackages() {
        val rule = slices()
            .matching("$basePackage.(*)..")
            .should().beFreeOfCycles()

        rule.check(importedClasses)
    }
}