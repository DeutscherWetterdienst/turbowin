# TurboWin+ documentation

This directory describes the current application using the [arc42 structure](about-arc42.md).
The extracted workflows and parsers improve navigation and testing, but the application still
shares mutable state across Swing forms and legacy coordinator classes.

## Architecture

1. [Introduction and goals](01_introduction_and_goals.md)
2. [Architecture constraints](02_architecture_constraints.md)
3. [Context and scope](03_context_and_scope.md)
4. [Solution strategy](04_solution_strategy.md)
5. [Building blocks and implementation entry points](05_building_block_view.md)
6. [Runtime scenarios and completion behavior](06_runtime_view.md)
7. [Deployment, packaging, and CI](07_deployment_view.md)
8. [Cross-cutting concepts and legacy contracts](08_concepts.md)
9. [Architecture decisions and tradeoffs](09_architecture_decisions.md)
10. [Quality requirements](10_quality_requirements.md)
11. [Remaining risks and technical debt](11_technical_risks.md)
12. [Glossary](12_glossary.md)

## Working on the repository

- [Contributor and testing guide](contributing_and_testing.md)
- [Repository cleanup and artifact policy](repository_cleanup.md)
- [Format 101 compatibility tooling](../contrib/format101/README.md)

The architecture views describe responsibilities, not independently deployed services or strict
Java package boundaries. Source links identify representative entry points rather than every
extracted class.
