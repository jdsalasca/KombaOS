# AGENTS.md

Agent guide for this repository. Keep it concise, actionable, and aligned with the codebase.

## Branches

- develop: main integration branch
- release/*: release preparation from develop
- master: published releases only

## Workflow

1. Create feature branches from develop: feature/<ticket>-<summary>
2. Open a descriptive PR into develop
3. Merge via PR after review
4. Create release/<version> from develop when the milestone has real value
5. Open PR release/<version> -> master with the release checklist
6. Publish the release from master and tag v<version>
7. Continue work on develop

## Releases

- Releases are cut from master only
- Tags are created by the release workflow

## Code principles

- Modular by domain and responsibility
- Small classes with a clear objective
- Short methods with one intent
- Prefer composition over inheritance, avoid superclasses
- Depend on interfaces, not implementations

## Quality

- Frontend: cd frontend && npm run lint, npm run test, npm run build
- Backend: cd backend && ./mvnw test

## Quick commands

- Frontend unit tests: cd frontend && npm run test
- Frontend lint: cd frontend && npm run lint
- Frontend build: cd frontend && npm run build
- Backend tests: cd backend && ./mvnw test

## Do / Don’t

- Do: follow existing patterns and file layout
- Do: keep changes small and reviewable
- Don’t: invent APIs or routes without searching first
- Don’t: add heavy dependencies without approval

## Multi-agent coordination

- Announce the files you are touching and avoid overlapping edits
- Prefer sequential merges instead of parallel rewrites
- Rebase or merge frequently to reduce conflicts
- Do not push other agents’ local changes
- Keep PRs scoped to one logical change

## Agent practices

- Prefer editing existing files over creating new ones
- Avoid repository-wide rewrites unless explicitly requested
- Validate lint and build after moving imports or folders
- Confirm conventions from the real code before implementing

## References

- .agents-rules/00-multi-agent.md
- .agents-rules/01-principles.md
- .agents-rules/02-classes-methods.md
- .agents-rules/03-git-flow.md
- .agents-rules/04-quality.md
- .agents-rules/05-frontend.md
- .agents-rules/06-backend.md
- .agents-rules/07-release.md
