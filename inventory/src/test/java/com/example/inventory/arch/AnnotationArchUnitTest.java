package com.example.inventory.arch;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.library.Architectures.LayeredArchitecture;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

@AnalyzeClasses(
        packages = "com.example.inventory",
        importOptions = {ImportOption.Predefined.DoNotIncludeTests.class}
)
public class AnnotationArchUnitTest {

    private final LayeredArchitecture baseRule = layeredArchitecture()
            .consideringOnlyDependenciesInLayers()
            .layer("Controller").definedBy("com.example.inventory.controller")
            .layer("Service").definedBy("com.example.inventory.service")
            .layer("Repository").definedBy("com.example.inventory.repository");

    // Controller 레이어는 Service 레이어에만 의존한다
    @ArchTest
    final ArchRule test1 = baseRule
            .whereLayer("Controller").mayOnlyAccessLayers("Service")
            .whereLayer("Controller").mayNotBeAccessedByAnyLayer();

    // Service 레이어는 그 어떤 레이어에 의존하지 않는다
    @ArchTest
    final ArchRule test2 = baseRule
            .whereLayer("Service").mayNotAccessAnyLayer()
            .whereLayer("Service").mayOnlyBeAccessedByLayers("Controller", "Repository");

    // Repository 레이어는 Service 레이어에만 의존한다
    @ArchTest
    final ArchRule test3 = baseRule
            .whereLayer("Repository").mayOnlyAccessLayers("Service")
            .whereLayer("Repository").mayNotBeAccessedByAnyLayer();

}
