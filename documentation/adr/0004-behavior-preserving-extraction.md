# ADR-0004: Extract responsibilities while preserving legacy contracts

- Status: Accepted
- Date: 2026-08-07

## Context

The application had large Swing coordinators, generated forms, shared static state, device-specific
parsers, and output workflows whose behavior is part of the operational contract. The code needed
better navigation and test seams without an uncontrolled rewrite of state ownership, threading, or
domain rules.

## Decision

Extract named workflows, parsers, formatters, calculators, and persistence helpers behind the
existing entry points. Preserve observable behavior during extraction: field layouts, units,
missing values, validation order, diagnostics, logging/reset side effects, and asynchronous
completion semantics. Characterization tests are added before or alongside changes. Extracted
classes may still use shared application state; this work is not a new layered architecture or a
promise of universal thread safety.

## Consequences

Responsibilities are easier to locate and many helpers can be tested with explicit inputs. The
application still has mutable global state, Swing lifecycle constraints, workflow-specific failure
handling, and background work whose `done()` callback can perform additional side effects. Future
behavior changes require an explicit decision and regression coverage.
