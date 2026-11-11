package edu.crm;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import org.junit.jupiter.api.Test;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;

class ColaArchitectureTest {
    private final JavaClasses classes = new ClassFileImporter().importPackages("edu.crm");
    @Test
    void domainDoesNotDependOnOuterLayers() {
        noClasses().that().resideInAPackage("..domain..").should().dependOnClassesThat()
                .resideInAnyPackage("..adapter..", "..app..", "..infrastructure..").check(classes);
    }
    @Test
    void controllerAndServiceNamesDescribeTheirRoles() {
        classes().that().resideInAPackage("..adapter..").and().areAnnotatedWith("org.springframework.web.bind.annotation.RestController")
                .should().haveSimpleNameEndingWith("Controller").check(classes);
        classes().that().resideInAPackage("..app..").and().areAnnotatedWith("org.springframework.stereotype.Service")
                .should().haveSimpleNameEndingWith("Service").check(classes);
    }
}
